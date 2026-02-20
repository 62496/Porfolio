import React, { useState } from "react";
import ReactDOM from "react-dom";
import Modal from "../../../components/common/Modal";
import Button from "../../../components/common/Button";
import GenericInput from "../../../components/forms/GenericInput";
import useReports from "../../reports/hooks/useReports";

export default function ReportBookModal({ isOpen, onClose, book }) {
    const { createBookReport } = useReports();
    const [currentStep, setCurrentStep] = useState(0);
    const [selectedReason, setSelectedReason] = useState("");
    const [customReason, setCustomReason] = useState("");
    const [description, setDescription] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const reportReasons = [
        "Missing or incomplete content",
        "Incorrect or inaccurate information",
        "Formatting or readability issues",
        "Wrong or mismatched book content",
        "Copyright or inappropriate content",
        "Other",
    ];

    const handleReasonSelect = (reason) => {
        setSelectedReason(reason);
    };

    const handleNext = () => {
        if (selectedReason === "Other" && !customReason.trim()) {
            return; // Don't proceed if "Other" is selected but no custom reason provided
        }
        if (selectedReason) {
            setCurrentStep(1);
        }
    };

    const handleBack = () => {
        setCurrentStep(0);
    };

    const handleSubmitReport = async () => {
        if (!description.trim()) return;

        setIsSubmitting(true);
        const data = {
            subject: selectedReason === "Other" ? customReason : selectedReason,
            messageReport: description.trim(),
            book: {
                isbn: book.isbn
            }
        };

        try {
            await createBookReport(book.isbn, data);
            // Reset and close
            setCurrentStep(0);
            setSelectedReason("");
            setCustomReason("");
            setDescription("");
            onClose();
        } catch {
            // Error handled silently
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleClose = () => {
        if (!isSubmitting) {
            setCurrentStep(0);
            setSelectedReason("");
            setCustomReason("");
            setDescription("");
            onClose();
        }
    };

    if (!isOpen) return null;

    return ReactDOM.createPortal(
        <Modal isOpen={isOpen} onClose={handleClose} title="Report Book Issue">
            {/* Step 1: Select Reason */}
            {currentStep === 0 && (
                <div className="fade-in visible">
                    <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-2 tracking-[-0.015em]">
                        What's wrong with this book?
                    </h3>
                    <p className="text-[15px] text-[#6e6e73] mb-6">
                        Select the issue you're experiencing with "{book?.title}"
                    </p>

                    <div className="space-y-3 mb-6">
                        {reportReasons.map((reason, index) => (
                            <button
                                key={index}
                                type="button"
                                onClick={() => handleReasonSelect(reason)}
                                className={`
                                    w-full text-left px-4 py-3 rounded-[10px] border-2
                                    transition-all duration-200
                                    ${selectedReason === reason
                                        ? "border-[#1d1d1f] bg-[#f5f5f7] shadow-sm"
                                        : "border-[#e5e5e7] hover:border-[#d1d1d6] hover:bg-[#fafafa]"
                                    }
                                `}
                            >
                                <div className="flex items-center justify-between gap-2">
                                    <span className="text-[15px] text-[#1d1d1f]">{reason}</span>
                                    {selectedReason === reason && (
                                        <svg
                                            xmlns="http://www.w3.org/2000/svg"
                                            viewBox="0 0 24 24"
                                            fill="currentColor"
                                            className="w-5 h-5 text-[#1d1d1f] flex-shrink-0"
                                        >
                                            <path
                                                fillRule="evenodd"
                                                d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12zm13.36-1.814a.75.75 0 10-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 00-1.06 1.06l2.25 2.25a.75.75 0 001.14-.094l3.75-5.25z"
                                                clipRule="evenodd"
                                            />
                                        </svg>
                                    )}
                                </div>
                            </button>
                        ))}

                        {/* Show custom reason input when "Other" is selected */}
                        {selectedReason === "Other" && (
                            <div className="mt-3 fade-in visible">
                                <GenericInput
                                    label="Please specify the issue"
                                    name="customReason"
                                    value={customReason}
                                    onChange={(e) => setCustomReason(e.target.value)}
                                    placeholder="Describe the issue..."
                                    required={true}
                                />
                            </div>
                        )}
                    </div>

                    <div className="flex justify-end gap-3 pt-4 border-t border-[#e5e5e7]">
                        <Button label="Cancel" type="secondary" onClick={handleClose} />
                        <Button
                            label="Next"
                            type="primary"
                            onClick={handleNext}
                        />
                    </div>
                </div>
            )}

            {/* Step 2: Add Description */}
            {currentStep === 1 && (
                <div className="fade-in visible">
                    <h3 className="text-[20px] font-semibold text-[#1d1d1f] mb-2 tracking-[-0.015em]">
                        Describe the issue
                    </h3>
                    <p className="text-[15px] text-[#6e6e73] mb-6">
                        Please provide more details about the problem
                    </p>

                    <div className="mb-6">
                        <label className="block mb-4">
                            <span className="text-[15px] font-medium text-[#1d1d1f]">
                                Selected issue
                            </span>
                            <div className="mt-2 px-4 py-3 bg-[#f5f5f7] rounded-[8px] text-[15px] text-[#6e6e73]">
                                {selectedReason === "Other" ? customReason : selectedReason}
                            </div>
                        </label>

                        <label className="block">
                            <span className="text-[15px] font-medium text-[#1d1d1f]">
                                Additional details <span className="text-[#ff3b30]">*</span>
                            </span>
                            <textarea
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                placeholder="Please describe the issue in detail..."
                                rows={6}
                                className="
                                    mt-2 w-full px-4 py-3 rounded-[8px] border border-[#e5e5e7]
                                    text-[15px] text-[#1d1d1f] placeholder-[#86868b]
                                    focus:outline-none focus:border-[#1d1d1f] focus:ring-1 focus:ring-[#1d1d1f]
                                    resize-none transition-colors
                                "
                            />
                            <span className="text-[13px] text-[#6e6e73] mt-1 block">
                                {description.length} characters
                            </span>
                        </label>
                    </div>

                    <div className="flex justify-between gap-3 pt-4 border-t border-[#e5e5e7]">
                        <Button label="Back" type="secondary" onClick={handleBack} />
                        <Button
                            label={isSubmitting ? "Submitting..." : "Report"}
                            type="danger"
                            onClick={handleSubmitReport}
                        />
                    </div>
                </div>
            )}
        </Modal>,
        document.body
    );
}
