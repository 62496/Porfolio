import React, { useEffect } from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import SeriesCard from '../../components/cards/SeriesCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import PageHeader from '../../components/layout/PageHeader';
import useSeries from '../../features/books/hooks/useSeries';
import userService from "../../api/services/userService";
import { useNavigate } from "react-router-dom";

export default function SeriesList() {
  const { series, fetchSeries, loading } = useSeries(true);

  const navigate = useNavigate();
  const currentUser = userService.getCurrentUser();

  useEffect(() => {
    if (!currentUser) {
      navigate('/login');
      return;
    } else {
      fetchSeries()
    }
  }, [fetchSeries]);

  if (loading) {
    return (
      <div className="min-h-screen bg-white font-sans text-[#1d1d1f] flex flex-col">
        <Header />
        <div className="flex-1">
          <LoadingSpinner message="Loading series..." />
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
            title="Book Series"
            description="Explore captivating book series and collections"
          />

          {series.length === 0 ? (
            <EmptyState
              title="No Series Available"
              description="There are no book series in the library yet. Check back soon for new collections!"
            />
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {series.map(s => <SeriesCard key={s.id} series={s} />)}
            </div>
          )}
        </main>
      </div>

      <Footer />
    </div>
  );
}