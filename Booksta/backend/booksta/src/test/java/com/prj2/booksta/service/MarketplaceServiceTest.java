package com.prj2.booksta.service;

import com.prj2.booksta.model.*;
import com.prj2.booksta.model.dto.MarketplaceBookListing;
import com.prj2.booksta.model.dto.MarketplaceSummary;
import com.prj2.booksta.model.dto.SellerListing;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.UserBookInventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketplaceServiceTest {

    @Mock
    private UserBookInventoryRepository inventoryRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private MarketplaceService marketplaceService;

    private Book book;
    private User seller1;
    private User seller2;
    private UserBookInventory inventory1;
    private UserBookInventory inventory2;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setIsbn("123456789");
        book.setTitle("Test Book");
        book.setDescription("Desc");
        book.setPublishingYear(2023);
        book.setPages(300L);
        
        Author author = new Author();
        author.setId(1L);
        author.setFirstName("John");
        author.setLastName("Doe");

        Image image = new Image();
        image.setUrl("http://image.url");
        book.setImage(image);
        author.setImage(image);

        Set<Author> authors = new HashSet<>();
        authors.add(author);
        book.setAuthors(authors);

        seller1 = new User();
        seller1.setId(10L);
        seller1.setFirstName("Seller");
        seller1.setLastName("One");

        seller2 = new User();
        seller2.setId(20L);
        seller2.setFirstName("Seller");
        seller2.setLastName("Two");

        inventory1 = new UserBookInventory();
        UserBookInventoryId id1 = new UserBookInventoryId(seller1.getId(), book.getIsbn());
        inventory1.setId(id1);
        inventory1.setBook(book);
        inventory1.setUser(seller1);
        inventory1.setQuantity(5L);
        inventory1.setPricePerUnit(new BigDecimal("10.00"));

        inventory2 = new UserBookInventory();
        UserBookInventoryId id2 = new UserBookInventoryId(seller2.getId(), book.getIsbn());
        inventory2.setId(id2);
        inventory2.setBook(book);
        inventory2.setUser(seller2);
        inventory2.setQuantity(3L);
        inventory2.setPricePerUnit(new BigDecimal("8.50"));
    }

    @Test
    void testGetMarketplaceSummary_BookNotFound() {
        when(bookRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            marketplaceService.getMarketplaceSummary("999")
        );
    }

    @Test
    void testGetMarketplaceSummary_NoStock() {
        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(inventoryRepository.findByBookIsbnAndQuantityGreaterThan(book.getIsbn(), 0L))
                .thenReturn(Collections.emptyList());

        MarketplaceSummary summary = marketplaceService.getMarketplaceSummary(book.getIsbn());

        assertThat(summary).isNotNull();
        assertThat(summary.getBook().getTitle()).isEqualTo(book.getTitle());
        assertThat(summary.isInStock()).isFalse();
        assertThat(summary.getTotalQuantityAvailable()).isZero();
        assertThat(summary.getLowestPrice()).isNull();
    }

    @Test
    void testGetMarketplaceSummary_WithStock() {
        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(inventoryRepository.findByBookIsbnAndQuantityGreaterThan(book.getIsbn(), 0L))
                .thenReturn(Arrays.asList(inventory1, inventory2));

        MarketplaceSummary summary = marketplaceService.getMarketplaceSummary(book.getIsbn());

        assertThat(summary.isInStock()).isTrue();
        assertThat(summary.getSellerCount()).isEqualTo(2);
        assertThat(summary.getTotalQuantityAvailable()).isEqualTo(8L);
        assertThat(summary.getLowestPrice()).isEqualTo(new BigDecimal("8.50"));
        
        assertThat(summary.getBook().getImageUrl()).isEqualTo("http://image.url");
        assertThat(summary.getBook().getAuthors()).hasSize(1);
        assertThat(summary.getBook().getAuthors().get(0).getImageUrl()).isEqualTo("http://image.url");
    }
    
    @Test
    void testGetMarketplaceSummary_MappingWithNullImages() {
        book.setImage(null);
        book.getAuthors().iterator().next().setImage(null);
        
        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(inventoryRepository.findByBookIsbnAndQuantityGreaterThan(book.getIsbn(), 0L))
                .thenReturn(Collections.emptyList());

        MarketplaceSummary summary = marketplaceService.getMarketplaceSummary(book.getIsbn());

        assertThat(summary.getBook().getImageUrl()).isNull();
        assertThat(summary.getBook().getAuthors().get(0).getImageUrl()).isNull();
    }

    @Test
    void testGetSellerListings() {
        when(inventoryRepository.findByBookIsbnAndQuantityGreaterThan(book.getIsbn(), 0L))
                .thenReturn(Arrays.asList(inventory1, inventory2));

        List<SellerListing> listings = marketplaceService.getSellerListings(book.getIsbn());

        assertThat(listings).hasSize(2);
        assertThat(listings.get(0).getPricePerUnit()).isEqualTo(new BigDecimal("8.50"));
        assertThat(listings.get(1).getPricePerUnit()).isEqualTo(new BigDecimal("10.00"));
        
        assertThat(listings.get(0).getSellerFirstName()).isEqualTo("Seller");
    }

    @Test
    void testGetAllBooksWithMarketplaceData_InStockOnly_True() {
        Book book2 = new Book();
        book2.setIsbn("EMPTY");
        book2.setTitle("Empty Book");
        book2.setAuthors(new HashSet<>());

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book, book2));
        
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory1, inventory2));

        List<MarketplaceBookListing> results = marketplaceService.getAllBooksWithMarketplaceData(true);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIsbn()).isEqualTo(book.getIsbn());
        assertThat(results.get(0).getTotalQuantityAvailable()).isEqualTo(8L);
    }

    @Test
    void testGetAllBooksWithMarketplaceData_InStockOnly_False() {
        Book book2 = new Book();
        book2.setIsbn("EMPTY");
        book2.setTitle("Empty Book");
        book2.setAuthors(new HashSet<>());

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book, book2));
        
        UserBookInventory zeroStockInv = new UserBookInventory();
        UserBookInventoryId zeroId = new UserBookInventoryId(123L, "EMPTY");
        zeroStockInv.setId(zeroId);
        zeroStockInv.setQuantity(0L);
        zeroStockInv.setBook(book2);

        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory1, inventory2, zeroStockInv));

        List<MarketplaceBookListing> results = marketplaceService.getAllBooksWithMarketplaceData(false);

        assertThat(results).hasSize(2);

        MarketplaceBookListing listing1 = results.stream()
                .filter(l -> l.getIsbn().equals(book.getIsbn())).findFirst().orElseThrow();
        assertThat(listing1.isInStock()).isTrue();
        assertThat(listing1.getLowestPrice()).isEqualTo(new BigDecimal("8.50"));

        MarketplaceBookListing listing2 = results.stream()
                .filter(l -> l.getIsbn().equals("EMPTY")).findFirst().orElseThrow();
        assertThat(listing2.isInStock()).isFalse();
        assertThat(listing2.getLowestPrice()).isNull();
        assertThat(listing2.getTotalQuantityAvailable()).isZero();
    }
}