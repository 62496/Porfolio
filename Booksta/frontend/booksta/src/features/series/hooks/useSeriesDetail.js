import { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import seriesService from "../../../api/services/seriesService";
import userService from "../../../api/services/userService";
import authService from "../../../api/services/authService";

export function useSeriesDetail(seriesId) {
    const navigate = useNavigate();
    const [series, setSeries] = useState(null);
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isFollowing, setIsFollowing] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const currentUser = useMemo(() => authService.getCurrentUser(), []);

    const hasLibrarianRole = useCallback(() => {
        if (!currentUser || !currentUser.roles) return false;
        return currentUser.roles.some(role => role.name === "LIBRARIAN");
    }, [currentUser]);

    const hasAuthorRole = useCallback(() => {
        if (!currentUser || !currentUser.roles) return false;
        return currentUser.roles.some(role => role.name === "AUTHOR");
    }, [currentUser]);

    const isSeriesOwner = useCallback(() => {
        if (!currentUser || !series) return false;
        // Check if the current user's authorId matches the series author
        return currentUser.authorId && series.author?.id === currentUser.authorId;
    }, [currentUser, series]);

    const canDeleteSeries = useCallback(() => {
        // LIBRARIAN can delete any series
        // AUTHOR can only delete their own series
        return hasLibrarianRole() || (hasAuthorRole() && isSeriesOwner());
    }, [hasLibrarianRole, hasAuthorRole, isSeriesOwner]);

    const canEditSeries = useCallback(() => {
        return hasLibrarianRole() || (hasAuthorRole() && isSeriesOwner());
    }, [hasLibrarianRole, hasAuthorRole, isSeriesOwner]);

    const fetchSeries = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await seriesService.getById(seriesId);
            setSeries(data);

            // Also fetch books in the series
            try {
                const booksData = await seriesService.getBooks(seriesId);
                setBooks(booksData || []);
            } catch (err) {
                console.warn("Could not fetch series books:", err);
                setBooks([]);
            }
        } catch (err) {
            console.error("Error fetching series:", err);
            setError("Failed to load series details");
        } finally {
            setLoading(false);
        }
    }, [seriesId]);

    const checkFollowStatus = useCallback(async () => {
        try {
            const res = await userService.isFollowingSeries(seriesId);
            setIsFollowing(Boolean(res));
        } catch (e) {
            console.warn("Could not get follow status", e);
        }
    }, [seriesId]);

    useEffect(() => {
        let mounted = true;

        const init = async () => {
            if (mounted) {
                await fetchSeries();
                await checkFollowStatus();
            }
        };

        init();

        return () => {
            mounted = false;
        };
    }, [fetchSeries, checkFollowStatus]);

    const handleBack = useCallback(() => {
        navigate(-1);
    }, [navigate]);

    const handleBookClick = useCallback((isbn) => {
        navigate(`/book/${isbn}`);
    }, [navigate]);

    const handleAuthorClick = useCallback((authorId) => {
        navigate(`/authors/${authorId}`);
    }, [navigate]);

    const openDeleteModal = useCallback(() => setIsDeleteModalOpen(true), []);
    const closeDeleteModal = useCallback(() => setIsDeleteModalOpen(false), []);

    const handleDeleteSeries = useCallback(async () => {
        if (!series) return;
        setDeleteLoading(true);
        try {
            await seriesService.delete(series.id);
            navigate('/series');
        } catch (err) {
            console.error('Error deleting series:', err);
            setError('Failed to delete series');
        } finally {
            setDeleteLoading(false);
            setIsDeleteModalOpen(false);
        }
    }, [series, navigate]);

    return {
        // State
        series,
        books,
        loading,
        error,
        isFollowing,
        isDeleteModalOpen,
        deleteLoading,
        currentUser,

        // Permissions
        hasLibrarianRole,
        hasAuthorRole,
        isSeriesOwner,
        canDeleteSeries,
        canEditSeries,

        // Actions
        setIsFollowing,
        handleBack,
        handleBookClick,
        handleAuthorClick,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteSeries,
        fetchSeries,
    };
}

export default useSeriesDetail;
