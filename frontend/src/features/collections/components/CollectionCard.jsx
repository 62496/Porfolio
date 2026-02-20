import React from 'react';
import { Link } from 'react-router-dom';
import Button from '../../../components/common/Button';
import bookCollectionsService from '../../../api/services/bookCollectionsService';

const CollectionCard = ({
    collection,
    isOwner,
    onEdit,
    onDelete,
    onShare,
    onAddBooks,
    delay = 0,
}) => {
    const bookCount = collection.books?.length || 0;
    const sharedCount = collection.sharedWith?.length || 0;
    const isPublic = collection.visibility === 'PUBLIC';
    const imageUrl = collection.image ? bookCollectionsService.getImageUrl(collection.id) : null;

    return (
        <div
            className="bg-white rounded-2xl border border-[#e5e5e7] overflow-hidden hover:shadow-lg transition-all duration-300 flex flex-col"
            style={{
                animation: `fadeInUp 0.5s ease-out ${delay * 0.1}s both`,
            }}
        >
            {/* Image / Placeholder */}
            <div className="relative h-40 bg-gradient-to-br from-[#f5f5f7] to-[#e5e5e7]">
                {imageUrl ? (
                    <img
                        src={imageUrl}
                        alt={collection.name}
                        className="w-full h-full object-cover"
                        onError={(e) => {
                            e.target.style.display = 'none';
                        }}
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center">
                        <svg
                            className="w-16 h-16 text-[#86868b]"
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

                {/* Visibility Badge */}
                <div
                    className={`absolute top-3 right-3 px-2 py-1 rounded-full text-xs font-medium ${
                        isPublic
                            ? 'bg-green-100 text-green-700'
                            : 'bg-gray-100 text-gray-700'
                    }`}
                >
                    {isPublic ? 'Public' : 'Private'}
                </div>

                {/* Book count badge */}
                <div className="absolute bottom-3 left-3 bg-black/60 text-white px-2 py-1 rounded-lg text-xs font-medium">
                    {bookCount} {bookCount === 1 ? 'book' : 'books'}
                </div>
            </div>

            {/* Content */}
            <div className="p-4 flex-1 flex flex-col">
                <Link
                    to={`/collections/${collection.id}`}
                    className="text-lg font-semibold text-[#1d1d1f] hover:text-[#0066cc] transition-colors line-clamp-1"
                >
                    {collection.name}
                </Link>

                {collection.description && (
                    <p className="text-sm text-[#6e6e73] mt-1 line-clamp-2">
                        {collection.description}
                    </p>
                )}

                {/* Owner info */}
                {collection.owner && !isOwner && (
                    <p className="text-xs text-[#86868b] mt-2">
                        By {collection.owner.firstName} {collection.owner.lastName}
                    </p>
                )}

                {/* Shared with count (only for private) */}
                {!isPublic && sharedCount > 0 && (
                    <p className="text-xs text-[#86868b] mt-1">
                        Shared with {sharedCount} {sharedCount === 1 ? 'person' : 'people'}
                    </p>
                )}

                {/* Actions (only for owner) */}
                {isOwner && (
                    <div className="mt-auto pt-4 flex flex-wrap gap-2">
                        <Button
                            type="small-secondary"
                            label="Add Books"
                            onClick={() => onAddBooks?.(collection)}
                            className="flex-1 min-w-[80px]"
                        />
                        {!isPublic && (
                            <Button
                                type="small-secondary"
                                label="Share"
                                onClick={() => onShare?.(collection)}
                                className="flex-1 min-w-[80px]"
                            />
                        )}
                        <button
                            onClick={() => onEdit?.(collection)}
                            className="px-3 py-2 text-xs font-medium text-[#6e6e73] hover:text-[#1d1d1f] transition-colors"
                        >
                            Edit
                        </button>
                        <button
                            onClick={() => onDelete?.(collection)}
                            className="px-3 py-2 text-xs font-medium text-red-500 hover:text-red-700 transition-colors"
                        >
                            Delete
                        </button>
                    </div>
                )}
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

export default CollectionCard;
