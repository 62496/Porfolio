import { useEffect, useState, useCallback, useRef } from "react";
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import bookService from "../../../api/services/bookService";
import readingService from "../../../api/services/readingService";
import { useBooks } from "../../books/hooks/useBooks";
import { useToast } from "../../../hooks/useToast";
import { startSessionSchema, endSessionSchema } from "../validations/readingSessionSchema";

export function useReadingSession(isbn) {
    const { createReadingEvent, getLatestBookReadingEvent } = useBooks(false);
    const { toast, showToast, hideToast } = useToast();

    // Book state
    const [book, setBook] = useState(null);
    const [loading, setLoading] = useState(true);
    const [latestReadingEvent, setLatestReadingEvent] = useState(null);

    // Session state - backend is authoritative for time
    const [currentSession, setCurrentSession] = useState(null);
    const [isSessionActive, setIsSessionActive] = useState(false);
    const [isPaused, setIsPaused] = useState(false);
    const [sessionDuration, setSessionDuration] = useState(0);

    // Local timer for display purposes only (backend is still authoritative)
    const timerIntervalRef = useRef(null);
    const localTimerStartRef = useRef(null);
    const accumulatedSecondsRef = useRef(0);

    // Modal state
    const [showStartModal, setShowStartModal] = useState(false);
    const [showCommentModal, setShowCommentModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [editingSession, setEditingSession] = useState(null);
    const [deletingSessionId, setDeletingSessionId] = useState(null);

    // History & Events state
    const [readingHistory, setReadingHistory] = useState([]);
    const [historyLoading, setHistoryLoading] = useState(false);
    const [readingEvents, setReadingEvents] = useState([]);
    const [eventsLoading, setEventsLoading] = useState(false);

    // Tab state
    const [activeTab, setActiveTab] = useState("session");

    // Forms
    const startForm = useForm({
        resolver: yupResolver(startSessionSchema),
        mode: 'onChange',
        defaultValues: { startPage: '' }
    });

    const endForm = useForm({
        resolver: yupResolver(endSessionSchema(book?.pages)),
        mode: 'onChange',
        defaultValues: { startPage: '', endPage: '', markAsFinished: false, note: '' }
    });

    const editForm = useForm({
        resolver: yupResolver(endSessionSchema(book?.pages)),
        mode: 'onChange',
        defaultValues: { startPage: '', endPage: '', markAsFinished: false, note: '' }
    });

    // Load book on mount
    useEffect(() => {
        if (!isbn) return;
        loadBook();
        loadLatestReadingEvent();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isbn]);

    // Local display timer effect
    useEffect(() => {
        if (isSessionActive && !isPaused) {
            localTimerStartRef.current = Date.now();
            timerIntervalRef.current = setInterval(() => {
                const elapsed = Math.floor((Date.now() - localTimerStartRef.current) / 1000);
                setSessionDuration(accumulatedSecondsRef.current + elapsed);
            }, 1000);
        } else {
            if (timerIntervalRef.current) {
                clearInterval(timerIntervalRef.current);
                timerIntervalRef.current = null;
            }
        }
        return () => {
            if (timerIntervalRef.current) {
                clearInterval(timerIntervalRef.current);
            }
        };
    }, [isSessionActive, isPaused]);

    // Auto-check "Mark as Finished" for end form
    useEffect(() => {
        const subscription = endForm.watch((value, { name }) => {
            if (name === 'endPage' && value.endPage && book?.pages) {
                if (parseInt(value.endPage) === book.pages) {
                    endForm.setValue('markAsFinished', true);
                }
            }
        });
        return () => subscription.unsubscribe();
    }, [endForm, book?.pages]);

    // Auto-check "Mark as Finished" for edit form
    useEffect(() => {
        const subscription = editForm.watch((value, { name }) => {
            if (name === 'endPage' && value.endPage && book?.pages) {
                if (parseInt(value.endPage) === book.pages) {
                    editForm.setValue('markAsFinished', true);
                }
            }
        });
        return () => subscription.unsubscribe();
    }, [editForm, book?.pages]);

    // Load data based on active tab
    useEffect(() => {
        if (activeTab === "history") {
            loadReadingHistory();
        } else if (activeTab === "habits") {
            loadReadingEvents();
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [activeTab]);

    // API functions
    const loadBook = async () => {
        try {
            setLoading(true);
            const bookData = await bookService.getBookByIsbn(isbn);
            setBook(bookData);
        } catch (error) {
            console.error("Error loading book:", error);
            showToast("Error loading book", "error");
        } finally {
            setLoading(false);
        }
    };

    const loadLatestReadingEvent = async () => {
        try {
            const event = await getLatestBookReadingEvent(isbn);
            setLatestReadingEvent(event);
        } catch (error) {
            console.error("Error loading latest reading event:", error);
        }
    };

    const loadReadingHistory = async () => {
        try {
            setHistoryLoading(true);
            const sessions = await bookService.getAllBookReadingSessions(isbn);
            const sortedSessions = sessions.sort((a, b) =>
                new Date(b.startedAt) - new Date(a.startedAt)
            );
            setReadingHistory(sortedSessions);
        } catch (error) {
            console.error("Error loading reading history:", error);
            showToast("Error loading reading history", "error");
        } finally {
            setHistoryLoading(false);
        }
    };

    const loadReadingEvents = async () => {
        try {
            setEventsLoading(true);
            const events = await bookService.getAllBookReadingEvents(isbn);
            setReadingEvents(events);
        } catch (error) {
            console.error("Error loading reading events:", error);
            showToast("Error loading reading habits", "error");
        } finally {
            setEventsLoading(false);
        }
    };

    // Session handlers
    const handleStartSession = () => {
        startForm.reset({ startPage: '' });
        setShowStartModal(true);
    };

    const handleConfirmStartSession = async (formData) => {
        try {
            const data = await readingService.create(isbn, parseInt(formData.startPage));
            setCurrentSession(data);
            setIsSessionActive(true);
            setIsPaused(false);
            accumulatedSecondsRef.current = 0;
            setSessionDuration(0);
            setShowStartModal(false);
            startForm.reset();
            showToast("Reading session started!", "success");
        } catch (error) {
            console.error("Error starting session:", error);
            showToast("Error starting session", "error");
        }
    };

    const handlePauseSession = useCallback(async () => {
        if (!currentSession) return;
        try {
            const updatedSession = await readingService.pause(currentSession.id);
            setCurrentSession(updatedSession);
            setIsPaused(true);
            // Store accumulated time from backend
            accumulatedSecondsRef.current = updatedSession.totalActiveSeconds || sessionDuration;
            setSessionDuration(accumulatedSecondsRef.current);
        } catch (error) {
            console.error("Error pausing session:", error);
            showToast("Error pausing session", "error");
        }
    }, [currentSession, sessionDuration, showToast]);

    const handleResumeSession = useCallback(async () => {
        if (!currentSession) return;
        try {
            const updatedSession = await readingService.resume(currentSession.id);
            setCurrentSession(updatedSession);
            setIsPaused(false);
            // Backend doesn't add time on resume, timer restarts from accumulated
        } catch (error) {
            console.error("Error resuming session:", error);
            showToast("Error resuming session", "error");
        }
    }, [currentSession, showToast]);

    const handleStopSession = useCallback(async () => {
        if (!currentSession) return;

        // Step 1: Immediately pause to stop the timer
        try {
            const updatedSession = await readingService.pause(currentSession.id);
            setCurrentSession(updatedSession);
            setIsPaused(true);
            accumulatedSecondsRef.current = updatedSession.totalActiveSeconds || sessionDuration;
            setSessionDuration(accumulatedSecondsRef.current);
        } catch (error) {
            console.error("Error pausing session before end:", error);
            // Continue anyway - the session might already be paused
        }

        // Step 2: Open end session modal
        setIsSessionActive(false);
        endForm.reset({
            startPage: currentSession?.startPage || '',
            endPage: '',
            note: ''
        });
        setShowCommentModal(true);
    }, [currentSession, sessionDuration, endForm]);

    const handleDeleteSession = async () => {
        try {
            await readingService.delete(currentSession.id);
            resetSessionState();
            setShowCommentModal(false);
            endForm.reset();
            showToast("Session deleted", "success");
        } catch (error) {
            console.error("Error deleting session:", error);
            showToast("Error deleting session", "error");
        }
    };

    const handleSaveSessionComment = async (formData) => {
        try {
            // Call end endpoint with final data
            await readingService.end(currentSession.id, {
                startPage: parseInt(formData.startPage),
                endPage: parseInt(formData.endPage),
                note: formData.note || null
            });

            if (formData.markAsFinished) {
                await createReadingEvent(isbn, "FINISHED_READING");
            }

            showToast(
                formData.markAsFinished
                    ? "Session saved and book marked as finished!"
                    : "Session saved!",
                "success"
            );
            setShowCommentModal(false);
            resetSessionState();
            endForm.reset();
            loadReadingHistory();

            if (formData.markAsFinished) {
                await loadBook();
                await loadLatestReadingEvent();
            }
        } catch (error) {
            console.error("Error saving session:", error);
            showToast("Error saving session", "error");
        }
    };

    const resetSessionState = () => {
        setIsSessionActive(false);
        setIsPaused(false);
        setCurrentSession(null);
        setSessionDuration(0);
        accumulatedSecondsRef.current = 0;
    };

    const handleEditSession = (session) => {
        setEditingSession(session);
        editForm.reset({
            startPage: session.startPage,
            endPage: session.endPage || '',
            note: session.note || ''
        });
        setShowEditModal(true);
    };

    const handleUpdateSession = async (formData) => {
        try {
            // For editing completed sessions, use the end endpoint
            await readingService.end(editingSession.id, {
                startPage: parseInt(formData.startPage),
                endPage: parseInt(formData.endPage),
                note: formData.note || null
            });

            if (formData.markAsFinished) {
                await createReadingEvent(isbn, "FINISHED_READING");
            }

            showToast(
                formData.markAsFinished
                    ? "Session updated and book marked as finished!"
                    : "Session updated!",
                "success"
            );
            setShowEditModal(false);
            editForm.reset();
            setEditingSession(null);
            loadReadingHistory();

            if (formData.markAsFinished) {
                await loadBook();
                await loadLatestReadingEvent();
            }
        } catch (error) {
            console.error("Error updating session:", error);
            showToast("Error updating session", "error");
        }
    };

    const handleDeleteHistorySession = (sessionId) => {
        setDeletingSessionId(sessionId);
        setShowDeleteModal(true);
    };

    const handleConfirmDelete = async () => {
        try {
            await readingService.delete(deletingSessionId);
            showToast("Session deleted", "success");
            setShowDeleteModal(false);
            setDeletingSessionId(null);
            loadReadingHistory();
        } catch (error) {
            console.error("Error deleting session:", error);
            showToast("Error deleting session", "error");
        }
    };

    // Book progress handlers
    const handleFinishBook = async () => {
        try {
            await createReadingEvent(isbn, "FINISHED_READING");
            showToast("Book marked as finished!", "success");
            await loadBook();
            await loadLatestReadingEvent();
        } catch (error) {
            console.error("Error finishing book:", error);
            showToast("Error finishing book", "error");
        }
    };

    const handleAbandonBook = async () => {
        try {
            await createReadingEvent(isbn, "ABANDONED_READING");
            showToast("Book marked as abandoned", "success");
            await loadBook();
            await loadLatestReadingEvent();
        } catch (error) {
            console.error("Error abandoning book:", error);
            showToast("Error abandoning book", "error");
        }
    };

    const handleRestartBook = async () => {
        try {
            await createReadingEvent(isbn, "RESTARTED_READING");
            showToast("Book restarted!", "success");
            await loadBook();
            await loadLatestReadingEvent();
        } catch (error) {
            console.error("Error restarting book:", error);
            showToast("Error restarting book", "error");
        }
    };

    // Cancel handlers for modals
    const handleCancelStartModal = () => {
        setShowStartModal(false);
        startForm.reset();
    };

    const handleCancelCommentModal = useCallback(async () => {
        // User cancelled the end modal, resume the session
        setShowCommentModal(false);
        setIsSessionActive(true);

        if (currentSession) {
            try {
                const updatedSession = await readingService.resume(currentSession.id);
                setCurrentSession(updatedSession);
                setIsPaused(false);
            } catch (error) {
                console.error("Error resuming session after cancel:", error);
                // Fallback: just set local state
                setIsPaused(false);
            }
        }

        endForm.reset();
    }, [currentSession, endForm]);

    const handleCancelEditModal = () => {
        setShowEditModal(false);
        setEditingSession(null);
        editForm.reset();
    };

    const handleCancelDeleteModal = () => {
        setShowDeleteModal(false);
        setDeletingSessionId(null);
    };

    // Computed values
    const currentStatus = latestReadingEvent?.readingEvent || book?.latestReadingEvent?.eventType;
    const canFinishOrAbandon = currentStatus === 'STARTED_READING' || currentStatus === 'RESTARTED_READING';
    const canRestart = currentStatus === 'FINISHED_READING' || currentStatus === 'ABANDONED_READING';

    return {
        // State
        book,
        loading,
        toast,
        hideToast,
        activeTab,
        setActiveTab,
        currentStatus,
        canFinishOrAbandon,
        canRestart,

        // Session state
        isSessionActive,
        isPaused,
        sessionDuration,
        currentSession,

        // History & Events
        readingHistory,
        historyLoading,
        readingEvents,
        eventsLoading,

        // Modal state
        showStartModal,
        showCommentModal,
        showEditModal,
        showDeleteModal,
        editingSession,

        // Forms
        startForm,
        endForm,
        editForm,

        // Session handlers
        handleStartSession,
        handleConfirmStartSession,
        handlePauseSession,
        handleResumeSession,
        handleStopSession,
        handleDeleteSession,
        handleSaveSessionComment,

        // History handlers
        handleEditSession,
        handleUpdateSession,
        handleDeleteHistorySession,
        handleConfirmDelete,

        // Book progress handlers
        handleFinishBook,
        handleAbandonBook,
        handleRestartBook,

        // Modal cancel handlers
        handleCancelStartModal,
        handleCancelCommentModal,
        handleCancelEditModal,
        handleCancelDeleteModal,
    };
}
