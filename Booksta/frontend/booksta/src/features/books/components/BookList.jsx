import userService from "../../../api/services/userService";
import { useState, useEffect } from "react";
import BookCard from "../../../components/cards/BookCard";
import { useNavigate } from "react-router";
import bookService from "../../../api/services/bookService";
import useToast from "../../../hooks/useToast";
import Toast from "../../../components/common/Toast";

export default function BookList({ books }) {
  const navigate = useNavigate();
  const { toast, showToast, hideToast } = useToast();

  const [ownedBooks, setOwnedBooks] = useState([]);
  const [favorites, setFavorites] = useState([]);

  const currentUser = userService.getCurrentUser();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      if (currentUser?.id) {
        const favoritesData = await userService.getFavorites();
        const formattedFavorites = favoritesData.map((book) =>
          bookService.formatBookForDisplay(book)
        );
        setFavorites(formattedFavorites);

        const ownedData = await userService.getOwnedBooks();
        const formattedOwned = ownedData.map((book) =>
          bookService.formatBookForDisplay(book)
        );
        setOwnedBooks(formattedOwned);
      }
    } catch {
      showToast("Error loading data", "error");
    }
  };

  const handleToggleFavorite = async (book) => {
    if (!currentUser?.id) {
      showToast("You must be logged in to add favorites", "warning");
      navigate("/login");
      return;
    }

    try {
      const isFav = favorites.some((f) => f.isbn === book.isbn);

      if (isFav) {
        await userService.removeFavorite(book.isbn);
        setFavorites((prev) => prev.filter((f) => f.isbn !== book.isbn));
      } else {
        await userService.addFavorite(book.isbn);
        setFavorites((prev) => [...prev, book]);
      }
    } catch {
      showToast("Error updating favorites", "error");
    }
  };

  const handleToggleOwned = async (book) => {
    if (!currentUser?.id) {
      showToast("You must be logged in to manage your books", "warning");
      navigate("/login");
      return;
    }

    try {
      const alreadyOwned = userService.isOwned(ownedBooks, book.isbn);

      if (alreadyOwned) {
        await userService.removeOwnedBook(book.isbn);
        setOwnedBooks((prev) => prev.filter((b) => b.isbn !== book.isbn));
      } else {
        await userService.addOwnedBook(book.isbn);
        setOwnedBooks((prev) => [...prev, book]);
      }
    } catch {
      showToast("Error updating your books", "error");
    }
  };

  return (
    <>
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={hideToast}
          duration={toast.duration}
        />
      )}
      <div className="max-w-[1200px] mx-auto text-center">
        <div className="grid gap-8 grid-cols-[repeat(auto-fill,minmax(250px,1fr))]">
        {books.length > 0 ? (
          books.map((book, i) => {
            const isFavorite = favorites.some((b) => b.isbn === book.isbn);

            const isOwned = userService.isOwned(ownedBooks, book.isbn);

            return (
              <BookCard
                key={book.isbn}
                book={book}
                delay={i}
                isFavorite={isFavorite}
                onToggleFavorite={handleToggleFavorite}
                isOwned={isOwned}
                onToggleOwned={handleToggleOwned}
              />
            );
          })
        ) : (
          <div className="col-span-full text-center py-12">
            <p className="text-[21px] text-[#6e6e73]">No books found.</p>

          </div>
        )}
        </div>
      </div>
    </>
  );
}
