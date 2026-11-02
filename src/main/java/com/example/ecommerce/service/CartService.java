package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartItemRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;

	public CartService(CartRepository cartRepository, ProductRepository productRepository) {
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
	}

	public Cart getCart() {
		return cartRepository.findAll().stream().findFirst().orElseGet(() -> cartRepository.save(new Cart()));
	}

	@Transactional
	public Cart addItem(CartItemRequest request) {
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

		if (!product.isActive()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not available");
		}

		if (request.getQuantity() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
		}

		Cart cart = getCart();
		Optional<CartItem> existingItem = cart.getItems().stream()
				.filter(item -> item.getProduct() != null && item.getProduct().getId().equals(product.getId()))
				.findFirst();

		if (existingItem.isPresent()) {
			CartItem item = existingItem.get();
			item.setQuantity(item.getQuantity() + request.getQuantity());
			cart.recalculateTotal();
			return cartRepository.save(cart);
		}

		CartItem item = new CartItem();
		item.setCart(cart);
		item.setProduct(product);
		item.setQuantity(request.getQuantity());
		cart.getItems().add(item);
		cart.recalculateTotal();
		return cartRepository.save(cart);
	}

	@Transactional
	public Cart updateItem(Long productId, int quantity) {
		if (quantity <= 0) {
			return removeItem(productId);
		}

		Cart cart = getCart();
		CartItem item = cart.getItems().stream()
				.filter(cartItem -> cartItem.getProduct() != null && cartItem.getProduct().getId().equals(productId))
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found in cart"));

		item.setQuantity(quantity);
		cart.recalculateTotal();
		return cartRepository.save(cart);
	}

	@Transactional
	public Cart removeItem(Long productId) {
		Cart cart = getCart();
		cart.getItems().removeIf(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));
		cart.recalculateTotal();
		return cartRepository.save(cart);
	}

	@Transactional
	public void clearCart() {
		Cart cart = getCart();
		cart.getItems().clear();
		cart.setTotal(BigDecimal.ZERO);
		cartRepository.save(cart);
	}

	public List<CartItem> getCartItems() {
		return getCart().getItems();
	}
}
