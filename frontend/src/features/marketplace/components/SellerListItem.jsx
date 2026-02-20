import React from 'react';
import Button from '../../../components/common/Button';

export default function SellerListItem({
    seller,
    isLowestPrice = false,
    showPriceLabel = true,
    onContact,
}) {
    return (
        <div className={`p-3 rounded-[10px] transition-all ${
            isLowestPrice
                ? 'bg-green-50 border border-green-200'
                : 'bg-[#f5f5f7] border border-transparent hover:border-[#e5e5e7]'
        }`}>
            <div className="flex items-center gap-3">
                {/* Seller Avatar */}
                <div className="flex-shrink-0">
                    {seller.sellerPicture ? (
                        <img
                            src={seller.sellerPicture}
                            alt={seller.sellerName}
                            className="w-10 h-10 rounded-full object-cover border-2 border-white shadow-sm"
                        />
                    ) : (
                        <div className="w-10 h-10 rounded-full bg-[#e5e5e7] flex items-center justify-center">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-5 h-5 text-[#6e6e73]">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                            </svg>
                        </div>
                    )}
                </div>

                {/* Seller Info - Clean layout */}
                <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2">
                        <p className="font-medium text-[14px] text-[#1d1d1f] truncate">
                            {seller.sellerName}
                        </p>
                        {isLowestPrice && (
                            <span className="flex-shrink-0 px-1.5 py-0.5 bg-green-500 text-white text-[9px] font-bold rounded uppercase">
                                Best
                            </span>
                        )}
                    </div>
                    <div className="flex items-center gap-2 text-[12px] text-[#86868b]">
                        <span>{seller.quantity} {seller.quantity === 1 ? 'copy' : 'copies'}</span>
                        <span className="w-1 h-1 rounded-full bg-[#d2d2d7]"></span>
                        <span className="text-[#6e6e73]">Good condition</span>
                    </div>
                </div>

                {/* Price */}
                <div className="text-right flex-shrink-0">
                    <p className={`text-[18px] font-bold ${isLowestPrice ? 'text-green-600' : 'text-[#1d1d1f]'}`}>
                        ${seller.pricePerUnit.toFixed(2)}
                    </p>
                    {showPriceLabel && (
                        <p className="text-[10px] text-[#86868b]">each</p>
                    )}
                </div>

                {/* Action */}
                <Button
                    type={isLowestPrice ? "primary" : "secondary"}
                    className="!px-3 !py-2 !text-[13px] flex-shrink-0"
                    onClick={() => onContact?.(seller)}
                >
                    Contact
                </Button>
            </div>
        </div>
    );
}
