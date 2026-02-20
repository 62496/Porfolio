package com.prj2.booksta.service;

import com.prj2.booksta.exception.UserNotReadingBookException;
import com.prj2.booksta.model.*;
import com.prj2.booksta.repository.ReadingSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class ReadingSessionService {

    @Autowired
    private ReadingSessionRepository readingSessionRepository;

    @Autowired
    private BookReadEventService bookReadEventService;

    @Autowired
    private UserService userService;

    /* -------------------------------------------------------
       CREATE (START)
       ------------------------------------------------------- */

    public ReadingSession createSession(User user, Book book, Integer startPage) {

        ReadingEventType event =
                bookReadEventService
                        .getLatestReadEvent(user.getEmail(), book.getIsbn())
                        .getReadingEvent();

        if (event != ReadingEventType.STARTED_READING &&
                event != ReadingEventType.RESTARTED_READING) {
            throw new UserNotReadingBookException(
                    "The book is not being read by the user anymore"
            );
        }

        Instant now = Instant.now();

        ReadingSession session = new ReadingSession();
        session.setUser(user);
        session.setBook(book);
        session.setStartedAt(now);
        session.setLastResumedAt(now);
        session.setTotalActiveSeconds(0L);
        session.setStartPage(startPage);
        session.setStatus(ReadingSessionStatus.ACTIVE);

        return readingSessionRepository.save(session);
    }

    @Transactional
    public ReadingSession pauseReadingSession(User user, Long sessionId)
            throws AccessDeniedException {

        ReadingSession session = getSessionForUser(user, sessionId);

        if (session.getStatus() == ReadingSessionStatus.FINISHED) {
            throw new IllegalStateException("Session already finished");
        }

        if (session.getStatus() == ReadingSessionStatus.ACTIVE) {
            Instant now = Instant.now();

            long chunkSeconds = Duration.between(
                    session.getLastResumedAt(),
                    now
            ).getSeconds();

            session.setTotalActiveSeconds(
                    session.getTotalActiveSeconds() + chunkSeconds
            );

            session.setLastResumedAt(null);
            session.setStatus(ReadingSessionStatus.PAUSED);
        }

        return readingSessionRepository.save(session);
    }

    @Transactional
    public ReadingSession resumeReadingSession(User user, Long sessionId)
            throws AccessDeniedException {

        ReadingSession session = getSessionForUser(user, sessionId);

        if (session.getStatus() == ReadingSessionStatus.FINISHED) {
            throw new IllegalStateException("Session already finished");
        }

        if (session.getStatus() == ReadingSessionStatus.PAUSED) {
            session.setLastResumedAt(Instant.now());
            session.setStatus(ReadingSessionStatus.ACTIVE);
        }

        return readingSessionRepository.save(session);
    }

    @Transactional
    public ReadingSession endReadingSession(
            User user,
            Long sessionId,
            Integer startPage,
            Integer endPage,
            String note
    ) throws AccessDeniedException {

        ReadingSession session = getSessionForUser(user, sessionId);

        if (session.getStatus() == ReadingSessionStatus.FINISHED) {
            throw new IllegalStateException("The reading session is already finished");
        }

        int currentStart = session.getStartPage();
        int newStart = (startPage != null) ? startPage : currentStart;

        if (newStart > endPage) {
            throw new IllegalArgumentException(
                    "Start page cannot be greater than end page"
            );
        }

        int totalPages = Math.toIntExact(session.getBook().getPages());
        if (endPage > totalPages) {
            throw new IllegalArgumentException(
                    "End page cannot be greater than book total pages (" + totalPages + ")"
            );
        }

        Instant now = Instant.now();

        if (session.getStatus() == ReadingSessionStatus.ACTIVE) {
            long finalChunk = Duration.between(
                    session.getLastResumedAt(),
                    now
            ).getSeconds();

            session.setTotalActiveSeconds(
                    session.getTotalActiveSeconds() + finalChunk
            );
        }

        session.setStartPage(newStart);
        session.setEndPage(endPage);
        session.setEndedAt(now);
        session.setLastResumedAt(null);
        session.setStatus(ReadingSessionStatus.FINISHED);

        if (note != null) {
            session.setNote(note);
        }

        return readingSessionRepository.save(session);
    }

    public void deleteSession(Long sessionId) throws AccessDeniedException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        ReadingSession session = readingSessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Reading session with id " + sessionId + " not found"
                        )
                );

        User user = userService.getUserByEmail(authentication.getName());

        if (!session.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "The user owning this session is not the authenticated user"
            );
        }

        readingSessionRepository.delete(session);
    }

    public List<ReadingSession> findByUserAndIsbn(User user, String isbn) {
        return readingSessionRepository.findByUserAndBookIsbn(user, isbn);
    }

    private ReadingSession getSessionForUser(User user, Long sessionId)
            throws AccessDeniedException {

        ReadingSession session = readingSessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Reading session with id " + sessionId + " not found"
                        )
                );

        if (!session.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this reading session");
        }

        return session;
    }
}