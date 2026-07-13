package com.example.voice.commanding.service;

import com.example.voice.commanding.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.time.Duration;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import reactor.util.retry.Retry;
import org.springframework.web.reactive.function.client.WebClientResponseException;
@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceCommandService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ShoppingListService shoppingListService;
    private final com.example.voice.commanding.repository.ProductRepository productRepository;
    private final com.example.voice.commanding.repository.ListItemRepository listItemRepository;
    private final com.example.voice.commanding.repository.PurchaseHistoryRepository purchaseHistoryRepository;
    private final EntityManager entityManager;

    @Value("${groq.api.model}")
    private String model;

    @Value("${groq.api.translation-model:llama-3.1-8b-instant}")
    private String translationModel;

    // ──────────────────────────────────────────────────────────────────────
    // Step 1: LLM-based translation — converts ANY language to English
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Uses a fast, lightweight LLM to translate the user's voice command
     * from any language (Hindi, Marathi, Tamil, mixed Hinglish, etc.)
     * into clean English. This replaces the old PRODUCT_ALIASES map.
     *
     * <p>If the translation call fails (rate limit, timeout, etc.), it
     * gracefully falls back to the original text — the main intent-parsing
     * LLM still has some multilingual understanding.</p>
     */
    private String translateToEnglish(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String systemPrompt = "You are a translation engine for a grocery shopping voice assistant. " +
                "Translate the following voice command into English. Rules:\n" +
                "1. Preserve the EXACT quantities mentioned (e.g. '5 santri' → '5 oranges').\n" +
                "2. Preserve the intent/action words (add, remove, delete, search, find, modify, change, update).\n" +
                "3. Translate product names to their standard English grocery names " +
                "(e.g. 'doodh/dudh/दूध' → 'milk', 'batata/बटाटा/आलू' → 'potatoes', " +
                "'kanda/कांदा/प्याज' → 'onions', 'santri/संतरी/संतरा' → 'oranges', " +
                "'tamatar/टमाटर' → 'tomatoes', 'seb/सेब' → 'apples', 'kela/केला' → 'bananas', " +
                "'chawal/चावल/तांदूळ' → 'rice', 'cheeni/चीनी/साखर' → 'sugar').\n" +
                "4. If the input is already in English, return it as-is.\n" +
                "5. Output ONLY the translated English text. No explanations, no quotes, no extra formatting.\n" +
                "6. Handle mixed-language inputs (e.g. Hinglish: '5 santri add karo' → 'add 5 oranges').";

        GroqRequest translationReq = GroqRequest.builder()
                .model(translationModel)
                .messages(List.of(
                        GroqRequest.Message.builder()
                                .role("system")
                                .content(systemPrompt)
                                .build(),
                        GroqRequest.Message.builder()
                                .role("user")
                                .content(text)
                                .build()))
                .temperature(0.1)
                .build();

        try {
            GroqResponse response = webClient.post()
                    .bodyValue(translationReq)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofSeconds(1))
                            .maxBackoff(Duration.ofSeconds(5))
                            .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests)
                            .doBeforeRetry(signal -> log.warn("Translation API rate limited, retry attempt {}", signal.totalRetries() + 1)))
                    .block();

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String translated = response.getChoices().get(0).getMessage().getContent().trim();
                // Strip any quotes the LLM may have wrapped around the output
                if (translated.startsWith("\"") && translated.endsWith("\"")) {
                    translated = translated.substring(1, translated.length() - 1);
                }
                log.info("Translation: '{}' → '{}'", text, translated);
                return translated;
            }
        } catch (Exception e) {
            log.warn("Translation step failed, falling back to original text. Error: {}", e.getMessage());
        }

        // Graceful fallback: return original text
        return text;
    }

    // ──────────────────────────────────────────────────────────────────────
    // Context builders
    // ──────────────────────────────────────────────────────────────────────

    private String getCatalogContext() {
        List<com.example.voice.commanding.model.Product> products = productRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("STORE PRODUCT CATALOG:\n");
        for (com.example.voice.commanding.model.Product p : products) {
            sb.append("- ").append(p.getName())
              .append(" (Category: ").append(p.getCategory())
              .append(", Price: ").append(p.getPrice())
              .append(", Available: ").append(p.getIsAvailable())
              .append(", Seasonal: ").append(p.getIsSeasonal())
              .append(")\n");
        }
        return sb.toString();
    }

    private String getCurrentListContext(Long listId) {
        List<com.example.voice.commanding.model.ListItem> items = listItemRepository.findByShoppingListId(listId);
        StringBuilder sb = new StringBuilder();
        sb.append("CURRENT SHOPPING LIST ITEMS:\n");
        if (items.isEmpty()) {
            sb.append("(Empty list)\n");
        } else {
            for (com.example.voice.commanding.model.ListItem item : items) {
                sb.append("- ").append(item.getItemName())
                  .append(" (Quantity: ").append(item.getQuantity())
                  .append(", Category: ").append(item.getCategory())
                  .append(")\n");
            }
        }
        return sb.toString();
    }

    private String getShoppingHistoryContext(Long currentListId) {
        java.util.Set<String> currentItemNames = new java.util.HashSet<>();
        for (com.example.voice.commanding.model.ListItem item : listItemRepository.findByShoppingListId(currentListId)) {
            currentItemNames.add(item.getItemName().toLowerCase().trim());
        }

        List<com.example.voice.commanding.model.PurchaseHistory> purchaseHistory = purchaseHistoryRepository
                .findAllByOrderByPurchaseCountDesc();

        StringBuilder sb = new StringBuilder();
        sb.append("USER PREVIOUS SHOPPING HISTORY (bought in the past but NOT in current list):\n");
        if (purchaseHistory.isEmpty()) {
            sb.append("(No history yet)\n");
        } else {
            purchaseHistory.stream()
                .filter(entry -> !currentItemNames.contains(entry.getProductName().toLowerCase().trim()))
                .limit(15)
                .forEach(entry -> sb.append("- ").append(entry.getProductName())
                                    .append(" (Purchased ").append(entry.getPurchaseCount()).append(" times; last purchased ")
                                    .append(entry.getLastPurchased()).append(")\n"));
        }
        return sb.toString();
    }

    // ──────────────────────────────────────────────────────────────────────
    // Step 2: Main command processing
    // ──────────────────────────────────────────────────────────────────────

    @Transactional
    public VoiceCommandResponse processCommand(VoiceCommandRequest request) {
        if (request.getListId() == null) {
            throw new IllegalArgumentException("List ID is required for voice commands");
        }

        // Step 1: Translate the user's command to English
        String englishCommand = translateToEnglish(request.getText());

        String catalogContext = getCatalogContext();
        String currentListContext = getCurrentListContext(request.getListId());
        String historyContext = getShoppingHistoryContext(request.getListId());

        String userLanguage = request.getLanguage() != null ? request.getLanguage() : "en";

        String prompt = "You are a smart grocery assistant. The user's voice command has already been translated to English for you. " +
                "Process it and output a strictly valid JSON object.\n" +
                "\n" +
                "### RESPONSE LANGUAGE:\n" +
                "The user's language setting is: " + userLanguage + ".\n" +
                "- The JSON field names must always be in English.\n" +
                "- The 'message' field should be in the user's language (" + userLanguage + ") for a natural experience.\n" +
                "\n" +
                "### QUANTITY MANAGEMENT:\n" +
                "- Capture the exact quantity specified. E.g. \"Add 2 bottles of water\" -> quantity = 2.\n" +
                "- If no quantity is specified, default to 1.\n" +
                "\n" +
                "### CONTEXT DATA:\n" +
                catalogContext + "\n" +
                currentListContext + "\n" +
                historyContext + "\n" +
                "\n" +
                "The user says: \"" + englishCommand + "\"\n" +
                "\n" +
                "Requirements:\n" +
                "1. \"action\": can be \"ADD\", \"REMOVE\", \"MODIFY\", or \"SEARCH\".\n" +
                "2. \"message\": A friendly confirmation message in the user's language.\n" +
                "3. \"itemsToAdd\", \"itemsToRemove\", \"itemsToModify\": Lists of items. Each item must have \"name\" (string) and \"quantity\" (integer).\n" +
                "   - For ADD: Every item must also include `requestedName`. Match the item to the closest product in the catalog. " +
                "If a match is found, set both `name` and `requestedName` to the exact catalog product name. " +
                "If no match is found, set both fields to the item name as given; never substitute with a different product.\n" +
                "   - For REMOVE: Place items in `itemsToRemove` with the name matching items in the current shopping list.\n" +
                "   - For MODIFY: Place items in `itemsToModify` with the name and updated quantity/unit.\n" +
                "4. \"searchResults\": If action is SEARCH, provide a list of matching items with \"name\", \"category\", \"brand\", \"price\", \"size\".\n" +
                "5. \"recommendations\": Suggest items based on these categories:\n" +
                "   - Alternatives/Substitutes: ONLY if the user asked to ADD a product not in the catalog or unavailable. " +
                "Suggest 2-3 alternatives from the catalog. Use type = \"substitute\". Never put a substitute in `itemsToAdd`.\n" +
                "   - Current-list Recommendations: 1-2 complementary catalog products that go well with items in the list. " +
                "Do not suggest items already in the list. Use type = \"list\".\n" +
                "   - Seasonal Recommendations: 1-2 items from the catalog where Seasonal is true and not in the current list. Use type = \"seasonal\".\n" +
                "   - History Recommendations: 1-2 previously purchased products not in the current list. Use type = \"history\".\n" +
                "\n" +
                "For each recommendation, provide: \"name\" (must match catalog), \"reason\", \"category\", and \"type\".\n" +
                "Return exactly this JSON structure and NO markdown or text formatting outside of the JSON braces.";

        GroqRequest groqReq = GroqRequest.builder()
                .model(model)
                .messages(List.of(GroqRequest.Message.builder()
                        .role("user")
                        .content(prompt)
                        .build()))
                .responseFormat(GroqRequest.ResponseFormat.builder()
                        .type("json_object")
                        .build())
                .temperature(0.3)
                .build();

        VoiceCommandIntent intent = null;
        try {
            GroqResponse response = webClient.post()
                    .bodyValue(groqReq)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(3))
                            .maxBackoff(Duration.ofSeconds(15))
                            .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests)
                            .doBeforeRetry(signal -> log.warn("Groq API rate limited, retry attempt {} after backoff", signal.totalRetries() + 1)))
                    .block();

            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                String jsonText = response.getChoices().get(0).getMessage().getContent();
                // Clean up possible markdown code blocks
                if (jsonText.startsWith("```json")) {
                    jsonText = jsonText.substring(7);
                } else if (jsonText.startsWith("```")) {
                    jsonText = jsonText.substring(3);
                }
                if (jsonText.endsWith("```")) {
                    jsonText = jsonText.substring(0, jsonText.length() - 3);
                }
                log.debug("Raw LLM JSON response: {}", jsonText.trim());
                intent = objectMapper.readValue(jsonText.trim(), VoiceCommandIntent.class);
                log.debug("Parsed intent - action: {}, itemsToRemove: {}, itemsToAdd: {}, message: {}",
                        intent.getAction(), intent.getItemsToRemove(), intent.getItemsToAdd(), intent.getMessage());
            } else {
                throw new RuntimeException("Empty response from Groq API");
            }
        } catch (WebClientResponseException.TooManyRequests e) {
            log.error("Groq API rate limit exhausted after all retries");
            return VoiceCommandResponse.builder()
                    .action("ERROR")
                    .message("API rate limit reached. Please wait a moment and try again.")
                    .success(false)
                    .build();
        } catch (Exception e) {
            if (e.getCause() instanceof WebClientResponseException.TooManyRequests
                    || (e.getMessage() != null && e.getMessage().contains("429"))) {
                log.error("Groq API rate limit exhausted after all retries");
                return VoiceCommandResponse.builder()
                        .action("ERROR")
                        .message("API rate limit reached. Please wait a moment and try again.")
                        .success(false)
                        .build();
            }
            log.error("Groq API Error: {}", e.getMessage());
            throw new RuntimeException("Failed to process command: " + e.getMessage(), e);
        }
        
        intent.setAction(intent.getAction() == null ? "" : intent.getAction().trim().toUpperCase(Locale.ROOT));
        return executeIntent(intent, request.getListId(), userLanguage);
    }

    // ──────────────────────────────────────────────────────────────────────
    // Intent execution
    // ──────────────────────────────────────────────────────────────────────

    private VoiceCommandResponse executeIntent(VoiceCommandIntent intent, Long listId, String userLanguage) {
        List<SuggestionDTO> extraSuggestions = new java.util.ArrayList<>();
        List<String> unavailableItems = new java.util.ArrayList<>();
        StringBuilder customMessage = new StringBuilder();

        if ("ADD".equals(intent.getAction()) && intent.getItemsToAdd() != null) {
            List<com.example.voice.commanding.model.Product> allCatalog = productRepository.findAll();
            for (VoiceCommandIntent.ParsedItem item : intent.getItemsToAdd()) {
                String requestedName = item.getRequestedName() != null && !item.getRequestedName().isBlank()
                        ? item.getRequestedName()
                        : item.getName();
                com.example.voice.commanding.model.Product matchedProduct = null;
                matchedProduct = findBestProductMatch(allCatalog, requestedName).orElse(null);

                if (matchedProduct == null || !isRequestedProductMatch(requestedName, matchedProduct.getName())) {
                    unavailableItems.add(requestedName);
                    
                    String category = item.getCategory() != null
                            ? item.getCategory()
                            : matchedProduct != null ? matchedProduct.getCategory() : "Dairy";
                    List<com.example.voice.commanding.model.Product> alternatives = productRepository.findByCategoryIgnoreCase(category);
                    if (alternatives.isEmpty()) {
                        alternatives = allCatalog;
                    }
                    
                    int count = 0;
                    for (com.example.voice.commanding.model.Product alt : alternatives) {
                        if (alt.getIsAvailable() && count < 3) {
                            extraSuggestions.add(SuggestionDTO.builder()
                                    .name(alt.getName())
                                    .category(alt.getCategory())
                                    .reason("Alternative to " + requestedName)
                                    .type("substitute")
                                    .build());
                            count++;
                        }
                    }
                } else if (!matchedProduct.getIsAvailable()) {
                    unavailableItems.add(matchedProduct.getName());
                    
                    List<com.example.voice.commanding.model.Product> alternatives = productRepository.findByCategoryIgnoreCase(matchedProduct.getCategory());
                    int count = 0;
                    for (com.example.voice.commanding.model.Product alt : alternatives) {
                        if (alt.getIsAvailable() && !alt.getId().equals(matchedProduct.getId()) && count < 3) {
                            extraSuggestions.add(SuggestionDTO.builder()
                                    .name(alt.getName())
                                    .category(alt.getCategory())
                                    .reason("Substitute for " + matchedProduct.getName())
                                    .type("substitute")
                                    .build());
                            count++;
                        }
                    }
                } else {
                    ListItemDTO dto = new ListItemDTO();
                    dto.setItemName(matchedProduct.getName());
                    dto.setQuantity(item.getQuantity() != null ? item.getQuantity() : 1);
                    dto.setUnit(item.getUnit() != null ? item.getUnit() : matchedProduct.getSize());
                    dto.setCategory(matchedProduct.getCategory());
                    dto.setEstimatedPrice(matchedProduct.getPrice());
                    dto.setProductId(matchedProduct.getId());
                    shoppingListService.addItem(listId, dto);
                }
            }
            // Flush add operations immediately so subsequent queries see them
            entityManager.flush();
        }

        List<String> removedItemNames = new java.util.ArrayList<>();
        if ("REMOVE".equals(intent.getAction()) && intent.getItemsToRemove() != null) {
            log.debug("Processing {} items to remove", intent.getItemsToRemove().size());
            ShoppingListDTO currentList = shoppingListService.getListById(listId);
            log.debug("Current list has {} items: {}", currentList.getItems().size(),
                    currentList.getItems().stream().map(i -> i.getItemName() + "(id=" + i.getId() + ")").collect(java.util.stream.Collectors.joining(", ")));
            for (VoiceCommandIntent.ParsedItem removeReq : intent.getItemsToRemove()) {
                log.debug("Looking for item to remove: '{}'", removeReq.getName());
                List<ListItemDTO> matchedItems = findMatchingListItems(currentList.getItems(), removeReq.getName());
                if (!matchedItems.isEmpty()) {
                    for (ListItemDTO matchedItem : matchedItems) {
                        log.debug("Found matching item: '{}' (id={}), deleting...", matchedItem.getItemName(), matchedItem.getId());
                        int deletedRows = listItemRepository.deleteFromList(listId, matchedItem.getId());
                        if (deletedRows > 0) {
                            removedItemNames.add(matchedItem.getItemName());
                        }
                    }
                } else {
                    log.warn("No matching item found for remove request: '{}'", removeReq.getName());
                }
            }
        } else {
            log.debug("No itemsToRemove in parsed intent");
        }

        if ("MODIFY".equals(intent.getAction()) && intent.getItemsToModify() != null) {
            ShoppingListDTO currentList = shoppingListService.getListById(listId);
            for (VoiceCommandIntent.ParsedItem modifyReq : intent.getItemsToModify()) {
                currentList.getItems().stream()
                        .filter(i -> i.getItemName().toLowerCase().contains(modifyReq.getName().toLowerCase()))
                        .findFirst()
                        .ifPresent(matchedItem -> {
                            ListItemDTO updateDto = new ListItemDTO();
                            updateDto.setQuantity(modifyReq.getQuantity());
                            updateDto.setUnit(modifyReq.getUnit());
                            if (modifyReq.getCategory() != null) updateDto.setCategory(modifyReq.getCategory());
                            if (modifyReq.getEstimatedPrice() != null) updateDto.setEstimatedPrice(modifyReq.getEstimatedPrice());
                            shoppingListService.updateItem(listId, matchedItem.getId(), updateDto);
                        });
            }
        }

        // Flush pending DB changes and clear Hibernate first-level cache
        // to ensure the subsequent query returns accurate post-mutation data
        entityManager.flush();
        entityManager.clear();

        List<ListItemDTO> freshItems = shoppingListService.getItemsByListId(listId);
        log.debug("Fresh items after mutations: {} items", freshItems.size());

        // Build custom localized message if there were unavailable items
        if (!unavailableItems.isEmpty()) {
            String joined = String.join(", ", unavailableItems);
            if ("hi-IN".equalsIgnoreCase(userLanguage)) {
                customMessage.append(joined).append(" उपलब्ध नहीं है, विकल्प देखें।");
            } else if ("mr-IN".equalsIgnoreCase(userLanguage)) {
                customMessage.append(joined).append(" उपलब्ध नाही, पर्याय पहा.");
            } else if ("es-ES".equalsIgnoreCase(userLanguage)) {
                customMessage.append(joined).append(" no está disponible, vea las alternativas.");
            } else if ("fr-FR".equalsIgnoreCase(userLanguage)) {
                customMessage.append(joined).append(" n'est pas disponible, voir les alternatives.");
            } else if ("de-DE".equalsIgnoreCase(userLanguage)) {
                customMessage.append(joined).append(" ist nicht verfügbar, siehe Alternativen.");
            } else {
                customMessage.append(joined).append(unavailableItems.size() == 1 ? " is not available, see alternatives." : " are not available, see alternatives.");
            }
        }

        String finalMessage = intent.getMessage();
        if (customMessage.length() > 0) {
            finalMessage = customMessage.toString();
        }

        // Override message for REMOVE actions to clearly confirm which items were removed
        if (!removedItemNames.isEmpty()) {
            String joined = String.join(", ", removedItemNames);
            finalMessage = joined + (removedItemNames.size() == 1 ? " has been removed" : " have been removed") + " from your list.";
        } else if ("REMOVE".equals(intent.getAction())) {
            finalMessage = "I could not find that item in your shopping list.";
        }

        List<SuggestionDTO> finalSuggestions = new java.util.ArrayList<>();
        if (intent.getRecommendations() != null) {
            finalSuggestions.addAll(intent.getRecommendations());
        }
        for (SuggestionDTO extra : extraSuggestions) {
            boolean exists = finalSuggestions.stream().anyMatch(s -> s.getName().equalsIgnoreCase(extra.getName()));
            if (!exists) {
                finalSuggestions.add(extra);
            }
        }

        finalSuggestions.removeIf(suggestion -> freshItems.stream()
                .anyMatch(item -> item.getItemName().equalsIgnoreCase(suggestion.getName())));

        // If all items were successfully added (no customMessage), remove any substitute
        // suggestions the LLM may have hallucinated — those would falsely trigger the
        // "Item Unavailable" modal on the frontend.
        boolean hasUnavailableItems = customMessage.length() > 0;
        if (!hasUnavailableItems) {
            finalSuggestions.removeIf(s -> "substitute".equalsIgnoreCase(s.getType()));
        }

        return VoiceCommandResponse.builder()
                .action(intent.getAction())
                .message(finalMessage)
                .success(!"REMOVE".equals(intent.getAction()) || !removedItemNames.isEmpty())
                .hasUnavailableItems(hasUnavailableItems)
                .updatedItems(freshItems)
                .searchResults(intent.getSearchResults())
                .suggestions(finalSuggestions)
                .build();
    }

    // ──────────────────────────────────────────────────────────────────────
    // Product matching utilities (no more alias maps — input is pre-translated)
    // ──────────────────────────────────────────────────────────────────────

    private Optional<com.example.voice.commanding.model.Product> findBestProductMatch(
            List<com.example.voice.commanding.model.Product> products, String requestedName) {
        return products.stream()
                .map(product -> new Match<com.example.voice.commanding.model.Product>(product,
                        matchScore(product.getName(), requestedName)))
                .filter(match -> match.score() > 0)
                .max(java.util.Comparator.comparingInt(Match::score))
                .map(Match::value);
    }

    private List<ListItemDTO> findMatchingListItems(List<ListItemDTO> items, String requestedName) {
        Optional<com.example.voice.commanding.model.Product> matchedProduct = findBestProductMatch(
                productRepository.findAll(), requestedName);
        if (matchedProduct.isPresent()) {
            List<ListItemDTO> productMatches = items.stream()
                    .filter(item -> matchedProduct.get().getId().equals(item.getProductId()))
                    .toList();
            if (!productMatches.isEmpty()) {
                return productMatches;
            }
        }

        List<Match<ListItemDTO>> matches = items.stream()
                .map(item -> new Match<ListItemDTO>(item, matchScore(item.getItemName(), requestedName)))
                .filter(match -> match.score() > 0)
                .toList();

        int bestScore = matches.stream().mapToInt(Match::score).max().orElse(0);
        return matches.stream()
                .filter(match -> match.score() == bestScore)
                .map(Match::value)
                .toList();
    }

    private int matchScore(String candidateName, String requestedName) {
        String candidate = normalizeItemName(candidateName);
        String requested = normalizeItemName(requestedName);

        if (candidate.isBlank() || requested.isBlank()) {
            return 0;
        }
        if (candidate.equals(requested)) {
            return 100;
        }
        if (candidate.contains(requested) || requested.contains(candidate)) {
            return 75;
        }

        long sharedWords = java.util.Arrays.stream(candidate.split(" "))
                .filter(word -> word.length() > 2)
                .filter(word -> java.util.Arrays.asList(requested.split(" ")).contains(word))
                .count();
        return sharedWords > 0 ? (int) sharedWords * 20 : 0;
    }

    private boolean isRequestedProductMatch(String requestedName, String catalogName) {
        String requested = normalizeItemName(requestedName);
        String catalog = normalizeItemName(catalogName);
        return !requested.isBlank()
                && (requested.equals(catalog) || catalog.contains(requested) || requested.contains(catalog));
    }

    private String normalizeItemName(String name) {
        if (name == null) {
            return "";
        }
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", " ")
                .trim();

        if (normalized.endsWith("ies") && normalized.length() > 3) {
            return normalized.substring(0, normalized.length() - 3) + "y";
        }
        if (normalized.endsWith("es") && normalized.length() > 3) {
            return normalized.substring(0, normalized.length() - 2);
        }
        if (normalized.endsWith("s") && normalized.length() > 2) {
            return normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private record Match<T>(T value, int score) {
    }
}
