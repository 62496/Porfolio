import React, { useState } from 'react';
import Button from './Button';
import { styles } from '../../styles/styles';

/**
 * Generic, reusable Filter component for filtering content across pages
 *
 * @param {Object} props
 * @param {Object} props.filters - Current filter values
 * @param {Function} props.updateFilter - Function to update a single filter
 * @param {Function} props.toggleArrayFilter - Function to toggle array filter values
 * @param {Function} props.onApply - Function called when Apply button is clicked
 * @param {Function} props.onReset - Function called when Reset button is clicked
 * @param {boolean} props.hasActiveFilters - Whether any filters are currently set
 * @param {number} props.activeFilterCount - Number of active applied filters
 * @param {boolean} props.loading - Whether data is currently loading
 * @param {Array} props.fields - Array of field configurations
 *
 * Field configuration options:
 * - { type: 'text', name: 'title', label: 'Title', placeholder: 'Search...' }
 * - { type: 'number', name: 'year', label: 'Year', min: 1900, max: 2024 }
 * - { type: 'range', nameMin: 'yearMin', nameMax: 'yearMax', label: 'Year Range', min: 1900, max: 2024 }
 * - { type: 'select', name: 'status', label: 'Status', options: [{ id: 1, label: 'Active' }], getOptionLabel: (opt) => opt.label, getOptionValue: (opt) => opt.id }
 * - { type: 'multiselect', name: 'authorIds', label: 'Authors', options: [...], getOptionLabel: (opt) => `${opt.firstName} ${opt.lastName}`, getOptionValue: (opt) => opt.id }
 */
const Filter = ({
    filters,
    updateFilter,
    toggleArrayFilter,
    onApply,
    onReset,
    hasActiveFilters,
    activeFilterCount = 0,
    loading = false,
    fields = [],
}) => {
    const [isExpanded, setIsExpanded] = useState(false);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        updateFilter(name, value);
    };

    const handleApply = () => {
        onApply();
        setIsExpanded(false);
    };

    const handleReset = () => {
        onReset();
    };

    const inputStyle = {
        ...styles.input,
        padding: '10px 14px',
        fontSize: '14px',
    };

    const labelStyle = {
        ...styles.label,
        fontSize: '13px',
        marginBottom: '6px',
    };

    const sectionStyle = {
        marginBottom: '20px',
    };

    const rangeContainerStyle = {
        display: 'flex',
        gap: '12px',
        alignItems: 'center',
    };

    const rangeInputStyle = {
        ...inputStyle,
        flex: 1,
    };

    const checkboxListStyle = {
        maxHeight: '150px',
        overflowY: 'auto',
        border: '1px solid #e5e5e7',
        borderRadius: '10px',
        padding: '8px',
    };

    const checkboxItemStyle = {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        padding: '8px 10px',
        cursor: 'pointer',
        borderRadius: '6px',
        transition: 'background 0.2s',
        fontSize: '14px',
    };

    const renderTextField = (field) => (
        <div key={field.name} style={sectionStyle}>
            <label style={labelStyle}>{field.label}</label>
            <input
                type="text"
                name={field.name}
                value={filters[field.name] || ''}
                onChange={handleInputChange}
                placeholder={field.placeholder || ''}
                style={inputStyle}
            />
        </div>
    );

    const renderNumberField = (field) => (
        <div key={field.name} style={sectionStyle}>
            <label style={labelStyle}>{field.label}</label>
            <input
                type="number"
                name={field.name}
                value={filters[field.name] || ''}
                onChange={handleInputChange}
                placeholder={field.placeholder || ''}
                min={field.min}
                max={field.max}
                style={inputStyle}
            />
        </div>
    );

    const renderRangeField = (field) => (
        <div key={`${field.nameMin}-${field.nameMax}`} style={sectionStyle}>
            <label style={labelStyle}>{field.label}</label>
            <div style={rangeContainerStyle}>
                <input
                    type="number"
                    name={field.nameMin}
                    value={filters[field.nameMin] || ''}
                    onChange={handleInputChange}
                    placeholder={field.placeholderMin || 'From'}
                    min={field.min}
                    max={field.max}
                    style={rangeInputStyle}
                />
                <span style={{ color: '#6e6e73' }}>to</span>
                <input
                    type="number"
                    name={field.nameMax}
                    value={filters[field.nameMax] || ''}
                    onChange={handleInputChange}
                    placeholder={field.placeholderMax || 'To'}
                    min={field.min}
                    max={field.max}
                    style={rangeInputStyle}
                />
            </div>
        </div>
    );

    const renderSelectField = (field) => {
        const getLabel = field.getOptionLabel || ((opt) => opt.label || opt.name || String(opt));
        const getValue = field.getOptionValue || ((opt) => opt.id || opt.value || opt);

        return (
            <div key={field.name} style={sectionStyle}>
                <label style={labelStyle}>{field.label}</label>
                <select
                    name={field.name}
                    value={filters[field.name] || ''}
                    onChange={handleInputChange}
                    style={{ ...inputStyle, cursor: 'pointer' }}
                >
                    <option value="">{field.placeholder || 'Select...'}</option>
                    {(field.options || []).map((option) => (
                        <option key={getValue(option)} value={getValue(option)}>
                            {getLabel(option)}
                        </option>
                    ))}
                </select>
            </div>
        );
    };

    const renderMultiselectField = (field) => {
        const options = field.options || [];
        const getLabel = field.getOptionLabel || ((opt) => opt.label || opt.name || String(opt));
        const getValue = field.getOptionValue || ((opt) => opt.id || opt.value || opt);
        const selectedValues = filters[field.name] || [];

        if (options.length === 0) return null;

        return (
            <div key={field.name} style={sectionStyle}>
                <label style={labelStyle}>
                    {field.label} {selectedValues.length > 0 && `(${selectedValues.length} selected)`}
                </label>
                <div style={checkboxListStyle}>
                    {options.map((option) => {
                        const value = getValue(option);
                        const isSelected = selectedValues.includes(value);
                        return (
                            <div
                                key={value}
                                style={{
                                    ...checkboxItemStyle,
                                    ...(isSelected ? { background: '#f5f5f7' } : {}),
                                }}
                                onClick={() => toggleArrayFilter(field.name, value)}
                                onMouseEnter={(e) => {
                                    if (!isSelected) e.currentTarget.style.background = '#f5f5f7';
                                }}
                                onMouseLeave={(e) => {
                                    if (!isSelected) e.currentTarget.style.background = 'transparent';
                                }}
                            >
                                <input
                                    type="checkbox"
                                    checked={isSelected}
                                    onChange={() => {}}
                                    style={{ ...styles.checkbox, cursor: 'pointer' }}
                                />
                                <span>{getLabel(option)}</span>
                            </div>
                        );
                    })}
                </div>
            </div>
        );
    };

    const renderField = (field) => {
        switch (field.type) {
            case 'text':
                return renderTextField(field);
            case 'number':
                return renderNumberField(field);
            case 'range':
                return renderRangeField(field);
            case 'select':
                return renderSelectField(field);
            case 'multiselect':
                return renderMultiselectField(field);
            default:
                return null;
        }
    };

    return (
        <div style={{ marginBottom: '24px' }}>
            {/* Filter Toggle Button */}
            <div style={{ display: 'flex', gap: '12px', alignItems: 'center', marginBottom: isExpanded ? '20px' : '0' }}>
                <Button
                    type={activeFilterCount > 0 ? 'filter-active' : 'filter'}
                    onClick={() => setIsExpanded(!isExpanded)}
                >
                    <svg
                        width="16"
                        height="16"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    >
                        <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
                    </svg>
                    Filters {activeFilterCount > 0 && `(${activeFilterCount})`}
                </Button>

                {activeFilterCount > 0 && !isExpanded && (
                    <Button type="link" onClick={handleReset}>
                        Clear all
                    </Button>
                )}
            </div>

            {/* Expanded Filter Panel */}
            {isExpanded && (
                <div
                    style={{
                        background: '#fff',
                        border: '1px solid #e5e5e7',
                        borderRadius: '12px',
                        padding: '24px',
                        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.04)',
                    }}
                >
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '24px' }}>
                        {fields.map(renderField)}
                    </div>

                    {/* Action Buttons */}
                    <div
                        style={{
                            display: 'flex',
                            gap: '12px',
                            justifyContent: 'flex-end',
                            paddingTop: '20px',
                            borderTop: '1px solid #e5e5e7',
                            marginTop: '8px',
                        }}
                    >
                        <Button type="modal-secondary" onClick={handleReset} disabled={!hasActiveFilters}>
                            Reset
                        </Button>
                        <Button type="modal-primary" onClick={handleApply} disabled={loading}>
                            {loading ? 'Applying...' : 'Apply Filters'}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Filter;
