import * as yup from 'yup';

export const authorValidationSchema = yup.object().shape({
    firstName: yup
        .string()
        .required('First name is required')
        .min(2, 'First name must be at least 2 characters')
        .max(50, 'First name must be at most 50 characters')
        .trim(),

    lastName: yup
        .string()
        .required('Last name is required')
        .min(2, 'Last name must be at least 2 characters')
        .max(50, 'Last name must be at most 50 characters')
        .trim(),

    imageType: yup
        .string()
        .oneOf(['file', 'url'], 'Please select either file upload or URL')
        .required('Image type is required'),

    imageUrl: yup
        .string()
        .when('imageType', {
            is: 'url',
            then: (schema) => schema
                .required('Image URL is required when URL is selected')
                .url('Please enter a valid URL'),
            otherwise: (schema) => schema.nullable()
        })
});
