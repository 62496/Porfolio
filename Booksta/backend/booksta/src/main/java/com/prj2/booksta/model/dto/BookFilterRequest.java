package com.prj2.booksta.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFilterRequest {
    private String title;
    private Integer yearMin;
    private Integer yearMax;
    private Long pagesMin;
    private Long pagesMax;
    private List<Long> authorIds;
    private List<Long> subjectIds;

    public boolean hasAuthorFilter() {
        return authorIds != null && !authorIds.isEmpty();
    }

    public boolean hasSubjectFilter() {
        return subjectIds != null && !subjectIds.isEmpty();
    }
}
