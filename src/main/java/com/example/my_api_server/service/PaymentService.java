package com.example.my_api_server.service;

import com.example.my_api_server.entity.OrderProduct;
import com.example.my_api_server.entity.Product;
import java.util.List;
import java.util.stream.IntStream;

public class PaymentService {


    public void payment() {
        List<OrderProduct> orderProducts = IntStream.range(0, dto.count().size())
            .mapToObj(idx -> {
                //재고에대해서 차감을 해야한다.(음수 처리x)
                Product product = products.get(idx);

                //만약에 변경이된다면 사이드 이펙트(어떻게하면 다른 쪾에서 영향을 적게받을 수 있을까?)
                //현재 재고에서 주문재고 감했을때 음수이면 <0 예외터트린다!(주문못하게 막는다!)
                if (product.isBuyAvaliable(1L) {
                    throw new RuntimeException("재고가 음수이니 주문 할 수 없습니다!");
                }

                //재고 감소
                //update product set stock = stock - 1 where pk =1;(더티체킹, 스냅샷 값을 비교한다!)
                product.decreaseStock(dto.count().get(idx));

                return OrderProduct.builder()
                    .order(order)
                    .number(dto.count().get(idx)) //product에 맞는 주문개수를 찾는다!
                    .product(products.get(idx))
                    .build();
            })
            .toList();
    }
}
