import { useState, useEffect, useCallback } from "react";
import authService from "../../../api/services/authService";

export function useAuth() {
    const [user, setUser] = useState(() => authService.getCurrentUser());
    const [loading, setLoading] = useState(true);

    // Refresh user data from localStorage
    const refreshUser = useCallback(() => {
        setUser(authService.getCurrentUser());
    }, []);

    // Check if user is authenticated
    const isAuthenticated = useCallback(() => {
        return authService.isAuthenticated();
    }, []);

    // Check if user has a specific role
    const hasRole = useCallback((roleName) => {
        if (!user || !user.roles) return false;
        return user.roles.some(role => role.name === roleName);
    }, [user]);

    // Check if user has any of the specified roles
    const hasAnyRole = useCallback((roleNames) => {
        if (!user || !user.roles) return false;
        return roleNames.some(roleName =>
            user.roles.some(role => role.name === roleName)
        );
    }, [user]);

    // Check if user has all specified roles
    const hasAllRoles = useCallback((roleNames) => {
        if (!user || !user.roles) return false;
        return roleNames.every(roleName =>
            user.roles.some(role => role.name === roleName)
        );
    }, [user]);

    // Logout function - now async to handle server-side logout
    const logout = useCallback(async () => {
        await authService.logout();
        setUser(null);
    }, []);

    // On mount, verify token and refresh user
    useEffect(() => {
        const initAuth = async () => {
            setLoading(true);

            // Check if we have a refresh token (indicates potential session)
            if (authService.isAuthenticated()) {
                // This will refresh the access token if needed and fetch user
                const freshUser = await authService.fetchCurrentUserFromApi();
                if (freshUser) {
                    setUser(freshUser);
                } else {
                    // Failed to restore session, clear user
                    setUser(null);
                }
            }

            setLoading(false);
        };

        initAuth();
    }, []);

    // Listen for storage changes (login/logout in other tabs)
    useEffect(() => {
        const handleStorageChange = (e) => {
            if (e.key === 'user' || e.key === 'refreshToken') {
                refreshUser();
            }
        };

        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, [refreshUser]);

    // Subscribe to token changes from authService
    useEffect(() => {
        const unsubscribe = authService.onTokenChange((token) => {
            if (!token) {
                // Token was cleared, update user state
                setUser(null);
            }
        });

        return unsubscribe;
    }, []);

    return {
        user,
        loading,
        isAuthenticated,
        hasRole,
        hasAnyRole,
        hasAllRoles,
        logout,
        refreshUser,
    };
}

export default useAuth;
