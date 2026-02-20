import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import userService from "../../../api/services/userService";
import bookService from "../../../api/services/bookService";
import useToast from "../../../hooks/useToast";

export function useFavorites() {
    const navigate = useNavigate();
    const { toast, showToast, hideToast } = useToast();
    const [favorites, setFavorites] = useState([]);
    const [loading, setLoading] = useState(true);

    const currentUser = userService.getCurrentUser();

    useEffect(() => {
        if (!currentUser) {
            navigate('/login');
            return;
        }
        loadFavorites();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const loadFavorites = useCallback(async () => {
        if (!currentUser?.id) return;

        try {
            setLoading(true);
            const favoritesData = await userService.getFavorites();
            const formattedFavorites = favoritesData.map(book =>
                bookService.formatBookForDisplay(book)
            );
            setFavorites(formattedFavorites);
        } catch {
            showToast('Error loading favorites', 'error');
        } finally {
            setLoading(false);
        }
    }, [currentUser?.id, showToast]);

    const handleToggleFavorite = useCallback(async (book) => {
        if (!currentUser?.id) {
            showToast('You must be logged in', 'warning');
            navigate('/login');
            return;
        }

        try {
            await userService.removeFavorite(book.isbn);
            setFavorites(prev => prev.filter(f => f.isbn !== book.isbn));
        } catch {
            showToast('Error removing favorite', 'error');
        }
    }, [currentUser?.id, navigate, showToast]);

    return {
        favorites,
        loading,
        handleToggleFavorite,
        toast,
        hideToast,
    };
}
