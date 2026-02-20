package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeriesResponse {
    private Long id;
    private String title;
    private String description;
    private AuthorSummary author;
    private int bookCount;
    private int followerCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorSummary {
        private Long id;
        private String firstName;
        private String lastName;
        private String imageUrl;
    }
}
