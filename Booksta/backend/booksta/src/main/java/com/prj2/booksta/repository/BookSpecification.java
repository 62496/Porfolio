package com.prj2.booksta.repository;

import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.dto.BookFilterRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> withFilters(BookFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getTitle() != null && !filter.getTitle().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + filter.getTitle().toLowerCase() + "%"
                ));
            }

            if (filter.getYearMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("publishingYear"), filter.getYearMin()
                ));
            }
            if (filter.getYearMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("publishingYear"), filter.getYearMax()
                ));
            }

            // Pages range filter
            if (filter.getPagesMin() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("pages"), filter.getPagesMin()
                ));
            }
            if (filter.getPagesMax() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("pages"), filter.getPagesMax()
                ));
            }

            if (filter.hasAuthorFilter()) {
                Join<Object, Object> authorsJoin = root.join("authors", JoinType.LEFT);
                predicates.add(authorsJoin.get("id").in(filter.getAuthorIds()));
            }

            //  filter
            if (filter.hasSubjectFilter()) {
                Join<Object, Object> subjectsJoin = root.join("subjects", JoinType.LEFT);
                predicates.add(subjectsJoin.get("id").in(filter.getSubjectIds()));
            }

            // Ensure distinct results
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
