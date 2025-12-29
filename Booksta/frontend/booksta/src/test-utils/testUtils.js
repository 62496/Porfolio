// Test utilities for acceptance tests
import React from 'react';
import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { GoogleOAuthProvider } from '@react-oauth/google';

// Mock user objects for different roles
export const mockUsers = {
    user: {
        id: 1,
        email: 'user@test.com',
        firstName: 'Test',
        lastName: 'User',
        picture: 'https://example.com/avatar.jpg',
        roles: [{ name: 'USER' }],
    },
    author: {
        id: 2,
        email: 'author@test.com',
        firstName: 'Test',
        lastName: 'Author',
        picture: 'https://example.com/author.jpg',
        roles: [{ name: 'USER' }, { name: 'AUTHOR' }],
    },
    librarian: {
        id: 3,
        email: 'librarian@test.com',
        firstName: 'Test',
        lastName: 'Librarian',
        picture: 'https://example.com/librarian.jpg',
        roles: [{ name: 'USER' }, { name: 'LIBRARIAN' }],
    },
    seller: {
        id: 4,
        email: 'seller@test.com',
        firstName: 'Test',
        lastName: 'Seller',
        picture: 'https://example.com/seller.jpg',
        roles: [{ name: 'USER' }, { name: 'SELLER' }],
    },
    admin: {
        id: 5,
        email: 'admin@test.com',
        firstName: 'Test',
        lastName: 'Admin',
        picture: 'https://example.com/admin.jpg',
        roles: [{ name: 'USER' }, { name: 'ADMIN' }],
    },
};

// Mock book data
export const mockBooks = [
    {
        isbn: '9781234567890',
        title: 'The Great Adventure',
        publishingYear: 2023,
        pages: 350,
        description: 'An epic adventure story about discovery and growth.',
        authors: [{ id: 1, firstName: 'John', lastName: 'Doe' }],
        subjects: [{ id: 1, name: 'Adventure' }, { id: 2, name: 'Fiction' }],
        image: { url: 'https://example.com/book1.jpg' },
        series: null,
    },
    {
        isbn: '9781234567891',
        title: 'Mystery at Midnight',
        publishingYear: 2022,
        pages: 280,
        description: 'A thrilling mystery novel.',
        authors: [{ id: 2, firstName: 'Jane', lastName: 'Smith' }],
        subjects: [{ id: 3, name: 'Mystery' }, { id: 4, name: 'Thriller' }],
        image: { url: 'https://example.com/book2.jpg' },
        series: { id: 1, title: 'Midnight Series' },
    },
    {
        isbn: '9781234567892',
        title: 'Science of Tomorrow',
        publishingYear: 2024,
        pages: 420,
        description: 'Exploring future technologies.',
        authors: [{ id: 1, firstName: 'John', lastName: 'Doe' }],
        subjects: [{ id: 5, name: 'Science Fiction' }],
        image: { url: 'https://example.com/book3.jpg' },
        series: null,
    },
];

// Mock authors data
export const mockAuthors = [
    {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        biography: 'A prolific writer of adventure and science fiction.',
        image: { url: 'https://example.com/author1.jpg' },
        user: { id: 2 },
    },
    {
        id: 2,
        firstName: 'Jane',
        lastName: 'Smith',
        biography: 'Known for gripping mystery novels.',
        image: { url: 'https://example.com/author2.jpg' },
        user: null,
    },
];

// Mock series data
export const mockSeries = [
    {
        id: 1,
        title: 'Midnight Series',
        description: 'A collection of mystery novels set at midnight.',
        author: { id: 2, firstName: 'Jane', lastName: 'Smith' },
        bookCount: 3,
    },
    {
        id: 2,
        title: 'Adventure Chronicles',
        description: 'Epic adventures across the world.',
        author: { id: 1, firstName: 'John', lastName: 'Doe' },
        bookCount: 5,
    },
];

// Mock subjects/genres
export const mockSubjects = [
    { id: 1, name: 'Adventure' },
    { id: 2, name: 'Fiction' },
    { id: 3, name: 'Mystery' },
    { id: 4, name: 'Thriller' },
    { id: 5, name: 'Science Fiction' },
    { id: 6, name: 'Romance' },
    { id: 7, name: 'Fantasy' },
];

// Mock collections
export const mockCollections = [
    {
        id: 1,
        name: 'Summer Reading',
        description: 'Books to read during summer vacation',
        isPublic: true,
        owner: { id: 1, firstName: 'Test', lastName: 'User' },
        books: [mockBooks[0], mockBooks[1]],
    },
    {
        id: 2,
        name: 'Favorites',
        description: 'My all-time favorite books',
        isPublic: false,
        owner: { id: 1, firstName: 'Test', lastName: 'User' },
        books: [mockBooks[2]],
    },
];

// Mock inventory items
export const mockInventoryItems = [
    {
        book: mockBooks[0],
        quantity: 5,
        price: 19.99,
        seller: mockUsers.seller,
    },
    {
        book: mockBooks[1],
        quantity: 3,
        price: 24.99,
        seller: mockUsers.seller,
    },
];

// Mock reading sessions
export const mockReadingSessions = [
    {
        id: 1,
        bookIsbn: '9781234567890',
        startTime: '2024-01-15T10:00:00Z',
        endTime: '2024-01-15T11:30:00Z',
        pagesRead: 50,
        status: 'COMPLETED',
    },
    {
        id: 2,
        bookIsbn: '9781234567890',
        startTime: '2024-01-16T14:00:00Z',
        endTime: null,
        pagesRead: 0,
        status: 'IN_PROGRESS',
    },
];

// Mock messages/conversations
export const mockConversations = [
    {
        id: 1,
        participants: [mockUsers.user, mockUsers.author],
        lastMessage: 'Looking forward to your next book!',
        unreadCount: 2,
        updatedAt: '2024-01-15T10:00:00Z',
    },
];

// Mock reports
export const mockReports = [
    {
        id: 1,
        book: mockBooks[0],
        reporter: mockUsers.user,
        reason: 'Incorrect author information',
        status: 'PENDING',
        createdAt: '2024-01-15T10:00:00Z',
    },
];

// All providers wrapper for rendering components
const AllProviders = ({ children }) => {
    return (
        <GoogleOAuthProvider clientId="test-client-id">
            <BrowserRouter>
                {children}
            </BrowserRouter>
        </GoogleOAuthProvider>
    );
};

// Custom render function with all providers
export const renderWithProviders = (ui, options = {}) => {
    return render(ui, { wrapper: AllProviders, ...options });
};

// Setup authenticated user in localStorage
export const setupAuthenticatedUser = (user = mockUsers.user) => {
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('refreshToken', 'mock-refresh-token');
};

// Clear authentication
export const clearAuth = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('refreshToken');
};

// Helper to check if user has specific role
export const hasRole = (user, roleName) => {
    return user?.roles?.some(role => role.name === roleName) || false;
};

// Wait for loading to complete
export const waitForLoadingToComplete = async (findByText) => {
    try {
        await findByText(/loading/i, {}, { timeout: 100 });
        // If loading text found, wait for it to disappear
        await new Promise(resolve => setTimeout(resolve, 100));
    } catch {
        // Loading text not found, component already loaded
    }
};

export default {
    mockUsers,
    mockBooks,
    mockAuthors,
    mockSeries,
    mockSubjects,
    mockCollections,
    mockInventoryItems,
    mockReadingSessions,
    mockConversations,
    mockReports,
    renderWithProviders,
    setupAuthenticatedUser,
    clearAuth,
    hasRole,
    waitForLoadingToComplete,
};
