import React from "react";

export default function SectionHeader({
  title,
  description,
  align = "center",
  className = ""
}) {
  const alignmentClasses = {
    center: "text-center",
    left: "text-left",
    right: "text-right"
  };

  return (
    <div className={`mb-12 ${alignmentClasses[align]} ${className}`}>
      <h2 className="text-[48px] font-semibold tracking-[-0.02em] text-[#1d1d1f] mb-4">
        {title}
      </h2>
      {description && (
        <p className="text-[21px] text-[#6e6e73] max-w-[800px] mx-auto">
          {description}
        </p>
      )}
    </div>
  );
}
