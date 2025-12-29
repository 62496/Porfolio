package com.prj2.booksta.repository;

import com.prj2.booksta.model.AuthorReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorReportRepository extends JpaRepository<AuthorReport, Long> {

}
