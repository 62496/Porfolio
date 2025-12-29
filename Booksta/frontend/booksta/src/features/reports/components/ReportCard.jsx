import Button from "../../../components/common/Button";
import { getReportType, formatDate, getReportStatusBadge, getReportTypeBadge } from "../utils/reportHelpers";

export default function ReportCard({
    report,
    index,
    isVisible,
    onTakeAction,
    onDismiss,
    onMarkResolved,
}) {
    const reportType = getReportType(report);
    const typeBadge = getReportTypeBadge(reportType);
    const statusBadge = getReportStatusBadge(report.reportStatus);
    const isPending = report.reportStatus === "PENDING";

    return (
        <div
            className={`
                bg-white border border-[#e5e5e7] rounded-[18px] p-6
                transition-all duration-500 opacity-0 translate-y-4
                ${isVisible ? "opacity-100 translate-y-0" : ""}
            `}
            style={{ transitionDelay: `${index * 100}ms` }}
        >
            <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                        <span className={`px-3 py-1 rounded-full text-[13px] font-medium ${typeBadge.className}`}>
                            {typeBadge.label}
                        </span>
                        <span className="text-[13px] text-[#6e6e73]">
                            Report #{report.id}
                        </span>
                    </div>

                    <h3 className="text-[24px] font-semibold mb-2">
                        {report.subject}
                    </h3>

                    <p className="text-[15px] text-[#6e6e73] mb-3">
                        {report.messageReport}
                    </p>

                    {/* Reported Item */}
                    <div className="bg-[#f5f5f7] rounded-xl p-4 mb-3">
                        {report.book && (
                            <div>
                                <p className="text-[13px] text-[#6e6e73] mb-1">Reported Book:</p>
                                <p className="text-[17px] font-semibold">{report.book.title}</p>
                                <p className="text-[15px] text-[#6e6e73]">
                                    by {report.book.authors.map(a => `${a.firstName} ${a.lastName}`).join(", ")}
                                </p>
                                <p className="text-[13px] text-[#86868b] mt-1">ISBN: {report.book.isbn}</p>
                            </div>
                        )}

                        {report.author && (
                            <div>
                                <p className="text-[13px] text-[#6e6e73] mb-1">Reported Author:</p>
                                <p className="text-[17px] font-semibold">
                                    {report.author.firstName} {report.author.lastName}
                                </p>
                                <p className="text-[13px] text-[#86868b] mt-1">Author ID: {report.author.id}</p>
                            </div>
                        )}
                    </div>

                    {/* Reporter Info */}
                    <div className="flex items-center gap-2 text-[13px] text-[#6e6e73]">
                        <span>Reported by:</span>
                        <span className="font-medium text-[#1d1d1f]">
                            {report.user.firstName} {report.user.lastName}
                        </span>
                        <span>•</span>
                        <span>{report.user.email}</span>
                        <span>•</span>
                        <span>{formatDate(report.createdAt)}</span>
                    </div>
                </div>

                <span className={`px-3 py-1 rounded-full text-[13px] font-medium ${statusBadge.className}`}>
                    {statusBadge.label}
                </span>
            </div>

            {/* Action Buttons - Only show if pending */}
            {isPending && (
                <div className="flex gap-3 pt-4 border-t border-[#e5e5e7]">
                    <Button
                        onClick={() => onDismiss(report.id)}
                        type="small-secondary"
                        label="Dismiss"
                    />
                    <Button
                        onClick={() => onMarkResolved(report.id)}
                        type="small-success"
                        label="Mark as Resolved"
                    />
                    <Button
                        onClick={() => onTakeAction(report)}
                        type="small-danger"
                        label="Take Action"
                    />
                </div>
            )}
        </div>
    );
}
