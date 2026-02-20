import { Route, Routes } from "react-router-dom";

// Auth
import PrivateRoute from "../features/auth/components/PrivateRoute";

// Public pages (no auth required)
import Login from "../pages/public/Login";

// User pages (require login)
import LandingPage from "../pages/user/LandingPage";
import BookCatalog from "../pages/user/BookCatalog";
import BookDetailPage from "../pages/user/BookDetailPage";
import AuthorListPage from "../pages/user/AuthorListPage";
import AuthorDetailPage from "../pages/user/AuthorDetailPage";
import SeriesListPage from "../pages/user/SeriesListPage";
import FavoritesPage from "../pages/user/FavoritesPage";
import ProfilePage from "../pages/user/ProfilePage";
import BookCollectionDetailPage from "../pages/user/BookCollectionDetailPage";
import CollectionsPage from "../pages/user/CollectionsPage";
import FollowingsPage from "../pages/user/FollowingsPage";
import MessagesPage from "../pages/user/MessagesPage";
import ReadingProgressPage from "../pages/user/ReadingProgressPage";
import MyBooksPage from "../pages/user/MyBooksPage";
import ReadingSessionPage from "../pages/user/ReadingSessionPage";
import MarketplacePage from "../pages/user/MarketplacePage";

// Dashboard pages (AUTHOR/LIBRARIAN)
import AddBookPage from "../pages/dashboard/AddBookPage";
import AddAuthorPage from "../pages/dashboard/AddAuthorPage";
import EditBookPage from "../pages/dashboard/EditBookPage";
import EditAuthorPage from "../pages/dashboard/EditAuthorPage";
import InventoryPage from "../pages/dashboard/InventoryPage";
import SeriesManagementPage from "../pages/author/SeriesManagementPage";

// Admin pages (LIBRARIAN/ADMIN)
import ReportsPage from "../pages/admin/ReportsPage";
import UserManagementPage from "../pages/admin/UserManagementPage";

export default function AppRoutes() {
    return (
        <Routes>
            {/* Public route - only login */}
            <Route path="/login" element={<Login />} />

            {/* Protected routes - require login */}
            <Route path="/" element={
                <LandingPage />
            } />
            <Route path="/books" element={
                <PrivateRoute>
                    <BookCatalog />
                </PrivateRoute>
            } />
            <Route path="/book/:id" element={
                <PrivateRoute>
                    <BookDetailPage />
                </PrivateRoute>
            } />
            <Route path="/authors" element={
                <PrivateRoute>
                    <AuthorListPage />
                </PrivateRoute>
            } />
            <Route path="/authors/:id" element={
                <PrivateRoute>
                    <AuthorDetailPage />
                </PrivateRoute>
            } />
            <Route path="/series" element={
                <PrivateRoute>
                    <SeriesListPage />
                </PrivateRoute>
            } />
            <Route path="/favorites" element={
                <PrivateRoute>
                    <FavoritesPage />
                </PrivateRoute>
            } />
            <Route path="/profile" element={
                <PrivateRoute>
                    <ProfilePage />
                </PrivateRoute>
            } />
            <Route path="/collections" element={
                <PrivateRoute>
                    <CollectionsPage />
                </PrivateRoute>
            } />
            <Route path="/collections/:collectionId" element={
                <PrivateRoute>
                    <BookCollectionDetailPage />
                </PrivateRoute>
            } />
            <Route path="/followings" element={
                <PrivateRoute>
                    <FollowingsPage />
                </PrivateRoute>
            } />
            <Route path="/messages" element={
                <PrivateRoute>
                    <MessagesPage />
                </PrivateRoute>
            } />
            <Route path="/progress" element={
                <PrivateRoute>
                    <ReadingProgressPage />
                </PrivateRoute>
            } />
            <Route path="/my-books" element={
                <PrivateRoute>
                    <MyBooksPage />
                </PrivateRoute>
            } />
            <Route path="/reading-session/:isbn" element={
                <PrivateRoute>
                    <ReadingSessionPage />
                </PrivateRoute>
            } />
            <Route path="/marketplace" element={
                <PrivateRoute>
                    <MarketplacePage />
                </PrivateRoute>
            } />

            {/* AUTHOR or LIBRARIAN routes */}
            <Route path="/books/:isbn/edit" element={
                <PrivateRoute roles={["AUTHOR", "LIBRARIAN"]}>
                    <EditBookPage />
                </PrivateRoute>
            } />
            <Route path="/author/dashboard/books/new" element={
                <PrivateRoute roles={["AUTHOR", "LIBRARIAN"]}>
                    <AddBookPage />
                </PrivateRoute>
            } />
            <Route path="/author/dashboard/authors/new" element={
                <PrivateRoute roles={["AUTHOR", "LIBRARIAN"]}>
                    <AddAuthorPage />
                </PrivateRoute>
            } />
            <Route path="/authors/:id/edit" element={
                <PrivateRoute roles={["LIBRARIAN"]}>
                    <EditAuthorPage />
                </PrivateRoute>
            } />
            <Route path="/author/dashboard/inventory" element={
                <PrivateRoute roles={["SELLER"]}>
                    <InventoryPage />
                </PrivateRoute>
            } />
            <Route path="/author/dashboard/series" element={
                <PrivateRoute roles={["AUTHOR"]}>
                    <SeriesManagementPage />
                </PrivateRoute>
            } />

            {/* LIBRARIAN only routes */}
            <Route path="/reports" element={
                <PrivateRoute roles={["LIBRARIAN"]}>
                    <ReportsPage />
                </PrivateRoute>
            } />

            {/* ADMIN only routes */}
            <Route path="/admin/users" element={
                <PrivateRoute roles={["ADMIN"]}>
                    <UserManagementPage />
                </PrivateRoute>
            } />
        </Routes>
    );
}
