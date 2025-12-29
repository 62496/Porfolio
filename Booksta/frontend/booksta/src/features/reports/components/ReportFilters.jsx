import Button from "../../../components/common/Button";
import GenericInput from "../../../components/forms/GenericInput";
import { getReportType } from "../utils/reportHelpers";

export default function ReportFilters({
    reports,
    filter,
    statusFilter,
    searchTerm,
    onFilterChange,
    onStatusFilterChange,
    onSearchChange,
}) {
    const handleResetFilters = () => {
        onFilterChange("ALL");
        onStatusFilterChange("ALL");
    };

    return (
        <div className="space-y-4 mb-8">
            <div className="flex flex-wrap gap-3 items-center">
                <GenericInput
                    type="text"
                    placeholder="Search reports..."
                    value={searchTerm}
                    onChange={(e) => onSearchChange(e.target.value)}
                    size="compact"
                    style={{
                        width: '300px',
                        border: '1px solid #e5e5e7',
                        borderRadius: '12px',
                    }}
                />

                <Button
                    onClick={handleResetFilters}
                    type={filter === "ALL" ? "filter-active" : "filter"}
                    label={`All Reports (${(reports || []).length})`}
                />

                <Button
                    onClick={() => {
                        onFilterChange("BOOK");
                        onStatusFilterChange("ALL");
                    }}
                    type={filter === "BOOK" ? "filter-active" : "filter"}
                    label={`Book Reports (${(reports || []).filter(r => getReportType(r) === "BOOK").length})`}
                />

                <Button
                    onClick={() => {
                        onFilterChange("AUTHOR");
                        onStatusFilterChange("ALL");
                    }}
                    type={filter === "AUTHOR" ? "filter-active" : "filter"}
                    label={`Author Reports (${(reports || []).filter(r => getReportType(r) === "AUTHOR").length})`}
                />
            </div>

            {/* Status Filters */}
            {filter !== "ALL" && (
                <div className="flex flex-wrap gap-3 pl-4 border-l-2 border-[#e5e5e7]">
                    <Button
                        onClick={() => onStatusFilterChange("ALL")}
                        type={statusFilter === "ALL" ? "filter-active" : "filter"}
                        label="All Status"
                    />

                    <Button
                        onClick={() => onStatusFilterChange("PENDING")}
                        type={statusFilter === "PENDING" ? "filter-active" : "filter"}
                        label={`Pending (${(reports || []).filter(r => getReportType(r) === filter && r.reportStatus === "PENDING").length})`}
                    />

                    <Button
                        onClick={() => onStatusFilterChange("RESOLVED")}
                        type={statusFilter === "RESOLVED" ? "filter-active" : "filter"}
                        label={`Resolved (${(reports || []).filter(r => getReportType(r) === filter && r.reportStatus === "RESOLVED").length})`}
                    />

                    <Button
                        onClick={() => onStatusFilterChange("DISMISSED")}
                        type={statusFilter === "DISMISSED" ? "filter-active" : "filter"}
                        label={`Dismissed (${(reports || []).filter(r => getReportType(r) === filter && r.reportStatus === "DISMISSED").length})`}
                    />
                </div>
            )}
        </div>
    );
}
