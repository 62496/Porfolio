package com.prj2.booksta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Book book;

    @Column(nullable = false, updatable = false)
    private Instant startedAt;

    private Instant endedAt;

    @Column(nullable = false)
    private Long totalActiveSeconds = 0L;

    private Instant lastResumedAt;

    private Integer startPage;

    private Integer endPage;

    @Column(length = 2000)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReadingSessionStatus status;
}

