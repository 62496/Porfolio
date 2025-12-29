import React from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../../components/common/Button";

export default function ReadingBookCard({ book, isCurrentlyReading = false }) {
    const navigate = useNavigate();

    return (
        <div
            className="bg-white border border-[#e5e5e7] rounded-[18px] p-6 transition-shadow duration-300 hover:shadow-xl flex flex-col"
        >
            {/* Cover Image */}
            <div
                className="mb-4 overflow-hidden rounded-[12px] h-[280px] cursor-pointer"
                onClick={() => navigate(`/book/${book.isbn}`)}
            >
                <img
                    src={book.imageUrl}
                    alt={book.title}
                    className="w-full h-full object-cover transition-transform duration-300 hover:scale-110"
                />
            </div>

            {/* Book Info */}
            <div className="mb-4">
                <h3
                    className="text-[19px] font-semibold mb-2 line-clamp-2 cursor-pointer hover:text-[#0071e3] transition-colors"
                    onClick={() => navigate(`/book/${book.isbn}`)}
                >
                    {book.title}
                </h3>
                <p className="text-[13px] text-[#86868b]">
                    {book.publishingYear} • {book.pages} pages
                </p>
            </div>

            {/* Latest Reading Event */}
            {book.latestReadingEvent && (
                <div className="mb-4">
                    <p className="text-[13px] text-[#86868b]">
                        {book.latestReadingEvent.eventType === 'STARTED_READING' && 'Started Reading'}
                        {book.latestReadingEvent.eventType === 'RESTARTED_READING' && 'Restarted Reading'}
                        {book.latestReadingEvent.eventType === 'FINISHED_READING' && 'Finished Reading'}
                        {book.latestReadingEvent.eventType === 'ABANDONED_READING' && 'Abandoned Reading'}
                        {book.latestReadingEvent.occurredAt && (
                            <span> • {new Date(book.latestReadingEvent.occurredAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}</span>
                        )}
                    </p>
                </div>
            )}

            {/* Action Buttons - Always at bottom */}
            <div className="mt-auto">
                {isCurrentlyReading ? (
                    <Button
                        type="primary"
                        label="Continue Reading"
                        onClick={() => navigate(`/reading-session/${book.isbn}`)}
                        className="w-full !text-[14px] !py-2"
                    />
                ) : (
                    <Button
                        type="small-secondary"
                        label="View Details"
                        onClick={() => navigate(`/reading-session/${book.isbn}`)}
                        className="w-full"
                    />
                )}
            </div>
        </div>
    );
}
