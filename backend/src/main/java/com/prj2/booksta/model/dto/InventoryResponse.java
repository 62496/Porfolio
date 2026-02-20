package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.UserBookInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long userId;
    private String bookIsbn;
    private String bookTitle;
    private String bookImageUrl;
    private Long quantity;
    private BigDecimal pricePerUnit;

    public static InventoryResponse fromEntity(UserBookInventory inventory) {
        InventoryResponse response = new InventoryResponse();
        response.setUserId(inventory.getUser().getId());
        response.setBookIsbn(inventory.getBook().getIsbn());
        response.setBookTitle(inventory.getBook().getTitle());
        response.setBookImageUrl(inventory.getBook().getImage() != null
                ? inventory.getBook().getImage().getUrl()
                : null);
        response.setQuantity(inventory.getQuantity());
        response.setPricePerUnit(inventory.getPricePerUnit());
        return response;
    }
}
