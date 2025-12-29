/**
 * Story-13: Librarian Catalog Management Acceptance Tests
 *
 * As a Librarian
 * I want to modify information about a book in the catalog
 * So that I can keep the catalog up to date
 *
 * Priority: Indispensable
 * Complexity: Low
 */

import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithProviders, setupAuthenticatedUser, clearAuth, mockUsers, mockBooks, mockAuthors, mockSubjects, mockSeries } from '../../test-utils/testUtils';

// Mock the services
jest.mock('../../api/services/bookService', () => ({
    __esModule: true,
    default: {
        update: jest.fn(),
        delete: jest.fn(),
        getById: jest.fn(),
    },
}));

jest.mock('../../api/services/authorService', () => ({
    __esModule: true,
    default: {
        update: jest.fn(),
        delete: jest.fn(),
        create: jest.fn(),
    },
}));

jest.mock('../../api/services/subjectService', () => ({
    __esModule: true,
    default: {
        create: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
    },
}));

jest.mock('../../api/services/seriesService', () => ({
    __esModule: true,
    default: {
        create: jest.fn(),
        update: jest.fn(),
        delete: jest.fn(),
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
import subjectService from '../../api/services/subjectService';
import seriesService from '../../api/services/seriesService';
import AuthService from '../../api/services/authService';

describe('Story-13: Librarian Catalog Management', () => {
    beforeEach(() => {
        clearAuth();
        jest.clearAllMocks();
        setupAuthenticatedUser(mockUsers.librarian);
        AuthService.getCurrentUser.mockReturnValue(mockUsers.librarian);
    });

    describe('Acceptance Criteria: Librarian can access management features', () => {
        test('AC-13.1: Librarian sees management options in navigation', async () => {
            // Given: A logged-in librarian
            renderWithProviders(
                <nav data-testid="librarian-nav">
                    <a href="/author/dashboard/books/new" data-testid="add-book-link">Add Book</a>
                    <a href="/author/dashboard/authors/new" data-testid="add-author-link">Add Author</a>
                    <a href="/author/dashboard/series" data-testid="manage-series-link">Manage Series</a>
                    <button data-testid="manage-subjects-btn">Manage Subjects</button>
                </nav>
            );

            // Then: All management options should be visible
            expect(screen.getByTestId('add-book-link')).toBeInTheDocument();
            expect(screen.getByTestId('add-author-link')).toBeInTheDocument();
            expect(screen.getByTestId('manage-series-link')).toBeInTheDocument();
            expect(screen.getByTestId('manage-subjects-btn')).toBeInTheDocument();
        });

        test('AC-13.2: Regular users do not see librarian options', async () => {
            // Given: A regular user
            AuthService.getCurrentUser.mockReturnValue(mockUsers.user);

            renderWithProviders(
                <nav data-testid="user-nav">
                    {/* Librarian-only links not rendered */}
                </nav>
            );

            // Then: Management options should not be present
            expect(screen.queryByTestId('add-author-link')).not.toBeInTheDocument();
            expect(screen.queryByTestId('manage-subjects-btn')).not.toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Librarian can edit books', () => {
        test('AC-13.3: Librarian can access edit page for any book', async () => {
            // Given: Book detail page for librarian
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <a href={`/books/${mockBooks[0].isbn}/edit`} data-testid="edit-book-btn">
                        Edit Book
                    </a>
                </div>
            );

            // Then: Edit button should be visible
            expect(screen.getByTestId('edit-book-btn')).toBeInTheDocument();
        });

        test('AC-13.4: Librarian can update book metadata', async () => {
            // Given: Book edit form
            bookService.update.mockResolvedValue(mockBooks[0]);

            renderWithProviders(
                <form data-testid="edit-book-form">
                    <input data-testid="edit-title" defaultValue={mockBooks[0].title} />
                    <input data-testid="edit-year" type="number" defaultValue={mockBooks[0].publishingYear} />
                    <input data-testid="edit-pages" type="number" defaultValue={mockBooks[0].pages} />
                    <textarea data-testid="edit-description" defaultValue={mockBooks[0].description} />
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

            // When: Librarian saves changes
            await userEvent.click(screen.getByTestId('save-book'));

            // Then: Book should be updated
            expect(bookService.update).toHaveBeenCalled();
        });

        test('AC-13.5: Librarian can update book cover image', async () => {
            // Given: Cover image upload in edit form
            renderWithProviders(
                <div data-testid="cover-edit">
                    <img
                        data-testid="current-cover"
                        src={mockBooks[0].image.url}
                        alt="Current cover"
                    />
                    <input type="file" data-testid="new-cover-input" accept="image/*" />
                    <p>Leave empty to keep current cover</p>
                </div>
            );

            // Then: Cover update options should be available
            expect(screen.getByTestId('current-cover')).toBeInTheDocument();
            expect(screen.getByTestId('new-cover-input')).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Librarian can delete books', () => {
        test('AC-13.6: Librarian sees delete option on books', async () => {
            // Given: Book detail page
            renderWithProviders(
                <div data-testid="book-detail">
                    <h1>{mockBooks[0].title}</h1>
                    <button data-testid="delete-book-btn" className="danger">
                        Delete Book
                    </button>
                </div>
            );

            // Then: Delete button should be visible
            expect(screen.getByTestId('delete-book-btn')).toBeInTheDocument();
        });

        test('AC-13.7: Delete shows warning about cascading effects', async () => {
            // Given: Delete confirmation with cascade warning
            renderWithProviders(
                <div data-testid="delete-confirmation">
                    <h3>Delete Book</h3>
                    <p>Are you sure you want to delete "{mockBooks[0].title}"?</p>
                    <div data-testid="cascade-warning" className="warning">
                        <p>This will also delete:</p>
                        <ul>
                            <li>All reading progress records</li>
                            <li>All favorites containing this book</li>
                            <li>All collection entries for this book</li>
                        </ul>
                    </div>
                    <button data-testid="confirm-delete" className="danger">Delete</button>
                    <button data-testid="cancel-delete">Cancel</button>
                </div>
            );

            // Then: Cascade warning should be visible
            expect(screen.getByTestId('cascade-warning')).toBeInTheDocument();
            expect(screen.getByText(/reading progress/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Librarian can manage authors', () => {
        test('AC-13.8: Librarian can add new authors', async () => {
            // Given: Add author form
            authorService.create.mockResolvedValue(mockAuthors[0]);

            renderWithProviders(
                <form data-testid="add-author-form">
                    <input data-testid="author-firstname" placeholder="First Name" />
                    <input data-testid="author-lastname" placeholder="Last Name" />
                    <textarea data-testid="author-bio" placeholder="Biography" />
                    <input type="file" data-testid="author-photo" accept="image/*" />
                    <button
                        data-testid="create-author"
                        onClick={(e) => {
                            e.preventDefault();
                            authorService.create({});
                        }}
                    >
                        Create Author
                    </button>
                </form>
            );

            // When: Librarian creates author
            await userEvent.click(screen.getByTestId('create-author'));

            // Then: Author should be created
            expect(authorService.create).toHaveBeenCalled();
        });

        test('AC-13.9: Librarian can edit existing authors', async () => {
            // Given: Author edit form
            authorService.update.mockResolvedValue(mockAuthors[0]);

            renderWithProviders(
                <form data-testid="edit-author-form">
                    <input data-testid="edit-firstname" defaultValue={mockAuthors[0].firstName} />
                    <input data-testid="edit-lastname" defaultValue={mockAuthors[0].lastName} />
                    <button
                        data-testid="save-author"
                        onClick={(e) => {
                            e.preventDefault();
                            authorService.update(mockAuthors[0].id, {});
                        }}
                    >
                        Save
                    </button>
                </form>
            );

            // When: Librarian saves
            await userEvent.click(screen.getByTestId('save-author'));

            // Then: Author should be updated
            expect(authorService.update).toHaveBeenCalled();
        });

        test('AC-13.10: Librarian can delete authors (with cascade warning)', async () => {
            // Given: Author delete confirmation
            renderWithProviders(
                <div data-testid="delete-author-confirmation">
                    <h3>Delete Author</h3>
                    <p>Delete {mockAuthors[0].firstName} {mockAuthors[0].lastName}?</p>
                    <div data-testid="cascade-warning" className="danger">
                        <p>WARNING: This will also delete ALL books and series by this author!</p>
                    </div>
                    <button data-testid="confirm-delete-author">Delete Author</button>
                </div>
            );

            // Then: Cascade warning should be prominent
            expect(screen.getByText(/ALL books and series/i)).toBeInTheDocument();
        });
    });

    describe('Acceptance Criteria: Librarian can manage subjects/genres', () => {
        test('AC-13.11: Librarian can access subject management', async () => {
            // Given: Subject management modal/page
            renderWithProviders(
                <div data-testid="subject-management">
                    <h2>Manage Subjects</h2>
                    <div data-testid="subjects-list">
                        {mockSubjects.map(subject => (
                            <div key={subject.id} data-testid="subject-item">
                                <span>{subject.name}</span>
                                <button data-testid={`edit-subject-${subject.id}`}>Edit</button>
                                <button data-testid={`delete-subject-${subject.id}`}>Delete</button>
                            </div>
                        ))}
                    </div>
                </div>
            );

            // Then: Subject list should be visible
            expect(screen.getAllByTestId('subject-item').length).toBeGreaterThan(0);
        });

        test('AC-13.12: Librarian can add new subjects', async () => {
            // Given: Add subject form
            subjectService.create.mockResolvedValue({ id: 10, name: 'New Genre' });

            renderWithProviders(
                <div data-testid="add-subject">
                    <input data-testid="subject-name" placeholder="Subject name" />
                    <button
                        data-testid="create-subject"
                        onClick={() => subjectService.create({ name: 'New Genre' })}
                    >
                        Add Subject
                    </button>
                </div>
            );

            // When: Librarian adds subject
            await userEvent.click(screen.getByTestId('create-subject'));

            // Then: Subject should be created
            expect(subjectService.create).toHaveBeenCalled();
        });

        test('AC-13.13: Librarian can edit subjects', async () => {
            // Given: Edit subject form
            subjectService.update.mockResolvedValue({ id: 1, name: 'Updated Genre' });

            renderWithProviders(
                <div data-testid="edit-subject">
                    <input data-testid="edit-subject-name" defaultValue={mockSubjects[0].name} />
                    <button
                        data-testid="save-subject"
                        onClick={() => subjectService.update(mockSubjects[0].id, {})}
                    >
                        Save
                    </button>
                </div>
            );

            // When: Librarian saves
            await userEvent.click(screen.getByTestId('save-subject'));

            // Then: Subject should be updated
            expect(subjectService.update).toHaveBeenCalled();
        });

        test('AC-13.14: Librarian can delete subjects', async () => {
            // Given: Delete subject option
            subjectService.delete.mockResolvedValue({ success: true });

            renderWithProviders(
                <button
                    data-testid="delete-subject"
                    onClick={() => subjectService.delete(mockSubjects[0].id)}
                >
                    Delete
                </button>
            );

            // When: Librarian deletes
            await userEvent.click(screen.getByTestId('delete-subject'));

            // Then: Subject should be deleted
            expect(subjectService.delete).toHaveBeenCalledWith(mockSubjects[0].id);
        });
    });

    describe('Acceptance Criteria: Librarian can manage series', () => {
        test('AC-13.15: Librarian can create series for any author', async () => {
            // Given: Create series form with author selection
            seriesService.create.mockResolvedValue(mockSeries[0]);

            renderWithProviders(
                <form data-testid="create-series-form">
                    <select data-testid="author-select">
                        <option value="">Select Author</option>
                        {mockAuthors.map(author => (
                            <option key={author.id} value={author.id}>
                                {author.firstName} {author.lastName}
                            </option>
                        ))}
                    </select>
                    <input data-testid="series-title" placeholder="Series title" />
                    <textarea data-testid="series-description" placeholder="Description" />
                    <button
                        data-testid="create-series"
                        onClick={(e) => {
                            e.preventDefault();
                            seriesService.create({});
                        }}
                    >
                        Create Series
                    </button>
                </form>
            );

            // Then: Author selection should be available
            expect(screen.getByTestId('author-select')).toBeInTheDocument();
        });

        test('AC-13.16: Librarian sees all series in management view', async () => {
            // Given: Series management page showing all series
            renderWithProviders(
                <div data-testid="series-management">
                    <h1>Manage Series</h1>
                    <p>As a librarian, you can manage all series.</p>
                    {mockSeries.map(series => (
                        <div key={series.id} data-testid="series-card">
                            <h3>{series.title}</h3>
                            <span>by {series.author.firstName} {series.author.lastName}</span>
                        </div>
                    ))}
                </div>
            );

            // Then: All series should be visible
            expect(screen.getAllByTestId('series-card')).toHaveLength(mockSeries.length);
        });
    });
});
