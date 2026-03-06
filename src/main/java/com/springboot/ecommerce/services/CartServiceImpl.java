package com.springboot.ecommerce.services;

import com.springboot.ecommerce.dtos.AddToCartRequest;
import com.springboot.ecommerce.dtos.CartDto;
import com.springboot.ecommerce.dtos.CartItemDto;
import com.springboot.ecommerce.entities.Cart;
import com.springboot.ecommerce.entities.CartItem;
import com.springboot.ecommerce.entities.Product;
import com.springboot.ecommerce.entities.User;
import com.springboot.ecommerce.exceptions.ProductNotFoundException;
import com.springboot.ecommerce.exceptions.UserNotFoundException;
import com.springboot.ecommerce.mappers.CartMapper;
import com.springboot.ecommerce.repositories.CartItemRepository;
import com.springboot.ecommerce.repositories.CartRepository;
import com.springboot.ecommerce.repositories.ProductRepository;
import com.springboot.ecommerce.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{
	private CartRepository cartRepository;
	private CartItemRepository cartItemRepository;
	private ProductRepository productRepository;
	private UserRepository userRepository;
	private CartMapper cartMapper;

	@Override
	@Transactional
	public CartDto addToCart(Long userId, AddToCartRequest dto) {

		User user = userRepository.findById(userId)
				.orElseThrow(UserNotFoundException::new);

		if (!user.isCustomer()) {
			throw new IllegalArgumentException("Only customers can add items to cart");
		}

		Integer addedQuantity = dto.getQuantity() == null ? 1 : dto.getQuantity();

		if (addedQuantity <= 0) {
			throw new IllegalArgumentException("Quantity must be greater than zero");
		}

		Product product = productRepository.findById(dto.getProductId())
				.orElseThrow(ProductNotFoundException::new);

		Integer availableStock = product.getStockQuantity();


		Cart cart;

		if (user.hasCart()) {
			cart = user.getCart();
			cart.setUpdatedAt(LocalDateTime.now());
		} else {
			cart = new Cart();
			cart.setUser(user);
			cart.setCreatedAt(LocalDateTime.now());
			cartRepository.save(cart);

			user.setCart(cart);
			userRepository.save(user);
		}

		if (cart.getItems() == null) {
			cart.setItems(new java.util.ArrayList<>());
		}

		Integer quantityInCart = cart.getItems() == null ? 0 : cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.map(CartItem::getQuantity)
				.reduce(0, Integer::sum);

		if (availableStock < quantityInCart + addedQuantity) {
			throw new IllegalArgumentException("Not enough stock available");
		}

		cart.getItems().stream()
				.filter(item -> item.getProduct().getId().equals(product.getId()))
				.findFirst()
				.ifPresentOrElse(
						item -> {
							item.setQuantity(item.getQuantity() + addedQuantity);
							cartItemRepository.save(item);
						},
						() -> addCartItemToCart(cart, product, addedQuantity)
				);

		cartRepository.save(cart);
		return cartMapper.toCartDto(cart);
	}

	@Override
	public CartDto getCart(Long userId) {

		return null;
	}

	@Override
	public CartItemDto getCartItem(Long userId, Long cartItemId) {
		return null;
	}

	@Override
	public void removeFromCart(Long userId, Long cartId, Long productId) {

	}

	@Override
	public void updateCartItem(Long userId, Long cartId, Long productId, Integer quantity) {

	}

	@Override
	public void clearCart(Long userId, Long cartId) {

	}

	private void addCartItemToCart(Cart cart, Product product, Integer quantity) {

		CartItem cartItem = new CartItem();
		cartItem.setCart(cart);
		cartItem.setProduct(product);
		cartItem.setQuantity(quantity);

		cartItemRepository.save(cartItem);

		cart.getItems().add(cartItem);
		cartRepository.save(cart);
	}
}
