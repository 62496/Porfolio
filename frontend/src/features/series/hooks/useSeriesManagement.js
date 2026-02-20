import { useState, useEffect, useCallback, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import seriesService from '../../../api/services/seriesService';
import bookService from '../../../api/services/bookService';
import authService from '../../../api/services/authService';
import { seriesSchema } from '../validations/seriesSchema';

export const useSeriesManagement = () => {
    const currentUser = useMemo(() => authService.getCurrentUser(), []);

    // Series state
    const [series, setSeries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Selected series for modals
    const [selectedSeries, setSelectedSeries] = useState(null);
    // Books currently in the selected series (fetched separately via GET /series/{id}/books)
    const [seriesBooks, setSeriesBooks] = useState([]);

    // All books for adding to series
    const [allBooks, setAllBooks] = useState([]);
    const [bookSearchTerm, setBookSearchTerm] = useState('');

    // Create modal state
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [createLoading, setCreateLoading] = useState(false);

    // Edit modal state
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editLoading, setEditLoading] = useState(false);

    // Delete modal state
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);

    // Add books modal state
    const [isAddBooksModalOpen, setIsAddBooksModalOpen] = useState(false);
    const [addBooksLoading, setAddBooksLoading] = useState(false);

    // Forms
    const createForm = useForm({
        resolver: yupResolver(seriesSchema),
        mode: 'onChange',
        defaultValues: {
            title: '',
            description: '',
        },
    });

    const editForm = useForm({
        resolver: yupResolver(seriesSchema),
        mode: 'onChange',
        defaultValues: {
            title: '',
            description: '',
        },
    });

    // ========================
    // Data Fetching
    // ========================

    const fetchSeries = useCallback(async () => {
        const authorId = currentUser?.authorId;
        if (!authorId) {
            setLoading(false);
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const data = await seriesService.getByAuthor(authorId);
            setSeries(data || []);
        } catch (err) {
            setError('Failed to load series');
            console.error('Error fetching series:', err);
        } finally {
            setLoading(false);
        }
    }, [currentUser?.authorId]);

    const fetchBooks = useCallback(async () => {
        const authorId = currentUser?.authorId;
        if (!authorId) return;
        try {
            // Fetch only books by this author
            const data = await bookService.getByAuthor(authorId);
            setAllBooks(data || []);
        } catch (err) {
            console.error('Error fetching books:', err);
        }
    }, [currentUser?.authorId]);

    useEffect(() => {
        fetchSeries();
        fetchBooks();
    }, [fetchSeries, fetchBooks]);

    // ========================
    // Filtered Books
    // ========================

    const filteredBooks = useMemo(() => {
        if (!bookSearchTerm.trim()) return allBooks;
        const term = bookSearchTerm.toLowerCase();
        return allBooks.filter(book =>
            book.title?.toLowerCase().includes(term) ||
            book.isbn?.toLowerCase().includes(term)
        );
    }, [allBooks, bookSearchTerm]);

    const isBookInSeries = useCallback((isbn) => {
        if (!seriesBooks || seriesBooks.length === 0) return false;
        return seriesBooks.some(book => book.isbn === isbn);
    }, [seriesBooks]);

    // ========================
    // Create Series
    // ========================

    const openCreateModal = useCallback(() => {
        createForm.reset({
            title: '',
            description: '',
        });
        setIsCreateModalOpen(true);
    }, [createForm]);

    const closeCreateModal = useCallback(() => {
        setIsCreateModalOpen(false);
    }, []);

    const handleCreateSeries = useCallback(async (data) => {
        setCreateLoading(true);
        try {
            await seriesService.create(data);
            await fetchSeries();
            closeCreateModal();
        } catch (err) {
            setError('Failed to create series');
            console.error('Error creating series:', err);
        } finally {
            setCreateLoading(false);
        }
    }, [fetchSeries, closeCreateModal]);

    // ========================
    // Edit Series
    // ========================

    const openEditModal = useCallback((seriesItem) => {
        setSelectedSeries(seriesItem);
        editForm.reset({
            title: seriesItem.title || '',
            description: seriesItem.description || '',
        });
        setIsEditModalOpen(true);
    }, [editForm]);

    const closeEditModal = useCallback(() => {
        setIsEditModalOpen(false);
        setSelectedSeries(null);
    }, []);

    const handleEditSeries = useCallback(async (data) => {
        if (!selectedSeries) return;
        setEditLoading(true);
        try {
            await seriesService.update(selectedSeries.id, data);
            await fetchSeries();
            closeEditModal();
        } catch (err) {
            setError('Failed to update series');
            console.error('Error updating series:', err);
        } finally {
            setEditLoading(false);
        }
    }, [selectedSeries, fetchSeries, closeEditModal]);

    // ========================
    // Delete Series
    // ========================

    const openDeleteModal = useCallback((seriesItem) => {
        setSelectedSeries(seriesItem);
        setIsDeleteModalOpen(true);
    }, []);

    const closeDeleteModal = useCallback(() => {
        setIsDeleteModalOpen(false);
        setSelectedSeries(null);
    }, []);

    const handleDeleteSeries = useCallback(async () => {
        if (!selectedSeries) return;
        setDeleteLoading(true);
        try {
            await seriesService.delete(selectedSeries.id);
            await fetchSeries();
            closeDeleteModal();
        } catch (err) {
            setError('Failed to delete series');
            console.error('Error deleting series:', err);
        } finally {
            setDeleteLoading(false);
        }
    }, [selectedSeries, fetchSeries, closeDeleteModal]);

    // ========================
    // Add/Remove Books
    // ========================

    const openAddBooksModal = useCallback(async (seriesItem) => {
        setSelectedSeries(seriesItem);
        setBookSearchTerm('');
        setSeriesBooks([]);
        setIsAddBooksModalOpen(true);
        // Fetch books in this series via GET /series/{id}/books
        try {
            const books = await seriesService.getBooks(seriesItem.id);
            setSeriesBooks(books || []);
        } catch (err) {
            console.error('Error fetching series books:', err);
            setSeriesBooks([]);
        }
    }, []);

    const closeAddBooksModal = useCallback(() => {
        setIsAddBooksModalOpen(false);
        setSelectedSeries(null);
        setBookSearchTerm('');
        setSeriesBooks([]);
    }, []);

    const handleAddBook = useCallback(async (isbn) => {
        if (!selectedSeries) return;
        setAddBooksLoading(true);
        try {
            await seriesService.addBook(selectedSeries.id, isbn);
            // Refresh series books list
            const books = await seriesService.getBooks(selectedSeries.id);
            setSeriesBooks(books || []);
            await fetchSeries();
        } catch (err) {
            setError('Failed to add book to series');
            console.error('Error adding book:', err);
        } finally {
            setAddBooksLoading(false);
        }
    }, [selectedSeries, fetchSeries]);

    const handleRemoveBook = useCallback(async (isbn) => {
        if (!selectedSeries) return;
        setAddBooksLoading(true);
        try {
            await seriesService.removeBook(selectedSeries.id, isbn);
            // Refresh series books list
            const books = await seriesService.getBooks(selectedSeries.id);
            setSeriesBooks(books || []);
            await fetchSeries();
        } catch (err) {
            setError('Failed to remove book from series');
            console.error('Error removing book:', err);
        } finally {
            setAddBooksLoading(false);
        }
    }, [selectedSeries, fetchSeries]);

    return {
        // User
        currentUser,

        // Series data
        series,
        loading,
        error,

        // Selected series
        selectedSeries,

        // Create modal
        isCreateModalOpen,
        createForm,
        createLoading,
        openCreateModal,
        closeCreateModal,
        handleCreateSeries,

        // Edit modal
        isEditModalOpen,
        editForm,
        editLoading,
        openEditModal,
        closeEditModal,
        handleEditSeries,

        // Delete modal
        isDeleteModalOpen,
        deleteLoading,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteSeries,

        // Add books modal
        isAddBooksModalOpen,
        filteredBooks,
        seriesBooks,
        bookSearchTerm,
        setBookSearchTerm,
        addBooksLoading,
        openAddBooksModal,
        closeAddBooksModal,
        isBookInSeries,
        handleAddBook,
        handleRemoveBook,

        // Refresh
        fetchSeries,
    };
};

export default useSeriesManagement;
