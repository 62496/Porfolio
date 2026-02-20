import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import userService from "../../../api/services/userService";
import bookService from "../../../api/services/bookService";
import { useBooks } from "./useBooks";

export function useMyBooks() {
    const [ownedBooks, setOwnedBooks] = useState([]);
    const [favorites, setFavorites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [toast, setToast] = useState(null);
    const navigate = useNavigate();
    const currentUser = userService.getCurrentUser();
    const { createReadingEvent } = useBooks(false);

    useEffect(() => {
        if (!currentUser) {
            navigate('/login');
            return;
        }
        loadData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const loadData = useCallback(async () => {
        try {
            setLoading(true);

            const booksWithStatus = await userService.getOwnedBooksWithLatestReadingStatus();
            setOwnedBooks(booksWithStatus);

            if (currentUser?.id) {
                const favoritesData = await userService.getFavorites();
                const formattedFavorites = favoritesData.map(book => bookService.formatBookForDisplay(book));
                setFavorites(formattedFavorites);
            }
        } catch (error) {
            console.error('Error loading owned books:', error);
            setToast({ message: 'Error loading your books', type: 'error' });
        } finally {
            setLoading(false);
        }
    }, [currentUser?.id]);

    const handleToggleFavorite = useCallback(async (book) => {
        if (!currentUser?.id) {
            setToast({ message: 'You must be logged in to add favorites', type: 'error' });
            navigate('/login');
            return;
        }

        try {
            const isFav = favorites.some(f => f.isbn === book.isbn);

            if (isFav) {
                await userService.removeFavorite(book.isbn);
                setFavorites(prev => prev.filter(f => f.isbn !== book.isbn));
                setToast({ message: 'Removed from favorites', type: 'success' });
            } else {
                await userService.addFavorite(book.isbn);
                setFavorites(prev => [...prev, book]);
                setToast({ message: 'Added to favorites', type: 'success' });
            }
        } catch (error) {
            console.error('Error toggling favorite:', error);
            setToast({ message: 'Error updating favorites', type: 'error' });
        }
    }, [currentUser?.id, favorites, navigate]);

    const handleStartReading = useCallback(async (book) => {
        try {
            await createReadingEvent(book.isbn, "STARTED_READING");
            setToast({ message: 'Started reading!', type: 'success' });

            // Update the book in the local state immediately
            setOwnedBooks(prev => prev.map(b =>
                b.isbn === book.isbn
                    ? { ...b, latestReadingEvent: { eventType: 'STARTED_READING', occurredAt: new Date().toISOString() } }
                    : b
            ));
        } catch (error) {
            console.error('Error starting reading:', error);
            setToast({ message: 'Error starting reading event', type: 'error' });
        }
    }, [createReadingEvent]);

    const isFavorite = useCallback((book) => {
        return favorites.some((b) => b.isbn === book.isbn);
    }, [favorites]);

    const handleAddToCollection = useCallback((book, collection) => {
        setToast({ message: `Added "${book.title}" to ${collection.name}`, type: 'success' });
    }, []);

    const hideToast = useCallback(() => setToast(null), []);

    return {
        // State
        ownedBooks,
        favorites,
        loading,
        toast,

        // Actions
        handleToggleFavorite,
        handleStartReading,
        handleAddToCollection,
        isFavorite,
        hideToast,
    };
}
