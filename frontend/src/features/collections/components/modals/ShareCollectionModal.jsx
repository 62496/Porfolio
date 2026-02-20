import React from 'react';
import Modal from '../../../../components/common/Modal';
import Button from '../../../../components/common/Button';
import GenericInput from '../../../../components/forms/GenericInput';

const ShareCollectionModal = ({
    isOpen,
    collection,
    searchTerm,
    searchResults,
    loading,
    onSearchChange,
    onShareWithUser,
    onUnshareWithUser,
    onClose,
}) => {
    if (!collection) return null;

    const sharedUsers = collection.sharedWith || [];

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Share Collection">
            <p className="text-sm text-[#6e6e73] -mt-2 mb-4">{collection.name}</p>

            {/* Search Users */}
            <div className="mb-6">
                <div className="relative">
                    <GenericInput
                        label="Add people by name or email"
                        value={searchTerm}
                        onChange={onSearchChange}
                        placeholder="Search users..."
                    />
                    <svg
                        className="absolute right-3 top-[42px] w-5 h-5 text-[#6e6e73]"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                        />
                    </svg>
                </div>

                {/* Search Results */}
                {searchResults.length > 0 && (
                    <div className="mt-2 border border-[#e5e5e7] rounded-xl overflow-hidden">
                        {searchResults.map((user) => {
                            const alreadyShared = sharedUsers.some(u => u.id === user.id);
                            return (
                                <div
                                    key={user.id}
                                    className="flex items-center justify-between px-4 py-3 hover:bg-[#f5f5f7] border-b border-[#e5e5e7] last:border-b-0"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 rounded-full bg-[#0066cc] flex items-center justify-center text-white font-medium">
                                            {user.firstName?.[0] || user.email?.[0]?.toUpperCase()}
                                        </div>
                                        <div>
                                            <p className="font-medium text-[#1d1d1f]">
                                                {user.firstName} {user.lastName}
                                            </p>
                                            <p className="text-xs text-[#6e6e73]">{user.email}</p>
                                        </div>
                                    </div>
                                    {alreadyShared ? (
                                        <span className="text-xs text-[#6e6e73]">Already shared</span>
                                    ) : (
                                        <Button
                                            type="small-secondary"
                                            label={loading ? '...' : 'Add'}
                                            onClick={() => onShareWithUser(user)}
                                            disabled={loading}
                                        />
                                    )}
                                </div>
                            );
                        })}
                    </div>
                )}

                {searchTerm && searchResults.length === 0 && (
                    <p className="mt-2 text-sm text-[#6e6e73] text-center py-4">
                        No users found
                    </p>
                )}
            </div>

            {/* Currently Shared With */}
            <div>
                <h3 className="text-sm font-medium text-[#1d1d1f] mb-3">
                    Shared with ({sharedUsers.length})
                </h3>

                {sharedUsers.length === 0 ? (
                    <div className="text-center py-8 text-[#6e6e73] bg-[#f5f5f7] rounded-xl">
                        <svg
                            className="w-12 h-12 mx-auto mb-2 text-[#86868b]"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                        >
                            <path
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="1.5"
                                d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                            />
                        </svg>
                        <p className="text-sm">Not shared with anyone yet</p>
                    </div>
                ) : (
                    <div className="space-y-2">
                        {sharedUsers.map((user) => (
                            <div
                                key={user.id}
                                className="flex items-center justify-between px-4 py-3 bg-[#f5f5f7] rounded-xl"
                            >
                                <div className="flex items-center gap-3">
                                    <div className="w-10 h-10 rounded-full bg-[#6e6e73] flex items-center justify-center text-white font-medium">
                                        {user.firstName?.[0] || user.email?.[0]?.toUpperCase()}
                                    </div>
                                    <div>
                                        <p className="font-medium text-[#1d1d1f]">
                                            {user.firstName} {user.lastName}
                                        </p>
                                        <p className="text-xs text-[#6e6e73]">{user.email}</p>
                                    </div>
                                </div>
                                <button
                                    onClick={() => onUnshareWithUser(user.id)}
                                    disabled={loading}
                                    className="px-3 py-1.5 text-xs font-medium text-red-500 hover:text-red-700 hover:bg-red-50 rounded-lg transition-colors disabled:opacity-50"
                                >
                                    Remove
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Close Button */}
            <div className="mt-6">
                <Button
                    type="modal-secondary"
                    label="Done"
                    onClick={onClose}
                    className="w-full"
                />
            </div>
        </Modal>
    );
};

export default ShareCollectionModal;
