import apiClient from '../client';
import { API_ENDPOINTS } from '../endpoints';

const bookService = {

    /**
     * Get all books
     * @param {Object} params - Query parameters (page, limit, sort, etc.)
     * @returns {Promise} - Array of books
     */
    getAll: async (params = {}) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_ALL, {
                params, // e.g., { page: 1, limit: 10, sort: 'title' }
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    createBookReadingEvent: async (bookId, eventType) => {
        try {
            const response = apiClient.post(
                API_ENDPOINTS.BOOKS.CREATE_BOOK_READING_EVENT(bookId),
                {
                    eventType: eventType
                }
            )
            return response.data;
        } catch (error) {
            throw error;
        }
    },


    async getAllBooks() {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_ALL);
            return response.data;
        } catch (error) {
            console.error('Error fetching books:', error);
            throw error;
        }
    },

    /**
     * Get a single book by ID
     * @param {string|number} id - Book ID
     * @returns {Promise} - Book object
     */
    getById: async (id) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_BY_ID(id));
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    async getBookByIsbn(isbn) {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_BY_ISBN(isbn));
            return response.data;
        } catch (error) {
            console.error('Error fetching book:', error);
            throw error;
        }
    },

    /**
     * Create a new book
     * @param {Object} bookData - Book information
     * @returns {Promise} - Created book object
     */
    create: async (payload) => {
        try {
            const response = await apiClient.post(
                API_ENDPOINTS.BOOKS.CREATE,
                payload,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            return response.data;
        } catch (error) {
            console.error("Error creating book:", error);
            throw error;
        }
    },

    /**
     * Update an existing book
     * @param {string|number} id - Book ID (ISBN)
     * @param {FormData} bookData - Updated book information with image
     * @returns {Promise} - Updated book object
     */
    update: async (id, bookData) => {
        try {
            const response = await apiClient.put(
                API_ENDPOINTS.BOOKS.UPDATE(id),
                bookData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Partially update a book
     * @param {string|number} id - Book ID
     * @param {Object} bookData - Partial book information
     * @returns {Promise} - Updated book object
     */
    patch: async (id, bookData) => {
        try {
            const response = await apiClient.patch(
                API_ENDPOINTS.BOOKS.UPDATE(id),
                bookData
            );
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Delete a book
     * @param {string|number} id - Book ID
     * @returns {Promise} - Success message
     */
    delete: async (id) => {
        try {
            const response = await apiClient.delete(API_ENDPOINTS.BOOKS.DELETE(id));
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Search books
     * @param {string} query - Search query
     * @param {Object} filters - Additional filters
     * @returns {Promise} - Array of matching books
     */
    search: async (query, filters = {}) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.SEARCH, {
                params: { q: query, ...filters },
            });
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    async searchBooks(filters = {}) {
        try {
            const params = {};

            if (filters.title) params.title = filters.title;
            if (filters.authorName) params.authorName = filters.authorName;
            if (filters.subjectName) params.subjectName = filters.subjectName;
            if (filters.year) params.year = filters.year;

            const response = await apiClient.get(API_ENDPOINTS.BOOKS.SEARCH, { params });
            return response.data;
        } catch (error) {
            console.error('Error searching books:', error);
            throw error;
        }
    },


    formatBookForDisplay(book) {
        let coverUrl = book.image?.url || null;

        return {
            id: book.isbn,
            pages: book.pages,
            isbn: book.isbn,
            title: book.title,
            author: this.formatAuthors(book.authors),
            genre: this.formatSubjects(book.subjects),
            year: book.publishingYear,
            description: book.description,
            cover: coverUrl,
            authors: book.authors,
            subjects: book.subjects,
            image: book.image
        };
    },

    formatAuthors(authors) {
        if (!authors || authors.length === 0) return 'Unknown Author';

        return authors
            .map(author => `${author.firstName} ${author.lastName}`.trim())
            .join(', ');
    },

    formatSubjects(subjects) {
        if (!subjects || subjects.length === 0) return 'General';

        return subjects
            .map(subject => subject.name)
            .join(', ');
    },

    /**
     * Get books by author ID
     * @param {string|number} authorId - Author ID
     * @returns {Promise<Array>} - Array of books by the author
     */
    getByAuthor: async (authorId) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_BY_AUTHOR(authorId));
            return response.data;
        } catch (error) {
            console.error('Error fetching books by author:', error);
            throw error;
        }
    },

    /**
     * Get books by series id (backend endpoint: GET /api/books/series/{id})
     * Falls back to fetching all books and filtering if endpoint fails.
     * @param {string|number} seriesId
     * @returns {Promise<Array>}
     */
    getBySeries: async (seriesId) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.BY_SERIES(seriesId));
            return response.data;
        } catch (error) {
            // Log for debugging and fallback to /books
            console.warn('getBySeries failed, will fallback to getAllBooks', error);
            try {
                const all = await bookService.getAllBooks();
                return (all || []).filter(b => b.series && String(b.series.id) === String(seriesId));
            } catch (e) {
                console.error('Fallback getAllBooks failed in getBySeries', e);
                return [];
            }
        }
    },

    getLatestBookReadingEvent: async (isbn) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_LATEST_BOOK_READING_EVENT(isbn))
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    getAllBookReadingSessions: async (isbn) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_ALL_BOOK_READING_SESSIONS(isbn));
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    getAllBookReadingEvents: async (isbn) => {
        try {
            const response = await apiClient.get(API_ENDPOINTS.BOOKS.GET_ALL_BOOK_READING_EVENTS(isbn));
            return response.data;
        } catch (error) {
            throw error;
        }
    },

    /**
     * Filter books with multiple optional criteria
     * @param {Object} filters - Filter parameters
     * @param {string} [filters.title] - Filter by title (partial match, case-insensitive)
     * @param {number} [filters.yearMin] - Minimum publication year
     * @param {number} [filters.yearMax] - Maximum publication year
     * @param {number} [filters.pagesMin] - Minimum page count
     * @param {number} [filters.pagesMax] - Maximum page count
     * @param {number[]} [filters.authorIds] - Filter by author IDs
     * @param {number[]} [filters.subjectIds] - Filter by subject IDs
     * @returns {Promise<Array>} - Array of filtered books
     */
    filterBooks: async (filters = {}) => {
        try {
            const params = {};

            if (filters.title) params.title = filters.title;
            if (filters.yearMin) params.yearMin = filters.yearMin;
            if (filters.yearMax) params.yearMax = filters.yearMax;
            if (filters.pagesMin) params.pagesMin = filters.pagesMin;
            if (filters.pagesMax) params.pagesMax = filters.pagesMax;
            if (filters.authorIds?.length) params.authorIds = filters.authorIds.join(',');
            if (filters.subjectIds?.length) params.subjectIds = filters.subjectIds.join(',');

            const response = await apiClient.get(API_ENDPOINTS.BOOKS.FILTER, { params });
            return response.data;
        } catch (error) {
            console.error('Error filtering books:', error);
            throw error;
        }
    }
};

export default bookService;