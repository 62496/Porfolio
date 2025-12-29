import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import Button from "../../components/common/Button";
import FollowButton from "../../features/social/components/FollowButton";
import DeleteSeriesModal from "../../features/series/components/modals/DeleteSeriesModal";
import { useSeriesDetail } from "../../features/series/hooks/useSeriesDetail";

export default function SeriesDetailPage() {
    const { id } = useParams();
    const navigate = useNavigate();

    const {
        series,
        books,
        loading,
        error,
        isFollowing,
        isDeleteModalOpen,
        deleteLoading,
        canDeleteSeries,
        canEditSeries,
        setIsFollowing,
        handleBack,
        handleBookClick,
        handleAuthorClick,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteSeries,
    } = useSeriesDetail(id);

    if (loading) {
        return (
            <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
                <Header />
                <div className="flex-1 flex items-center justify-center">
                    <div className="text-center">
                        <div className="w-16 h-16 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                        <p className="text-[17px] text-[#6e6e73]">Loading series details...</p>
                    </div>
                </div>
                <Footer />
            </div>
        );
    }

    if (error || !series) {
        return (
            <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
                <Header />
                <div className="flex-1 flex items-center justify-center">
                    <div className="text-center max-w-md mx-auto px-6">
                        <div className="mb-6">
                            <svg
                                className="w-20 h-20 mx-auto text-[#ff3b30]"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth="2"
                                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                                />
                            </svg>
                        </div>
                        <h2 className="text-[28px] font-semibold mb-4">Series Not Found</h2>
                        <p className="text-[17px] text-[#6e6e73] mb-8">
                            {error || "The series you're looking for doesn't exist or has been removed."}
                        </p>
                        <Button
                            label="Back to Series"
                            type="primary"
                            onClick={() => navigate('/series')}
                        />
                    </div>
                </div>
                <Footer />
            </div>
        );
    }

    const authorName = series.author
        ? `${series.author.firstName} ${series.author.lastName}`
        : "Unknown Author";

    return (
        <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
            <Header />
            <div className="flex-1 py-12">
                <div className="max-w-6xl mx-auto px-6">
                    {/* Back Button */}
                    <div className="mb-8">
                        <button
                            onClick={handleBack}
                            className="w-12 h-12 rounded-full bg-white border-2 border-[#e5e5e7] text-[#6e6e73] hover:border-[#1d1d1f] hover:text-[#1d1d1f] hover:scale-110 transition-all duration-300 ease-in-out flex items-center justify-center shadow-sm"
                            aria-label="Go back"
                        >
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="2"
                                className="w-6 h-6"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    d="M15 19l-7-7 7-7"
                                />
                            </svg>
                        </button>
                    </div>

                    {/* Series Detail Card */}
                    <div className="bg-white rounded-[24px] border border-[#e5e5e7] overflow-hidden shadow-sm">
                        <div className="p-8 md:p-12">
                            {/* Admin Controls */}
                            {(canEditSeries() || canDeleteSeries()) && (
                                <div className="flex justify-end gap-3 mb-4">
                                    {canEditSeries() && (
                                        <Button
                                            type="secondary"
                                            onClick={() => navigate(`/author/dashboard/series`)}
                                            className="!px-5 !py-2.5"
                                        >
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                strokeWidth="2"
                                                className="w-4 h-4"
                                            >
                                                <path
                                                    strokeLinecap="round"
                                                    strokeLinejoin="round"
                                                    d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10"
                                                />
                                            </svg>
                                            Manage Series
                                        </Button>
                                    )}
                                    {canDeleteSeries() && (
                                        <Button
                                            type="danger"
                                            onClick={openDeleteModal}
                                            className="!px-5 !py-2.5"
                                        >
                                            <svg
                                                xmlns="http://www.w3.org/2000/svg"
                                                viewBox="0 0 24 24"
                                                fill="none"
                                                stroke="currentColor"
                                                strokeWidth="2"
                                                className="w-4 h-4"
                                            >
                                                <path
                                                    strokeLinecap="round"
                                                    strokeLinejoin="round"
                                                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                                                />
                                            </svg>
                                            Delete Series
                                        </Button>
                                    )}
                                </div>
                            )}

                            {/* Series Header */}
                            <div className="mb-10">
                                <h1 className="text-[40px] md:text-[48px] font-bold leading-tight mb-4">
                                    {series.title}
                                </h1>

                                {/* Author */}
                                <div className="mb-6">
                                    <button
                                        onClick={() => series.author && handleAuthorClick(series.author.id)}
                                        className="text-[20px] text-[#0066cc] hover:underline"
                                    >
                                        by {authorName}
                                    </button>
                                </div>

                                {/* Stats */}
                                <div className="flex flex-wrap gap-4 mb-6">
                                    <div className="bg-[#f5f5f7] rounded-[12px] px-4 py-3">
                                        <p className="text-[13px] text-[#6e6e73] uppercase font-semibold">
                                            Books
                                        </p>
                                        <p className="text-[20px] font-semibold">{series.bookCount || books.length || 0}</p>
                                    </div>
                                    <div className="bg-[#f5f5f7] rounded-[12px] px-4 py-3">
                                        <p className="text-[13px] text-[#6e6e73] uppercase font-semibold">
                                            Followers
                                        </p>
                                        <p className="text-[20px] font-semibold">{series.followerCount || 0}</p>
                                    </div>
                                </div>

                                {/* Follow Button */}
                                <FollowButton
                                    type="series"
                                    id={series.id}
                                    initiallyFollowing={isFollowing}
                                    onChange={(val) => setIsFollowing(val)}
                                />

                                {/* Description */}
                                {series.description && (
                                    <div className="mt-6">
                                        <p className="text-[17px] text-[#1d1d1f] leading-relaxed">
                                            {series.description}
                                        </p>
                                    </div>
                                )}
                            </div>

                            {/* Books Section */}
                            {books && books.length > 0 ? (
                                <div>
                                    <h2 className="text-[24px] font-semibold mb-6">Books in this Series</h2>
                                    <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-6">
                                        {books.map((book) => (
                                            <div
                                                key={book.isbn}
                                                onClick={() => handleBookClick(book.isbn)}
                                                className="cursor-pointer group"
                                            >
                                                <div className="aspect-[3/4] rounded-[12px] overflow-hidden shadow-md group-hover:shadow-xl transition-shadow duration-300 mb-3">
                                                    {book.imageUrl ? (
                                                        <img
                                                            src={book.imageUrl}
                                                            alt={book.title}
                                                            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                                                        />
                                                    ) : (
                                                        <div className="w-full h-full bg-[#f5f5f7] flex items-center justify-center">
                                                            <svg
                                                                className="w-12 h-12 text-[#86868b]"
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
                                                <h3 className="text-[15px] font-semibold text-[#1d1d1f] line-clamp-2 group-hover:text-[#0066cc] transition-colors">
                                                    {book.title}
                                                </h3>
                                                {book.publishingYear && (
                                                    <p className="text-[13px] text-[#6e6e73]">{book.publishingYear}</p>
                                                )}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ) : (
                                <div className="text-center py-12 bg-[#f5f5f7] rounded-[18px]">
                                    <svg
                                        className="w-16 h-16 mx-auto mb-4 text-[#86868b]"
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
                                    <p className="text-[17px] text-[#6e6e73]">
                                        No books in this series yet.
                                    </p>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>

            {/* Delete Modal */}
            <DeleteSeriesModal
                isOpen={isDeleteModalOpen}
                series={series}
                loading={deleteLoading}
                onConfirm={handleDeleteSeries}
                onClose={closeDeleteModal}
            />

            <Footer />
        </div>
    );
}
