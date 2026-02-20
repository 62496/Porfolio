import React from "react";
import { useNavigate } from "react-router-dom";

export default function BookThumb({ book }) {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/book/${book.isbn}`);
    };

    return (
        <div
            className="w-[120px] text-center cursor-pointer hover:opacity-80 transition-opacity"
            onClick={handleClick}
        >
            <img
                src={book.cover || book.image?.url}
                alt={book.title}
                className="w-[120px] h-[160px] object-cover rounded-[8px] mb-2"
            />
            <div className="text-[13px] text-[#1d1d1f] line-clamp-2">{book.title}</div>
        </div>
    );
}
