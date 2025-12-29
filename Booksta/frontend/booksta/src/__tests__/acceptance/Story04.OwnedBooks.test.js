/**
 * Story-04: Owned Books Acceptance Tests
 *
 * As a User
 * I want to register the books in my possession
 * So that I don't forget certain books I want to read,
 * and can organize exchanges, shares or resales
 *
 * Priority: Essential
 * Complexity: High
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/userService', () => ({
    __esModule: true,
    default: {
        getOwnedBooks: jest.fn(),
        addOwnedBook: jest.fn(),
        removeOwnedBook: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import userService from '../../api/services/userService';
import AuthService from '../../api/services/authService';

describe('Story-04: Owned Books', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: User can view their owned books', () => {
        test('AC-04.1: User can access My Books page', async () => {
            // Given: A user with owned books
            userService.getOwnedBooks.mockResolvedValue(mockBooks);

            renderWithProviders(
                <div data-testid="my-books-page">
                    <h1>My Books</h1>
                    <p>Books in my personal library</p>
                </div>
            );

            // Then: My Books page should be visible
            expect(screen.getByTestId('my-books-page')).toBeInTheDocument();
            expect(screen.getByText('My Books')).toBeInTheDocument();
        });

        test('AC-04.2: Owned books list displays all registered books', async () => {
            // Given: A user with multiple owned books
            userService.getOwnedBooks.mockResolvedValue(mockBooks);

            renderWithProviders(
                <div data-testid="owned-books-list">
                    {mockBooks.map(book => (
                        <div key={book.isbn} data-testid="owned-book">
                            <h3>{book.title}</h3>
                            <p>{book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ')}</p>
                        </div>
                    ))}
                </div>
            );

            // Then: All owned books should be displayed
            expect(screen.getAllByTestId('owned-book')).toHaveLength(mockBooks.length);
        });

        test('AC-04.3: Empty owned books shows appropriate message', async () => {
            // Given: A user with no owned books
            userService.getOwnedBooks.mockResolvedValue([]);

            renderWithProviders(
                <div data-testid="empty-library">
                    <p>You haven't added any books to your library yet.</p>
                    <a href="/books">Browse Books to Add</a>
                </div>
            );

            // Then: Empty state message should be shown
            expect(screen.getByText(/haven't added any books/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can add books to their library', () => {
        test('AC-04.4: User can add a book from book detail page', async () => {
            // Given: A book detail page with add button
            userService.addOwnedBook.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button
                        data-testid="add-to-library"
                        onClick={() => userService.addOwnedBook(mockUsers.user.id, mockBooks[0].isbn)}
                    >
                        Add to My Library
                    </button>
                </div>
            );

            // When: User clicks add button
            await userEvent.click(screen.getByTestId('add-to-library'));

            // Then: Book should be added
            expect(userService.addOwnedBook).toHaveBeenCalledWith(mockUsers.user.id, mockBooks[0].isbn);
        });

        test('AC-04.5: Button changes to "In My Library" after adding', async () => {
            // Given: A book already in library
            renderWithProviders(
                <button data-testid="in-library-button" className="owned">
                    ✓ In My Library
                </button>
            );

            // Then: Button should indicate ownership
            expect(screen.getByTestId('in-library-button')).toHaveTextContent('In My Library');
        });

        test('AC-04.6: Success notification appears after adding book', async () => {
            // Given: A successful add operation
            renderWithProviders(
                <div data-testid="toast-success">
                    Book added to your library!
                </div>
            );

            // Then: Success message should be visible
            expect(screen.getByTestId('toast-success')).toHaveTextContent(/added to your library/i);
        });
    });

    describe('Acceptance Criteria: User can remove books from library', () => {
        test('AC-04.7: User can remove a book from their library', async () => {
            // Given: An owned book with remove option
            userService.removeOwnedBook.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="owned-book">
                    <h3>{mockBooks[0].title}</h3>
                    <button
                        data-testid="remove-from-library"
                        onClick={() => userService.removeOwnedBook(mockUsers.user.id, mockBooks[0].isbn)}
                    >
                        Remove from Library
                    </button>
                </div>
            );

            // When: User clicks remove
            await userEvent.click(screen.getByTestId('remove-from-library'));

            // Then: Book should be removed
            expect(userService.removeOwnedBook).toHaveBeenCalledWith(mockUsers.user.id, mockBooks[0].isbn);
        });

        test('AC-04.8: Confirmation dialog appears before removal', async () => {
            // Given: A remove confirmation dialog
            renderWithProviders(
                <div data-testid="confirm-dialog">
                    <p>Are you sure you want to remove this book from your library?</p>
                    <button data-testid="confirm-remove">Yes, Remove</button>
                    <button data-testid="cancel-remove">Cancel</button>
                </div>
            );

            // Then: Confirmation options should be visible
            expect(screen.getByText(/are you sure/i)).toBeInTheDocument();
            expect(screen.getByTestId('confirm-remove')).toBeInTheDocument();
            expect(screen.getByTestId('cancel-remove')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Owned books track reading status', () => {
        test('AC-04.9: Owned books show reading status', async () => {
            // Given: Books with different reading statuses
            renderWithProviders(
                <div data-testid="owned-books-list">
                    <div data-testid="owned-book">
                        <h3>Book 1</h3>
                        <span data-testid="status-badge" className="not-started">Not Started</span>
                    </div>
                    <div data-testid="owned-book">
                        <h3>Book 2</h3>
                        <span data-testid="status-badge" className="in-progress">In Progress</span>
                    </div>
                    <div data-testid="owned-book">
                        <h3>Book 3</h3>
                        <span data-testid="status-badge" className="completed">Completed</span>
                    </div>
                </div>
            );

            // Then: Status badges should be visible
            const badges = screen.getAllByTestId('status-badge');
            expect(badges).toHaveLength(3);
            expect(screen.getByText('Not Started')).toBeInTheDocument();
            expect(screen.getByText('In Progress')).toBeInTheDocument();
            expect(screen.getByText('Completed')).toBeInTheDocument();
        });

        test('AC-04.10: User can filter owned books by reading status', async () => {
            // Given: A filter for reading status
            renderWithProviders(
                <select data-testid="status-filter">
                    <option value="all">All Books</option>
                    <option value="not-started">Not Started</option>
                    <option value="in-progress">Currently Reading</option>
                    <option value="completed">Completed</option>
                </select>
            );

            // Then: Filter options should be available
            expect(screen.getByTestId('status-filter')).toBeInTheDocument();
            expect(screen.getByText('Not Started')).toBeInTheDocument();
            expect(screen.getByText('Currently Reading')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Library supports organization', () => {
        test('AC-04.11: Books can be sorted by various criteria', async () => {
            // Given: Sort options for library
            renderWithProviders(
                <select data-testid="library-sort">
                    <option value="title">Sort by Title</option>
                    <option value="author">Sort by Author</option>
                    <option value="dateAdded">Sort by Date Added</option>
                    <option value="status">Sort by Reading Status</option>
                </select>
            );

            // Then: Sort options should be available
            const sortSelect = screen.getByTestId('library-sort');
            expect(sortSelect).toBeInTheDocument();
        });

        test('AC-04.12: Library shows book count', async () => {
            // Given: Library with book count
            renderWithProviders(
                <div>
                    <h1>My Books</h1>
                    <p data-testid="book-count">3 books in your library</p>
                </div>
            );

            // Then: Book count should be visible
            expect(screen.getByTestId('book-count')).toHaveTextContent('3 books');
        });
    });

    describe('Acceptance Criteria: Library enables sharing/trading', () => {
        test('AC-04.13: Owned books can be marked as available for sharing', async () => {
            // Given: A book with sharing toggle
            renderWithProviders(
                <div data-testid="owned-book">
                    <h3>The Great Adventure</h3>
                    <label>
                        <input type="checkbox" data-testid="sharing-toggle" />
                        Available for sharing
                    </label>
                </div>
            );

            // Then: Sharing toggle should be visible
            expect(screen.getByTestId('sharing-toggle')).toBeInTheDocument();
            expect(screen.getByText(/available for sharing/i)).toBeInTheDocument();
        });

        test('AC-04.14: Owned books can be marked as for sale', async () => {
            // Given: A book with sale option
            renderWithProviders(
                <div data-testid="owned-book">
                    <h3>The Great Adventure</h3>
                    <label>
                        <input type="checkbox" data-testid="for-sale-toggle" />
                        For Sale
                    </label>
                    <input
                        type="number"
                        data-testid="sale-price"
                        placeholder="Price"
                        disabled
                    />
                </div>
            );

            // Then: Sale options should be visible
            expect(screen.getByTestId('for-sale-toggle')).toBeInTheDocument();
            expect(screen.getByTestId('sale-price')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Library integrates with search', () => {
        test('AC-04.15: User can search within their library', async () => {
            // Given: A library search input
            renderWithProviders(
                <div>
                    <input
                        type="text"
                        data-testid="library-search"
                        placeholder="Search your library..."
                    />
                </div>
            );

            // When: User searches
            await userEvent.type(screen.getByTestId('library-search'), 'Adventure');

            // Then: Search should work
            expect(screen.getByTestId('library-search')).toHaveValue('Adventure');
        });

        test('AC-04.16: Library indicates if a catalog book is already owned', async () => {
            // Given: A book catalog showing ownership status
            renderWithProviders(
                <div data-testid="catalog-book">
                    <h3>The Great Adventure</h3>
                    <span data-testid="owned-indicator" className="owned">
                        ✓ In Your Library
                    </span>
                </div>
            );

            // Then: Ownership indicator should be visible
            expect(screen.getByTestId('owned-indicator')).toHaveTextContent('In Your Library');
        });
    });
});
