package com.prj2.booksta.controller;

import com.prj2.booksta.model.dto.MarketplaceBookListing;
import com.prj2.booksta.model.dto.MarketplaceSummary;
import com.prj2.booksta.model.dto.SellerListing;
import com.prj2.booksta.service.MarketplaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    @Autowired
    private MarketplaceService marketplaceService;

    /**
     * Get all books with their marketplace data in one call
     * Only returns books that are being sold (have at least one seller with stock > 0)
     */
    @GetMapping("/books")
    public ResponseEntity<List<MarketplaceBookListing>> getAllBooksWithMarketplaceData() {
        return ResponseEntity.ok(marketplaceService.getAllBooksWithMarketplaceData(true));
    }

    /**
     * Get marketplace summary for a specific book (lowest price, seller count, availability)
     */
    @GetMapping("/books/{isbn}/summary")
    public ResponseEntity<MarketplaceSummary> getMarketplaceSummary(@PathVariable String isbn) {
        return ResponseEntity.ok(marketplaceService.getMarketplaceSummary(isbn));
    }

    /**
     * Get all seller listings for a book (sorted by price ascending)
     */
    @GetMapping("/books/{isbn}/sellers")
    public ResponseEntity<List<SellerListing>> getSellerListings(@PathVariable String isbn) {
        return ResponseEntity.ok(marketplaceService.getSellerListings(isbn));
    }
}
