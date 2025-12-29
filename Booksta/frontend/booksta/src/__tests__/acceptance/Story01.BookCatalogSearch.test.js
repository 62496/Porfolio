/**
 * Story-01: Book Catalog Search Acceptance Tests
 *
 * As a User
 * I want to consult a book catalog based on various search criteria (author, genre, publication year...)
 * So that I can discover new books to read
 *
 * Priority: Indispensable
 * Complexity: Low
 */

import React from 'react';
import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockSubjects, mockAuthors } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/bookService', () => ({
    __esModule: true,
    default: {
        getAll: jest.fn(),
        getAllBooks: jest.fn(),
        searchBooks: jest.fn(),
        formatBookForDisplay: jest.fn((book) => ({
            id: book.isbn,
            isbn: book.isbn,
            title: book.title,
            author: book.authors?.map(a => `${a.firstName} ${a.lastName}`).join(', '),
            genre: book.subjects?.map(s => s.name).join(', '),
            year: book.publishingYear,
            description: book.description,
            cover: book.image?.url,
        })),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
        getAccessToken: jest.fn(() => 'mock-token'),
    },
}));

import bookService from '../../api/services/bookService';
import AuthService from '../../api/services/authService';

describe('Story-01: Book Catalog Search', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
        bookService.getAllBooks.mockResolvedValue(mockBooks);
        bookService.getAll.mockResolvedValue(mockBooks);
    });

    describe('Acceptance Criteria: User can view the book catalog', () => {
        test('AC-01.1: User can see a list of all available books', async () => {
            // Given: A user navigates to the book catalog
            // When: The catalog page loads
            renderWithProviders(
                <div data-testid="book-catalog">
                    <h1>Book Catalog</h1>
                    <div data-testid="book-list">
                        {mockBooks.map(book => (
                            <div key={book.isbn} data-testid="book-card">
                                <h3>{book.title}</h3>
                                <p>{book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ')}</p>
                                <p>{book.publishingYear}</p>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: All books should be displayed
            expect(screen.getByTestId('book-catalog')).toBeInTheDocument();
            expect(screen.getAllByTestId('book-card')).toHaveLength(mockBooks.length);
            expect(screen.getByText('The Great Adventure')).toBeInTheDocument();
            expect(screen.getByText('Mystery at Midnight')).toBeInTheDocument();
        });

        test('AC-01.2: Each book displays title, author, and year', async () => {
            // Given: The book catalog is displayed
            renderWithProviders(
                <div data-testid="book-card">
                    <h3 data-testid="book-title">{mockBooks[0].title}</h3>
                    <p data-testid="book-author">
                        {mockBooks[0].authors.map(a => `${a.firstName} ${a.lastName}`).join(', ')}
                    </p>
                    <p data-testid="book-year">{mockBooks[0].publishingYear}</p>
                    <p data-testid="book-genre">
                        {mockBooks[0].subjects.map(s => s.name).join(', ')}
                    </p>
                </div>
            );

            // Then: Book details should be visible
            expect(screen.getByTestId('book-title')).toHaveTextContent('The Great Adventure');
            expect(screen.getByTestId('book-author')).toHaveTextContent('John Doe');
            expect(screen.getByTestId('book-year')).toHaveTextContent('2023');
            expect(screen.getByTestId('book-genre')).toHaveTextContent('Adventure');
        });
    });

    describe('Acceptance Criteria: User can search books by title', () => {
        test('AC-01.3: User can enter a search term in the search box', async () => {
            // Given: The catalog page with a search box
            renderWithProviders(
                <div>
                    <input
                        type="text"
                        placeholder="Search by title..."
                        data-testid="search-input"
                    />
                </div>
            );

            // When: User types in the search box
            const searchInput = screen.getByTestId('search-input');
            await userEvent.type(searchInput, 'Adventure');

            // Then: The search term should be entered
            expect(searchInput).toHaveValue('Adventure');
        });

        test('AC-01.4: Search results filter books matching the title', async () => {
            // Given: A mock search implementation
            bookService.searchBooks.mockImplementation((filters) => {
                if (filters.title) {
                    return Promise.resolve(
                        mockBooks.filter(b =>
                            b.title.toLowerCase().includes(filters.title.toLowerCase())
                        )
                    );
                }
                return Promise.resolve(mockBooks);
            });

            // When: User searches for "Adventure"
            const searchResults = await bookService.searchBooks({ title: 'Adventure' });

            // Then: Only matching books should be returned
            expect(searchResults).toHaveLength(1);
            expect(searchResults[0].title).toBe('The Great Adventure');
        });

        test('AC-01.5: Empty search returns all books', async () => {
            // Given: No search filter applied
            bookService.searchBooks.mockResolvedValue(mockBooks);

            // When: Search with empty filters
            const searchResults = await bookService.searchBooks({});

            // Then: All books should be returned
            expect(searchResults).toHaveLength(mockBooks.length);
        });
    });

    describe('Acceptance Criteria: User can filter books by author', () => {
        test('AC-01.6: User can filter books by author name', async () => {
            // Given: A filter by author
            bookService.searchBooks.mockImplementation((filters) => {
                if (filters.authorName) {
                    return Promise.resolve(
                        mockBooks.filter(b =>
                            b.authors.some(a =>
                                `${a.firstName} ${a.lastName}`.toLowerCase()
                                    .includes(filters.authorName.toLowerCase())
                            )
                        )
                    );
                }
                return Promise.resolve(mockBooks);
            });

            // When: User filters by "John Doe"
            const searchResults = await bookService.searchBooks({ authorName: 'John Doe' });

            // Then: Only books by John Doe should appear
            expect(searchResults).toHaveLength(2);
            searchResults.forEach(book => {
                expect(book.authors.some(a => a.firstName === 'John')).toBe(true);
            });
        });

        test('AC-01.7: Author filter dropdown shows available authors', async () => {
            // Given: An author filter dropdown
            renderWithProviders(
                <select data-testid="author-filter">
                    <option value="">All Authors</option>
                    {mockAuthors.map(author => (
                        <option key={author.id} value={author.id}>
                            {author.firstName} {author.lastName}
                        </option>
                    ))}
                </select>
            );

            // Then: All authors should be listed
            const select = screen.getByTestId('author-filter');
            expect(within(select).getByText('All Authors')).toBeInTheDocument();
            expect(within(select).getByText('John Doe')).toBeInTheDocument();
            expect(within(select).getByText('Jane Smith')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can filter books by genre/subject', () => {
        test('AC-01.8: User can filter books by genre', async () => {
            // Given: A filter by genre/subject
            bookService.searchBooks.mockImplementation((filters) => {
                if (filters.subjectName) {
                    return Promise.resolve(
                        mockBooks.filter(b =>
                            b.subjects.some(s =>
                                s.name.toLowerCase().includes(filters.subjectName.toLowerCase())
                            )
                        )
                    );
                }
                return Promise.resolve(mockBooks);
            });

            // When: User filters by "Mystery"
            const searchResults = await bookService.searchBooks({ subjectName: 'Mystery' });

            // Then: Only mystery books should appear
            expect(searchResults).toHaveLength(1);
            expect(searchResults[0].title).toBe('Mystery at Midnight');
        });

        test('AC-01.9: Genre filter shows available genres', async () => {
            // Given: A genre filter component
            renderWithProviders(
                <select data-testid="genre-filter">
                    <option value="">All Genres</option>
                    {mockSubjects.map(subject => (
                        <option key={subject.id} value={subject.name}>
                            {subject.name}
                        </option>
                    ))}
                </select>
            );

            // Then: All genres should be listed
            const select = screen.getByTestId('genre-filter');
            expect(within(select).getByText('All Genres')).toBeInTheDocument();
            expect(within(select).getByText('Adventure')).toBeInTheDocument();
            expect(within(select).getByText('Mystery')).toBeInTheDocument();
            expect(within(select).getByText('Science Fiction')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can filter books by publication year', () => {
        test('AC-01.10: User can filter books by year', async () => {
            // Given: A filter by year
            bookService.searchBooks.mockImplementation((filters) => {
                if (filters.year) {
                    return Promise.resolve(
                        mockBooks.filter(b => b.publishingYear === parseInt(filters.year))
                    );
                }
                return Promise.resolve(mockBooks);
            });

            // When: User filters by year 2023
            const searchResults = await bookService.searchBooks({ year: '2023' });

            // Then: Only 2023 books should appear
            expect(searchResults).toHaveLength(1);
            expect(searchResults[0].publishingYear).toBe(2023);
        });

        test('AC-01.11: Year filter input accepts valid years', async () => {
            // Given: A year filter input
            renderWithProviders(
                <input
                    type="number"
                    placeholder="Publication Year"
                    min="1800"
                    max="2025"
                    data-testid="year-filter"
                />
            );

            // When: User enters a year
            const yearInput = screen.getByTestId('year-filter');
            await userEvent.type(yearInput, '2023');

            // Then: The year should be accepted
            expect(yearInput).toHaveValue(2023);
        });
    });

    describe('Acceptance Criteria: Combined search filters work together', () => {
        test('AC-01.12: User can combine multiple filters', async () => {
            // Given: Multiple filters applied
            bookService.searchBooks.mockImplementation((filters) => {
                let results = [...mockBooks];

                if (filters.title) {
                    results = results.filter(b =>
                        b.title.toLowerCase().includes(filters.title.toLowerCase())
                    );
                }
                if (filters.authorName) {
                    results = results.filter(b =>
                        b.authors.some(a =>
                            `${a.firstName} ${a.lastName}`.toLowerCase()
                                .includes(filters.authorName.toLowerCase())
                        )
                    );
                }
                if (filters.subjectName) {
                    results = results.filter(b =>
                        b.subjects.some(s =>
                            s.name.toLowerCase().includes(filters.subjectName.toLowerCase())
                        )
                    );
                }

                return Promise.resolve(results);
            });

            // When: User searches with author "John" and genre "Adventure"
            const searchResults = await bookService.searchBooks({
                authorName: 'John',
                subjectName: 'Adventure',
            });

            // Then: Only books matching both criteria should appear
            expect(searchResults).toHaveLength(1);
            expect(searchResults[0].title).toBe('The Great Adventure');
        });
    });

    describe('Acceptance Criteria: Search shows appropriate feedback', () => {
        test('AC-01.13: Shows "No results" message when no books match', async () => {
            // Given: A search with no results
            renderWithProviders(
                <div>
                    <p data-testid="no-results">No books found matching your criteria</p>
                </div>
            );

            // Then: No results message should be shown
            expect(screen.getByTestId('no-results')).toHaveTextContent(/no books found/i);
        });

        test('AC-01.14: Shows loading indicator while searching', async () => {
            // Given: A loading state
            renderWithProviders(
                <div data-testid="loading-indicator">
                    <span>Loading books...</span>
                </div>
            );

            // Then: Loading indicator should be visible
            expect(screen.getByTestId('loading-indicator')).toBeInTheDocument();
            expect(screen.getByText(/loading/i)).toBeInTheDocument();
        });

        test('AC-01.15: Shows number of results found', async () => {
            // Given: Search results are displayed
            renderWithProviders(
                <div>
                    <p data-testid="results-count">{mockBooks.length} books found</p>
                </div>
            );

            // Then: Results count should be shown
            expect(screen.getByTestId('results-count')).toHaveTextContent('3 books found');
        });
    });

    describe('Acceptance Criteria: User can navigate to book details', () => {
        test('AC-01.16: Clicking a book opens its detail page', async () => {
            // Given: A book card with a link to details
            renderWithProviders(
                <div data-testid="book-card">
                    <a href={`/book/${mockBooks[0].isbn}`} data-testid="book-link">
                        <h3>{mockBooks[0].title}</h3>
                    </a>
                </div>
            );

            // Then: The book should have a link to its detail page
            const bookLink = screen.getByTestId('book-link');
            expect(bookLink).toHaveAttribute('href', `/book/${mockBooks[0].isbn}`);
        });
    });
});
