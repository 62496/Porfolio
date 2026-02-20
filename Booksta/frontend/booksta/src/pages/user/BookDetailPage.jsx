import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useBookDetail } from "../../features/books/hooks/useBookDetail";
import Button from "../../components/common/Button";
import ReportBookModal from "../../features/books/components/ReportBookModal";
import DropdownMenu from "../../components/common/DropdownMenu";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";

export default function BookDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const {
    book,
    loading,
    error,
    isFavorite,
    isOwned,
    showFullDescription,
    isReportModalOpen,
    DESCRIPTION_PREVIEW_LENGTH,
    handleToggleFavorite,
    handleToggleOwned,
    handleBackToBooks,
    canEditBook,
    openReportModal,
    closeReportModal,
    toggleDescription,
  } = useBookDetail(id);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
        <Header />
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <div className="w-16 h-16 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
            <p className="text-[17px] text-[#6e6e73]">Loading book details...</p>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  if (error || !book) {
    return (
      <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
        <Header />
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center max-w-md mx-auto px-6">
            <div className="mb-6">
              <svg
                className="w-20 h-20 mx-auto text-[#ff3b30]"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>
            <h2 className="text-[28px] font-semibold mb-4">Book Not Found</h2>
            <p className="text-[17px] text-[#6e6e73] mb-8">
              {error || "The book you're looking for doesn't exist or has been removed."}
            </p>
            <Button
              label="Back to Books"
              type="primary"
              onClick={handleBackToBooks}
            />
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
      <Header />
      <div className="flex-1 py-12">
        <div className="max-w-6xl mx-auto px-6">
          {/* Back Button */}
          <div className="mb-8">
            <button
              onClick={handleBackToBooks}
              className="w-12 h-12 rounded-full bg-white border-2 border-[#e5e5e7] text-[#6e6e73] hover:border-[#1d1d1f] hover:text-[#1d1d1f] hover:scale-110 transition-all duration-300 ease-in-out flex items-center justify-center shadow-sm"
              aria-label="Go back"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                className="w-6 h-6"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M15 19l-7-7 7-7"
                />
              </svg>
            </button>
          </div>

          {/* Book Detail Card */}
          <div className="bg-white rounded-[24px] border border-[#e5e5e7] overflow-hidden shadow-sm">
            <div className="grid md:grid-cols-2 gap-8 p-8 md:p-12">
              {/* Left Column - Book Cover */}
              <div className="flex flex-col items-center md:items-start">
                <div className="w-full max-w-md shadow-lg mb-6 aspect-[3/4] rounded-[16px] relative">
                  <img
                    src={book.cover || "/placeholder-book.png"}
                    alt={book.title}
                    className="w-full h-full object-cover rounded-[16px]"
                  />

                  {/* Favorite Heart Button */}
                  <button
                    type="button"
                    onClick={handleToggleFavorite}
                    className={`
                    absolute top-4 right-4 w-12 h-12 rounded-full
                    flex items-center justify-center
                    transition-all duration-300 ease-in-out
                    ${isFavorite
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
                      className="w-6 h-6"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z"
                      />
                    </svg>
                  </button>

                  {/* Three Dots Menu */}
                  <DropdownMenu
                    className="absolute top-4 left-4 z-10"
                    trigger={
                      <button
                        type="button"
                        className="w-12 h-12 rounded-full bg-white border-2 border-[#e5e5e7] text-[#6e6e73] hover:border-[#1d1d1f] hover:text-[#1d1d1f] hover:scale-110 transition-all duration-300 ease-in-out flex items-center justify-center"
                      >
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 24 24"
                          fill="currentColor"
                          className="w-6 h-6"
                        >
                          <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" />
                        </svg>
                      </button>
                    }
                    items={[
                      {
                        label: "Report Issue",
                        onClick: openReportModal,
                        className: "text-[#ff9500] hover:bg-[#fff4e6]",
                        icon: (
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            d="M3 3v1.5M3 21v-6m0 0l2.77-.693a9 9 0 016.208.682l.108.054a9 9 0 006.086.71l3.114-.732a48.524 48.524 0 01-.005-10.499l-3.11.732a9 9 0 01-6.085-.711l-.108-.054a9 9 0 00-6.208-.682L3 4.5M3 15V4.5"
                          />
                        ),
                      },
                    ]}
                  />
                </div>

                {/* Action Button - Add to My Books */}
                <div className="w-full max-w-md">
                  <Button
                    label={isOwned ? "Remove from My Books" : "Add to My Books"}
                    type={isOwned ? "secondary" : "primary"}
                    onClick={handleToggleOwned}
                    className="w-full"
                  />
                </div>
              </div>
              {/* Right Column - Book Information */}
              <div className="flex flex-col gap-6">
                {/* Edit Book Admin Control - Contextual, outside reader flow */}
                {canEditBook() && (
                  <div className="flex justify-end">
                    <Button
                      type="secondary"
                      onClick={() => navigate(`/books/${book.isbn}/edit`)}
                      className="!px-5 !py-2.5"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        className="w-4 h-4"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          d="M16.862 4.487l1.687-1.688a1.875 1.875 0 112.652 2.652L10.582 16.07a4.5 4.5 0 01-1.897 1.13L6 18l.8-2.685a4.5 4.5 0 011.13-1.897l8.932-8.931zm0 0L19.5 7.125M18 14v4.75A2.25 2.25 0 0115.75 21H5.25A2.25 2.25 0 013 18.75V8.25A2.25 2.25 0 015.25 6H10"
                        />
                      </svg>
                      Edit Book
                    </Button>
                  </div>
                )}

                {/* Title */}
                <div>
                  <h1 className="text-[40px] md:text-[48px] font-bold leading-tight mb-2">
                    {book.title}
                  </h1>
                  <p className="text-[20px] text-[#6e6e73]">
                    by {book.author}
                  </p>
                </div>

                {/* Book Metadata */}
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-[#f5f5f7] rounded-[12px] p-4">
                    <p className="text-[13px] text-[#6e6e73] uppercase font-semibold mb-1">
                      Published
                    </p>
                    <p className="text-[17px] font-semibold">{book.year}</p>
                  </div>
                  <div className="bg-[#f5f5f7] rounded-[12px] p-4">
                    <p className="text-[13px] text-[#6e6e73] uppercase font-semibold mb-1">
                      ISBN
                    </p>
                    <p className="text-[17px] font-semibold">{book.isbn}</p>
                  </div>
                  {book.pages && (
                    <div className="bg-[#f5f5f7] rounded-[12px] p-4">
                      <p className="text-[13px] text-[#6e6e73] uppercase font-semibold mb-1">
                        Pages
                      </p>
                      <p className="text-[17px] font-semibold">{book.pages}</p>
                    </div>
                  )}
                  <div className="bg-[#f5f5f7] rounded-[12px] p-4">
                    <p className="text-[13px] text-[#6e6e73] uppercase font-semibold mb-1">
                      Genre
                    </p>
                    <p className="text-[17px] font-semibold truncate">{book.genre}</p>
                  </div>
                </div>

                {/* Authors */}
                {book.authors && book.authors.length > 0 && (
                  <div>
                    <h3 className="text-[20px] font-semibold mb-3">
                      {book.authors.length > 1 ? "Authors" : "Author"}
                    </h3>
                    <div className="flex flex-wrap gap-2">
                      {book.authors.map((author) => (
                        <span
                          key={author.id}
                          className="inline-flex items-center px-4 py-2 bg-[#1d1d1f] text-white rounded-full text-[15px]"
                        >
                          {author.firstName} {author.lastName}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Subjects/Genres */}
                {book.subjects && book.subjects.length > 0 && (
                  <div>
                    <h3 className="text-[20px] font-semibold mb-3">Subjects</h3>
                    <div className="flex flex-wrap gap-2">
                      {book.subjects.map((subject) => (
                        <span
                          key={subject.id}
                          className="inline-flex items-center px-4 py-2 border border-[#e5e5e7] text-[#1d1d1f] rounded-full text-[15px] hover:border-[#1d1d1f] transition-colors"
                        >
                          {subject.name}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Description */}
                <div>
                  <h3 className="text-[20px] font-semibold mb-3">Description</h3>
                  <p className="text-[17px] text-[#1d1d1f] leading-relaxed whitespace-pre-line">
                    {showFullDescription || book.description.length <= DESCRIPTION_PREVIEW_LENGTH
                      ? book.description
                      : `${book.description.substring(0, DESCRIPTION_PREVIEW_LENGTH)}...`}
                  </p>
                  {book.description.length > DESCRIPTION_PREVIEW_LENGTH && (
                    <div className="mt-3">
                      <Button
                        label={showFullDescription ? "Show Less" : "View All"}
                        type="link"
                        onClick={toggleDescription}
                      />
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Report Modal */}
        <ReportBookModal
          isOpen={isReportModalOpen}
          onClose={closeReportModal}
          book={book}
        />
      </div>
      <Footer />
    </div>
  );
}
