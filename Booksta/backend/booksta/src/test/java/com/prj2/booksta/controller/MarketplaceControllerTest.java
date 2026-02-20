package com.prj2.booksta.controller;

import com.prj2.booksta.model.dto.MarketplaceBookListing;
import com.prj2.booksta.model.dto.MarketplaceSummary;
import com.prj2.booksta.model.dto.SellerListing;
import com.prj2.booksta.service.MarketplaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MarketplaceControllerTest {

    @Mock
    private MarketplaceService marketplaceService;

    @InjectMocks
    private MarketplaceController marketplaceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(marketplaceController).build();
    }

    @Test
    void testGetAllBooksWithMarketplaceData() throws Exception {
        MarketplaceBookListing listing = new MarketplaceBookListing();
        listing.setIsbn("12345");
        listing.setTitle("Test Book");
        listing.setLowestPrice(new BigDecimal("10.00"));

        when(marketplaceService.getAllBooksWithMarketplaceData(true))
                .thenReturn(Collections.singletonList(listing));

        mockMvc.perform(get("/api/marketplace/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].isbn", is("12345")))
                .andExpect(jsonPath("$[0].title", is("Test Book")));

        verify(marketplaceService).getAllBooksWithMarketplaceData(true);
    }

    @Test
    void testGetMarketplaceSummary() throws Exception {
        MarketplaceSummary summary = new MarketplaceSummary();
        summary.setInStock(true);
        summary.setLowestPrice(new BigDecimal("15.50"));
        summary.setSellerCount(3);

        when(marketplaceService.getMarketplaceSummary("12345")).thenReturn(summary);

        mockMvc.perform(get("/api/marketplace/books/{isbn}/summary", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inStock", is(true)))
                .andExpect(jsonPath("$.sellerCount", is(3)));

        verify(marketplaceService).getMarketplaceSummary("12345");
    }

    @Test
    void testGetSellerListings() throws Exception {
        SellerListing seller1 = new SellerListing();
        seller1.setSellerFirstName("John");
        seller1.setPricePerUnit(new BigDecimal("10.00"));

        SellerListing seller2 = new SellerListing();
        seller2.setSellerFirstName("Jane");
        seller2.setPricePerUnit(new BigDecimal("12.00"));

        when(marketplaceService.getSellerListings("12345"))
                .thenReturn(Arrays.asList(seller1, seller2));

        mockMvc.perform(get("/api/marketplace/books/{isbn}/sellers", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sellerFirstName", is("John")))
                .andExpect(jsonPath("$[1].sellerFirstName", is("Jane")));

        verify(marketplaceService).getSellerListings("12345");
    }
}