package com.prj2.booksta.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("BOOK")
@Getter
@Setter
public class BookReport extends Report {
    @ManyToOne
    @JoinColumn(name = "book_isbn")
    private Book book;
}
