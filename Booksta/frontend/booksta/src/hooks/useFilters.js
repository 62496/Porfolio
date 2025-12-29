import { useState, useCallback } from 'react';

/**
 * Default filter state for books
 */
const defaultFilters = {
    title: '',
    yearMin: '',
    yearMax: '',
    pagesMin: '',
    pagesMax: '',
    authorIds: [],
    subjectIds: [],
};

/**
 * Reusable hook for managing filter state
 * Can be used across different pages that need filtering functionality
 *
 * @param {Object} initialFilters - Initial filter values (merged with defaults)
 * @returns {Object} Filter state and actions
 */
export const useFilters = (initialFilters = {}) => {
    const [filters, setFilters] = useState({
        ...defaultFilters,
        ...initialFilters,
    });
    const [appliedFilters, setAppliedFilters] = useState({
        ...defaultFilters,
        ...initialFilters,
    });

    /**
     * Update a single filter value
     */
    const updateFilter = useCallback((name, value) => {
        setFilters(prev => ({
            ...prev,
            [name]: value,
        }));
    }, []);

    /**
     * Update multiple filter values at once
     */
    const updateFilters = useCallback((updates) => {
        setFilters(prev => ({
            ...prev,
            ...updates,
        }));
    }, []);

    /**
     * Toggle an ID in an array filter (authorIds, subjectIds)
     */
    const toggleArrayFilter = useCallback((name, id) => {
        setFilters(prev => {
            const currentArray = prev[name] || [];
            const newArray = currentArray.includes(id)
                ? currentArray.filter(item => item !== id)
                : [...currentArray, id];
            return {
                ...prev,
                [name]: newArray,
            };
        });
    }, []);

    /**
     * Apply current filters (copy filters to appliedFilters)
     */
    const applyFilters = useCallback(() => {
        setAppliedFilters({ ...filters });
    }, [filters]);

    /**
     * Reset all filters to defaults
     */
    const resetFilters = useCallback(() => {
        setFilters({ ...defaultFilters });
        setAppliedFilters({ ...defaultFilters });
    }, []);

    /**
     * Check if any filters are currently set
     */
    const hasActiveFilters = useCallback(() => {
        return (
            filters.title !== '' ||
            filters.yearMin !== '' ||
            filters.yearMax !== '' ||
            filters.pagesMin !== '' ||
            filters.pagesMax !== '' ||
            filters.authorIds.length > 0 ||
            filters.subjectIds.length > 0
        );
    }, [filters]);

    /**
     * Get filters formatted for API call (excludes empty values)
     */
    const getApiFilters = useCallback(() => {
        const apiFilters = {};

        if (appliedFilters.title) apiFilters.title = appliedFilters.title;
        if (appliedFilters.yearMin) apiFilters.yearMin = parseInt(appliedFilters.yearMin, 10);
        if (appliedFilters.yearMax) apiFilters.yearMax = parseInt(appliedFilters.yearMax, 10);
        if (appliedFilters.pagesMin) apiFilters.pagesMin = parseInt(appliedFilters.pagesMin, 10);
        if (appliedFilters.pagesMax) apiFilters.pagesMax = parseInt(appliedFilters.pagesMax, 10);
        if (appliedFilters.authorIds?.length) apiFilters.authorIds = appliedFilters.authorIds;
        if (appliedFilters.subjectIds?.length) apiFilters.subjectIds = appliedFilters.subjectIds;

        return apiFilters;
    }, [appliedFilters]);

    /**
     * Count the number of active applied filters
     */
    const getActiveFilterCount = useCallback(() => {
        let count = 0;
        if (appliedFilters.title) count++;
        if (appliedFilters.yearMin || appliedFilters.yearMax) count++;
        if (appliedFilters.pagesMin || appliedFilters.pagesMax) count++;
        if (appliedFilters.authorIds?.length) count++;
        if (appliedFilters.subjectIds?.length) count++;
        return count;
    }, [appliedFilters]);

    return {
        // State
        filters,
        appliedFilters,

        // Actions
        updateFilter,
        updateFilters,
        toggleArrayFilter,
        applyFilters,
        resetFilters,

        // Helpers
        hasActiveFilters,
        getApiFilters,
        getActiveFilterCount,
    };
};

export default useFilters;
