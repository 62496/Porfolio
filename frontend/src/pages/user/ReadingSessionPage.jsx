import { useParams, useNavigate } from "react-router-dom";
import Button from "../../components/common/Button";
import Toast from "../../components/common/Toast";
import { useReadingSession } from "../../features/reading/hooks/useReadingSession";
import { formatEventType } from "../../features/reading/utils/formatters";
import SessionTab from "../../features/reading/components/SessionTab";
import HistoryTab from "../../features/reading/components/HistoryTab";
import HabitsTab from "../../features/reading/components/HabitsTab";
import StartSessionModal from "../../features/reading/components/modals/StartSessionModal";
import EndSessionModal from "../../features/reading/components/modals/EndSessionModal";
import EditSessionModal from "../../features/reading/components/modals/EditSessionModal";
import DeleteSessionModal from "../../features/reading/components/modals/DeleteSessionModal";

export default function ReadingSessionPage() {
    const { isbn } = useParams();
    const navigate = useNavigate();

    const {
        // State
        book,
        loading,
        toast,
        hideToast,
        activeTab,
        setActiveTab,
        currentStatus,
        canFinishOrAbandon,
        canRestart,

        // Session state
        isSessionActive,
        isPaused,
        sessionDuration,

        // History & Events
        readingHistory,
        historyLoading,
        readingEvents,
        eventsLoading,

        // Modal state
        showStartModal,
        showCommentModal,
        showEditModal,
        showDeleteModal,

        // Forms
        startForm,
        endForm,
        editForm,

        // Session handlers
        handleStartSession,
        handleConfirmStartSession,
        handlePauseSession,
        handleResumeSession,
        handleStopSession,
        handleDeleteSession,
        handleSaveSessionComment,

        // History handlers
        handleEditSession,
        handleUpdateSession,
        handleDeleteHistorySession,
        handleConfirmDelete,

        // Book progress handlers
        handleFinishBook,
        handleAbandonBook,
        handleRestartBook,

        // Modal cancel handlers
        handleCancelStartModal,
        handleCancelCommentModal,
        handleCancelEditModal,
        handleCancelDeleteModal,
    } = useReadingSession(isbn);

    // Loading state
    if (loading) {
        return (
            <div className="min-h-screen bg-[#f5f5f7] flex items-center justify-center">
                <div className="text-center">
                    <div className="w-16 h-16 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                    <p className="text-[17px] text-[#6e6e73]">Loading...</p>
                </div>
            </div>
        );
    }

    // Book not found state
    if (!book) {
        return (
            <div className="min-h-screen bg-[#f5f5f7] flex items-center justify-center">
                <div className="text-center max-w-md mx-auto px-6">
                    <h2 className="text-[28px] font-semibold mb-4">Book Not Found</h2>
                    <p className="text-[17px] text-[#6e6e73] mb-8">
                        The book you're looking for doesn't exist or has been removed.
                    </p>
                    <Button
                        label="Back"
                        type="primary"
                        onClick={() => navigate(-1)}
                    />
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-[#f5f5f7] py-12">
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={hideToast}
                />
            )}

            <div className="max-w-6xl mx-auto px-6">
                {/* Back Button */}
                <div className="mb-8">
                    <button
                        onClick={() => navigate(-1)}
                        className="w-12 h-12 rounded-full bg-white border-2 border-[#e5e5e7] text-[#6e6e73] hover:border-[#1d1d1f] hover:text-[#1d1d1f] hover:scale-110 transition-all duration-300 ease-in-out flex items-center justify-center shadow-sm"
                        aria-label="Go back"
                    >
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            className="w-6 h-6"
                        >
                            <path strokeLinecap="round" strokeLinejoin="round" d="M15 19l-7-7 7-7" />
                        </svg>
                    </button>
                </div>

                {/* Main Content Card */}
                <div className="bg-white rounded-[24px] border border-[#e5e5e7] overflow-hidden shadow-sm">
                    <div className="grid md:grid-cols-2 gap-8 p-8 md:p-12">
                        {/* Left Column - Book Cover */}
                        <div className="flex flex-col items-center md:items-start">
                            <div className="w-full max-w-md shadow-lg mb-6 aspect-[3/4] rounded-[16px] bg-[#f5f5f7] flex items-center justify-center">
                                {book.image?.url ? (
                                    <img
                                        src={book.image.url}
                                        alt={book.title}
                                        className="w-full h-full object-cover rounded-[16px]"
                                        onError={(e) => {
                                            e.target.style.display = 'none';
                                            e.target.parentElement.innerHTML = `<div class="text-center p-8"><svg class="w-24 h-24 mx-auto text-[#6e6e73] mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25"></path></svg><p class="text-[17px] text-[#6e6e73]">Cover not available</p></div>`;
                                        }}
                                    />
                                ) : (
                                    <div className="text-center p-8">
                                        <svg className="w-24 h-24 mx-auto text-[#6e6e73] mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25"></path>
                                        </svg>
                                        <p className="text-[17px] text-[#6e6e73]">Cover not available</p>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Right Column - Book Information */}
                        <div className="flex flex-col gap-6">
                            {/* Title */}
                            <div>
                                <h1 className="text-[40px] md:text-[48px] font-bold leading-tight mb-2">
                                    {book.title}
                                </h1>
                                <div className="flex items-center gap-4 mb-4 text-[15px] text-[#6e6e73]">
                                    <div className="flex items-center gap-1.5">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
                                            <path d="M5.25 12a.75.75 0 01.75-.75h.01a.75.75 0 01.75.75v.01a.75.75 0 01-.75.75H6a.75.75 0 01-.75-.75V12zM6 13.25a.75.75 0 00-.75.75v.01c0 .414.336.75.75.75h.01a.75.75 0 00.75-.75V14a.75.75 0 00-.75-.75H6zM7.25 12a.75.75 0 01.75-.75h.01a.75.75 0 01.75.75v.01a.75.75 0 01-.75.75H8a.75.75 0 01-.75-.75V12zM8 13.25a.75.75 0 00-.75.75v.01c0 .414.336.75.75.75h.01a.75.75 0 00.75-.75V14a.75.75 0 00-.75-.75H8zM9.25 10a.75.75 0 01.75-.75h.01a.75.75 0 01.75.75v.01a.75.75 0 01-.75.75H10a.75.75 0 01-.75-.75V10zM10 11.25a.75.75 0 00-.75.75v.01c0 .414.336.75.75.75h.01a.75.75 0 00.75-.75V12a.75.75 0 00-.75-.75H10zM9.25 14a.75.75 0 01.75-.75h.01a.75.75 0 01.75.75v.01a.75.75 0 01-.75.75H10a.75.75 0 01-.75-.75V14zM12 9.25a.75.75 0 00-.75.75v.01c0 .414.336.75.75.75h.01a.75.75 0 00.75-.75V10a.75.75 0 00-.75-.75H12zM11.25 12a.75.75 0 01.75-.75h.01a.75.75 0 01.75.75v.01a.75.75 0 01-.75.75H12a.75.75 0 01-.75-.75V12zM12 13.25a.75.75 0 00-.75.75v.01c0 .414.336.75.75.75h.01a.75.75 0 00.75-.75V14a.75.75 0 00-.75-.75H12zM13.25 10a.75.75 0 01.75-.75h.01a.75.75 0 01.75.75v.01a.75.75 0 01-.75.75H14a.75.75 0 01-.75-.75V10zM14 11.25a.75.75 0 00-.75.75v.01c0 .414.336.75.75.75h.01a.75.75 0 00.75-.75V12a.75.75 0 00-.75-.75H14z" />
                                            <path fillRule="evenodd" d="M5.75 2a.75.75 0 01.75.75V4h7V2.75a.75.75 0 011.5 0V4h.25A2.75 2.75 0 0118 6.75v8.5A2.75 2.75 0 0115.25 18H4.75A2.75 2.75 0 012 15.25v-8.5A2.75 2.75 0 014.75 4H5V2.75A.75.75 0 015.75 2zm-1 5.5c-.69 0-1.25.56-1.25 1.25v6.5c0 .69.56 1.25 1.25 1.25h10.5c.69 0 1.25-.56 1.25-1.25v-6.5c0-.69-.56-1.25-1.25-1.25H4.75z" clipRule="evenodd" />
                                        </svg>
                                        <span className="font-medium">{book.publishingYear}</span>
                                    </div>
                                    <div className="flex items-center gap-1.5">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4">
                                            <path d="M10.75 16.82A7.462 7.462 0 0115 15.5c.71 0 1.396.098 2.046.282A.75.75 0 0018 15.06v-11a.75.75 0 00-.546-.721A9.006 9.006 0 0015 3a8.963 8.963 0 00-4.25 1.065V16.82zM9.25 4.065A8.963 8.963 0 005 3c-.85 0-1.673.118-2.454.339A.75.75 0 002 4.06v11a.75.75 0 00.954.721A7.506 7.506 0 015 15.5c1.579 0 3.042.487 4.25 1.32V4.065z" />
                                        </svg>
                                        <span className="font-medium">{book.pages} pages</span>
                                    </div>
                                </div>
                                {currentStatus && (
                                    <div className={`inline-block px-4 py-2 rounded-full text-[15px] font-medium ${currentStatus === 'FINISHED_READING'
                                        ? 'bg-green-100 text-green-800'
                                        : currentStatus === 'ABANDONED_READING'
                                            ? 'bg-red-100 text-red-800'
                                            : 'bg-blue-100 text-blue-800'
                                        }`}>
                                        {formatEventType(currentStatus)}
                                    </div>
                                )}
                            </div>

                            {/* Tabs */}
                            <div className="border-b border-[#e5e5e7]">
                                <div className="flex gap-8">
                                    {["session", "history", "habits"].map((tab) => (
                                        <button
                                            key={tab}
                                            onClick={() => setActiveTab(tab)}
                                            className={`pb-3 text-[17px] font-medium border-b-2 transition-colors ${activeTab === tab
                                                ? "border-[#0071e3] text-[#0071e3]"
                                                : "border-transparent text-[#6e6e73] hover:text-[#1d1d1f]"
                                                }`}
                                        >
                                            {tab === "session" ? "Reading Session" : tab === "history" ? "History" : "Reading Habits"}
                                        </button>
                                    ))}
                                </div>
                            </div>

                            {/* Tab Content */}
                            <div className="min-h-[400px]">
                                {activeTab === "session" && (
                                    <SessionTab
                                        currentStatus={currentStatus}
                                        canFinishOrAbandon={canFinishOrAbandon}
                                        canRestart={canRestart}
                                        isSessionActive={isSessionActive}
                                        isPaused={isPaused}
                                        sessionDuration={sessionDuration}
                                        onStartSession={handleStartSession}
                                        onPauseSession={handlePauseSession}
                                        onResumeSession={handleResumeSession}
                                        onStopSession={handleStopSession}
                                        onFinishBook={handleFinishBook}
                                        onAbandonBook={handleAbandonBook}
                                        onRestartBook={handleRestartBook}
                                    />
                                )}

                                {activeTab === "history" && (
                                    <HistoryTab
                                        historyLoading={historyLoading}
                                        readingHistory={readingHistory}
                                        onEditSession={handleEditSession}
                                        onDeleteSession={handleDeleteHistorySession}
                                    />
                                )}

                                {activeTab === "habits" && (
                                    <HabitsTab
                                        eventsLoading={eventsLoading}
                                        readingEvents={readingEvents}
                                    />
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Modals */}
            <StartSessionModal
                isOpen={showStartModal}
                form={startForm}
                onConfirm={handleConfirmStartSession}
                onCancel={handleCancelStartModal}
            />

            <EndSessionModal
                isOpen={showCommentModal}
                form={endForm}
                book={book}
                sessionDuration={sessionDuration}
                onSave={handleSaveSessionComment}
                onCancel={handleCancelCommentModal}
                onDelete={handleDeleteSession}
            />

            <EditSessionModal
                isOpen={showEditModal}
                form={editForm}
                book={book}
                onUpdate={handleUpdateSession}
                onCancel={handleCancelEditModal}
            />

            <DeleteSessionModal
                isOpen={showDeleteModal}
                onConfirm={handleConfirmDelete}
                onCancel={handleCancelDeleteModal}
            />
        </div>
    );
}
