import React from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import PageHeader from '../../components/layout/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Button from '../../components/common/Button';
import CollectionCard from '../../features/collections/components/CollectionCard';
import CreateCollectionModal from '../../features/collections/components/modals/CreateCollectionModal';
import EditCollectionModal from '../../features/collections/components/modals/EditCollectionModal';
import DeleteCollectionModal from '../../features/collections/components/modals/DeleteCollectionModal';
import ShareCollectionModal from '../../features/collections/components/modals/ShareCollectionModal';
import AddBooksModal from '../../features/collections/components/modals/AddBooksModal';
import { useCollectionsPage } from '../../features/collections/hooks/useCollectionsPage';

export default function CollectionsPage() {
    const {
        // Collections data
        displayedCollections,
        loading,
        error,

        // Tabs
        activeTab,
        setActiveTab,

        // Create modal
        isCreateModalOpen,
        createForm,
        createLoading,
        collectionImage,
        setCollectionImage,
        collectionImagePreview,
        setCollectionImagePreview,
        openCreateModal,
        closeCreateModal,
        handleCreateCollection,

        // Edit modal
        isEditModalOpen,
        editForm,
        editLoading,
        editImage,
        setEditImage,
        editImagePreview,
        setEditImagePreview,
        openEditModal,
        closeEditModal,
        handleEditCollection,

        // Delete modal
        isDeleteModalOpen,
        deleteLoading,
        selectedCollection,
        openDeleteModal,
        closeDeleteModal,
        handleDeleteCollection,

        // Share modal
        isShareModalOpen,
        shareSearchTerm,
        shareSearchResults,
        shareLoading,
        openShareModal,
        closeShareModal,
        handleShareSearchChange,
        handleShareWithUser,
        handleUnshareWithUser,

        // Add books modal
        isAddBooksModalOpen,
        filteredBooks,
        bookSearchTerm,
        setBookSearchTerm,
        addBooksLoading,
        openAddBooksModal,
        closeAddBooksModal,
        isBookInCollection,
        handleAddBook,
        handleRemoveBook,

        // Helpers
        isOwner,
    } = useCollectionsPage();

    const tabs = [
        { id: 'public', label: 'Public Collections' },
        { id: 'mine', label: 'My Collections' },
        { id: 'shared', label: 'Shared with Me' },
    ];

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f]">
                <Header />
                <LoadingSpinner message="Loading collections..." />
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            <Header />

            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="Book Collections"
                        description="Discover public collections or create your own"
                        action={
                            <Button
                                label="Create Collection"
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

                    {/* Tabs */}
                    <div className="flex gap-2 mb-8 border-b border-[#e5e5e7]">
                        {tabs.map((tab) => (
                            <button
                                key={tab.id}
                                onClick={() => setActiveTab(tab.id)}
                                className={`px-4 py-3 text-sm font-medium transition-colors relative ${
                                    activeTab === tab.id
                                        ? 'text-[#0066cc]'
                                        : 'text-[#6e6e73] hover:text-[#1d1d1f]'
                                }`}
                            >
                                {tab.label}
                                {activeTab === tab.id && (
                                    <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-[#0066cc]" />
                                )}
                            </button>
                        ))}
                    </div>

                    {/* Collections Grid */}
                    {displayedCollections.length > 0 ? (
                        <div className="grid gap-6 grid-cols-[repeat(auto-fill,minmax(280px,1fr))]">
                            {displayedCollections.map((collection, i) => (
                                <CollectionCard
                                    key={collection.id}
                                    collection={collection}
                                    isOwner={isOwner(collection)}
                                    onEdit={openEditModal}
                                    onDelete={openDeleteModal}
                                    onShare={openShareModal}
                                    onAddBooks={openAddBooksModal}
                                    delay={i}
                                />
                            ))}
                        </div>
                    ) : (
                        <EmptyState
                            title={
                                activeTab === 'public'
                                    ? 'No public collections yet'
                                    : activeTab === 'mine'
                                    ? 'You haven\'t created any collections'
                                    : 'No collections shared with you'
                            }
                            description={
                                activeTab === 'mine'
                                    ? 'Create your first collection to organize your books'
                                    : 'Check back later for new collections'
                            }
                            action={
                                activeTab === 'mine' && (
                                    <Button
                                        label="Create Collection"
                                        type="primary"
                                        onClick={openCreateModal}
                                    />
                                )
                            }
                        />
                    )}
                </main>
            </div>

            <Footer />

            {/* Modals */}
            <CreateCollectionModal
                isOpen={isCreateModalOpen}
                form={createForm}
                loading={createLoading}
                image={collectionImage}
                setImage={setCollectionImage}
                imagePreview={collectionImagePreview}
                setImagePreview={setCollectionImagePreview}
                onSubmit={handleCreateCollection}
                onClose={closeCreateModal}
            />

            <EditCollectionModal
                isOpen={isEditModalOpen}
                collection={selectedCollection}
                form={editForm}
                loading={editLoading}
                image={editImage}
                setImage={setEditImage}
                imagePreview={editImagePreview}
                setImagePreview={setEditImagePreview}
                onSubmit={handleEditCollection}
                onClose={closeEditModal}
            />

            <DeleteCollectionModal
                isOpen={isDeleteModalOpen}
                collection={selectedCollection}
                loading={deleteLoading}
                onConfirm={handleDeleteCollection}
                onClose={closeDeleteModal}
            />

            <ShareCollectionModal
                isOpen={isShareModalOpen}
                collection={selectedCollection}
                searchTerm={shareSearchTerm}
                searchResults={shareSearchResults}
                loading={shareLoading}
                onSearchChange={handleShareSearchChange}
                onShareWithUser={handleShareWithUser}
                onUnshareWithUser={handleUnshareWithUser}
                onClose={closeShareModal}
            />

            <AddBooksModal
                isOpen={isAddBooksModalOpen}
                collection={selectedCollection}
                filteredBooks={filteredBooks}
                searchTerm={bookSearchTerm}
                setSearchTerm={setBookSearchTerm}
                loading={addBooksLoading}
                isBookInCollection={isBookInCollection}
                onAddBook={handleAddBook}
                onRemoveBook={handleRemoveBook}
                onClose={closeAddBooksModal}
            />
        </div>
    );
}
