package com.prj2.booksta.repository;

import com.prj2.booksta.model.ReadingSession;
import com.prj2.booksta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReadingSessionRepository extends JpaRepository<ReadingSession, Long> {
    List<ReadingSession> findByUserAndBookIsbn(User user, String isbn);
}
