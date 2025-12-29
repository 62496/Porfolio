import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import userService from "../../../api/services/userService";

export function useReadingProgress() {
    const navigate = useNavigate();
    const [startedBooks, setStartedBooks] = useState([]);
    const [restartedBooks, setRestartedBooks] = useState([]);
    const [finishedBooks, setFinishedBooks] = useState([]);
    const [abandonedBooks, setAbandonedBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [finishedExpanded, setFinishedExpanded] = useState(false);
    const [abandonedExpanded, setAbandonedExpanded] = useState(false);

    useEffect(() => {
        loadBooks();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const loadBooks = useCallback(async () => {
        try {
            setLoading(true);
            const booksWithStatus = await userService.getOwnedBooksWithExistingReadingStatus();

            const started = [];
            const restarted = [];
            const finished = [];
            const abandoned = [];

            booksWithStatus.forEach(book => {
                if (book.latestReadingEvent) {
                    switch (book.latestReadingEvent.eventType) {
                        case 'STARTED_READING':
                            started.push(book);
                            break;
                        case 'RESTARTED_READING':
                            restarted.push(book);
                            break;
                        case 'FINISHED_READING':
                            finished.push(book);
                            break;
                        case 'ABANDONED_READING':
                            abandoned.push(book);
                            break;
                        default:
                            break;
                    }
                }
            });

            setStartedBooks(started);
            setRestartedBooks(restarted);
            setFinishedBooks(finished);
            setAbandonedBooks(abandoned);
        } catch (error) {
            console.error("Error loading books", error);
        } finally {
            setLoading(false);
        }
    }, []);

    const currentlyReadingBooks = [...startedBooks, ...restartedBooks];

    const toggleFinishedExpanded = useCallback(() => {
        setFinishedExpanded(prev => !prev);
    }, []);

    const toggleAbandonedExpanded = useCallback(() => {
        setAbandonedExpanded(prev => !prev);
    }, []);

    const goToMyBooks = useCallback(() => {
        navigate('/my-books');
    }, [navigate]);

    const goToBookDetail = useCallback((isbn) => {
        navigate(`/book/${isbn}`);
    }, [navigate]);

    const goToReadingSession = useCallback((isbn) => {
        navigate(`/reading-session/${isbn}`);
    }, [navigate]);

    const hasNoBooks = startedBooks.length === 0 &&
                       restartedBooks.length === 0 &&
                       finishedBooks.length === 0 &&
                       abandonedBooks.length === 0;

    return {
        // State
        startedBooks,
        restartedBooks,
        finishedBooks,
        abandonedBooks,
        currentlyReadingBooks,
        loading,
        finishedExpanded,
        abandonedExpanded,
        hasNoBooks,

        // Actions
        toggleFinishedExpanded,
        toggleAbandonedExpanded,
        goToMyBooks,
        goToBookDetail,
        goToReadingSession,
    };
}
