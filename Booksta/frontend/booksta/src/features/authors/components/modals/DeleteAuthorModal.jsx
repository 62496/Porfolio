import React from 'react';
import Modal from '../../../../components/common/Modal';
import Button from '../../../../components/common/Button';

const DeleteAuthorModal = ({
    isOpen,
    author,
    loading,
    onConfirm,
    onClose,
}) => {
    if (!author) return null;

    const fullName = `${author.firstName} ${author.lastName}`;

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Delete Author">
            <div className="text-center py-4">
                <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-red-100 flex items-center justify-center">
                    <svg
                        className="w-8 h-8 text-red-500"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                    >
                        <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth="2"
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                    </svg>
                </div>

                <h3 className="text-lg font-semibold text-[#1d1d1f] mb-2">
                    Delete "{fullName}"?
                </h3>

                <div className="text-left bg-red-50 border border-red-200 rounded-xl p-4 mb-4">
                    <p className="text-red-600 text-sm font-semibold mb-2">
                        Danger: This is a destructive action
                    </p>
                    <ul className="text-[#6e6e73] text-sm space-y-1 list-disc list-inside">
                        <li><strong className="text-red-600">ALL books</strong> by this author will be permanently deleted</li>
                        <li><strong className="text-red-600">ALL series</strong> by this author will be deleted</li>
                        <li>Author will be removed from all user followers</li>
                        <li>Author profile image will be deleted</li>
                    </ul>
                </div>

                <p className="text-[#6e6e73] text-sm mb-6">
                    The user account associated with this author will not be deleted.
                </p>

                <div className="flex gap-3">
                    <Button
                        type="modal-secondary"
                        label="Cancel"
                        onClick={onClose}
                        className="flex-1"
                    />
                    <Button
                        type="modal-danger"
                        label={loading ? 'Deleting...' : 'Delete Author'}
                        onClick={onConfirm}
                        disabled={loading}
                        className="flex-1"
                    />
                </div>
            </div>
        </Modal>
    );
};

export default DeleteAuthorModal;
