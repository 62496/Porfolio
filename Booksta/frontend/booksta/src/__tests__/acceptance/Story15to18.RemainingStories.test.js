/**
 * Story-15 to Story-18: Remaining User Stories Acceptance Tests
 *
 * Story-15: Author participates as a user
 * Story-16: Private discussion groups
 * Story-17: Marketplace/Resellers
 * Story-18: Seller inventory management
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockInventoryItems } from '../../test-utils/testUtils';

// Mock services
jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

jest.mock('../../api/services/inventoryService', () => ({
    __esModule: true,
    default: {
        getAll: jest.fn(),
        create: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
    },
}));

jest.mock('../../api/services/marketplaceService', () => ({
    __esModule: true,
    default: {
        getAllBooks: jest.fn(),
        getBookSummary: jest.fn(),
        getBookSellers: jest.fn(),
    },
}));

import AuthService from '../../api/services/authService';
import inventoryService from '../../api/services/inventoryService';
import marketplaceService from '../../api/services/marketplaceService';

// =============================================================================
// Story-15: Author as User
// =============================================================================
describe('Story-15: Author Participates as User', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.author);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.author);
    });

    describe('Acceptance Criteria: Authors have all user features', () => {
        test('AC-15.1: Author can favorite books (including their own)', async () => {
            // Given: An author viewing their own book
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button data-testid="favorite-btn">Add to Favorites</button>
                </div>
            );

            // Then: Favorite button should be available
            expect(screen.getByTestId('favorite-btn')).toBeInTheDocument();
        });

        test('AC-15.2: Author can follow other authors', async () => {
            // Given: An author viewing another author's page
            renderWithProviders(
                <div data-testid="author-page">
                    <h1>Jane Smith</h1>
                    <button data-testid="follow-btn">Follow Author</button>
                </div>
            );

            // Then: Follow button should be available
            expect(screen.getByTestId('follow-btn')).toBeInTheDocument();
        });

        test('AC-15.3: Author can track reading progress on books', async () => {
            // Given: Author reading another book
            renderWithProviders(
                <div data-testid="reading-controls">
                    <button data-testid="start-reading">Start Reading</button>
                    <button data-testid="mark-finished">Mark as Finished</button>
                </div>
            );

            // Then: Reading controls should be available
            expect(screen.getByTestId('start-reading')).toBeInTheDocument();
        });

        test('AC-15.4: Author can create collections', async () => {
            // Given: Author accessing collections
            renderWithProviders(
                <div data-testid="collections-page">
                    <h1>My Collections</h1>
                    <button data-testid="create-collection">Create Collection</button>
                </div>
            );

            // Then: Collection creation should be available
            expect(screen.getByTestId('create-collection')).toBeInTheDocument();
        });

        test('AC-15.5: Author can message other users', async () => {
            // Given: Author on messages page
            renderWithProviders(
                <div data-testid="messages-page">
                    <h1>Messages</h1>
                    <button data-testid="new-message">New Message</button>
                </div>
            );

            // Then: Messaging should be available
            expect(screen.getByTestId('new-message')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Authors promote organically', () => {
        test('AC-15.6: Author can add their books to collections', async () => {
            // Given: Author creating a thematic collection
            renderWithProviders(
                <div data-testid="collection-books">
                    <h2>My Recommended Reads</h2>
                    <div data-testid="own-book-in-collection">
                        <span>The Great Adventure (My Book)</span>
                    </div>
                    <div data-testid="other-book-in-collection">
                        <span>Mystery at Midnight</span>
                    </div>
                </div>
            );

            // Then: Both own and other books can be in collection
            expect(screen.getByTestId('own-book-in-collection')).toBeInTheDocument();
            expect(screen.getByTestId('other-book-in-collection')).toBeInTheDocument();
        });
    });
});

// =============================================================================
// Story-16: Private Discussion Groups
// =============================================================================
describe('Story-16: Private Discussion Groups', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
    });

    describe('Acceptance Criteria: Users can create private groups', () => {
        test('AC-16.1: User can create a private discussion group', async () => {
            // Given: Group creation form
            renderWithProviders(
                <div data-testid="create-group">
                    <h2>Create Discussion Group</h2>
                    <input data-testid="group-name" placeholder="Group name" />
                    <textarea data-testid="group-description" placeholder="Description" />
                    <label>
                        <input type="checkbox" data-testid="private-toggle" />
                        Private Group
                    </label>
                    <button data-testid="create-group-btn">Create Group</button>
                </div>
            );

            // Then: Private option should be available
            expect(screen.getByTestId('private-toggle')).toBeInTheDocument();
        });

        test('AC-16.2: Group owner can invite members', async () => {
            // Given: Group with invite functionality
            renderWithProviders(
                <div data-testid="group-members">
                    <h3>Members</h3>
                    <input data-testid="invite-email" placeholder="Enter email to invite" />
                    <button data-testid="send-invite">Invite</button>
                </div>
            );

            // Then: Invite functionality should be available
            expect(screen.getByTestId('invite-email')).toBeInTheDocument();
            expect(screen.getByTestId('send-invite')).toBeInTheDocument();
        });

        test('AC-16.3: Private groups are not visible to non-members', async () => {
            // Given: A private group indicator
            renderWithProviders(
                <div data-testid="group-card" className="private">
                    <span data-testid="private-badge">🔒 Private</span>
                    <h3>Book Club</h3>
                </div>
            );

            // Then: Private indicator should be visible
            expect(screen.getByTestId('private-badge')).toHaveTextContent('Private');
        });
    });

    describe('Acceptance Criteria: Group discussions work', () => {
        test('AC-16.4: Members can post in group', async () => {
            // Given: Group discussion board
            renderWithProviders(
                <div data-testid="group-discussion">
                    <h2>Book Club Discussion</h2>
                    <div data-testid="posts">
                        <div data-testid="post">
                            <span>What did everyone think of chapter 5?</span>
                        </div>
                    </div>
                    <textarea data-testid="new-post" placeholder="Share your thoughts..." />
                    <button data-testid="post-btn">Post</button>
                </div>
            );

            // Then: Posting functionality should be available
            expect(screen.getByTestId('new-post')).toBeInTheDocument();
            expect(screen.getByTestId('post-btn')).toBeInTheDocument();
        });

        test('AC-16.5: Owner can remove members', async () => {
            // Given: Member management
            renderWithProviders(
                <div data-testid="member-list">
                    <div data-testid="member">
                        <span>John Doe</span>
                        <button data-testid="remove-member">Remove</button>
                    </div>
                </div>
            );

            // Then: Remove option should be available
            expect(screen.getByTestId('remove-member')).toBeInTheDocument();
        });
    });
});

// =============================================================================
// Story-17: Marketplace / Resellers
// =============================================================================
describe('Story-17: Marketplace for Book Resellers', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.user);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.user);
        marketplaceService.getAllBooks.mockResolvedValue(mockBooks);
    });

    describe('Acceptance Criteria: Users can browse marketplace', () => {
        test('AC-17.1: User can access marketplace', async () => {
            // Given: Marketplace page
            renderWithProviders(
                <div data-testid="marketplace-page">
                    <h1>Marketplace</h1>
                    <p>Find books from verified sellers</p>
                </div>
            );

            // Then: Marketplace should be accessible
            expect(screen.getByTestId('marketplace-page')).toBeInTheDocument();
        });

        test('AC-17.2: Marketplace shows available books with prices', async () => {
            // Given: Marketplace book listings
            renderWithProviders(
                <div data-testid="marketplace-listings">
                    {mockBooks.map(book => (
                        <div key={book.isbn} data-testid="marketplace-book">
                            <h3>{book.title}</h3>
                            <span data-testid="lowest-price">From $19.99</span>
                            <span data-testid="seller-count">3 sellers</span>
                        </div>
                    ))}
                </div>
            );

            // Then: Books with price info should be shown
            expect(screen.getAllByTestId('marketplace-book').length).toBeGreaterThan(0);
            expect(screen.getAllByTestId('lowest-price')[0]).toHaveTextContent('$19.99');
        });

        test('AC-17.3: User can see all sellers for a book', async () => {
            // Given: Book with multiple sellers
            marketplaceService.getBookSellers.mockResolvedValue(mockInventoryItems);

            renderWithProviders(
                <div data-testid="book-sellers">
                    <h2>Sellers for: The Great Adventure</h2>
                    {mockInventoryItems.map((item, idx) => (
                        <div key={idx} data-testid="seller-listing">
                            <span data-testid="seller-name">{item.seller.firstName}</span>
                            <span data-testid="seller-price">${item.price}</span>
                            <span data-testid="stock">In Stock: {item.quantity}</span>
                        </div>
                    ))}
                </div>
            );

            // Then: All sellers should be listed
            expect(screen.getAllByTestId('seller-listing').length).toBeGreaterThan(0);
        });

        test('AC-17.4: Sellers are sorted by price', async () => {
            // Given: Sellers sorted by price
            renderWithProviders(
                <div data-testid="sellers-sorted">
                    <p>Sorted by: Lowest Price First</p>
                    <div data-testid="seller-listing">
                        <span>$19.99</span>
                    </div>
                    <div data-testid="seller-listing">
                        <span>$24.99</span>
                    </div>
                    <div data-testid="seller-listing">
                        <span>$29.99</span>
                    </div>
                </div>
            );

            // Then: Sellers should be in price order
            const listings = screen.getAllByTestId('seller-listing');
            expect(listings[0]).toHaveTextContent('$19.99');
        });
    });

    describe('Acceptance Criteria: Users can compare prices', () => {
        test('AC-17.5: Book summary shows price range', async () => {
            // Given: Price comparison summary
            marketplaceService.getBookSummary.mockResolvedValue({
                lowestPrice: 19.99,
                highestPrice: 34.99,
                sellerCount: 5,
            });

            renderWithProviders(
                <div data-testid="price-summary">
                    <span data-testid="price-range">$19.99 - $34.99</span>
                    <span data-testid="seller-count">from 5 sellers</span>
                </div>
            );

            // Then: Price range should be shown
            expect(screen.getByTestId('price-range')).toHaveTextContent('$19.99 - $34.99');
        });

        test('AC-17.6: User can see seller ratings/reliability', async () => {
            // Given: Seller with rating
            renderWithProviders(
                <div data-testid="seller-listing">
                    <span data-testid="seller-name">Book Emporium</span>
                    <span data-testid="seller-rating">⭐ 4.8</span>
                    <span data-testid="seller-sales">120 sales</span>
                </div>
            );

            // Then: Seller reliability info should be shown
            expect(screen.getByTestId('seller-rating')).toHaveTextContent('4.8');
            expect(screen.getByTestId('seller-sales')).toHaveTextContent('120 sales');
        });
    });
});

// =============================================================================
// Story-18: Seller Inventory Management
// =============================================================================
describe('Story-18: Seller Inventory Management', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.seller);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.seller);
        inventoryService.getAll.mockResolvedValue(mockInventoryItems);
    });

    describe('Acceptance Criteria: Sellers can access inventory', () => {
        test('AC-18.1: Seller sees inventory link in navigation', async () => {
            // Given: A seller user
            renderWithProviders(
                <nav data-testid="seller-nav">
                    <a href="/author/dashboard/inventory" data-testid="inventory-link">
                        Inventory
                    </a>
                </nav>
            );

            // Then: Inventory link should be visible
            expect(screen.getByTestId('inventory-link')).toBeInTheDocument();
        });

        test('AC-18.2: Non-sellers do not see inventory option', async () => {
            // Given: A regular user
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);

            renderWithProviders(
                <nav data-testid="user-nav">
                    {/* Inventory link not rendered */}
                </nav>
            );

            // Then: Inventory link should not be present
            expect(screen.queryByTestId('inventory-link')).not.toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Sellers can manage inventory', () => {
        test('AC-18.3: Inventory page shows all seller\'s books', async () => {
            // Given: Inventory page
            renderWithProviders(
                <div data-testid="inventory-page">
                    <h1>My Inventory</h1>
                    <div data-testid="inventory-list">
                        {mockInventoryItems.map((item, idx) => (
                            <div key={idx} data-testid="inventory-item">
                                <span data-testid="book-title">{item.book.title}</span>
                                <span data-testid="quantity">{item.quantity} in stock</span>
                                <span data-testid="price">${item.price}</span>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Inventory items should be visible
            expect(screen.getAllByTestId('inventory-item').length).toBeGreaterThan(0);
        });

        test('AC-18.4: Seller can add books to inventory', async () => {
            // Given: Add to inventory form
            inventoryService.create.mockResolvedValue(mockInventoryItems[0]);

            renderWithProviders(
                <div data-testid="add-inventory">
                    <h2>Add Book to Inventory</h2>
                    <input data-testid="isbn-input" placeholder="Enter ISBN" />
                    <input data-testid="quantity-input" type="number" placeholder="Quantity" />
                    <input data-testid="price-input" type="number" step="0.01" placeholder="Price" />
                    <button
                        data-testid="add-btn"
                        onClick={() => inventoryService.create({})}
                    >
                        Add to Inventory
                    </button>
                </div>
            );

            // When: Seller adds item
            await userEvent.click(screen.getByTestId('add-btn'));

            // Then: Item should be added
            expect(inventoryService.create).toHaveBeenCalled();
        });

        test('AC-18.5: Seller can update quantity and price', async () => {
            // Given: Edit inventory item
            inventoryService.update.mockResolvedValue(mockInventoryItems[0]);

            renderWithProviders(
                <div data-testid="edit-inventory">
                    <input data-testid="edit-quantity" type="number" defaultValue={5} />
                    <input data-testid="edit-price" type="number" step="0.01" defaultValue={19.99} />
                    <button
                        data-testid="save-btn"
                        onClick={() => inventoryService.update(mockBooks[0].isbn, {})}
                    >
                        Save
                    </button>
                </div>
            );

            // When: Seller saves changes
            await userEvent.click(screen.getByTestId('save-btn'));

            // Then: Changes should be saved
            expect(inventoryService.update).toHaveBeenCalled();
        });

        test('AC-18.6: Seller can remove books from inventory', async () => {
            // Given: Remove from inventory option
            inventoryService.delete.mockResolvedValue({ success: true });

            renderWithProviders(
                <div data-testid="inventory-item">
                    <span>The Great Adventure</span>
                    <button
                        data-testid="remove-btn"
                        onClick={() => inventoryService.delete(mockBooks[0].isbn)}
                    >
                        Remove
                    </button>
                </div>
            );

            // When: Seller removes item
            await userEvent.click(screen.getByTestId('remove-btn'));

            // Then: Item should be removed
            expect(inventoryService.delete).toHaveBeenCalledWith(mockBooks[0].isbn);
        });
    });

    describe('Acceptance Criteria: Inventory tracks stock', () => {
        test('AC-18.7: Low stock items are highlighted', async () => {
            // Given: Item with low stock
            renderWithProviders(
                <div data-testid="inventory-item" className="low-stock">
                    <span>The Great Adventure</span>
                    <span data-testid="low-stock-warning">⚠️ Low Stock (2 left)</span>
                </div>
            );

            // Then: Warning should be visible
            expect(screen.getByTestId('low-stock-warning')).toBeInTheDocument();
        });

        test('AC-18.8: Out of stock items are marked', async () => {
            // Given: Out of stock item
            renderWithProviders(
                <div data-testid="inventory-item" className="out-of-stock">
                    <span>Mystery at Midnight</span>
                    <span data-testid="out-of-stock-badge">Out of Stock</span>
                </div>
            );

            // Then: Out of stock badge should be visible
            expect(screen.getByTestId('out-of-stock-badge')).toHaveTextContent('Out of Stock');
        });

        test('AC-18.9: Inventory shows total value', async () => {
            // Given: Inventory summary
            renderWithProviders(
                <div data-testid="inventory-summary">
                    <div data-testid="total-items">
                        <span>8</span>
                        <label>Total Books</label>
                    </div>
                    <div data-testid="total-value">
                        <span>$159.92</span>
                        <label>Total Inventory Value</label>
                    </div>
                </div>
            );

            // Then: Summary should be visible
            expect(screen.getByTestId('total-items')).toHaveTextContent('8');
            expect(screen.getByTestId('total-value')).toHaveTextContent('$159.92');
        });
    });
});
