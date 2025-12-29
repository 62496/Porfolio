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

  const imageUrl = author.image?.url || author.picture || author.imageUrl;

  return (
    <div
      className="bg-white rounded-xl border border-[#e5e5e7] overflow-hidden cursor-pointer hover:shadow-md transition-all duration-300 flex items-center gap-4 p-4"
      onClick={handleCardClick}
    >
      {/* Author Photo */}
      <div className="w-16 h-16 rounded-full bg-[#f5f5f7] overflow-hidden flex-shrink-0">
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={`${author.firstName} ${author.lastName}`}
            className="w-full h-full object-cover"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center">
            <svg
              className="w-8 h-8 text-[#c5c5c7]"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth="1.5"
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
              />
            </svg>
          </div>
        )}
      </div>

      {/* Author Info */}
      <div className="flex-1 min-w-0">
        <h3 className="text-[15px] font-semibold text-[#1d1d1f] line-clamp-1 hover:text-[#0066cc] transition-colors">
          {author.firstName} {author.lastName}
        </h3>
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