import React from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import PageHeader from '../../components/layout/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Button from '../../components/common/Button';
import GenericInput from '../../components/forms/GenericInput';
import MarketplaceBookCard from '../../features/marketplace/components/MarketplaceBookCard';
import BookDetailModal from '../../features/marketplace/components/BookDetailModal';
import { useMarketplace } from '../../features/marketplace/hooks/useMarketplace';

export default function MarketplacePage() {
    const {
        // State
        books,
        totalBooks,
        loading,
        error,

        // Filters
        search,
        sortBy,
        filterInStock,
        hasFilters,
        setSearch,
        setSortBy,
        setFilterInStock,
        clearFilters,

        // Selected book
        selectedBook,
        selectedBookSellers,
        selectedBookMarketplace,
        loadingSellers,
        selectBook,
        closeBookDetail,

        // Actions
        refresh,
    } = useMarketplace();

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f]">
                <Header />
                <LoadingSpinner message="Loading marketplace..." />
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
                        title="Marketplace"
                        description="Buy and sell books with fellow readers"
                        action={
                            <Button
                                label="Refresh"
                                type="secondary"
                                onClick={refresh}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99" />
                                </svg>
                                Refresh
                            </Button>
                        }
                    />

                    {/* Error Message */}
                    {error && (
                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                            {error}
                        </div>
                    )}

                    {/* Filters */}
                    <div className="bg-[#f5f5f7] rounded-[16px] p-5 mb-8">
                        <div className="flex flex-wrap gap-4 items-end">
                            {/* Search */}
                            <div className="flex-1 min-w-[200px]">
                                <GenericInput
                                    name="search"
                                    value={search}
                                    onChange={(e) => setSearch(e.target.value)}
                                    placeholder="Search by title, author, or ISBN..."
                                    size="compact"
                                    style={{ backgroundColor: 'white' }}
                                />
                            </div>

                            {/* Sort */}
                            <div className="min-w-[180px]">
                                <GenericInput
                                    type="select"
                                    name="sortBy"
                                    value={sortBy}
                                    onChange={(e) => setSortBy(e.target.value)}
                                    size="compact"
                                    style={{ backgroundColor: 'white' }}
                                    options={[
                                        { value: 'title', label: 'Sort by Title' },
                                        { value: 'price-low', label: 'Price: Low to High' },
                                        { value: 'price-high', label: 'Price: High to Low' },
                                        { value: 'sellers', label: 'Most Sellers' },
                                    ]}
                                />
                            </div>

                            {/* In Stock Toggle */}
                            <button
                                onClick={() => setFilterInStock(!filterInStock)}
                                className={`px-5 py-3 rounded-[12px] text-[14px] font-medium transition-all ${filterInStock
                                        ? 'bg-green-500 text-white'
                                        : 'bg-white border border-[#e5e5e7] text-[#6e6e73] hover:border-[#1d1d1f]'
                                    }`}
                            >
                                <span className="flex items-center gap-2">
                                    {filterInStock && (
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
                                            <path fillRule="evenodd" d="M16.704 4.153a.75.75 0 01.143 1.052l-8 10.5a.75.75 0 01-1.127.075l-4.5-4.5a.75.75 0 011.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 011.05-.143z" clipRule="evenodd" />
                                        </svg>
                                    )}
                                    In Stock Only
                                </span>
                            </button>

                            {/* Clear Filters */}
                            {hasFilters && (
                                <Button
                                    type="text"
                                    onClick={clearFilters}
                                >
                                    Clear Filters
                                </Button>
                            )}
                        </div>
                    </div>

                    {/* Results Count */}
                    <div className="mb-6 text-[14px] text-[#6e6e73]">
                        Showing {books.length} of {totalBooks} books
                        {hasFilters && ' (filtered)'}
                    </div>

                    {/* Books Grid */}
                    {books.length > 0 ? (
                        <div className="grid gap-6 grid-cols-[repeat(auto-fill,minmax(220px,1fr))]">
                            {books.map((book, index) => (
                                <MarketplaceBookCard
                                    key={book.isbn}
                                    book={book}
                                    onClick={selectBook}
                                    delay={index}
                                />
                            ))}
                        </div>
                    ) : (
                        <EmptyState
                            title={hasFilters ? 'No books match your filters' : 'No books available'}
                            description={
                                hasFilters
                                    ? 'Try adjusting your search or filters'
                                    : 'Check back later for new listings'
                            }
                            action={
                                hasFilters && (
                                    <Button
                                        label="Clear Filters"
                                        type="primary"
                                        onClick={clearFilters}
                                    />
                                )
                            }
                        />
                    )}
                </main>
            </div>

            <Footer />

            {/* Book Detail Modal */}
            <BookDetailModal
                isOpen={!!selectedBook}
                onClose={closeBookDetail}
                book={selectedBook}
                marketplace={selectedBookMarketplace}
                sellers={selectedBookSellers}
                loadingSellers={loadingSellers}
            />
        </div>
    );
}
