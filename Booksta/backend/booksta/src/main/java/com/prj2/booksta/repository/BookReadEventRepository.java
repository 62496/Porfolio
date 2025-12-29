package com.prj2.booksta.repository;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.BookReadEvent;
import com.prj2.booksta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookReadEventRepository extends JpaRepository<BookReadEvent, Long> {
    Optional<BookReadEvent> findTopByUserAndBookOrderByOccurredAtDesc(User user, Book book);

    List<BookReadEvent> findByUser_IdAndBook_IsbnOrderByOccurredAtDesc(Long id, String isbn);

    void deleteByBook_Isbn(String isbn);
}
