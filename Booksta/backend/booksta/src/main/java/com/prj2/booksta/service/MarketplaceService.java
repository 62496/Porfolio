package com.prj2.booksta.service;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.UserBookInventory;
import com.prj2.booksta.model.dto.MarketplaceBookListing;
import com.prj2.booksta.model.dto.MarketplaceSummary;
import com.prj2.booksta.model.dto.SellerListing;
import com.prj2.booksta.repository.BookRepository;
import com.prj2.booksta.repository.UserBookInventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarketplaceService {

    @Autowired
    private UserBookInventoryRepository inventoryRepository;

    @Autowired
    private BookRepository bookRepository;

    private MarketplaceSummary.BookInfo toBookInfo(Book book) {
        MarketplaceSummary.BookInfo bookInfo = new MarketplaceSummary.BookInfo();
        bookInfo.setIsbn(book.getIsbn());
        bookInfo.setTitle(book.getTitle());
        bookInfo.setDescription(book.getDescription());
        bookInfo.setPublishingYear(book.getPublishingYear());
        bookInfo.setPages(book.getPages());
        bookInfo.setImageUrl(book.getImage() != null ? book.getImage().getUrl() : null);

        List<MarketplaceSummary.AuthorInfo> authors = book.getAuthors().stream()
                .map(author -> new MarketplaceSummary.AuthorInfo(
                        author.getId(),
                        author.getFirstName(),
                        author.getLastName(),
                        author.getImage() != null ? author.getImage().getUrl() : null
                ))
                .collect(Collectors.toList());
        bookInfo.setAuthors(authors);

        return bookInfo;
    }

    @Transactional(readOnly = true)
    public MarketplaceSummary getMarketplaceSummary(String bookIsbn) {
        Book book = bookRepository.findById(bookIsbn)
                .orElseThrow(() -> new EntityNotFoundException("Book not found: " + bookIsbn));

        // Get all sellers with stock > 0 for this book
        List<UserBookInventory> availableStock = inventoryRepository
                .findByBookIsbnAndQuantityGreaterThan(bookIsbn, 0L);

        MarketplaceSummary summary = new MarketplaceSummary();
        summary.setBook(toBookInfo(book));

        if (availableStock.isEmpty()) {
            summary.setLowestPrice(null);
            summary.setSellerCount(0);
            summary.setTotalQuantityAvailable(0);
            summary.setInStock(false);
        } else {
            // Find lowest price
            BigDecimal lowestPrice = availableStock.stream()
                    .map(UserBookInventory::getPricePerUnit)
                    .min(Comparator.naturalOrder())
                    .orElse(null);

            // Count total quantity available
            long totalQuantity = availableStock.stream()
                    .mapToLong(UserBookInventory::getQuantity)
                    .sum();

            summary.setLowestPrice(lowestPrice);
            summary.setSellerCount(availableStock.size());
            summary.setTotalQuantityAvailable(totalQuantity);
            summary.setInStock(true);
        }

        return summary;
    }

    @Transactional(readOnly = true)
    public List<SellerListing> getSellerListings(String bookIsbn) {
        List<UserBookInventory> availableStock = inventoryRepository
                .findByBookIsbnAndQuantityGreaterThan(bookIsbn, 0L);

        return availableStock.stream()
                .map(SellerListing::fromEntity)
                .sorted(Comparator.comparing(SellerListing::getPricePerUnit))
                .collect(Collectors.toList());
    }

    /**
     * Get all books with their marketplace data in a single call
     * @param inStockOnly if true, only return books that have at least one seller
     */
    @Transactional(readOnly = true)
    public List<MarketplaceBookListing> getAllBooksWithMarketplaceData(boolean inStockOnly) {
        List<Book> allBooks = bookRepository.findAll();

        // Get all inventory entries with quantity > 0 in one query
        List<UserBookInventory> allInventory = inventoryRepository.findAll().stream()
                .filter(inv -> inv.getQuantity() > 0)
                .collect(Collectors.toList());

        // Group inventory by book ISBN
        Map<String, List<UserBookInventory>> inventoryByIsbn = allInventory.stream()
                .collect(Collectors.groupingBy(inv -> inv.getBook().getIsbn()));

        List<MarketplaceBookListing> listings = new ArrayList<>();

        for (Book book : allBooks) {
            List<UserBookInventory> bookInventory = inventoryByIsbn.getOrDefault(book.getIsbn(), Collections.emptyList());

            // Skip books with no stock if inStockOnly is true
            if (inStockOnly && bookInventory.isEmpty()) {
                continue;
            }

            MarketplaceBookListing listing = new MarketplaceBookListing();
            listing.setIsbn(book.getIsbn());
            listing.setTitle(book.getTitle());
            listing.setDescription(book.getDescription());
            listing.setPublishingYear(book.getPublishingYear());
            listing.setPages(book.getPages());
            listing.setImageUrl(book.getImage() != null ? book.getImage().getUrl() : null);

            List<MarketplaceBookListing.AuthorInfo> authors = book.getAuthors().stream()
                    .map(author -> new MarketplaceBookListing.AuthorInfo(
                            author.getId(),
                            author.getFirstName(),
                            author.getLastName(),
                            author.getImage() != null ? author.getImage().getUrl() : null
                    ))
                    .collect(Collectors.toList());
            listing.setAuthors(authors);

            if (bookInventory.isEmpty()) {
                listing.setLowestPrice(null);
                listing.setSellerCount(0);
                listing.setTotalQuantityAvailable(0);
                listing.setInStock(false);
            } else {
                BigDecimal lowestPrice = bookInventory.stream()
                        .map(UserBookInventory::getPricePerUnit)
                        .min(Comparator.naturalOrder())
                        .orElse(null);

                long totalQuantity = bookInventory.stream()
                        .mapToLong(UserBookInventory::getQuantity)
                        .sum();

                listing.setLowestPrice(lowestPrice);
                listing.setSellerCount(bookInventory.size());
                listing.setTotalQuantityAvailable(totalQuantity);
                listing.setInStock(true);
            }

            listings.add(listing);
        }

        return listings;
    }
}
