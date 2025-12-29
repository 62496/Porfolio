package com.prj2.booksta.controller;

import com.prj2.booksta.model.RefreshToken;
import com.prj2.booksta.model.User;
import com.prj2.booksta.model.dto.AuthResponse;
import com.prj2.booksta.model.dto.GoogleLoginRequest;
import com.prj2.booksta.model.dto.RefreshTokenRequest;
import com.prj2.booksta.model.dto.TokenRefreshResponse;
import com.prj2.booksta.service.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {

        String googleToken = request.getToken();

        if (googleToken == null || googleToken.isBlank()) {
            return ResponseEntity.badRequest().body("Missing Google token");
        }

        GoogleIdToken.Payload payload = googleTokenVerifier.verify(googleToken);

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Google token");
        }

        String email = payload.getEmail();
        String givenName = (String) payload.get("given_name");
        String familyName = (String) payload.get("family_name");
        String fullName = (String) payload.get("name");
        String googleId = payload.getSubject();
        String picture = (String) payload.get("picture");

        String firstName = (givenName != null && !givenName.isBlank())
                ? givenName
                : (fullName != null && !fullName.isBlank() ? fullName : "Unknown");

        String lastName = (familyName != null && !familyName.isBlank())
                ? familyName
                : "User";

        User user = userService.findOrCreateGoogleUser(
                email,
                firstName,
                lastName,
                googleId,
                picture
        );

        String jwt = jwtService.generateToken(user);

        refreshTokenService.deleteByUser(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        Long authorId = null;
        var author = authorService.findByUserId(user.getId());
        if (author != null) {
            authorId = author.getId();
        }

        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.fromUser(user, authorId);
        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken.getToken(), userResponse));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "").trim();

        String email;
        try {
            email = jwtService.extractEmail(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired token");
        }

        User user = userService.getUserByEmailOrThrow(email);

        // Get author ID if user is an author
        Long authorId = null;
        var author = authorService.findByUserId(user.getId());
        if (author != null) {
            authorId = author.getId();
        }

        return ResponseEntity.ok(AuthResponse.UserResponse.fromUser(user, authorId));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, requestRefreshToken));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new TokenRefreshResponse(null, null)));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok().body("Logged out successfully");
    }
}
