import React, { useState, useMemo } from "react";
import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";

export default function AddInventoryModal({ isOpen, onClose, availableBooks, onAdd }) {
    const [selectedBook, setSelectedBook] = useState(null);
    const [quantity, setQuantity] = useState("");
    const [pricePerUnit, setPricePerUnit] = useState("");
    const [bookSearch, setBookSearch] = useState("");
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    const filteredBooks = useMemo(() => {
        if (!bookSearch) return availableBooks.slice(0, 20);
        const searchLower = bookSearch.toLowerCase();
        return availableBooks
            .filter(
                (book) =>
                    book.title?.toLowerCase().includes(searchLower) ||
                    book.author?.toLowerCase().includes(searchLower) ||
                    book.isbn?.toLowerCase().includes(searchLower)
            )
            .slice(0, 20);
    }, [availableBooks, bookSearch]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedBook || !quantity || !pricePerUnit) return;

        const qty = parseInt(quantity);
        const price = parseFloat(pricePerUnit);

        if (isNaN(qty) || qty < 1) {
            setError("Quantity must be at least 1");
            return;
        }
        if (isNaN(price) || price < 0) {
            setError("Price must be a positive number");
            return;
        }

        setSaving(true);
        setError(null);

        const success = await onAdd(selectedBook.isbn, qty, price);
        setSaving(false);

        if (success) {
            handleClose();
        }
    };

    const handleClose = () => {
        setSelectedBook(null);
        setQuantity("");
        setPricePerUnit("");
        setBookSearch("");
        setError(null);
        onClose();
    };

    const handleSelectBook = (book) => {
        setSelectedBook(book);
        setBookSearch("");
    };

    return (
        <Modal isOpen={isOpen} onClose={handleClose} title="Add Book to Inventory">
            <form onSubmit={handleSubmit} className="space-y-5">
                {/* Selected Book Display */}
                {selectedBook ? (
                    <div className="flex items-center gap-4 p-4 bg-[#f5f5f7] rounded-[12px]">
                        {selectedBook.cover && (
                            <img
                                src={selectedBook.cover}
                                alt={selectedBook.title}
                                className="w-14 h-20 object-cover rounded-[8px]"
                            />
                        )}
                        <div className="flex-1 min-w-0">
                            <h3 className="font-semibold text-[#1d1d1f] line-clamp-2">
                                {selectedBook.title}
                            </h3>
                            <p className="text-[14px] text-[#6e6e73]">
                                {selectedBook.author}
                            </p>
                            <p className="text-[13px] text-[#a1a1a6]">
                                ISBN: {selectedBook.isbn}
                            </p>
                        </div>
                        <button
                            type="button"
                            onClick={() => setSelectedBook(null)}
                            className="p-2 hover:bg-[#e5e5e7] rounded-full transition-colors"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-5 h-5 text-[#6e6e73]">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>
                ) : (
                    <div className="space-y-3">
                        <label className="block text-[14px] font-medium text-[#1d1d1f]">
                            Select a Book
                        </label>
                        <input
                            type="text"
                            placeholder="Search by title, author, or ISBN..."
                            value={bookSearch}
                            onChange={(e) => setBookSearch(e.target.value)}
                            className="w-full px-4 py-3 rounded-[12px] border border-[#e5e5e7] text-[15px] focus:border-[#0071e3] focus:outline-none transition-colors"
                        />
                        <div className="max-h-[200px] overflow-y-auto space-y-1">
                            {filteredBooks.length === 0 ? (
                                <p className="text-[14px] text-[#6e6e73] text-center py-4">
                                    {bookSearch ? "No books found" : "All books are in your inventory"}
                                </p>
                            ) : (
                                filteredBooks.map((book) => (
                                    <button
                                        key={book.isbn}
                                        type="button"
                                        onClick={() => handleSelectBook(book)}
                                        className="w-full p-3 rounded-[10px] text-left hover:bg-[#f5f5f7] transition-colors flex items-center gap-3"
                                    >
                                        {book.cover && (
                                            <img
                                                src={book.cover}
                                                alt={book.title}
                                                className="w-10 h-14 object-cover rounded-[6px]"
                                            />
                                        )}
                                        <div className="flex-1 min-w-0">
                                            <p className="font-medium text-[#1d1d1f] truncate">
                                                {book.title}
                                            </p>
                                            <p className="text-[13px] text-[#6e6e73] truncate">
                                                {book.author}
                                            </p>
                                        </div>
                                    </button>
                                ))
                            )}
                        </div>
                    </div>
                )}

                {/* Quantity and Price Fields (shown only when book is selected) */}
                {selectedBook && (
                    <>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-[14px] font-medium text-[#1d1d1f] mb-2">
                                    Quantity
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    value={quantity}
                                    onChange={(e) => setQuantity(e.target.value)}
                                    placeholder="0"
                                    className="w-full px-4 py-3 rounded-[12px] border border-[#e5e5e7] text-[15px] focus:border-[#0071e3] focus:outline-none transition-colors"
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-[14px] font-medium text-[#1d1d1f] mb-2">
                                    Price per Unit ($)
                                </label>
                                <input
                                    type="number"
                                    min="0"
                                    step="0.01"
                                    value={pricePerUnit}
                                    onChange={(e) => setPricePerUnit(e.target.value)}
                                    placeholder="0.00"
                                    className="w-full px-4 py-3 rounded-[12px] border border-[#e5e5e7] text-[15px] focus:border-[#0071e3] focus:outline-none transition-colors"
                                    required
                                />
                            </div>
                        </div>

                        {/* Error Message */}
                        {error && (
                            <div className="p-3 bg-red-50 border border-red-200 rounded-[8px] text-red-600 text-[14px]">
                                {error}
                            </div>
                        )}

                        {/* Action Buttons */}
                        <div className="flex gap-3 pt-2">
                            <Button
                                type="secondary"
                                onClick={handleClose}
                                className="flex-1"
                            >
                                Cancel
                            </Button>
                            <Button
                                type="primary"
                                htmlType="submit"
                                disabled={!selectedBook || !quantity || !pricePerUnit || saving}
                                className="flex-1"
                            >
                                {saving ? "Adding..." : "Add to Inventory"}
                            </Button>
                        </div>
                    </>
                )}
            </form>
        </Modal>
    );
}
