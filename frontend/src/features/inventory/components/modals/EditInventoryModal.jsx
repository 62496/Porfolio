import React, { useState, useEffect } from "react";
import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";

export default function EditInventoryModal({ isOpen, onClose, item, onUpdate }) {
    const [quantity, setQuantity] = useState("");
    const [pricePerUnit, setPricePerUnit] = useState("");
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (item) {
            setQuantity(item.quantity?.toString() || "");
            setPricePerUnit(item.pricePerUnit?.toString() || "");
        }
    }, [item]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!item?.book?.isbn || !quantity || !pricePerUnit) return;

        const qty = parseInt(quantity);
        const price = parseFloat(pricePerUnit);

        if (isNaN(qty) || qty < 0) {
            setError("Quantity must be 0 or greater");
            return;
        }
        if (isNaN(price) || price < 0) {
            setError("Price must be a positive number");
            return;
        }

        setSaving(true);
        setError(null);

        const success = await onUpdate(item.book.isbn, qty, price);
        setSaving(false);

        if (success) {
            handleClose();
        }
    };

    const handleClose = () => {
        setError(null);
        onClose();
    };

    const book = item?.book;

    return (
        <Modal isOpen={isOpen} onClose={handleClose} title="Edit Inventory Item">
            <form onSubmit={handleSubmit} className="space-y-5">
                {/* Book Info */}
                {book && (
                    <div className="flex items-center gap-4 p-4 bg-[#f5f5f7] rounded-[12px]">
                        {book.cover && (
                            <img
                                src={book.cover}
                                alt={book.title}
                                className="w-14 h-20 object-cover rounded-[8px]"
                            />
                        )}
                        <div className="flex-1 min-w-0">
                            <h3 className="font-semibold text-[#1d1d1f] line-clamp-2">
                                {book.title}
                            </h3>
                            <p className="text-[14px] text-[#6e6e73]">
                                {book.author}
                            </p>
                            <p className="text-[13px] text-[#a1a1a6]">
                                ISBN: {book.isbn}
                            </p>
                        </div>
                    </div>
                )}

                {/* Quantity and Price Fields */}
                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-[14px] font-medium text-[#1d1d1f] mb-2">
                            Quantity
                        </label>
                        <input
                            type="number"
                            min="0"
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

                {/* Total Value Display */}
                {quantity && pricePerUnit && (
                    <div className="p-4 bg-[#f5f5f7] rounded-[12px]">
                        <div className="flex justify-between items-center">
                            <span className="text-[14px] text-[#6e6e73]">Total Inventory Value</span>
                            <span className="text-[18px] font-semibold text-[#1d1d1f]">
                                ${(parseFloat(quantity || 0) * parseFloat(pricePerUnit || 0)).toFixed(2)}
                            </span>
                        </div>
                    </div>
                )}

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
                        disabled={!quantity || !pricePerUnit || saving}
                        className="flex-1"
                    >
                        {saving ? "Saving..." : "Save Changes"}
                    </Button>
                </div>
            </form>
        </Modal>
    );
}
