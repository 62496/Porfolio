import React from "react";

export default function EmptyState({
  icon,
  title,
  description,
  action,
  className = ""
}) {
  return (
    <div className={`text-center py-20 ${className}`}>
      {icon && (
        <div className="mb-6 flex justify-center">
          {typeof icon === 'string' ? (
            <div className="text-[64px]">{icon}</div>
          ) : (
            icon
          )}
        </div>
      )}

      <h3 className="text-[28px] font-semibold text-[#1d1d1f] mb-3">
        {title}
      </h3>

      {description && (
        <p className="text-[17px] text-[#6e6e73] mb-6 max-w-[600px] mx-auto">
          {description}
        </p>
      )}

      {action && (
        <div className="mt-6">
          {action}
        </div>
      )}
    </div>
  );
}
