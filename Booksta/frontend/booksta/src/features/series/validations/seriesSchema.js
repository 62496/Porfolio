import * as yup from 'yup';

export const seriesSchema = yup.object().shape({
    title: yup
        .string()
        .required('Series title is required')
        .min(2, 'Title must be at least 2 characters')
        .max(200, 'Title must be less than 200 characters')
        .trim(),

    description: yup
        .string()
        .max(1000, 'Description must be less than 1000 characters'),
});

// Schema for librarian creating series (requires author selection)
export const librarianSeriesSchema = yup.object().shape({
    title: yup
        .string()
        .required('Series title is required')
        .min(2, 'Title must be at least 2 characters')
        .max(200, 'Title must be less than 200 characters')
        .trim(),

    description: yup
        .string()
        .max(1000, 'Description must be less than 1000 characters'),

    authorId: yup
        .string()
        .required('Please select an author for this series'),
});

export default seriesSchema;
