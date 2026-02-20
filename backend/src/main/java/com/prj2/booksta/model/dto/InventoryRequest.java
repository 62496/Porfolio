package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    private String bookIsbn;
    private Long quantity;
    private BigDecimal pricePerUnit;
}
