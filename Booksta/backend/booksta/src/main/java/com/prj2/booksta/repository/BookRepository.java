package com.prj2.booksta.repository;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.dto.BookWithLatestReadingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query("""
        SELECT DISTINCT b FROM Book b
        JOIN b.authors a
        JOIN b.subjects s
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:authorName IS NULL OR 
                LOWER(a.firstName) LIKE LOWER(CONCAT('%', :authorName, '%')) OR 
                LOWER(a.lastName) LIKE LOWER(CONCAT('%', :authorName, '%')))
          AND (:subjectName IS NULL OR LOWER(s.name) = LOWER(:subjectName))
          AND (:year IS NULL OR b.publishingYear = :year)
    """)
    List<Book> searchBooks(@Param("title") String title,
                           @Param("authorName") String authorName,
                           @Param("subjectName") String subjectName,
                           @Param("year") Integer year);

    List<Book> findBySeries_Id(Long seriesId);
    List<Book> findByAuthors_Id(Long authorId);
}
