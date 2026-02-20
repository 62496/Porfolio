import React, { useEffect, useState, useRef } from "react";

export default function Toast({ message, type = "success", onClose, duration = 3000 }) {
  const [progress, setProgress] = useState(100);
  const [isExiting, setIsExiting] = useState(false);
  const onCloseRef = useRef(onClose);

  // Keep onClose ref up to date
  useEffect(() => {
    onCloseRef.current = onClose;
  }, [onClose]);

  useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        setIsExiting(true);
        // Give time for exit animation before calling onClose
        setTimeout(() => {
          onCloseRef.current();
        }, 300);
      }, duration);

      // Update progress bar
      const interval = setInterval(() => {
        setProgress((prev) => {
          const decrement = (100 / duration) * 50; // Update every 50ms
          return Math.max(0, prev - decrement);
        });
      }, 50);

      return () => {
        clearTimeout(timer);
        clearInterval(interval);
      };
    }
  }, [duration]);

  const handleClose = () => {
    setIsExiting(true);
    setTimeout(() => {
      onCloseRef.current();
    }, 300);
  };

  const getStyles = () => {
    switch (type) {
      case "success":
        return {
          bg: "bg-white",
          border: "border-green-200",
          text: "text-[#1d1d1f]",
          icon: "text-green-600",
          iconBg: "bg-green-50",
          progressBg: "bg-green-600",
          icon: (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          )
        };
      case "error":
        return {
          bg: "bg-white",
          border: "border-red-200",
          text: "text-[#1d1d1f]",
          icon: "text-red-600",
          iconBg: "bg-red-50",
          progressBg: "bg-red-600",
          icon: (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          )
        };
      case "warning":
        return {
          bg: "bg-white",
          border: "border-yellow-200",
          text: "text-[#1d1d1f]",
          icon: "text-yellow-600",
          iconBg: "bg-yellow-50",
          progressBg: "bg-yellow-600",
          icon: (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          )
        };
      case "info":
        return {
          bg: "bg-white",
          border: "border-blue-200",
          text: "text-[#1d1d1f]",
          icon: "text-blue-600",
          iconBg: "bg-blue-50",
          progressBg: "bg-blue-600",
          icon: (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          )
        };
      default:
        return {
          bg: "bg-white",
          border: "border-[#e5e5e7]",
          text: "text-[#1d1d1f]",
          icon: "text-[#1d1d1f]",
          iconBg: "bg-[#f5f5f7]",
          progressBg: "bg-[#1d1d1f]",
          icon: (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          )
        };
    }
  };

  const styles = getStyles();

  return (
    <div
      className={`
        fixed top-24 right-6 z-50
        ${styles.bg} ${styles.text}
        rounded-xl border ${styles.border}
        shadow-xl
        max-w-md
        overflow-hidden
        transition-all duration-300 ease-out
        ${isExiting ? 'opacity-0 translate-x-full' : 'opacity-100 translate-x-0 animate-slide-in-right'}
      `}
    >
      <div className="px-4 py-3">
        <div className="flex items-start gap-3">
          {/* Icon */}
          <div className={`flex-shrink-0 w-8 h-8 rounded-full ${styles.iconBg} flex items-center justify-center ${styles.icon}`}>
            {styles.icon}
          </div>

          {/* Message */}
          <div className="flex-1 min-w-0 pt-0.5">
            <p className="text-[14px] font-medium leading-snug">{message}</p>
          </div>

          {/* Close Button */}
          <button
            onClick={handleClose}
            className="flex-shrink-0 text-[#6e6e73] hover:text-[#1d1d1f] transition-colors"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>

      {/* Progress Bar */}
      {duration > 0 && (
        <div className="h-1 bg-[#f5f5f7]">
          <div
            className={`h-full ${styles.progressBg} transition-all duration-50 ease-linear`}
            style={{ width: `${progress}%` }}
          />
        </div>
      )}
    </div>
  );
}
