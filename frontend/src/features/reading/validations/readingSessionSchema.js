import * as yup from 'yup';

export const startSessionSchema = yup.object().shape({
    startPage: yup
        .number()
        .typeError('Start page is required')
        .required('Start page is required')
        .min(1, 'Start page must be at least 1')
        .integer('Start page must be a whole number')
});

export const endSessionSchema = (totalPages) => yup.object().shape({
    startPage: yup
        .number()
        .typeError('Start page is required')
        .required('Start page is required')
        .min(1, 'Start page must be at least 1')
        .integer('Start page must be a whole number'),

    endPage: yup
        .number()
        .typeError('End page is required')
        .required('End page is required')
        .min(1, 'End page must be at least 1')
        .integer('End page must be a whole number')
        .test('greater-than-start', 'End page must be greater than start page', function(value) {
            const { startPage } = this.parent;
            if (!value || !startPage) return true;
            return value > startPage;
        })
        .test('max-pages', `End page cannot exceed ${totalPages} pages`, function(value) {
            if (!value || !totalPages) return true;
            return value <= totalPages;
        }),

    markAsFinished: yup
        .boolean()
        .nullable(),

    note: yup
        .string()
        .max(2000, 'Note must be at most 2000 characters')
        .nullable()
});
