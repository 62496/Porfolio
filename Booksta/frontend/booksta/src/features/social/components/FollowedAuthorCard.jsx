import React, { useEffect, useState } from "react";
import bookService from "../../../api/services/bookService";
import BookThumb from "./BookThumb";

export default function FollowedAuthorCard({ author, onUnfollow }) {
    const [books, setBooks] = useState([]);
    const [loadingBooks, setLoadingBooks] = useState(true);

    useEffect(() => {
        let mounted = true;
        (async () => {
            setLoadingBooks(true);
            try {
                let data = [];
                try {
                    data = await bookService.getByAuthor(author.id);
                } catch (e) {
                    console.warn('getByAuthor failed, will fallback to all books filter', e);
                }

                if ((!data || data.length === 0)) {
                    try {
                        const all = await bookService.getAllBooks();
                        data = (all || []).filter(b =>
                            Array.isArray(b.authors) && b.authors.some(a => String(a.id) === String(author.id))
                        );
                    } catch (e) {
                        console.warn('Fallback fetching all books failed', e);
                        data = [];
                    }
                }

                if (mounted) setBooks(data || []);
            } catch (err) {
                console.error('Failed fetching author books', err);
                if (mounted) setBooks([]);
            } finally {
                if (mounted) setLoadingBooks(false);
            }
        })();

        return () => { mounted = false; };
    }, [author.id]);

    return (
        <div className="bg-white border border-[#e5e5e7] rounded-[18px] p-6 hover:shadow-lg transition-shadow duration-300">
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <img
                        src={author.image?.url || author.picture || '/default-avatar.png'}
                        alt={`${author.firstName} ${author.lastName}`}
                        className="w-16 h-16 object-cover rounded-full border-2 border-[#e5e5e7]"
                    />
                    <div>
                        <div className="text-[17px] font-semibold text-[#1d1d1f]">{author.firstName} {author.lastName}</div>
                        <div className="text-[13px] text-[#6e6e73]">{books.length} {books.length === 1 ? 'book' : 'books'}</div>
                    </div>
                </div>

                <button
                    onClick={() => onUnfollow(author.id, 'author')}
                    className="px-4 py-2 text-[13px] font-medium bg-red-50 text-red-700 rounded-full hover:bg-red-100 transition-colors duration-200"
                >
                    Unfollow
                </button>
            </div>

            <div>
                <div className="text-[15px] font-semibold text-[#1d1d1f] mb-3">Books</div>

                {loadingBooks ? (
                    <div className="text-[15px] text-[#6e6e73]">Loading books…</div>
                ) : books.length === 0 ? (
                    <div className="text-[15px] text-[#6e6e73]">No books yet</div>
                ) : (
                    <div className="flex gap-4 flex-wrap">
                        {books.map(b => <BookThumb key={b.isbn} book={b} />)}
                    </div>
                )}
            </div>
        </div>
    );
}
