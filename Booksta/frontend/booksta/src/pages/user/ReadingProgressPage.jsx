import React from "react";
import Footer from "../../components/layout/Footer";
import Header from "../../components/layout/Header";
import Button from "../../components/common/Button";
import PageHeader from "../../components/layout/PageHeader";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import { useReadingProgress } from "../../features/reading/hooks/useReadingProgress";
import ReadingBookCard from "../../features/reading/components/ReadingBookCard";
import CollapsibleBookSection from "../../features/reading/components/CollapsibleBookSection";

export default function ReadingProgressPage() {
    const {
        currentlyReadingBooks,
        finishedBooks,
        abandonedBooks,
        loading,
        finishedExpanded,
        abandonedExpanded,
        hasNoBooks,
        toggleFinishedExpanded,
        toggleAbandonedExpanded,
        goToMyBooks,
    } = useReadingProgress();

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
                <Header />
                <div className="flex-1">
                    <LoadingSpinner message="Loading your reading progress..." />
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            <Header />

            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="Reading Progress"
                        description="Track your progress across all your readings"
                    />

                    {/* Currently Reading - Always Expanded, Pinned */}
                    {currentlyReadingBooks.length > 0 && (
                        <div className="mb-12 bg-white rounded-[20px] p-8 shadow-sm border border-[#e5e5e7]">
                            <div className="flex items-center justify-between mb-6">
                                <h2 className="text-[32px] font-semibold flex items-center gap-3">
                                    Currently Reading
                                    <span className="text-[16px] px-3 py-1 rounded-full bg-blue-100 text-blue-800">
                                        {currentlyReadingBooks.length}
                                    </span>
                                </h2>
                                <div className="flex items-center gap-2 text-[#0071e3]">
                                    <span className="text-[14px] font-medium">Active</span>
                                </div>
                            </div>
                            <div className="grid gap-6 grid-cols-[repeat(auto-fill,minmax(240px,1fr))]">
                                {currentlyReadingBooks.map((book) => (
                                    <ReadingBookCard
                                        key={book.isbn}
                                        book={book}
                                        isCurrentlyReading={true}
                                    />
                                ))}
                            </div>
                        </div>
                    )}

                    {/* Finished Books - Collapsible */}
                    <CollapsibleBookSection
                        title="Finished"
                        books={finishedBooks}
                        statusColor="bg-green-100 text-green-800"
                        isExpanded={finishedExpanded}
                        onToggle={toggleFinishedExpanded}
                    />

                    {/* Abandoned Books - Collapsible */}
                    <CollapsibleBookSection
                        title="Abandoned"
                        books={abandonedBooks}
                        statusColor="bg-red-100 text-red-800"
                        isExpanded={abandonedExpanded}
                        onToggle={toggleAbandonedExpanded}
                    />

                    {/* No books message */}
                    {hasNoBooks && (
                        <div className="text-center py-16">
                            <p className="text-[28px] font-semibold text-[#1d1d1f] mb-3">No reading progress yet</p>
                            <p className="text-[17px] text-[#6e6e73] mb-6 max-w-[600px] mx-auto">
                                Start reading a book from your library to track your progress
                            </p>
                            <Button
                                type="primary"
                                label="Go to My Books"
                                onClick={goToMyBooks}
                            />
                        </div>
                    )}
                </main>
            </div>

            <Footer />
        </div>
    );
}
