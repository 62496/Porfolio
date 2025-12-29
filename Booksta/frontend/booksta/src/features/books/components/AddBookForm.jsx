import React, { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { bookValidationSchema } from '../validations/bookSchema';
import { useBooks } from '../hooks/useBooks';
import { styles } from '../../../styles/styles';
import GenericInput from '../../../components/forms/GenericInput';
import AuthorSelector from './AuthorSelector';
import GenreSelector from './GenreSelector';
import ProgressSteps from '../../../components/forms/ProgressSteps';
import { useAuthors } from "../hooks/useAuthors";
import { useSubjects } from "../hooks/useSubjects";
import { useNavigate } from "react-router-dom";
import useSeries from '../hooks/useSeries';
import seriesService from '../../../api/services/seriesService';
import useToast from '../../../hooks/useToast';
import Toast from '../../../components/common/Toast';

const AddBookForm = () => {
    const [currentStep, setCurrentStep] = useState(0);

    const [bookImage, setBookImage] = useState(null);
    const [bookImagePreview, setBookImagePreview] = useState(null);

    const navigate = useNavigate();
    const { toast, showToast, hideToast } = useToast();

    const { authors, fetchAuthors } = useAuthors(false);
    const { subjects, fetchSubjects } = useSubjects(false);
    const { series, fetchSeries } = useSeries(false);
    
    useEffect(() => {
        fetchAuthors();
        fetchSubjects();
        fetchSeries();
    }, [fetchAuthors, fetchSubjects, fetchSeries]);

    const { createBook, loading, error: apiError } = useBooks(false);

    const steps = ['Basic Info', 'Authors', 'Genres'];

    const {
        control,
        handleSubmit,
        formState: { errors, touchedFields },
        trigger,
        reset,
        watch,
    } = useForm({
        resolver: yupResolver(bookValidationSchema),
        mode: 'onBlur',
        defaultValues: {
            isbn: '',
            title: '',
            publishingYear: '',
            pages: '',
            description: '',
            authors: [],
            genres: [],
            seriesId: '',
            newSeriesTitle: ''
        }
    });

    const selectedSeriesId = watch('seriesId');

    const validateCurrentStep = async () => {
        const stepFields = {
            0: ['isbn', 'title', 'publishingYear', 'pages', 'description'],
            1: ['authors'],
            2: ['genres']
        };
        return await trigger(stepFields[currentStep]);
    };

    const handleNext = async () => {
        if (await validateCurrentStep()) {
            setCurrentStep(prev => prev + 1);
        }
    };

    const handleBack = () => setCurrentStep(prev => prev - 1);

    const onSubmit = async (data) => {
        try {
            const bookData = {
                ...data,
                pages: Number(data.pages),  // ensure number
                authors: data.authors.map(id => ({ id })),
                subjects: data.genres.map(id => ({ id }))
            };

            if (data.seriesId && data.seriesId !== '__create_new__' && data.seriesId !== '') {
                bookData.series = { id: Number(data.seriesId) };
            } else if (data.seriesId === '__create_new__' && data.newSeriesTitle && data.newSeriesTitle.trim().length > 0) {
                const createdSeries = await seriesService.create({ title: data.newSeriesTitle.trim() });
                if (!createdSeries || !createdSeries.id) {
                    throw new Error('Failed to create series');
                }
                bookData.series = { id: Number(createdSeries.id) };
            }

            const formData = new FormData();

            formData.append(
                "book",
                new Blob([JSON.stringify(bookData)], { type: "application/json" })
            );

            formData.append(
                "image",
                bookImage || new Blob([], { type: "application/octet-stream" })
            );

            await createBook(formData);

            reset();
            setBookImage(null);
            setBookImagePreview(null);
            setCurrentStep(0);
            navigate("/books");
        } catch {
            showToast(apiError || "Failed to add book. Please try again.", "error");
        }
    };

    return (
        <div style={styles.container}>
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={hideToast}
                    duration={toast.duration}
                />
            )}
            <div style={styles.formWrapper}>
                <div style={styles.formHeader}>
                    <h1 style={styles.formTitle}>Add New Book</h1>
                    <p style={styles.formSubtitle}>Complete the steps to add your book</p>
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

                <ProgressSteps currentStep={currentStep} steps={steps} />

                <div style={styles.stepContent}>

                    {/* STEP 0 - BASIC INFO */}
                    {currentStep === 0 && (
                        <div style={styles.fadeIn}>
                            <h2 style={styles.stepTitle}>Basic Information</h2>
                            <p style={styles.stepDescription}>Enter the essential details about your book</p>

                            <div style={styles.gridTwo}>
                                {/* ISBN */}
                                <Controller
                                    name="isbn"
                                    control={control}
                                    render={({ field }) => (
                                        <GenericInput
                                            label="ISBN"
                                            {...field}
                                            onChange={(e) => {
                                                const value = e.target.value.replace(/\D/g, '').slice(0, 13);
                                                field.onChange(value);
                                            }}
                                            placeholder="9781234567890"
                                            maxLength={13}
                                            error={touchedFields.isbn && errors.isbn?.message}
                                            required
                                        />
                                    )}
                                />

                                {/* Publishing Year */}
                                <Controller
                                    name="publishingYear"
                                    control={control}
                                    render={({ field }) => (
                                        <GenericInput
                                            type="number"
                                            label="Publishing Year"
                                            {...field}
                                            placeholder="2024"
                                            min={1000}
                                            max={new Date().getFullYear() + 1}
                                            error={touchedFields.publishingYear && errors.publishingYear?.message}
                                            required
                                        />
                                    )}
                                />
                            </div>

                            {/* ⭐ NEW ROW: Pages */}
                            <div style={styles.gridTwo}>
                                <Controller
                                    name="pages"
                                    control={control}
                                    render={({ field }) => (
                                        <GenericInput
                                            type="number"
                                            label="Pages"
                                            {...field}
                                            placeholder="300"
                                            min={1}
                                            max={20000}
                                            error={touchedFields.pages && errors.pages?.message}
                                            required
                                        />
                                    )}
                                />

                                {/* Title beside pages for balanced UI */}
                                <Controller
                                    name="title"
                                    control={control}
                                    render={({ field }) => (
                                        <GenericInput
                                            label="Book Title"
                                            {...field}
                                            placeholder="Enter the complete book title"
                                            error={touchedFields.title && errors.title?.message}
                                            required
                                        />
                                    )}
                                />
                            </div>

                            {/* Description */}
                            <Controller
                                name="description"
                                control={control}
                                render={({ field }) => (
                                    <GenericInput
                                        type="textarea"
                                        label="Description"
                                        {...field}
                                        placeholder="Write a compelling description"
                                        rows={6}
                                        showCharCount
                                        maxLength={2000}
                                        error={touchedFields.description && errors.description?.message}
                                        required
                                    />
                                )}
                            />

                            {/* Cover Image */}
                            <GenericInput
                                type="file"
                                label="Book Cover Image"
                                name="bookImage"
                                onChange={(e) => setBookImage(e.target.files?.[0])}
                                onFilePreview={(dataUrl) => setBookImagePreview(dataUrl)}
                                preview={bookImagePreview}
                                accept="image/*"
                            />

                        {/* Series selector */}
                        <div style={{ marginTop: 12, marginBottom: 12 }}>
                            <label style={{ display: 'block', marginBottom: 6 }}>Series (optional)</label>

                            <Controller
                            name="seriesId"
                            control={control}
                            render={({ field }) => (
                                <select {...field} className="px-3 py-2 border rounded w-full">
                                <option value="">-- Select existing series --</option>
                                <option value="__create_new__">+ Create new series...</option>
                                {series.map(s => <option key={s.id} value={s.id}>{s.title}</option>)}
                                </select>
                            )}
                            />

                            {selectedSeriesId === '__create_new__' && (
                            <Controller
                                name="newSeriesTitle"
                                control={control}
                                render={({ field }) => (
                                <input
                                    {...field}
                                    placeholder="New series title"
                                    className="mt-2 px-3 py-2 border rounded w-full"
                                />
                                )}
                            />
                            )}
                        </div>
                        </div>
                    )}


                    {/* STEP 1 – AUTHORS */}
                    {currentStep === 1 && (
                        <div style={styles.fadeIn}>
                            <h2 style={styles.stepTitle}>Select Authors</h2>
                            <p style={styles.stepDescription}>Choose one or more authors</p>

                            <Controller
                                name="authors"
                                control={control}
                                render={({ field }) => (
                                    <AuthorSelector
                                        selected={field.value}
                                        options={authors}
                                        onToggle={(author) => {
                                            const updated = field.value.includes(author)
                                                ? field.value.filter(a => a !== author)
                                                : [...field.value, author];
                                            field.onChange(updated);
                                        }}
                                    />
                                )}
                            />

                            {errors.authors && (
                                <div style={styles.errorMessage}>{errors.authors.message}</div>
                            )}
                        </div>
                    )}

                    {/* STEP 2 – GENRES */}
                    {currentStep === 2 && (
                        <div style={styles.fadeIn}>
                            <h2 style={styles.stepTitle}>Select Genres</h2>
                            <p style={styles.stepDescription}>Choose all genres that apply</p>

                            <Controller
                                name="genres"
                                control={control}
                                render={({ field }) => (
                                    <GenreSelector
                                        selected={field.value}
                                        options={subjects}
                                        onToggle={(genre) => {
                                            const updated = field.value.includes(genre)
                                                ? field.value.filter(g => g !== genre)
                                                : [...field.value, genre];
                                            field.onChange(updated);
                                        }}
                                    />
                                )}
                            />

                            {errors.genres && (
                                <div style={styles.errorMessage}>{errors.genres.message}</div>
                            )}
                        </div>
                    )}
                </div>

                {/* NAVIGATION BUTTONS */}
                <div style={styles.navigationButtons}>
                    {currentStep > 0 && (
                        <button
                            type="button"
                            onClick={handleBack}
                            style={styles.btnSecondary}
                            disabled={loading}
                        >
                            ← Back
                        </button>
                    )}

                    <div style={{ marginLeft: 'auto', display: 'flex', gap: '12px' }}>
                        <button
                            type="button"
                            onClick={() => window.history.back()}
                            style={styles.btnCancel}
                            disabled={loading}
                        >
                            Cancel
                        </button>

                        {currentStep < steps.length - 1 ? (
                            <button
                                type="button"
                                onClick={handleNext}
                                style={styles.btnPrimary}
                                disabled={loading}
                            >
                                Next →
                            </button>
                        ) : (
                            <button
                                type="button"
                                onClick={handleSubmit(onSubmit)}
                                style={{ ...styles.btnPrimary, ...(loading && styles.btnDisabled) }}
                                disabled={loading}
                            >
                                {loading ? 'Adding...' : 'Add Book'}
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AddBookForm;
