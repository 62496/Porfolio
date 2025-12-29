import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import FollowButton from '../../features/social/components/FollowButton';
import bookService from '../../api/services/bookService';
import userService from '../../api/services/userService';

export default function SeriesCard({ series, initiallyFollowing = undefined }) {
  const [books, setBooks] = useState([]);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const [isFollowing, setIsFollowing] = useState(Boolean(initiallyFollowing));

  useEffect(() => {
    let mounted = true;
    (async () => {
      if (!series?.id) return;
      try {
        setLoadingBooks(true);

        // If backend already gives the books inside the series object
        if (Array.isArray(series.books) && series.books.length > 0) {
          if (mounted) setBooks(series.books);
        } else {
          // Otherwise get books for this series
          const data = await bookService.getBySeries(series.id);
          if (mounted) setBooks(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        console.error("Failed fetching series books", err);
        if (mounted) setBooks([]);
      } finally {
        if (mounted) setLoadingBooks(false);
      }
    })();
    return () => { mounted = false; };
  }, [series.id]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      // if prop passed explicitly, use it
      if (typeof initiallyFollowing === 'boolean') {
        if (mounted) setIsFollowing(Boolean(initiallyFollowing));
        return;
      }

      // else query backend for current user follow status
      if (!series?.id) return;
      try {
        const following = await userService.isFollowingSeries(series.id);
        if (mounted) setIsFollowing(Boolean(following));
      } catch (err) {
        console.error('Failed to check follow status for series', series.id, err);
        if (mounted) setIsFollowing(false);
      }
    })();

    return () => { mounted = false; };
  }, [series?.id, initiallyFollowing]);

  return (
    <div className="bg-white rounded-2xl border border-[#e5e5e7] overflow-hidden hover:shadow-lg transition-all duration-300">
      {/* Header */}
      <div className="p-5 border-b border-[#e5e5e7]">
        <div className="flex items-start justify-between gap-4">
          <div className="flex-1 min-w-0">
            <Link
              to={`/series/${series.id}`}
              className="text-lg font-semibold text-[#1d1d1f] line-clamp-1 hover:text-[#0066cc] transition-colors"
            >
              {series.title}
            </Link>
            {series.description && (
              <p className="text-sm text-[#6e6e73] mt-1 line-clamp-2">
                {series.description}
              </p>
            )}
            <p className="text-xs text-[#86868b] mt-2">
              {books.length} {books.length === 1 ? 'book' : 'books'}
            </p>
          </div>

          <FollowButton
            type="series"
            id={series.id}
            initiallyFollowing={isFollowing}
            onChange={(val) => setIsFollowing(val)}
          />
        </div>
      </div>

      {/* Books Preview */}
      <div className="p-5">
        {loadingBooks ? (
          <div className="flex items-center justify-center py-8">
            <div className="w-6 h-6 border-2 border-[#0066cc] border-t-transparent rounded-full animate-spin" />
          </div>
        ) : books.length === 0 ? (
          <div className="text-center py-8 text-[#6e6e73]">
            <svg
              className="w-12 h-12 mx-auto mb-2 text-[#c5c5c7]"
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
            <p className="text-sm">No books in this series yet</p>
          </div>
        ) : (
          <div className="flex gap-3 overflow-x-auto pb-2 -mx-1 px-1">
            {books.slice(0, 6).map(b => (
              <Link
                key={b.isbn}
                to={`/book/${b.isbn}`}
                className="flex-shrink-0 group"
              >
                <div className="w-[80px] h-[120px] rounded-lg overflow-hidden bg-[#f5f5f7] shadow-sm group-hover:shadow-md transition-shadow">
                  {(b.cover || b.image?.url) ? (
                    <img
                      src={b.cover || b.image?.url}
                      alt={b.title}
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center">
                      <svg
                        className="w-8 h-8 text-[#c5c5c7]"
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
                <p className="text-xs text-[#1d1d1f] mt-1.5 line-clamp-2 w-[80px] group-hover:text-[#0066cc] transition-colors">
                  {b.title}
                </p>
              </Link>
            ))}
            {books.length > 6 && (
              <div className="flex-shrink-0 w-[80px] h-[120px] rounded-lg bg-[#f5f5f7] flex items-center justify-center">
                <span className="text-sm font-medium text-[#6e6e73]">
                  +{books.length - 6} more
                </span>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
