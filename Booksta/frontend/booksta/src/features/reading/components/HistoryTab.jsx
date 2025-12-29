import SessionCard from "./SessionCard";

export default function HistoryTab({
    historyLoading,
    readingHistory,
    onEditSession,
    onDeleteSession,
}) {
    if (historyLoading) {
        return (
            <div className="text-center py-12">
                <div className="w-12 h-12 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                <p className="text-[17px] text-[#6e6e73]">Loading history...</p>
            </div>
        );
    }

    if (readingHistory.length === 0) {
        return (
            <div className="text-center py-12">
                <p className="text-[21px] font-medium text-[#292524] mb-2">No reading sessions yet</p>
                <p className="text-[15px] text-[#78716c]">
                    Start reading to see your history here
                </p>
            </div>
        );
    }

    const completedCount = readingHistory.filter(s => s.status === 'COMPLETED_SESSION').length;
    const totalPages = readingHistory.reduce((sum, s) => sum + (s.endPage && s.startPage ? s.endPage - s.startPage : 0), 0);

    return (
        <div className="space-y-6">
            {/* Session Stats Summary */}
            <div className="grid grid-cols-3 gap-4">
                <div className="bg-blue-50 rounded-[16px] p-6 border border-blue-100">
                    <div className="text-[13px] text-blue-700 font-medium mb-1">Total Sessions</div>
                    <div className="text-[32px] font-semibold text-blue-900">{readingHistory.length}</div>
                </div>
                <div className="bg-green-50 rounded-[16px] p-6 border border-green-100">
                    <div className="text-[13px] text-green-700 font-medium mb-1">Completed</div>
                    <div className="text-[32px] font-semibold text-green-900">{completedCount}</div>
                </div>
                <div className="bg-purple-50 rounded-[16px] p-6 border border-purple-100">
                    <div className="text-[13px] text-purple-700 font-medium mb-1">Total Pages</div>
                    <div className="text-[32px] font-semibold text-purple-900">{totalPages}</div>
                </div>
            </div>

            {/* Session List */}
            <div className="bg-[#fafaf9] rounded-[16px] p-6 border border-[#e7e5e4]">
                <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-4">Session History</h3>
                <div className="space-y-3 max-h-[500px] overflow-y-auto pr-2">
                    {readingHistory.map((session) => (
                        <SessionCard
                            key={session.id}
                            session={session}
                            onEdit={onEditSession}
                            onDelete={onDeleteSession}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}
