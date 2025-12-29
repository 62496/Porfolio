package com.prj2.booksta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj2.booksta.model.ReadingProgress;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {
    Optional<ReadingProgress> findByUserIdAndBookIsbn(Long userId, String bookIsbn);

    List<ReadingProgress> findByUserId(Long userId);

    void deleteByBook_Isbn(String isbn);
}
