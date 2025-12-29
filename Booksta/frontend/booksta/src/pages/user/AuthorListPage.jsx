import React, { useEffect, useState } from 'react';
import Header from "../../components/layout/Header"
import Footer from '../../components/layout/Footer';
import AuthorCard from '../../components/cards/AuthorCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import PageHeader from '../../components/layout/PageHeader';
import { useAuthors } from '../../features/books/hooks/useAuthors';
import AuthService from "../../api/services/authService";
import { useNavigate, useLocation } from "react-router-dom";
import Button from '../../components/common/Button';

export default function AuthorList() {
  const { authors, fetchAuthors, loading } = useAuthors(true);
  const [user, setUser] = useState(AuthService.getCurrentUser());
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    setUser(AuthService.getCurrentUser());
  }, [location.pathname]);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    fetchAuthors();
  }, [fetchAuthors, user, navigate]);

  const hasLibrarianRole = () => {
    if (!user || !user.roles) return false;
    return user.roles.some(role => role.name === "LIBRARIAN");
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
        <Header />
        <div className="flex-1">
          <LoadingSpinner message="Loading authors..." />
        </div>
        <Footer />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
      <Header />

      <div className="flex-1">
        <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
          <PageHeader
            title="Authors"
            description="Discover talented authors and their works"
            action={
              hasLibrarianRole() ? (
                <Button
                  label="Add Author"
                  type="secondary"
                  href="/author/dashboard/authors/new"
                />
              ) : null
            }
          />

          {authors.length === 0 ? (
            <EmptyState
              title="No Authors Yet"
              description="There are no authors in the library yet. Check back soon!"
              action={
                hasLibrarianRole() ? (
                  <Button
                    label="Add First Author"
                    type="primary"
                    href="/author/dashboard/authors/new"
                  />
                ) : null
              }
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {authors.map(a => <AuthorCard key={a.id} author={a} />)}
            </div>
          )}
        </main>
      </div>

      <Footer />
    </div>
  );
}