import { useState, useCallback } from 'react';
import userService from '../../../api/services/userService';

/**
 * Custom hook for managing user followings (authors and series)
 */
export const useFollowings = () => {
    const [authors, setAuthors] = useState([]);
    const [series, setSeries] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /**
     * Fetch followed authors and series for a user
     */
    const fetchFollowings = useCallback(async (userId) => {
        if (!userId) {
            setError('User ID is required');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const [authorsData, seriesData] = await Promise.all([
                userService.getFollowedAuthors(userId),
                userService.getFollowedSeries(userId)
            ]);

            // Deduplicate by id
            const uniqueAuthors = deduplicateById(authorsData);
            const uniqueSeries = deduplicateById(seriesData);

            setAuthors(uniqueAuthors);
            setSeries(uniqueSeries);

            return { authors: uniqueAuthors, series: uniqueSeries };
        } catch (err) {
            const errorMsg = err.response?.data?.message || 'Failed to fetch followings';
            setError(errorMsg);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Unfollow an author
     */
    const unfollowAuthor = useCallback(async (authorId) => {
        await userService.unfollowAuthor(authorId);
        setAuthors(prev => prev.filter(a => String(a.id) !== String(authorId)));
        return true;
    }, []);

    /**
     * Unfollow a series
     */
    const unfollowSeries = useCallback(async (seriesId) => {
        await userService.unfollowSeries(seriesId);
        setSeries(prev => prev.filter(s => String(s.id) !== String(seriesId)));
        return true;
    }, []);

    /**
     * Follow an author
     */
    const followAuthor = useCallback(async (authorId) => {
        const result = await userService.followAuthor(authorId);
        return result;
    }, []);

    /**
     * Follow a series
     */
    const followSeries = useCallback(async (seriesId) => {
        const result = await userService.followSeries(seriesId);
        return result;
    }, []);

    return {
        authors,
        series,
        loading,
        error,
        fetchFollowings,
        unfollowAuthor,
        unfollowSeries,
        followAuthor,
        followSeries,
        setAuthors,
        setSeries
    };
};

/**
 * Utility: deduplicate array of objects by id
 */
const deduplicateById = (arr = []) => {
    if (!Array.isArray(arr)) {
        arr = Array.from(arr || []);
    }

    const map = new Map();
    for (const item of arr) {
        const key = item?.id ?? item?.isbn ?? JSON.stringify(item);
        if (key != null && !map.has(String(key))) {
            map.set(String(key), item);
        }
    }
    return Array.from(map.values());
};

export default useFollowings;
