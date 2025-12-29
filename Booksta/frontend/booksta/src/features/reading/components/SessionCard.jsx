import { formatDuration, formatSessionDate, getSessionStatusInfo, calculatePagesPerMinute } from "../utils/formatters";

export default function SessionCard({ session, onEdit, onDelete }) {
    const statusInfo = getSessionStatusInfo(session.status);
    const isOngoing = session.status === 'ONGOING_SESSION';
    const pagesRead = session.endPage ? session.endPage - session.startPage : null;
    const pagesPerMin = calculatePagesPerMinute(pagesRead, session.durationSeconds);

    return (
        <div
            className={`bg-white rounded-[12px] p-5 border transition-all ${
                isOngoing ? 'border-blue-200 bg-blue-50/30' : 'border-[#e7e5e4] hover:border-[#d2d2d7]'
            }`}
        >
            {/* Header Row */}
            <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-3">
                    <div className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-bold uppercase tracking-wider ${statusInfo.color}`}>
                        <span className="w-1.5 h-1.5 rounded-full bg-current"></span>
                        {statusInfo.label}
                    </div>
                    <span className="text-[13px] text-[#86868b]">
                        {formatSessionDate(session.startedAt)}
                    </span>
                </div>

                {/* Action Buttons */}
                <div className="flex gap-1">
                    {isOngoing && (
                        <button
                            onClick={() => onEdit(session)}
                            className="p-2 hover:bg-blue-100 rounded-lg transition-colors"
                            title="Edit session"
                        >
                            <svg className="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                            </svg>
                        </button>
                    )}
                    <button
                        onClick={() => onDelete(session.id)}
                        className="p-2 hover:bg-red-100 rounded-lg transition-colors"
                        title="Delete session"
                    >
                        <svg className="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                    </button>
                </div>
            </div>

            {/* Content Row */}
            <div className="flex items-center justify-between">
                {/* Pages */}
                <div className="flex items-center gap-3">
                    <span className="text-[24px] font-bold text-[#1d1d1f]">
                        {session.startPage}
                    </span>
                    <svg className="w-5 h-5 text-[#0071e3]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                    </svg>
                    <span className="text-[24px] font-bold text-[#1d1d1f]">
                        {session.endPage || <span className="text-[#c7c7cc] text-[16px] font-normal">—</span>}
                    </span>
                    {pagesRead && (
                        <span className="text-[13px] font-medium text-[#6e6e73] ml-1 px-2 py-0.5 bg-[#f5f5f7] rounded-md">
                            {pagesRead} {pagesRead === 1 ? 'page' : 'pages'}
                        </span>
                    )}
                </div>

                {/* Duration & Speed */}
                <div className="flex items-center gap-3 text-[13px] text-[#6e6e73]">
                    <div className="flex items-center gap-1.5">
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <span className="font-medium">
                            {session.durationSeconds != null
                                ? formatDuration(session.durationSeconds, false)
                                : <span className="italic">Active</span>
                            }
                        </span>
                    </div>
                    {pagesPerMin && (
                        <div className="flex items-center gap-1.5 px-2 py-1 bg-green-50 text-green-700 rounded-md">
                            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                            </svg>
                            <span className="font-semibold">{pagesPerMin} p/min</span>
                        </div>
                    )}
                </div>
            </div>

            {/* Note */}
            {session.note && (
                <div className="mt-3 pt-3 border-t border-[#e7e5e4]">
                    <div className="flex items-start gap-2">
                        <svg className="w-4 h-4 text-[#86868b] mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                        </svg>
                        <p className="text-[14px] text-[#1d1d1f] leading-relaxed flex-1">
                            {session.note}
                        </p>
                    </div>
                </div>
            )}
        </div>
    );
}
