package com.example.voice.commanding.service;

import com.example.voice.commanding.dto.ListItemDTO;
import com.example.voice.commanding.dto.ShoppingListDTO;
import com.example.voice.commanding.exception.ResourceNotFoundException;
import com.example.voice.commanding.model.ListItem;
import com.example.voice.commanding.model.PurchaseHistory;
import com.example.voice.commanding.model.Product;
import com.example.voice.commanding.model.ShoppingList;
import com.example.voice.commanding.repository.ListItemRepository;
import com.example.voice.commanding.repository.ProductRepository;
import com.example.voice.commanding.repository.PurchaseHistoryRepository;
import com.example.voice.commanding.repository.ShoppingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ListItemRepository listItemRepository;
    private final ProductRepository productRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    // ==================== SHOPPING LIST CRUD ====================

    public List<ShoppingListDTO> getAllLists() {
        return shoppingListRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ShoppingListDTO getListById(Long id) {
        ShoppingList list = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + id));
        return toDTO(list);
    }

    @Transactional
    public ShoppingListDTO createList(String name) {
        ShoppingList list = ShoppingList.builder()
                .name(name)
                .build();
        return toDTO(shoppingListRepository.save(list));
    }

    @Transactional
    public ShoppingListDTO updateList(Long id, String name) {
        ShoppingList list = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + id));
        list.setName(name);
        return toDTO(shoppingListRepository.save(list));
    }

    @Transactional
    public void deleteList(Long id) {
        if (!shoppingListRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shopping list not found with id: " + id);
        }
        shoppingListRepository.deleteById(id);
    }

    // ==================== LIST ITEM CRUD ====================

    @Transactional
    public ListItemDTO addItem(Long listId, ListItemDTO itemDTO) {
        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found with id: " + listId));

        ListItem item = ListItem.builder()
                .shoppingList(list)
                .itemName(itemDTO.getItemName())
                .quantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1)
                .unit(itemDTO.getUnit())
                .category(itemDTO.getCategory())
                .estimatedPrice(itemDTO.getEstimatedPrice())
                .build();

        // Try to link with a product from catalog
        if (itemDTO.getProductId() != null) {
            productRepository.findById(itemDTO.getProductId()).ifPresent(item::setProduct);
        } else {
            // Auto-match by name
            List<Product> matched = productRepository.findByNameContainingIgnoreCase(itemDTO.getItemName());
            if (!matched.isEmpty()) {
                Product product = matched.get(0);
                item.setProduct(product);
                if (item.getEstimatedPrice() == null) {
                    item.setEstimatedPrice(product.getPrice());
                }
                if (item.getCategory() == null || item.getCategory().isEmpty()) {
                    item.setCategory(product.getCategory());
                }
            }
        }

        ListItem saved = listItemRepository.save(item);
        log.info("Added item '{}' (qty: {}) to list '{}'", saved.getItemName(), saved.getQuantity(), list.getName());
        return toItemDTO(saved);
    }

    @Transactional
    public ListItemDTO updateItem(Long listId, Long itemId, ListItemDTO itemDTO) {
        ListItem item = listItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getShoppingList().getId().equals(listId)) {
            throw new ResourceNotFoundException("Item does not belong to list: " + listId);
        }

        if (itemDTO.getItemName() != null) item.setItemName(itemDTO.getItemName());
        if (itemDTO.getQuantity() != null) item.setQuantity(itemDTO.getQuantity());
        if (itemDTO.getUnit() != null) item.setUnit(itemDTO.getUnit());
        if (itemDTO.getCategory() != null) item.setCategory(itemDTO.getCategory());
        if (itemDTO.getEstimatedPrice() != null) item.setEstimatedPrice(itemDTO.getEstimatedPrice());
        if (itemDTO.getIsChecked() != null) item.setIsChecked(itemDTO.getIsChecked());

        return toItemDTO(listItemRepository.save(item));
    }

    @Transactional
    public void deleteItem(Long listId, Long itemId) {
        ListItem item = listItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getShoppingList().getId().equals(listId)) {
            throw new ResourceNotFoundException("Item does not belong to list: " + listId);
        }

        listItemRepository.delete(item);
        log.info("Removed item '{}' from list id {}", item.getItemName(), listId);
    }

    @Transactional
    public ListItemDTO toggleItem(Long listId, Long itemId) {
        ListItem item = listItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        if (!item.getShoppingList().getId().equals(listId)) {
            throw new ResourceNotFoundException("Item does not belong to list: " + listId);
        }

        boolean wasChecked = Boolean.TRUE.equals(item.getIsChecked());
        if (wasChecked) {
            throw new IllegalArgumentException("Purchased items cannot be unchecked");
        }

        item.setIsChecked(true);
        ListItem savedItem = listItemRepository.save(item);

        recordPurchase(savedItem);

        return toItemDTO(savedItem);
    }

    private void recordPurchase(ListItem item) {
        List<PurchaseHistory> matches = purchaseHistoryRepository.findByProductNameIgnoreCase(item.getItemName());
        PurchaseHistory purchase;
        if (!matches.isEmpty()) {
            purchase = matches.get(0);
            if (matches.size() > 1) {
                log.warn("Found multiple purchase history entries for '{}', merging them.", item.getItemName());
                int extraCount = 0;
                for (int i = 1; i < matches.size(); i++) {
                    PurchaseHistory dup = matches.get(i);
                    if (dup.getPurchaseCount() != null) {
                        extraCount += dup.getPurchaseCount();
                    }
                    purchaseHistoryRepository.delete(dup);
                }
                purchase.setPurchaseCount((purchase.getPurchaseCount() == null ? 0 : purchase.getPurchaseCount()) + extraCount);
            }
        } else {
            purchase = PurchaseHistory.builder()
                    .productName(item.getItemName())
                    .category(item.getCategory())
                    .purchaseCount(0)
                    .build();
        }

        purchase.setCategory(item.getCategory());
        purchase.setQuantity(item.getQuantity());
        purchase.setPurchaseCount((purchase.getPurchaseCount() == null ? 0 : purchase.getPurchaseCount()) + 1);
        purchase.setLastPurchased(java.time.LocalDateTime.now());
        purchaseHistoryRepository.save(purchase);
        log.info("Recorded purchase history for '{}'", item.getItemName());
    }

    public List<ListItemDTO> getItemsByListId(Long listId) {
        return listItemRepository.findByShoppingListIdOrderByCategoryAscItemNameAsc(listId)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    // ==================== DTO MAPPERS ====================

    public ShoppingListDTO toDTO(ShoppingList list) {
        List<ListItemDTO> itemDTOs = list.getItems() != null
                ? list.getItems().stream().map(this::toItemDTO).collect(Collectors.toList())
                : List.of();

        BigDecimal totalCost = itemDTOs.stream()
                .filter(i -> i.getEstimatedPrice() != null && !Boolean.TRUE.equals(i.getIsChecked()))
                .map(i -> i.getEstimatedPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ShoppingListDTO.builder()
                .id(list.getId())
                .name(list.getName())
                .isActive(list.getIsActive())
                .createdAt(list.getCreatedAt())
                .updatedAt(list.getUpdatedAt())
                .items(itemDTOs)
                .itemCount(itemDTOs.size())
                .totalEstimatedCost(totalCost)
                .build();
    }

    public ListItemDTO toItemDTO(ListItem item) {
        return ListItemDTO.builder()
                .id(item.getId())
                .shoppingListId(item.getShoppingList().getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .category(item.getCategory())
                .estimatedPrice(item.getEstimatedPrice())
                .isChecked(item.getIsChecked())
                .addedAt(item.getAddedAt())
                .imageUrl(item.getProduct() != null ? item.getProduct().getImageUrl() : null)
                .build();
    }
}
