import React, { useState, useMemo } from "react";
import Header from "../../components/layout/Header";
import Footer from "../../components/layout/Footer";
import Button from "../../components/common/Button";
import Toast from "../../components/common/Toast";
import LoadingSpinner from "../../components/common/LoadingSpinner";
import EmptyState from "../../components/common/EmptyState";
import PageHeader from "../../components/layout/PageHeader";
import { useInventory } from "../../features/inventory/hooks/useInventory";
import InventoryBookItem from "../../features/inventory/components/InventoryBookItem";
import AddInventoryModal from "../../features/inventory/components/modals/AddInventoryModal";
import EditInventoryModal from "../../features/inventory/components/modals/EditInventoryModal";
import DeleteInventoryModal from "../../features/inventory/components/modals/DeleteInventoryModal";

export default function InventoryPage() {
    const {
        inventoryItems,
        totalInventoryCount,
        availableBooks,
        loading,
        error,
        toast,
        search,
        sortBy,
        hasFilters,
        setSearch,
        setSortBy,
        addToInventory,
        updateInventoryItem,
        removeFromInventory,
        hideToast,
        clearFilters,
    } = useInventory();

    // Modal states
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [editItem, setEditItem] = useState(null);
    const [deleteItem, setDeleteItem] = useState(null);

    // Calculate totals
    const totals = useMemo(() => {
        const totalItems = inventoryItems.length;
        const totalStock = inventoryItems.reduce((sum, item) => sum + (item.quantity || 0), 0);
        const totalValue = inventoryItems.reduce(
            (sum, item) => sum + ((item.quantity || 0) * (item.pricePerUnit || 0)),
            0
        );
        return { totalItems, totalStock, totalValue };
    }, [inventoryItems]);

    if (loading) {
        return (
            <div className="min-h-screen bg-white flex flex-col">
                <Header />
                <div className="flex-1">
                    <LoadingSpinner message="Loading inventory..." />
                </div>
                <Footer />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-white flex flex-col">
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={hideToast}
                />
            )}

            <Header />

            <main className="flex-1 max-w-[1200px] mx-auto w-full py-16 px-4 sm:px-6 lg:px-8">
                {/* Header */}
                <div className="flex flex-col md:flex-row md:items-start md:justify-between gap-4 mb-8">
                    <PageHeader
                        title="Your Inventory"
                        description="Manage your book stock and pricing"
                    />
                    <Button
                        type="secondary"
                        label="Add Book"
                        onClick={() => setIsAddModalOpen(true)}
                    />
                </div>

                {/* Stats Cards */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
                    <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                        <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Total Books</p>
                        <p className="text-[32px] font-semibold text-[#1d1d1f]">{totals.totalItems}</p>
                    </div>
                    <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                        <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Total Stock</p>
                        <p className="text-[32px] font-semibold text-[#1d1d1f]">{totals.totalStock}</p>
                    </div>
                    <div className="bg-white rounded-[16px] p-6 border border-[#e5e5e7]">
                        <p className="text-[13px] text-[#6e6e73] uppercase tracking-wide mb-1">Total Value</p>
                        <p className="text-[32px] font-semibold text-[#0071e3]">€{totals.totalValue.toFixed(2)}</p>
                    </div>
                </div>

                {/* Filters */}
                {totalInventoryCount > 0 && (
                    <div className="bg-white rounded-[16px] p-4 border border-[#e5e5e7] mb-6">
                        <div className="flex flex-wrap items-center gap-3">
                            {/* Search */}
                            <div className="relative flex-1 min-w-[200px] max-w-[320px]">
                                <svg
                                    xmlns="http://www.w3.org/2000/svg"
                                    viewBox="0 0 24 24"
                                    fill="none"
                                    stroke="currentColor"
                                    strokeWidth="2"
                                    className="w-5 h-5 text-[#6e6e73] absolute left-4 top-1/2 -translate-y-1/2"
                                >
                                    <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
                                </svg>
                                <input
                                    type="text"
                                    placeholder="Search inventory..."
                                    value={search}
                                    onChange={(e) => setSearch(e.target.value)}
                                    className="w-full pl-12 pr-4 py-2.5 rounded-[10px] border border-[#e5e5e7] bg-[#f5f5f7] text-[15px] focus:border-[#0071e3] focus:bg-white focus:outline-none transition-colors"
                                />
                            </div>

                            {/* Sort By */}
                            <select
                                value={sortBy}
                                onChange={(e) => setSortBy(e.target.value)}
                                className="px-4 py-2.5 rounded-[10px] border border-[#e5e5e7] bg-[#f5f5f7] text-[15px] focus:border-[#0071e3] focus:outline-none transition-colors cursor-pointer"
                            >
                                <option value="title">Sort by Title</option>
                                <option value="price">Sort by Price</option>
                                <option value="quantity">Sort by Stock</option>
                            </select>

                            {/* Clear Filters */}
                            {hasFilters && (
                                <button
                                    onClick={clearFilters}
                                    className="px-4 py-2.5 rounded-[10px] border border-[#e5e5e7] text-[15px] hover:bg-[#f5f5f7] transition-colors"
                                >
                                    Clear
                                </button>
                            )}
                        </div>
                    </div>
                )}

                {/* Error State */}
                {error && (
                    <div className="bg-red-50 border border-red-200 rounded-[16px] p-6 text-center mb-6">
                        <p className="text-red-600">{error}</p>
                    </div>
                )}

                {/* Inventory List */}
                {inventoryItems.length > 0 ? (
                    <div className="bg-white rounded-[16px] border border-[#e5e5e7] overflow-hidden divide-y divide-[#e5e5e7]">
                        {inventoryItems.map((item) => (
                            <InventoryBookItem
                                key={item.book?.isbn}
                                item={item}
                                onEdit={setEditItem}
                                onDelete={setDeleteItem}
                            />
                        ))}
                    </div>
                ) : totalInventoryCount > 0 ? (
                    /* No results from search */
                    <div className="bg-white rounded-[16px] border border-[#e5e5e7] p-12 text-center">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-12 h-12 text-[#d2d2d7] mx-auto mb-4">
                            <path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
                        </svg>
                        <h3 className="text-[18px] font-semibold text-[#1d1d1f] mb-2">No results found</h3>
                        <p className="text-[15px] text-[#6e6e73] mb-4">
                            No books match "{search}"
                        </p>
                        <button
                            onClick={clearFilters}
                            className="px-6 py-2.5 bg-[#1d1d1f] text-white rounded-[10px] text-[15px] font-medium hover:bg-black transition-colors"
                        >
                            Clear Search
                        </button>
                    </div>
                ) : (
                    /* Empty inventory */
                    <EmptyState
                        title="No books in inventory"
                        description="Start by adding books to your inventory to manage stock and pricing."
                        action={
                            <Button
                                type="primary"
                                onClick={() => setIsAddModalOpen(true)}
                            >
                                Add Your First Book
                            </Button>
                        }
                    />
                )}
            </main>

            <Footer />

            {/* Modals */}
            <AddInventoryModal
                isOpen={isAddModalOpen}
                onClose={() => setIsAddModalOpen(false)}
                availableBooks={availableBooks}
                onAdd={addToInventory}
            />

            <EditInventoryModal
                isOpen={!!editItem}
                onClose={() => setEditItem(null)}
                item={editItem}
                onUpdate={updateInventoryItem}
            />

            <DeleteInventoryModal
                isOpen={!!deleteItem}
                onClose={() => setDeleteItem(null)}
                item={deleteItem}
                onDelete={removeFromInventory}
            />
        </div>
    );
}
