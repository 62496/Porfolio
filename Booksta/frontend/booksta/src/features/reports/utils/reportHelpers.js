export const getReportType = (report) => {
    if (report.book) return "BOOK";
    if (report.author) return "AUTHOR";
    return "UNKNOWN";
};

export const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
};

export const getReportStatusBadge = (status) => {
    switch (status) {
        case "PENDING":
            return {
                className: "bg-[#fef3c7] text-[#92400e]",
                label: "Pending"
            };
        case "RESOLVED":
            return {
                className: "bg-green-100 text-green-800",
                label: "Resolved"
            };
        case "DISMISSED":
            return {
                className: "bg-gray-100 text-gray-800",
                label: "Dismissed"
            };
        default:
            return {
                className: "bg-gray-100 text-gray-800",
                label: status
            };
    }
};

export const getReportTypeBadge = (type) => {
    switch (type) {
        case "BOOK":
            return {
                className: "bg-blue-100 text-blue-800",
                label: "BOOK REPORT"
            };
        case "AUTHOR":
            return {
                className: "bg-purple-100 text-purple-800",
                label: "AUTHOR REPORT"
            };
        default:
            return {
                className: "bg-gray-100 text-gray-800",
                label: "UNKNOWN REPORT"
            };
    }
};

export const filterReports = (reports, filter, statusFilter, searchTerm) => {
    return (reports || []).filter((report) => {
        const reportType = getReportType(report);
        const matchesFilter = filter === "ALL" || reportType === filter;

        const matchesStatus = statusFilter === "ALL" ||
            (statusFilter === "PENDING" && report.reportStatus === "PENDING") ||
            (statusFilter === "RESOLVED" && report.reportStatus === "RESOLVED") ||
            (statusFilter === "DISMISSED" && report.reportStatus === "DISMISSED");

        const searchLower = searchTerm.toLowerCase();
        const matchesSearch = searchTerm === "" ||
            report.subject.toLowerCase().includes(searchLower) ||
            report.messageReport.toLowerCase().includes(searchLower) ||
            report.user.firstName.toLowerCase().includes(searchLower) ||
            report.user.lastName.toLowerCase().includes(searchLower) ||
            (report.book && report.book.title.toLowerCase().includes(searchLower)) ||
            (report.author && `${report.author.firstName} ${report.author.lastName}`.toLowerCase().includes(searchLower));

        return matchesFilter && matchesStatus && matchesSearch;
    });
};

export const getReportStats = (reports) => {
    const total = (reports || []).length;
    const bookReports = (reports || []).filter(r => getReportType(r) === "BOOK").length;
    const authorReports = (reports || []).filter(r => getReportType(r) === "AUTHOR").length;
    const pending = (reports || []).filter(r => r.reportStatus === "PENDING").length;
    const resolved = (reports || []).filter(r => r.reportStatus === "RESOLVED").length;
    const dismissed = (reports || []).filter(r => r.reportStatus === "DISMISSED").length;

    return { total, bookReports, authorReports, pending, resolved, dismissed };
};
