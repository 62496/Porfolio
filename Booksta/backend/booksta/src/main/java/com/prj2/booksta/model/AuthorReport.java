package com.prj2.booksta.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("AUTHOR")
@Getter
@Setter
public class AuthorReport extends Report {
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
