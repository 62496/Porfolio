import React, { useState } from 'react';
import { styles } from "../../styles/styles"

const MultiSelect = ({
    selected,
    options,
    onToggle,

    displayMode = 'list',
    searchable = false,
    searchPlaceholder = 'Search...',

    filterOption = (option, searchTerm) =>
        String(option).toLowerCase().includes(searchTerm.toLowerCase()),

    renderOption = (option) => String(option),

    getOptionValue = (option) => option,

    renderSelected = (selectedValue) => String(selectedValue)
}) => {

    const [searchTerm, setSearchTerm] = useState('');

    const filteredOptions = searchable
        ? options.filter(opt => filterOption(opt, searchTerm))
        : options;

    const isSelected = (option) => {
        const value = getOptionValue(option);
        return selected.includes(value);
    };

    const handleToggle = (option) => {
        const storedValue = getOptionValue(option);
        onToggle(storedValue, option);
    };

    const renderListMode = () => (
        <div style={styles.authorList}>
            {filteredOptions.map(option => {
                const value = getOptionValue(option);
                const selected = isSelected(option);

                return (
                    <div
                        key={value}
                        style={{
                            ...styles.authorItem,
                            ...(selected && styles.authorItemSelected)
                        }}
                        onClick={() => handleToggle(option)}
                    >
                        <input
                            type="checkbox"
                            checked={selected}
                            onChange={() => { }}
                            style={styles.checkbox}
                        />
                        <span>{renderOption(option)}</span>
                    </div>
                );
            })}
        </div>
    );

    const renderGridMode = () => (
        <div style={styles.genreGrid}>
            {filteredOptions.map(option => {
                const value = getOptionValue(option);
                const selected = isSelected(option);

                return (
                    <button
                        key={value}
                        type="button"
                        onClick={() => handleToggle(option)}
                        style={{
                            ...styles.genreButton,
                            ...(selected && styles.genreButtonActive)
                        }}
                    >
                        {renderOption(option)}
                    </button>
                );
            })}
        </div>
    );

    return (
        <div>
            {searchable && (
                <input
                    type="text"
                    placeholder={searchPlaceholder}
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    style={styles.searchInput}
                />
            )}

            {displayMode === 'list' ? renderListMode() : renderGridMode()}

            {selected.length > 0 && (
                <div style={styles.selectedSection}>
                    <div style={styles.selectedLabel}>Selected ({selected.length}):</div>
                    <div style={styles.selectedTags}>
                        {selected.map((stored) => (
                            <div key={stored} style={styles.tag}>
                                <span>{renderSelected(stored)}</span>
                                <button
                                    type="button"
                                    onClick={() => onToggle(stored)}
                                    style={styles.tagRemove}
                                >
                                    ✕
                                </button>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default MultiSelect;
