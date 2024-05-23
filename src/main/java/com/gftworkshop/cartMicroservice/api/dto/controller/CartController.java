package com.gftworkshop.cartMicroservice.api.dto.controller;

import com.gftworkshop.cartMicroservice.api.dto.CartDto;
import com.gftworkshop.cartMicroservice.api.dto.UpdatedCartProductDto;
import com.gftworkshop.cartMicroservice.api.dto.CartProductDto;
import com.gftworkshop.cartMicroservice.model.Cart;
import com.gftworkshop.cartMicroservice.model.CartProduct;
import com.gftworkshop.cartMicroservice.services.impl.CartProductServiceImpl;
import com.gftworkshop.cartMicroservice.services.impl.CartServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Validated
public class CartController {

    private CartServiceImpl cartService;
    private CartProductServiceImpl cartProductService;

    public CartController(CartServiceImpl cartService, CartProductServiceImpl cartProductService) {
        this.cartService = cartService;
        this.cartProductService = cartProductService;
    }

    @GetMapping("/carts")
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> savedCart = cartService.getAllCarts();
        return ResponseEntity.ok(savedCart);
    }

    @PostMapping("/carts/{id}")
    public ResponseEntity<CartDto> addCartByUserId(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        CartDto createdCart = cartService.createCart(idCart);
        return ResponseEntity.created(URI.create("/carts/" + createdCart.getId())).body(createdCart);
    }


    @GetMapping("/carts/{id}")
    public ResponseEntity<CartDto> getCartById(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        CartDto receivedCart = cartService.getCart(idCart);
        return ResponseEntity.ok(receivedCart);
    }

    @DeleteMapping("/carts/{id}")
    public ResponseEntity<Void> removeCartById(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        cartService.clearCart(idCart);
        return ResponseEntity.ok().build();

    }

    @PostMapping("/carts/products")
    public ResponseEntity<Void> addProduct(@Valid @RequestBody CartProduct cartProduct) {
        cartService.addProductToCart(cartProduct);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/carts/products")
    public ResponseEntity<Void> updateProduct(@Valid @RequestBody UpdatedCartProductDto cartProduct) {
        cartProductService.updateQuantity(cartProduct.getId(), cartProduct.getQuantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/carts/products/{id}")
    public ResponseEntity<CartProductDto> removeProductById(@PathVariable("id") String id) {
        Long idCart = Long.parseLong(id);
        CartProductDto deletedCartProduct = cartProductService.removeProduct(idCart);
        return ResponseEntity.ok(deletedCartProduct);
    }
}
