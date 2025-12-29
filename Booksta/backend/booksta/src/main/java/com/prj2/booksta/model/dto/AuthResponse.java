package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.Role;
import com.prj2.booksta.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private Collection<Role> roles;
        private String googleId;
        private String picture;
        private Long authorId;

        public static UserResponse fromUser(User user, Long authorId) {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setEmail(user.getEmail());
            response.setRoles(user.getRoles());
            response.setGoogleId(user.getGoogleId());
            response.setPicture(user.getPicture());
            response.setAuthorId(authorId);
            return response;
        }
    }
}
