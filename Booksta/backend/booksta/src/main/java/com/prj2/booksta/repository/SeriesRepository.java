package com.prj2.booksta.repository;

import com.prj2.booksta.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByAuthorId(Long authorId);
}