import { formatEventType, formatDate } from "../utils/formatters";

export default function HabitsTab({
    eventsLoading,
    readingEvents,
}) {
    if (eventsLoading) {
        return (
            <div className="text-center py-12">
                <div className="w-12 h-12 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                <p className="text-[17px] text-[#6e6e73]">Loading reading habits...</p>
            </div>
        );
    }

    if (readingEvents.length === 0) {
        return (
            <div className="text-center py-12">
                <p className="text-[21px] font-medium text-[#292524] mb-2">No reading data yet</p>
                <p className="text-[15px] text-[#78716c]">
                    Start reading to build your reading habits
                </p>
            </div>
        );
    }

    const timesStarted = readingEvents.filter(
        e => e.readingEvent === 'STARTED_READING' || e.readingEvent === 'RESTARTED_READING'
    ).length;

    const getEventColor = (eventType) => {
        switch (eventType) {
            case 'STARTED_READING': return 'bg-blue-500';
            case 'RESTARTED_READING': return 'bg-purple-500';
            case 'FINISHED_READING': return 'bg-green-500';
            default: return 'bg-red-500';
        }
    };

    const getBarColor = (eventType) => {
        switch (eventType) {
            case 'STARTED_READING': return 'bg-blue-500';
            case 'RESTARTED_READING': return 'bg-purple-500';
            case 'FINISHED_READING': return 'bg-green-500';
            default: return 'bg-red-500';
        }
    };

    return (
        <div className="space-y-6">
            {/* Reading Timeline */}
            <div className="bg-[#fafaf9] rounded-[16px] p-6 border border-[#e7e5e4]">
                <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-4">Reading Timeline</h3>
                <div className="space-y-3 max-h-[300px] overflow-y-auto pr-2">
                    {readingEvents.map((event, index) => (
                        <div key={index} className="flex items-start gap-4 pb-3 border-b border-[#e7e5e4] last:border-0">
                            <div className={`w-2 h-2 rounded-full mt-2 flex-shrink-0 ${getEventColor(event.readingEvent)}`} />
                            <div className="flex-1 min-w-0">
                                <div className="flex items-baseline justify-between gap-2 mb-1">
                                    <span className="text-[15px] font-medium text-[#1d1d1f]">
                                        {formatEventType(event.readingEvent)}
                                    </span>
                                    <span className="text-[13px] text-[#86868b] flex-shrink-0">
                                        {formatDate(event.occurredAt)}
                                    </span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            {/* Reading Stats */}
            <div className="grid grid-cols-2 gap-4">
                <div className="bg-blue-50 rounded-[16px] p-6 border border-blue-100">
                    <div className="text-[13px] text-blue-700 font-medium mb-1">Total Events</div>
                    <div className="text-[32px] font-semibold text-blue-900">{readingEvents.length}</div>
                </div>
                <div className="bg-green-50 rounded-[16px] p-6 border border-green-100">
                    <div className="text-[13px] text-green-700 font-medium mb-1">Times Started</div>
                    <div className="text-[32px] font-semibold text-green-900">{timesStarted}</div>
                </div>
            </div>

            {/* Event Breakdown */}
            <div className="bg-[#fafaf9] rounded-[16px] p-6 border border-[#e7e5e4]">
                <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-4">Event Breakdown</h3>
                <div className="space-y-2">
                    {['STARTED_READING', 'RESTARTED_READING', 'FINISHED_READING', 'ABANDONED_READING'].map(eventType => {
                        const count = readingEvents.filter(e => e.readingEvent === eventType).length;
                        const percentage = readingEvents.length > 0 ? (count / readingEvents.length * 100).toFixed(0) : 0;

                        if (count === 0) return null;

                        return (
                            <div key={eventType} className="flex items-center gap-3">
                                <div className="flex-1">
                                    <div className="flex justify-between mb-1">
                                        <span className="text-[14px] text-[#1d1d1f]">{formatEventType(eventType)}</span>
                                        <span className="text-[14px] text-[#6e6e73]">{count} ({percentage}%)</span>
                                    </div>
                                    <div className="h-2 bg-[#e7e5e4] rounded-full overflow-hidden">
                                        <div
                                            className={`h-full ${getBarColor(eventType)}`}
                                            style={{ width: `${percentage}%` }}
                                        />
                                    </div>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </div>
    );
}
