package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.UserBookInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerListing {
    private Long sellerId;
    private String sellerFirstName;
    private String sellerLastName;
    private String sellerPicture;
    private Long quantity;
    private BigDecimal pricePerUnit;

    public static SellerListing fromEntity(UserBookInventory inventory) {
        SellerListing listing = new SellerListing();
        listing.setSellerId(inventory.getUser().getId());
        listing.setSellerFirstName(inventory.getUser().getFirstName());
        listing.setSellerLastName(inventory.getUser().getLastName());
        listing.setSellerPicture(inventory.getUser().getPicture());
        listing.setQuantity(inventory.getQuantity());
        listing.setPricePerUnit(inventory.getPricePerUnit());
        return listing;
    }
}
