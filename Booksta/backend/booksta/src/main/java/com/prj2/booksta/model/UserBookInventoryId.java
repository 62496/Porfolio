package com.prj2.booksta.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBookInventoryId implements Serializable {
    private Long userId;
    private String bookIsbn;
}
