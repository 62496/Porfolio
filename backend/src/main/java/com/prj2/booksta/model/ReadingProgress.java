package com.prj2.booksta.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reading_progress")
@Data
@NoArgsConstructor
public class ReadingProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_isbn", nullable = false)
    private Book book;

    private Long currentPage;

    private Long totalPages;

    private double progressPercent;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private ReadingStatus status;

    public void initializeTotalPages(Long bookPages) {
    this.totalPages = bookPages != null ? bookPages : 0L;
    
}

    public void setCurrentPage(Long currentPage) {
        if (currentPage <= 0L) {
            this.currentPage = 0L;
        } else if (currentPage > this.totalPages) {
            this.currentPage = this.totalPages;
        } else {
            this.currentPage = currentPage;
        }

        if (this.currentPage.equals(this.totalPages)) {
            this.status = ReadingStatus.FINISHED;
        } else if (this.currentPage > 0) {
            this.status = ReadingStatus.READING;
        }
    }
    public int getProgressPercent() {
        if (totalPages == 0) return 0;
        return (int) Math.round((currentPage * 100.0) / totalPages);
    }

}
