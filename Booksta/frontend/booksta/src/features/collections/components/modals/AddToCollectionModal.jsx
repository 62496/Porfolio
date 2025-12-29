import React, { useState, useEffect } from "react";
import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";
import LoadingSpinner from "../../../../components/common/LoadingSpinner";
import GenericInput from "../../../../components/forms/GenericInput";
import bookCollectionsService from "../../../../api/services/bookCollectionsService";

export default function AddToCollectionModal({ isOpen, onClose, book, onSuccess }) {
    const [collections, setCollections] = useState([]);
    const [loading, setLoading] = useState(true);
    const [adding, setAdding] = useState(false);
    const [selectedCollectionId, setSelectedCollectionId] = useState(null);
    const [error, setError] = useState(null);

    // Create collection state
    const [showCreateForm, setShowCreateForm] = useState(false);
    const [creating, setCreating] = useState(false);
    const [newCollectionName, setNewCollectionName] = useState("");
    const [newCollectionDescription, setNewCollectionDescription] = useState("");
    const [newCollectionVisibility, setNewCollectionVisibility] = useState("PRIVATE");

    useEffect(() => {
        if (isOpen) {
            fetchCollections();
            setShowCreateForm(false);
            setNewCollectionName("");
            setNewCollectionDescription("");
            setNewCollectionVisibility("PRIVATE");
        }
    }, [isOpen]);

    const fetchCollections = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await bookCollectionsService.getOwnCollections();
            setCollections(data || []);
        } catch (err) {
            console.error("Error fetching collections:", err);
            setError("Failed to load collections");
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCollection = async () => {
        if (!selectedCollectionId || !book) return;

        setAdding(true);
        setError(null);
        try {
            await bookCollectionsService.addBook(selectedCollectionId, book.isbn);
            if (onSuccess) {
                const selectedCollection = collections.find(c => c.id === selectedCollectionId);
                onSuccess(selectedCollection);
            }
            onClose();
        } catch (err) {
            console.error("Error adding book to collection:", err);
            if (err.response?.status === 409) {
                setError("This book is already in the selected collection");
            } else {
                setError("Failed to add book to collection");
            }
        } finally {
            setAdding(false);
        }
    };

    const handleCreateCollection = async () => {
        if (!newCollectionName.trim()) return;

        setCreating(true);
        setError(null);
        try {
            const newCollection = await bookCollectionsService.create({
                name: newCollectionName.trim(),
                description: newCollectionDescription.trim(),
                visibility: newCollectionVisibility,
            });

            // Refresh collections and select the new one
            await fetchCollections();
            setSelectedCollectionId(newCollection.id);
            setShowCreateForm(false);
            setNewCollectionName("");
            setNewCollectionDescription("");
            setNewCollectionVisibility("PRIVATE");
        } catch (err) {
            console.error("Error creating collection:", err);
            setError("Failed to create collection");
        } finally {
            setCreating(false);
        }
    };

    const handleClose = () => {
        setSelectedCollectionId(null);
        setError(null);
        setShowCreateForm(false);
        onClose();
    };

    return (
        <Modal isOpen={isOpen} onClose={handleClose} title="Add to Collection">
            {loading ? (
                <div className="py-8">
                    <LoadingSpinner message="Loading collections..." />
                </div>
            ) : (
                <div className="space-y-4">
                    {/* Book Info */}
                    <div className="flex items-center gap-4 p-4 bg-[#f5f5f7] rounded-[12px]">
                        {book?.imageUrl && (
                            <img
                                src={book.imageUrl}
                                alt={book.title}
                                className="w-16 h-24 object-cover rounded-[8px]"
                            />
                        )}
                        <div className="flex-1 min-w-0">
                            <h3 className="font-semibold text-[#1d1d1f] line-clamp-2">
                                {book?.title}
                            </h3>
                            <p className="text-[14px] text-[#6e6e73]">
                                {book?.publishingYear}
                            </p>
                        </div>
                    </div>

                    {/* Error Message */}
                    {error && (
                        <div className="p-3 bg-red-50 border border-red-200 rounded-[8px] text-red-600 text-[14px]">
                            {error}
                        </div>
                    )}

                    {/* Create Collection Form */}
                    {showCreateForm ? (
                        <div className="space-y-4">
                            <div className="flex items-center justify-between">
                                <h4 className="font-medium text-[#1d1d1f]">Create New Collection</h4>
                                <Button
                                    type="link"
                                    onClick={() => setShowCreateForm(false)}
                                >
                                    Cancel
                                </Button>
                            </div>

                            {/* Name Input */}
                            <GenericInput
                                label="Collection Name"
                                name="name"
                                value={newCollectionName}
                                onChange={(e) => setNewCollectionName(e.target.value)}
                                placeholder="My Reading List"
                                maxLength={100}
                                required
                            />

                            {/* Description Input */}
                            <GenericInput
                                type="textarea"
                                label="Description"
                                name="description"
                                value={newCollectionDescription}
                                onChange={(e) => setNewCollectionDescription(e.target.value)}
                                placeholder="What's this collection about?"
                                rows={2}
                                maxLength={500}
                            />

                            {/* Visibility Toggle */}
                            <div>
                                <label className="block text-sm font-medium text-[#1d1d1f] mb-2">
                                    Visibility
                                </label>
                                <div className="flex gap-2">
                                    <Button
                                        type={newCollectionVisibility === "PRIVATE" ? "primary" : "filter"}
                                        onClick={() => setNewCollectionVisibility("PRIVATE")}
                                        className="flex-1"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
                                        </svg>
                                        Private
                                    </Button>
                                    <Button
                                        type={newCollectionVisibility === "PUBLIC" ? "primary" : "filter"}
                                        onClick={() => setNewCollectionVisibility("PUBLIC")}
                                        className="flex-1"
                                    >
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
                                        </svg>
                                        Public
                                    </Button>
                                </div>
                            </div>

                            {/* Create Button */}
                            <Button
                                type="primary"
                                onClick={handleCreateCollection}
                                disabled={!newCollectionName.trim() || creating}
                                className="w-full"
                            >
                                {creating ? "Creating..." : "Create Collection"}
                            </Button>
                        </div>
                    ) : (
                        <>
                            {/* Collections List or Empty State */}
                            {collections.length === 0 ? (
                                <div className="text-center py-8">
                                    <div className="w-16 h-16 bg-[#f5f5f7] rounded-full flex items-center justify-center mx-auto mb-4">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-8 h-8 text-[#6e6e73]">
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 12.75V12A2.25 2.25 0 014.5 9.75h15A2.25 2.25 0 0121.75 12v.75m-8.69-6.44l-2.12-2.12a1.5 1.5 0 00-1.061-.44H4.5A2.25 2.25 0 002.25 6v12a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9a2.25 2.25 0 00-2.25-2.25h-5.379a1.5 1.5 0 01-1.06-.44z" />
                                        </svg>
                                    </div>
                                    <p className="text-[#6e6e73] mb-4">You don't have any collections yet.</p>
                                    <Button
                                        type="primary"
                                        onClick={() => setShowCreateForm(true)}
                                    >
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            viewBox="0 0 24 24"
                                            fill="none"
                                            stroke="currentColor"
                                            strokeWidth="2"
                                            className="w-4 h-4"
                                        >
                                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                                        </svg>
                                        Create Your First Collection
                                    </Button>
                                </div>
                            ) : (
                                <>
                                    <div className="flex items-center justify-between">
                                        <p className="text-[14px] text-[#6e6e73]">
                                            Select a collection:
                                        </p>
                                        <Button
                                            type="link"
                                            onClick={() => setShowCreateForm(true)}
                                            className="!text-[#0066cc] hover:!text-[#004499]"
                                        >
                                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
                                            </svg>
                                            New
                                        </Button>
                                    </div>
                                    <div className="space-y-2 max-h-[300px] overflow-y-auto">
                                        {collections.map((collection) => (
                                            <button
                                                key={collection.id}
                                                type="button"
                                                onClick={() => setSelectedCollectionId(collection.id)}
                                                className={`
                                                    w-full p-4 rounded-[12px] text-left transition-all
                                                    flex items-center gap-3
                                                    ${selectedCollectionId === collection.id
                                                        ? 'bg-[#1d1d1f] text-white'
                                                        : 'bg-[#f5f5f7] hover:bg-[#e5e5e7] text-[#1d1d1f]'
                                                    }
                                                `}
                                            >
                                                {/* Collection Icon */}
                                                <div className={`
                                                    w-10 h-10 rounded-[8px] flex items-center justify-center flex-shrink-0
                                                    ${selectedCollectionId === collection.id
                                                        ? 'bg-white/20'
                                                        : 'bg-white'
                                                    }
                                                `}>
                                                    <svg
                                                        xmlns="http://www.w3.org/2000/svg"
                                                        viewBox="0 0 24 24"
                                                        fill="none"
                                                        stroke="currentColor"
                                                        strokeWidth="1.5"
                                                        className="w-5 h-5"
                                                    >
                                                        <path
                                                            strokeLinecap="round"
                                                            strokeLinejoin="round"
                                                            d="M2.25 12.75V12A2.25 2.25 0 014.5 9.75h15A2.25 2.25 0 0121.75 12v.75m-8.69-6.44l-2.12-2.12a1.5 1.5 0 00-1.061-.44H4.5A2.25 2.25 0 002.25 6v12a2.25 2.25 0 002.25 2.25h15A2.25 2.25 0 0021.75 18V9a2.25 2.25 0 00-2.25-2.25h-5.379a1.5 1.5 0 01-1.06-.44z"
                                                        />
                                                    </svg>
                                                </div>

                                                {/* Collection Info */}
                                                <div className="flex-1 min-w-0">
                                                    <p className="font-medium truncate">
                                                        {collection.name}
                                                    </p>
                                                    <p className={`text-[13px] ${selectedCollectionId === collection.id ? 'text-white/70' : 'text-[#6e6e73]'}`}>
                                                        {collection.books?.length || collection.bookCount || 0} books
                                                        {collection.visibility === 'PUBLIC' && ' • Public'}
                                                    </p>
                                                </div>

                                                {/* Selected Indicator */}
                                                {selectedCollectionId === collection.id && (
                                                    <svg
                                                        xmlns="http://www.w3.org/2000/svg"
                                                        viewBox="0 0 24 24"
                                                        fill="currentColor"
                                                        className="w-5 h-5 flex-shrink-0"
                                                    >
                                                        <path
                                                            fillRule="evenodd"
                                                            d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12zm13.36-1.814a.75.75 0 10-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 00-1.06 1.06l2.25 2.25a.75.75 0 001.14-.094l3.75-5.25z"
                                                            clipRule="evenodd"
                                                        />
                                                    </svg>
                                                )}
                                            </button>
                                        ))}
                                    </div>

                                    {/* Action Buttons */}
                                    <div className="flex gap-3 pt-4">
                                        <Button
                                            type="modal-secondary"
                                            onClick={handleClose}
                                            className="flex-1"
                                        >
                                            Cancel
                                        </Button>
                                        <Button
                                            type="modal-primary"
                                            onClick={handleAddToCollection}
                                            disabled={!selectedCollectionId || adding}
                                            className="flex-1"
                                        >
                                            {adding ? "Adding..." : "Add to Collection"}
                                        </Button>
                                    </div>
                                </>
                            )}
                        </>
                    )}
                </div>
            )}
        </Modal>
    );
}
