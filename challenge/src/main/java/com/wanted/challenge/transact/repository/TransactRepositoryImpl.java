package com.wanted.challenge.transact.repository;

import static com.wanted.challenge.transact.entity.QTransact.transact;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.challenge.product.entity.Product;
import com.wanted.challenge.transact.model.TransactDetail;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactRepositoryImpl implements TransactRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public TransactDetail retrieveLastTransactDetail(Long buyerId, Long productId) {
        return jpaQueryFactory
                .select(transact.transactDetail)
                .from(transact)
                .where(transact.id.eq(maxPurchaseId(buyerId, productId)))
                .fetchOne();
    }

    private static JPQLQuery<Long> maxPurchaseId(Long buyerId, Long productId) {
        return JPAExpressions
                .select(transact.id.max())
                .from(transact)
                .where(transact.buyer.id.eq(buyerId),
                        transact.product.id.eq(productId));
    }

    public boolean isPurchaseAlready(Long buyerId, Long productId) {
        Long transactId = jpaQueryFactory
                .select(transact.id)
                .from(transact)
                .where(transact.buyer.id.eq(buyerId),
                        transact.product.id.eq(productId))
                .fetchFirst();

        return Objects.nonNull(transactId);
    }

    public Set<TransactDetail> retrieveProductTransactDetails(Product product) {
        List<TransactDetail> transactDetails = jpaQueryFactory
                .selectDistinct(transact.transactDetail)
                .from(transact)
                .where(transact.id.in(lastBuyerPurchaseId(product)))
                .fetch();

        return EnumSet.copyOf(transactDetails);
    }

    private static JPQLQuery<Long> lastBuyerPurchaseId(Product product) {
        return JPAExpressions
                .select(transact.id.max())
                .from(transact)
                .groupBy(transact.buyer)
                .where(transact.product.eq(product));
    }
}
