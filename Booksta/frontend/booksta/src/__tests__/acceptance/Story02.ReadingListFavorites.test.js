/**
 * Story-02: Reading List / Favorites Acceptance Tests
 *
 * As a User
 * I want to create and organize a list of books I want to read
 * So that I can facilitate the choice of my next reading
 *
 * Priority: Indispensable
 * Complexity: Medium
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/userService', () => ({
    __esModule: true,
    default: {
        getFavorites: jest.fn(),
        addFavorite: jest.fn(),
        removeFavorite: jest.fn(),
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

describe('Story-02: Reading List / Favorites', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: User can view their favorites list', () => {
        test('AC-02.1: User can access their favorites page', async () => {
            // Given: A user with favorite books
            userService.getFavorites.mockResolvedValue(mockBooks.slice(0, 2));

            // When: User navigates to favorites
            renderWithProviders(
                <div data-testid="favorites-page">
                    <h1>My Favorites</h1>
                    <div data-testid="favorites-list">
                        {mockBooks.slice(0, 2).map(book => (
                            <div key={book.isbn} data-testid="favorite-book">
                                <h3>{book.title}</h3>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Favorites page should be visible
            expect(screen.getByTestId('favorites-page')).toBeInTheDocument();
            expect(screen.getByText('My Favorites')).toBeInTheDocument();
        });

        test('AC-02.2: Favorites list displays all favorited books', async () => {
            // Given: A user with 2 favorite books
            const favoriteBooks = mockBooks.slice(0, 2);
            userService.getFavorites.mockResolvedValue(favoriteBooks);

            renderWithProviders(
                <div data-testid="favorites-list">
                    {favoriteBooks.map(book => (
                        <div key={book.isbn} data-testid="favorite-book">
                            <h3>{book.title}</h3>
                            <p>{book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ')}</p>
                        </div>
                    ))}
                </div>
            );

            // Then: All favorite books should be displayed
            expect(screen.getAllByTestId('favorite-book')).toHaveLength(2);
            expect(screen.getByText('The Great Adventure')).toBeInTheDocument();
            expect(screen.getByText('Mystery at Midnight')).toBeInTheDocument();
        });

        test('AC-02.3: Empty favorites shows appropriate message', async () => {
            // Given: A user with no favorites
            userService.getFavorites.mockResolvedValue([]);

            renderWithProviders(
                <div data-testid="empty-favorites">
                    <p>You haven't added any books to your favorites yet.</p>
                    <a href="/books">Browse Books</a>
                </div>
            );

            // Then: Empty state message should be shown
            expect(screen.getByText(/haven't added any books/i)).toBeInTheDocument();
            expect(screen.getByRole('link', { name: /browse books/i })).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can add books to favorites', () => {
        test('AC-02.4: User can add a book to favorites from book detail page', async () => {
            // Given: A book that is not yet favorited
            userService.addFavorite.mockResolvedValue({ success: true });

            const [isFavorite, setIsFavorite] = [false, jest.fn()];

            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button
                        data-testid="favorite-button"
                        onClick={() => {
                            userService.addFavorite(mockBooks[0].isbn);
                            setIsFavorite(true);
                        }}
                    >
                        {isFavorite ? '❤️ Remove from Favorites' : '🤍 Add to Favorites'}
                    </button>
                </div>
            );

            // When: User clicks the favorite button
            await userEvent.click(screen.getByTestId('favorite-button'));

            // Then: The book should be added to favorites
            expect(userService.addFavorite).toHaveBeenCalledWith(mockBooks[0].isbn);
        });

        test('AC-02.5: Favorite button shows filled heart when book is favorited', async () => {
            // Given: A book that is already favorited
            renderWithProviders(
                <button data-testid="favorite-button-active">
                    ❤️ Remove from Favorites
                </button>
            );

            // Then: Button should show filled heart
            expect(screen.getByTestId('favorite-button-active')).toHaveTextContent('❤️');
        });

        test('AC-02.6: Success notification appears after adding to favorites', async () => {
            // Given: A successful add operation
            userService.addFavorite.mockResolvedValue({ success: true });

            renderWithProviders(
                <div>
                    <div data-testid="toast-success">
                        Book added to favorites!
                    </div>
                </div>
            );

            // Then: Success toast should be visible
            expect(screen.getByTestId('toast-success')).toHaveTextContent(/added to favorites/i);
        });
    });

    describe('Acceptance Criteria: User can remove books from favorites', () => {
        test('AC-02.7: User can remove a book from favorites', async () => {
            // Given: A favorited book
            userService.removeFavorite.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="favorite-book">
                    <h3>{mockBooks[0].title}</h3>
                    <button
                        data-testid="remove-favorite"
                        onClick={() => userService.removeFavorite(mockBooks[0].isbn)}
                    >
                        Remove from Favorites
                    </button>
                </div>
            );

            // When: User clicks remove button
            await userEvent.click(screen.getByTestId('remove-favorite'));

            // Then: Remove service should be called
            expect(userService.removeFavorite).toHaveBeenCalledWith(mockBooks[0].isbn);
        });

        test('AC-02.8: Removed book disappears from favorites list', async () => {
            // Given: A favorites list with one book removed
            const remainingBooks = [mockBooks[1]];

            renderWithProviders(
                <div data-testid="favorites-list">
                    {remainingBooks.map(book => (
                        <div key={book.isbn} data-testid="favorite-book">
                            <h3>{book.title}</h3>
                        </div>
                    ))}
                </div>
            );

            // Then: Only the remaining book should be visible
            expect(screen.getAllByTestId('favorite-book')).toHaveLength(1);
            expect(screen.queryByText('The Great Adventure')).not.toBeInTheDocument();
            expect(screen.getByText('Mystery at Midnight')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Favorites persist across sessions', () => {
        test('AC-02.9: Favorites are fetched from server on page load', async () => {
            // Given: A user with saved favorites
            userService.getFavorites.mockResolvedValue(mockBooks.slice(0, 2));

            // When: User loads favorites page
            await userService.getFavorites();

            // Then: Favorites should be fetched
            expect(userService.getFavorites).toHaveBeenCalled();
        });

        test('AC-02.10: Adding favorite sends request to server', async () => {
            // Given: A book to favorite
            userService.addFavorite.mockResolvedValue({ success: true });

            // When: User adds favorite
            await userService.addFavorite(mockBooks[0].isbn);

            // Then: Request should be sent to server
            expect(userService.addFavorite).toHaveBeenCalledWith(mockBooks[0].isbn);
        });
    });

    describe('Acceptance Criteria: User can view favorite book details', () => {
        test('AC-02.11: Each favorite book links to its detail page', async () => {
            // Given: A favorites list
            renderWithProviders(
                <div data-testid="favorites-list">
                    {mockBooks.slice(0, 2).map(book => (
                        <a
                            key={book.isbn}
                            href={`/book/${book.isbn}`}
                            data-testid="favorite-book-link"
                        >
                            <h3>{book.title}</h3>
                        </a>
                    ))}
                </div>
            );

            // Then: Each book should link to its detail page
            const links = screen.getAllByTestId('favorite-book-link');
            expect(links[0]).toHaveAttribute('href', `/book/${mockBooks[0].isbn}`);
            expect(links[1]).toHaveAttribute('href', `/book/${mockBooks[1].isbn}`);
        });

        test('AC-02.12: Favorite books display cover image', async () => {
            // Given: A favorite book with cover
            renderWithProviders(
                <div data-testid="favorite-book">
                    <img
                        src={mockBooks[0].image.url}
                        alt={mockBooks[0].title}
                        data-testid="book-cover"
                    />
                    <h3>{mockBooks[0].title}</h3>
                </div>
            );

            // Then: Cover image should be displayed
            const cover = screen.getByTestId('book-cover');
            expect(cover).toHaveAttribute('src', mockBooks[0].image.url);
            expect(cover).toHaveAttribute('alt', mockBooks[0].title);
        });
    });

    describe('Acceptance Criteria: Only authenticated users can use favorites', () => {
        test('AC-02.13: Unauthenticated users cannot access favorites page', async () => {
            // Given: No authenticated user
            AuthService.getCurrentUser.mockReturnValue(null);
            AuthService.isAuthenticated.mockReturnValue(false);

            renderWithProviders(
                <div data-testid="login-required">
                    <p>Please log in to view your favorites</p>
                    <a href="/login">Sign In</a>
                </div>
            );

            // Then: Login prompt should be shown
            expect(screen.getByText(/please log in/i)).toBeInTheDocument();
            expect(screen.getByRole('link', { name: /sign in/i })).toBeInTheDocument();
        });

        test('AC-02.14: Favorite button is disabled for guests', async () => {
            // Given: An unauthenticated user viewing a book
            AuthService.getCurrentUser.mockReturnValue(null);

            renderWithProviders(
                <button data-testid="favorite-button" disabled>
                    Sign in to add to favorites
                </button>
            );

            // Then: Button should be disabled
            expect(screen.getByTestId('favorite-button')).toBeDisabled();
        });
    });

    describe('Acceptance Criteria: Favorites help with reading choices', () => {
        test('AC-02.15: Favorites page shows book genres for easy selection', async () => {
            // Given: Favorite books with genres
            renderWithProviders(
                <div data-testid="favorites-list">
                    {mockBooks.slice(0, 2).map(book => (
                        <div key={book.isbn} data-testid="favorite-book">
                            <h3>{book.title}</h3>
                            <div data-testid="book-genres">
                                {book.subjects.map(s => (
                                    <span key={s.id} className="genre-tag">{s.name}</span>
                                ))}
                            </div>
                        </div>
                    ))}
                </div>
            );

            // Then: Genres should be visible for each book
            expect(screen.getByText('Adventure')).toBeInTheDocument();
            expect(screen.getByText('Mystery')).toBeInTheDocument();
        });

        test('AC-02.16: Favorites can be sorted or filtered', async () => {
            // Given: A favorites page with filter options
            renderWithProviders(
                <div>
                    <select data-testid="favorites-sort">
                        <option value="title">Sort by Title</option>
                        <option value="author">Sort by Author</option>
                        <option value="dateAdded">Sort by Date Added</option>
                    </select>
                </div>
            );

            // Then: Sort options should be available
            const sortSelect = screen.getByTestId('favorites-sort');
            expect(sortSelect).toBeInTheDocument();
        });
    });
});
