import React from "react";

export default function LoadingSpinner({ message = "Loading...", size = "medium" }) {
  const sizeClasses = {
    small: "w-8 h-8 border-2",
    medium: "w-12 h-12 border-4",
    large: "w-16 h-16 border-4"
  };

  return (
    <div className="min-h-[400px] flex items-center justify-center">
      <div className="text-center">
        <div
          className={`${sizeClasses[size]} border-[#1d1d1f] border-t-transparent rounded-full animate-spin mx-auto mb-4`}
        />
        <p className="text-[17px] text-[#6e6e73]">{message}</p>
      </div>
    </div>
  );
}
