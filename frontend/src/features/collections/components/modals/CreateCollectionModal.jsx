import React from 'react';
import { Controller } from 'react-hook-form';
import Modal from '../../../../components/common/Modal';
import Button from '../../../../components/common/Button';
import GenericInput from '../../../../components/forms/GenericInput';

const CreateCollectionModal = ({
    isOpen,
    form,
    loading,
    image,
    setImage,
    imagePreview,
    setImagePreview,
    onSubmit,
    onClose,
}) => {
    const handleImageChange = (e) => {
        const file = e.target.files?.[0];
        if (file) {
            setImage(file);
            const reader = new FileReader();
            reader.onloadend = () => setImagePreview(reader.result);
            reader.readAsDataURL(file);
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Create Collection">
            <form onSubmit={form.handleSubmit(onSubmit)}>
                {/* Name */}
                <Controller
                    name="name"
                    control={form.control}
                    render={({ field }) => (
                        <GenericInput
                            label="Collection Name"
                            {...field}
                            placeholder="My Reading List"
                            error={form.formState.errors.name?.message}
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
                            placeholder="What's this collection about?"
                            rows={3}
                            maxLength={500}
                            showCharCount
                            error={form.formState.errors.description?.message}
                        />
                    )}
                />

                {/* Visibility */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-[#1d1d1f] mb-2">
                        Visibility <span className="text-red-500">*</span>
                    </label>
                    <Controller
                        name="visibility"
                        control={form.control}
                        render={({ field }) => (
                            <div className="flex gap-2">
                                <button
                                    type="button"
                                    onClick={() => field.onChange('PRIVATE')}
                                    className={`flex-1 flex items-center justify-center gap-2 px-4 py-3 rounded-xl text-[14px] font-medium transition-all ${
                                        field.value === 'PRIVATE'
                                            ? 'bg-[#1d1d1f] text-white'
                                            : 'bg-[#f5f5f7] text-[#1d1d1f] hover:bg-[#e5e5e7]'
                                    }`}
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z" />
                                    </svg>
                                    Private
                                </button>
                                <button
                                    type="button"
                                    onClick={() => field.onChange('PUBLIC')}
                                    className={`flex-1 flex items-center justify-center gap-2 px-4 py-3 rounded-xl text-[14px] font-medium transition-all ${
                                        field.value === 'PUBLIC'
                                            ? 'bg-[#1d1d1f] text-white'
                                            : 'bg-[#f5f5f7] text-[#1d1d1f] hover:bg-[#e5e5e7]'
                                    }`}
                                >
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-4 h-4">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
                                    </svg>
                                    Public
                                </button>
                            </div>
                        )}
                    />
                    {form.formState.errors.visibility && (
                        <p className="text-red-500 text-sm mt-1">
                            {form.formState.errors.visibility.message}
                        </p>
                    )}
                </div>

                {/* Image */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-[#1d1d1f] mb-2">
                        Cover Image (optional)
                    </label>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={handleImageChange}
                        className="w-full px-4 py-3 border border-[#e5e5e7] rounded-xl focus:outline-none focus:border-[#0066cc] text-sm"
                    />
                    {imagePreview && (
                        <div className="mt-3 relative inline-block">
                            <img
                                src={imagePreview}
                                alt="Preview"
                                className="h-24 w-auto rounded-lg object-cover border border-[#e5e5e7]"
                            />
                            <button
                                type="button"
                                onClick={() => {
                                    setImage(null);
                                    setImagePreview(null);
                                }}
                                className="absolute -top-2 -right-2 w-6 h-6 bg-red-500 text-white rounded-full flex items-center justify-center text-xs hover:bg-red-600"
                            >
                                <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
                                </svg>
                            </button>
                        </div>
                    )}
                </div>

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
                        label={loading ? 'Creating...' : 'Create Collection'}
                        onClick={form.handleSubmit(onSubmit)}
                        disabled={!form.formState.isValid || loading}
                        className="flex-1"
                    />
                </div>
            </form>
        </Modal>
    );
};

export default CreateCollectionModal;
