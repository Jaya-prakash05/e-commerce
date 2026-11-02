package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CartItemRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping("/cart")
	public Cart getCart() {
		return cartService.getCart();
	}

	@GetMapping("/cart/items")
	public List<CartItem> getCartItems() {
		return cartService.getCartItems();
	}

	@PostMapping("/cart/items")
	public ResponseEntity<Cart> addItem(@Valid @RequestBody CartItemRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItem(request));
	}

	@PutMapping("/cart/items/{productId}")
	public Cart updateItem(@PathVariable Long productId, @RequestBody CartItemRequest request) {
		return cartService.updateItem(productId, request.getQuantity());
	}

	@DeleteMapping("/cart/items/{productId}")
	public ResponseEntity<Cart> removeItem(@PathVariable Long productId) {
		return ResponseEntity.ok(cartService.removeItem(productId));
	}

	@DeleteMapping("/cart")
	public ResponseEntity<Void> clearCart() {
		cartService.clearCart();
		return ResponseEntity.noContent().build();
	}
}
