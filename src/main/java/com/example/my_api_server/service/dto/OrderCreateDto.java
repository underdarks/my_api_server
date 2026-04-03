package com.example.my_api_server.service.dto;

import java.util.List;

//주문 시 필요한 데이터(DTO)
public record OrderCreateDto(

    Long memberId, //구매자

    List<Long> productId, //주문 상품 ID들

    List<Long> count //주문 수량

) {

}
