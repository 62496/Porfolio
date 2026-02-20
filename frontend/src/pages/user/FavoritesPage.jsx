import React from "react";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import Button from "../../components/common/Button";
import BookCard from "../../components/cards/BookCard";
import PageHeader from "../../components/layout/PageHeader";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import { useFavorites } from "../../features/books/hooks/useFavorites";

export default function FavoritesPage() {
    const {
        favorites,
        loading,
        handleToggleFavorite,
    } = useFavorites();

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
                <Header />
                <div className="flex-1">
                    <LoadingSpinner message="Loading your favorites..." />
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
                        title="My Favorite Books"
                        description={
                            favorites.length > 0
                                ? `You have ${favorites.length} book${favorites.length > 1 ? 's' : ''} saved to read`
                                : "All the books you love are saved here"
                        }
                        action={
                            <div className="flex gap-3">
                                <Button label="Back to Catalog" type="secondary" href="/books" />
                            </div>
                        }
                    />

                    {favorites.length === 0 ? (
                        <EmptyState
                            title="No favorites yet"
                            description="Start building your collection by browsing our catalog"
                            action={
                                <Button
                                    label="Browse Catalog"
                                    type="primary"
                                    href="/books"
                                />
                            }
                        />
                    ) : (
                        <div className="grid gap-8 grid-cols-[repeat(auto-fill,minmax(250px,1fr))]">
                            {favorites.map((book, i) => (
                                <BookCard
                                    key={book.isbn}
                                    book={book}
                                    delay={i}
                                    isFavorite={true}
                                    onToggleFavorite={handleToggleFavorite}
                                />
                            ))}
                        </div>
                    )}
                </main>
            </div>

            <Footer />
        </div>
    );
}
