import * as yup from 'yup';

// Validation schema for warning message form
export const warnAuthorSchema = yup.object().shape({
    warningMessage: yup
        .string()
        .required('Warning message is required')
        .min(20, 'Warning message must be at least 20 characters')
        .max(1000, 'Warning message must be at most 1000 characters')
        .trim(),
});

// Validation schema for editing book content in reports
export const editBookReportSchema = yup.object().shape({
    isbn: yup
        .string()
        .required('ISBN is required')
        .matches(/^\d{13}$/, 'ISBN must be exactly 13 digits'),

    title: yup
        .string()
        .required('Title is required')
        .min(1, 'Title cannot be empty')
        .trim(),

    publishingYear: yup
        .number()
        .required('Publishing year is required')
        .min(1000, 'Year must be 1000 or later')
        .max(new Date().getFullYear() + 1, 'Year cannot be in the future')
        .typeError('Publishing year must be a number'),

    pages: yup
        .number()
        .required('Number of pages is required')
        .min(1, 'Pages must be at least 1')
        .max(20000, 'Pages must be at most 20,000')
        .typeError('Pages must be a number'),

    description: yup
        .string()
        .required('Description is required')
        .min(10, 'Description must be at least 10 characters')
        .max(2000, 'Description must be at most 2000 characters')
        .trim(),

    authors: yup
        .array()
        .min(1, 'Please select at least one author')
        .required('Authors are required'),

    genres: yup
        .array()
        .min(1, 'Please select at least one genre')
        .required('Genres are required'),
});

// Validation schema for editing author content in reports
export const editAuthorReportSchema = yup.object().shape({
    firstName: yup
        .string()
        .required('First name is required')
        .min(1, 'First name cannot be empty')
        .max(100, 'First name must be at most 100 characters')
        .trim(),

    lastName: yup
        .string()
        .required('Last name is required')
        .min(1, 'Last name cannot be empty')
        .max(100, 'Last name must be at most 100 characters')
        .trim(),
});
