package com.prj2.booksta.model.dto;

import com.prj2.booksta.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String picture;
    private String googleId;
    private List<RoleInfo> roles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private Long id;
        private String name;
    }

    public static UserAdminResponse fromUser(User user) {
        UserAdminResponse response = new UserAdminResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPicture(user.getPicture());
        response.setGoogleId(user.getGoogleId());

        List<RoleInfo> roles = user.getRoles().stream()
                .map(role -> new RoleInfo(role.getId(), role.getName()))
                .collect(Collectors.toList());
        response.setRoles(roles);

        return response;
    }
}
