import { Controller } from 'react-hook-form';
import Button from "../../../../components/common/Button";
import GenericInput from "../../../../components/forms/GenericInput";

export default function StartSessionModal({
    isOpen,
    form,
    onConfirm,
    onCancel,
}) {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center" onClick={onCancel}>
            <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" />
            <div
                className="relative bg-white rounded-[18px] shadow-2xl max-w-lg w-full mx-4 p-6"
                onClick={(e) => e.stopPropagation()}
            >
                <h3 className="text-[24px] font-semibold mb-4">Start Reading Session</h3>

                <form onSubmit={form.handleSubmit(onConfirm)}>
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

                    <div className="flex gap-3 mt-6">
                        <Button
                            type="modal-primary"
                            label="Start"
                            onClick={form.handleSubmit(onConfirm)}
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
                </form>
            </div>
        </div>
    );
}
