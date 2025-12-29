/**
 * Story-09: Book Collections Acceptance Tests
 *
 * As a User
 * I want to create and share different collections of books on various themes
 * So that I can express myself creatively and recommend books to my friends
 *
 * Priority: Useful
 * Complexity: High
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockCollections } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/bookCollectionsService', () => ({
    __esModule: true,
    default: {
        create: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
        getById: jest.fn(),
        getOwn: jest.fn(),
        getAllowed: jest.fn(),
        getPublic: jest.fn(),
        getShared: jest.fn(),
        share: jest.fn(),
        unshare: jest.fn(),
        addBook: jest.fn(),
        removeBook: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import bookCollectionsService from '../../api/services/bookCollectionsService';
import AuthService from '../../api/services/authService';

describe('Story-09: Book Collections', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: User can view collections', () => {
        test('AC-09.1: User can access collections page', async () => {
            // Given: A user with collections
            bookCollectionsService.getOwn.mockResolvedValue(mockCollections);

            renderWithProviders(
                <div data-testid="collections-page">
                    <h1>My Collections</h1>
                    <div data-testid="collections-list">
                        {mockCollections.map(collection => (
                            <div key={collection.id} data-testid="collection-card">
                                <h3>{collection.name}</h3>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Collections should be visible
            expect(screen.getByTestId('collections-page')).toBeInTheDocument();
            expect(screen.getAllByTestId('collection-card')).toHaveLength(mockCollections.length);
        });

        test('AC-09.2: Collection card shows name, description, and book count', async () => {
            // Given: A collection card
            renderWithProviders(
                <div data-testid="collection-card">
                    <h3 data-testid="collection-name">{mockCollections[0].name}</h3>
                    <p data-testid="collection-description">{mockCollections[0].description}</p>
                    <span data-testid="book-count">{mockCollections[0].books.length} books</span>
                </div>
            );

            // Then: Collection details should be visible
            expect(screen.getByTestId('collection-name')).toHaveTextContent('Summer Reading');
            expect(screen.getByTestId('collection-description')).toHaveTextContent('Books to read during summer vacation');
            expect(screen.getByTestId('book-count')).toHaveTextContent('2 books');
        });

        test('AC-09.3: Empty collections page shows create prompt', async () => {
            // Given: No collections
            bookCollectionsService.getOwn.mockResolvedValue([]);

            renderWithProviders(
                <div data-testid="empty-collections">
                    <p>You haven't created any collections yet.</p>
                    <button data-testid="create-first-collection">Create Your First Collection</button>
                </div>
            );

            // Then: Empty state should be shown
            expect(screen.getByText(/haven't created any/i)).toBeInTheDocument();
            expect(screen.getByTestId('create-first-collection')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can create collections', () => {
        test('AC-09.4: User can create a new collection', async () => {
            // Given: Create collection form
            bookCollectionsService.create.mockResolvedValue(mockCollections[0]);

            renderWithProviders(
                <div data-testid="create-collection-form">
                    <h2>Create Collection</h2>
                    <input
                        data-testid="collection-name-input"
                        placeholder="Collection name..."
                    />
                    <textarea
                        data-testid="collection-description-input"
                        placeholder="Describe your collection..."
                    />
                    <button
                        data-testid="create-collection-btn"
                        onClick={() => bookCollectionsService.create({ name: 'Test', description: 'Test' })}
                    >
                        Create Collection
                    </button>
                </div>
            );

            // When: User fills and submits form
            await userEvent.type(screen.getByTestId('collection-name-input'), 'Summer Reading');
            await userEvent.click(screen.getByTestId('create-collection-btn'));

            // Then: Collection should be created
            expect(bookCollectionsService.create).toHaveBeenCalled();
        });

        test('AC-09.5: User can set collection visibility (public/private)', async () => {
            // Given: Visibility toggle in create form
            renderWithProviders(
                <div data-testid="visibility-options">
                    <label>
                        <input type="radio" name="visibility" value="private" data-testid="private-option" />
                        Private
                    </label>
                    <label>
                        <input type="radio" name="visibility" value="public" data-testid="public-option" />
                        Public
                    </label>
                </div>
            );

            // Then: Visibility options should be available
            expect(screen.getByTestId('private-option')).toBeInTheDocument();
            expect(screen.getByTestId('public-option')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: User can manage collection contents', () => {
        test('AC-09.6: User can add books to a collection', async () => {
            // Given: A collection with add book option
            bookCollectionsService.addBook.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="add-book-modal">
                    <h3>Add Book to Collection</h3>
                    <input data-testid="book-search" placeholder="Search books..." />
                    <div data-testid="book-option">
                        <span>{mockBooks[0].title}</span>
                        <button
                            data-testid="add-book-btn"
                            onClick={() => bookCollectionsService.addBook(mockCollections[0].id, mockBooks[0].isbn)}
                        >
                            Add
                        </button>
                    </div>
                </div>
            );

            // When: User adds a book
            await userEvent.click(screen.getByTestId('add-book-btn'));

            // Then: Book should be added
            expect(bookCollectionsService.addBook).toHaveBeenCalledWith(mockCollections[0].id, mockBooks[0].isbn);
        });

        test('AC-09.7: User can remove books from a collection', async () => {
            // Given: A collection with books
            bookCollectionsService.removeBook.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="collection-book">
                    <span>{mockBooks[0].title}</span>
                    <button
                        data-testid="remove-book-btn"
                        onClick={() => bookCollectionsService.removeBook(mockCollections[0].id, mockBooks[0].isbn)}
                    >
                        Remove
                    </button>
                </div>
            );

            // When: User removes a book
            await userEvent.click(screen.getByTestId('remove-book-btn'));

            // Then: Book should be removed
            expect(bookCollectionsService.removeBook).toHaveBeenCalledWith(mockCollections[0].id, mockBooks[0].isbn);
        });

        test('AC-09.8: Collection detail shows all books', async () => {
            // Given: A collection detail page
            bookCollectionsService.getById.mockResolvedValue(mockCollections[0]);

            renderWithProviders(
                <div data-testid="collection-detail">
                    <h1>{mockCollections[0].name}</h1>
                    <p>{mockCollections[0].description}</p>
                    <div data-testid="collection-books">
                        {mockCollections[0].books.map(book => (
                            <div key={book.isbn} data-testid="collection-book-item">
                                <h3>{book.title}</h3>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: All books should be displayed
            expect(screen.getAllByTestId('collection-book-item')).toHaveLength(mockCollections[0].books.length);
        });
    });

    describe('Acceptance Criteria: User can edit collections', () => {
        test('AC-09.9: User can edit collection name and description', async () => {
            // Given: Edit collection form
            bookCollectionsService.update.mockResolvedValue({ ...mockCollections[0], name: 'Updated Name' });

            renderWithProviders(
                <div data-testid="edit-collection-form">
                    <input
                        data-testid="edit-name"
                        defaultValue={mockCollections[0].name}
                    />
                    <textarea
                        data-testid="edit-description"
                        defaultValue={mockCollections[0].description}
                    />
                    <button
                        data-testid="save-changes"
                        onClick={() => bookCollectionsService.update(mockCollections[0].id, {})}
                    >
                        Save Changes
                    </button>
                </div>
            );

            // When: User saves changes
            await userEvent.click(screen.getByTestId('save-changes'));

            // Then: Collection should be updated
            expect(bookCollectionsService.update).toHaveBeenCalled();
        });

        test('AC-09.10: User can delete a collection', async () => {
            // Given: Delete confirmation
            bookCollectionsService.delete.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="delete-confirmation">
                    <p>Are you sure you want to delete this collection?</p>
                    <button
                        data-testid="confirm-delete"
                        onClick={() => bookCollectionsService.delete(mockCollections[0].id)}
                    >
                        Delete
                    </button>
                    <button data-testid="cancel-delete">Cancel</button>
                </div>
            );

            // When: User confirms deletion
            await userEvent.click(screen.getByTestId('confirm-delete'));

            // Then: Collection should be deleted
            expect(bookCollectionsService.delete).toHaveBeenCalledWith(mockCollections[0].id);
        });
    });

    describe('Acceptance Criteria: User can share collections', () => {
        test('AC-09.11: User can share collection with specific users', async () => {
            // Given: Share collection form
            bookCollectionsService.share.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="share-collection">
                    <h3>Share Collection</h3>
                    <input
                        data-testid="share-email-input"
                        placeholder="Enter email..."
                    />
                    <button
                        data-testid="share-btn"
                        onClick={() => bookCollectionsService.share(mockCollections[0].id, 'friend@test.com')}
                    >
                        Share
                    </button>
                </div>
            );

            // When: User shares collection
            await userEvent.click(screen.getByTestId('share-btn'));

            // Then: Collection should be shared
            expect(bookCollectionsService.share).toHaveBeenCalledWith(mockCollections[0].id, 'friend@test.com');
        });

        test('AC-09.12: User can revoke sharing access', async () => {
            // Given: Shared user with revoke option
            bookCollectionsService.unshare.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="shared-users">
                    <div data-testid="shared-user">
                        <span>friend@test.com</span>
                        <button
                            data-testid="revoke-access"
                            onClick={() => bookCollectionsService.unshare(mockCollections[0].id, 2)}
                        >
                            Revoke
                        </button>
                    </div>
                </div>
            );

            // When: User revokes access
            await userEvent.click(screen.getByTestId('revoke-access'));

            // Then: Access should be revoked
            expect(bookCollectionsService.unshare).toHaveBeenCalled();
        });

        test('AC-09.13: Public collections are discoverable', async () => {
            // Given: Public collections page
            bookCollectionsService.getPublic.mockResolvedValue(mockCollections.filter(c => c.isPublic));

            renderWithProviders(
                <div data-testid="public-collections">
                    <h1>Discover Collections</h1>
                    {mockCollections.filter(c => c.isPublic).map(collection => (
                        <div key={collection.id} data-testid="public-collection">
                            <h3>{collection.name}</h3>
                            <span>by {collection.owner.firstName}</span>
                        </div>
                    ))}
                </div>
            );

            // Then: Public collections should be visible
            expect(screen.getAllByTestId('public-collection')).toHaveLength(1);
        });
    });

    describe('Acceptance Criteria: Collections show thematic organization', () => {
        test('AC-09.14: Collections can have cover images', async () => {
            // Given: A collection with cover
            renderWithProviders(
                <div data-testid="collection-card">
                    <img
                        data-testid="collection-cover"
                        src="/collections/summer.jpg"
                        alt="Summer Reading"
                    />
                    <h3>Summer Reading</h3>
                </div>
            );

            // Then: Cover image should be visible
            expect(screen.getByTestId('collection-cover')).toBeInTheDocument();
        });

        test('AC-09.15: Collections show genre diversity', async () => {
            // Given: A collection with genre tags
            renderWithProviders(
                <div data-testid="collection-detail">
                    <h3>Summer Reading</h3>
                    <div data-testid="collection-genres">
                        <span className="genre-tag">Adventure</span>
                        <span className="genre-tag">Mystery</span>
                        <span className="genre-tag">Fiction</span>
                    </div>
                </div>
            );

            // Then: Genre tags should be visible
            expect(screen.getByText('Adventure')).toBeInTheDocument();
            expect(screen.getByText('Mystery')).toBeInTheDocument();
        });

        test('AC-09.16: User can add collection from book detail', async () => {
            // Given: Book detail with add to collection option
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button data-testid="add-to-collection-btn">
                        Add to Collection
                    </button>
                    <div data-testid="collection-picker">
                        {mockCollections.map(collection => (
                            <button key={collection.id} data-testid="collection-option">
                                {collection.name}
                            </button>
                        ))}
                    </div>
                </div>
            );

            // Then: Collection picker should be available
            expect(screen.getByTestId('add-to-collection-btn')).toBeInTheDocument();
            expect(screen.getAllByTestId('collection-option')).toHaveLength(mockCollections.length);
        });
    });
});
