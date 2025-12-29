import React from "react";

export default function Badge({ label, type = "default", className = "" }) {
  const typeStyles = {
    default: "bg-[#f5f5f7] text-[#1d1d1f]",
    primary: "bg-blue-100 text-blue-800",
    success: "bg-green-100 text-green-800",
    danger: "bg-red-100 text-red-800",
    warning: "bg-orange-100 text-orange-800",
    info: "bg-blue-100 text-blue-800"
  };

  return (
    <span
      className={`inline-block px-3 py-1 rounded-full text-[13px] font-medium ${
        typeStyles[type] || typeStyles.default
      } ${className}`}
    >
      {label}
    </span>
  );
}
