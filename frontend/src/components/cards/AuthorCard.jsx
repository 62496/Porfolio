import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import FollowButton from '../../features/social/components/FollowButton';
import userService from '../../api/services/userService';

export default function AuthorCard({ author }) {
  const navigate = useNavigate();
  const [isFollowing, setIsFollowing] = useState(false);

  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const res = await userService.isFollowingAuthor(author.id);
        if (mounted) setIsFollowing(Boolean(res));
      } catch (e) {
        console.warn('Could not get follow status', e);
      }
    })();
    return () => { mounted = false; };
  }, [author.id]);

  const handleCardClick = (e) => {
    // Prevent navigation when clicking the follow button
    if (e.target.closest('button')) return;
    navigate(`/authors/${author.id}`);
  };

  return (
    <div
      className="p-4 border rounded flex items-center justify-between cursor-pointer hover:bg-[#f5f5f7] transition-colors"
      onClick={handleCardClick}
    >
      <div className="flex items-center gap-3">
        {author.picture && (
          <img src={author.picture} alt={`${author.firstName} ${author.lastName}`} className="w-12 h-12 rounded-full object-cover" />
        )}
        <div>
          <div className="text-lg font-semibold">{author.firstName} {author.lastName}</div>
        </div>
      </div>

      <FollowButton
        type="author"
        id={author.id}
        initiallyFollowing={isFollowing}
        onChange={(val) => setIsFollowing(val)}
      />
    </div>
  );
}