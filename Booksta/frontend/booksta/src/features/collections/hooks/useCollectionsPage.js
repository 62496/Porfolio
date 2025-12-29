import { useState, useEffect, useCallback, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import bookCollectionsService from '../../../api/services/bookCollectionsService';
import userService from '../../../api/services/userService';
import authService from '../../../api/services/authService';
import { collectionSchema } from '../validations/collectionSchema';

export const useCollectionsPage = () => {
    // Current user
    const currentUser = useMemo(() => authService.getCurrentUser(), []);

    // Collections state
    const [publicCollections, setPublicCollections] = useState([]);
    const [ownCollections, setOwnCollections] = useState([]);
    const [sharedCollections, setSharedCollections] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Active tab
    const [activeTab, setActiveTab] = useState('public'); // 'public', 'mine', 'shared'

    // Create modal state
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [createLoading, setCreateLoading] = useState(false);
    const [collectionImage, setCollectionImage] = useState(null);
    const [collectionImagePreview, setCollectionImagePreview] = useState(null);

    // Share modal state
    const [isShareModalOpen, setIsShareModalOpen] = useState(false);
    const [selectedCollection, setSelectedCollection] = useState(null);
    const [shareSearchTerm, setShareSearchTerm] = useState('');
    const [shareSearchResults, setShareSearchResults] = useState([]);
    const [shareLoading, setShareLoading] = useState(false);

    // Add books modal state
    const [isAddBooksModalOpen, setIsAddBooksModalOpen] = useState(false);
    const [allBooks, setAllBooks] = useState([]);
    const [bookSearchTerm, setBookSearchTerm] = useState('');
    const [addBooksLoading, setAddBooksLoading] = useState(false);

    // Edit modal state
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [editLoading, setEditLoading] = useState(false);
    const [editImage, setEditImage] = useState(null);
    const [editImagePreview, setEditImagePreview] = useState(null);

    // Delete modal state
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [deleteLoading, setDeleteLoading] = useState(false);

    // Create form
    const createForm = useForm({
        resolver: yupResolver(collectionSchema),
        mode: 'onChange',
        defaultValues: {
            name: '',
            description: '',
            visibility: 'PRIVATE',
        },
    });

    // Edit form
    const editForm = useForm({
        resolver: yupResolver(collectionSchema),
        mode: 'onChange',
        defaultValues: {
            name: '',
            description: '',
            visibility: 'PRIVATE',
        },
    });

    // ========================
    // Data Fetching
    // ========================

    const fetchCollections = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            const [publicData, ownData, sharedData] = await Promise.all([
                bookCollectionsService.getPublicCollections(),
                bookCollectionsService.getOwnCollections(),
                bookCollectionsService.getSharedCollections(),
            ]);
            setPublicCollections(publicData || []);
            setOwnCollections(ownData || []);
            setSharedCollections(sharedData || []);
        } catch (err) {
            setError('Failed to load collections');
            console.error('Error fetching collections:', err);
        } finally {
            setLoading(false);
        }
    }, []);

    const fetchBooks = useCallback(async () => {
        try {
            const data = await userService.getOwnedBooks();
            setAllBooks(data || []);
        } catch (err) {
            console.error('Error fetching owned books:', err);
        }
    }, []);

    useEffect(() => {
        fetchCollections();
        fetchBooks();
    }, [fetchCollections, fetchBooks]);

    // ========================
    // Create Collection
    // ========================

    const openCreateModal = useCallback(() => {
        createForm.reset({
            name: '',
            description: '',
            visibility: 'PRIVATE',
        });
        setCollectionImage(null);
        setCollectionImagePreview(null);
        setIsCreateModalOpen(true);
    }, [createForm]);

    const closeCreateModal = useCallback(() => {
        setIsCreateModalOpen(false);
        setCollectionImage(null);
        setCollectionImagePreview(null);
    }, []);

    const handleCreateCollection = useCallback(async (data) => {
        setCreateLoading(true);
        try {
            await bookCollectionsService.create(data, collectionImage);
            await fetchCollections();
            closeCreateModal();
        } catch (err) {
            setError('Failed to create collection');
            console.error('Error creating collection:', err);
        } finally {
            setCreateLoading(false);
        }
    }, [collectionImage, fetchCollections, closeCreateModal]);

    // ========================
    // Edit Collection
    // ========================

    const openEditModal = useCallback((collection) => {
        setSelectedCollection(collection);
        editForm.reset({
            name: collection.name || '',
            description: collection.description || '',
            visibility: collection.visibility || 'PRIVATE',
        });
        setEditImage(null);
        setEditImagePreview(collection.image ? bookCollectionsService.getImageUrl(collection.id) : null);
        setIsEditModalOpen(true);
    }, [editForm]);

    const closeEditModal = useCallback(() => {
        setIsEditModalOpen(false);
        setSelectedCollection(null);
        setEditImage(null);
        setEditImagePreview(null);
    }, []);

    const handleEditCollection = useCallback(async (data) => {
        if (!selectedCollection) return;
        setEditLoading(true);
        try {
            await bookCollectionsService.update(selectedCollection.id, data, editImage);
            await fetchCollections();
            closeEditModal();
        } catch (err) {
            setError('Failed to update collection');
            console.error('Error updating collection:', err);
        } finally {
            setEditLoading(false);
        }
    }, [selectedCollection, editImage, fetchCollections, closeEditModal]);

    // ========================
    // Delete Collection
    // ========================

    const openDeleteModal = useCallback((collection) => {
        setSelectedCollection(collection);
        setIsDeleteModalOpen(true);
    }, []);

    const closeDeleteModal = useCallback(() => {
        setIsDeleteModalOpen(false);
        setSelectedCollection(null);
    }, []);

    const handleDeleteCollection = useCallback(async () => {
        if (!selectedCollection) return;
        setDeleteLoading(true);
        try {
            await bookCollectionsService.delete(selectedCollection.id);
            await fetchCollections();
            closeDeleteModal();
        } catch (err) {
            setError('Failed to delete collection');
            console.error('Error deleting collection:', err);
        } finally {
            setDeleteLoading(false);
        }
    }, [selectedCollection, fetchCollections, closeDeleteModal]);

    // ========================
    // Share Collection
    // ========================

    const openShareModal = useCallback((collection) => {
        setSelectedCollection(collection);
        setShareSearchTerm('');
        setShareSearchResults([]);
        setIsShareModalOpen(true);
    }, []);

    const closeShareModal = useCallback(() => {
        setIsShareModalOpen(false);
        setSelectedCollection(null);
        setShareSearchTerm('');
        setShareSearchResults([]);
    }, []);

    const handleShareSearchChange = useCallback(async (e) => {
        const value = e.target.value;
        setShareSearchTerm(value);
        if (!value.trim()) {
            setShareSearchResults([]);
            return;
        }
        try {
            const results = await userService.searchGoogleUsers(value.trim(), currentUser?.id);
            setShareSearchResults(results || []);
        } catch (err) {
            setShareSearchResults([]);
        }
    }, [currentUser?.id]);

    const handleShareWithUser = useCallback(async (user) => {
        if (!selectedCollection) return;
        setShareLoading(true);
        try {
            const updated = await bookCollectionsService.shareWithUser(selectedCollection.id, user.email);
            setSelectedCollection(updated);
            await fetchCollections();
            setShareSearchTerm('');
            setShareSearchResults([]);
        } catch (err) {
            setError('Failed to share collection');
            console.error('Error sharing collection:', err);
        } finally {
            setShareLoading(false);
        }
    }, [selectedCollection, fetchCollections]);

    const handleUnshareWithUser = useCallback(async (userId) => {
        if (!selectedCollection) return;
        setShareLoading(true);
        try {
            const updated = await bookCollectionsService.unshareWithUser(selectedCollection.id, userId);
            setSelectedCollection(updated);
            await fetchCollections();
        } catch (err) {
            setError('Failed to remove user from collection');
            console.error('Error unsharing collection:', err);
        } finally {
            setShareLoading(false);
        }
    }, [selectedCollection, fetchCollections]);

    // ========================
    // Add Books to Collection
    // ========================

    const openAddBooksModal = useCallback((collection) => {
        setSelectedCollection(collection);
        setBookSearchTerm('');
        setIsAddBooksModalOpen(true);
    }, []);

    const closeAddBooksModal = useCallback(() => {
        setIsAddBooksModalOpen(false);
        setSelectedCollection(null);
        setBookSearchTerm('');
    }, []);

    const filteredBooks = useMemo(() => {
        if (!bookSearchTerm.trim()) return allBooks;
        const term = bookSearchTerm.toLowerCase();
        return allBooks.filter(book =>
            book.title?.toLowerCase().includes(term) ||
            book.isbn?.toLowerCase().includes(term)
        );
    }, [allBooks, bookSearchTerm]);

    const isBookInCollection = useCallback((isbn) => {
        if (!selectedCollection?.books) return false;
        return selectedCollection.books.some(book => book.isbn === isbn);
    }, [selectedCollection]);

    const handleAddBook = useCallback(async (isbn) => {
        if (!selectedCollection) return;
        setAddBooksLoading(true);
        try {
            const updated = await bookCollectionsService.addBook(selectedCollection.id, isbn);
            setSelectedCollection(updated);
            await fetchCollections();
        } catch (err) {
            setError('Failed to add book to collection');
            console.error('Error adding book:', err);
        } finally {
            setAddBooksLoading(false);
        }
    }, [selectedCollection, fetchCollections]);

    const handleRemoveBook = useCallback(async (isbn) => {
        if (!selectedCollection) return;
        setAddBooksLoading(true);
        try {
            const updated = await bookCollectionsService.removeBook(selectedCollection.id, isbn);
            setSelectedCollection(updated);
            await fetchCollections();
        } catch (err) {
            setError('Failed to remove book from collection');
            console.error('Error removing book:', err);
        } finally {
            setAddBooksLoading(false);
        }
    }, [selectedCollection, fetchCollections]);

    // ========================
    // Helpers
    // ========================

    const isOwner = useCallback((collection) => {
        return collection?.owner?.id === currentUser?.id;
    }, [currentUser?.id]);

    const displayedCollections = useMemo(() => {
        switch (activeTab) {
            case 'public':
                return publicCollections;
            case 'mine':
                return ownCollections;
            case 'shared':
                return sharedCollections;
            default:
                return publicCollections;
        }
    }, [activeTab, publicCollections, ownCollections, sharedCollections]);

    return {
        // User
        currentUser,

        // Collections data
        publicCollections,
        ownCollections,
        sharedCollections,
        displayedCollections,
        loading,
        error,

        // Tabs
        activeTab,
        setActiveTab,

        // Create modal
        isCreateModalOpen,
        createForm,
        createLoading,
        collectionImage,
        setCollectionImage,
        collectionImagePreview,
        setCollectionImagePreview,
        openCreateModal,
        closeCreateModal,
        handleCreateCollection,

        // Edit modal
        isEditModalOpen,
        editForm,
        editLoading,
        editImage,
        setEditImage,
        editImagePreview,
        setEditImagePreview,
        openEditModal,
        closeEditModal,
        handleEditCollection,

        // Delete modal
        isDeleteModalOpen,
        deleteLoading,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteCollection,

        // Share modal
        isShareModalOpen,
        selectedCollection,
        shareSearchTerm,
        shareSearchResults,
        shareLoading,
        openShareModal,
        closeShareModal,
        handleShareSearchChange,
        handleShareWithUser,
        handleUnshareWithUser,

        // Add books modal
        isAddBooksModalOpen,
        allBooks,
        filteredBooks,
        bookSearchTerm,
        setBookSearchTerm,
        addBooksLoading,
        openAddBooksModal,
        closeAddBooksModal,
        isBookInCollection,
        handleAddBook,
        handleRemoveBook,

        // Helpers
        isOwner,
        getImageUrl: bookCollectionsService.getImageUrl,
    };
};

export default useCollectionsPage;
