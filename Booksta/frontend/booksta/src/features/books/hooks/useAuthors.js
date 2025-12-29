import { useState, useEffect, useCallback } from 'react';
import authorService from '../../../api/services/authorService';

/**
 * Custom hook for author operations
 * Handles loading states, errors, and caching
 */
export const useAuthors = (autoFetch = true) => {
    const [authors, setAuthors] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /**
     * Fetch all authors
     */
    const fetchAuthors = useCallback(async (params = {}) => {
        setLoading(true);
        setError(null);

        try {
            const data = await authorService.getAll(params);
            setAuthors(data);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch authors');
            console.error('Error fetching authors:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    const createAuthor = useCallback(async (formData = {}) => {
        setLoading(true);
        setError(null);

        try {
            const data = await authorService.create(formData);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create author");
            console.error("Error creating author:", err);
            throw err;
        } finally {
            setLoading(false);
        }
    })

    return {
        authors,
        loading,
        error,
        fetchAuthors,
        createAuthor
    };
};

export default useAuthors;