import { useState, useEffect } from "react";
import useReports from "./useReports";
import { useAuthors } from "../../books/hooks/useAuthors";
import { useSubjects } from "../../books/hooks/useSubjects";
import useToast from "../../../hooks/useToast";
import { warnAuthorSchema, editBookReportSchema, editAuthorReportSchema } from "../validations/reportSchema";
import { filterReports } from "../utils/reportHelpers";

export function useReportsPage() {
    // Visibility for animations
    const [isVisible, setIsVisible] = useState(false);

    // Filter state
    const [filter, setFilter] = useState("ALL");
    const [statusFilter, setStatusFilter] = useState("ALL");
    const [searchTerm, setSearchTerm] = useState("");

    // Selected report for action
    const [selectedReport, setSelectedReport] = useState(null);

    // Action modal state
    const [showActionModal, setShowActionModal] = useState(false);
    const [actionType, setActionType] = useState("");
    const [currentStep, setCurrentStep] = useState(0);
    const [warningMessage, setWarningMessage] = useState("");

    // Edit data state
    const [editedBookData, setEditedBookData] = useState(null);
    const [editedAuthorData, setEditedAuthorData] = useState(null);
    const [authorAvatar, setAuthorAvatar] = useState(null);
    const [authorAvatarPreview, setAuthorAvatarPreview] = useState(null);
    const [bookImage, setBookImage] = useState(null);
    const [bookImagePreview, setBookImagePreview] = useState(null);

    // Confirmation modals state
    const [showResolveConfirmModal, setShowResolveConfirmModal] = useState(false);
    const [showDismissConfirmModal, setShowDismissConfirmModal] = useState(false);
    const [reportToResolve, setReportToResolve] = useState(null);
    const [reportToDismiss, setReportToDismiss] = useState(null);

    // Validation errors
    const [validationErrors, setValidationErrors] = useState({});

    // Hooks
    const { reports, getAllReports, resolveBookReport, dismissBookReport } = useReports();
    const { authors, fetchAuthors } = useAuthors(false);
    const { subjects, fetchSubjects } = useSubjects(false);
    const { toast, showToast, hideToast } = useToast();

    // Load data on mount
    useEffect(() => {
        getAllReports();
        fetchAuthors();
        fetchSubjects();
    }, []);

    // Trigger visibility animation
    useEffect(() => setIsVisible(true), []);

    // Computed filtered reports
    const filteredReports = filterReports(reports, filter, statusFilter, searchTerm);

    // Reset action modal state
    const resetActionModalState = () => {
        setShowActionModal(false);
        setSelectedReport(null);
        setActionType("");
        setCurrentStep(0);
        setWarningMessage("");
        setEditedBookData(null);
        setEditedAuthorData(null);
        setAuthorAvatar(null);
        setAuthorAvatarPreview(null);
        setBookImage(null);
        setBookImagePreview(null);
        setValidationErrors({});
    };

    // Handle take action
    const handleTakeAction = (report) => {
        setSelectedReport(report);
        setShowActionModal(true);
        setActionType("");
        setCurrentStep(0);
        setWarningMessage("");

        // Pre-fill book data for edit content action
        if (report.book) {
            setEditedBookData({
                isbn: report.book.isbn,
                title: report.book.title,
                publishingYear: report.book.publishingYear || "",
                description: report.book.description || "",
                pages: report.book.pages || "",
                authors: report.book.authors?.map(a => a.id) || [],
                genres: report.book.subjects?.map(s => s.id) || []
            });
            // Set book image preview if available
            if (report.book.image?.url) {
                setBookImagePreview(report.book.image.url);
            }
        }

        // Pre-fill author data for edit content action
        if (report.author) {
            setEditedAuthorData({
                firstName: report.author.firstName || "",
                lastName: report.author.lastName || ""
            });
        }
    };

    // Handle dismiss
    const handleDismiss = (reportId) => {
        setReportToDismiss(reportId);
        setShowDismissConfirmModal(true);
    };

    const confirmDismiss = async () => {
        try {
            await dismissBookReport(reportToDismiss);
            await getAllReports();
            showToast("Report dismissed successfully!", "success");
            setShowDismissConfirmModal(false);
            setReportToDismiss(null);
        } catch {
            showToast("Failed to dismiss report. Please try again.", "error");
        }
    };

    const cancelDismiss = () => {
        setShowDismissConfirmModal(false);
        setReportToDismiss(null);
    };

    // Handle mark resolved
    const handleMarkResolved = (reportId) => {
        setReportToResolve(reportId);
        setShowResolveConfirmModal(true);
    };

    const confirmMarkResolved = async () => {
        try {
            const formData = new FormData();
            const data = { action: "" };
            formData.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));

            await resolveBookReport(reportToResolve, formData);
            await getAllReports();
            showToast("Report marked as resolved!", "success");
            setShowResolveConfirmModal(false);
            setReportToResolve(null);
        } catch {
            showToast("Failed to mark report as resolved. Please try again.", "error");
        }
    };

    const cancelMarkResolved = () => {
        setShowResolveConfirmModal(false);
        setReportToResolve(null);
    };

    // Step navigation
    const handleNextStep = () => {
        if (!actionType) {
            showToast("Please select an action type", "warning");
            return;
        }
        setCurrentStep(1);
    };

    const handleBackStep = () => {
        setCurrentStep(0);
    };

    // Submit action
    const submitAction = async () => {
        // Clear previous validation errors
        setValidationErrors({});

        try {
            // Validate based on action type
            if (actionType === "warn_user") {
                try {
                    await warnAuthorSchema.validate({ warningMessage }, { abortEarly: false });
                } catch (validationError) {
                    const errors = {};
                    validationError.inner.forEach((err) => {
                        errors[err.path] = err.message;
                    });
                    setValidationErrors(errors);
                    return;
                }
            } else if (actionType === "edit_content" && editedBookData) {
                try {
                    await editBookReportSchema.validate(editedBookData, { abortEarly: false });
                } catch (validationError) {
                    const errors = {};
                    validationError.inner.forEach((err) => {
                        errors[err.path] = err.message;
                    });
                    setValidationErrors(errors);
                    return;
                }
            } else if (actionType === "edit_content" && editedAuthorData) {
                try {
                    await editAuthorReportSchema.validate(editedAuthorData, { abortEarly: false });
                } catch (validationError) {
                    const errors = {};
                    validationError.inner.forEach((err) => {
                        errors[err.path] = err.message;
                    });
                    setValidationErrors(errors);
                    return;
                }
            }

            if (actionType === "warn_user" && warningMessage) {
                const formData = new FormData();
                const data = {
                    action: "WARN_AUTHOR",
                    isbn: selectedReport.book?.isbn
                };
                formData.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));

                await resolveBookReport(selectedReport.id, formData);
                showToast(`Warning sent to ${selectedReport.user.email}`, "success");
            } else if (actionType === "edit_content" && editedBookData) {
                // Build data object with only changed fields
                const data = {
                    action: "EDIT_BOOK",
                    isbn: editedBookData.isbn
                };

                const originalBook = selectedReport.book;

                // Only include changed fields
                if (editedBookData.publishingYear !== originalBook.publishingYear) {
                    data.publishingYear = editedBookData.publishingYear;
                }

                if (editedBookData.pages !== originalBook.pages) {
                    data.pages = editedBookData.pages;
                }

                if (editedBookData.title !== originalBook.title) {
                    data.bookTitle = editedBookData.title;
                }

                if (editedBookData.description !== originalBook.description) {
                    data.description = editedBookData.description;
                }

                // Compare authors array
                const originalAuthorIds = originalBook.authors?.map(a => a.id) || [];
                const hasAuthorsChanged = JSON.stringify(originalAuthorIds.sort()) !== JSON.stringify(editedBookData.authors.sort());
                if (hasAuthorsChanged) {
                    data.authors = editedBookData.authors;
                }

                // Compare subjects/genres array
                const originalSubjectIds = originalBook.subjects?.map(s => s.id) || [];
                const hasSubjectsChanged = JSON.stringify(originalSubjectIds.sort()) !== JSON.stringify(editedBookData.genres.sort());
                if (hasSubjectsChanged) {
                    data.subjects = editedBookData.genres;
                }

                const formData = new FormData();
                formData.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));

                // Only include image if a new one was uploaded
                if (bookImage) {
                    formData.append("image", bookImage);
                }

                await resolveBookReport(selectedReport.id, formData);
                showToast("Book content updated successfully!", "success");
            } else if (actionType === "edit_content" && editedAuthorData) {
                showToast("Author content updated successfully!", "success");
            }

            await getAllReports();
            resetActionModalState();
        } catch (error) {
            // Check if it's actually successful despite the error
            if (error?.response?.status === 403) {
                // Refresh to see if changes were applied
                await getAllReports();
                showToast("Action completed but received permission error. Please check if changes were applied.", "warning");
            } else {
                showToast("Failed to complete action. Please try again.", "error");
            }
        }
    };

    return {
        // State
        isVisible,
        filter,
        statusFilter,
        searchTerm,
        selectedReport,
        showActionModal,
        actionType,
        currentStep,
        warningMessage,
        editedBookData,
        editedAuthorData,
        authorAvatar,
        authorAvatarPreview,
        bookImage,
        bookImagePreview,
        showResolveConfirmModal,
        showDismissConfirmModal,
        validationErrors,

        // Data
        reports,
        filteredReports,
        authors,
        subjects,
        toast,

        // Setters
        setFilter,
        setStatusFilter,
        setSearchTerm,
        setActionType,
        setWarningMessage,
        setEditedBookData,
        setEditedAuthorData,
        setAuthorAvatar,
        setAuthorAvatarPreview,
        setBookImage,
        setBookImagePreview,

        // Handlers
        handleTakeAction,
        handleDismiss,
        confirmDismiss,
        cancelDismiss,
        handleMarkResolved,
        confirmMarkResolved,
        cancelMarkResolved,
        handleNextStep,
        handleBackStep,
        submitAction,
        resetActionModalState,
        hideToast,
    };
}
