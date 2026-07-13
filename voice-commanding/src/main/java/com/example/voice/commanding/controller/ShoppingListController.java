package com.example.voice.commanding.controller;

import com.example.voice.commanding.dto.ListItemDTO;
import com.example.voice.commanding.dto.ShoppingListDTO;
import com.example.voice.commanding.service.ShoppingListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;


    @GetMapping
    public ResponseEntity<List<ShoppingListDTO>> getAllLists() {
        return ResponseEntity.ok(shoppingListService.getAllLists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingListDTO> getListById(@PathVariable Long id) {
        return ResponseEntity.ok(shoppingListService.getListById(id));
    }

    @PostMapping
    public ResponseEntity<ShoppingListDTO> createList(@RequestBody Map<String, String> body) {
        String name = body.getOrDefault("name", "My Shopping List");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shoppingListService.createList(name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingListDTO> updateList(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String name = body.get("name");
        return ResponseEntity.ok(shoppingListService.updateList(id, name));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long id) {
        shoppingListService.deleteList(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/{listId}/items")
    public ResponseEntity<List<ListItemDTO>> getItems(@PathVariable Long listId) {
        return ResponseEntity.ok(shoppingListService.getItemsByListId(listId));
    }

    @PostMapping("/{listId}/items")
    public ResponseEntity<ListItemDTO> addItem(@PathVariable Long listId, @RequestBody ListItemDTO itemDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shoppingListService.addItem(listId, itemDTO));
    }

    @PutMapping("/{listId}/items/{itemId}")
    public ResponseEntity<ListItemDTO> updateItem(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @RequestBody ListItemDTO itemDTO) {
        return ResponseEntity.ok(shoppingListService.updateItem(listId, itemId, itemDTO));
    }

    @DeleteMapping("/{listId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long listId, @PathVariable Long itemId) {
        shoppingListService.deleteItem(listId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{listId}/items/{itemId}/toggle")
    public ResponseEntity<ListItemDTO> toggleItem(@PathVariable Long listId, @PathVariable Long itemId) {
        return ResponseEntity.ok(shoppingListService.toggleItem(listId, itemId));
    }
}
