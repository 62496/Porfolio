import { useState, useEffect, useCallback, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import seriesService from '../../../api/services/seriesService';
import bookService from '../../../api/services/bookService';
import authorService from '../../../api/services/authorService';
import authService from '../../../api/services/authService';
import { seriesSchema, librarianSeriesSchema } from '../validations/seriesSchema';

export const useSeriesManagement = () => {
    const currentUser = useMemo(() => authService.getCurrentUser(), []);

    // Role checks
    const isLibrarian = useMemo(() => {
        return currentUser?.roles?.some(role => role.name === 'LIBRARIAN') || false;
    }, [currentUser]);

    const isAuthor = useMemo(() => {
        return currentUser?.roles?.some(role => role.name === 'AUTHOR') || false;
    }, [currentUser]);

    // Series state
    const [series, setSeries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Authors list (for librarian to select author when creating series)
    const [authors, setAuthors] = useState([]);
    const [selectedAuthorId, setSelectedAuthorId] = useState(null);

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

    // Forms - use different schema for librarians
    const createForm = useForm({
        resolver: yupResolver(isLibrarian ? librarianSeriesSchema : seriesSchema),
        mode: 'onChange',
        defaultValues: {
            title: '',
            description: '',
            authorId: '',
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

    const fetchAuthors = useCallback(async () => {
        if (!isLibrarian) return;
        try {
            const data = await authorService.getAll();
            setAuthors(data || []);
        } catch (err) {
            console.error('Error fetching authors:', err);
        }
    }, [isLibrarian]);

    const fetchSeries = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            if (isLibrarian) {
                // Librarian sees all series
                const data = await seriesService.getAll();
                setSeries(data || []);
            } else if (isAuthor && currentUser?.authorId) {
                // Author sees only their own series
                const data = await seriesService.getByAuthor(currentUser.authorId);
                setSeries(data || []);
            } else {
                setSeries([]);
            }
        } catch (err) {
            setError('Failed to load series');
            console.error('Error fetching series:', err);
        } finally {
            setLoading(false);
        }
    }, [isLibrarian, isAuthor, currentUser?.authorId]);

    const fetchBooks = useCallback(async (authorId = null) => {
        try {
            if (isLibrarian) {
                // Librarian can see all books or filter by author
                if (authorId) {
                    const data = await bookService.getByAuthor(authorId);
                    setAllBooks(data || []);
                } else {
                    const data = await bookService.getAll();
                    setAllBooks(data || []);
                }
            } else if (isAuthor && currentUser?.authorId) {
                // Author sees only their own books
                const data = await bookService.getByAuthor(currentUser.authorId);
                setAllBooks(data || []);
            }
        } catch (err) {
            console.error('Error fetching books:', err);
        }
    }, [isLibrarian, isAuthor, currentUser?.authorId]);

    useEffect(() => {
        fetchSeries();
        fetchBooks();
        fetchAuthors();
    }, [fetchSeries, fetchBooks, fetchAuthors]);

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
            authorId: '',
        });
        setSelectedAuthorId(null);
        setIsCreateModalOpen(true);
    }, [createForm]);

    const closeCreateModal = useCallback(() => {
        setIsCreateModalOpen(false);
        setSelectedAuthorId(null);
    }, []);

    const handleCreateSeries = useCallback(async (data) => {
        setCreateLoading(true);
        try {
            const payload = {
                title: data.title,
                description: data.description,
            };

            // Librarian must provide authorId
            if (isLibrarian && data.authorId) {
                payload.authorId = parseInt(data.authorId, 10);
            }

            await seriesService.create(payload);
            await fetchSeries();
            closeCreateModal();
        } catch (err) {
            setError('Failed to create series');
            console.error('Error creating series:', err);
        } finally {
            setCreateLoading(false);
        }
    }, [isLibrarian, fetchSeries, closeCreateModal]);

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

        // For librarian, fetch all books or the series author's books
        if (isLibrarian && seriesItem.author?.id) {
            await fetchBooks(seriesItem.author.id);
        }

        // Fetch books in this series via GET /series/{id}/books
        try {
            const books = await seriesService.getBooks(seriesItem.id);
            setSeriesBooks(books || []);
        } catch (err) {
            console.error('Error fetching series books:', err);
            setSeriesBooks([]);
        }
    }, [isLibrarian, fetchBooks]);

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
        isLibrarian,
        isAuthor,

        // Series data
        series,
        loading,
        error,

        // Authors (for librarian)
        authors,
        selectedAuthorId,
        setSelectedAuthorId,

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
