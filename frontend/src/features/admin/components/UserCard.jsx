import React from 'react';
import Button from '../../../components/common/Button';

const ROLE_COLORS = {
    ADMIN: 'bg-red-100 text-red-700 border-red-200',
    SELLER: 'bg-green-100 text-green-700 border-green-200',
    AUTHOR: 'bg-purple-100 text-purple-700 border-purple-200',
    LIBRARIAN: 'bg-blue-100 text-blue-700 border-blue-200',
    USER: 'bg-gray-100 text-gray-700 border-gray-200',
};

export default function UserCard({ user, onClick }) {
    return (
        <div
            className="bg-white rounded-[16px] border border-[#e5e5e7] p-5 hover:border-[#1d1d1f] transition-all cursor-pointer"
            onClick={() => onClick(user)}
        >
            <div className="flex items-center gap-4">
                {/* Avatar */}
                <div className="flex-shrink-0">
                    {user.picture ? (
                        <img
                            src={user.picture}
                            alt={user.fullName}
                            className="w-12 h-12 rounded-full object-cover border-2 border-white shadow-sm"
                        />
                    ) : (
                        <div className="w-12 h-12 rounded-full bg-[#f5f5f7] flex items-center justify-center">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-6 h-6 text-[#6e6e73]">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 6a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.501 20.118a7.5 7.5 0 0114.998 0A17.933 17.933 0 0112 21.75c-2.676 0-5.216-.584-7.499-1.632z" />
                            </svg>
                        </div>
                    )}
                </div>

                {/* User Info */}
                <div className="flex-1 min-w-0">
                    <h3 className="text-[16px] font-semibold text-[#1d1d1f] truncate">
                        {user.fullName}
                    </h3>
                    <p className="text-[14px] text-[#6e6e73] truncate">
                        {user.email}
                    </p>
                </div>

                {/* Manage Button */}
                <Button
                    type="small-secondary"
                    onClick={(e) => {
                        e.stopPropagation();
                        onClick(user);
                    }}
                >
                    Manage
                </Button>
            </div>

            {/* Roles */}
            <div className="mt-4 flex flex-wrap gap-2">
                {user.roleNames.map(role => (
                    <span
                        key={role}
                        className={`px-2.5 py-1 text-[12px] font-medium rounded-full border ${ROLE_COLORS[role] || ROLE_COLORS.USER}`}
                    >
                        {role}
                    </span>
                ))}
            </div>
        </div>
    );
}
