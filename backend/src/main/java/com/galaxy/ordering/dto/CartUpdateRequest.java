package com.galaxy.ordering.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartUpdateRequest {
    @NotNull
    @Min(1)
    private Integer quantity;
}
