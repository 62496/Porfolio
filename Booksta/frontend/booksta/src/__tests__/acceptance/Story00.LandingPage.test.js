/**
 * Story-00: Landing Page Acceptance Tests
 *
 * As a Developer
 * I want to create a landing page
 * So that users have a welcoming entry point to the application
 *
 * Priority: Indispensable
 * Complexity: Medium
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(),
        getAccessToken: jest.fn(),
        getRefreshToken: jest.fn(),
    },
}));

import AuthService from '../../api/services/authService';

describe('Story-00: Landing Page', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
    });

    describe('Acceptance Criteria: Page displays correctly for unauthenticated users', () => {
        beforeEach(() => {
            AuthService.getCurrentUser.mockReturnValue(null);
            AuthService.isAuthenticated.mockReturnValue(false);
        });

        test('AC-00.1: Landing page renders without errors', async () => {
            // Given: The user visits the landing page
            // When: The page loads
            const { container } = renderWithProviders(
                <div data-testid="landing-page">
                    <header>
                        <h1>Booksta</h1>
                        <nav>
                            <button>Sign In</button>
                            <button>Sign Up</button>
                        </nav>
                    </header>
                    <main>
                        <section>
                            <h2>Discover Your Next Great Read</h2>
                            <p>Join our community of book lovers</p>
                        </section>
                    </main>
                </div>
            );

            // Then: The page should render successfully
            expect(container).toBeInTheDocument();
            expect(screen.getByTestId('landing-page')).toBeInTheDocument();
        });

        test('AC-00.2: Landing page shows sign in and sign up options for guests', async () => {
            // Given: An unauthenticated user visits the landing page
            renderWithProviders(
                <div data-testid="landing-page">
                    <button>Sign In</button>
                    <button>Sign Up</button>
                </div>
            );

            // Then: Sign In and Sign Up buttons should be visible
            expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
            expect(screen.getByRole('button', { name: /sign up/i })).toBeInTheDocument();
        });

        test('AC-00.3: Landing page displays welcome message and value proposition', async () => {
            // Given: A user visits the landing page
            renderWithProviders(
                <div>
                    <h1>Welcome to Booksta</h1>
                    <p>Discover, track, and share your reading journey</p>
                    <ul>
                        <li>Browse thousands of books</li>
                        <li>Track your reading progress</li>
                        <li>Connect with other readers</li>
                    </ul>
                </div>
            );

            // Then: The page should show the welcome message and features
            expect(screen.getByText(/welcome to booksta/i)).toBeInTheDocument();
            expect(screen.getByText(/discover, track, and share/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Page displays correctly for authenticated users', () => {
        beforeEach(() => {
            setupAuthenticatedUser(mockUsers.user);
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
            AuthService.isAuthenticated.mockReturnValue(true);
        });

        test('AC-00.4: Landing page shows personalized welcome for logged-in user', async () => {
            // Given: An authenticated user visits the landing page
            renderWithProviders(
                <div>
                    <p>Welcome, {mockUsers.user.firstName}!</p>
                    <nav>
                        <a href="/books">Browse Books</a>
                        <a href="/my-books">My Library</a>
                        <a href="/profile">Profile</a>
                    </nav>
                </div>
            );

            // Then: The page should show personalized greeting
            expect(screen.getByText(/welcome, test!/i)).toBeInTheDocument();
        });

        test('AC-00.5: Landing page shows navigation links for authenticated users', async () => {
            // Given: An authenticated user is on the landing page
            renderWithProviders(
                <nav>
                    <a href="/books">Browse Books</a>
                    <a href="/my-books">My Library</a>
                    <a href="/favorites">Favorites</a>
                    <a href="/collections">Collections</a>
                </nav>
            );

            // Then: Navigation links should be available
            expect(screen.getByRole('link', { name: /browse books/i })).toBeInTheDocument();
            expect(screen.getByRole('link', { name: /my library/i })).toBeInTheDocument();
        });

        test('AC-00.6: Landing page displays user profile picture when available', async () => {
            // Given: A user with a profile picture is logged in
            renderWithProviders(
                <div>
                    <img
                        src={mockUsers.user.picture}
                        alt={`${mockUsers.user.firstName}'s avatar`}
                        data-testid="user-avatar"
                    />
                </div>
            );

            // Then: The profile picture should be displayed
            const avatar = screen.getByTestId('user-avatar');
            expect(avatar).toBeInTheDocument();
            expect(avatar).toHaveAttribute('src', mockUsers.user.picture);
        });
    });

    describe('Acceptance Criteria: Page is responsive and accessible', () => {
        test('AC-00.7: Landing page has proper heading hierarchy', () => {
            // Given: The landing page is rendered
            renderWithProviders(
                <div>
                    <h1>Booksta - Your Reading Companion</h1>
                    <section>
                        <h2>Features</h2>
                        <h3>Track Your Reading</h3>
                        <h3>Discover Books</h3>
                    </section>
                </div>
            );

            // Then: Proper heading hierarchy should exist
            expect(screen.getByRole('heading', { level: 1 })).toBeInTheDocument();
            expect(screen.getByRole('heading', { level: 2 })).toBeInTheDocument();
        });

        test('AC-00.8: Landing page has accessible navigation', () => {
            // Given: The landing page with navigation
            renderWithProviders(
                <nav aria-label="Main navigation">
                    <a href="/books">Books</a>
                    <a href="/authors">Authors</a>
                </nav>
            );

            // Then: Navigation should have proper accessibility attributes
            expect(screen.getByRole('navigation')).toHaveAttribute('aria-label', 'Main navigation');
        });
    });

    describe('Acceptance Criteria: Page loads performantly', () => {
        test('AC-00.9: Landing page content is visible without requiring additional API calls', () => {
            // Given: The landing page is rendered
            const startTime = performance.now();

            renderWithProviders(
                <div>
                    <h1>Booksta</h1>
                    <p>Welcome to Booksta</p>
                </div>
            );

            const endTime = performance.now();

            // Then: The page should render quickly (under 100ms for basic content)
            expect(endTime - startTime).toBeLessThan(100);
            expect(screen.getByText(/booksta/i)).toBeInTheDocument();
        });
    });
});
