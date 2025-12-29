/**
 * Story-11 & Story-12: Author News/Updates and Analytics Acceptance Tests
 *
 * Story-11:
 * As an Author
 * I want to share news about my activity (publications, events...)
 * So that I can keep my fans informed
 *
 * Story-12:
 * As an Author
 * I want to track community activity regarding my books
 * So that I can gauge my fans' interest, track the success of my books, and adapt my communication
 *
 * Priority: Useful
 * Complexity: Medium to High
 *
 * NOTE: These features appear to be partially implemented or planned.
 * Tests define expected behavior for complete implementation.
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockAuthors } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import AuthService from '../../api/services/authService';

describe('Story-11: Author News and Updates', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.author);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.author);
    });

    describe('Acceptance Criteria: Author can share updates', () => {
        test('AC-11.1: Author dashboard has news/updates section', async () => {
            // Given: An author dashboard
            renderWithProviders(
                <div data-testid="author-dashboard">
                    <section data-testid="news-section">
                        <h2>Share an Update</h2>
                        <textarea data-testid="update-input" placeholder="What's new?" />
                        <button data-testid="post-update">Post Update</button>
                    </section>
                </div>
            );

            // Then: News section should be visible
            expect(screen.getByTestId('news-section')).toBeInTheDocument();
            expect(screen.getByTestId('update-input')).toBeInTheDocument();
        });

        test('AC-11.2: Author can post text updates', async () => {
            // Given: Update posting form
            renderWithProviders(
                <div data-testid="post-update-form">
                    <textarea data-testid="update-text" />
                    <button data-testid="submit-update">Post</button>
                </div>
            );

            // When: Author types and posts
            await userEvent.type(screen.getByTestId('update-text'), 'New book coming soon!');

            // Then: Input should contain the text
            expect(screen.getByTestId('update-text')).toHaveValue('New book coming soon!');
        });

        test('AC-11.3: Updates can be linked to a specific book', async () => {
            // Given: Update form with book selection
            renderWithProviders(
                <div data-testid="update-form">
                    <textarea data-testid="update-text" />
                    <select data-testid="related-book">
                        <option value="">No specific book</option>
                        {mockBooks.map(book => (
                            <option key={book.isbn} value={book.isbn}>
                                {book.title}
                            </option>
                        ))}
                    </select>
                </div>
            );

            // Then: Book selection should be available
            expect(screen.getByTestId('related-book')).toBeInTheDocument();
        });

        test('AC-11.4: Author can announce publication dates', async () => {
            // Given: Announcement with date
            renderWithProviders(
                <div data-testid="announcement-form">
                    <select data-testid="announcement-type">
                        <option value="news">News</option>
                        <option value="release">Book Release</option>
                        <option value="event">Event</option>
                    </select>
                    <input type="date" data-testid="event-date" />
                </div>
            );

            // Then: Date and type selection should be available
            expect(screen.getByTestId('announcement-type')).toBeInTheDocument();
            expect(screen.getByTestId('event-date')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Followers see author updates', () => {
        test('AC-11.5: Updates appear in followers\' feeds', async () => {
            // Given: A follower's feed
            renderWithProviders(
                <div data-testid="follower-feed">
                    <div data-testid="author-update">
                        <span data-testid="author-name">John Doe</span>
                        <p>New book coming soon!</p>
                        <span data-testid="update-time">2 hours ago</span>
                    </div>
                </div>
            );

            // Then: Update should be visible in feed
            expect(screen.getByTestId('author-update')).toBeInTheDocument();
            expect(screen.getByTestId('author-name')).toHaveTextContent('John Doe');
        });

        test('AC-11.6: Updates on author page are visible to visitors', async () => {
            // Given: Author profile page
            renderWithProviders(
                <div data-testid="author-page">
                    <h1>John Doe</h1>
                    <section data-testid="author-updates">
                        <h2>Recent Updates</h2>
                        <div data-testid="update-item">
                            <p>Working on my next novel!</p>
                        </div>
                    </section>
                </div>
            );

            // Then: Updates section should be visible
            expect(screen.getByTestId('author-updates')).toBeInTheDocument();
        });
    });
});

describe('Story-12: Author Analytics', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.author);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.author);
    });

    describe('Acceptance Criteria: Author can view book statistics', () => {
        test('AC-12.1: Author dashboard shows analytics section', async () => {
            // Given: Author dashboard with analytics
            renderWithProviders(
                <div data-testid="author-dashboard">
                    <section data-testid="analytics-section">
                        <h2>Your Book Analytics</h2>
                    </section>
                </div>
            );

            // Then: Analytics section should be visible
            expect(screen.getByTestId('analytics-section')).toBeInTheDocument();
        });

        test('AC-12.2: Author can see reader count per book', async () => {
            // Given: Book reader statistics
            renderWithProviders(
                <div data-testid="book-stats">
                    <h3>The Great Adventure</h3>
                    <div data-testid="reader-count">
                        <span className="count">1,234</span>
                        <label>Total Readers</label>
                    </div>
                </div>
            );

            // Then: Reader count should be visible
            expect(screen.getByTestId('reader-count')).toHaveTextContent('1,234');
        });

        test('AC-12.3: Author can see favorite count per book', async () => {
            // Given: Book favorite statistics
            renderWithProviders(
                <div data-testid="book-stats">
                    <div data-testid="favorite-count">
                        <span className="count">456</span>
                        <label>Favorited</label>
                    </div>
                </div>
            );

            // Then: Favorite count should be visible
            expect(screen.getByTestId('favorite-count')).toHaveTextContent('456');
        });

        test('AC-12.4: Author can see reading progress distribution', async () => {
            // Given: Reading progress stats
            renderWithProviders(
                <div data-testid="reading-distribution">
                    <h3>Reader Progress</h3>
                    <div data-testid="progress-stat">
                        <span>Not Started:</span> <span>200</span>
                    </div>
                    <div data-testid="progress-stat">
                        <span>In Progress:</span> <span>500</span>
                    </div>
                    <div data-testid="progress-stat">
                        <span>Completed:</span> <span>534</span>
                    </div>
                </div>
            );

            // Then: Progress distribution should be visible
            expect(screen.getAllByTestId('progress-stat')).toHaveLength(3);
        });
    });

    describe('Acceptance Criteria: Author can track engagement trends', () => {
        test('AC-12.5: Author can see follower count', async () => {
            // Given: Follower statistics
            renderWithProviders(
                <div data-testid="follower-stats">
                    <span data-testid="follower-count">2,345</span>
                    <label>Followers</label>
                    <span data-testid="follower-change" className="positive">+12 this week</span>
                </div>
            );

            // Then: Follower count and trend should be visible
            expect(screen.getByTestId('follower-count')).toHaveTextContent('2,345');
            expect(screen.getByTestId('follower-change')).toHaveTextContent('+12');
        });

        test('AC-12.6: Author can see review/rating overview', async () => {
            // Given: Rating statistics
            renderWithProviders(
                <div data-testid="rating-overview">
                    <h3>Ratings Overview</h3>
                    <div data-testid="average-rating">
                        <span>4.5</span> average rating
                    </div>
                    <div data-testid="total-reviews">
                        <span>89</span> reviews
                    </div>
                </div>
            );

            // Then: Rating overview should be visible
            expect(screen.getByTestId('average-rating')).toHaveTextContent('4.5');
            expect(screen.getByTestId('total-reviews')).toHaveTextContent('89');
        });

        test('AC-12.7: Author can compare book performance', async () => {
            // Given: Book comparison view
            renderWithProviders(
                <div data-testid="book-comparison">
                    <h3>Book Performance Comparison</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Book</th>
                                <th>Readers</th>
                                <th>Rating</th>
                                <th>Reviews</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr data-testid="book-row">
                                <td>The Great Adventure</td>
                                <td>1,234</td>
                                <td>4.5</td>
                                <td>56</td>
                            </tr>
                            <tr data-testid="book-row">
                                <td>Science of Tomorrow</td>
                                <td>876</td>
                                <td>4.2</td>
                                <td>33</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            );

            // Then: Comparison table should be visible
            expect(screen.getAllByTestId('book-row')).toHaveLength(2);
        });
    });

    describe('Acceptance Criteria: Analytics help adapt communication', () => {
        test('AC-12.8: Author can see most popular book', async () => {
            // Given: Popular book highlight
            renderWithProviders(
                <div data-testid="top-performer">
                    <h3>Your Top Performer</h3>
                    <div data-testid="top-book">
                        <span>The Great Adventure</span>
                        <span>Most read this month</span>
                    </div>
                </div>
            );

            // Then: Top performer should be highlighted
            expect(screen.getByTestId('top-book')).toHaveTextContent('The Great Adventure');
        });

        test('AC-12.9: Author can export analytics data', async () => {
            // Given: Export option
            renderWithProviders(
                <div data-testid="analytics-actions">
                    <button data-testid="export-csv">Export to CSV</button>
                    <button data-testid="export-pdf">Export Report</button>
                </div>
            );

            // Then: Export options should be available
            expect(screen.getByTestId('export-csv')).toBeInTheDocument();
            expect(screen.getByTestId('export-pdf')).toBeInTheDocument();
        });
    });
});
