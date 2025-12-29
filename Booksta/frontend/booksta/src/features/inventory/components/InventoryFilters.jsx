import React from "react";

export default function InventoryFilters({
    search,
    setSearch,
    genre,
    setGenre,
    year,
    setYear,
    genres,
    years,
    showOnlyMyStock,
    toggleShowOnlyMyStock,
    hasFilters,
    clearFilters,
}) {
    return (
        <section className="bg-white rounded-2xl shadow-sm border border-[#e5e5e7] p-6 mb-8">
            <div className="mb-6">
                <button
                    type="button"
                    onClick={toggleShowOnlyMyStock}
                    className={`inline-flex items-center gap-2 px-4 py-2 rounded-full border text-[14px] transition
                        ${showOnlyMyStock ? "bg-black text-white border-black" : "bg-white text-[#1d1d1f] border-[#d2d2d7] hover:bg-[#f5f5f7]"}`}
                >
                    <span className={`inline-block w-2 h-2 rounded-full ${showOnlyMyStock ? "bg-green-400" : "bg-[#d2d2d7]"}`}></span>
                    Show only my stock
                </button>
            </div>

            <div className="flex flex-wrap justify-center gap-3 mb-2">
                <input
                    type="text"
                    placeholder="Search by title or author"
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="px-4 py-3 w-[260px] rounded-xl border border-[#e5e5e7] text-[15px]"
                />
                <select
                    value={genre}
                    onChange={(e) => setGenre(e.target.value)}
                    className="px-4 py-3 rounded-xl border border-[#e5e5e7] text-[15px]"
                >
                    <option value="">All genres</option>
                    {genres.map((g) => <option key={g} value={g}>{g}</option>)}
                </select>

                <select
                    value={year}
                    onChange={(e) => setYear(e.target.value)}
                    className="px-4 py-3 rounded-xl border border-[#e5e5e7] text-[15px]"
                >
                    <option value="">All years</option>
                    {years.map((y) => <option key={y} value={y}>{y}</option>)}
                </select>

                {hasFilters && (
                    <button
                        onClick={clearFilters}
                        className="px-4 py-3 rounded-xl border border-[#e5e5e7] text-[15px] hover:bg-[#f5f5f7]"
                    >
                        Clear filters
                    </button>
                )}
            </div>
        </section>
    );
}
