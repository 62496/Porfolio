import React, { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { authorValidationSchema } from '../validations/authorSchema';
import { styles } from '../../../styles/styles';
import GenericInput from '../../../components/forms/GenericInput';
import { useNavigate } from 'react-router-dom';
import { useAuthors } from '../hooks/useAuthors';
import Toast from '../../../components/common/Toast';

const AddAuthorForm = () => {
    const [imagePreview, setImagePreview] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [toast, setToast] = useState(null);
    const navigate = useNavigate();
    const { createAuthor, loading, error: apiError } = useAuthors(false);

    const {
        control,
        handleSubmit,
        formState: { errors, touchedFields },
        watch,
        reset
    } = useForm({
        resolver: yupResolver(authorValidationSchema),
        mode: 'onBlur',
        defaultValues: {
            firstName: '',
            lastName: '',
            imageType: 'file',
            imageUrl: ''
        }
    });

    const imageType = watch('imageType');
    const imageUrl = watch('imageUrl');

    const onSubmit = async (data) => {
        try {
            const authorData = {
                firstName: data.firstName.trim(),
                lastName: data.lastName.trim()
            };

            const formData = new FormData();

            formData.append(
                'author',
                new Blob([JSON.stringify(authorData)], { type: 'application/json' })
            );

            // Handle image based on type
            if (data.imageType === 'url' && data.imageUrl) {
                formData.append('imageUrl', data.imageUrl);
                formData.append(
                    'image',
                    new Blob([], { type: 'application/octet-stream' })
                );
            } else if (imageFile) {
                formData.append('image', imageFile);
                formData.append('imageUrl', '');
            } else {
                formData.append(
                    'image',
                    new Blob([], { type: 'application/octet-stream' })
                );
                formData.append('imageUrl', '');
            }

            await createAuthor(formData);

            // Reset form on success
            reset();
            setImageFile(null);
            setImagePreview(null);

            setToast({ message: 'Author created successfully!', type: 'success' });

            // Navigate to authors list after a short delay
            setTimeout(() => {
                navigate('/authors');
            }, 1500);

        } catch (err) {
            console.error('Failed to create author:', err);
            setToast({
                message: err?.response?.data?.message || 'Failed to add author. Please try again.',
                type: 'error'
            });
        }
    };

    return (
        <div style={styles.container}>
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={() => setToast(null)}
                />
            )}

            <div style={styles.formWrapper}>
                <div style={styles.formHeader}>
                    <h1 style={styles.formTitle}>Add New Author</h1>
                    <p style={styles.formSubtitle}>Complete the form to add a new author</p>
                </div>

                {apiError && (
                    <div style={{
                        padding: '12px',
                        background: '#fee',
                        border: '1px solid #fcc',
                        borderRadius: '8px',
                        color: '#c00',
                        marginBottom: '24px'
                    }}>
                        ⚠️ {apiError}
                    </div>
                )}

                <div style={styles.stepContent}>
                    <div style={styles.fadeIn}>
                        <h2 style={styles.stepTitle}>Author Information</h2>
                        <p style={styles.stepDescription}>Enter the author's details</p>

                        <div style={styles.gridTwo}>
                            {/* First Name */}
                            <Controller
                                name="firstName"
                                control={control}
                                render={({ field }) => (
                                    <GenericInput
                                        label="First Name"
                                        {...field}
                                        placeholder="John"
                                        error={touchedFields.firstName && errors.firstName?.message}
                                        required
                                    />
                                )}
                            />

                            {/* Last Name */}
                            <Controller
                                name="lastName"
                                control={control}
                                render={({ field }) => (
                                    <GenericInput
                                        label="Last Name"
                                        {...field}
                                        placeholder="Doe"
                                        error={touchedFields.lastName && errors.lastName?.message}
                                        required
                                    />
                                )}
                            />
                        </div>

                        {/* Image Type Selection */}
                        <div style={{ ...styles.formGroup, marginTop: '20px' }}>
                            <label style={styles.label}>
                                Author Image <span style={styles.required}>*</span>
                            </label>

                            <Controller
                                name="imageType"
                                control={control}
                                render={({ field }) => (
                                    <div style={{ display: 'flex', gap: '16px', marginBottom: '16px' }}>
                                        <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                                            <input
                                                type="radio"
                                                value="file"
                                                checked={field.value === 'file'}
                                                onChange={() => {
                                                    field.onChange('file');
                                                    setImagePreview(null);
                                                }}
                                                style={{ marginRight: '8px' }}
                                            />
                                            Upload File
                                        </label>
                                        <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                                            <input
                                                type="radio"
                                                value="url"
                                                checked={field.value === 'url'}
                                                onChange={() => {
                                                    field.onChange('url');
                                                    setImageFile(null);
                                                }}
                                                style={{ marginRight: '8px' }}
                                            />
                                            Image URL
                                        </label>
                                    </div>
                                )}
                            />

                            {/* File Upload */}
                            {imageType === 'file' && (
                                <GenericInput
                                    type="file"
                                    label=""
                                    name="authorImage"
                                    onChange={(e) => setImageFile(e.target.files?.[0])}
                                    onFilePreview={(dataUrl) => setImagePreview(dataUrl)}
                                    preview={imagePreview}
                                    accept="image/*"
                                />
                            )}

                            {/* URL Input */}
                            {imageType === 'url' && (
                                <>
                                    <Controller
                                        name="imageUrl"
                                        control={control}
                                        render={({ field }) => (
                                            <GenericInput
                                                label=""
                                                {...field}
                                                placeholder="https://example.com/author-image.jpg"
                                                error={touchedFields.imageUrl && errors.imageUrl?.message}
                                            />
                                        )}
                                    />

                                    {/* URL Preview */}
                                    {imageUrl && !errors.imageUrl && (
                                        <div style={{ marginTop: '12px' }}>
                                            <img
                                                src={imageUrl}
                                                alt="Author preview"
                                                style={{
                                                    width: '150px',
                                                    height: '150px',
                                                    objectFit: 'cover',
                                                    borderRadius: '8px',
                                                    border: '2px solid #e5e5e7'
                                                }}
                                                onError={(e) => {
                                                    e.target.style.display = 'none';
                                                }}
                                            />
                                        </div>
                                    )}
                                </>
                            )}
                        </div>
                    </div>
                </div>

                {/* NAVIGATION BUTTONS */}
                <div style={styles.navigationButtons}>
                    <button
                        type="button"
                        onClick={() => window.history.back()}
                        style={styles.btnCancel}
                        disabled={loading}
                    >
                        Cancel
                    </button>

                    <button
                        type="button"
                        onClick={handleSubmit(onSubmit)}
                        style={{ ...styles.btnPrimary, ...(loading && styles.btnDisabled) }}
                        disabled={loading}
                    >
                        {loading ? 'Adding...' : 'Add Author'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AddAuthorForm;
