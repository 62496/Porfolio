import * as yup from 'yup';

export const collectionSchema = yup.object().shape({
    name: yup
        .string()
        .required('Collection name is required')
        .min(2, 'Name must be at least 2 characters')
        .max(100, 'Name must be less than 100 characters')
        .trim(),

    description: yup
        .string()
        .required('Description is required')
        .min(10, 'Description must be at least 10 characters')
        .max(500, 'Description must be less than 500 characters'),

    visibility: yup
        .string()
        .oneOf(['PUBLIC', 'PRIVATE'], 'Invalid visibility option')
        .required('Visibility is required'),
});

export default collectionSchema;
