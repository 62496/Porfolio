import React from 'react';
import Button from '../../../components/common/Button';

const SeriesManagementCard = ({
    series,
    onEdit,
    onDelete,
    onAddBooks,
    delay = 0,
}) => {
    const bookCount = series.bookCount || 0;

    return (
        <div
            className="bg-white rounded-2xl border border-[#e5e5e7] overflow-hidden hover:shadow-lg transition-all duration-300 flex flex-col"
            style={{
                animation: `fadeInUp 0.5s ease-out ${delay * 0.1}s both`,
            }}
        >
            {/* Content */}
            <div className="p-4 flex-1 flex flex-col">
                <div className="flex items-start justify-between gap-2 mb-1">
                    <h3 className="text-lg font-semibold text-[#1d1d1f] line-clamp-1">
                        {series.title}
                    </h3>
                    <span className="bg-[#f5f5f7] text-[#6e6e73] px-2 py-1 rounded-lg text-xs font-medium whitespace-nowrap">
                        {bookCount} {bookCount === 1 ? 'book' : 'books'}
                    </span>
                </div>

                {series.description && (
                    <p className="text-sm text-[#6e6e73] line-clamp-2 mb-4">
                        {series.description}
                    </p>
                )}

                {/* Actions */}
                <div className="mt-auto pt-4 flex flex-wrap gap-2">
                    <Button
                        type="small-secondary"
                        label="Edit Books"
                        onClick={() => onAddBooks?.(series)}
                        className="flex-1 min-w-[80px]"
                    />
                    <button
                        onClick={() => onEdit?.(series)}
                        className="px-3 py-2 text-xs font-medium text-[#6e6e73] hover:text-[#1d1d1f] transition-colors"
                    >
                        Edit
                    </button>
                    <button
                        onClick={() => onDelete?.(series)}
                        className="px-3 py-2 text-xs font-medium text-red-500 hover:text-red-700 transition-colors"
                    >
                        Delete
                    </button>
                </div>
            </div>

            <style>{`
                @keyframes fadeInUp {
                    from {
                        opacity: 0;
                        transform: translateY(20px);
                    }
                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }
            `}</style>
        </div>
    );
};

export default SeriesManagementCard;
