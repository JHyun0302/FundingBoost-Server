package kcs.funding.fundingboost.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @NotNull
    private int quantity;

    @NotNull
    private int price;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "memeber_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public static Order createOrder(int quantity, int price, Item item, Member member) {
        Order order = new Order();
        order.quantity = quantity;
        order.price = price;
        order.item = item;
        order.member = member;
        return order;
    }
}
