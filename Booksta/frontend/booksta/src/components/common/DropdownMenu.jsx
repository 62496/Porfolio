import React, { useState, useEffect, useRef } from "react";
import Button from "./Button";

export default function DropdownMenu({ trigger, items, className = "" }) {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  const handleItemClick = (onClick) => {
    if (onClick) {
      onClick();
    }
    setIsOpen(false);
  };

  return (
    <div ref={menuRef} className={className} onClick={(e) => e.stopPropagation()}>
      {/* Trigger Button */}
      <div onClick={() => setIsOpen(!isOpen)}>
        {trigger}
      </div>

      {/* Dropdown Menu */}
      {isOpen && (
        <div className="absolute top-14 left-0 bg-white rounded-[12px] shadow-lg border border-[#e5e5e7] overflow-hidden min-w-[160px] z-50">
          {items.map((item, index) => {
            if (!item.visible && item.visible !== undefined) return null;

            // Render separator
            if (item.type === 'separator') {
              return (
                <div key={index} className="border-t border-[#e5e5e7] mx-2" />
              );
            }

            return (
              <Button
                key={index}
                type="dropdown-item"
                onClick={() => handleItemClick(item.onClick)}
                className={`flex items-center gap-3 ${item.className || ""}`}
              >
                {item.icon && (
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    className="w-5 h-5 flex-shrink-0"
                  >
                    {item.icon}
                  </svg>
                )}
                <span className="flex-1 text-left">{item.label}</span>
              </Button>
            );
          })}
        </div>
      )}
    </div>
  );
}
