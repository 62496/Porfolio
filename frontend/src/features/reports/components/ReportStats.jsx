import { getReportType } from "../utils/reportHelpers";

export default function ReportStats({ reports }) {
    const total = (reports || []).length;
    const bookReports = (reports || []).filter(r => getReportType(r) === "BOOK").length;
    const authorReports = (reports || []).filter(r => getReportType(r) === "AUTHOR").length;

    return (
        <div className="mt-16 grid grid-cols-3 gap-6">
            <div className="bg-[#f5f5f7] rounded-[18px] p-6 text-center">
                <div className="text-[48px] font-semibold mb-2">{total}</div>
                <div className="text-[17px] text-[#6e6e73]">Total Reports</div>
            </div>
            <div className="bg-[#f5f5f7] rounded-[18px] p-6 text-center">
                <div className="text-[48px] font-semibold mb-2">{bookReports}</div>
                <div className="text-[17px] text-[#6e6e73]">Book Reports</div>
            </div>
            <div className="bg-[#f5f5f7] rounded-[18px] p-6 text-center">
                <div className="text-[48px] font-semibold mb-2">{authorReports}</div>
                <div className="text-[17px] text-[#6e6e73]">Author Reports</div>
            </div>
        </div>
    );
}
