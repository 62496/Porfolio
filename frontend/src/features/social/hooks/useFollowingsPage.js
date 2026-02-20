import { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import userService from "../../../api/services/userService";
import authService from "../../../api/services/authService";
import useFollowings from "./useFollowings";
import useToast from "../../../hooks/useToast";

export function useFollowingsPage() {
    const navigate = useNavigate();
    const { toast, showToast, hideToast } = useToast();
    const [currentUser] = useState(() => userService.getCurrentUser());

    const {
        authors,
        series,
        loading,
        error,
        fetchFollowings,
        unfollowAuthor,
        unfollowSeries
    } = useFollowings();

    useEffect(() => {
        if (!currentUser?.id) {
            navigate('/login');
            return;
        }

        const loadFollowings = async () => {
            try {
                await fetchFollowings(currentUser.id);
            } catch (err) {
                const status = err?.response?.status;
                if (status === 401 || status === 403) {
                    authService.logout();
                    navigate('/login');
                }
            }
        };

        loadFollowings();
    }, [currentUser?.id, navigate, fetchFollowings]);

    const handleUnfollow = useCallback(async (id, type) => {
        if (!currentUser?.id) {
            navigate('/login');
            return;
        }

        try {
            if (type === 'author') {
                await unfollowAuthor(id);
            } else {
                await unfollowSeries(id);
            }
        } catch {
            showToast('Unable to unfollow. Please try again.', 'error');
        }
    }, [currentUser?.id, navigate, unfollowAuthor, unfollowSeries, showToast]);

    return {
        currentUser,
        authors,
        series,
        loading,
        error,
        handleUnfollow,
        toast,
        hideToast,
    };
}
