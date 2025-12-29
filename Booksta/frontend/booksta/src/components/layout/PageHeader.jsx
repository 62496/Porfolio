import React from "react";

export default function PageHeader({
  title,
  description,
  action,
  className = ""
}) {
  return (
    <div className={`flex items-center justify-between mb-12 min-h-[120px] ${className}`}>
      <div className="flex-1">
        <h1 className="text-[48px] font-semibold tracking-[-0.02em] text-[#1d1d1f] mb-2">
          {title}
        </h1>
        {description && (
          <p className="text-[21px] text-[#6e6e73]">
            {description}
          </p>
        )}
      </div>

      {action && (
        <div className="ml-6 flex-shrink-0">
          {action}
        </div>
      )}
    </div>
  );
}
