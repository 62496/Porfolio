import { useState, useEffect, useCallback, useMemo } from 'react';
import marketplaceService from '../../../api/services/marketplaceService';

export function useMarketplace() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filter states
    const [search, setSearch] = useState('');
    const [sortBy, setSortBy] = useState('title');
    const [filterInStock, setFilterInStock] = useState(false);

    // Selected book for detail view
    const [selectedBook, setSelectedBook] = useState(null);
    const [selectedBookSellers, setSelectedBookSellers] = useState([]);
    const [loadingSellers, setLoadingSellers] = useState(false);

    useEffect(() => {
        loadBooks();
    }, []);

    const loadBooks = async () => {
        try {
            setLoading(true);
            setError(null);

            // Get all books with marketplace data in ONE call
            const listings = await marketplaceService.getAllBooks();
            const formattedBooks = listings.map(listing =>
                marketplaceService.formatListingForDisplay(listing)
            );
            setBooks(formattedBooks);
        } catch (err) {
            console.error('Error loading marketplace:', err);
            setError('Failed to load marketplace data');
        } finally {
            setLoading(false);
        }
    };

    const loadBookSellers = useCallback(async (isbn) => {
        try {
            setLoadingSellers(true);
            const sellers = await marketplaceService.getBookSellers(isbn);
            const formattedSellers = sellers.map(seller =>
                marketplaceService.formatSellerForDisplay(seller)
            );
            setSelectedBookSellers(formattedSellers);
        } catch (err) {
            console.error('Error loading sellers:', err);
            setSelectedBookSellers([]);
        } finally {
            setLoadingSellers(false);
        }
    }, []);

    const selectBook = useCallback((book) => {
        setSelectedBook(book);
        if (book) {
            loadBookSellers(book.isbn);
        } else {
            setSelectedBookSellers([]);
        }
    }, [loadBookSellers]);

    const closeBookDetail = useCallback(() => {
        setSelectedBook(null);
        setSelectedBookSellers([]);
    }, []);

    // Filtered and sorted books
    const filteredBooks = useMemo(() => {
        let result = books;

        // Search filter
        if (search) {
            const searchLower = search.toLowerCase();
            result = result.filter(book =>
                book.title?.toLowerCase().includes(searchLower) ||
                book.author?.toLowerCase().includes(searchLower) ||
                book.isbn?.toLowerCase().includes(searchLower)
            );
        }

        // In stock filter
        if (filterInStock) {
            result = result.filter(book => book.marketplace.inStock);
        }

        // Sort
        result = [...result].sort((a, b) => {
            switch (sortBy) {
                case 'title':
                    return (a.title || '').localeCompare(b.title || '');
                case 'price-low':
                    if (!a.marketplace.lowestPrice) return 1;
                    if (!b.marketplace.lowestPrice) return -1;
                    return a.marketplace.lowestPrice - b.marketplace.lowestPrice;
                case 'price-high':
                    if (!a.marketplace.lowestPrice) return 1;
                    if (!b.marketplace.lowestPrice) return -1;
                    return b.marketplace.lowestPrice - a.marketplace.lowestPrice;
                case 'sellers':
                    return b.marketplace.sellerCount - a.marketplace.sellerCount;
                default:
                    return 0;
            }
        });

        return result;
    }, [books, search, filterInStock, sortBy]);

    // Stats
    const stats = useMemo(() => {
        const inStockBooks = books.filter(b => b.marketplace.inStock);
        const totalListings = books.reduce((sum, b) => sum + b.marketplace.sellerCount, 0);

        return {
            totalBooks: books.length,
            booksInStock: inStockBooks.length,
            totalListings,
        };
    }, [books]);

    const clearFilters = useCallback(() => {
        setSearch('');
        setSortBy('title');
        setFilterInStock(false);
    }, []);

    const hasFilters = search || sortBy !== 'title' || filterInStock;

    return {
        // State
        books: filteredBooks,
        totalBooks: books.length,
        loading,
        error,
        stats,

        // Filters
        search,
        sortBy,
        filterInStock,
        hasFilters,
        setSearch,
        setSortBy,
        setFilterInStock,
        clearFilters,

        // Selected book
        selectedBook,
        selectedBookSellers,
        selectedBookMarketplace: selectedBook?.marketplace || null,
        loadingSellers,
        selectBook,
        closeBookDetail,

        // Actions
        refresh: loadBooks,
    };
}

export default useMarketplace;
