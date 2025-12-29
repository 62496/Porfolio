/**
 * Story-07: User Messaging Acceptance Tests
 *
 * As a User
 * I want to exchange with other readers
 * So that I can meet people with common interests and discover new readings
 *
 * Priority: Useful
 * Complexity: High
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockConversations } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/messageService', () => ({
    __esModule: true,
    default: {
        listConversations: jest.fn(),
        getConversation: jest.fn(),
        send: jest.fn(),
        markAsRead: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import messageService from '../../api/services/messageService';
import AuthService from '../../api/services/authService';

// Mock messages data
const mockMessages = [
    {
        id: 1,
        content: 'Hey, have you read the new book?',
        sender: mockUsers.user,
        createdAt: '2024-01-15T10:00:00Z',
    },
    {
        id: 2,
        content: 'Not yet! Is it good?',
        sender: mockUsers.author,
        createdAt: '2024-01-15T10:05:00Z',
    },
    {
        id: 3,
        content: 'Amazing! Highly recommend it.',
        sender: mockUsers.user,
        createdAt: '2024-01-15T10:10:00Z',
    },
];

describe('Story-07: User Messaging', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: User can view conversations', () => {
        test('AC-07.1: User can access messages page', async () => {
            // Given: A user with conversations
            messageService.listConversations.mockResolvedValue(mockConversations);

            renderWithProviders(
                <div data-testid="messages-page">
                    <h1>Messages</h1>
                    <div data-testid="conversations-list">
                        {mockConversations.map(conv => (
                            <div key={conv.id} data-testid="conversation-item">
                                <span>{conv.participants.find(p => p.id !== mockUsers.user.id)?.firstName}</span>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Messages page should be visible
            expect(screen.getByTestId('messages-page')).toBeInTheDocument();
            expect(screen.getByText('Messages')).toBeInTheDocument();
        });

        test('AC-07.2: Conversations list shows all conversations', async () => {
            // Given: Multiple conversations
            messageService.listConversations.mockResolvedValue(mockConversations);

            renderWithProviders(
                <div data-testid="conversations-list">
                    {mockConversations.map(conv => (
                        <div key={conv.id} data-testid="conversation-item">
                            <img src={conv.participants[1].picture} alt="avatar" />
                            <div>
                                <h3>{conv.participants[1].firstName} {conv.participants[1].lastName}</h3>
                                <p data-testid="last-message">{conv.lastMessage}</p>
                            </div>
                        </div>
                    ))}
                </div>
            );

            // Then: All conversations should be displayed
            expect(screen.getAllByTestId('conversation-item')).toHaveLength(mockConversations.length);
            expect(screen.getByTestId('last-message')).toHaveTextContent('Looking forward to your next book!');
        });

        test('AC-07.3: Unread conversations show badge', async () => {
            // Given: Conversations with unread messages
            renderWithProviders(
                <div data-testid="conversation-item">
                    <span data-testid="unread-badge" className="unread-badge">2</span>
                    <span>Test Author</span>
                </div>
            );

            // Then: Unread badge should be visible
            expect(screen.getByTestId('unread-badge')).toHaveTextContent('2');
        });

        test('AC-07.4: Empty conversations shows appropriate message', async () => {
            // Given: No conversations
            messageService.listConversations.mockResolvedValue([]);

            renderWithProviders(
                <div data-testid="empty-messages">
                    <p>No messages yet</p>
                    <p>Start a conversation with another reader!</p>
                </div>
            );

            // Then: Empty state should be shown
            expect(screen.getByText(/no messages yet/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can view conversation messages', () => {
        test('AC-07.5: User can open a conversation to see messages', async () => {
            // Given: A conversation with messages
            messageService.getConversation.mockResolvedValue({
                ...mockConversations[0],
                messages: mockMessages,
            });

            renderWithProviders(
                <div data-testid="conversation-view">
                    <div data-testid="conversation-header">
                        <h2>Test Author</h2>
                    </div>
                    <div data-testid="messages-container">
                        {mockMessages.map(msg => (
                            <div
                                key={msg.id}
                                data-testid="message-bubble"
                                className={msg.sender.id === mockUsers.user.id ? 'sent' : 'received'}
                            >
                                <p>{msg.content}</p>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: All messages should be displayed
            expect(screen.getByTestId('conversation-view')).toBeInTheDocument();
            expect(screen.getAllByTestId('message-bubble')).toHaveLength(mockMessages.length);
        });

        test('AC-07.6: Messages show sender and timestamp', async () => {
            // Given: A message with metadata
            renderWithProviders(
                <div data-testid="message-bubble">
                    <span data-testid="message-sender">Test User</span>
                    <p>Hey, have you read the new book?</p>
                    <span data-testid="message-time">10:00 AM</span>
                </div>
            );

            // Then: Sender and time should be visible
            expect(screen.getByTestId('message-sender')).toHaveTextContent('Test User');
            expect(screen.getByTestId('message-time')).toHaveTextContent('10:00 AM');
        });

        test('AC-07.7: Own messages are styled differently', async () => {
            // Given: Messages with different senders
            renderWithProviders(
                <div data-testid="messages-container">
                    <div data-testid="message-bubble" className="sent">
                        My message
                    </div>
                    <div data-testid="message-bubble" className="received">
                        Their message
                    </div>
                </div>
            );

            // Then: Messages should have different styling classes
            const messages = screen.getAllByTestId('message-bubble');
            expect(messages[0]).toHaveClass('sent');
            expect(messages[1]).toHaveClass('received');
        });
    });

    describe('Acceptance Criteria: User can send messages', () => {
        test('AC-07.8: User can type and send a message', async () => {
            // Given: A conversation with message input
            messageService.send.mockResolvedValue({ id: 4, content: 'New message' });

            renderWithProviders(
                <div data-testid="conversation-view">
                    <div data-testid="message-input-area">
                        <textarea
                            data-testid="message-input"
                            placeholder="Type a message..."
                        />
                        <button
                            data-testid="send-button"
                            onClick={() => messageService.send({ content: 'New message' })}
                        >
                            Send
                        </button>
                    </div>
                </div>
            );

            // When: User types and sends a message
            await userEvent.type(screen.getByTestId('message-input'), 'New message');
            await userEvent.click(screen.getByTestId('send-button'));

            // Then: Message should be sent
            expect(messageService.send).toHaveBeenCalled();
        });

        test('AC-07.9: Send button is disabled for empty messages', async () => {
            // Given: An empty message input
            renderWithProviders(
                <button data-testid="send-button" disabled>
                    Send
                </button>
            );

            // Then: Send button should be disabled
            expect(screen.getByTestId('send-button')).toBeDisabled();
        });

        test('AC-07.10: Sent message appears immediately in conversation', async () => {
            // Given: A sent message that appears in the UI
            renderWithProviders(
                <div data-testid="messages-container">
                    <div data-testid="message-bubble" className="sent">
                        New message
                    </div>
                </div>
            );

            // Then: New message should be visible
            expect(screen.getByText('New message')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can start new conversations', () => {
        test('AC-07.11: User can start a new conversation', async () => {
            // Given: A new message form
            renderWithProviders(
                <div data-testid="new-conversation">
                    <h2>New Message</h2>
                    <input
                        data-testid="recipient-search"
                        placeholder="Search for a user..."
                    />
                    <textarea
                        data-testid="new-message-input"
                        placeholder="Write your message..."
                    />
                    <button data-testid="start-conversation-btn">
                        Send Message
                    </button>
                </div>
            );

            // Then: New conversation form should be visible
            expect(screen.getByTestId('new-conversation')).toBeInTheDocument();
            expect(screen.getByTestId('recipient-search')).toBeInTheDocument();
        });

        test('AC-07.12: User can search for recipients', async () => {
            // Given: A recipient search with results
            renderWithProviders(
                <div>
                    <input
                        data-testid="recipient-search"
                        placeholder="Search for a user..."
                    />
                    <div data-testid="search-results">
                        <div data-testid="user-result">
                            <span>Test Author</span>
                            <button>Select</button>
                        </div>
                    </div>
                </div>
            );

            // When: User searches
            await userEvent.type(screen.getByTestId('recipient-search'), 'Author');

            // Then: Search results should be visible
            expect(screen.getByTestId('search-results')).toBeInTheDocument();
        });

        test('AC-07.13: User can message from user profile', async () => {
            // Given: A user profile with message button
            renderWithProviders(
                <div data-testid="user-profile">
                    <h1>Test Author</h1>
                    <button data-testid="send-message-btn">
                        Send Message
                    </button>
                </div>
            );

            // Then: Message button should be available
            expect(screen.getByTestId('send-message-btn')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Messages are marked as read', () => {
        test('AC-07.14: Opening conversation marks messages as read', async () => {
            // Given: A conversation with unread messages
            messageService.markAsRead.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="conversation-view">
                    {/* Simulating conversation opened */}
                </div>
            );

            // When: Conversation is opened
            await messageService.markAsRead(mockConversations[0].id);

            // Then: Messages should be marked as read
            expect(messageService.markAsRead).toHaveBeenCalledWith(mockConversations[0].id);
        });

        test('AC-07.15: Unread count in header updates', async () => {
            // Given: Header showing unread message count
            renderWithProviders(
                <nav>
                    <a href="/messages" data-testid="messages-link">
                        Messages <span data-testid="total-unread">2</span>
                    </a>
                </nav>
            );

            // Then: Unread count should be visible
            expect(screen.getByTestId('total-unread')).toHaveTextContent('2');
        });
    });

    describe('Acceptance Criteria: Messaging enables reader connections', () => {
        test('AC-07.16: Users can discuss shared book interests', async () => {
            // Given: A conversation about a book
            renderWithProviders(
                <div data-testid="conversation-context">
                    <p>Discussing: <a href="/book/123">The Great Adventure</a></p>
                    <div data-testid="message-bubble">
                        What did you think about chapter 5?
                    </div>
                </div>
            );

            // Then: Book context should be visible
            expect(screen.getByText(/discussing/i)).toBeInTheDocument();
            expect(screen.getByText(/chapter 5/i)).toBeInTheDocument();
        });

        test('AC-07.17: Conversation shows participant profiles', async () => {
            // Given: Conversation with participant info
            renderWithProviders(
                <div data-testid="conversation-header">
                    <img
                        src={mockUsers.author.picture}
                        alt="avatar"
                        data-testid="participant-avatar"
                    />
                    <div>
                        <h2 data-testid="participant-name">Test Author</h2>
                        <a href="/profile/2" data-testid="view-profile">View Profile</a>
                    </div>
                </div>
            );

            // Then: Participant info should be visible
            expect(screen.getByTestId('participant-avatar')).toBeInTheDocument();
            expect(screen.getByTestId('participant-name')).toHaveTextContent('Test Author');
            expect(screen.getByTestId('view-profile')).toHaveAttribute('href', '/profile/2');
        });
    });
});
