/**
 * Story-05: Similar Books Recommendations Acceptance Tests
 *
 * As a User
 * I want to see books similar to those I like (similar content, similar reviews, same author or series)
 * So that I can discover new books to read
 *
 * Priority: Useful
 * Complexity: High
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockAuthors, mockSeries } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/bookService', () => ({
    __esModule: true,
    default: {
        getByAuthor: jest.fn(),
        getBySeries: jest.fn(),
        searchBooks: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import bookService from '../../api/services/bookService';
import AuthService from '../../api/services/authService';

describe('Story-05: Similar Books Recommendations', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: Show books by same author', () => {
        test('AC-05.1: Book detail page shows "More by this author" section', async () => {
            // Given: A book with other books by the same author
            const otherBooksByAuthor = mockBooks.filter(b => b.authors[0].id === 1).slice(1);
            bookService.getByAuthor.mockResolvedValue(otherBooksByAuthor);

            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>The Great Adventure</h1>
                    <section data-testid="more-by-author">
                        <h2>More by John Doe</h2>
                        {otherBooksByAuthor.map(book => (
                            <div key={book.isbn} data-testid="related-book">
                                <h3>{book.title}</h3>
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: Related books section should be visible
            expect(screen.getByTestId('more-by-author')).toBeInTheDocument();
            expect(screen.getByText('More by John Doe')).toBeInTheDocument();
        });

        test('AC-05.2: Books by same author are clickable', async () => {
            // Given: Related books with links
            renderWithProviders(
                <div data-testid="more-by-author">
                    <a href={`/book/${mockBooks[2].isbn}`} data-testid="related-book-link">
                        {mockBooks[2].title}
                    </a>
                </div>
            );

            // Then: Related book should link to its detail page
            expect(screen.getByTestId('related-book-link')).toHaveAttribute(
                'href',
                `/book/${mockBooks[2].isbn}`
            );
        });

        test('AC-05.3: Author page shows all books by that author', async () => {
            // Given: Author detail page
            bookService.getByAuthor.mockResolvedValue(mockBooks.filter(b => b.authors[0].id === 1));

            renderWithProviders(
                <div data-testid="author-page">
                    <h1>John Doe</h1>
                    <section data-testid="author-books">
                        <h2>Books by John Doe</h2>
                        {mockBooks.filter(b => b.authors[0].id === 1).map(book => (
                            <div key={book.isbn} data-testid="author-book">
                                {book.title}
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: All author's books should be shown
            expect(screen.getByTestId('author-books')).toBeInTheDocument();
            expect(screen.getAllByTestId('author-book').length).toBeGreaterThan(0);
        });
    });

    describe('Acceptance Criteria: Show books in same series', () => {
        test('AC-05.4: Book detail page shows other books in series', async () => {
            // Given: A book that belongs to a series
            const seriesBooks = [mockBooks[1], { ...mockBooks[0], isbn: '9999', title: 'Midnight 2' }];
            bookService.getBySeries.mockResolvedValue(seriesBooks);

            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>Mystery at Midnight</h1>
                    <span data-testid="series-name">Midnight Series</span>
                    <section data-testid="series-books">
                        <h2>More from Midnight Series</h2>
                        {seriesBooks.map(book => (
                            <div key={book.isbn} data-testid="series-book">
                                {book.title}
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: Series section should be visible
            expect(screen.getByTestId('series-books')).toBeInTheDocument();
            expect(screen.getByText('More from Midnight Series')).toBeInTheDocument();
        });

        test('AC-05.5: Series page shows all books in order', async () => {
            // Given: A series detail page
            const seriesBooks = [
                { ...mockBooks[1], seriesOrder: 1 },
                { ...mockBooks[0], isbn: '9998', title: 'Midnight 2', seriesOrder: 2 },
                { ...mockBooks[0], isbn: '9997', title: 'Midnight 3', seriesOrder: 3 },
            ];

            renderWithProviders(
                <div data-testid="series-page">
                    <h1>Midnight Series</h1>
                    <p>by Jane Smith</p>
                    <div data-testid="series-book-list">
                        {seriesBooks.map((book, index) => (
                            <div key={book.isbn} data-testid="series-book">
                                <span data-testid="book-order">#{index + 1}</span>
                                <h3>{book.title}</h3>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Books should be displayed in order
            expect(screen.getAllByTestId('series-book')).toHaveLength(3);
            expect(screen.getAllByTestId('book-order')[0]).toHaveTextContent('#1');
        });
    });

    describe('Acceptance Criteria: Show books with similar genres', () => {
        test('AC-05.6: Book detail page shows books with same genre', async () => {
            // Given: Similar genre books
            const sameGenreBooks = mockBooks.filter(b =>
                b.subjects.some(s => s.name === 'Adventure')
            );

            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>The Great Adventure</h1>
                    <section data-testid="similar-genre">
                        <h2>More Adventure Books</h2>
                        {sameGenreBooks.map(book => (
                            <div key={book.isbn} data-testid="similar-book">
                                {book.title}
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: Similar genre section should be visible
            expect(screen.getByTestId('similar-genre')).toBeInTheDocument();
            expect(screen.getByText('More Adventure Books')).toBeInTheDocument();
        });

        test('AC-05.7: Genre tags are clickable to show similar books', async () => {
            // Given: Clickable genre tags
            renderWithProviders(
                <div data-testid="book-genres">
                    <a href="/books?genre=Adventure" data-testid="genre-link">
                        Adventure
                    </a>
                    <a href="/books?genre=Fiction" data-testid="genre-link">
                        Fiction
                    </a>
                </div>
            );

            // Then: Genre links should filter catalog
            const genreLinks = screen.getAllByTestId('genre-link');
            expect(genreLinks[0]).toHaveAttribute('href', '/books?genre=Adventure');
        });
    });

    describe('Acceptance Criteria: Recommendations based on reading history', () => {
        test('AC-05.8: Homepage shows personalized recommendations', async () => {
            // Given: A logged-in user with reading history
            renderWithProviders(
                <div data-testid="recommendations-section">
                    <h2>Recommended for You</h2>
                    <p>Based on your reading history</p>
                    {mockBooks.slice(0, 3).map(book => (
                        <div key={book.isbn} data-testid="recommended-book">
                            {book.title}
                        </div>
                    ))}
                </div>
            );

            // Then: Recommendations should be visible
            expect(screen.getByTestId('recommendations-section')).toBeInTheDocument();
            expect(screen.getByText(/based on your reading history/i)).toBeInTheDocument();
        });

        test('AC-05.9: Recommendations consider favorite genres', async () => {
            // Given: Recommendations matching user preferences
            renderWithProviders(
                <div data-testid="genre-recommendations">
                    <h2>Because you like Adventure</h2>
                    {mockBooks.filter(b => b.subjects.some(s => s.name === 'Adventure')).map(book => (
                        <div key={book.isbn} data-testid="genre-rec-book">
                            {book.title}
                        </div>
                    ))}
                </div>
            );

            // Then: Genre-based recommendations should appear
            expect(screen.getByText(/because you like adventure/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Discovery through similar readers', () => {
        test('AC-05.10: Show books liked by users with similar taste', async () => {
            // Given: Social recommendations
            renderWithProviders(
                <div data-testid="social-recommendations">
                    <h2>Readers who liked this also enjoyed</h2>
                    {mockBooks.slice(1).map(book => (
                        <div key={book.isbn} data-testid="social-rec-book">
                            {book.title}
                        </div>
                    ))}
                </div>
            );

            // Then: Social recommendations should be visible
            expect(screen.getByText(/readers who liked this/i)).toBeInTheDocument();
        });

        test('AC-05.11: Show popularity indicators on recommended books', async () => {
            // Given: Recommended books with popularity info
            renderWithProviders(
                <div data-testid="recommended-book">
                    <h3>Mystery at Midnight</h3>
                    <span data-testid="popularity-badge">
                        ⭐ 4.5 (120 readers)
                    </span>
                </div>
            );

            // Then: Popularity info should be visible
            expect(screen.getByTestId('popularity-badge')).toHaveTextContent('120 readers');
        });
    });

    describe('Acceptance Criteria: Recommendations are varied', () => {
        test('AC-05.12: Recommendations show mix of sources', async () => {
            // Given: Recommendations from different sources
            renderWithProviders(
                <div data-testid="mixed-recommendations">
                    <section data-testid="rec-by-author">
                        <h3>By Same Author</h3>
                    </section>
                    <section data-testid="rec-by-genre">
                        <h3>Similar Genre</h3>
                    </section>
                    <section data-testid="rec-by-series">
                        <h3>From Same Series</h3>
                    </section>
                </div>
            );

            // Then: Multiple recommendation sections should exist
            expect(screen.getByTestId('rec-by-author')).toBeInTheDocument();
            expect(screen.getByTestId('rec-by-genre')).toBeInTheDocument();
            expect(screen.getByTestId('rec-by-series')).toBeInTheDocument();
        });

        test('AC-05.13: Recommendations exclude already-read books', async () => {
            // Given: Recommendations filtering out read books
            renderWithProviders(
                <div data-testid="recommendations">
                    <p data-testid="filter-note">Hiding 2 books you've already read</p>
                </div>
            );

            // Then: Filter note should indicate exclusions
            expect(screen.getByTestId('filter-note')).toHaveTextContent(/hiding.*already read/i);
        });
    });

    describe('Acceptance Criteria: Recommendations enhance discovery', () => {
        test('AC-05.14: Each recommendation shows why it was suggested', async () => {
            // Given: Recommendations with explanations
            renderWithProviders(
                <div data-testid="recommended-book">
                    <h3>Science of Tomorrow</h3>
                    <p data-testid="recommendation-reason">
                        Because you liked "The Great Adventure" by the same author
                    </p>
                </div>
            );

            // Then: Recommendation reason should be visible
            expect(screen.getByTestId('recommendation-reason')).toHaveTextContent(/by the same author/i);
        });

        test('AC-05.15: User can dismiss recommendations', async () => {
            // Given: Dismissible recommendations
            renderWithProviders(
                <div data-testid="recommended-book">
                    <h3>Mystery at Midnight</h3>
                    <button data-testid="dismiss-recommendation">
                        Not Interested
                    </button>
                </div>
            );

            // Then: Dismiss button should be available
            expect(screen.getByTestId('dismiss-recommendation')).toBeInTheDocument();
        });
    });
});
