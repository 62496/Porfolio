import React, { useState, useEffect, useMemo } from "react";
import { useForm, Controller } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { yupResolver } from "@hookform/resolvers/yup";
import { useAuthors } from "../hooks/useAuthors";
import { useSubjects } from "../hooks/useSubjects";
import GenericInput from "../../../components/forms/GenericInput";
import AuthorSelector from "./AuthorSelector";
import GenreSelector from "./GenreSelector";
import ProgressSteps from "../../../components/forms/ProgressSteps";
import { styles } from "../../../styles/styles";
import bookService from "../../../api/services/bookService";
import { bookValidationSchema } from "../validations/bookSchema";
import AuthService from "../../../api/services/authService";

export default function BookForm({ mode = "create", bookData = null, onSuccess }) {
  const navigate = useNavigate();
  const [currentStep, setCurrentStep] = useState(0);
  const [bookImage, setBookImage] = useState(null);
  const [bookImagePreview, setBookImagePreview] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [apiError, setApiError] = useState(null);
  const currentUser = AuthService.getCurrentUser();

  const { authors, loading: authorsLoading, fetchAuthors } = useAuthors(false);
  const { subjects, loading: subjectsLoading, fetchSubjects } = useSubjects(false);

  const isEditMode = mode === "edit";
  const steps = ["Basic Info", "Authors", "Genres"];

  // Create validation schema based on mode
  const validationSchema = useMemo(() => {
    if (isEditMode) {
      // In edit mode, omit ISBN validation
      return bookValidationSchema.omit(["isbn"]);
    }
    return bookValidationSchema;
  }, [isEditMode]);

  const {
    control,
    handleSubmit,
    formState: { errors, touchedFields },
    trigger,
    setValue,
    watch,
  } = useForm({
    mode: "onBlur",
    resolver: yupResolver(validationSchema),
    defaultValues: {
      isbn: "",
      title: "",
      publishingYear: "",
      pages: "",
      description: "",
      authors: [],
      genres: [],
    },
  });

  // Fetch authors when entering step 1
  useEffect(() => {
    if (currentStep === 1) {
      fetchAuthors();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentStep]);

  // Fetch subjects when entering step 2
  useEffect(() => {
    if (currentStep === 2) {
      fetchSubjects();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentStep]);

  // Populate form in edit mode
  useEffect(() => {
    if (isEditMode && bookData) {
      setValue("isbn", bookData.isbn || "");
      setValue("title", bookData.title || "");
      setValue("publishingYear", bookData.publishingYear || bookData.year || "");
      setValue("pages", bookData.pages || "");
      setValue("description", bookData.description || "");
      setValue("authors", bookData.authors?.map((a) => a.id) || []);
      setValue("genres", bookData.subjects?.map((s) => s.id) || []);

      if (bookData.cover) {
        setBookImagePreview(bookData.cover);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isEditMode, bookData?.isbn]);

  // Pre-select current user's author in create mode
  useEffect(() => {
    if (!isEditMode && currentUser && authors.length > 0) {
      const currentUserAuthor = authors.find(
        (author) => author.user?.id === currentUser.id
      );
      if (currentUserAuthor) {
        setValue("authors", [currentUserAuthor.id]);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isEditMode, authors, currentUser?.id]);

  const validateCurrentStep = async () => {
    const stepFields = {
      0: isEditMode
        ? ["title", "publishingYear", "pages", "description"]
        : ["isbn", "title", "publishingYear", "pages", "description"],
      1: ["authors"],
      2: ["genres"],
    };
    return await trigger(stepFields[currentStep]);
  };

  const handleNext = async () => {
    if (await validateCurrentStep()) {
      setCurrentStep((prev) => prev + 1);
    }
  };

  const handleBack = () => setCurrentStep((prev) => prev - 1);

  const onSubmit = async (data) => {
    try {
      setSubmitting(true);
      setApiError(null);

      const payload = isEditMode
        ? {
            bookTitle: data.title,
            publishingYear: Number(data.publishingYear),
            pages: Number(data.pages),
            description: data.description,
            authors: data.authors,
            subjects: data.genres,
            imageUrl: bookImage ? null : bookData.cover,
          }
        : {
            isbn: data.isbn,
            title: data.title,
            publishingYear: Number(data.publishingYear),
            pages: Number(data.pages),
            description: data.description,
            authors: data.authors.map((id) => ({ id })),
            subjects: data.genres.map((id) => ({ id })),
          };

      const formData = new FormData();
      formData.append(
        "book",
        new Blob([JSON.stringify(payload)], { type: "application/json" })
      );
      formData.append(
        "image",
        bookImage || new Blob([], { type: "application/octet-stream" })
      );

      if (isEditMode) {
        await bookService.update(bookData.isbn, formData);
        if (onSuccess) {
          onSuccess();
        } else {
          navigate(`/book/${bookData.isbn}`);
        }
      } else {
        await bookService.create(formData);
        if (onSuccess) {
          onSuccess();
        } else {
          navigate("/books");
        }
      }
    } catch (err) {
      console.error(`Failed to ${isEditMode ? "update" : "create"} book:`, err);
      setApiError(`Failed to ${isEditMode ? "update" : "add"} book. Please try again.`);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.formWrapper}>
        <div style={styles.formHeader}>
          <h1 style={styles.formTitle}>{isEditMode ? "Edit Book" : "Add New Book"}</h1>
          <p style={styles.formSubtitle}>
            {isEditMode
              ? "Update the book information"
              : "Complete the steps to add your book"}
          </p>
        </div>

        {apiError && (
          <div
            style={{
              padding: "12px",
              background: "#fee",
              border: "1px solid #fcc",
              borderRadius: "8px",
              color: "#c00",
              marginBottom: "24px",
            }}
          >
            ⚠️ {apiError}
          </div>
        )}

        <ProgressSteps currentStep={currentStep} steps={steps} />

        <div style={styles.stepContent}>
          {/* STEP 0 - BASIC INFO */}
          {currentStep === 0 && (
            <div style={styles.fadeIn}>
              <h2 style={styles.stepTitle}>Basic Information</h2>
              <p style={styles.stepDescription}>
                {isEditMode ? "Update" : "Enter"} the essential details about{" "}
                {isEditMode ? "the" : "your"} book
              </p>

              <div style={styles.gridTwo}>
                {!isEditMode && (
                  <Controller
                    name="isbn"
                    control={control}
                    rules={{ required: "ISBN is required" }}
                    render={({ field }) => (
                      <GenericInput
                        label="ISBN"
                        {...field}
                        onChange={(e) => {
                          const value = e.target.value.replace(/\D/g, "").slice(0, 13);
                          field.onChange(value);
                        }}
                        placeholder="9781234567890"
                        maxLength={13}
                        error={touchedFields.isbn && errors.isbn?.message}
                        required
                      />
                    )}
                  />
                )}

                <Controller
                  name="publishingYear"
                  control={control}
                  rules={{ required: "Publishing year is required" }}
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
              </div>

              <Controller
                name="title"
                control={control}
                rules={{ required: "Title is required" }}
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

              <Controller
                name="description"
                control={control}
                rules={{ required: "Description is required" }}
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

              <GenericInput
                type="file"
                label={
                  isEditMode
                    ? "Book Cover Image (optional - leave empty to keep current)"
                    : "Book Cover Image"
                }
                name="bookImage"
                onChange={(e) => setBookImage(e.target.files?.[0])}
                onFilePreview={(dataUrl) => setBookImagePreview(dataUrl)}
                preview={bookImagePreview}
                accept="image/*"
              />
            </div>
          )}

          {/* STEP 1 – AUTHORS */}
          {currentStep === 1 && (
            <div style={styles.fadeIn}>
              <h2 style={styles.stepTitle}>Select Authors</h2>
              <p style={styles.stepDescription}>Choose one or more authors</p>

              {authorsLoading || authors.length === 0 ? (
                <div style={{ textAlign: "center", padding: "40px 0", color: "#666" }}>
                  Loading authors...
                </div>
              ) : (
                <Controller
                  name="authors"
                  control={control}
                  rules={{ required: "At least one author is required" }}
                  render={({ field }) => (
                    <AuthorSelector
                      selected={field.value}
                      options={authors}
                      onToggle={(author) => {
                        const updated = field.value.includes(author)
                          ? field.value.filter((a) => a !== author)
                          : [...field.value, author];
                        field.onChange(updated);
                      }}
                    />
                  )}
                />
              )}

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

              {subjectsLoading || subjects.length === 0 ? (
                <div style={{ textAlign: "center", padding: "40px 0", color: "#666" }}>
                  Loading genres...
                </div>
              ) : (
                <Controller
                  name="genres"
                  control={control}
                  rules={{ required: "At least one genre is required" }}
                  render={({ field }) => (
                    <GenreSelector
                      selected={field.value}
                      options={subjects}
                      onToggle={(genre) => {
                        const updated = field.value.includes(genre)
                          ? field.value.filter((g) => g !== genre)
                          : [...field.value, genre];
                        field.onChange(updated);
                      }}
                    />
                  )}
                />
              )}

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
              disabled={submitting}
            >
              ← Back
            </button>
          )}

          <div style={{ marginLeft: "auto", display: "flex", gap: "12px" }}>
            <button
              type="button"
              onClick={() =>
                isEditMode ? navigate(`/book/${bookData.isbn}`) : window.history.back()
              }
              style={styles.btnCancel}
              disabled={submitting}
            >
              Cancel
            </button>

            {currentStep < steps.length - 1 ? (
              <button
                type="button"
                onClick={handleNext}
                style={styles.btnPrimary}
                disabled={submitting}
              >
                Next →
              </button>
            ) : (
              <button
                type="button"
                onClick={handleSubmit(onSubmit)}
                style={{
                  ...styles.btnPrimary,
                  ...(submitting && styles.btnDisabled),
                }}
                disabled={submitting}
              >
                {submitting
                  ? isEditMode
                    ? "Updating..."
                    : "Adding..."
                  : isEditMode
                  ? "Update Book"
                  : "Add Book"}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
