package com.example.my_api_server.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Test
    @DisplayName("리팩토링 코드에 대한 테스트 코드 작성")
    public void OrderServiceTest() {
        //given
//        OrderResponseDto order = orderService.createOrder(new OrderCreateDto(1L, ));
//
//        //when
//
//        //then
//        assertThat(order).isNull();
//        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
    }

}