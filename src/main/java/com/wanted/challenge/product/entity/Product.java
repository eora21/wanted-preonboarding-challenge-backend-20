package com.wanted.challenge.product.entity;

import com.wanted.challenge.account.entity.Account;
import com.wanted.challenge.base.BaseEntity;
import com.wanted.challenge.exception.CustomException;
import com.wanted.challenge.exception.ExceptionStatus;
import com.wanted.challenge.product.model.Price;
import com.wanted.challenge.product.model.PriceConverter;
import com.wanted.challenge.product.model.Quantity;
import com.wanted.challenge.product.model.QuantityConverter;
import com.wanted.challenge.product.model.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "account_id")
    private Account seller;

    @Column(columnDefinition = "VARCHAR(100)")
    private String name;

    @Convert(converter = PriceConverter.class)
    private Price price;

    @Convert(converter = QuantityConverter.class)
    private Quantity quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_code")
    private Reservation reservation;

    public Product(Account seller, String name, Price price, Quantity quantity) {
        validInitQuantity(quantity);

        this.seller = seller;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.reservation = Reservation.SALE;
    }

    private void validInitQuantity(Quantity quantity) {
        if (quantity.value() <= 0) {
            throw new CustomException(ExceptionStatus.INIT_QUANTITY);
        }
    }

    public void purchase() {
        this.quantity = Quantity.minus(this.quantity);

        if (quantity.value() == 0 && this.reservation == Reservation.SALE) {
            this.reservation = Reservation.RESERVE;
        }
    }

    public void complete() {
        if (this.reservation == Reservation.RESERVE) {
            this.reservation = Reservation.COMPLETE;
        }
    }

    public void updatePrice(Price price) {
        this.price = price;
    }
}
