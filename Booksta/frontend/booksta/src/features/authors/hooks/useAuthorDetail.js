import { useState, useEffect, useCallback, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import authorService from "../../../api/services/authorService";
import userService from "../../../api/services/userService";
import authService from "../../../api/services/authService";

export function useAuthorDetail(authorId) {
    const navigate = useNavigate();
    const [author, setAuthor] = useState(null);
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

    const fetchAuthor = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await authorService.getById(authorId);
            setAuthor(data);
        } catch (err) {
            console.error("Error fetching author:", err);
            setError("Failed to load author details");
        } finally {
            setLoading(false);
        }
    }, [authorId]);

    const checkFollowStatus = useCallback(async () => {
        try {
            const res = await userService.isFollowingAuthor(authorId);
            setIsFollowing(Boolean(res));
        } catch (e) {
            console.warn("Could not get follow status", e);
        }
    }, [authorId]);

    useEffect(() => {
        let mounted = true;

        const init = async () => {
            if (mounted) {
                await fetchAuthor();
                await checkFollowStatus();
            }
        };

        init();

        return () => {
            mounted = false;
        };
    }, [fetchAuthor, checkFollowStatus]);

    const handleBackToAuthors = useCallback(() => {
        navigate(-1);
    }, [navigate]);

    const handleBookClick = useCallback((isbn) => {
        navigate(`/book/${isbn}`);
    }, [navigate]);

    const handleSeriesClick = useCallback((seriesId) => {
        navigate(`/series/${seriesId}`);
    }, [navigate]);

    const openDeleteModal = useCallback(() => setIsDeleteModalOpen(true), []);
    const closeDeleteModal = useCallback(() => setIsDeleteModalOpen(false), []);

    const handleDeleteAuthor = useCallback(async () => {
        if (!author) return;
        setDeleteLoading(true);
        try {
            await authorService.delete(author.id);
            navigate('/authors');
        } catch (err) {
            console.error('Error deleting author:', err);
            setError('Failed to delete author');
        } finally {
            setDeleteLoading(false);
            setIsDeleteModalOpen(false);
        }
    }, [author, navigate]);

    return {
        // State
        author,
        loading,
        error,
        isFollowing,
        isDeleteModalOpen,
        deleteLoading,
        currentUser,

        // Permissions
        hasLibrarianRole,

        // Actions
        setIsFollowing,
        handleBackToAuthors,
        handleBookClick,
        handleSeriesClick,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteAuthor,
    };
}

export default useAuthorDetail;
