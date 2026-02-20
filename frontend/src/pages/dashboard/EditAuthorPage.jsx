import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { authorValidationSchema } from '../../features/books/validations/authorSchema';
import { styles } from '../../styles/styles';
import GenericInput from '../../components/forms/GenericInput';
import Header from '../../components/layout/Header';
import Footer from '../../components/layout/Footer';
import Toast from '../../components/common/Toast';
import authorService from '../../api/services/authorService';

export default function EditAuthorPage() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [author, setAuthor] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [toast, setToast] = useState(null);

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

    useEffect(() => {
        const fetchAuthor = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await authorService.getById(id);
                setAuthor(data);
                reset({
                    firstName: data.firstName || '',
                    lastName: data.lastName || '',
                    imageType: 'file',
                    imageUrl: ''
                });
                if (data.imageUrl) {
                    setImagePreview(data.imageUrl);
                }
            } catch (err) {
                console.error('Error fetching author:', err);
                setError('Failed to load author details');
            } finally {
                setLoading(false);
            }
        };

        fetchAuthor();
    }, [id, reset]);

    const onSubmit = async (data) => {
        setSaving(true);
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
                // No new image - keep existing
                formData.append(
                    'image',
                    new Blob([], { type: 'application/octet-stream' })
                );
                formData.append('imageUrl', '');
            }

            await authorService.update(id, formData);

            setToast({ message: 'Author updated successfully!', type: 'success' });

            // Navigate back to author detail after a short delay
            setTimeout(() => {
                navigate(`/authors/${id}`);
            }, 1500);

        } catch (err) {
            console.error('Failed to update author:', err);
            setToast({
                message: err?.response?.data?.message || 'Failed to update author. Please try again.',
                type: 'error'
            });
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="font-sans bg-white text-[#1d1d1f] min-h-screen flex flex-col">
                <Header />
                <div className="flex-1 flex items-center justify-center">
                    <div className="text-center">
                        <div className="w-16 h-16 border-4 border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
                        <p className="text-[17px] text-[#6e6e73]">Loading author...</p>
                    </div>
                </div>
                <Footer />
            </div>
        );
    }

    if (error || !author) {
        return (
            <div className="font-sans bg-white text-[#1d1d1f] min-h-screen flex flex-col">
                <Header />
                <div className="flex-1 flex items-center justify-center">
                    <div className="text-center max-w-md mx-auto px-6">
                        <h2 className="text-[28px] font-semibold mb-4">Author Not Found</h2>
                        <p className="text-[17px] text-[#6e6e73] mb-8">
                            {error || "The author you're looking for doesn't exist."}
                        </p>
                        <button
                            onClick={() => navigate('/authors')}
                            className="px-6 py-3 bg-[#1d1d1f] text-white rounded-full font-medium hover:bg-[#424245] transition-colors"
                        >
                            Back to Authors
                        </button>
                    </div>
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="font-sans bg-white text-[#1d1d1f] min-h-screen flex flex-col">
            <Header />
            <div className="flex-1">
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
                            <h1 style={styles.formTitle}>Edit Author</h1>
                            <p style={styles.formSubtitle}>Update author information</p>
                        </div>

                        {/* Current Author Image */}
                        {author.imageUrl && !imageFile && imageType === 'file' && (
                            <div style={{ marginBottom: '24px', textAlign: 'center' }}>
                                <p style={{ ...styles.label, marginBottom: '12px' }}>Current Image</p>
                                <img
                                    src={author.imageUrl}
                                    alt={`${author.firstName} ${author.lastName}`}
                                    style={{
                                        width: '150px',
                                        height: '150px',
                                        objectFit: 'cover',
                                        borderRadius: '50%',
                                        border: '4px solid #e5e5e7',
                                        margin: '0 auto'
                                    }}
                                />
                            </div>
                        )}

                        <div style={styles.stepContent}>
                            <div style={styles.fadeIn}>
                                <h2 style={styles.stepTitle}>Author Information</h2>
                                <p style={styles.stepDescription}>Update the author's details</p>

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
                                        Update Author Image
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
                                                            setImagePreview(author.imageUrl || null);
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
                                            preview={imageFile ? imagePreview : null}
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
                                onClick={() => navigate(`/authors/${id}`)}
                                style={styles.btnCancel}
                                disabled={saving}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                onClick={handleSubmit(onSubmit)}
                                style={{ ...styles.btnPrimary, ...(saving && styles.btnDisabled) }}
                                disabled={saving}
                            >
                                {saving ? 'Saving...' : 'Save Changes'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </div>
    );
}
