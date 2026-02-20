import { Controller } from 'react-hook-form';
import Button from "../../../../components/common/Button";
import GenericInput from "../../../../components/forms/GenericInput";
import { formatDuration } from "../../utils/formatters";

export default function EndSessionModal({
    isOpen,
    form,
    book,
    sessionDuration,
    onSave,
    onCancel,
    onDelete,
}) {
    if (!isOpen) return null;

    const endPageValue = parseInt(form.watch('endPage'));
    const showFinishCheckbox = endPageValue === book?.pages;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center" onClick={onCancel}>
            <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" />
            <div
                className="relative bg-white rounded-[18px] shadow-2xl max-w-lg w-full mx-4 p-6"
                onClick={(e) => e.stopPropagation()}
            >
                <h3 className="text-[24px] font-semibold mb-4">Session Complete</h3>
                <p className="text-[15px] text-[#78716c] mb-4">
                    You read for <span className="font-semibold text-[#292524]">{formatDuration(sessionDuration, false)}</span>
                </p>

                <form onSubmit={form.handleSubmit(onSave)}>
                    <Controller
                        name="startPage"
                        control={form.control}
                        render={({ field }) => (
                            <GenericInput
                                type="number"
                                label="Start Page"
                                {...field}
                                placeholder="Enter start page number"
                                min={1}
                                error={form.formState.errors.startPage?.message}
                                required
                            />
                        )}
                    />

                    <Controller
                        name="endPage"
                        control={form.control}
                        render={({ field }) => (
                            <GenericInput
                                type="number"
                                label="End Page"
                                {...field}
                                placeholder="Enter end page number"
                                min={1}
                                max={book?.pages}
                                error={form.formState.errors.endPage?.message}
                                required
                            />
                        )}
                    />

                    {showFinishCheckbox && (
                        <Controller
                            name="markAsFinished"
                            control={form.control}
                            render={({ field }) => (
                                <div className="flex items-start gap-3 p-4 bg-green-50 border border-green-200 rounded-lg mb-4">
                                    <input
                                        type="checkbox"
                                        id="markAsFinished"
                                        checked={field.value || false}
                                        onChange={(e) => field.onChange(e.target.checked)}
                                        className="mt-0.5 w-4 h-4 text-green-600 border-green-300 rounded focus:ring-green-500"
                                    />
                                    <label htmlFor="markAsFinished" className="flex-1 cursor-pointer">
                                        <div className="text-[14px] font-medium text-green-900">
                                            Mark book as finished
                                        </div>
                                        <div className="text-[12px] text-green-700 mt-1">
                                            You've read all {book?.pages} pages! This book will be marked as finished.
                                        </div>
                                    </label>
                                </div>
                            )}
                        />
                    )}

                    <Controller
                        name="note"
                        control={form.control}
                        render={({ field }) => (
                            <GenericInput
                                type="textarea"
                                label="Session Notes (optional)"
                                {...field}
                                placeholder="What did you think? Any notes?"
                                rows={4}
                                maxLength={2000}
                                showCharCount
                                error={form.formState.errors.note?.message}
                            />
                        )}
                    />

                    <div className="flex flex-col gap-3 mt-6">
                        <div className="flex gap-3">
                            <Button
                                type="modal-primary"
                                label="Save"
                                onClick={form.handleSubmit(onSave)}
                                disabled={!form.formState.isValid || form.formState.isSubmitting}
                                className="flex-1"
                            />
                            <Button
                                type="modal-secondary"
                                label="Cancel"
                                onClick={onCancel}
                                className="flex-1"
                            />
                        </div>
                        <Button
                            type="modal-danger"
                            label="Delete Session"
                            onClick={onDelete}
                            className="w-full"
                        />
                    </div>
                </form>
            </div>
        </div>
    );
}
