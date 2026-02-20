import { useState, useEffect, useCallback } from 'react';
import bookService from '../../../api/services/bookService';

/**
 * Custom hook for book operations
 * Handles loading states, errors, and caching
 */

export const useBooks = (autoFetch = true) => {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    /**
     * Fetch all books
     */
    const fetchBooks = useCallback(async (params = {}) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.getAll(params);
            setBooks(data);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch books');
            console.error('Error fetching books:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Fetch a single book by ID
     */
    const fetchBook = useCallback(async (id) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.getById(id);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch book');
            console.error('Error fetching book:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Create a new book
     */
    const createBook = useCallback(async (bookData) => {
        setLoading(true);
        setError(null);

        try {
            const created = await bookService.create(bookData);
            setBooks(prev => [...prev, created]);
            return created;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to create book');
            console.error('Error creating book:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Update an existing book
     */
    const updateBook = useCallback(async (id, bookData) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.update(id, bookData);
            setBooks(prev => prev.map(book => book.id === id ? data : book));
            return data;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update book');
            console.error('Error updating book:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Delete a book
     */
    const deleteBook = useCallback(async (id) => {
        setLoading(true);
        setError(null);

        try {
            await bookService.delete(id);
            setBooks(prev => prev.filter(book => book.id !== id));
            return true;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to delete book');
            console.error('Error deleting book:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    /**
     * Search books
     */
    const searchBooks = useCallback(async (query, filters = {}) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.search(query, filters);
            setBooks(data);
            return data;
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to search books');
            console.error('Error searching books:', err);
            throw err;
        } finally {
            setLoading(false);
        }
    }, []);

    const createReadingEvent = useCallback(async (bookId, eventType) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.createBookReadingEvent(bookId, eventType);
            return data;
        } catch (error) {
            setError(error.response?.data?.message || "Failed to create reading event");
            console.error("Error creating reading event:", error);
            throw error;
        } finally {
            setLoading(false);
        }
    }, [])

    useEffect(() => {
        if (autoFetch) {
            fetchBooks();
        }
    }, [autoFetch, fetchBooks]);

    const getLatestBookReadingEvent = useCallback(async (isbn) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.getLatestBookReadingEvent(isbn);
            return data;
        } catch (error) {
            setError(error.response?.data?.message || "Failed to fetch latest reading event for this book");
            console.error("Error fetching latest reading event:", error);
            throw error;
        } finally {
            setLoading(false);
        }
    }, []);

    const getAllBookReadingSessions = useCallback(async (isbn) => {
        setLoading(true);
        setError(null);

        try {
            const data = await bookService.getAllBookReadingSessions(isbn);
            return data;
        } catch (error) {
            setError(error.response?.data?.message || "Failed to fetch reading sessions for this book");
            console.error("Error fetching reading sessions for this book", error);
            throw error;
        } finally {
            setLoading(false);
        }
    }, [])

    return {
        books,
        loading,
        error,
        fetchBooks,
        fetchBook,
        createBook,
        updateBook,
        deleteBook,
        searchBooks,
        createReadingEvent,
        getLatestBookReadingEvent,
        getAllBookReadingSessions
    };
};

export default useBooks;