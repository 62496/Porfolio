import React from "react";
import { Link } from "react-router-dom";

export default function Button({ label, type = "primary", onClick, href, disabled = false, className: extraClassName = "", children }) {

  const baseClasses =
    type === "primary"
      ? "flex items-center gap-2 px-8 py-3 bg-[#1d1d1f] text-white rounded-xl text-[15px] font-medium hover:bg-[#424245] transition-colors border border-transparent"
      : type === "secondary"
      ? "flex items-center gap-2 px-8 py-3 border border-[#1d1d1f] text-[#1d1d1f] rounded-xl text-[15px] font-medium hover:bg-[#f5f5f7] transition-colors"
      : type === "signup"
      ? "flex items-center gap-2 px-6 py-2.5 bg-[#1d1d1f] text-white rounded-xl text-[15px] font-medium hover:bg-[#424245] transition-colors"
      : type === "signin"
      ? "flex items-center gap-2 px-6 py-2.5 border border-[#1d1d1f] text-[#1d1d1f] rounded-xl text-[15px] font-medium hover:bg-[#f5f5f7] transition-colors"
      : type === "danger"
      ? "flex items-center gap-2 px-8 py-3 bg-red-600 text-white rounded-xl text-[15px] font-medium hover:bg-red-700 transition-colors"
      : type === "filter"
      ? "flex items-center gap-2 px-5 py-2.5 rounded-lg text-[14px] font-medium transition-colors border border-[#e5e5e7] text-[#1d1d1f] hover:border-[#1d1d1f] hover:bg-[#f5f5f7]"
      : type === "filter-active"
      ? "flex items-center gap-2 px-5 py-2.5 rounded-lg text-[14px] font-medium transition-colors bg-[#1d1d1f] text-white"
      : type === "small-secondary"
      ? "flex items-center gap-2 px-4 py-2 border border-[#e5e5e7] text-[#1d1d1f] rounded-lg text-[13px] font-medium hover:border-[#1d1d1f] hover:bg-[#f5f5f7] transition-colors"
      : type === "small-danger"
      ? "flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg text-[13px] font-medium hover:bg-red-700 transition-colors"
      : type === "modal-primary"
      ? "flex items-center gap-2 px-6 py-3 bg-[#1d1d1f] text-white rounded-xl text-[15px] font-medium hover:bg-[#424245] transition-colors"
      : type === "modal-secondary"
      ? "flex items-center gap-2 px-6 py-3 border border-[#e5e5e7] text-[#1d1d1f] rounded-xl text-[15px] font-medium hover:border-[#1d1d1f] hover:bg-[#f5f5f7] transition-colors"
      : type === "modal-danger"
      ? "flex items-center gap-2 px-6 py-3 bg-red-600 text-white rounded-xl text-[15px] font-medium hover:bg-red-700 transition-colors"
      : type === "modal-success"
      ? "flex items-center gap-2 px-6 py-3 bg-green-600 text-white rounded-xl text-[15px] font-medium hover:bg-green-700 transition-colors"
      : type === "small-success"
      ? "flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg text-[13px] font-medium hover:bg-green-700 transition-colors"
      : type === "warning"
      ? "flex items-center gap-2 px-6 py-3 border-2 border-orange-500 text-orange-600 rounded-xl text-[15px] font-medium hover:bg-orange-500 hover:text-white transition-all"
      : type === "link"
      ? "text-[15px] font-medium text-[#1d1d1f] hover:text-[#6e6e73] transition-colors underline"
      : type === "dropdown-item"
      ? "w-full px-4 py-2 text-left text-[15px] text-[#1d1d1f] hover:bg-[#f5f5f7] transition-colors rounded-lg"
      // New icon-enhanced button variants
      : type === "icon-primary"
      ? "flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl text-[15px] font-medium hover:bg-blue-700 transition-colors"
      : type === "icon-pause"
      ? "flex items-center gap-2 px-6 py-3 bg-orange-50 text-orange-700 rounded-xl text-[15px] font-medium hover:bg-orange-100 transition-colors border border-orange-200"
      : type === "icon-success"
      ? "flex items-center gap-2 px-6 py-3 bg-green-600 text-white rounded-xl text-[15px] font-medium hover:bg-green-700 transition-colors"
      : type === "icon-success-outline"
      ? "flex items-center gap-2 px-5 py-2.5 bg-green-50 text-green-700 rounded-lg text-[14px] font-medium hover:bg-green-100 transition-colors border border-green-200"
      : type === "icon-danger-outline"
      ? "flex items-center gap-2 px-5 py-2.5 bg-red-50 text-red-700 rounded-lg text-[14px] font-medium hover:bg-red-100 transition-colors border border-red-200"
      : type === "icon-purple-outline"
      ? "flex items-center gap-2 px-5 py-2.5 bg-purple-50 text-purple-700 rounded-lg text-[14px] font-medium hover:bg-purple-100 transition-colors border border-purple-200"
      : "";

  const disabledClasses = disabled ? "bg-[#e5e5e7] text-[#86868b] cursor-not-allowed hover:bg-[#e5e5e7] hover:border-[#e5e5e7]" : "";
  const shared = "cursor-pointer inline-flex items-center justify-center no-underline";
  const className = `${baseClasses} ${disabled ? disabledClasses : ""} ${shared} ${extraClassName}`.trim();

  if (href && href.startsWith("/")) {
    return (
      <Link to={href} className={className} onClick={onClick}>
        {children || label}
      </Link>
    );
  }
  if (href) {
    return (
      <a href={href} className={className} onClick={onClick}>
        {children || label}
      </a>
    );
  }

  return (
    <button onClick={onClick} className={className} disabled={disabled}>
      {children || label}
    </button>
  );
}
