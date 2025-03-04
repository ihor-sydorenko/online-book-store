package online.book.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.book.store.dto.cartitem.CartItemRequestDto;
import online.book.store.dto.cartitem.UpdateCartItemRequestDto;
import online.book.store.dto.shoppingcart.ShoppingCartDto;
import online.book.store.repository.cartitem.CartItemRepository;
import online.book.store.service.ShoppingCartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management",
        description = "Endpoints for managing shopping cart and cartItems")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemRepository cartItemRepository;

    @Operation(summary = "Retrieve user's shopping cart",
            description = "Find shopping cart by user")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ShoppingCartDto getShoppingCartByUser() {
        return shoppingCartService.getShoppingCartByUser();
    }

    @Operation(summary = "Add book to the shopping cart",
            description = "Add book to the shopping cart")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ShoppingCartDto addBookToShoppingCart(
            @RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addBookToShoppingCart(requestDto);
    }

    @Operation(summary = "Update quantity)",
            description = "Change quantity of book in shopping cart by cart item id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/cart-items/{id}")
    public ShoppingCartDto updateBookQuantity(
            @RequestBody @Valid UpdateCartItemRequestDto requestDto,
            @PathVariable Long id) {
        return shoppingCartService.updateCartItem(requestDto, id);
    }

    @Operation(summary = "Remove a book",
            description = "Delete cart item by id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/cart-items/{id}")
    public ShoppingCartDto deleteCartItemById(@PathVariable Long id) {
        return shoppingCartService.deleteCartItemById(id);
    }
}
