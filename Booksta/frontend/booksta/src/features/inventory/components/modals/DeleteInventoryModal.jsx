import React, { useState } from "react";
import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";

export default function DeleteInventoryModal({ isOpen, onClose, item, onDelete }) {
    const [deleting, setDeleting] = useState(false);

    const handleDelete = async () => {
        if (!item?.book?.isbn) return;

        setDeleting(true);
        const success = await onDelete(item.book.isbn);
        setDeleting(false);

        if (success) {
            onClose();
        }
    };

    const book = item?.book;

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Remove from Inventory">
            <div className="space-y-5">
                {/* Warning Icon */}
                <div className="flex justify-center">
                    <div className="w-16 h-16 rounded-full bg-red-50 flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-8 h-8 text-red-500">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v3.75m-9.303 3.376c-.866 1.5.217 3.374 1.948 3.374h14.71c1.73 0 2.813-1.874 1.948-3.374L13.949 3.378c-.866-1.5-3.032-1.5-3.898 0L2.697 16.126zM12 15.75h.007v.008H12v-.008z" />
                        </svg>
                    </div>
                </div>

                {/* Message */}
                <div className="text-center">
                    <p className="text-[16px] text-[#1d1d1f]">
                        Are you sure you want to remove this book from your inventory?
                    </p>
                </div>

                {/* Book Info */}
                {book && (
                    <div className="flex items-center gap-4 p-4 bg-[#f5f5f7] rounded-[12px]">
                        {book.cover && (
                            <img
                                src={book.cover}
                                alt={book.title}
                                className="w-12 h-16 object-cover rounded-[8px]"
                            />
                        )}
                        <div className="flex-1 min-w-0">
                            <h3 className="font-medium text-[#1d1d1f] line-clamp-1">
                                {book.title}
                            </h3>
                            <p className="text-[14px] text-[#6e6e73]">
                                {item.quantity} units @ €{item.pricePerUnit?.toFixed(2)}
                            </p>
                        </div>
                    </div>
                )}

                <p className="text-[14px] text-[#6e6e73] text-center">
                    This action cannot be undone.
                </p>

                {/* Action Buttons */}
                <div className="flex gap-3 pt-2">
                    <Button
                        type="secondary"
                        onClick={onClose}
                        className="flex-1"
                    >
                        Cancel
                    </Button>
                    <button
                        type="button"
                        onClick={handleDelete}
                        disabled={deleting}
                        className="flex-1 px-6 py-3 bg-red-500 hover:bg-red-600 text-white rounded-[12px] font-medium text-[15px] transition-colors disabled:opacity-50"
                    >
                        {deleting ? "Removing..." : "Remove"}
                    </button>
                </div>
            </div>
        </Modal>
    );
}
