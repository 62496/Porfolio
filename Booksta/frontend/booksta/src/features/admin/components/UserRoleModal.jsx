import React from 'react';
import Modal from '../../../components/common/Modal';
import Button from '../../../components/common/Button';

const ROLE_COLORS = {
    ADMIN: 'bg-red-100 text-red-700 border-red-200',
    SELLER: 'bg-green-100 text-green-700 border-green-200',
    AUTHOR: 'bg-purple-100 text-purple-700 border-purple-200',
    LIBRARIAN: 'bg-blue-100 text-blue-700 border-blue-200',
    USER: 'bg-gray-100 text-gray-700 border-gray-200',
};

const ROLE_DESCRIPTIONS = {
    USER: 'Basic user access - can browse books and use the platform',
    SELLER: 'Can list books for sale in the marketplace',
    AUTHOR: 'Author profile with ability to manage their books and series',
    LIBRARIAN: 'Can manage books, authors, and library content',
    ADMIN: 'Full administrative access to all features',
};

export default function UserRoleModal({
    isOpen,
    onClose,
    user,
    allRoles,
    onAddRole,
    onRemoveRole,
    updating,
}) {
    if (!user) return null;

    const userRoleNames = user.roleNames || [];

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Manage User Roles">
            <div className="space-y-6">
                {/* User Info */}
                <div className="flex items-center gap-4 pb-4 border-b border-[#e5e5e7]">
                    {user.picture ? (
                        <img
                            src={user.picture}
                            alt={user.fullName}
                            className="w-16 h-16 rounded-full object-cover border-2 border-white shadow-md"
                        />
                    ) : (
                        <div className="w-16 h-16 rounded-full bg-[#f5f5f7] flex items-center justify-center">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-8 h-8 text-[#6e6e73]">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                            </svg>
                        </div>
                    )}
                    <div>
                        <h3 className="text-[18px] font-semibold text-[#1d1d1f]">
                            {user.fullName}
                        </h3>
                        <p className="text-[14px] text-[#6e6e73]">
                            {user.email}
                        </p>
                    </div>
                </div>

                {/* Current Roles */}
                <div>
                    <h4 className="text-[14px] font-semibold text-[#1d1d1f] mb-3">
                        Current Roles
                    </h4>
                    <div className="flex flex-wrap gap-2">
                        {userRoleNames.length > 0 ? (
                            userRoleNames.map(role => (
                                <span
                                    key={role}
                                    className={`px-3 py-1.5 text-[13px] font-medium rounded-full border ${ROLE_COLORS[role] || ROLE_COLORS.USER}`}
                                >
                                    {role}
                                </span>
                            ))
                        ) : (
                            <span className="text-[14px] text-[#6e6e73]">No roles assigned</span>
                        )}
                    </div>
                </div>

                {/* Available Roles */}
                <div>
                    <h4 className="text-[14px] font-semibold text-[#1d1d1f] mb-3">
                        Manage Roles
                    </h4>
                    <div className="space-y-3">
                        {allRoles.map(role => {
                            const hasRole = userRoleNames.includes(role.name);
                            return (
                                <div
                                    key={role.id}
                                    className={`p-4 rounded-[12px] border transition-all ${
                                        hasRole
                                            ? 'bg-[#f5f5f7] border-[#e5e5e7]'
                                            : 'bg-white border-[#e5e5e7] hover:border-[#1d1d1f]'
                                    }`}
                                >
                                    <div className="flex items-center justify-between">
                                        <div className="flex-1">
                                            <div className="flex items-center gap-2">
                                                <span className={`px-2 py-0.5 text-[11px] font-bold rounded ${ROLE_COLORS[role.name] || ROLE_COLORS.USER}`}>
                                                    {role.name}
                                                </span>
                                                {hasRole && (
                                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 text-green-600">
                                                        <path fillRule="evenodd" d="M16.704 4.153a.75.75 0 01.143 1.052l-8 10.5a.75.75 0 01-1.127.075l-4.5-4.5a.75.75 0 011.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 011.05-.143z" clipRule="evenodd" />
                                                    </svg>
                                                )}
                                            </div>
                                            <p className="text-[13px] text-[#6e6e73] mt-1">
                                                {ROLE_DESCRIPTIONS[role.name]}
                                            </p>
                                        </div>
                                        <Button
                                            type={hasRole ? 'small-danger' : 'small-success'}
                                            onClick={() => hasRole
                                                ? onRemoveRole(user.id, role.name)
                                                : onAddRole(user.id, role.name)
                                            }
                                            disabled={updating}
                                        >
                                            {updating ? '...' : hasRole ? 'Remove' : 'Add'}
                                        </Button>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </Modal>
    );
}
