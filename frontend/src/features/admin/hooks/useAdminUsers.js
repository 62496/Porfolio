import { useState, useEffect, useCallback, useMemo } from 'react';
import adminService from '../../../api/services/adminService';
import useToast from '../../../hooks/useToast';

export function useAdminUsers() {
    const [users, setUsers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filter states
    const [search, setSearch] = useState('');
    const [roleFilter, setRoleFilter] = useState('ALL');

    // Selected user for role management
    const [selectedUser, setSelectedUser] = useState(null);
    const [updatingRole, setUpdatingRole] = useState(false);

    const { toast, showToast, hideToast } = useToast();

    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);

            const [usersData, rolesData] = await Promise.all([
                adminService.getAllUsers(),
                adminService.getAllRoles(),
            ]);

            const formattedUsers = usersData.map(user =>
                adminService.formatUserForDisplay(user)
            );

            setUsers(formattedUsers);
            setRoles(rolesData);
        } catch (err) {
            console.error('Error loading admin data:', err);
            setError('Failed to load user data. Make sure you have admin permissions.');
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const addRole = useCallback(async (userId, roleName) => {
        try {
            setUpdatingRole(true);
            const updatedUser = await adminService.addRoleToUser(userId, roleName);
            const formattedUser = adminService.formatUserForDisplay(updatedUser);

            setUsers(prev => prev.map(u =>
                u.id === userId ? formattedUser : u
            ));

            if (selectedUser?.id === userId) {
                setSelectedUser(formattedUser);
            }

            showToast(`Added ${roleName} role successfully`, 'success');
        } catch (err) {
            console.error('Error adding role:', err);
            showToast('Failed to add role', 'error');
        } finally {
            setUpdatingRole(false);
        }
    }, [selectedUser, showToast]);

    const removeRole = useCallback(async (userId, roleName) => {
        try {
            setUpdatingRole(true);
            const updatedUser = await adminService.removeRoleFromUser(userId, roleName);
            const formattedUser = adminService.formatUserForDisplay(updatedUser);

            setUsers(prev => prev.map(u =>
                u.id === userId ? formattedUser : u
            ));

            if (selectedUser?.id === userId) {
                setSelectedUser(formattedUser);
            }

            showToast(`Removed ${roleName} role successfully`, 'success');
        } catch (err) {
            console.error('Error removing role:', err);
            showToast('Failed to remove role', 'error');
        } finally {
            setUpdatingRole(false);
        }
    }, [selectedUser, showToast]);

    const selectUser = useCallback((user) => {
        setSelectedUser(user);
    }, []);

    const closeUserDetail = useCallback(() => {
        setSelectedUser(null);
    }, []);

    // Filtered users
    const filteredUsers = useMemo(() => {
        let result = users;

        // Search filter
        if (search) {
            const searchLower = search.toLowerCase();
            result = result.filter(user =>
                user.fullName?.toLowerCase().includes(searchLower) ||
                user.email?.toLowerCase().includes(searchLower)
            );
        }

        // Role filter
        if (roleFilter !== 'ALL') {
            result = result.filter(user =>
                user.roleNames.includes(roleFilter)
            );
        }

        return result;
    }, [users, search, roleFilter]);

    // Stats
    const stats = useMemo(() => {
        const totalUsers = users.length;
        const adminCount = users.filter(u => u.roleNames.includes('ADMIN')).length;
        const sellerCount = users.filter(u => u.roleNames.includes('SELLER')).length;
        return { totalUsers, adminCount, sellerCount };
    }, [users]);

    const clearFilters = useCallback(() => {
        setSearch('');
        setRoleFilter('ALL');
    }, []);

    const hasFilters = search || roleFilter !== 'ALL';

    return {
        // State
        users: filteredUsers,
        totalUsers: users.length,
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
        refresh: loadData,

        // Toast
        toast,
        hideToast,
    };
}

export default useAdminUsers;
