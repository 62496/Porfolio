import React from "react";
import ReadingBookCard from "./ReadingBookCard";

export default function CollapsibleBookSection({
    title,
    books,
    statusColor,
    isExpanded,
    onToggle
}) {
    if (books.length === 0) return null;

    return (
        <div className="mb-8">
            <button
                onClick={onToggle}
                className="w-full flex items-center justify-between mb-6 group"
            >
                <h2 className="text-[28px] font-semibold flex items-center gap-3">
                    {title}
                    <span className={`text-[16px] px-3 py-1 rounded-full ${statusColor}`}>
                        {books.length}
                    </span>
                </h2>
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    className={`w-6 h-6 text-[#6e6e73] group-hover:text-[#1d1d1f] transition-all duration-300 ${isExpanded ? 'rotate-180' : ''}`}
                >
                    <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                </svg>
            </button>

            <div
                className={`grid transition-all duration-300 ease-in-out ${isExpanded
                    ? 'grid-rows-[1fr] opacity-100'
                    : 'grid-rows-[0fr] opacity-0'
                }`}
            >
                <div className="overflow-hidden">
                    <div className="grid gap-6 grid-cols-[repeat(auto-fill,minmax(240px,1fr))] pb-2">
                        {books.map((book) => (
                            <ReadingBookCard
                                key={book.isbn}
                                book={book}
                                isCurrentlyReading={false}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}
