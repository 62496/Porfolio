import React, { useState, useEffect } from 'react';
import userService from '../../../api/services/userService';
import { useNavigate } from 'react-router-dom';
import Button from '../../../components/common/Button';
import Toast from '../../../components/common/Toast';

export default function FollowButton({
  type = 'author', // 'author' | 'series'
  id,              // authorId or seriesId
  initiallyFollowing = null, // null means "ask backend"
  onChange
}) {
  const [following, setFollowing] = useState(Boolean(initiallyFollowing));
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);
  const navigate = useNavigate();

  // fetch initial state if initiallyFollowing === null
  useEffect(() => {
    let mounted = true;

    async function check() {
      // if user not logged in, keep false
      const current = userService.getCurrentUser();
      if (!current) {
        if (mounted) setFollowing(false);
        return;
      }

      if (initiallyFollowing !== null) {
        if (mounted) setFollowing(Boolean(initiallyFollowing));
        return;
      }

      try {
        if (type === 'author') {
          const res = await userService.isFollowingAuthor(id);
          if (mounted) setFollowing(Boolean(res));
        } else {
          const res = await userService.isFollowingSeries(id);
          if (mounted) setFollowing(Boolean(res));
        }
      } catch (err) {
        console.warn('Could not load follow status', err);
        if (mounted) setFollowing(false);
      }
    }

    check();
    return () => { mounted = false; };
  }, [id, initiallyFollowing, type]);

  const toggle = async () => {
    const current = userService.getCurrentUser();
    if (!current) {
      // if not logged in redirect to login
      navigate('/login');
      return;
    }

    setLoading(true);
    const prev = following;
    setFollowing(!prev);
    onChange?.(!prev);

    try {
      if (prev) {
        if (type === 'author') await userService.unfollowAuthor(id);
        else await userService.unfollowSeries(id);
      } else {
        if (type === 'author') await userService.followAuthor(id);
        else await userService.followSeries(id);
      }
    } catch (err) {
      console.error('Follow toggle failed', err);
      setFollowing(prev); // revert
      onChange?.(prev);
      setToast({ message: 'Failed to update follow status. Please try again.', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}

      <Button
        label={loading ? 'Loading...' : (following ? 'Following' : 'Follow')}
        type={following ? 'small-secondary' : 'small-success'}
        onClick={toggle}
        disabled={loading}
      />
    </>
  );
}