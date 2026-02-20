import { useState, useEffect, useCallback } from "react";
import bookService from "../../../api/services/bookService";
import inventoryService from "../../../api/services/inventoryService";

export function useInventory() {
    const [inventoryItems, setInventoryItems] = useState([]);
    const [allBooks, setAllBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [toast, setToast] = useState(null);

    // Filter states
    const [search, setSearch] = useState("");
    const [sortBy, setSortBy] = useState("title");

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [inventoryData, booksData] = await Promise.all([
                inventoryService.getAll(),
                bookService.getAllBooks(),
            ]);

            // Format all books first
            const formattedBooks = booksData.map(book => bookService.formatBookForDisplay(book));

            // Create a map for quick lookup by ISBN
            const booksByIsbn = {};
            formattedBooks.forEach(book => {
                booksByIsbn[book.isbn] = book;
            });

            // Format inventory items with book details
            // The API might return bookIsbn (string) or book (object)
            const formattedInventory = (inventoryData || []).map((item) => {
                // Get the ISBN - could be item.bookIsbn or item.book.isbn or item.book (if it's a string)
                const isbn = item.bookIsbn || item.book?.isbn || (typeof item.book === 'string' ? item.book : null);

                // Look up full book details from our formatted books
                const bookDetails = isbn ? booksByIsbn[isbn] : null;

                // If we have a book object from API, merge with formatted details
                const bookFromApi = item.book && typeof item.book === 'object' ? item.book : null;

                return {
                    ...item,
                    book: bookDetails || (bookFromApi ? bookService.formatBookForDisplay(bookFromApi) : null),
                };
            });

            setInventoryItems(formattedInventory);
            setAllBooks(formattedBooks);
        } catch (err) {
            console.error("Error loading inventory:", err);
            setError("Unable to load inventory data.");
        } finally {
            setLoading(false);
        }
    };

    // Filter and sort inventory items
    const filteredInventory = inventoryItems
        .filter((item) => {
            if (!search) return true;
            const searchLower = search.toLowerCase();
            return (
                item.book?.title?.toLowerCase().includes(searchLower) ||
                item.book?.author?.toLowerCase().includes(searchLower) ||
                item.book?.isbn?.toLowerCase().includes(searchLower)
            );
        })
        .sort((a, b) => {
            switch (sortBy) {
                case "title":
                    return (a.book?.title || "").localeCompare(b.book?.title || "");
                case "price":
                    return (b.pricePerUnit || 0) - (a.pricePerUnit || 0);
                case "quantity":
                    return (b.quantity || 0) - (a.quantity || 0);
                case "recent":
                default:
                    return 0;
            }
        });

    // Get books that are NOT in inventory (for adding)
    const availableBooks = allBooks.filter(
        (book) => !inventoryItems.some((item) => item.book?.isbn === book.isbn)
    );

    const addToInventory = useCallback(async (bookIsbn, quantity, pricePerUnit) => {
        try {
            await inventoryService.create(bookIsbn, quantity, pricePerUnit);
            await loadData();
            setToast({ message: "Book added to inventory", type: "success" });
            return true;
        } catch (err) {
            console.error("Error adding to inventory:", err);
            if (err.response?.status === 409) {
                setToast({ message: "This book is already in your inventory", type: "error" });
            } else {
                setToast({ message: "Failed to add book to inventory", type: "error" });
            }
            return false;
        }
    }, []);

    const updateInventoryItem = useCallback(async (bookIsbn, quantity, pricePerUnit) => {
        try {
            await inventoryService.update(bookIsbn, quantity, pricePerUnit);
            setInventoryItems((prev) =>
                prev.map((item) =>
                    item.book?.isbn === bookIsbn
                        ? { ...item, quantity, pricePerUnit }
                        : item
                )
            );
            setToast({ message: "Inventory updated", type: "success" });
            return true;
        } catch (err) {
            console.error("Error updating inventory:", err);
            setToast({ message: "Failed to update inventory", type: "error" });
            return false;
        }
    }, []);

    const removeFromInventory = useCallback(async (bookIsbn) => {
        try {
            await inventoryService.delete(bookIsbn);
            setInventoryItems((prev) =>
                prev.filter((item) => item.book?.isbn !== bookIsbn)
            );
            setToast({ message: "Book removed from inventory", type: "success" });
            return true;
        } catch (err) {
            console.error("Error removing from inventory:", err);
            setToast({ message: "Failed to remove book from inventory", type: "error" });
            return false;
        }
    }, []);

    const hideToast = useCallback(() => setToast(null), []);

    const clearFilters = useCallback(() => {
        setSearch("");
        setSortBy("title");
    }, []);

    const hasFilters = search || sortBy !== "title";

    return {
        // State
        inventoryItems: filteredInventory,
        totalInventoryCount: inventoryItems.length,
        availableBooks,
        loading,
        error,
        toast,
        search,
        sortBy,
        hasFilters,

        // Setters
        setSearch,
        setSortBy,

        // Actions
        addToInventory,
        updateInventoryItem,
        removeFromInventory,
        hideToast,
        clearFilters,
        refresh: loadData,
    };
}
