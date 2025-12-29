package com.prj2.booksta.repository;

import com.prj2.booksta.model.BookReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReportRepository extends JpaRepository<BookReport, Long> {
    void deleteByBook_Isbn(String isbn);
}
