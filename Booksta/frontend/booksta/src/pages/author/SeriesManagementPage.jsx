import React from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import PageHeader from '../../components/layout/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Button from '../../components/common/Button';
import SeriesManagementCard from '../../features/series/components/SeriesManagementCard';
import CreateSeriesModal from '../../features/series/components/modals/CreateSeriesModal';
import EditSeriesModal from '../../features/series/components/modals/EditSeriesModal';
import DeleteSeriesModal from '../../features/series/components/modals/DeleteSeriesModal';
import AddBooksToSeriesModal from '../../features/series/components/modals/AddBooksToSeriesModal';
import { useSeriesManagement } from '../../features/series/hooks/useSeriesManagement';

export default function SeriesManagementPage() {
    const {
        // User info
        isLibrarian,
        isAuthor,

        // Series data
        series,
        loading,
        error,

        // Authors (for librarian)
        authors,

        // Selected series
        selectedSeries,

        // Create modal
        isCreateModalOpen,
        createForm,
        createLoading,
        openCreateModal,
        closeCreateModal,
        handleCreateSeries,

        // Edit modal
        isEditModalOpen,
        editForm,
        editLoading,
        openEditModal,
        closeEditModal,
        handleEditSeries,

        // Delete modal
        isDeleteModalOpen,
        deleteLoading,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteSeries,

        // Add books modal
        isAddBooksModalOpen,
        filteredBooks,
        seriesBooks,
        bookSearchTerm,
        setBookSearchTerm,
        addBooksLoading,
        openAddBooksModal,
        closeAddBooksModal,
        isBookInSeries,
        handleAddBook,
        handleRemoveBook,
    } = useSeriesManagement();

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f]">
                <Header />
                <LoadingSpinner message="Loading series..." />
                <Footer />
            </div>
        );
    }

    // Page title and description based on role
    const pageTitle = isLibrarian ? "Series Management" : "My Series";
    const pageDescription = isLibrarian
        ? "Manage all book series in the system"
        : "Manage your book series";

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            <Header />

            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title={pageTitle}
                        description={pageDescription}
                        action={
                            <Button
                                label="Create Series"
                                type="primary"
                                onClick={openCreateModal}
                            />
                        }
                    />

                    {/* Error Message */}
                    {error && (
                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                            {error}
                        </div>
                    )}

                    {/* Info banner for librarians */}
                    {isLibrarian && (
                        <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-xl text-blue-700 text-sm">
                            As a librarian, you can manage all series. When creating a series, you must select an author.
                        </div>
                    )}

                    {/* Series Grid */}
                    {series.length > 0 ? (
                        <div className="grid gap-6 grid-cols-[repeat(auto-fill,minmax(280px,1fr))]">
                            {series.map((s, i) => (
                                <SeriesManagementCard
                                    key={s.id}
                                    series={s}
                                    onEdit={openEditModal}
                                    onDelete={openDeleteModal}
                                    onAddBooks={openAddBooksModal}
                                    showAuthor={isLibrarian}
                                    delay={i}
                                />
                            ))}
                        </div>
                    ) : (
                        <EmptyState
                            title="No series yet"
                            description={isLibrarian
                                ? "No series have been created yet"
                                : "Create your first series to organize your books"
                            }
                            action={
                                <Button
                                    label="Create Series"
                                    type="primary"
                                    onClick={openCreateModal}
                                />
                            }
                        />
                    )}
                </main>
            </div>

            <Footer />

            {/* Modals */}
            <CreateSeriesModal
                isOpen={isCreateModalOpen}
                form={createForm}
                loading={createLoading}
                onSubmit={handleCreateSeries}
                onClose={closeCreateModal}
                isLibrarian={isLibrarian}
                authors={authors}
            />

            <EditSeriesModal
                isOpen={isEditModalOpen}
                series={selectedSeries}
                form={editForm}
                loading={editLoading}
                onSubmit={handleEditSeries}
                onClose={closeEditModal}
            />

            <DeleteSeriesModal
                isOpen={isDeleteModalOpen}
                series={selectedSeries}
                loading={deleteLoading}
                onConfirm={handleDeleteSeries}
                onClose={closeDeleteModal}
            />

            <AddBooksToSeriesModal
                isOpen={isAddBooksModalOpen}
                series={selectedSeries}
                seriesBooks={seriesBooks}
                filteredBooks={filteredBooks}
                searchTerm={bookSearchTerm}
                setSearchTerm={setBookSearchTerm}
                loading={addBooksLoading}
                isBookInSeries={isBookInSeries}
                onAddBook={handleAddBook}
                onRemoveBook={handleRemoveBook}
                onClose={closeAddBooksModal}
            />
        </div>
    );
}
