// Mock services for testing
import { mockBooks, mockAuthors, mockSeries, mockSubjects, mockCollections, mockInventoryItems, mockReadingSessions, mockConversations, mockReports, mockUsers } from './testUtils';

// Mock bookService
export const mockBookService = {
    getAll: jest.fn().mockResolvedValue(mockBooks),
    getAllBooks: jest.fn().mockResolvedValue(mockBooks),
    getById: jest.fn().mockImplementation((id) => {
        const book = mockBooks.find(b => b.isbn === id);
        return Promise.resolve(book || null);
    }),
    getBookByIsbn: jest.fn().mockImplementation((isbn) => {
        const book = mockBooks.find(b => b.isbn === isbn);
        return Promise.resolve(book || null);
    }),
    create: jest.fn().mockResolvedValue(mockBooks[0]),
    update: jest.fn().mockResolvedValue(mockBooks[0]),
    delete: jest.fn().mockResolvedValue({ success: true }),
    search: jest.fn().mockImplementation((query, filters) => {
        return Promise.resolve(mockBooks.filter(b =>
            b.title.toLowerCase().includes(query?.toLowerCase() || '')
        ));
    }),
    searchBooks: jest.fn().mockImplementation((filters) => {
        let results = [...mockBooks];
        if (filters.title) {
            results = results.filter(b => b.title.toLowerCase().includes(filters.title.toLowerCase()));
        }
        if (filters.authorName) {
            results = results.filter(b =>
                b.authors.some(a =>
                    `${a.firstName} ${a.lastName}`.toLowerCase().includes(filters.authorName.toLowerCase())
                )
            );
        }
        if (filters.subjectName) {
            results = results.filter(b =>
                b.subjects.some(s => s.name.toLowerCase().includes(filters.subjectName.toLowerCase()))
            );
        }
        if (filters.year) {
            results = results.filter(b => b.publishingYear === parseInt(filters.year));
        }
        return Promise.resolve(results);
    }),
    getByAuthor: jest.fn().mockImplementation((authorId) => {
        return Promise.resolve(mockBooks.filter(b =>
            b.authors.some(a => a.id === parseInt(authorId))
        ));
    }),
    getBySeries: jest.fn().mockImplementation((seriesId) => {
        return Promise.resolve(mockBooks.filter(b => b.series?.id === parseInt(seriesId)));
    }),
    formatBookForDisplay: jest.fn().mockImplementation((book) => ({
        id: book.isbn,
        isbn: book.isbn,
        title: book.title,
        author: book.authors?.map(a => `${a.firstName} ${a.lastName}`).join(', ') || 'Unknown',
        genre: book.subjects?.map(s => s.name).join(', ') || 'General',
        year: book.publishingYear,
        description: book.description,
        cover: book.image?.url,
        pages: book.pages,
        authors: book.authors,
        subjects: book.subjects,
    })),
    createBookReadingEvent: jest.fn().mockResolvedValue({ id: 1, eventType: 'STARTED' }),
    getLatestBookReadingEvent: jest.fn().mockResolvedValue({ eventType: 'STARTED' }),
    getAllBookReadingSessions: jest.fn().mockResolvedValue(mockReadingSessions),
    getAllBookReadingEvents: jest.fn().mockResolvedValue([{ eventType: 'STARTED', createdAt: new Date().toISOString() }]),
};

// Mock authorService
export const mockAuthorService = {
    getAll: jest.fn().mockResolvedValue(mockAuthors),
    getById: jest.fn().mockImplementation((id) => {
        const author = mockAuthors.find(a => a.id === parseInt(id));
        return Promise.resolve(author || null);
    }),
    create: jest.fn().mockResolvedValue(mockAuthors[0]),
    update: jest.fn().mockResolvedValue(mockAuthors[0]),
    delete: jest.fn().mockResolvedValue({ success: true }),
};

// Mock seriesService
export const mockSeriesService = {
    getAll: jest.fn().mockResolvedValue(mockSeries),
    getById: jest.fn().mockImplementation((id) => {
        const series = mockSeries.find(s => s.id === parseInt(id));
        return Promise.resolve(series || null);
    }),
    getByAuthor: jest.fn().mockImplementation((authorId) => {
        return Promise.resolve(mockSeries.filter(s => s.author?.id === parseInt(authorId)));
    }),
    getBooks: jest.fn().mockResolvedValue(mockBooks.slice(0, 2)),
    create: jest.fn().mockResolvedValue(mockSeries[0]),
    update: jest.fn().mockResolvedValue(mockSeries[0]),
    delete: jest.fn().mockResolvedValue({ success: true }),
    addBook: jest.fn().mockResolvedValue({ success: true }),
    removeBook: jest.fn().mockResolvedValue({ success: true }),
};

// Mock subjectService
export const mockSubjectService = {
    getAll: jest.fn().mockResolvedValue(mockSubjects),
    getById: jest.fn().mockImplementation((id) => {
        const subject = mockSubjects.find(s => s.id === parseInt(id));
        return Promise.resolve(subject || null);
    }),
    create: jest.fn().mockResolvedValue(mockSubjects[0]),
    update: jest.fn().mockResolvedValue(mockSubjects[0]),
    delete: jest.fn().mockResolvedValue({ success: true }),
};

// Mock userService
export const mockUserService = {
    // Favorites
    addFavorite: jest.fn().mockResolvedValue({ success: true }),
    removeFavorite: jest.fn().mockResolvedValue({ success: true }),
    getFavorites: jest.fn().mockResolvedValue(mockBooks.slice(0, 2)),

    // Following
    followAuthor: jest.fn().mockResolvedValue({ success: true }),
    unfollowAuthor: jest.fn().mockResolvedValue({ success: true }),
    getFollowedAuthors: jest.fn().mockResolvedValue(mockAuthors),
    isFollowingAuthor: jest.fn().mockResolvedValue(true),

    followSeries: jest.fn().mockResolvedValue({ success: true }),
    unfollowSeries: jest.fn().mockResolvedValue({ success: true }),
    getFollowedSeries: jest.fn().mockResolvedValue(mockSeries),
    isFollowingSeries: jest.fn().mockResolvedValue(true),

    // Owned books
    getOwnedBooks: jest.fn().mockResolvedValue(mockBooks),
    addOwnedBook: jest.fn().mockResolvedValue({ success: true }),
    removeOwnedBook: jest.fn().mockResolvedValue({ success: true }),

    // Search
    searchByGoogle: jest.fn().mockResolvedValue([mockUsers.user]),
};

// Mock authService
export const mockAuthService = {
    loginWithGoogle: jest.fn().mockResolvedValue(mockUsers.user),
    logout: jest.fn().mockResolvedValue(undefined),
    getCurrentUser: jest.fn().mockReturnValue(mockUsers.user),
    getAccessToken: jest.fn().mockReturnValue('mock-access-token'),
    getRefreshToken: jest.fn().mockReturnValue('mock-refresh-token'),
    isAuthenticated: jest.fn().mockReturnValue(true),
    refreshAccessToken: jest.fn().mockResolvedValue('new-access-token'),
};

// Mock bookCollectionsService
export const mockBookCollectionsService = {
    create: jest.fn().mockResolvedValue(mockCollections[0]),
    update: jest.fn().mockResolvedValue(mockCollections[0]),
    delete: jest.fn().mockResolvedValue({ success: true }),
    getById: jest.fn().mockImplementation((id) => {
        const collection = mockCollections.find(c => c.id === parseInt(id));
        return Promise.resolve(collection || null);
    }),
    getOwn: jest.fn().mockResolvedValue(mockCollections),
    getAllowed: jest.fn().mockResolvedValue(mockCollections),
    getPublic: jest.fn().mockResolvedValue(mockCollections.filter(c => c.isPublic)),
    getShared: jest.fn().mockResolvedValue([]),
    checkAccess: jest.fn().mockResolvedValue(true),
    share: jest.fn().mockResolvedValue({ success: true }),
    unshare: jest.fn().mockResolvedValue({ success: true }),
    addBook: jest.fn().mockResolvedValue({ success: true }),
    removeBook: jest.fn().mockResolvedValue({ success: true }),
    containsBook: jest.fn().mockResolvedValue(false),
};

// Mock inventoryService
export const mockInventoryService = {
    getAll: jest.fn().mockResolvedValue(mockInventoryItems),
    create: jest.fn().mockResolvedValue(mockInventoryItems[0]),
    update: jest.fn().mockResolvedValue(mockInventoryItems[0]),
    delete: jest.fn().mockResolvedValue({ success: true }),
};

// Mock marketplaceService
export const mockMarketplaceService = {
    getAllBooks: jest.fn().mockResolvedValue(mockBooks),
    getBookSummary: jest.fn().mockResolvedValue({
        lowestPrice: 19.99,
        sellerCount: 3,
    }),
    getBookSellers: jest.fn().mockResolvedValue(mockInventoryItems),
};

// Mock readingService
export const mockReadingService = {
    startSession: jest.fn().mockResolvedValue(mockReadingSessions[1]),
    pauseSession: jest.fn().mockResolvedValue({ ...mockReadingSessions[1], status: 'PAUSED' }),
    resumeSession: jest.fn().mockResolvedValue({ ...mockReadingSessions[1], status: 'IN_PROGRESS' }),
    endSession: jest.fn().mockResolvedValue({ ...mockReadingSessions[1], status: 'COMPLETED' }),
    deleteSession: jest.fn().mockResolvedValue({ success: true }),
};

// Mock readingProgressService
export const mockReadingProgressService = {
    getBooksWithReadStatus: jest.fn().mockResolvedValue(
        mockBooks.map(b => ({ ...b, readStatus: 'NOT_STARTED' }))
    ),
    getBooksWithExistingReadStatus: jest.fn().mockResolvedValue(
        mockBooks.slice(0, 1).map(b => ({ ...b, readStatus: 'IN_PROGRESS' }))
    ),
};

// Mock messageService
export const mockMessageService = {
    listConversations: jest.fn().mockResolvedValue(mockConversations),
    getConversation: jest.fn().mockResolvedValue({
        ...mockConversations[0],
        messages: [
            { id: 1, content: 'Hello!', sender: mockUsers.user, createdAt: new Date().toISOString() },
            { id: 2, content: 'Hi there!', sender: mockUsers.author, createdAt: new Date().toISOString() },
        ],
    }),
    send: jest.fn().mockResolvedValue({ id: 3, content: 'New message', sender: mockUsers.user }),
    markAsRead: jest.fn().mockResolvedValue({ success: true }),
};

// Mock reportService
export const mockReportService = {
    create: jest.fn().mockResolvedValue(mockReports[0]),
    getAll: jest.fn().mockResolvedValue(mockReports),
    resolve: jest.fn().mockResolvedValue({ ...mockReports[0], status: 'RESOLVED' }),
    dismiss: jest.fn().mockResolvedValue({ ...mockReports[0], status: 'DISMISSED' }),
};

// Mock adminService
export const mockAdminService = {
    getAllUsers: jest.fn().mockResolvedValue(Object.values(mockUsers)),
    getUserById: jest.fn().mockImplementation((id) => {
        const user = Object.values(mockUsers).find(u => u.id === parseInt(id));
        return Promise.resolve(user || null);
    }),
    getAllRoles: jest.fn().mockResolvedValue(['USER', 'AUTHOR', 'LIBRARIAN', 'SELLER', 'ADMIN']),
    addRole: jest.fn().mockResolvedValue({ success: true }),
    removeRole: jest.fn().mockResolvedValue({ success: true }),
};

// Helper to setup all mocks
export const setupAllMocks = () => {
    jest.mock('../../api/services/bookService', () => mockBookService);
    jest.mock('../../api/services/authorService', () => mockAuthorService);
    jest.mock('../../api/services/seriesService', () => mockSeriesService);
    jest.mock('../../api/services/subjectService', () => mockSubjectService);
    jest.mock('../../api/services/userService', () => mockUserService);
    jest.mock('../../api/services/authService', () => mockAuthService);
    jest.mock('../../api/services/bookCollectionsService', () => mockBookCollectionsService);
    jest.mock('../../api/services/inventoryService', () => mockInventoryService);
    jest.mock('../../api/services/marketplaceService', () => mockMarketplaceService);
    jest.mock('../../api/services/readingService', () => mockReadingService);
    jest.mock('../../api/services/readingProgressService', () => mockReadingProgressService);
    jest.mock('../../api/services/messageService', () => mockMessageService);
    jest.mock('../../api/services/reportService', () => mockReportService);
    jest.mock('../../api/services/adminService', () => mockAdminService);
};

// Reset all mocks
export const resetAllMocks = () => {
    Object.values(mockBookService).forEach(fn => fn.mockClear?.());
    Object.values(mockAuthorService).forEach(fn => fn.mockClear?.());
    Object.values(mockSeriesService).forEach(fn => fn.mockClear?.());
    Object.values(mockSubjectService).forEach(fn => fn.mockClear?.());
    Object.values(mockUserService).forEach(fn => fn.mockClear?.());
    Object.values(mockAuthService).forEach(fn => fn.mockClear?.());
    Object.values(mockBookCollectionsService).forEach(fn => fn.mockClear?.());
    Object.values(mockInventoryService).forEach(fn => fn.mockClear?.());
    Object.values(mockMarketplaceService).forEach(fn => fn.mockClear?.());
    Object.values(mockReadingService).forEach(fn => fn.mockClear?.());
    Object.values(mockReadingProgressService).forEach(fn => fn.mockClear?.());
    Object.values(mockMessageService).forEach(fn => fn.mockClear?.());
    Object.values(mockReportService).forEach(fn => fn.mockClear?.());
    Object.values(mockAdminService).forEach(fn => fn.mockClear?.());
};

export default {
    mockBookService,
    mockAuthorService,
    mockSeriesService,
    mockSubjectService,
    mockUserService,
    mockAuthService,
    mockBookCollectionsService,
    mockInventoryService,
    mockMarketplaceService,
    mockReadingService,
    mockReadingProgressService,
    mockMessageService,
    mockReportService,
    mockAdminService,
    setupAllMocks,
    resetAllMocks,
};
