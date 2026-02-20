import React, { useState } from "react";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import BookCardWithReadingStatus from "../../components/cards/BookCardWithReadingStatus";
import PageHeader from "../../components/layout/PageHeader";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import Toast from "../../components/common/Toast";
import Button from "../../components/common/Button";
import AddToCollectionModal from "../../features/collections/components/modals/AddToCollectionModal";
import { useMyBooks } from "../../features/books/hooks/useMyBooks";

export default function MyPersonalBooks() {
    const {
        ownedBooks,
        loading,
        toast,
        handleToggleFavorite,
        handleStartReading,
        handleAddToCollection,
        isFavorite,
        hideToast,
    } = useMyBooks();

    const [collectionModalBook, setCollectionModalBook] = useState(null);

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
                <Header />
                <div className="flex-1">
                    <LoadingSpinner message="Loading your books..." />
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={hideToast}
                />
            )}

            <Header />

            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="My Personal Books"
                        description={
                            ownedBooks.length > 0
                                ? `Manage your collection of ${ownedBooks.length} ${ownedBooks.length === 1 ? 'book' : 'books'}`
                                : 'Start building your personal library'
                        }
                    />

                    {ownedBooks.length > 0 ? (
                        <div className="grid gap-8 grid-cols-[repeat(auto-fill,minmax(250px,1fr))]">
                            {ownedBooks.map((book, i) => (
                                <BookCardWithReadingStatus
                                    key={book.isbn}
                                    book={book}
                                    delay={i}
                                    isFavorite={isFavorite(book)}
                                    onToggleFavorite={handleToggleFavorite}
                                    onStartReading={handleStartReading}
                                    onOpenCollectionModal={setCollectionModalBook}
                                    showAddToCollection={false}
                                    showReport={false}
                                />
                            ))}
                        </div>
                    ) : (
                        <EmptyState
                            title="No books in your library yet"
                            description="Start building your personal collection by browsing our catalog"
                            action={
                                <Button
                                    label="Browse Catalog"
                                    type="primary"
                                    href="/books"
                                />
                            }
                        />
                    )}
                </main>
            </div>

            <Footer />

            {/* Add to Collection Modal - rendered at page level */}
            <AddToCollectionModal
                isOpen={!!collectionModalBook}
                onClose={() => setCollectionModalBook(null)}
                book={collectionModalBook}
                onSuccess={(collection) => {
                    handleAddToCollection(collectionModalBook, collection);
                    setCollectionModalBook(null);
                }}
            />
        </div>
    );
}
