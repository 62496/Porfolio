import { useState, useEffect, useCallback } from 'react';
import subjectService from '../../../api/services/subjectService';

/**
 * Custom hook for subjects operations
 * Handles loading states, errors, and caching
 */
export const useSubjects = (autoFetch = true) => {
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /**
     * Fetch all subjects
     */
    const fetchSubjects = useCallback(async (params = {}) => {
        setLoading(true);
        setError(null);

        try {
            const data = await subjectService.getAll(params);
            setSubjects(data);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch subjects');
            console.error('Error fetching subjects:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    return {
        subjects,
        loading,
        error,
        fetchSubjects,
    };
};

export default useSubjects;