package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDetailResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private int followerCount;
    private int bookCount;
    private int seriesCount;
    private List<BookSummary> books;
    private List<SeriesSummary> series;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeriesSummary {
        private Long id;
        private String title;
        private String description;
        private int bookCount;
        private int followerCount;
    }
}
