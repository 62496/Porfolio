import * as yup from 'yup';

export const bookValidationSchema = yup.object().shape({
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
        .required('Pages is required')
        .positive('Pages must be positive')
        .integer('Pages must be a whole number')
        .min(1, 'Pages must be at least 1')
        .max(20000, 'Pages cannot exceed 20,000')
        .typeError('Pages must be a number'),

    description: yup
        .string()
        .required('Description is required')
        .min(50, 'Description must be at least 50 characters')
        .max(2000, 'Description must be at most 2000 characters'),

    authors: yup
        .array()
        .min(1, 'Please select at least one author')
        .required('Authors are required'),

    genres: yup
        .array()
        .min(1, 'Please select at least one genre')
        .required('Genres are required')
});