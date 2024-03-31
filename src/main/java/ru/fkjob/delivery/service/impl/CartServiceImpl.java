package ru.fkjob.delivery.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fkjob.delivery.domain.CartEntity;
import ru.fkjob.delivery.domain.DishEntity;
import ru.fkjob.delivery.domain.UserEntity;
import ru.fkjob.delivery.dto.cart.CartDto;
import ru.fkjob.delivery.dto.cart.CartItemDishDto;
import ru.fkjob.delivery.dto.cart.CartItemDto;
import ru.fkjob.delivery.dto.cart.CartDishInfoDto;
import ru.fkjob.delivery.dto.dish.DishItemDto;
import ru.fkjob.delivery.dto.image.ImageDishDto;
import ru.fkjob.delivery.exception.NotFoundException;
import ru.fkjob.delivery.repository.CartRepository;
import ru.fkjob.delivery.repository.DishRepository;
import ru.fkjob.delivery.repository.UserRepository;
import ru.fkjob.delivery.service.CartService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final DishRepository dishRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public CartDto createCart(List<DishItemDto> dishes) {
        Long userId = getUserIdIfAuthentication();
        CartDto cartDto = new CartDto();
        if (dishes == null || dishes.isEmpty()) {
            cartDto.setSuccess(false);
            return cartDto;
        }
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found with id: %s", userId)));
        CartEntity cartEntity = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(user);
                    return newCart;
                });
        cartEntity.getDishes().clear();
        dishes.forEach(dishItem -> {
            DishEntity dish = dishRepository.findById(dishItem.getId())
                    .orElseThrow(() -> new NotFoundException(String.format("Dish not found with id: %s", dishItem.getId())));
            IntStream.range(0, dishItem.getCount()).forEach(i -> cartEntity.getDishes().add(dish));
        });
        cartRepository.save(cartEntity);
        cartDto.setSuccess(true);
        cartDto.setId(cartEntity.getId());
        CartItemDto cartItem = new CartItemDto();
        List<DishItemDto> dishItems = cartEntity.getDishes().stream()
                .collect(Collectors.groupingBy(DishEntity::getId, Collectors.summingInt(d -> 1)))
                .entrySet().stream()
                .map(entry -> new DishItemDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        cartItem.setDishItems(dishItems);
        cartDto.setCart(cartItem);
        return cartDto;
    }

    @Override
    public List<CartDishInfoDto> getSummary(Long cartId) {
        Long userId = getUserIdIfAuthentication();
        CartEntity cartEntity = getCartEntity(userId, cartId);
        List<CartItemDishDto> items = createCart(cartEntity);
        CartDishInfoDto cartInfo = calculateCartInfo(items);
        return Collections.singletonList(cartInfo);
    }

    private Long getUserIdIfAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден")).getId();
        }
        throw new NotFoundException("Пользователь не авторизован");
    }

    private CartEntity getCartEntity(Long userId, Long cartId) {
        return cartId == null ? userRepository.findById(userId)
                .map(user -> cartRepository.findByUserId(user.getId())
                        .orElseGet(() -> {
                            CartEntity newCart = new CartEntity();
                            newCart.setUser(user);
                            return newCart;
                        })).orElseThrow(() -> new NotFoundException(String.format("User not found with id: %s", userId)))
                : cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException(String.format("Cart not found with id: %s", cartId)));
    }

    private List<CartItemDishDto> createCart(CartEntity cartEntity) {
        return cartEntity.getDishes().stream()
                .distinct()
                .map(dishEntity -> CartItemDishDto.builder()
                        .dishId(dishEntity.getId())
                        .name(dishEntity.getName())
                        .price(dishEntity.getPrice())
                        .quantity(cartRepository.findCountDishFromCartByDishId(dishEntity.getId()))
                        .imageDish(ImageDishDto.builder()
                                .id(dishEntity.getImage() != null ? dishEntity.getImage().getId() : null)
                                .url(dishEntity.getImage() != null ? dishEntity.getImage().getUrl() : null)
                                .build())
                        .build())
                .collect(Collectors.toList());
    }

    private CartDishInfoDto calculateCartInfo(List<CartItemDishDto> items) {
        int quantity = 0;
        double totalPrice = 0;
        for (CartItemDishDto item : items) {
            quantity += item.getQuantity();
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return CartDishInfoDto.builder()
                .items(items)
                .totalPrice(totalPrice)
                .totalQuantity(quantity)
                .build();
    }
}
