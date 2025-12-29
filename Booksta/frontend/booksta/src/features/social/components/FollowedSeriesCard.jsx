import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import bookService from "../../../api/services/bookService";
import BookThumb from "./BookThumb";

export default function FollowedSeriesCard({ series, onUnfollow }) {
    const [books, setBooks] = useState([]);
    const [loadingBooks, setLoadingBooks] = useState(true);

    useEffect(() => {
        let mounted = true;
        (async () => {
            setLoadingBooks(true);
            try {
                if (series?.books && Array.isArray(series.books) && series.books.length) {
                    setBooks(series.books);
                } else {
                    const data = await bookService.getBySeries(series.id);
                    setBooks(data || []);
                }
            } catch (err) {
                console.error('Failed fetching series books', err);
                if (mounted) setBooks([]);
            } finally {
                if (mounted) setLoadingBooks(false);
            }
        })();

        return () => { mounted = false; };
    }, [series.id, series.books]);

    return (
        <div className="bg-white border border-[#e5e5e7] rounded-[18px] p-6 hover:shadow-lg transition-shadow duration-300">
            <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                    <Link
                        to={`/series/${series.id}`}
                        className="text-[20px] font-semibold text-[#1d1d1f] hover:text-[#0066cc] transition-colors"
                    >
                        {series.title}
                    </Link>
                </div>

                <button
                    onClick={() => onUnfollow(series.id, 'series')}
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
