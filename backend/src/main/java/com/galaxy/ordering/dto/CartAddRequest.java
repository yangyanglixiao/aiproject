package com.galaxy.ordering.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartAddRequest {
    @NotNull
    private Long merchantId;
    @NotNull
    private Long productId;
    @Min(1)
    private Integer quantity = 1;
}
