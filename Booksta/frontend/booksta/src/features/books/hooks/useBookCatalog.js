import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import bookService from "../../../api/services/bookService";
import userService from "../../../api/services/userService";
import authorService from "../../../api/services/authorService";
import subjectService from "../../../api/services/subjectService";
import useToast from "../../../hooks/useToast";
import useFilters from "../../../hooks/useFilters";

export function useBookCatalog() {
    const navigate = useNavigate();
    const { toast, showToast, hideToast } = useToast();
    const [books, setBooks] = useState([]);
    const [favorites, setFavorites] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filterLoading, setFilterLoading] = useState(false);
    const [search, setSearch] = useState("");
    const [genre, setGenre] = useState("");
    const [year, setYear] = useState("");
    const [genres, setGenres] = useState([]);
    const [years, setYears] = useState([]);
    const [authors, setAuthors] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [isFiltered, setIsFiltered] = useState(false);

    const filterHook = useFilters();

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

            const [booksData, authorsData, subjectsData] = await Promise.all([
                bookService.getAllBooks(),
                authorService.getAll(),
                subjectService.getAll(),
            ]);

            const formattedBooks = booksData.map(book => bookService.formatBookForDisplay(book));
            setBooks(formattedBooks);
            setAuthors(authorsData || []);
            setSubjects(subjectsData || []);

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

    const applyApiFilters = useCallback(async () => {
        // First, apply the current filters to appliedFilters
        filterHook.applyFilters();

        // Get the API filters from the current filters (not appliedFilters, since setState is async)
        const apiFilters = {};
        if (filterHook.filters.title) apiFilters.title = filterHook.filters.title;
        if (filterHook.filters.yearMin) apiFilters.yearMin = parseInt(filterHook.filters.yearMin, 10);
        if (filterHook.filters.yearMax) apiFilters.yearMax = parseInt(filterHook.filters.yearMax, 10);
        if (filterHook.filters.pagesMin) apiFilters.pagesMin = parseInt(filterHook.filters.pagesMin, 10);
        if (filterHook.filters.pagesMax) apiFilters.pagesMax = parseInt(filterHook.filters.pagesMax, 10);
        if (filterHook.filters.authorIds?.length) apiFilters.authorIds = filterHook.filters.authorIds;
        if (filterHook.filters.subjectIds?.length) apiFilters.subjectIds = filterHook.filters.subjectIds;

        // If no filters are set, reload all books
        if (Object.keys(apiFilters).length === 0) {
            try {
                setFilterLoading(true);
                const booksData = await bookService.getAllBooks();
                const formattedBooks = booksData.map(book => bookService.formatBookForDisplay(book));
                setBooks(formattedBooks);
                setIsFiltered(false);
            } catch {
                showToast('Error loading books', 'error');
            } finally {
                setFilterLoading(false);
            }
            return;
        }

        try {
            setFilterLoading(true);
            const filteredData = await bookService.filterBooks(apiFilters);
            const formattedBooks = filteredData.map(book => bookService.formatBookForDisplay(book));
            setBooks(formattedBooks);
            setIsFiltered(true);
            showToast(`Found ${formattedBooks.length} books`, 'success');
        } catch {
            showToast('Error applying filters', 'error');
        } finally {
            setFilterLoading(false);
        }
    }, [filterHook, showToast]);

    const resetApiFilters = useCallback(async () => {
        filterHook.resetFilters();
        try {
            setFilterLoading(true);
            const booksData = await bookService.getAllBooks();
            const formattedBooks = booksData.map(book => bookService.formatBookForDisplay(book));
            setBooks(formattedBooks);
            setIsFiltered(false);
        } catch {
            showToast('Error loading books', 'error');
        } finally {
            setFilterLoading(false);
        }
    }, [filterHook, showToast]);

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
        filterLoading,
        search,
        genre,
        year,
        genres,
        years,
        authors,
        subjects,
        filteredBooks,
        currentUser,
        toast,
        isFiltered,

        // Filter hook
        filters: filterHook.filters,
        appliedFilters: filterHook.appliedFilters,
        updateFilter: filterHook.updateFilter,
        toggleArrayFilter: filterHook.toggleArrayFilter,
        hasActiveFilters: filterHook.hasActiveFilters,
        getActiveFilterCount: filterHook.getActiveFilterCount,

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
        applyApiFilters,
        resetApiFilters,
    };
}
