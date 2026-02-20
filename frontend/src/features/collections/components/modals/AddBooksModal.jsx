import React from 'react';
import Modal from '../../../../components/common/Modal';
import Button from '../../../../components/common/Button';
import GenericInput from '../../../../components/forms/GenericInput';

const AddBooksModal = ({
    isOpen,
    collection,
    filteredBooks,
    searchTerm,
    setSearchTerm,
    loading,
    isBookInCollection,
    onAddBook,
    onRemoveBook,
    onClose,
}) => {
    if (!collection) return null;

    const collectionBooks = collection.books || [];

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Manage Books">
            <p className="text-sm text-[#6e6e73] -mt-2 mb-4">{collection.name}</p>

            {/* Current books in collection */}
            {collectionBooks.length > 0 && (
                <div className="mb-6">
                    <h3 className="text-sm font-medium text-[#1d1d1f] mb-3">
                        In this collection ({collectionBooks.length})
                    </h3>
                    <div className="flex flex-wrap gap-2">
                        {collectionBooks.map((book) => (
                            <div
                                key={book.isbn}
                                className="flex items-center gap-2 px-3 py-2 bg-[#f0f7ff] border border-[#0066cc]/20 rounded-lg"
                            >
                                <span className="text-sm text-[#1d1d1f] max-w-[200px] truncate">
                                    {book.title}
                                </span>
                                <button
                                    onClick={() => onRemoveBook(book.isbn)}
                                    disabled={loading}
                                    className="w-5 h-5 flex items-center justify-center text-[#6e6e73] hover:text-red-500 transition-colors disabled:opacity-50"
                                >
                                    <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Search Books */}
            <div>
                <div className="relative">
                    <GenericInput
                        label="Search books to add"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        placeholder="Search by title or ISBN..."
                    />
                    <svg
                        className="absolute right-3 top-[42px] w-5 h-5 text-[#6e6e73]"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                        />
                    </svg>
                </div>
            </div>

            {/* Book Results */}
            <div className="mt-4 space-y-2 max-h-[300px] overflow-y-auto">
                {filteredBooks.length === 0 ? (
                    <div className="text-center py-8 text-[#6e6e73] bg-[#f5f5f7] rounded-xl">
                        <svg
                            className="w-12 h-12 mx-auto mb-2 text-[#86868b]"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="1.5"
                                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                            />
                        </svg>
                        <p className="text-sm">No books found</p>
                    </div>
                ) : (
                    filteredBooks.slice(0, 20).map((book) => {
                        const inCollection = isBookInCollection(book.isbn);
                        return (
                            <div
                                key={book.isbn}
                                className={`flex items-center justify-between px-4 py-3 rounded-xl transition-colors ${
                                    inCollection
                                        ? 'bg-[#f0f7ff] border border-[#0066cc]/20'
                                        : 'bg-[#f5f5f7] hover:bg-[#e5e5e7]'
                                }`}
                            >
                                <div className="flex items-center gap-3 flex-1 min-w-0">
                                    {/* Book cover */}
                                    <div className="w-12 h-16 bg-[#e5e5e7] rounded flex-shrink-0 overflow-hidden">
                                        {book.image?.url ? (
                                            <img
                                                src={book.image.url}
                                                alt={book.title}
                                                className="w-full h-full object-cover"
                                            />
                                        ) : (
                                            <div className="w-full h-full flex items-center justify-center">
                                                <svg
                                                    className="w-6 h-6 text-[#86868b]"
                                                    fill="none"
                                                    stroke="currentColor"
                                                    viewBox="0 0 24 24"
                                                >
                                                    <path
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                        strokeWidth="1.5"
                                                        d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                                                    />
                                                </svg>
                                            </div>
                                        )}
                                    </div>
                                    <div className="min-w-0">
                                        <p className="font-medium text-[#1d1d1f] truncate">
                                            {book.title}
                                        </p>
                                        <p className="text-xs text-[#6e6e73]">
                                            ISBN: {book.isbn}
                                        </p>
                                        {book.authors && book.authors.length > 0 && (
                                            <p className="text-xs text-[#6e6e73] truncate">
                                                {book.authors.map(a => `${a.firstName} ${a.lastName}`).join(', ')}
                                            </p>
                                        )}
                                    </div>
                                </div>
                                {inCollection ? (
                                    <Button
                                        type="small-danger"
                                        label="Remove"
                                        onClick={() => onRemoveBook(book.isbn)}
                                        disabled={loading}
                                    />
                                ) : (
                                    <Button
                                        type="small-secondary"
                                        label="Add"
                                        onClick={() => onAddBook(book.isbn)}
                                        disabled={loading}
                                    />
                                )}
                            </div>
                        );
                    })
                )}
                {filteredBooks.length > 20 && (
                    <p className="text-center text-sm text-[#6e6e73] py-2">
                        Showing 20 of {filteredBooks.length} books. Refine your search.
                    </p>
                )}
            </div>

            {/* Close Button */}
            <div className="mt-6">
                <Button
                    type="modal-secondary"
                    label="Done"
                    onClick={onClose}
                    className="w-full"
                />
            </div>
        </Modal>
    );
};

export default AddBooksModal;
