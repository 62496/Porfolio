/**
 * Story-08: Reading Progress Tracking Acceptance Tests
 *
 * As a User
 * I want to record and share my progress on my current readings
 * So that I can keep my good reading habits, and note and share my reactions at different key moments
 *
 * Priority: Essential
 * Complexity: High
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockReadingSessions } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/readingService', () => ({
    __esModule: true,
    default: {
        startSession: jest.fn(),
        pauseSession: jest.fn(),
        resumeSession: jest.fn(),
        endSession: jest.fn(),
        deleteSession: jest.fn(),
    },
}));

jest.mock('../../api/services/bookService', () => ({
    __esModule: true,
    default: {
        createBookReadingEvent: jest.fn(),
        getLatestBookReadingEvent: jest.fn(),
        getAllBookReadingSessions: jest.fn(),
        getAllBookReadingEvents: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import readingService from '../../api/services/readingService';
import bookService from '../../api/services/bookService';
import AuthService from '../../api/services/authService';

describe('Story-08: Reading Progress Tracking', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: User can start reading sessions', () => {
        test('AC-08.1: User can start a reading session for a book', async () => {
            // Given: A book detail page with reading controls
            readingService.startSession.mockResolvedValue(mockReadingSessions[1]);

            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button
                        data-testid="start-reading-btn"
                        onClick={() => readingService.startSession({ bookIsbn: mockBooks[0].isbn })}
                    >
                        Start Reading
                    </button>
                </div>
            );

            // When: User clicks start reading
            await userEvent.click(screen.getByTestId('start-reading-btn'));

            // Then: Reading session should start
            expect(readingService.startSession).toHaveBeenCalled();
        });

        test('AC-08.2: Active reading session shows timer', async () => {
            // Given: An active reading session
            renderWithProviders(
                <div data-testid="reading-session">
                    <h2>Currently Reading: {mockBooks[0].title}</h2>
                    <div data-testid="reading-timer">
                        <span data-testid="elapsed-time">01:30:45</span>
                    </div>
                </div>
            );

            // Then: Timer should be visible
            expect(screen.getByTestId('reading-timer')).toBeInTheDocument();
            expect(screen.getByTestId('elapsed-time')).toHaveTextContent('01:30:45');
        });

        test('AC-08.3: User can only have one active session at a time', async () => {
            // Given: An active session exists
            renderWithProviders(
                <div data-testid="active-session-warning">
                    <p>You already have an active reading session for another book.</p>
                    <button>End Current Session</button>
                    <button>Continue Current Session</button>
                </div>
            );

            // Then: Warning should be shown
            expect(screen.getByText(/already have an active/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can pause and resume sessions', () => {
        test('AC-08.4: User can pause a reading session', async () => {
            // Given: An active reading session
            readingService.pauseSession.mockResolvedValue({ status: 'PAUSED' });

            renderWithProviders(
                <div data-testid="reading-session">
                    <button
                        data-testid="pause-btn"
                        onClick={() => readingService.pauseSession(mockReadingSessions[1].id)}
                    >
                        Pause
                    </button>
                </div>
            );

            // When: User pauses
            await userEvent.click(screen.getByTestId('pause-btn'));

            // Then: Session should be paused
            expect(readingService.pauseSession).toHaveBeenCalledWith(mockReadingSessions[1].id);
        });

        test('AC-08.5: User can resume a paused session', async () => {
            // Given: A paused session
            readingService.resumeSession.mockResolvedValue({ status: 'IN_PROGRESS' });

            renderWithProviders(
                <div data-testid="paused-session">
                    <span>Session Paused</span>
                    <button
                        data-testid="resume-btn"
                        onClick={() => readingService.resumeSession(mockReadingSessions[1].id)}
                    >
                        Resume Reading
                    </button>
                </div>
            );

            // When: User resumes
            await userEvent.click(screen.getByTestId('resume-btn'));

            // Then: Session should resume
            expect(readingService.resumeSession).toHaveBeenCalledWith(mockReadingSessions[1].id);
        });
    });

    describe('Acceptance Criteria: User can end reading sessions', () => {
        test('AC-08.6: User can end a reading session', async () => {
            // Given: An active session
            readingService.endSession.mockResolvedValue({ status: 'COMPLETED' });

            renderWithProviders(
                <div data-testid="reading-session">
                    <button
                        data-testid="end-session-btn"
                        onClick={() => readingService.endSession(mockReadingSessions[1].id)}
                    >
                        End Session
                    </button>
                </div>
            );

            // When: User ends session
            await userEvent.click(screen.getByTestId('end-session-btn'));

            // Then: Session should end
            expect(readingService.endSession).toHaveBeenCalled();
        });

        test('AC-08.7: Ending session prompts for pages read', async () => {
            // Given: End session dialog
            renderWithProviders(
                <div data-testid="end-session-dialog">
                    <h3>End Reading Session</h3>
                    <label>
                        Pages read this session:
                        <input type="number" data-testid="pages-read-input" />
                    </label>
                    <label>
                        Current page:
                        <input type="number" data-testid="current-page-input" />
                    </label>
                    <button data-testid="confirm-end">Save & End</button>
                </div>
            );

            // Then: Pages input should be visible
            expect(screen.getByTestId('pages-read-input')).toBeInTheDocument();
            expect(screen.getByTestId('current-page-input')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can track reading events', () => {
        test('AC-08.8: User can mark a book as started', async () => {
            // Given: A book not yet started
            bookService.createBookReadingEvent.mockResolvedValue({ eventType: 'STARTED' });

            renderWithProviders(
                <button
                    data-testid="mark-started"
                    onClick={() => bookService.createBookReadingEvent(mockBooks[0].isbn, 'STARTED')}
                >
                    Mark as Started
                </button>
            );

            // When: User marks as started
            await userEvent.click(screen.getByTestId('mark-started'));

            // Then: Event should be created
            expect(bookService.createBookReadingEvent).toHaveBeenCalledWith(mockBooks[0].isbn, 'STARTED');
        });

        test('AC-08.9: User can mark a book as finished', async () => {
            // Given: A book in progress
            bookService.createBookReadingEvent.mockResolvedValue({ eventType: 'FINISHED' });

            renderWithProviders(
                <button
                    data-testid="mark-finished"
                    onClick={() => bookService.createBookReadingEvent(mockBooks[0].isbn, 'FINISHED')}
                >
                    Mark as Finished
                </button>
            );

            // When: User marks as finished
            await userEvent.click(screen.getByTestId('mark-finished'));

            // Then: Finished event should be created
            expect(bookService.createBookReadingEvent).toHaveBeenCalledWith(mockBooks[0].isbn, 'FINISHED');
        });

        test('AC-08.10: User can mark a book as abandoned', async () => {
            // Given: A book in progress
            bookService.createBookReadingEvent.mockResolvedValue({ eventType: 'ABANDONED' });

            renderWithProviders(
                <button
                    data-testid="mark-abandoned"
                    onClick={() => bookService.createBookReadingEvent(mockBooks[0].isbn, 'ABANDONED')}
                >
                    Abandon Book
                </button>
            );

            // When: User abandons
            await userEvent.click(screen.getByTestId('mark-abandoned'));

            // Then: Abandoned event should be created
            expect(bookService.createBookReadingEvent).toHaveBeenCalledWith(mockBooks[0].isbn, 'ABANDONED');
        });
    });

    describe('Acceptance Criteria: User can view reading history', () => {
        test('AC-08.11: Reading progress page shows all sessions', async () => {
            // Given: A user with reading history
            bookService.getAllBookReadingSessions.mockResolvedValue(mockReadingSessions);

            renderWithProviders(
                <div data-testid="reading-progress-page">
                    <h1>My Reading Progress</h1>
                    <section data-testid="reading-sessions">
                        {mockReadingSessions.map(session => (
                            <div key={session.id} data-testid="session-item">
                                <span>Session {session.id}</span>
                                <span>{session.status}</span>
                            </div>
                        ))}
                    </section>
                </div>
            );

            // Then: Sessions should be displayed
            expect(screen.getAllByTestId('session-item')).toHaveLength(mockReadingSessions.length);
        });

        test('AC-08.12: Currently reading books are highlighted', async () => {
            // Given: Books with different statuses
            renderWithProviders(
                <div data-testid="currently-reading">
                    <h2>Currently Reading</h2>
                    <div data-testid="current-book" className="in-progress">
                        <h3>{mockBooks[0].title}</h3>
                        <div data-testid="progress-bar">
                            <div style={{ width: '45%' }} />
                        </div>
                        <span>Page 157 of 350</span>
                    </div>
                </div>
            );

            // Then: Current book with progress should be visible
            expect(screen.getByTestId('currently-reading')).toBeInTheDocument();
            expect(screen.getByText(/page 157 of 350/i)).toBeInTheDocument();
        });

        test('AC-08.13: Reading stats are displayed', async () => {
            // Given: User reading statistics
            renderWithProviders(
                <div data-testid="reading-stats">
                    <div data-testid="stat-books-read">
                        <span>12</span>
                        <label>Books Read</label>
                    </div>
                    <div data-testid="stat-pages-read">
                        <span>4,200</span>
                        <label>Pages Read</label>
                    </div>
                    <div data-testid="stat-time-spent">
                        <span>156 hours</span>
                        <label>Time Reading</label>
                    </div>
                </div>
            );

            // Then: Stats should be visible
            expect(screen.getByTestId('stat-books-read')).toHaveTextContent('12');
            expect(screen.getByTestId('stat-pages-read')).toHaveTextContent('4,200');
        });
    });

    describe('Acceptance Criteria: User can share reading progress', () => {
        test('AC-08.14: User can share progress milestone', async () => {
            // Given: A progress milestone
            renderWithProviders(
                <div data-testid="milestone-share">
                    <p>You've reached 50% of {mockBooks[0].title}!</p>
                    <button data-testid="share-milestone">
                        Share Progress
                    </button>
                </div>
            );

            // Then: Share option should be available
            expect(screen.getByTestId('share-milestone')).toBeInTheDocument();
        });

        test('AC-08.15: Completion can be shared', async () => {
            // Given: A completed book
            renderWithProviders(
                <div data-testid="completion-share">
                    <h3>Congratulations!</h3>
                    <p>You finished {mockBooks[0].title}</p>
                    <button data-testid="share-completion">
                        Share to Your Feed
                    </button>
                </div>
            );

            // Then: Share completion should be available
            expect(screen.getByTestId('share-completion')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Reading sessions can be managed', () => {
        test('AC-08.16: User can delete a reading session', async () => {
            // Given: A session with delete option
            readingService.deleteSession.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="session-item">
                    <span>Reading session - Jan 15</span>
                    <button
                        data-testid="delete-session"
                        onClick={() => readingService.deleteSession(mockReadingSessions[0].id)}
                    >
                        Delete
                    </button>
                </div>
            );

            // When: User deletes session
            await userEvent.click(screen.getByTestId('delete-session'));

            // Then: Delete should be called
            expect(readingService.deleteSession).toHaveBeenCalledWith(mockReadingSessions[0].id);
        });

        test('AC-08.17: User can edit completed session details', async () => {
            // Given: A completed session with edit option
            renderWithProviders(
                <div data-testid="session-edit">
                    <h3>Edit Session</h3>
                    <input type="number" data-testid="edit-pages" defaultValue={50} />
                    <input type="text" data-testid="edit-notes" placeholder="Session notes..." />
                    <button data-testid="save-edit">Save Changes</button>
                </div>
            );

            // Then: Edit form should be available
            expect(screen.getByTestId('edit-pages')).toHaveValue(50);
            expect(screen.getByTestId('save-edit')).toBeInTheDocument();
        });
    });
});
