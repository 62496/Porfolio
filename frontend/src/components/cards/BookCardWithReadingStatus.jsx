import React, { useState, useRef, useEffect } from "react";
import ReportBookModal from "../../features/books/components/ReportBookModal";
import AddToCollectionModal from "../../features/collections/components/modals/AddToCollectionModal";
import DropdownMenu from "../common/DropdownMenu";
import Button from "../common/Button";
import { useNavigate } from "react-router-dom";

export default function BookCardWithReadingStatus({
  book,
  delay = 0,
  isFavorite = false,
  onToggleFavorite,
  onStartReading,
  onAddToCollection,
  onOpenCollectionModal,
  showReport = true,
  showAddToCollection = true,
  disableNavigation = false,
}) {
  const navigate = useNavigate();
  const [isVisible, setIsVisible] = useState(false);
  const [isReportModalOpen, setIsReportModalOpen] = useState(false);
  const [isCollectionModalOpen, setIsCollectionModalOpen] = useState(false);
  const ref = useRef(null);

  const hasReadingStatus = book.latestReadingEvent !== null && book.latestReadingEvent !== undefined;

  const handleCardClick = (e) => {
    // Don't navigate if clicking on buttons or modals
    if (disableNavigation || e.target.closest('button') || e.target.closest('[role="dialog"]')) {
      return;
    }
    navigate(`/book/${book.isbn}`);
  };

  const handleReportClick = () => {
    setIsReportModalOpen(true);
  };

  const handleCollectionSuccess = (collection) => {
    if (onAddToCollection) {
      onAddToCollection(book, collection);
    }
  };

  const handleStartReading = () => {
    if (onStartReading) {
      onStartReading(book);
    }
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

      {/* Dropdown Menu - Three Dots (Report only) */}
      {showReport && (
        <DropdownMenu
          className="absolute top-4 left-4 z-10"
          trigger={
            <button
              type="button"
              className="
                w-10 h-10 rounded-full
                flex items-center justify-center
                bg-white border-2 border-[#e5e5e7] text-[#6e6e73]
                hover:border-[#1d1d1f] hover:text-[#1d1d1f] hover:scale-110
                transition-all duration-300 ease-in-out
              "
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="currentColor"
                className="w-5 h-5"
              >
                <circle cx="12" cy="5" r="2" />
                <circle cx="12" cy="12" r="2" />
                <circle cx="12" cy="19" r="2" />
              </svg>
            </button>
          }
          items={[
            {
              label: "Report Book",
              visible: showReport,
              onClick: handleReportClick,
              icon: (
                <>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M3 3v1.5M3 21v-6m0 0l2.77-.693a9 9 0 016.208.682l.108.054a9 9 0 006.086.71l3.114-.732a48.524 48.524 0 01-.005-10.499l-3.11.732a9 9 0 01-6.085-.711l-.108-.054a9 9 0 00-6.208-.682L3 4.5M3 15V4.5" />
                </>
              )
            }
          ]}
        />
      )}

      {/* Cover Image */}
      <div className="mb-4 overflow-hidden rounded-[8px] h-[320px] flex-shrink-0 relative">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-full object-cover"
        />
      </div>

      {/* Content section */}
      <div className="flex flex-col gap-2 mt-auto">
        {/* Title */}
        <h3 className="text-[20px] font-semibold line-clamp-2 min-h-[3rem]">
          {book.title}
        </h3>

        {/* Year and Pages */}
        <small className="text-[#666] text-[14px]">
          {book.publishingYear} • {book.pages} pages
        </small>

        {/* Primary Action Button */}
        {onStartReading && (
          <Button
            type={hasReadingStatus ? "primary" : "secondary"}
            onClick={(e) => {
              e.stopPropagation();
              if (hasReadingStatus) {
                navigate(`/reading-session/${book.isbn}`);
              } else {
                handleStartReading();
              }
            }}
            className="mt-3 w-full !py-2.5 !text-[14px]"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              className="w-4 h-4"
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25" />
            </svg>
            {hasReadingStatus ? 'Continue Reading' : 'Start Reading'}
          </Button>
        )}

        {/* Add to Collection Button */}
        {(showAddToCollection || onOpenCollectionModal) && (
          <Button
            type="secondary"
            onClick={(e) => {
              e.stopPropagation();
              if (onOpenCollectionModal) {
                onOpenCollectionModal(book);
              } else {
                setIsCollectionModalOpen(true);
              }
            }}
            className="w-full !py-2.5 !text-[14px]"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              className="w-4 h-4"
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 10.5v6m3-3H9m4.06-7.19l-2.12-2.12a1.5 1.5 0 00-1.061-.44H4.5A2.25 2.25 0 002.25 6v12a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9a2.25 2.25 0 00-2.25-2.25h-5.379a1.5 1.5 0 01-1.06-.44z" />
            </svg>
            Add to Collection
          </Button>
        )}
      </div>

      {/* Report modal */}
      <ReportBookModal
        isOpen={isReportModalOpen}
        onClose={() => setIsReportModalOpen(false)}
        book={book}
      />

      {/* Add to Collection modal - only render when not using external modal */}
      {!onOpenCollectionModal && (
        <AddToCollectionModal
          isOpen={isCollectionModalOpen}
          onClose={() => setIsCollectionModalOpen(false)}
          book={book}
          onSuccess={handleCollectionSuccess}
        />
      )}
    </div>
  );
}
