import React from 'react';
import MultiSelect from '../../../components/forms/Multiselect';

const AuthorSelector = ({ selected, options, onToggle }) => {
    return (
        <MultiSelect
            selected={selected}
            options={options}
            onToggle={onToggle}
            displayMode="list"
            searchable={true}
            searchPlaceholder="Search authors..."
            renderOption={(author) => author.firstName + " " + author.lastName}
            getOptionValue={(author) => author.id}
            filterOption={(author, term) => {
                const combined = author.firstName + " " + author.lastName;
                return combined.toLowerCase().includes(term.toLowerCase());
            }}
            renderSelected={(id) => {
                const author = options.find(a => a.id === id);
                return author ? `${author.firstName} ${author.lastName}` : "Unknown";
            }}
        />
    );
};

export default AuthorSelector;
