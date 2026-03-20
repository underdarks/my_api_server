package com.example.my_api_server.service.dto;

public record ProductUpdateDto(
    Long productId, //상품 id
    String changeProductName, //상품명
    Long changeStock //변경할 재고 수량
) {

}
