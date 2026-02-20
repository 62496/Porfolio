import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import bookService from "../../../api/services/bookService";
import userService from "../../../api/services/userService";
import useToast from "../../../hooks/useToast";

export function useBookCatalog() {
    const navigate = useNavigate();
    const { toast, showToast, hideToast } = useToast();
    const [books, setBooks] = useState([]);
    const [favorites, setFavorites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState("");
    const [genre, setGenre] = useState("");
    const [year, setYear] = useState("");
    const [genres, setGenres] = useState([]);
    const [years, setYears] = useState([]);

    const currentUser = userService.getCurrentUser();

    const hasAuthorRole = useCallback(() => {
        if (!currentUser || !currentUser.roles) return false;
        return currentUser.roles.some(role => role.name === "AUTHOR");
    }, [currentUser]);

    const hasLibrarianRole = useCallback(() => {
        if (!currentUser || !currentUser.roles) return false;
        return currentUser.roles.some(role => role.name === "LIBRARIAN");
    }, [currentUser]);

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

            const booksData = await bookService.getAllBooks();
            const formattedBooks = booksData.map(book => bookService.formatBookForDisplay(book));
            setBooks(formattedBooks);

            const uniqueGenres = [...new Set(formattedBooks.flatMap(b =>
                b.subjects?.map(s => s.name) || []
            ))];
            setGenres(uniqueGenres.sort());

            const uniqueYears = [...new Set(formattedBooks.map(b => b.year))].sort((a, b) => b - a);
            setYears(uniqueYears);

            if (currentUser?.id) {
                const favoritesData = await userService.getFavorites();
                const formattedFavorites = favoritesData.map(book => bookService.formatBookForDisplay(book));
                setFavorites(formattedFavorites);
            }

        } catch {
            showToast('Error loading data', 'error');
        } finally {
            setLoading(false);
        }
    }, [currentUser?.id]);

    const handleToggleFavorite = useCallback(async (book) => {
        if (!currentUser?.id) {
            showToast('You must be logged in to add favorites', 'warning');
            navigate('/login');
            return;
        }

        try {
            const isFav = favorites.some(f => f.isbn === book.isbn);

            if (isFav) {
                await userService.removeFavorite(book.isbn);
                setFavorites(prev => prev.filter(f => f.isbn !== book.isbn));
            } else {
                await userService.addFavorite(book.isbn);
                setFavorites(prev => [...prev, book]);
            }
        } catch {
            showToast('Error updating favorites', 'error');
        }
    }, [currentUser?.id, favorites, navigate, showToast]);

    const filteredBooks = books.filter((b) => {
        const matchesSearch =
            search === "" ||
            b.title.toLowerCase().includes(search.toLowerCase()) ||
            b.author.toLowerCase().includes(search.toLowerCase());

        const matchesGenre =
            genre === "" ||
            b.subjects?.some(s => s.name === genre);

        const matchesYear =
            year === "" ||
            b.year === parseInt(year);

        return matchesSearch && matchesGenre && matchesYear;
    });

    const isFavorite = useCallback((book) => {
        return favorites.some((b) => b.isbn === book.isbn);
    }, [favorites]);

    return {
        // State
        books,
        favorites,
        loading,
        search,
        genre,
        year,
        genres,
        years,
        filteredBooks,
        currentUser,
        toast,

        // Setters
        setSearch,
        setGenre,
        setYear,

        // Actions
        handleToggleFavorite,
        isFavorite,
        hasAuthorRole,
        hasLibrarianRole,
        hideToast,
    };
}
