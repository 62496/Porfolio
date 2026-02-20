import { useState, useEffect, useCallback } from 'react';
import reportService from '../../../api/services/reportService';

/**
 * Custom hook for author operations
 * Handles loading states, errors, and caching
 */
export const useReports = (autoFetch = true) => {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /**
     * Creates a book report
     */
    const createBookReport = useCallback(async (bookId, info = {}) => {
        setLoading(true);
        setError(null);

        try {
            const data = await reportService.createBookReport(bookId, info);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create book report");
            console.error("Error creating book report", err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [])

    const getAllReports = useCallback(async () => {
        setLoading(true)
        setError(null)

        try {
            const data = await reportService.getAll();
            setReports(data);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || "Failed to fetch all reports");
            console.error("Error fetching all reports", err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    const resolveBookReport = useCallback(async (reportId, payload) => {
        setLoading(true);
        setError(null);

        try {
            const data = await reportService.resolveBookReport(reportId, payload);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || "Failed to resolve book report");
            console.error("Error resolving book report reports", err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    const dismissBookReport = useCallback(async (reportId) => {
        setLoading(true);
        setError(null);

        try {
            const data = await reportService.dismissBookReport(reportId);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || "Failed to dismiss book report");
            console.error("Error dismissing book report reports", err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, [])

    return {
        reports,
        loading,
        error,
        createBookReport,
        getAllReports,
        resolveBookReport,
        dismissBookReport
    };
};

export default useReports;