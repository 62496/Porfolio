import React from 'react';
import MultiSelect from '../../../components/forms/Multiselect';

const GenreSelector = ({ selected, options, onToggle }) => {
    return (
        <MultiSelect
            selected={selected}
            options={options}
            onToggle={onToggle}

            displayMode="grid"
            searchable={false}

            renderOption={(genre) => genre.name}

            getOptionValue={(genre) => genre.id}

            renderSelected={(id) => {
                const genre = options.find(g => g.id === id);
                return genre ? genre.name : "Unknown";
            }}
        />
    );
};

export default GenreSelector;
