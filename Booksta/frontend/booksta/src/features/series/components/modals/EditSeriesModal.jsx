import React from 'react';
import { Controller } from 'react-hook-form';
import Modal from '../../../../components/common/Modal';
import Button from '../../../../components/common/Button';
import GenericInput from '../../../../components/forms/GenericInput';

const EditSeriesModal = ({
    isOpen,
    series,
    form,
    loading,
    onSubmit,
    onClose,
}) => {
    if (!series) return null;

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Edit Series">
            <form onSubmit={form.handleSubmit(onSubmit)}>
                {/* Title */}
                <Controller
                    name="title"
                    control={form.control}
                    render={({ field }) => (
                        <GenericInput
                            label="Series Title"
                            {...field}
                            placeholder="Harry Potter"
                            error={form.formState.errors.title?.message}
                            required
                        />
                    )}
                />

                {/* Description */}
                <Controller
                    name="description"
                    control={form.control}
                    render={({ field }) => (
                        <GenericInput
                            type="textarea"
                            label="Description"
                            {...field}
                            placeholder="Describe your series..."
                            rows={3}
                            maxLength={1000}
                            showCharCount
                            error={form.formState.errors.description?.message}
                        />
                    )}
                />

                {/* Actions */}
                <div className="flex gap-3 mt-6">
                    <Button
                        type="modal-secondary"
                        label="Cancel"
                        onClick={onClose}
                        className="flex-1"
                    />
                    <Button
                        type="modal-primary"
                        label={loading ? 'Saving...' : 'Save Changes'}
                        onClick={form.handleSubmit(onSubmit)}
                        disabled={!form.formState.isValid || loading}
                        className="flex-1"
                    />
                </div>
            </form>
        </Modal>
    );
};

export default EditSeriesModal;
