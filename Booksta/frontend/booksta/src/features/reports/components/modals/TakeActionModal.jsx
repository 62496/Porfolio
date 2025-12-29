import Modal from "../../../../components/common/Modal";
import Button from "../../../../components/common/Button";
import ProgressSteps from "../../../../components/forms/ProgressSteps";
import GenericInput from "../../../../components/forms/GenericInput";
import AuthorSelector from "../../../../features/books/components/AuthorSelector";
import GenreSelector from "../../../../features/books/components/GenreSelector";

export default function TakeActionModal({
    isOpen,
    selectedReport,
    actionType,
    currentStep,
    warningMessage,
    editedBookData,
    editedAuthorData,
    bookImagePreview,
    authorAvatarPreview,
    validationErrors,
    authors,
    subjects,
    onClose,
    onActionTypeChange,
    onWarningMessageChange,
    onEditedBookDataChange,
    onEditedAuthorDataChange,
    onBookImageChange,
    onBookImagePreviewChange,
    onAuthorAvatarChange,
    onAuthorAvatarPreviewChange,
    onNextStep,
    onBackStep,
    onSubmit,
}) {
    if (!isOpen || !selectedReport) return null;

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title="Take Action"
        >
            <div className="bg-[#f5f5f7] rounded-xl p-4 mb-4">
                <p className="text-[13px] text-[#6e6e73] mb-1">Report #{selectedReport.id}</p>
                <h3 className="text-[20px] font-semibold mb-2">{selectedReport.subject}</h3>

                {selectedReport.book && (
                    <p className="text-[15px] text-[#6e6e73]">
                        Book: {selectedReport.book.title}
                    </p>
                )}

                {selectedReport.author && (
                    <p className="text-[15px] text-[#6e6e73]">
                        Author: {selectedReport.author.firstName} {selectedReport.author.lastName}
                    </p>
                )}
            </div>

            {/* Progress Steps */}
            <div className="mb-6">
                <ProgressSteps
                    currentStep={currentStep}
                    steps={['Select Action', 'Details']}
                />
            </div>

            {/* Step 0: Select Action */}
            {currentStep === 0 && (
                <>
                    <label className="block mb-3 text-[15px] font-medium">Select Action</label>
                    <div className="space-y-3 mb-6">
                        <label className="flex items-center gap-3 p-4 border border-[#e5e5e7] rounded-xl cursor-pointer hover:border-[#1d1d1f] transition-colors">
                            <input
                                type="radio"
                                name="actionType"
                                value="warn_user"
                                checked={actionType === "warn_user"}
                                onChange={(e) => onActionTypeChange(e.target.value)}
                                className="w-4 h-4"
                            />
                            <div>
                                <p className="font-medium">Warn Author</p>
                                <p className="text-[13px] text-[#6e6e73]">
                                    Send a warning to the content owner about policy violations
                                </p>
                            </div>
                        </label>

                        <label className="flex items-center gap-3 p-4 border border-[#e5e5e7] rounded-xl cursor-pointer hover:border-[#1d1d1f] transition-colors">
                            <input
                                type="radio"
                                name="actionType"
                                value="edit_content"
                                checked={actionType === "edit_content"}
                                onChange={(e) => onActionTypeChange(e.target.value)}
                                className="w-4 h-4"
                            />
                            <div>
                                <p className="font-medium">Edit Content</p>
                                <p className="text-[13px] text-[#6e6e73]">
                                    Manually edit the content to fix reported issues
                                </p>
                            </div>
                        </label>
                    </div>

                    <div className="flex gap-3">
                        <Button
                            onClick={onNextStep}
                            disabled={!actionType}
                            type="modal-primary"
                            label="Next"
                            className="flex-1"
                        />
                        <Button
                            onClick={onClose}
                            type="modal-secondary"
                            label="Cancel"
                            className="flex-1"
                        />
                    </div>
                </>
            )}

            {/* Step 1: Details based on action type */}
            {currentStep === 1 && (
                <>
                    {/* Warn User - Message Form */}
                    {actionType === "warn_user" && (
                        <div className="mb-6">
                            <h3 className="text-[18px] font-semibold mb-4">Warning Message</h3>
                            <p className="text-[13px] text-[#6e6e73] mb-3">
                                This message will be sent to {selectedReport.user.email}
                            </p>
                            <GenericInput
                                type="textarea"
                                value={warningMessage}
                                onChange={(e) => onWarningMessageChange(e.target.value)}
                                placeholder="Enter your warning message here..."
                                rows={6}
                                required
                                error={validationErrors.warningMessage}
                            />
                        </div>
                    )}

                    {/* Edit Content - Book Form */}
                    {actionType === "edit_content" && editedBookData && selectedReport.book && (
                        <div className="mb-6">
                            <h3 className="text-[18px] font-semibold mb-4">Edit Book Content</h3>
                            <div className="space-y-4">
                                <div className="grid grid-cols-2 gap-4">
                                    <GenericInput
                                        label="ISBN"
                                        type="text"
                                        value={editedBookData.isbn}
                                        onChange={(e) => onEditedBookDataChange({ ...editedBookData, isbn: e.target.value })}
                                        readOnly
                                        error={validationErrors.isbn}
                                    />
                                    <GenericInput
                                        label="Publishing Year"
                                        type="number"
                                        value={editedBookData.publishingYear}
                                        onChange={(e) => onEditedBookDataChange({ ...editedBookData, publishingYear: e.target.value })}
                                        min={1000}
                                        max={new Date().getFullYear() + 1}
                                        error={validationErrors.publishingYear}
                                    />
                                </div>

                                <div className="grid grid-cols-2 gap-4">
                                    <GenericInput
                                        label="Pages"
                                        type="number"
                                        value={editedBookData.pages}
                                        onChange={(e) => onEditedBookDataChange({ ...editedBookData, pages: e.target.value })}
                                        min={1}
                                        max={20000}
                                        error={validationErrors.pages}
                                    />
                                    <GenericInput
                                        label="Book Title"
                                        type="text"
                                        value={editedBookData.title}
                                        onChange={(e) => onEditedBookDataChange({ ...editedBookData, title: e.target.value })}
                                        error={validationErrors.title}
                                    />
                                </div>

                                <GenericInput
                                    label="Description"
                                    type="textarea"
                                    value={editedBookData.description}
                                    onChange={(e) => onEditedBookDataChange({ ...editedBookData, description: e.target.value })}
                                    rows={6}
                                    maxLength={2000}
                                    showCharCount
                                    error={validationErrors.description}
                                />

                                <GenericInput
                                    label="Book Cover Image"
                                    type="file"
                                    name="bookImage"
                                    onChange={(e) => onBookImageChange(e.target.files?.[0])}
                                    onFilePreview={(dataUrl) => onBookImagePreviewChange(dataUrl)}
                                    preview={bookImagePreview}
                                    accept="image/*"
                                />

                                <div>
                                    <label className="block text-[13px] font-medium mb-2">Select Authors</label>
                                    <AuthorSelector
                                        selected={editedBookData.authors}
                                        options={authors}
                                        onToggle={(authorId) => {
                                            const updated = editedBookData.authors.includes(authorId)
                                                ? editedBookData.authors.filter(a => a !== authorId)
                                                : [...editedBookData.authors, authorId];
                                            onEditedBookDataChange({ ...editedBookData, authors: updated });
                                        }}
                                    />
                                    {validationErrors.authors && (
                                        <div style={{ color: '#cc0000', fontSize: '13px', marginTop: '6px' }}>
                                            {validationErrors.authors}
                                        </div>
                                    )}
                                </div>

                                <div>
                                    <label className="block text-[13px] font-medium mb-2">Select Genres</label>
                                    <GenreSelector
                                        selected={editedBookData.genres}
                                        options={subjects}
                                        onToggle={(genreId) => {
                                            const updated = editedBookData.genres.includes(genreId)
                                                ? editedBookData.genres.filter(g => g !== genreId)
                                                : [...editedBookData.genres, genreId];
                                            onEditedBookDataChange({ ...editedBookData, genres: updated });
                                        }}
                                    />
                                    {validationErrors.genres && (
                                        <div style={{ color: '#cc0000', fontSize: '13px', marginTop: '6px' }}>
                                            {validationErrors.genres}
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Edit Content - Author Form */}
                    {actionType === "edit_content" && editedAuthorData && selectedReport.author && (
                        <div className="mb-6">
                            <h3 className="text-[18px] font-semibold mb-4">Edit Author Content</h3>
                            <div className="space-y-4">
                                <GenericInput
                                    label="First Name"
                                    type="text"
                                    value={editedAuthorData.firstName}
                                    onChange={(e) => onEditedAuthorDataChange({ ...editedAuthorData, firstName: e.target.value })}
                                    placeholder="Enter first name"
                                    required
                                    error={validationErrors.firstName}
                                />
                                <GenericInput
                                    label="Last Name"
                                    type="text"
                                    value={editedAuthorData.lastName}
                                    onChange={(e) => onEditedAuthorDataChange({ ...editedAuthorData, lastName: e.target.value })}
                                    placeholder="Enter last name"
                                    required
                                    error={validationErrors.lastName}
                                />
                                <GenericInput
                                    label="Author Avatar"
                                    type="file"
                                    name="authorAvatar"
                                    onChange={(e) => onAuthorAvatarChange(e.target.files?.[0])}
                                    onFilePreview={(dataUrl) => onAuthorAvatarPreviewChange(dataUrl)}
                                    preview={authorAvatarPreview}
                                    accept="image/*"
                                />
                            </div>
                        </div>
                    )}

                    <div className="flex gap-3">
                        <Button
                            onClick={onBackStep}
                            type="modal-secondary"
                            label="Back"
                            className="flex-1"
                        />
                        <Button
                            onClick={onSubmit}
                            type="modal-danger"
                            label="Confirm Action"
                            className="flex-1"
                        />
                    </div>
                </>
            )}
        </Modal>
    );
}
