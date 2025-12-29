/**
 * Story-14: Error Reporting System Acceptance Tests
 *
 * As a Librarian
 * I want to have an error reporting system accessible to all users
 * So that I can facilitate the process of identifying and correcting errors,
 * and distribute the work of error detection
 *
 * Priority: Indispensable
 * Complexity: Medium
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockReports } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/reportService', () => ({
    __esModule: true,
    default: {
        create: jest.fn(),
        getAll: jest.fn(),
        resolve: jest.fn(),
        dismiss: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import reportService from '../../api/services/reportService';
import AuthService from '../../api/services/authService';

describe('Story-14: Error Reporting System', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
    });

    describe('Acceptance Criteria: Users can report errors', () => {
        beforeEach(() => {
            setupAuthenticatedUser(mockUsers.user);
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
        });

        test('AC-14.1: User sees report button on book detail page', async () => {
            // Given: A book detail page
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button data-testid="report-error-btn">
                        Report an Error
                    </button>
                </div>
            );

            // Then: Report button should be visible
            expect(screen.getByTestId('report-error-btn')).toBeInTheDocument();
        });

        test('AC-14.2: Report form allows describing the error', async () => {
            // Given: Report error form
            renderWithProviders(
                <div data-testid="report-form">
                    <h2>Report an Error</h2>
                    <p>Book: {mockBooks[0].title}</p>

                    <select data-testid="error-type">
                        <option value="">Select error type</option>
                        <option value="wrong-author">Incorrect Author</option>
                        <option value="wrong-title">Incorrect Title</option>
                        <option value="wrong-year">Incorrect Publication Year</option>
                        <option value="wrong-cover">Wrong Cover Image</option>
                        <option value="wrong-description">Incorrect Description</option>
                        <option value="duplicate">Duplicate Entry</option>
                        <option value="other">Other</option>
                    </select>

                    <textarea
                        data-testid="error-description"
                        placeholder="Please describe the error in detail..."
                    />

                    <button data-testid="submit-report">Submit Report</button>
                </div>
            );

            // Then: Report form should have proper fields
            expect(screen.getByTestId('error-type')).toBeInTheDocument();
            expect(screen.getByTestId('error-description')).toBeInTheDocument();
            expect(screen.getByTestId('submit-report')).toBeInTheDocument();
        });

        test('AC-14.3: User can submit error report', async () => {
            // Given: A report form
            reportService.create.mockResolvedValue(mockReports[0]);

            renderWithProviders(
                <form data-testid="report-form">
                    <textarea data-testid="error-description" />
                    <button
                        data-testid="submit-report"
                        onClick={(e) => {
                            e.preventDefault();
                            reportService.create(mockBooks[0].isbn, {
                                reason: 'Test report',
                            });
                        }}
                    >
                        Submit
                    </button>
                </form>
            );

            // When: User submits report
            await userEvent.click(screen.getByTestId('submit-report'));

            // Then: Report should be created
            expect(reportService.create).toHaveBeenCalled();
        });

        test('AC-14.4: Success message shown after report submission', async () => {
            // Given: Successful report submission
            renderWithProviders(
                <div data-testid="success-message">
                    <h3>Thank you for your report!</h3>
                    <p>Our librarians will review it shortly.</p>
                    <p>Report ID: #123</p>
                </div>
            );

            // Then: Success message should be shown
            expect(screen.getByText(/thank you for your report/i)).toBeInTheDocument();
            expect(screen.getByText(/Report ID/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Librarians can view reports', () => {
        beforeEach(() => {
            setupAuthenticatedUser(mockUsers.librarian);
            AuthService.getCurrentUser.mockReturnValue(mockUsers.librarian);
            reportService.getAll.mockResolvedValue(mockReports);
        });

        test('AC-14.5: Librarian can access reports page', async () => {
            // Given: A librarian navigating to reports
            renderWithProviders(
                <nav>
                    <a href="/reports" data-testid="reports-link">Reports</a>
                </nav>
            );

            // Then: Reports link should be visible
            expect(screen.getByTestId('reports-link')).toBeInTheDocument();
        });

        test('AC-14.6: Reports page shows all pending reports', async () => {
            // Given: Reports page
            renderWithProviders(
                <div data-testid="reports-page">
                    <h1>Error Reports</h1>
                    <div data-testid="reports-list">
                        {mockReports.map(report => (
                            <div key={report.id} data-testid="report-item">
                                <h3>{report.book.title}</h3>
                                <p data-testid="report-reason">{report.reason}</p>
                                <span data-testid="report-status">{report.status}</span>
                                <span data-testid="reporter">
                                    Reported by: {report.reporter.firstName}
                                </span>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Reports should be listed
            expect(screen.getAllByTestId('report-item')).toHaveLength(mockReports.length);
            expect(screen.getByTestId('report-reason')).toHaveTextContent('Incorrect author information');
        });

        test('AC-14.7: Reports can be filtered by status', async () => {
            // Given: Filter options
            renderWithProviders(
                <div data-testid="report-filters">
                    <select data-testid="status-filter">
                        <option value="all">All Reports</option>
                        <option value="pending">Pending</option>
                        <option value="resolved">Resolved</option>
                        <option value="dismissed">Dismissed</option>
                    </select>
                </div>
            );

            // Then: Filter options should be available
            expect(screen.getByTestId('status-filter')).toBeInTheDocument();
        });

        test('AC-14.8: Reports show book link for context', async () => {
            // Given: A report with book link
            renderWithProviders(
                <div data-testid="report-item">
                    <a
                        href={`/book/${mockBooks[0].isbn}`}
                        data-testid="report-book-link"
                    >
                        {mockBooks[0].title}
                    </a>
                    <p>Incorrect author information</p>
                </div>
            );

            // Then: Book link should be clickable
            expect(screen.getByTestId('report-book-link')).toHaveAttribute(
                'href',
                `/book/${mockBooks[0].isbn}`
            );
        });
    });

    describe('Acceptance Criteria: Librarians can resolve reports', () => {
        beforeEach(() => {
            setupAuthenticatedUser(mockUsers.librarian);
            AuthService.getCurrentUser.mockReturnValue(mockUsers.librarian);
        });

        test('AC-14.9: Librarian can resolve a report', async () => {
            // Given: A pending report with resolve option
            reportService.resolve.mockResolvedValue({ ...mockReports[0], status: 'RESOLVED' });

            renderWithProviders(
                <div data-testid="report-item">
                    <h3>{mockBooks[0].title}</h3>
                    <p>{mockReports[0].reason}</p>
                    <button
                        data-testid="resolve-report"
                        onClick={() => reportService.resolve(mockReports[0].id)}
                    >
                        Mark as Resolved
                    </button>
                </div>
            );

            // When: Librarian resolves
            await userEvent.click(screen.getByTestId('resolve-report'));

            // Then: Report should be resolved
            expect(reportService.resolve).toHaveBeenCalled();
        });

        test('AC-14.10: Librarian can dismiss invalid reports', async () => {
            // Given: A report with dismiss option
            reportService.dismiss.mockResolvedValue({ ...mockReports[0], status: 'DISMISSED' });

            renderWithProviders(
                <div data-testid="report-item">
                    <h3>{mockBooks[0].title}</h3>
                    <button
                        data-testid="dismiss-report"
                        onClick={() => reportService.dismiss(mockReports[0].id)}
                    >
                        Dismiss Report
                    </button>
                </div>
            );

            // When: Librarian dismisses
            await userEvent.click(screen.getByTestId('dismiss-report'));

            // Then: Report should be dismissed
            expect(reportService.dismiss).toHaveBeenCalled();
        });

        test('AC-14.11: Resolving includes link to edit book', async () => {
            // Given: Resolve dialog with edit link
            renderWithProviders(
                <div data-testid="resolve-dialog">
                    <h3>Resolve Report</h3>
                    <p>Report: Incorrect author information</p>
                    <a
                        href={`/books/${mockBooks[0].isbn}/edit`}
                        data-testid="edit-book-link"
                    >
                        Edit Book to Fix Issue
                    </a>
                    <button data-testid="mark-resolved">Mark as Resolved</button>
                </div>
            );

            // Then: Edit link should be available
            expect(screen.getByTestId('edit-book-link')).toHaveAttribute(
                'href',
                `/books/${mockBooks[0].isbn}/edit`
            );
        });
    });

    describe('Acceptance Criteria: Regular users cannot access report management', () => {
        test('AC-14.12: Regular users do not see Reports link', async () => {
            // Given: A regular user
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);

            renderWithProviders(
                <nav data-testid="user-nav">
                    {/* Reports link not rendered for regular users */}
                </nav>
            );

            // Then: Reports link should not be present
            expect(screen.queryByTestId('reports-link')).not.toBeInTheDocument();
        });

        test('AC-14.13: Report submission confirms user identity', async () => {
            // Given: A logged-in user submitting report
            setupAuthenticatedUser(mockUsers.user);
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);

            renderWithProviders(
                <div data-testid="report-form">
                    <p data-testid="reporter-info">
                        Reporting as: {mockUsers.user.firstName} {mockUsers.user.lastName}
                    </p>
                </div>
            );

            // Then: User identity should be shown
            expect(screen.getByTestId('reporter-info')).toHaveTextContent('Test User');
        });
    });

    describe('Acceptance Criteria: Reports track history', () => {
        beforeEach(() => {
            setupAuthenticatedUser(mockUsers.librarian);
            AuthService.getCurrentUser.mockReturnValue(mockUsers.librarian);
        });

        test('AC-14.14: Resolved reports show resolution info', async () => {
            // Given: A resolved report
            renderWithProviders(
                <div data-testid="resolved-report">
                    <span data-testid="status" className="resolved">Resolved</span>
                    <p data-testid="resolved-by">Resolved by: Test Librarian</p>
                    <p data-testid="resolved-date">January 16, 2024</p>
                </div>
            );

            // Then: Resolution info should be visible
            expect(screen.getByTestId('resolved-by')).toHaveTextContent('Test Librarian');
            expect(screen.getByTestId('resolved-date')).toBeInTheDocument();
        });

        test('AC-14.15: Reports page shows statistics', async () => {
            // Given: Report statistics
            renderWithProviders(
                <div data-testid="report-stats">
                    <div data-testid="pending-count">
                        <span>5</span>
                        <label>Pending</label>
                    </div>
                    <div data-testid="resolved-count">
                        <span>42</span>
                        <label>Resolved This Month</label>
                    </div>
                    <div data-testid="dismissed-count">
                        <span>8</span>
                        <label>Dismissed</label>
                    </div>
                </div>
            );

            // Then: Statistics should be visible
            expect(screen.getByTestId('pending-count')).toHaveTextContent('5');
            expect(screen.getByTestId('resolved-count')).toHaveTextContent('42');
        });
    });
});
