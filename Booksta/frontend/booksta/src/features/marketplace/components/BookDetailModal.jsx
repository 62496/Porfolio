import React, { useState } from 'react';
import Modal from '../../../components/common/Modal';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Button from '../../../components/common/Button';
import SellerListItem from './SellerListItem';
import ContactSellerModal from './ContactSellerModal';

export default function BookDetailModal({
    isOpen,
    onClose,
    book,
    marketplace,
    sellers,
    loadingSellers,
}) {
    const [descriptionExpanded, setDescriptionExpanded] = useState(false);
    const [contactSeller, setContactSeller] = useState(null);

    if (!book) return null;

    const lowestPriceSeller = sellers.length > 0 ? sellers[0] : null;
    const hasMultiplePrices = sellers.length > 1 && sellers.some(s => s.pricePerUnit !== lowestPriceSeller?.pricePerUnit);

    // Truncate description at ~150 chars for preview
    const descriptionPreview = book.description?.length > 150
        ? book.description.substring(0, 150).trim() + '...'
        : book.description;

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Book Details">
            {/* Content */}
            <div className="-mx-6 -my-6 flex flex-col max-h-[calc(90vh-80px)]">
                {/* Scrollable Content */}
                <div className="flex-1 overflow-y-auto px-6 py-5">
                    <div className="space-y-5">
                        {/* Book Info Header */}
                        <div className="flex gap-4">
                            {/* Cover */}
                            <div className="flex-shrink-0 w-28">
                                {book.imageUrl ? (
                                    <img
                                        src={book.imageUrl}
                                        alt={book.title}
                                        className="w-full aspect-[2/3] object-cover rounded-[10px] shadow-md"
                                    />
                                ) : (
                                    <div className="w-full aspect-[2/3] bg-[#f5f5f7] rounded-[10px] flex items-center justify-center">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1" className="w-10 h-10 text-[#d2d2d7]">
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25" />
                                        </svg>
                                    </div>
                                )}
                            </div>

                            {/* Details */}
                            <div className="flex-1 min-w-0">
                                <h3 className="text-[22px] font-semibold text-[#1d1d1f] leading-tight mb-1">
                                    {book.title}
                                </h3>
                                <p className="text-[15px] text-[#6e6e73] mb-3">
                                    {book.author || 'Unknown Author'}
                                </p>
                                <div className="flex flex-wrap gap-2 text-[12px] text-[#86868b]">
                                    {book.publishingYear && (
                                        <span className="px-2 py-1 bg-[#f5f5f7] rounded-md">
                                            {book.publishingYear}
                                        </span>
                                    )}
                                    {book.pages && (
                                        <span className="px-2 py-1 bg-[#f5f5f7] rounded-md">
                                            {book.pages} pages
                                        </span>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Description with Read More */}
                        {book.description && (
                            <div>
                                <p className="text-[14px] text-[#6e6e73] leading-relaxed">
                                    {descriptionExpanded ? book.description : descriptionPreview}
                                </p>
                                {book.description.length > 150 && (
                                    <button
                                        onClick={() => setDescriptionExpanded(!descriptionExpanded)}
                                        className="text-[14px] text-[#0066cc] font-medium mt-1 hover:underline"
                                    >
                                        {descriptionExpanded ? 'Show less' : 'Read more'}
                                    </button>
                                )}
                            </div>
                        )}

                        {/* Sellers Section */}
                        <div>
                            <div className="flex items-center justify-between mb-3">
                                <h4 className="text-[15px] font-semibold text-[#1d1d1f]">
                                    Available Sellers
                                </h4>
                                {sellers.length > 0 && (
                                    <span className="text-[13px] text-[#6e6e73]">
                                        {marketplace?.totalQuantityAvailable || sellers.reduce((sum, s) => sum + s.quantity, 0)} copies total
                                    </span>
                                )}
                            </div>

                            {loadingSellers ? (
                                <LoadingSpinner message="Loading sellers..." />
                            ) : sellers.length > 0 ? (
                                <div className="space-y-2 max-h-[250px] overflow-y-auto pr-1">
                                    {sellers.map((seller, index) => (
                                        <SellerListItem
                                            key={seller.sellerId}
                                            seller={seller}
                                            isLowestPrice={index === 0 && hasMultiplePrices}
                                            showPriceLabel={false}
                                            onContact={setContactSeller}
                                        />
                                    ))}
                                </div>
                            ) : (
                                <div className="text-center py-6 bg-[#f5f5f7] rounded-[12px]">
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-10 h-10 text-[#86868b] mx-auto mb-2">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 21v-7.5a.75.75 0 01.75-.75h3a.75.75 0 01.75.75V21m-4.5 0H2.36m11.14 0H18m0 0h3.64m-1.39 0V9.349m-16.5 11.65V9.35m0 0a3.001 3.001 0 003.75-.615A2.993 2.993 0 009.75 9.75c.896 0 1.7-.393 2.25-1.016a2.993 2.993 0 002.25 1.016c.896 0 1.7-.393 2.25-1.016a3.001 3.001 0 003.75.614m-16.5 0a3.004 3.004 0 01-.621-4.72L4.318 3.44A1.5 1.5 0 015.378 3h13.243a1.5 1.5 0 011.06.44l1.19 1.189a3 3 0 01-.621 4.72m-13.5 8.65h3.75a.75.75 0 00.75-.75V13.5a.75.75 0 00-.75-.75H6.75a.75.75 0 00-.75.75v3.75c0 .415.336.75.75.75z" />
                                    </svg>
                                    <p className="text-[#6e6e73] font-medium text-[14px]">No sellers available</p>
                                    <p className="text-[13px] text-[#86868b] mt-1">Check back later for listings</p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* Sticky Footer with Price & CTA */}
                {marketplace?.inStock && lowestPriceSeller && (
                    <div className="flex-shrink-0 px-6 py-4 border-t border-[#e5e5e7] bg-white">
                        <div className="flex items-center justify-between">
                            <div>
                                <p className="text-[12px] text-[#6e6e73]">
                                    {hasMultiplePrices ? 'Lowest price' : 'Price'}
                                </p>
                                <p className="text-[28px] font-bold text-[#1d1d1f] leading-none">
                                    ${lowestPriceSeller.pricePerUnit.toFixed(2)}
                                </p>
                                <p className="text-[12px] text-[#86868b]">
                                    from {lowestPriceSeller.sellerName}
                                </p>
                            </div>
                            <Button
                                type="primary"
                                className="!px-6 !py-3 !text-[15px]"
                                onClick={() => setContactSeller(lowestPriceSeller)}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-5 h-5">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M8.625 12a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0H8.25m4.125 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0H12m4.125 0a.375.375 0 11-.75 0 .375.375 0 01.75 0zm0 0h-.375M21 12c0 4.556-4.03 8.25-9 8.25a9.764 9.764 0 01-2.555-.337A5.972 5.972 0 015.41 20.97a5.969 5.969 0 01-.474-.065 4.48 4.48 0 00.978-2.025c.09-.457-.133-.901-.467-1.226C3.93 16.178 3 14.189 3 12c0-4.556 4.03-8.25 9-8.25s9 3.694 9 8.25z" />
                                </svg>
                                Contact Seller
                            </Button>
                        </div>
                    </div>
                )}
            </div>

            {/* Contact Seller Modal */}
            <ContactSellerModal
                isOpen={!!contactSeller}
                onClose={() => setContactSeller(null)}
                seller={contactSeller}
                book={book}
            />
        </Modal>
    );
}
