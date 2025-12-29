import React, { useState, useEffect } from 'react';
import Modal from '../../../components/common/Modal';
import Button from '../../../components/common/Button';
import GenericInput from '../../../components/forms/GenericInput';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import subjectService from '../../../api/services/subjectService';

export default function SubjectManagementModal({ isOpen, onClose }) {
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [newSubjectName, setNewSubjectName] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [editingName, setEditingName] = useState('');
    const [saving, setSaving] = useState(false);
    const [deleteConfirm, setDeleteConfirm] = useState(null);

    useEffect(() => {
        if (isOpen) {
            loadSubjects();
        }
    }, [isOpen]);

    const loadSubjects = async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await subjectService.getAll();
            setSubjects(data);
        } catch {
            setError('Failed to load subjects');
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async () => {
        if (!newSubjectName.trim()) return;

        try {
            setSaving(true);
            const created = await subjectService.create({ name: newSubjectName.trim() });
            setSubjects(prev => [...prev, created].sort((a, b) => a.name.localeCompare(b.name)));
            setNewSubjectName('');
        } catch {
            setError('Failed to create subject');
        } finally {
            setSaving(false);
        }
    };

    const handleUpdate = async (id) => {
        if (!editingName.trim()) return;

        try {
            setSaving(true);
            const updated = await subjectService.update(id, { name: editingName.trim() });
            setSubjects(prev => prev.map(s => s.id === id ? updated : s).sort((a, b) => a.name.localeCompare(b.name)));
            setEditingId(null);
            setEditingName('');
        } catch {
            setError('Failed to update subject');
        } finally {
            setSaving(false);
        }
    };

    const handleDeleteClick = (subject) => {
        setDeleteConfirm(subject);
    };

    const confirmDelete = async () => {
        if (!deleteConfirm) return;

        try {
            setSaving(true);
            await subjectService.delete(deleteConfirm.id);
            setSubjects(prev => prev.filter(s => s.id !== deleteConfirm.id));
            setDeleteConfirm(null);
        } catch {
            setError('Failed to delete subject. It may be in use by books.');
        } finally {
            setSaving(false);
        }
    };

    const cancelDelete = () => {
        setDeleteConfirm(null);
    };

    const startEditing = (subject) => {
        setEditingId(subject.id);
        setEditingName(subject.name);
    };

    const cancelEditing = () => {
        setEditingId(null);
        setEditingName('');
    };

    return (
        <Modal isOpen={isOpen} onClose={onClose} title="Manage Subjects">
            <div className="space-y-6">
                {/* Add New Subject */}
                <div>
                    <h4 className="text-[14px] font-semibold text-[#1d1d1f] mb-3">
                        Add New Subject
                    </h4>
                    <div className="flex gap-3">
                        <div className="flex-1">
                            <GenericInput
                                name="newSubject"
                                value={newSubjectName}
                                onChange={(e) => setNewSubjectName(e.target.value)}
                                placeholder="Enter subject name..."
                                size="compact"
                                onKeyDown={(e) => e.key === 'Enter' && handleCreate()}
                            />
                        </div>
                        <Button
                            type="primary"
                            onClick={handleCreate}
                            disabled={!newSubjectName.trim() || saving}
                            className="!px-4 !py-2"
                        >
                            {saving ? '...' : 'Add'}
                        </Button>
                    </div>
                </div>

                {/* Error */}
                {error && (
                    <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
                        {error}
                        <button
                            onClick={() => setError(null)}
                            className="ml-2 underline"
                        >
                            Dismiss
                        </button>
                    </div>
                )}

                {/* Subjects List */}
                <div>
                    <h4 className="text-[14px] font-semibold text-[#1d1d1f] mb-3">
                        Existing Subjects ({subjects.length})
                    </h4>

                    {loading ? (
                        <LoadingSpinner message="Loading subjects..." />
                    ) : subjects.length > 0 ? (
                        <div className="max-h-[300px] overflow-y-auto space-y-2">
                            {subjects.map(subject => (
                                <div
                                    key={subject.id}
                                    className="flex items-center gap-3 p-3 bg-[#f5f5f7] rounded-[10px]"
                                >
                                    {editingId === subject.id ? (
                                        <>
                                            <div className="flex-1">
                                                <input
                                                    type="text"
                                                    value={editingName}
                                                    onChange={(e) => setEditingName(e.target.value)}
                                                    className="w-full px-3 py-2 rounded-lg border border-[#e5e5e7] text-[14px] focus:border-[#0071e3] focus:outline-none"
                                                    onKeyDown={(e) => {
                                                        if (e.key === 'Enter') handleUpdate(subject.id);
                                                        if (e.key === 'Escape') cancelEditing();
                                                    }}
                                                    autoFocus
                                                />
                                            </div>
                                            <Button
                                                type="small-success"
                                                onClick={() => handleUpdate(subject.id)}
                                                disabled={saving}
                                            >
                                                Save
                                            </Button>
                                            <Button
                                                type="small-secondary"
                                                onClick={cancelEditing}
                                            >
                                                Cancel
                                            </Button>
                                        </>
                                    ) : (
                                        <>
                                            <span className="flex-1 text-[14px] text-[#1d1d1f]">
                                                {subject.name}
                                            </span>
                                            <button
                                                onClick={() => startEditing(subject)}
                                                className="p-1.5 hover:bg-[#e5e5e7] rounded-lg transition-colors"
                                                title="Edit"
                                            >
                                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 text-[#6e6e73]">
                                                    <path d="M2.695 14.763l-1.262 3.154a.5.5 0 00.65.65l3.155-1.262a4 4 0 001.343-.885L17.5 5.5a2.121 2.121 0 00-3-3L3.58 13.42a4 4 0 00-.885 1.343z" />
                                                </svg>
                                            </button>
                                            <button
                                                onClick={() => handleDeleteClick(subject)}
                                                className="p-1.5 hover:bg-red-100 rounded-lg transition-colors"
                                                title="Delete"
                                                disabled={saving}
                                            >
                                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" className="w-4 h-4 text-red-500">
                                                    <path fillRule="evenodd" d="M8.75 1A2.75 2.75 0 006 3.75v.443c-.795.077-1.584.176-2.365.298a.75.75 0 10.23 1.482l.149-.022.841 10.518A2.75 2.75 0 007.596 19h4.807a2.75 2.75 0 002.742-2.53l.841-10.519.149.023a.75.75 0 00.23-1.482A41.03 41.03 0 0014 4.193V3.75A2.75 2.75 0 0011.25 1h-2.5zM10 4c.84 0 1.673.025 2.5.075V3.75c0-.69-.56-1.25-1.25-1.25h-2.5c-.69 0-1.25.56-1.25 1.25v.325C8.327 4.025 9.16 4 10 4zM8.58 7.72a.75.75 0 00-1.5.06l.3 7.5a.75.75 0 101.5-.06l-.3-7.5zm4.34.06a.75.75 0 10-1.5-.06l-.3 7.5a.75.75 0 101.5.06l.3-7.5z" clipRule="evenodd" />
                                                </svg>
                                            </button>
                                        </>
                                    )}
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-8 text-[#6e6e73]">
                            No subjects yet. Add one above!
                        </div>
                    )}
                </div>

                {/* Delete Confirmation Modal */}
                {deleteConfirm && (
                    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[60]">
                        <div className="bg-white rounded-2xl p-6 max-w-sm mx-4 shadow-xl">
                            <h3 className="text-[18px] font-semibold text-[#1d1d1f] mb-2">
                                Delete Subject
                            </h3>
                            <p className="text-[14px] text-[#6e6e73] mb-6">
                                Are you sure you want to delete "{deleteConfirm.name}"? This action cannot be undone.
                            </p>
                            <div className="flex gap-3 justify-end">
                                <Button
                                    type="secondary"
                                    onClick={cancelDelete}
                                    disabled={saving}
                                >
                                    Cancel
                                </Button>
                                <Button
                                    type="danger"
                                    onClick={confirmDelete}
                                    disabled={saving}
                                >
                                    {saving ? 'Deleting...' : 'Delete'}
                                </Button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </Modal>
    );
}
