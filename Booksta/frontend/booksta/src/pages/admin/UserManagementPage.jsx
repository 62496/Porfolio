import React from 'react';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import PageHeader from '../../components/layout/PageHeader';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import EmptyState from '../../components/common/EmptyState';
import Button from '../../components/common/Button';
import Toast from '../../components/common/Toast';
import GenericInput from '../../components/forms/GenericInput';
import UserCard from '../../features/admin/components/UserCard';
import UserRoleModal from '../../features/admin/components/UserRoleModal';
import { useAdminUsers } from '../../features/admin/hooks/useAdminUsers';

export default function UserManagementPage() {
    const {
        // State
        users,
        totalUsers,
        roles,
        loading,
        error,
        stats,

        // Filters
        search,
        roleFilter,
        hasFilters,
        setSearch,
        setRoleFilter,
        clearFilters,

        // Selected user
        selectedUser,
        updatingRole,
        selectUser,
        closeUserDetail,

        // Actions
        addRole,
        removeRole,
        refresh,

        // Toast
        toast,
        hideToast,
    } = useAdminUsers();

    if (loading) {
        return (
            <div className="min-h-screen bg-white font-sans text-[#1d1d1f]">
                <Header />
                <LoadingSpinner message="Loading users..." />
                <Footer />
            </div>
        );
    }

    // Build role filter options
    const roleOptions = [
        { value: 'ALL', label: 'All Roles' },
        ...roles.map(r => ({ value: r.name, label: r.name }))
    ];

    return (
        <div className="font-sans text-[#1d1d1f] bg-white min-h-screen flex flex-col">
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={hideToast}
                />
            )}

            <Header />

            <div className="flex-1">
                <main className="max-w-[1200px] mx-auto py-20 px-[20px]">
                    <PageHeader
                        title="User Management"
                        description="Manage user roles and permissions"
                        action={
                            <Button
                                label="Refresh"
                                type="secondary"
                                onClick={refresh}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0l3.181 3.183a8.25 8.25 0 0013.803-3.7M4.031 9.865a8.25 8.25 0 0113.803-3.7l3.181 3.182m0-4.991v4.99" />
                                </svg>
                                Refresh
                            </Button>
                        }
                    />

                    {/* Stats */}
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
                        <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                            <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Total Users</p>
                            <p className="text-[32px] font-semibold text-[#1d1d1f]">{stats.totalUsers}</p>
                        </div>
                        <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                            <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Admins</p>
                            <p className="text-[32px] font-semibold text-red-600">{stats.adminCount}</p>
                        </div>
                        <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                            <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Sellers</p>
                            <p className="text-[32px] font-semibold text-green-600">{stats.sellerCount}</p>
                        </div>
                    </div>

                    {/* Error Message */}
                    {error && (
                        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl text-red-600 text-sm">
                            {error}
                        </div>
                    )}

                    {/* Filters */}
                    <div className="bg-[#f5f5f7] rounded-[16px] p-5 mb-8">
                        <div className="flex flex-wrap gap-4 items-end">
                            {/* Search */}
                            <div className="flex-1 min-w-[200px]">
                                <GenericInput
                                    name="search"
                                    value={search}
                                    onChange={(e) => setSearch(e.target.value)}
                                    placeholder="Search by name or email..."
                                    size="compact"
                                    style={{ backgroundColor: 'white' }}
                                />
                            </div>

                            {/* Role Filter */}
                            <div className="min-w-[180px]">
                                <GenericInput
                                    type="select"
                                    name="roleFilter"
                                    value={roleFilter}
                                    onChange={(e) => setRoleFilter(e.target.value)}
                                    size="compact"
                                    style={{ backgroundColor: 'white' }}
                                    options={roleOptions}
                                />
                            </div>

                            {/* Clear Filters */}
                            {hasFilters && (
                                <Button
                                    type="text"
                                    onClick={clearFilters}
                                >
                                    Clear Filters
                                </Button>
                            )}
                        </div>
                    </div>

                    {/* Results Count */}
                    <div className="mb-6 text-[14px] text-[#6e6e73]">
                        Showing {users.length} of {totalUsers} users
                        {hasFilters && ' (filtered)'}
                    </div>

                    {/* Users Grid */}
                    {users.length > 0 ? (
                        <div className="grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                            {users.map((user) => (
                                <UserCard
                                    key={user.id}
                                    user={user}
                                    onClick={selectUser}
                                />
                            ))}
                        </div>
                    ) : (
                        <EmptyState
                            title={hasFilters ? 'No users match your filters' : 'No users found'}
                            description={
                                hasFilters
                                    ? 'Try adjusting your search or filters'
                                    : 'No users available'
                            }
                            action={
                                hasFilters && (
                                    <Button
                                        label="Clear Filters"
                                        type="primary"
                                        onClick={clearFilters}
                                    />
                                )
                            }
                        />
                    )}
                </main>
            </div>

            <Footer />

            {/* User Role Modal */}
            <UserRoleModal
                isOpen={!!selectedUser}
                onClose={closeUserDetail}
                user={selectedUser}
                allRoles={roles}
                onAddRole={addRole}
                onRemoveRole={removeRole}
                updating={updatingRole}
            />
        </div>
    );
}
