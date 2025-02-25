package com.wanted.challenge.product.controller;

import com.wanted.challenge.account.model.AccountDetail;
import com.wanted.challenge.product.model.Price;
import com.wanted.challenge.product.model.Quantity;
import com.wanted.challenge.product.request.PurchaseRequest;
import com.wanted.challenge.product.request.RegisterRequest;
import com.wanted.challenge.product.request.UpdatePriceRequest;
import com.wanted.challenge.product.response.ProductDetailResponse;
import com.wanted.challenge.product.response.ProductPreviewResponse;
import com.wanted.challenge.product.response.PurchaseProductResponse;
import com.wanted.challenge.product.response.ReserveProductResponse;
import com.wanted.challenge.product.service.ProductService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest registerRequest,
                                         @AuthenticationPrincipal AccountDetail accountDetail) {

        Price price = new Price(registerRequest.price());
        Quantity quantity = new Quantity(registerRequest.quantity());
        Long productId = productService.register(registerRequest.name(), price, quantity, accountDetail.getAccountId());

        return ResponseEntity.created(URI.create("/products/" + productId))
                .build();
    }

    @PostMapping("/purchase")
    public ResponseEntity<Void> purchase(@RequestBody @Valid PurchaseRequest purchaseRequest,
                                         @AuthenticationPrincipal AccountDetail accountDetail) {
        productService.purchase(purchaseRequest.productId(), accountDetail.getAccountId());

        return ResponseEntity.ok()
                .build();
    }

    @GetMapping
    public ResponseEntity<Page<ProductPreviewResponse>> preview(Pageable pageable) {
        Page<ProductPreviewResponse> preview = productService.preview(pageable);

        return ResponseEntity.ok(preview);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> detail(@PathVariable Long productId,
                                                        @AuthenticationPrincipal AccountDetail accountDetail) {

        ProductDetailResponse productDetailResponse = productService.detail(productId, getAccountId(accountDetail));

        return ResponseEntity.ok(productDetailResponse);
    }

    private Optional<Long> getAccountId(AccountDetail accountDetail) {
        if (Objects.isNull(accountDetail)) {
            return Optional.empty();
        }

        return Optional.ofNullable(accountDetail.getAccountId());
    }

    @GetMapping("/purchase")
    public ResponseEntity<Page<PurchaseProductResponse>> purchaseProducts(
            @AuthenticationPrincipal AccountDetail accountDetail,
            Pageable pageable) {

        Page<PurchaseProductResponse> purchaseProductResponses =
                productService.purchaseProducts(accountDetail.getAccountId(), pageable);

        return ResponseEntity.ok(purchaseProductResponses);
    }

    @GetMapping("/reserve")
    public ResponseEntity<Page<ReserveProductResponse>> reserveProducts(
            @AuthenticationPrincipal AccountDetail accountDetail,
            Pageable pageable) {

        Page<ReserveProductResponse> purchaseProductResponses =
                productService.reserveProducts(accountDetail.getAccountId(), pageable);

        return ResponseEntity.ok(purchaseProductResponses);
    }

    @PatchMapping("/price")
    public ResponseEntity<Void> updatePrice(@RequestBody @Valid UpdatePriceRequest updatePriceRequest,
                                            @AuthenticationPrincipal AccountDetail accountDetail) {

        productService.updatePrice(updatePriceRequest.productId(), updatePriceRequest.price(),
                accountDetail.getAccountId());

        return ResponseEntity.ok()
                .build();
    }
}
