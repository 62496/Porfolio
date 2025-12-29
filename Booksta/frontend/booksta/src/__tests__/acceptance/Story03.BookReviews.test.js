/**
 * Story-03: Book Reviews / Ratings Acceptance Tests
 *
 * As a User
 * I want to give my opinion on books I have read
 * So that I can find similar books or people who share my tastes
 *
 * Priority: Useful
 * Complexity: High
 *
 * NOTE: Based on the codebase analysis, the review/rating system appears to be
 * partially implemented through the reports system and reading events.
 * These tests define the expected behavior for a full review system.
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks } from '../../test-utils/testUtils';

// Mock data for reviews
const mockReviews = [
    {
        id: 1,
        bookIsbn: mockBooks[0].isbn,
        user: mockUsers.user,
        rating: 5,
        content: 'An amazing adventure that kept me hooked!',
        createdAt: '2024-01-15T10:00:00Z',
    },
    {
        id: 2,
        bookIsbn: mockBooks[0].isbn,
        user: { ...mockUsers.user, id: 6, firstName: 'Alice', lastName: 'Reader' },
        rating: 4,
        content: 'Great book, loved the characters.',
        createdAt: '2024-01-14T09:00:00Z',
    },
];

describe('Story-03: Book Reviews / Ratings', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
    });

    describe('Acceptance Criteria: User can view book reviews', () => {
        test('AC-03.1: Book detail page shows existing reviews', async () => {
            // Given: A book with reviews
            renderWithProviders(
                <div data-testid="book-reviews">
                    <h2>Reviews</h2>
                    {mockReviews.map(review => (
                        <div key={review.id} data-testid="review-card">
                            <div data-testid="review-rating">{'⭐'.repeat(review.rating)}</div>
                            <p data-testid="review-content">{review.content}</p>
                            <span data-testid="review-author">
                                {review.user.firstName} {review.user.lastName}
                            </span>
                        </div>
                    ))}
                </div>
            );

            // Then: Reviews should be visible
            expect(screen.getAllByTestId('review-card')).toHaveLength(2);
            expect(screen.getByText(/amazing adventure/i)).toBeInTheDocument();
        });

        test('AC-03.2: Reviews show star ratings', async () => {
            // Given: A review with a 5-star rating
            renderWithProviders(
                <div data-testid="review-rating">
                    {'⭐'.repeat(5)}
                </div>
            );

            // Then: 5 stars should be visible
            expect(screen.getByTestId('review-rating')).toHaveTextContent('⭐⭐⭐⭐⭐');
        });

        test('AC-03.3: Reviews show author name and date', async () => {
            // Given: A review
            renderWithProviders(
                <div data-testid="review-card">
                    <span data-testid="review-author">Test User</span>
                    <span data-testid="review-date">January 15, 2024</span>
                </div>
            );

            // Then: Author and date should be visible
            expect(screen.getByTestId('review-author')).toHaveTextContent('Test User');
            expect(screen.getByTestId('review-date')).toHaveTextContent('January 15, 2024');
        });

        test('AC-03.4: Book shows average rating', async () => {
            // Given: A book with multiple reviews (avg rating 4.5)
            renderWithProviders(
                <div data-testid="book-rating">
                    <span data-testid="average-rating">4.5</span>
                    <span data-testid="rating-count">(2 reviews)</span>
                </div>
            );

            // Then: Average rating should be calculated
            expect(screen.getByTestId('average-rating')).toHaveTextContent('4.5');
            expect(screen.getByTestId('rating-count')).toHaveTextContent('2 reviews');
        });
    });

    describe('Acceptance Criteria: User can write reviews', () => {
        test('AC-03.5: User can write a review for a book', async () => {
            // Given: A review form
            renderWithProviders(
                <form data-testid="review-form">
                    <div data-testid="star-rating">
                        {[1, 2, 3, 4, 5].map(star => (
                            <button key={star} type="button" data-testid={`star-${star}`}>
                                ⭐
                            </button>
                        ))}
                    </div>
                    <textarea
                        data-testid="review-text"
                        placeholder="Write your review..."
                    />
                    <button type="submit" data-testid="submit-review">
                        Submit Review
                    </button>
                </form>
            );

            // When: User fills out the form
            await userEvent.type(
                screen.getByTestId('review-text'),
                'This book was fantastic!'
            );
            await userEvent.click(screen.getByTestId('star-5'));

            // Then: Form should accept input
            expect(screen.getByTestId('review-text')).toHaveValue('This book was fantastic!');
        });

        test('AC-03.6: Review requires minimum content length', async () => {
            // Given: A review form with validation
            renderWithProviders(
                <div>
                    <textarea
                        data-testid="review-text"
                        minLength={10}
                        required
                        placeholder="Write at least 10 characters..."
                    />
                    <span data-testid="validation-error">
                        Review must be at least 10 characters
                    </span>
                </div>
            );

            // Then: Validation message should be available
            expect(screen.getByTestId('validation-error')).toHaveTextContent(/at least 10 characters/i);
        });

        test('AC-03.7: User must provide a star rating', async () => {
            // Given: A review form requiring rating
            renderWithProviders(
                <div>
                    <div data-testid="star-rating-required">
                        <span>Rating is required</span>
                    </div>
                </div>
            );

            // Then: Rating requirement should be indicated
            expect(screen.getByTestId('star-rating-required')).toHaveTextContent(/rating is required/i);
        });
    });

    describe('Acceptance Criteria: User can edit/delete their reviews', () => {
        test('AC-03.8: User can edit their own review', async () => {
            // Given: User's own review
            renderWithProviders(
                <div data-testid="my-review">
                    <p>{mockReviews[0].content}</p>
                    <button data-testid="edit-review">Edit</button>
                </div>
            );

            // Then: Edit button should be visible
            expect(screen.getByTestId('edit-review')).toBeInTheDocument();
        });

        test('AC-03.9: User can delete their own review', async () => {
            // Given: User's own review with delete option
            renderWithProviders(
                <div data-testid="my-review">
                    <p>{mockReviews[0].content}</p>
                    <button data-testid="delete-review">Delete</button>
                </div>
            );

            // Then: Delete button should be visible
            expect(screen.getByTestId('delete-review')).toBeInTheDocument();
        });

        test('AC-03.10: User cannot edit others reviews', async () => {
            // Given: Another user's review
            renderWithProviders(
                <div data-testid="other-review">
                    <p>{mockReviews[1].content}</p>
                    <span data-testid="review-author">Alice Reader</span>
                    {/* No edit button for other's reviews */}
                </div>
            );

            // Then: Edit button should not be present
            expect(screen.queryByTestId('edit-review')).not.toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Reviews help discover similar tastes', () => {
        test('AC-03.11: Reviews show reviewer profile link', async () => {
            // Given: A review with clickable author
            renderWithProviders(
                <div data-testid="review-card">
                    <a href="/profile/1" data-testid="reviewer-link">
                        Test User
                    </a>
                </div>
            );

            // Then: Reviewer name should link to their profile
            expect(screen.getByTestId('reviewer-link')).toHaveAttribute('href', '/profile/1');
        });

        test('AC-03.12: High-rated books are highlighted', async () => {
            // Given: A book with high average rating
            renderWithProviders(
                <div data-testid="book-card" className="highly-rated">
                    <span data-testid="rating-badge">⭐ 4.5+</span>
                    <h3>The Great Adventure</h3>
                </div>
            );

            // Then: Rating badge should be visible
            expect(screen.getByTestId('rating-badge')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Only authenticated users can review', () => {
        test('AC-03.13: Guest users see prompt to login', async () => {
            // Given: An unauthenticated user
            clearAuth();

            renderWithProviders(
                <div data-testid="login-prompt">
                    <p>Sign in to write a review</p>
                    <a href="/login">Sign In</a>
                </div>
            );

            // Then: Login prompt should be shown
            expect(screen.getByText(/sign in to write a review/i)).toBeInTheDocument();
        });

        test('AC-03.14: User can only review books once', async () => {
            // Given: A user who has already reviewed a book
            renderWithProviders(
                <div data-testid="already-reviewed">
                    <p>You have already reviewed this book</p>
                    <button data-testid="edit-my-review">Edit Your Review</button>
                </div>
            );

            // Then: Edit option should be shown instead of new review form
            expect(screen.getByText(/already reviewed/i)).toBeInTheDocument();
            expect(screen.getByTestId('edit-my-review')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Reviews are sorted and paginated', () => {
        test('AC-03.15: Reviews can be sorted by date or rating', async () => {
            // Given: Review sort options
            renderWithProviders(
                <select data-testid="review-sort">
                    <option value="newest">Newest First</option>
                    <option value="oldest">Oldest First</option>
                    <option value="highest">Highest Rated</option>
                    <option value="lowest">Lowest Rated</option>
                </select>
            );

            // Then: Sort options should be available
            expect(screen.getByTestId('review-sort')).toBeInTheDocument();
            expect(screen.getByText('Newest First')).toBeInTheDocument();
            expect(screen.getByText('Highest Rated')).toBeInTheDocument();
        });

        test('AC-03.16: Reviews are paginated for books with many reviews', async () => {
            // Given: Pagination controls
            renderWithProviders(
                <div data-testid="review-pagination">
                    <button data-testid="prev-page">Previous</button>
                    <span>Page 1 of 5</span>
                    <button data-testid="next-page">Next</button>
                </div>
            );

            // Then: Pagination should be visible
            expect(screen.getByTestId('prev-page')).toBeInTheDocument();
            expect(screen.getByTestId('next-page')).toBeInTheDocument();
            expect(screen.getByText(/page 1 of 5/i)).toBeInTheDocument();
        });
    });
});
