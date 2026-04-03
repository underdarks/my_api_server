package com.example.my_api_server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Getter
@Builder
public class Product { //상품

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //pk

    private String productName; //상품명

    private String productNumber; //상품번호(SHIRT-RED-001)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType; //상품타입

    private Long price; //가격

    private Long stock; //재고

    @Version
    private Long version; //버전

    //필요한건만 바꿀수있게 Setter처럼 변경할수 있게하고, 네이밍은 의미있는 메서드로 만들어둡니다.
    public void changeProductName(String changeProductName) {
        this.productName = changeProductName;
    }

    //재고 +
    public void increaseStock(Long addStock) {
        this.stock += addStock; //현재 재고 + 더해줄재고
    }

    //재고 -
    public void decreaseStock(Long subStock) {
        this.stock -= subStock; //현재 재고 - 감소할 재고
    }

    //구매 가능 여부 확인
    //캡슐화를하게되면 변경지점이 되게 작아진다. 코드의 유지보수(변화)가 적어지게되요
    public void isBuyAvaliable(Long count) {
        if (getStock() - count < 0 &&
            getProductType().equals(ProductType.ACCESSORIES)
            || getProductType().equals(ProductType.CLOTHES)
        ) {
            throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다!");
        }
    }
}
