import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import useBookCollections from "./useBookCollections";
import authService from "../../../api/services/authService";
import bookService from "../../../api/services/bookService";

export function useBookCollectionDetail(collectionId) {
    const navigate = useNavigate();
    const [userEmail, setUserEmail] = useState("");
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);

    const {
        currentCollection,
        error,
        fetchABookCollection,
        deleteBookCollection,
        shareCollection,
        unshareCollection,
    } = useBookCollections(false);

    useEffect(() => {
        loadData();
    }, [collectionId]);

    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            const collection = await fetchABookCollection({ collectionId });

            if (collection?.books) {
                const formattedBooks = collection.books.map((book) =>
                    bookService.formatBookForDisplay(book)
                );
                setBooks(formattedBooks);
            }
        } catch (err) {
            console.error("Error loading collection:", err);
        } finally {
            setLoading(false);
        }
    }, [collectionId, fetchABookCollection]);

    const handleDelete = useCallback(() => {
        deleteBookCollection({ collectionId });
        navigate(`/profile`);
    }, [collectionId, deleteBookCollection, navigate]);

    const handleShare = useCallback(async () => {
        if (!userEmail) return;
        await shareCollection(collectionId, userEmail);
        setUserEmail("");
    }, [collectionId, userEmail, shareCollection]);

    const handleUnshare = useCallback(async (userId) => {
        await unshareCollection(collectionId, userId);
    }, [collectionId, unshareCollection]);

    const currentUser = authService.getCurrentUser();
    const isOwner = currentCollection?.owner?.id === currentUser?.id;

    return {
        currentCollection,
        books,
        loading,
        error,
        userEmail,
        setUserEmail,
        isOwner,
        handleDelete,
        handleShare,
        handleUnshare,
    };
}
