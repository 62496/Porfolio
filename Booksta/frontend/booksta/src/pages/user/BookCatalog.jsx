import React, { useRef, useEffect, useState } from "react";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import BookCard from "../../components/cards/BookCard";
import PageHeader from "../../components/layout/PageHeader";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import Button from "../../components/common/Button";
import Toast from "../../components/common/Toast";
import { useBookCatalog } from "../../features/books/hooks/useBookCatalog";

export default function BookCatalog() {
    const ref = useRef(null);
    const [isVisible, setIsVisible] = useState(false);

    const {
        books,
        loading,
        filteredBooks,
        handleToggleFavorite,
        isFavorite,
        hasAuthorRole,
        hasLibrarianRole,
        toast,
        hideToast,
    } = useBookCatalog();

    useEffect(() => {
        setIsVisible(true);
    }, []);

    useEffect(() => {
        const observer = new IntersectionObserver(
            ([entry]) => entry.isIntersecting && setIsVisible(true),
            { threshold: 0.15 }
        );

        const currentRef = ref.current;
        if (currentRef) observer.observe(currentRef);

        return () => currentRef && observer.unobserve(currentRef);
    }, []);

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f]">
                <Header />
                <LoadingSpinner message="Loading books..." />
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
                    duration={toast.duration}
                />
            )}
            <Header />

            <div className="flex-1">
                <main ref={ref} className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="Book Catalog"
                        description={`Search and filter through ${books.length} books in our collection`}
                        action={
                            (hasAuthorRole() || hasLibrarianRole()) ? (
                                <Button
                                    label="Add Book"
                                    type="secondary"
                                    href="/author/dashboard/books/new"
                                />
                            ) : null
                        }
                    />

                    {filteredBooks.length > 0 ? (
                        <div className="grid gap-8 grid-cols-[repeat(auto-fill,minmax(250px,1fr))]">
                            {filteredBooks.map((book, i) => (
                                <BookCard
                                    key={book.isbn}
                                    book={book}
                                    delay={i}
                                    isFavorite={isFavorite(book)}
                                    onToggleFavorite={handleToggleFavorite}
                                    showReport={false}
                                />
                            ))}
                        </div>
                    ) : (
                        <EmptyState
                            icon="📚"
                            title="No books found"
                            description="Try adjusting your filters or search criteria"
                        />
                    )}
                </main>
            </div>

            <Footer />
        </div>
    );
}
