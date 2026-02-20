import Button from "../../../components/common/Button";
import { formatDuration } from "../utils/formatters";

export default function SessionTab({
    currentStatus,
    canFinishOrAbandon,
    canRestart,
    isSessionActive,
    isPaused,
    sessionDuration,
    onStartSession,
    onPauseSession,
    onResumeSession,
    onStopSession,
    onFinishBook,
    onAbandonBook,
    onRestartBook,
}) {
    const isBookFinishedOrAbandoned = currentStatus === 'FINISHED_READING' || currentStatus === 'ABANDONED_READING';

    return (
        <div className="space-y-6">
            {/* Hide session tracking if book is finished or abandoned */}
            {!isBookFinishedOrAbandoned && (
                <div className="bg-[#fafaf9] rounded-[16px] p-6 border border-[#e7e5e4]">
                    <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-4">Reading Progress</h3>

                    {!isSessionActive ? (
                        <div className="text-center py-6">
                            <div className="mb-5">
                                <svg className="w-12 h-12 mx-auto text-[#d1d1d6] mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                                <p className="text-[13px] text-[#78716c] mb-1 font-medium">No time tracked yet</p>
                                <p className="text-[12px] text-[#86868b]">Start a session to track your reading time</p>
                            </div>
                            <Button
                                type="primary"
                                label="Start Reading Session"
                                onClick={onStartSession}
                                className="!px-8"
                            />
                        </div>
                    ) : (
                        <div>
                            {/* Timer Display */}
                            <div className="text-center mb-6 py-6 bg-white rounded-[12px] border border-[#e7e5e4]">
                                <div className="flex items-center justify-center gap-2 mb-3">
                                    <svg className={`w-5 h-5 ${isPaused ? 'text-orange-500' : 'text-blue-500'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    <p className="text-[11px] font-medium text-[#78716c] uppercase tracking-wider">
                                        {isPaused ? 'Paused' : 'Active'}
                                    </p>
                                </div>
                                <div className="text-[36px] font-bold text-[#1d1d1f] tracking-tight mb-1">
                                    {formatDuration(sessionDuration, true)}
                                </div>
                                <p className="text-[11px] text-[#86868b]">
                                    {isPaused ? 'Resume when ready' : 'Time spent reading'}
                                </p>
                            </div>

                            {/* Session Controls */}
                            <div className="flex gap-3 justify-center">
                                {!isPaused ? (
                                    <button
                                        onClick={onPauseSession}
                                        className="flex items-center gap-2 px-6 py-3 bg-orange-50 text-orange-700 rounded-xl text-[15px] font-medium hover:bg-orange-100 transition-colors border border-orange-200"
                                    >
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 9v6m4-6v6m7-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                        </svg>
                                        Pause
                                    </button>
                                ) : (
                                    <button
                                        onClick={onResumeSession}
                                        className="flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl text-[15px] font-medium hover:bg-blue-700 transition-colors"
                                    >
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                                        </svg>
                                        Resume
                                    </button>
                                )}
                                <button
                                    onClick={onStopSession}
                                    className="flex items-center gap-2 px-6 py-3 bg-green-600 text-white rounded-xl text-[15px] font-medium hover:bg-green-700 transition-colors"
                                >
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    Finish Session
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            )}

            {/* Book Progress Actions */}
            {(canFinishOrAbandon || canRestart) && (
                <div className="bg-[#fafaf9] rounded-[16px] p-6 border border-[#e7e5e4]">
                    <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-4">Book Progress</h3>
                    <p className="text-[14px] text-[#6e6e73] mb-4">
                        {canFinishOrAbandon
                            ? 'Update your reading status when you finish or stop reading this book'
                            : 'Restart this book to begin tracking again'
                        }
                    </p>
                    <div className="flex gap-3 flex-wrap">
                        {canFinishOrAbandon && (
                            <>
                                <button
                                    onClick={onFinishBook}
                                    className="flex items-center gap-2 px-5 py-2.5 bg-green-50 text-green-700 rounded-lg text-[14px] font-medium hover:bg-green-100 transition-colors border border-green-200"
                                >
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    Mark as Finished
                                </button>
                                <button
                                    onClick={onAbandonBook}
                                    className="flex items-center gap-2 px-5 py-2.5 bg-red-50 text-red-700 rounded-lg text-[14px] font-medium hover:bg-red-100 transition-colors border border-red-200"
                                >
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                    Stop Reading
                                </button>
                            </>
                        )}
                        {canRestart && (
                            <button
                                onClick={onRestartBook}
                                className="flex items-center gap-2 px-5 py-2.5 bg-purple-50 text-purple-700 rounded-lg text-[14px] font-medium hover:bg-purple-100 transition-colors border border-purple-200"
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                </svg>
                                Reset Progress
                            </button>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}
