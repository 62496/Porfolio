import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import AuthService from "../../api/services/authService";
import Button from "../common/Button";
import Logo from "../common/Logo";
import DropdownMenu from "../common/DropdownMenu";
import SubjectManagementModal from "../../features/subjects/components/SubjectManagementModal";

export default function Header() {
  const [user, setUser] = useState(AuthService.getCurrentUser());
  const [showSubjectModal, setShowSubjectModal] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    setUser(AuthService.getCurrentUser());
  }, [location.pathname]);

  const handleLogout = async () => {
    setUser(null);
    await AuthService.logout();
    navigate("/");
  };

  const hasAuthorRole = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(role => role.name === "AUTHOR");
  };

  const hasLibrarianRole = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(role => role.name === "LIBRARIAN");
  };

  const hasSellerRole = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(role => role.name === "SELLER");
  };

  const hasAdminRole = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(role => role.name === "ADMIN");
  };

  const myLibraryItems = [
    { label: "All Books", onClick: () => navigate("/my-books"), visible: true },
    { label: "Currently Reading", onClick: () => navigate("/progress"), visible: true },
    { label: "Favorites", onClick: () => navigate("/favorites"), visible: true },
  ];

  const exploreItems = [
    { label: "Book Catalog", onClick: () => navigate("/books"), visible: true },
    { label: "Authors", onClick: () => navigate("/authors"), visible: true },
    { label: "Series", onClick: () => navigate("/series"), visible: true },
    { label: "Marketplace", onClick: () => navigate("/marketplace"), visible: user },
    { label: "Followings", onClick: () => navigate("/followings"), visible: user },
  ];

  const createItems = [
    { label: "Add Book", onClick: () => navigate("/author/dashboard/books/new"), visible: hasAuthorRole() || hasLibrarianRole() },
    { label: "Add Author", onClick: () => navigate("/author/dashboard/authors/new"), visible: hasLibrarianRole() },
    { label: "Manage Subjects", onClick: () => setShowSubjectModal(true), visible: hasLibrarianRole() },
    { type: "separator", visible: hasAuthorRole() },
    { label: "My Series", onClick: () => navigate("/author/dashboard/series"), visible: hasAuthorRole() },
  ];

  const showCreateMenu = (hasAuthorRole() || hasLibrarianRole()) && createItems.some(item => item.visible && item.type !== 'separator');

  return (
    <header className="w-full flex items-center justify-between px-8 py-4 border-b border-[#e5e5e7] bg-white">
      <Logo />

      <nav className="flex gap-6 text-[15px] items-center">
        {user && (
          <DropdownMenu
            trigger={
              <button className="hover:underline flex items-center gap-1">
                My Library
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
                  <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clipRule="evenodd" />
                </svg>
              </button>
            }
            items={myLibraryItems}
            className="relative"
          />
        )}

        <DropdownMenu
          trigger={
            <button className="hover:underline flex items-center gap-1">
              Explore
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
                <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clipRule="evenodd" />
              </svg>
            </button>
          }
          items={exploreItems}
          className="relative"
        />

        {user && (
          <Link to="/messages" className="hover:underline">Messages</Link>
        )}

        {hasLibrarianRole() && (
          <Link to="/reports" className="hover:underline">Reports</Link>
        )}

        {hasSellerRole() && (
          <Link to="/author/dashboard/inventory" className="hover:underline">Inventory</Link>
        )}

        {hasAdminRole() && (
          <Link to="/admin/users" className="hover:underline">Users</Link>
        )}

        {showCreateMenu && (
          <DropdownMenu
            trigger={
              <button className="flex items-center gap-1 px-4 py-2 border border-[#e5e5e7] text-[#1d1d1f] rounded-lg text-[13px] font-medium hover:border-[#1d1d1f] hover:bg-[#f5f5f7] transition-colors">
                + New
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
                  <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clipRule="evenodd" />
                </svg>
              </button>
            }
            items={createItems}
            className="relative"
          />
        )}
      </nav>

      {!user && (
        <div className="flex gap-3">
          <Button label="Sign In" type="secondary" href="/login" />
          <Button label="Sign Up" type="signup" href="/login" />
        </div>
      )}

      {user && (
        <div className="flex items-center gap-3">
          {user.picture && (
            <button
              onClick={() => navigate("/profile")}
              className="w-9 h-9 rounded-full overflow-hidden border border-[#e5e5e7]"
            >
              <img
                src={user.picture}
                alt={`${user.firstName} ${user.lastName}`}
                className="w-full h-full object-cover"
              />
            </button>
          )}

          <div className="text-right text-sm">
            <div className="text-[#6e6e73] leading-tight">Welcome,</div>
            <button
              onClick={() => navigate("/profile")}
              className="font-semibold hover:underline"
            >
              {user.firstName} {user.lastName}
            </button>
          </div>

          <button
            onClick={handleLogout}
            className="text-xs text-[#6e6e73] hover:underline"
          >
            Logout
          </button>
        </div>
      )}

      {/* Subject Management Modal */}
      <SubjectManagementModal
        isOpen={showSubjectModal}
        onClose={() => setShowSubjectModal(false)}
      />
    </header>
  );
}
