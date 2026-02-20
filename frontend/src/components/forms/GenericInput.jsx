import { styles } from "../../styles/styles";
import { useState, useEffect } from "react";

const GenericInput = ({
  label,
  name,
  value,
  onChange,
  onBlur,
  placeholder,
  error,
  required = false,
  type = "text",
  min,
  max,
  rows = 4,
  maxLength,
  showCharCount = false,
  style: customStyle = {},
  accept,
  preview: externalPreview,
  onFilePreview,
  readOnly = false,
  size = "default", // default, compact
  options = [], // for select type: [{ value: '', label: '' }]
  children, // alternative way to pass options for select
}) => {
  const [preview, setPreview] = useState(null);

  // Size variants
  const sizeStyles = {
    default: {
      padding: '14px 16px',
      fontSize: '15px',
    },
    compact: {
      padding: '12px 24px',
      fontSize: '15px',
      fontWeight: '500',
    },
  };

  useEffect(() => {
    if (externalPreview) {
      setPreview(externalPreview);
    }
  }, [externalPreview]);

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];

    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreview(reader.result);

        if (onFilePreview) {
          onFilePreview(reader.result);
        }
      };
      reader.readAsDataURL(file);

      if (onChange) {
        onChange(e);
      }
    }
  };

  const formGroupStyle = {
    ...styles.formGroup,
    ...(size === "compact" && { marginBottom: 0 }),
  };

  if (type === "file") {
    return (
      <div style={formGroupStyle}>
        {label && (
          <label style={styles.label}>
            {label} {required && <span style={styles.required}>*</span>}
          </label>
        )}

        <input
          type="file"
          name={name}
          onChange={handleFileChange}
          onBlur={onBlur}
          accept={accept || "image/*"}
          style={{
            ...styles.input,
            ...sizeStyles[size],
            ...(error && styles.inputError),
            ...customStyle,
          }}
        />

        {(preview || externalPreview) && (
          <div style={{ marginTop: "12px" }}>
            <img
              src={preview || externalPreview}
              alt="Preview"
              style={{
                maxWidth: "200px",
                maxHeight: "200px",
                objectFit: "contain",
                border: "1px solid #e5e5e7",
                borderRadius: "8px",
                padding: "8px",
              }}
            />
          </div>
        )}

        {error && <div style={styles.errorMessage}>{error}</div>}
      </div>
    );
  }

  if (type === "textarea") {
    return (
      <div style={formGroupStyle}>
        {label && (
          <label style={styles.label}>
            {label} {required && <span style={styles.required}>*</span>}
          </label>
        )}

        <textarea
          name={name}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          placeholder={placeholder}
          rows={rows}
          maxLength={maxLength}
          readOnly={readOnly}
          style={{
            ...styles.textarea,
            ...sizeStyles[size],
            ...(error && styles.inputError),
            ...customStyle,
          }}
        />

        {showCharCount && (
          <div style={styles.charCount}>
            {value?.length || 0} {maxLength && `/ ${maxLength}`} characters
          </div>
        )}

        {error && <div style={styles.errorMessage}>{error}</div>}
      </div>
    );
  }

  if (type === "select") {
    return (
      <div style={formGroupStyle}>
        {label && (
          <label style={styles.label}>
            {label} {required && <span style={styles.required}>*</span>}
          </label>
        )}

        <select
          name={name}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          disabled={readOnly}
          style={{
            ...styles.input,
            ...sizeStyles[size],
            ...(error && styles.inputError),
            ...customStyle,
            cursor: 'pointer',
          }}
        >
          {children || options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>

        {error && <div style={styles.errorMessage}>{error}</div>}
      </div>
    );
  }

  return (
    <div style={formGroupStyle}>
      {label && (
        <label style={styles.label}>
          {label} {required && <span style={styles.required}>*</span>}
        </label>
      )}

      <input
        type={type}
        name={name}
        value={value}
        onChange={onChange}
        onBlur={onBlur}
        placeholder={placeholder}
        min={min}
        max={max}
        maxLength={maxLength}
        required={required}
        readOnly={readOnly}
        style={{
          ...styles.input,
          ...sizeStyles[size],
          ...(error && styles.inputError),
          ...customStyle,
        }}
      />

      {error && <div style={styles.errorMessage}>{error}</div>}
    </div>
  );
};

export default GenericInput;
