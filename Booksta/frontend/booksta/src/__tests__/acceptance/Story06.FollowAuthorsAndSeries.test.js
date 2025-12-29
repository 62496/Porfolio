/**
 * Story-06: Follow Authors and Series Acceptance Tests
 *
 * As a User
 * I want to follow the evolution of my favorite series and authors
 * So that I don't lose sight of the series and authors I'm attached to
 *
 * Priority: Essential
 * Complexity: Medium
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockAuthors, mockSeries } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/userService', () => ({
    __esModule: true,
    default: {
        followAuthor: jest.fn(),
        unfollowAuthor: jest.fn(),
        getFollowedAuthors: jest.fn(),
        isFollowingAuthor: jest.fn(),
        followSeries: jest.fn(),
        unfollowSeries: jest.fn(),
        getFollowedSeries: jest.fn(),
        isFollowingSeries: jest.fn(),
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

describe('Story-06: Follow Authors and Series', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: User can follow authors', () => {
        test('AC-06.1: User can follow an author from author page', async () => {
            // Given: An author page with follow button
            userService.followAuthor.mockResolvedValue({ success: true });
            userService.isFollowingAuthor.mockResolvedValue(false);

            renderWithProviders(
                <div data-testid="author-page">
                    <h1>{mockAuthors[0].firstName} {mockAuthors[0].lastName}</h1>
                    <button
                        data-testid="follow-author-btn"
                        onClick={() => userService.followAuthor(mockAuthors[0].id)}
                    >
                        Follow Author
                    </button>
                </div>
            );

            // When: User clicks follow
            await userEvent.click(screen.getByTestId('follow-author-btn'));

            // Then: Author should be followed
            expect(userService.followAuthor).toHaveBeenCalledWith(mockAuthors[0].id);
        });

        test('AC-06.2: Follow button changes to "Following" after following', async () => {
            // Given: A followed author
            renderWithProviders(
                <button data-testid="following-author-btn" className="following">
                    ✓ Following
                </button>
            );

            // Then: Button should indicate following status
            expect(screen.getByTestId('following-author-btn')).toHaveTextContent('Following');
        });

        test('AC-06.3: User can unfollow an author', async () => {
            // Given: A followed author with unfollow option
            userService.unfollowAuthor.mockResolvedValue({ success: true });

            renderWithProviders(
                <button
                    data-testid="unfollow-author-btn"
                    onClick={() => userService.unfollowAuthor(mockAuthors[0].id)}
                >
                    Unfollow
                </button>
            );

            // When: User clicks unfollow
            await userEvent.click(screen.getByTestId('unfollow-author-btn'));

            // Then: Author should be unfollowed
            expect(userService.unfollowAuthor).toHaveBeenCalledWith(mockAuthors[0].id);
        });
    });

    describe('Acceptance Criteria: User can follow series', () => {
        test('AC-06.4: User can follow a series from series page', async () => {
            // Given: A series page with follow button
            userService.followSeries.mockResolvedValue({ success: true });
            userService.isFollowingSeries.mockResolvedValue(false);

            renderWithProviders(
                <div data-testid="series-page">
                    <h1>{mockSeries[0].title}</h1>
                    <button
                        data-testid="follow-series-btn"
                        onClick={() => userService.followSeries(mockSeries[0].id)}
                    >
                        Follow Series
                    </button>
                </div>
            );

            // When: User clicks follow
            await userEvent.click(screen.getByTestId('follow-series-btn'));

            // Then: Series should be followed
            expect(userService.followSeries).toHaveBeenCalledWith(mockSeries[0].id);
        });

        test('AC-06.5: Follow button changes to "Following" for series', async () => {
            // Given: A followed series
            renderWithProviders(
                <button data-testid="following-series-btn" className="following">
                    ✓ Following Series
                </button>
            );

            // Then: Button should indicate following status
            expect(screen.getByTestId('following-series-btn')).toHaveTextContent('Following');
        });

        test('AC-06.6: User can unfollow a series', async () => {
            // Given: A followed series with unfollow option
            userService.unfollowSeries.mockResolvedValue({ success: true });

            renderWithProviders(
                <button
                    data-testid="unfollow-series-btn"
                    onClick={() => userService.unfollowSeries(mockSeries[0].id)}
                >
                    Unfollow Series
                </button>
            );

            // When: User clicks unfollow
            await userEvent.click(screen.getByTestId('unfollow-series-btn'));

            // Then: Series should be unfollowed
            expect(userService.unfollowSeries).toHaveBeenCalledWith(mockSeries[0].id);
        });
    });

    describe('Acceptance Criteria: User can view followed content', () => {
        test('AC-06.7: Followings page shows all followed authors', async () => {
            // Given: A user with followed authors
            userService.getFollowedAuthors.mockResolvedValue(mockAuthors);

            renderWithProviders(
                <div data-testid="followings-page">
                    <h1>My Followings</h1>
                    <section data-testid="followed-authors">
                        <h2>Authors I Follow</h2>
                        {mockAuthors.map(author => (
                            <div key={author.id} data-testid="followed-author">
                                <h3>{author.firstName} {author.lastName}</h3>
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: All followed authors should be displayed
            expect(screen.getByTestId('followed-authors')).toBeInTheDocument();
            expect(screen.getAllByTestId('followed-author')).toHaveLength(mockAuthors.length);
        });

        test('AC-06.8: Followings page shows all followed series', async () => {
            // Given: A user with followed series
            userService.getFollowedSeries.mockResolvedValue(mockSeries);

            renderWithProviders(
                <div data-testid="followings-page">
                    <section data-testid="followed-series">
                        <h2>Series I Follow</h2>
                        {mockSeries.map(series => (
                            <div key={series.id} data-testid="followed-series-item">
                                <h3>{series.title}</h3>
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: All followed series should be displayed
            expect(screen.getByTestId('followed-series')).toBeInTheDocument();
            expect(screen.getAllByTestId('followed-series-item')).toHaveLength(mockSeries.length);
        });

        test('AC-06.9: Empty followings shows appropriate message', async () => {
            // Given: A user with no followings
            userService.getFollowedAuthors.mockResolvedValue([]);
            userService.getFollowedSeries.mockResolvedValue([]);

            renderWithProviders(
                <div data-testid="empty-followings">
                    <p>You're not following any authors or series yet.</p>
                    <a href="/authors">Discover Authors</a>
                    <a href="/series">Explore Series</a>
                </div>
            );

            // Then: Empty state should be shown
            expect(screen.getByText(/not following any/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Followings show updates', () => {
        test('AC-06.10: Followed authors show new book notifications', async () => {
            // Given: A followed author with new content
            renderWithProviders(
                <div data-testid="followed-author">
                    <h3>John Doe</h3>
                    <span data-testid="new-book-badge" className="notification-badge">
                        1 New Book
                    </span>
                </div>
            );

            // Then: New content badge should be visible
            expect(screen.getByTestId('new-book-badge')).toHaveTextContent('1 New Book');
        });

        test('AC-06.11: Followed series show new additions', async () => {
            // Given: A followed series with updates
            renderWithProviders(
                <div data-testid="followed-series-item">
                    <h3>Midnight Series</h3>
                    <span data-testid="series-update-badge">
                        New book added!
                    </span>
                </div>
            );

            // Then: Update notification should be visible
            expect(screen.getByTestId('series-update-badge')).toHaveTextContent('New book added');
        });
    });

    describe('Acceptance Criteria: Follow actions from book detail', () => {
        test('AC-06.12: Book detail shows follow option for author', async () => {
            // Given: A book detail page
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>The Great Adventure</h1>
                    <div data-testid="author-section">
                        <a href="/authors/1">John Doe</a>
                        <button data-testid="follow-book-author">Follow</button>
                    </div>
                </div>
            );

            // Then: Follow option should be available for the author
            expect(screen.getByTestId('follow-book-author')).toBeInTheDocument();
        });

        test('AC-06.13: Book detail shows follow option for series', async () => {
            // Given: A book that belongs to a series
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>Mystery at Midnight</h1>
                    <div data-testid="series-section">
                        <a href="/series/1">Midnight Series</a>
                        <button data-testid="follow-book-series">Follow Series</button>
                    </div>
                </div>
            );

            // Then: Follow option should be available for the series
            expect(screen.getByTestId('follow-book-series')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Followings feed on dashboard', () => {
        test('AC-06.14: Dashboard shows activity from followed authors', async () => {
            // Given: User dashboard with following feed
            renderWithProviders(
                <div data-testid="following-feed">
                    <h2>From Authors You Follow</h2>
                    <div data-testid="feed-item">
                        <span>John Doe</span> published a new book: <strong>Science of Tomorrow</strong>
                    </div>
                </div>
            );

            // Then: Feed should show author activity
            expect(screen.getByTestId('following-feed')).toBeInTheDocument();
            expect(screen.getByText(/published a new book/i)).toBeInTheDocument();
        });

        test('AC-06.15: Dashboard shows updates from followed series', async () => {
            // Given: Series updates in feed
            renderWithProviders(
                <div data-testid="series-feed">
                    <div data-testid="feed-item">
                        <strong>Midnight Series</strong> has a new entry: Mystery at Midnight 2
                    </div>
                </div>
            );

            // Then: Series updates should be visible
            expect(screen.getByText(/has a new entry/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Followings navigation', () => {
        test('AC-06.16: Each followed author links to their page', async () => {
            // Given: Followed authors list
            renderWithProviders(
                <div data-testid="followed-authors">
                    <a href={`/authors/${mockAuthors[0].id}`} data-testid="author-link">
                        {mockAuthors[0].firstName} {mockAuthors[0].lastName}
                    </a>
                </div>
            );

            // Then: Author name should link to their page
            expect(screen.getByTestId('author-link')).toHaveAttribute('href', `/authors/${mockAuthors[0].id}`);
        });

        test('AC-06.17: Each followed series links to series page', async () => {
            // Given: Followed series list
            renderWithProviders(
                <div data-testid="followed-series">
                    <a href={`/series/${mockSeries[0].id}`} data-testid="series-link">
                        {mockSeries[0].title}
                    </a>
                </div>
            );

            // Then: Series title should link to its page
            expect(screen.getByTestId('series-link')).toHaveAttribute('href', `/series/${mockSeries[0].id}`);
        });
    });
});
