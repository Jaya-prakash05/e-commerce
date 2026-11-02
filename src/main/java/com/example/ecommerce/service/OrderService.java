package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final ProductRepository productRepository;

	public OrderService(OrderRepository orderRepository, CartRepository cartRepository,
			ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Order getOrderById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
	}

	@Transactional
	public Order createOrder(CheckoutRequest request) {
		Cart cart = cartRepository.findAll().stream().findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty"));

		if (cart.getItems().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
		}

		Order order = new Order();
		order.setCustomerName(request.getCustomerName());
		order.setEmail(request.getEmail());
		order.setOrderDate(LocalDateTime.now());

		for (CartItem cartItem : cart.getItems()) {
			Product product = productRepository.findById(cartItem.getProduct().getId())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

			if (product.getStock() < cartItem.getQuantity()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Insufficient stock for product: " + product.getName());
			}

			product.setStock(product.getStock() - cartItem.getQuantity());
			productRepository.save(product);

			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setProductName(product.getName());
			orderItem.setUnitPrice(product.getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			order.getItems().add(orderItem);
			order.setTotal(order.getTotal().add(orderItem.getSubtotal()));
		}

		Order savedOrder = orderRepository.save(order);
		cart.getItems().clear();
		cart.setTotal(BigDecimal.ZERO);
		cartRepository.save(cart);

		return savedOrder;
	}
}
