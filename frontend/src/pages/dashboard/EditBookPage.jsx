import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import bookService from "../../api/services/bookService";
import AuthService from "../../api/services/authService";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import BookForm from "../../features/books/components/BookForm";

export default function EditBookPage() {
  const { isbn } = useParams();
  const navigate = useNavigate();
  const [book, setBook] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const currentUser = AuthService.getCurrentUser();

  useEffect(() => {
    const fetchBook = async () => {
      try {
        setLoading(true);
        const data = await bookService.getById(isbn);
        const formattedBook = bookService.formatBookForDisplay(data);

        const hasLibrarianRole = currentUser?.roles?.some(
          (role) => role.name === "LIBRARIAN"
        );
        const hasAuthorRole = currentUser?.roles?.some(
          (role) => role.name === "AUTHOR"
        );
        const isBookAuthor = formattedBook.authors?.some(
          (author) => author.user?.id === currentUser?.id
        );

        if (!hasLibrarianRole && !(hasAuthorRole && isBookAuthor)) {
          setError("You don't have permission to edit this book");
          return;
        }

        setBook(formattedBook);
        setError(null);
      } catch (err) {
        console.error("Error fetching book:", err);
        setError("Failed to load book details. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    if (isbn) {
      fetchBook();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isbn]);

  if (loading) {
    return (
      <div className="min-h-screen bg-[#f5f5f7] flex flex-col">
        <Header />
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <div className="w-16 h-16 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
            <p className="text-[17px] text-[#6e6e73]">Loading book...</p>
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
            <h2 className="text-[28px] font-semibold mb-4">Access Denied</h2>
            <p className="text-[17px] text-[#6e6e73] mb-8">
              {error || "Unable to load book for editing."}
            </p>
            <button
              onClick={() => navigate("/books")}
              className="px-8 py-3 bg-[#1d1d1f] text-white rounded-full text-[17px] hover:bg-[#424245] transition-colors"
            >
              Back to Books
            </button>
          </div>
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header />
      <div className="flex-1">
        <BookForm mode="edit" bookData={book} />
      </div>
      <Footer />
    </div>
  );
}
