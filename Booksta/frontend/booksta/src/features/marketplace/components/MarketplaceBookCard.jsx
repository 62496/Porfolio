import React from 'react';

export default function MarketplaceBookCard({ book, onClick, delay = 0 }) {
    const { marketplace } = book;

    return (
        <div
            onClick={() => onClick(book)}
            className="bg-white border border-[#e5e5e7] rounded-[18px] p-5 cursor-pointer
                       hover:shadow-lg hover:border-[#d1d1d6] transition-all duration-300
                       flex flex-col h-full"
            style={{ animationDelay: `${delay * 0.05}s` }}
        >
            {/* Book Cover */}
            <div className="relative mb-4 overflow-hidden rounded-[12px] bg-[#f5f5f7] aspect-[2/3]">
                {book.imageUrl ? (
                    <img
                        src={book.imageUrl}
                        alt={book.title}
                        className="w-full h-full object-cover"
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" className="w-16 h-16 text-[#d2d2d7]">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25" />
                        </svg>
                    </div>
                )}

                {/* Stock Badge */}
                {marketplace.inStock ? (
                    <div className="absolute top-3 right-3 px-2.5 py-1 bg-green-500 text-white text-[11px] font-semibold rounded-full">
                        In Stock
                    </div>
                ) : (
                    <div className="absolute top-3 right-3 px-2.5 py-1 bg-[#6e6e73] text-white text-[11px] font-semibold rounded-full">
                        Out of Stock
                    </div>
                )}
            </div>

            {/* Book Info */}
            <div className="flex-1 flex flex-col">
                <h3 className="text-[16px] font-semibold text-[#1d1d1f] line-clamp-2 mb-1">
                    {book.title}
                </h3>
                <p className="text-[13px] text-[#6e6e73] mb-3">
                    {book.author || 'Unknown Author'}
                </p>

                {/* Marketplace Info */}
                <div className="mt-auto pt-3 border-t border-[#e5e5e7]">
                    {marketplace.inStock ? (
                        <div className="flex items-end justify-between">
                            <div>
                                <p className="text-[11px] text-[#6e6e73] uppercase tracking-wide">From</p>
                                <p className="text-[22px] font-bold text-[#1d1d1f]">
                                    ${marketplace.lowestPrice?.toFixed(2)}
                                </p>
                            </div>
                            <div className="text-right">
                                <p className="text-[13px] text-[#6e6e73]">
                                    {marketplace.sellerCount} {marketplace.sellerCount === 1 ? 'seller' : 'sellers'}
                                </p>
                                <p className="text-[12px] text-[#86868b]">
                                    {marketplace.totalQuantityAvailable} available
                                </p>
                            </div>
                        </div>
                    ) : (
                        <div className="text-center py-2">
                            <p className="text-[14px] text-[#6e6e73]">No sellers available</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
