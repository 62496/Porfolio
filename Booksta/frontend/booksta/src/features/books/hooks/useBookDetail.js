import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import bookService from "../../../api/services/bookService";
import userService from "../../../api/services/userService";
import useToast from "../../../hooks/useToast";

export function useBookDetail(bookId) {
    const navigate = useNavigate();
    const { toast, showToast, hideToast } = useToast();
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isReportModalOpen, setIsReportModalOpen] = useState(false);
    const [isFavorite, setIsFavorite] = useState(false);
    const [isOwned, setIsOwned] = useState(false);
    const [showFullDescription, setShowFullDescription] = useState(false);
    const [isCollectionModalOpen, setIsCollectionModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const currentUser = userService.getCurrentUser();
    const DESCRIPTION_PREVIEW_LENGTH = 300;

    const openCollectionModal = useCallback(() => setIsCollectionModalOpen(true), []);
    const closeCollectionModal = useCallback(() => setIsCollectionModalOpen(false), []);

    useEffect(() => {
        if (bookId) {
            fetchBook();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [bookId, currentUser?.id]);

    const fetchBook = useCallback(async () => {
        try {
            setLoading(true);
            const data = await bookService.getById(bookId);
            const formattedBook = bookService.formatBookForDisplay(data);
            setBook(formattedBook);
            setError(null);

            // Check if book is in user's favorites and owned books
            if (currentUser?.id) {
                try {
                    const favorites = await userService.getFavorites();
                    const isBookFavorite = favorites.some(fav => fav.isbn === formattedBook.isbn);
                    setIsFavorite(isBookFavorite);

                    const ownedBooks = await userService.getOwnedBooks();
                    const isBookOwned = userService.isOwned(ownedBooks, formattedBook.isbn);
                    setIsOwned(isBookOwned);
                } catch {
                    // Error fetching favorites silently handled
                }
            }
        } catch {
            setError("Failed to load book details. Please try again later.");
        } finally {
            setLoading(false);
        }
    }, [bookId, currentUser?.id]);

    const handleToggleFavorite = useCallback(async () => {
        if (!currentUser?.id) {
            showToast('You must be logged in to add favorites', 'warning');
            navigate('/login');
            return;
        }

        if (!book) return;

        try {
            if (isFavorite) {
                await userService.removeFavorite(book.isbn);
                setIsFavorite(false);
            } else {
                await userService.addFavorite(book.isbn);
                setIsFavorite(true);
            }
        } catch {
            showToast('Error updating favorites', 'error');
        }
    }, [currentUser?.id, book, isFavorite, navigate, showToast]);

    const handleToggleOwned = useCallback(async () => {
        if (!currentUser?.id) {
            showToast('You must be logged in to manage your books', 'warning');
            navigate('/login');
            return;
        }

        if (!book) return;

        try {
            if (isOwned) {
                await userService.removeOwnedBook(book.isbn);
                setIsOwned(false);
            } else {
                await userService.addOwnedBook(book.isbn);
                setIsOwned(true);
            }
        } catch {
            showToast('Error updating your books', 'error');
        }
    }, [currentUser?.id, book, isOwned, navigate, showToast]);

    const handleBackToBooks = useCallback(() => {
        navigate(-1);
    }, [navigate]);

    const canEditBook = useCallback(() => {
        if (!currentUser || !book) return false;

        const hasLibrarianRole = currentUser.roles?.some(
            (role) => role.name === "LIBRARIAN"
        );
        const hasAuthorRole = currentUser.roles?.some(
            (role) => role.name === "AUTHOR"
        );
        const isBookAuthor = book.authors?.some(
            (author) => author.user?.id === currentUser.id
        );

        return hasLibrarianRole || (hasAuthorRole && isBookAuthor);
    }, [currentUser, book]);

    const canDeleteBook = useCallback(() => {
        if (!currentUser || !book) return false;

        const hasLibrarianRole = currentUser.roles?.some(
            (role) => role.name === "LIBRARIAN"
        );
        const hasAuthorRole = currentUser.roles?.some(
            (role) => role.name === "AUTHOR"
        );
        const isBookAuthor = book.authors?.some(
            (author) => author.user?.id === currentUser.id
        );

        // LIBRARIAN can delete any book, AUTHOR can delete their own books
        return hasLibrarianRole || (hasAuthorRole && isBookAuthor);
    }, [currentUser, book]);

    const openReportModal = useCallback(() => setIsReportModalOpen(true), []);
    const closeReportModal = useCallback(() => setIsReportModalOpen(false), []);
    const toggleDescription = useCallback(() => setShowFullDescription(prev => !prev), []);

    const openDeleteModal = useCallback(() => setIsDeleteModalOpen(true), []);
    const closeDeleteModal = useCallback(() => setIsDeleteModalOpen(false), []);

    const handleDeleteBook = useCallback(async () => {
        if (!book) return;
        setDeleteLoading(true);
        try {
            await bookService.delete(book.isbn);
            showToast('Book deleted successfully', 'success');
            navigate('/books');
        } catch (err) {
            console.error('Error deleting book:', err);
            showToast('Failed to delete book', 'error');
        } finally {
            setDeleteLoading(false);
            setIsDeleteModalOpen(false);
        }
    }, [book, navigate, showToast]);

    return {
        // State
        book,
        loading,
        error,
        isFavorite,
        isOwned,
        showFullDescription,
        isReportModalOpen,
        isCollectionModalOpen,
        isDeleteModalOpen,
        deleteLoading,
        currentUser,
        DESCRIPTION_PREVIEW_LENGTH,
        toast,

        // Actions
        handleToggleFavorite,
        handleToggleOwned,
        handleBackToBooks,
        canEditBook,
        canDeleteBook,
        openReportModal,
        closeReportModal,
        openCollectionModal,
        closeCollectionModal,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteBook,
        toggleDescription,
        hideToast,
    };
}
