import React, { useState, useRef, useEffect } from "react";
import ReportBookModal from "../../features/books/components/ReportBookModal";
import { useNavigate } from "react-router-dom";


export default function BookCard({
  book,
  delay = 0,
  isFavorite = false,
  onToggleFavorite,
  showReport = true,
  disableNavigation = false,
}) {
  const navigate = useNavigate();
  const [isVisible, setIsVisible] = useState(false);
  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const ref = useRef(null);

  const handleCardClick = (e) => {
    // Don't navigate if clicking on buttons or modals
    if (disableNavigation || e.target.closest('button') || e.target.closest('[role="dialog"]')) {
      return;
    }
    navigate(`/book/${book.isbn}`);
  };

  const handleReportClick = (e) => {
    e.stopPropagation();
    setIsReportModalOpen(true);
  };

  useEffect(() => {
    const ob = new IntersectionObserver(
      ([entry]) => entry.isIntersecting && setIsVisible(true),
      { threshold: 0.15 }
    );
    if (ref.current) ob.observe(ref.current);
    return () => ref.current && ob.unobserve(ref.current);
  }, []);

  return (
    <div
      ref={ref}
      onClick={handleCardClick}
      className={`
        bg-white border border-[#e5e5e7] rounded-[18px]
        p-6 text-center relative
        flex flex-col h-full
        fade-in ${isVisible ? "visible" : ""}
        cursor-pointer hover:shadow-lg transition-shadow duration-300
      `}
      style={{ transitionDelay: `${delay * 0.1}s` }}
    >
      {/* Favorite Button */}
      {onToggleFavorite && (
        <button
          type="button"
          onClick={() => onToggleFavorite(book)}
          className={`
            absolute top-4 right-4 w-10 h-10 rounded-full
            flex items-center justify-center
            transition-all duration-300 ease-in-out
            ${
              isFavorite
                ? "bg-[#ff3b30] text-white shadow-lg hover:shadow-xl hover:scale-110"
                : "bg-white border-2 border-[#e5e5e7] text-[#6e6e73] hover:border-[#ff3b30] hover:text-[#ff3b30] hover:scale-110"
            }
            z-10
          `}
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill={isFavorite ? "currentColor" : "none"}
            stroke="currentColor"
            strokeWidth="2"
            className="w-5 h-5"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z"
            />
          </svg>
        </button>
      )}

      {/* Report Button */}
      {showReport && (
        <button
          type="button"
          onClick={handleReportClick}
          className="
            absolute top-4 left-4 w-10 h-10 rounded-full
            flex items-center justify-center
            bg-white border-2 border-[#e5e5e7] text-[#6e6e73]
            hover:border-[#ff9500] hover:text-[#ff9500] hover:scale-110
            transition-all duration-300 ease-in-out
            z-10
          "
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            className="w-5 h-5"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M3 3v1.5M3 21v-6m0 0l2.77-.693a9 9 0 016.208.682l.108.054a9 9 0 006.086.71l3.114-.732a48.524 48.524 0 01-.005-10.499l-3.11.732a9 9 0 01-6.085-.711l-.108-.054a9 9 0 00-6.208-.682L3 4.5M3 15V4.5"
            />
          </svg>
        </button>
      )}

      {/* Cover Image */}
      <div className="mb-4 overflow-hidden rounded-[8px] h-[320px] flex-shrink-0">
        <img
          src={book.cover}
          alt={book.title}
          className="w-full h-full object-cover"
        />
      </div>

      {/* Content section */}
      <div className="flex flex-col gap-1 mt-auto">
        {/* Title */}
        <h3 className="text-[20px] font-semibold line-clamp-2 min-h-[3rem]">
          {book.title}
        </h3>

        {/* Authors */}
        <p className="text-[17px] text-[#6e6e73] truncate">
          by {book.authors.map((a) => `${a.firstName} ${a.lastName}`).join(", ")}
        </p>

        {/* Subjects + Year */}
        <small className="text-[#666] text-[14px] truncate">
          {book.subjects.map((g) => g.name).join(", ")} • {book.publishingYear}
        </small>
      </div>

      {/* Report modal */}
      <ReportBookModal
        isOpen={isReportModalOpen}
        onClose={() => setIsReportModalOpen(false)}
        book={book}
      />
    </div>
  );
}
