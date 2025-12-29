import React from "react";
import { useParams } from "react-router-dom";
import Button from "../../components/common/Button";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import GenericInput from "../../components/forms/GenericInput";
import BookList from "../../features/books/components/BookList";
import { useBookCollectionDetail } from "../../features/collections/hooks/useBookCollectionDetail";

export default function BookCollectionDetailPage() {
    const { collectionId } = useParams();

    const {
        currentCollection,
        books,
        loading,
        error,
        userEmail,
        setUserEmail,
        isOwner,
        handleDelete,
        handleShare,
        handleUnshare,
    } = useBookCollectionDetail(collectionId);

    if (loading) {
        return (
            <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
                <Header />
                <LoadingSpinner message="Loading collection..." />
                <Footer />
            </div>
        );
    }

    if (!currentCollection) {
        return (
            <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
                <Header />
                <main className="flex-1 pt-[100px] px-4">
                    <div className="text-center py-12">
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
                                d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                            />
                        </svg>
                        <p className="text-[#6e6e73]">Collection not found</p>
                    </div>
                </main>
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            <Header />

            <main className="flex-1 max-w-[1200px] mx-auto py-20 px-5 w-full">
                <div className="space-y-6">
                    {/* Collection Header */}
                    <div className="rounded-2xl shadow-sm p-6 bg-white border border-[#e5e5e7]">
                        <div className="flex items-start justify-between">
                            <div>
                                <h1 className="text-2xl font-semibold mb-2 text-[#1d1d1f]">
                                    {currentCollection.name}
                                </h1>
                                {currentCollection.description && (
                                    <p className="text-[#6e6e73] mb-4">
                                        {currentCollection.description}
                                    </p>
                                )}
                                <div className="flex flex-wrap gap-4 text-sm text-[#6e6e73]">
                                    <span className="flex items-center gap-1">
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                                        </svg>
                                        Shared with: {currentCollection.sharedWith?.length || 0} users
                                    </span>
                                    <span className="flex items-center gap-1">
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
                                        </svg>
                                        {currentCollection.books?.length || 0} books
                                    </span>
                                </div>
                            </div>
                            {isOwner && (
                                <Button
                                    type="small-danger"
                                    label="Delete"
                                    onClick={handleDelete}
                                />
                            )}
                        </div>
                    </div>

                    {/* Books */}
                    <BookList books={books} />

                    {/* Share Section (Owner Only) */}
                    {isOwner && (
                        <div className="rounded-2xl shadow-sm p-6 bg-white border border-[#e5e5e7]">
                            <h2 className="text-xl font-semibold mb-4 text-[#1d1d1f]">Shared With</h2>

                            {currentCollection.sharedWith?.length === 0 ? (
                                <div className="text-center py-8 text-[#6e6e73] bg-[#f5f5f7] rounded-xl mb-4">
                                    <svg
                                        className="w-12 h-12 mx-auto mb-2 text-[#86868b]"
                                        fill="none"
                                        stroke="currentColor"
                                        viewBox="0 0 24 24"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            strokeWidth="1.5"
                                            d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                                        />
                                    </svg>
                                    <p className="text-sm">Not shared with anyone yet</p>
                                </div>
                            ) : (
                                <div className="space-y-2 mb-4">
                                    {currentCollection.sharedWith.map((user) => (
                                        <div
                                            key={user.id}
                                            className="flex justify-between items-center p-3 border border-[#e5e5e7] rounded-xl bg-[#f5f5f7]"
                                        >
                                            <div className="flex items-center gap-3">
                                                <div className="w-10 h-10 rounded-full bg-[#6e6e73] flex items-center justify-center text-white font-medium">
                                                    {user.firstName?.[0] || user.email?.[0]?.toUpperCase()}
                                                </div>
                                                <div>
                                                    <p className="font-medium text-[#1d1d1f]">
                                                        {user.firstName} {user.lastName}
                                                    </p>
                                                    <p className="text-xs text-[#6e6e73]">{user.email}</p>
                                                </div>
                                            </div>
                                            <button
                                                onClick={() => handleUnshare(user.id)}
                                                className="px-3 py-1.5 text-xs font-medium text-red-500 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                                            >
                                                Remove
                                            </button>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {/* Add User Form */}
                            <div className="flex items-end gap-3">
                                <div className="flex-1">
                                    <GenericInput
                                        type="email"
                                        label="Share with user"
                                        placeholder="Enter user email"
                                        value={userEmail}
                                        onChange={(e) => setUserEmail(e.target.value)}
                                    />
                                </div>
                                <Button
                                    type="modal-primary"
                                    label="Share"
                                    onClick={handleShare}
                                    className="mb-4"
                                />
                            </div>

                            {error && (
                                <p className="text-sm text-red-600 mt-2">{error}</p>
                            )}
                        </div>
                    )}
                </div>
            </main>

            <Footer />
        </div>
    );
}
