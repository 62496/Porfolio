import React from "react";

export default function Footer() {
  return (
    <footer className="bg-white border-t border-[#e5e5e7] py-10 px-5">
      <div className="max-w-[1200px] mx-auto pt-8 flex justify-between text-[13px] text-[#6e6e73]">
        <div>© {new Date().getFullYear()} Booksta. All rights reserved.</div>
        <div>Made with love for book lovers</div>
      </div>
    </footer>
  );
}
