import React, { useState, useRef, useEffect } from "react";

export default function FeatureCard({ icon, title, description, delay }) {
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const ob = new IntersectionObserver(
      ([entry]) => entry.isIntersecting && setIsVisible(true),
      { threshold: 0.15 }
    );
    if (ref.current) ob.observe(ref.current);
    return () => ref.current && ob.unobserve(ref.current);
  }, []);

  const renderIcon = () => {
    switch (icon) {
      case 'search':
        return (
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-11 h-11 text-white">
            <path d="M10 2a8 8 0 105.293 14.707l4.5 4.5a1 1 0 001.414-1.414l-4.5-4.5A8 8 0 0010 2zm0 2a6 6 0 110 12 6 6 0 010-12z"/>
          </svg>
        );
      case 'catalog':
        return (
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-11 h-11 text-white">
            <path d="M6 2a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8l-6-6H6zm0 2h7v5h5v11H6V4zm2 8v2h8v-2H8zm0 4v2h5v-2H8z"/>
          </svg>
        );
      case 'discovery':
        return (
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-11 h-11 text-white">
            <path d="M12 2a1 1 0 011 1v1.09A7.002 7.002 0 0119 11a7 7 0 01-6 6.92V21h3a1 1 0 110 2H8a1 1 0 110-2h3v-3.08A7.002 7.002 0 015 11a7.002 7.002 0 016-6.92V3a1 1 0 011-1zm0 4a5 5 0 100 10 5 5 0 000-10zm0 2a3 3 0 110 6 3 3 0 010-6z"/>
          </svg>
        );
      default:
        return <span className="text-[40px] text-white">{icon}</span>;
    }
  };

  return (
    <div
      ref={ref}
      className={`
        bg-white border border-[#e5e5e7] rounded-[18px]
        p-12 px-8 text-center
        hover:border-[#d1d1d6] hover:shadow-lg
        transition-all duration-300
        stagger ${isVisible ? "visible" : ""}
      `}
      style={{ transitionDelay: `${delay * 0.1}s` }}
    >
      <div className="w-[80px] h-[80px] bg-gradient-to-br from-[#1d1d1f] to-[#3a3a3c] rounded-[16px] flex items-center justify-center mx-auto mb-6 shadow-md transform hover:scale-110 transition-transform duration-300">
        {renderIcon()}
      </div>

      <h3 className="text-[24px] font-semibold text-[#1d1d1f] mb-3 tracking-[-0.015em]">
        {title}
      </h3>

      <p className="text-[17px] text-[#6e6e73] leading-[1.47]">
        {description}
      </p>
    </div>
  );
}
