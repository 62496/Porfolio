import React from 'react';
import { Controller } from 'react-hook-form';
import Modal from '../../../../components/common/Modal';
import Button from '../../../../components/common/Button';
import GenericInput from '../../../../components/forms/GenericInput';

const CreateSeriesModal = ({
    isOpen,
    form,
    loading,
    onSubmit,
    onClose,
    isLibrarian = false,
    authors = [],
}) => {
    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Create Series">
            <form onSubmit={form.handleSubmit(onSubmit)}>
                {/* Author Selector (Librarian only) */}
                {isLibrarian && (
                    <Controller
                        name="authorId"
                        control={form.control}
                        render={({ field }) => (
                            <div className="mb-4">
                                <label className="block text-sm font-medium text-[#1d1d1f] mb-2">
                                    Author <span className="text-red-500">*</span>
                                </label>
                                <select
                                    {...field}
                                    className={`w-full px-4 py-3 border rounded-xl bg-white text-[#1d1d1f] focus:outline-none focus:ring-2 focus:ring-[#0066cc] transition-all ${
                                        form.formState.errors.authorId
                                            ? 'border-red-500'
                                            : 'border-[#e5e5e7]'
                                    }`}
                                >
                                    <option value="">Select an author...</option>
                                    {authors.map((author) => (
                                        <option key={author.id} value={author.id}>
                                            {author.firstName} {author.lastName}
                                        </option>
                                    ))}
                                </select>
                                {form.formState.errors.authorId && (
                                    <p className="mt-1 text-sm text-red-500">
                                        {form.formState.errors.authorId.message}
                                    </p>
                                )}
                            </div>
                        )}
                    />
                )}

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
                            placeholder="Describe this series..."
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
                        label={loading ? 'Creating...' : 'Create Series'}
                        onClick={form.handleSubmit(onSubmit)}
                        disabled={!form.formState.isValid || loading}
                        className="flex-1"
                    />
                </div>
            </form>
        </Modal>
    );
};

export default CreateSeriesModal;
