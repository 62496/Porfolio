import React from "react";

export default function InventoryBookItem({
    item,
    onEdit,
    onDelete,
}) {
    const book = item?.book;
    const totalValue = (item.quantity || 0) * (item.pricePerUnit || 0);

    return (
        <div className="p-4 sm:p-6 flex flex-col md:flex-row md:items-center gap-4 hover:bg-[#fafafa] transition-colors">
            {/* Book Image */}
            <div className="flex-shrink-0">
                {book?.cover ? (
                    <img
                        src={book.cover}
                        alt={book.title}
                        className="w-16 h-24 object-cover rounded-[8px] shadow-sm"
                    />
                ) : (
                    <div className="w-16 h-24 bg-[#f5f5f7] rounded-[8px] flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-8 h-8 text-[#d2d2d7]">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25" />
                        </svg>
                    </div>
                )}
            </div>

            {/* Book Info */}
            <div className="flex-1 min-w-0">
                <h3 className="text-[16px] font-semibold text-[#1d1d1f] line-clamp-1">
                    {book?.title || "Unknown Book"}
                </h3>
                <p className="text-[14px] text-[#6e6e73] line-clamp-1">
                    {book?.author || "Unknown Author"}
                </p>
                <p className="text-[13px] text-[#a1a1a6] mt-1">
                    ISBN: {book?.isbn || "N/A"}
                </p>
            </div>

            {/* Stock Info */}
            <div className="flex flex-wrap items-center gap-4 md:gap-6">
                {/* Quantity */}
                <div className="text-center min-w-[80px]">
                    <p className="text-[12px] text-[#6e6e73] uppercase tracking-wide">Stock</p>
                    <p className={`text-[20px] font-semibold ${item.quantity === 0 ? 'text-red-500' : 'text-[#1d1d1f]'}`}>
                        {item.quantity}
                    </p>
                </div>

                {/* Price */}
                <div className="text-center min-w-[80px]">
                    <p className="text-[12px] text-[#6e6e73] uppercase tracking-wide">Price</p>
                    <p className="text-[20px] font-semibold text-[#1d1d1f]">
                        ${item.pricePerUnit?.toFixed(2) || "0.00"}
                    </p>
                </div>

                {/* Total Value */}
                <div className="text-center min-w-[100px]">
                    <p className="text-[12px] text-[#6e6e73] uppercase tracking-wide">Value</p>
                    <p className="text-[20px] font-semibold text-[#0071e3]">
                        ${totalValue.toFixed(2)}
                    </p>
                </div>

                {/* Actions */}
                <div className="flex items-center gap-2 ml-auto md:ml-0">
                    <button
                        type="button"
                        onClick={() => onEdit(item)}
                        className="p-2.5 rounded-full hover:bg-[#f5f5f7] transition-colors"
                        title="Edit"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-5 h-5 text-[#6e6e73]">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10" />
                        </svg>
                    </button>
                    <button
                        type="button"
                        onClick={() => onDelete(item)}
                        className="p-2.5 rounded-full hover:bg-red-50 transition-colors"
                        title="Remove from inventory"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-5 h-5 text-red-500">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M14.74 9l-.346 9m-4.788 0L9.26 9m9.968-3.21c.342.052.682.107 1.022.166m-1.022-.165L18.16 19.673a2.25 2.25 0 01-2.244 2.077H8.084a2.25 2.25 0 01-2.244-2.077L4.772 5.79m14.456 0a48.108 48.108 0 00-3.478-.397m-12 .562c.34-.059.68-.114 1.022-.165m0 0a48.11 48.11 0 013.478-.397m7.5 0v-.916c0-1.18-.91-2.164-2.09-2.201a51.964 51.964 0 00-3.32 0c-1.18.037-2.09 1.022-2.09 2.201v.916m7.5 0a48.667 48.667 0 00-7.5 0" />
                        </svg>
                    </button>
                </div>
            </div>
        </div>
    );
}
