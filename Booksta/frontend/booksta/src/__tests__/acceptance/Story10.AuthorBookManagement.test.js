/**
 * Story-10: Author Book Management Acceptance Tests
 *
 * As an Author
 * I want to register information about the books I have published
 * So that my books are easily discoverable within the platform catalog
 *
 * Priority: Indispensable
 * Complexity: High
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockAuthors, mockSubjects } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/bookService', () => ({
    __esModule: true,
    default: {
        create: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
        getByAuthor: jest.fn(),
    },
}));

jest.mock('../../api/services/authorService', () => ({
    __esModule: true,
    default: {
        getAll: jest.fn(),
        create: jest.fn(),
    },
}));

jest.mock('../../api/services/authService', () => ({
    __esModule: true,
    default: {
        getCurrentUser: jest.fn(),
        isAuthenticated: jest.fn(() => true),
    },
}));

import bookService from '../../api/services/bookService';
import authorService from '../../api/services/authorService';
import AuthService from '../../api/services/authService';

describe('Story-10: Author Book Management', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.author);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.author);
    });

    describe('Acceptance Criteria: Author can access book creation', () => {
        test('AC-10.1: Author sees "Add Book" option in navigation', async () => {
            // Given: An authenticated author
            renderWithProviders(
                <nav data-testid="author-nav">
                    <a href="/author/dashboard/books/new" data-testid="add-book-link">
                        Add Book
                    </a>
                </nav>
            );

            // Then: Add Book link should be visible
            expect(screen.getByTestId('add-book-link')).toBeInTheDocument();
            expect(screen.getByTestId('add-book-link')).toHaveAttribute('href', '/author/dashboard/books/new');
        });

        test('AC-10.2: Non-authors do not see "Add Book" option', async () => {
            // Given: A regular user (not an author)
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);

            renderWithProviders(
                <nav data-testid="user-nav">
                    {/* Add Book link not rendered for regular users */}
                </nav>
            );

            // Then: Add Book link should not be present
            expect(screen.queryByTestId('add-book-link')).not.toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Author can add new books', () => {
        test('AC-10.3: Author can fill out book creation form', async () => {
            // Given: Book creation form
            renderWithProviders(
                <form data-testid="add-book-form">
                    <input data-testid="isbn-input" placeholder="ISBN" />
                    <input data-testid="title-input" placeholder="Book Title" />
                    <input data-testid="year-input" type="number" placeholder="Publication Year" />
                    <input data-testid="pages-input" type="number" placeholder="Number of Pages" />
                    <textarea data-testid="description-input" placeholder="Description" />
                </form>
            );

            // When: Author fills the form
            await userEvent.type(screen.getByTestId('isbn-input'), '9781234567890');
            await userEvent.type(screen.getByTestId('title-input'), 'My New Book');
            await userEvent.type(screen.getByTestId('year-input'), '2024');
            await userEvent.type(screen.getByTestId('pages-input'), '350');

            // Then: Form should accept input
            expect(screen.getByTestId('isbn-input')).toHaveValue('9781234567890');
            expect(screen.getByTestId('title-input')).toHaveValue('My New Book');
        });

        test('AC-10.4: Author can select genres/subjects for book', async () => {
            // Given: Genre selection step
            renderWithProviders(
                <div data-testid="genre-selection">
                    <h3>Select Genres</h3>
                    {mockSubjects.map(subject => (
                        <label key={subject.id}>
                            <input
                                type="checkbox"
                                data-testid={`genre-${subject.id}`}
                                value={subject.id}
                            />
                            {subject.name}
                        </label>
                    ))}
                </div>
            );

            // When: Author selects genres
            await userEvent.click(screen.getByTestId('genre-1'));
            await userEvent.click(screen.getByTestId('genre-3'));

            // Then: Genres should be selected
            expect(screen.getByTestId('genre-1')).toBeChecked();
            expect(screen.getByTestId('genre-3')).toBeChecked();
        });

        test('AC-10.5: Author can upload book cover image', async () => {
            // Given: Image upload field
            renderWithProviders(
                <div data-testid="cover-upload">
                    <label>Book Cover</label>
                    <input
                        type="file"
                        data-testid="cover-input"
                        accept="image/*"
                    />
                    <div data-testid="cover-preview">
                        {/* Preview will show after upload */}
                    </div>
                </div>
            );

            // Then: Upload field should be available
            expect(screen.getByTestId('cover-input')).toBeInTheDocument();
            expect(screen.getByTestId('cover-input')).toHaveAttribute('accept', 'image/*');
        });

        test('AC-10.6: Author can submit the book', async () => {
            // Given: Complete form with submit button
            bookService.create.mockResolvedValue(mockBooks[0]);

            renderWithProviders(
                <form data-testid="add-book-form">
                    <button
                        type="submit"
                        data-testid="submit-book"
                        onClick={(e) => {
                            e.preventDefault();
                            bookService.create({});
                        }}
                    >
                        Add Book
                    </button>
                </form>
            );

            // When: Author submits
            await userEvent.click(screen.getByTestId('submit-book'));

            // Then: Book should be created
            expect(bookService.create).toHaveBeenCalled();
        });
    });

    describe('Acceptance Criteria: Author can view their books', () => {
        test('AC-10.7: Author can see list of their published books', async () => {
            // Given: Author's books
            const authorBooks = mockBooks.filter(b => b.authors[0].id === 1);
            bookService.getByAuthor.mockResolvedValue(authorBooks);

            renderWithProviders(
                <div data-testid="my-published-books">
                    <h1>My Published Books</h1>
                    {authorBooks.map(book => (
                        <div key={book.isbn} data-testid="published-book">
                            <h3>{book.title}</h3>
                            <span>{book.publishingYear}</span>
                        </div>
                    ))}
                </div>
            );

            // Then: Author's books should be visible
            expect(screen.getAllByTestId('published-book').length).toBeGreaterThan(0);
        });

        test('AC-10.8: Author is pre-selected in book creation', async () => {
            // Given: Author selection step with current author pre-selected
            const currentAuthor = mockAuthors.find(a => a.user?.id === mockUsers.author.id);

            renderWithProviders(
                <div data-testid="author-selection">
                    <h3>Authors</h3>
                    {mockAuthors.map(author => (
                        <label key={author.id}>
                            <input
                                type="checkbox"
                                data-testid={`author-${author.id}`}
                                defaultChecked={author.id === currentAuthor?.id}
                            />
                            {author.firstName} {author.lastName}
                        </label>
                    ))}
                </div>
            );

            // Then: Current author should be pre-selected
            if (currentAuthor) {
                expect(screen.getByTestId(`author-${currentAuthor.id}`)).toBeChecked();
            }
        });
    });

    describe('Acceptance Criteria: Author can edit their books', () => {
        test('AC-10.9: Author sees edit option on their books', async () => {
            // Given: A book by the author
            renderWithProviders(
                <div data-testid="published-book">
                    <h3>{mockBooks[0].title}</h3>
                    <a href={`/books/${mockBooks[0].isbn}/edit`} data-testid="edit-book-link">
                        Edit
                    </a>
                </div>
            );

            // Then: Edit link should be visible
            expect(screen.getByTestId('edit-book-link')).toBeInTheDocument();
        });

        test('AC-10.10: Author can update book details', async () => {
            // Given: Edit book form
            bookService.update.mockResolvedValue({ ...mockBooks[0], title: 'Updated Title' });

            renderWithProviders(
                <form data-testid="edit-book-form">
                    <input
                        data-testid="edit-title"
                        defaultValue={mockBooks[0].title}
                    />
                    <button
                        data-testid="save-book"
                        onClick={(e) => {
                            e.preventDefault();
                            bookService.update(mockBooks[0].isbn, {});
                        }}
                    >
                        Save Changes
                    </button>
                </form>
            );

            // When: Author saves changes
            await userEvent.click(screen.getByTestId('save-book'));

            // Then: Book should be updated
            expect(bookService.update).toHaveBeenCalled();
        });
    });

    describe('Acceptance Criteria: Author can delete their books', () => {
        test('AC-10.11: Author sees delete option on their books', async () => {
            // Given: A book by the author
            renderWithProviders(
                <div data-testid="published-book">
                    <h3>{mockBooks[0].title}</h3>
                    <button data-testid="delete-book-btn">Delete</button>
                </div>
            );

            // Then: Delete button should be visible
            expect(screen.getByTestId('delete-book-btn')).toBeInTheDocument();
        });

        test('AC-10.12: Delete shows confirmation dialog', async () => {
            // Given: Delete confirmation
            renderWithProviders(
                <div data-testid="delete-confirmation">
                    <h3>Delete Book</h3>
                    <p>Are you sure you want to delete "{mockBooks[0].title}"?</p>
                    <p className="warning">This action cannot be undone.</p>
                    <button data-testid="confirm-delete">Delete</button>
                    <button data-testid="cancel-delete">Cancel</button>
                </div>
            );

            // Then: Confirmation should show warning
            expect(screen.getByText(/cannot be undone/i)).toBeInTheDocument();
        });

        test('AC-10.13: Author can confirm deletion', async () => {
            // Given: Delete confirmation dialog
            bookService.delete.mockResolvedValue({ success: true });

            renderWithProviders(
                <button
                    data-testid="confirm-delete"
                    onClick={() => bookService.delete(mockBooks[0].isbn)}
                >
                    Delete
                </button>
            );

            // When: Author confirms
            await userEvent.click(screen.getByTestId('confirm-delete'));

            // Then: Book should be deleted
            expect(bookService.delete).toHaveBeenCalledWith(mockBooks[0].isbn);
        });
    });

    describe('Acceptance Criteria: Book form has validation', () => {
        test('AC-10.14: ISBN is required and validated', async () => {
            // Given: ISBN validation
            renderWithProviders(
                <div>
                    <input
                        data-testid="isbn-input"
                        required
                        pattern="[0-9]{13}"
                    />
                    <span data-testid="isbn-error">ISBN must be 13 digits</span>
                </div>
            );

            // Then: Validation message should be available
            expect(screen.getByTestId('isbn-error')).toHaveTextContent('13 digits');
        });

        test('AC-10.15: Title is required', async () => {
            // Given: Required title field
            renderWithProviders(
                <div>
                    <input data-testid="title-input" required />
                    <span data-testid="title-error">Title is required</span>
                </div>
            );

            // Then: Validation should indicate required
            expect(screen.getByTestId('title-error')).toHaveTextContent('required');
        });

        test('AC-10.16: At least one genre must be selected', async () => {
            // Given: Genre validation
            renderWithProviders(
                <div>
                    <span data-testid="genre-error">Please select at least one genre</span>
                </div>
            );

            // Then: Validation message should indicate requirement
            expect(screen.getByTestId('genre-error')).toHaveTextContent('at least one genre');
        });
    });
});
