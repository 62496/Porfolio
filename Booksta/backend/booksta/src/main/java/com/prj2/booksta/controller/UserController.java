package com.prj2.booksta.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj2.booksta.model.Author;
import com.prj2.booksta.model.Book;
import com.prj2.booksta.model.Series;
import com.prj2.booksta.model.User;
import com.prj2.booksta.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/users/{userId}/favorites/{bookIsbn}
     * Ajouter un livre à la liste de favoris (liste à lire)
     */
    @PostMapping("/favorites/{bookIsbn}")
    public ResponseEntity<ApiResponse> addFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String bookIsbn) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            userService.addFavorite(user.getId(), bookIsbn);
            return ResponseEntity.ok(
                    new ApiResponse("Livre ajouté aux favoris avec succès", true)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * DELETE /api/users/{userId}/favorites/{bookIsbn}
     * Retirer un livre de la liste de favoris
     */
    @DeleteMapping("/favorites/{bookIsbn}")
    public ResponseEntity<ApiResponse> removeFavorite(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String bookIsbn) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            userService.removeFavorite(user.getId(), bookIsbn);
            return ResponseEntity.ok(
                    new ApiResponse("Livre retiré des favoris avec succès", true)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), false));
        }
    }

    /**
     * GET /api/users/{userId}/favorites
     * Récupérer la liste complète des favoris d'un utilisateur
     * Optimisé pour charger les favoris en UNE SEULE requête
     */
    @GetMapping("/favorites")
    public ResponseEntity<Set<Book>> getFavorites(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            Set<Book> favorites = userService.getFavoritesOptimized(user.getId());
            return ResponseEntity.ok(favorites);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * POST /{userId}/follow/author/{authorId}
     * Follow an author
     */

    @PostMapping("/follow/author/{authorId}")
    public ResponseEntity<Void> followAuthor(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long authorId) {
        User user = userService.getUserByEmail(userDetails.getUsername());                                         
        userService.followAuthor(user.getId(), authorId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /{userId}/follow/author/{authorId}
     * Unfollow an author
     */

    @DeleteMapping("/follow/author/{authorId}")
    public ResponseEntity<Void> unfollowAuthor(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable Long authorId) {
        User user = userService.getUserByEmail(userDetails.getUsername());                                          
        userService.unfollowAuthor(user.getId(), authorId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /{userId}/follow/author/{authorId}
     * Check if the user is following an author
     */
    @GetMapping("/follow/author/{authorId}")
    public ResponseEntity<Boolean> isFollowingAuthor(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Long authorId) {
        User user = userService.getUserByEmail(userDetails.getUsername());                                                
        return ResponseEntity.ok(userService.isFollowingAuthor(user.getId(), authorId));
    }

    /**
     * POST /{userId}/follow/series/{seriesId}
     * Follow a book series
     */
    @PostMapping("/follow/series/{seriesId}")
    public ResponseEntity<Void> followSeries(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long seriesId) {
        User user = userService.getUserByEmail(userDetails.getUsername());                                         
        userService.followSeries(user.getId(), seriesId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /{userId}/follow/series/{seriesId}
     * Unfollow a book series
     */

    @DeleteMapping("/follow/series/{seriesId}")
    public ResponseEntity<Void> unfollowSeries(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable Long seriesId) {
        User user = userService.getUserByEmail(userDetails.getUsername());                                       
        userService.unfollowSeries(user.getId(), seriesId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /{userId}/follow/series/{seriesId}
     * Check if the user is following a series
     */
    @GetMapping("/follow/series/{seriesId}")
    public ResponseEntity<Boolean> isFollowingSeries(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Long seriesId) {
            User user = userService.getUserByEmail(userDetails.getUsername());
        return ResponseEntity.ok(userService.isFollowingSeries(user.getId(), seriesId));
    }

    /**
     * GET /api/users/{userId}/followed-authors
     * Return authors followed by the user
     */
    @GetMapping("/followed-authors")
    public ResponseEntity<Set<Author>> getFollowedAuthors(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            Set<Author> authors = userService.getFollowedAuthors(user.getId());
            return ResponseEntity.ok(authors);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * GET /api/users/{userId}/followed-series
     * Return series followed by the user
     */
    @GetMapping("/followed-series")
    public ResponseEntity<Set<Series>> getFollowedSeries(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.getUserByEmail(userDetails.getUsername());
            Set<Series> series = userService.getFollowedSeries(user.getId());
            return ResponseEntity.ok(series);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * GET /api/users/search?query=...
     * Recherche des utilisateurs par nom/prénom/email
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/search-google?query=...&excludeId=...
     * Recherche des utilisateurs disposant d'un compte Google
     */
    @GetMapping("/search-google")
    public ResponseEntity<List<User>> searchGoogleUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long excludeId) {

        List<User> users = userService.searchGoogleUsers(query, excludeId);
        return ResponseEntity.ok(users);
    }

    /**
     * Classe interne pour les réponses API standardisées
     */
    
    public class ApiResponse {
        public String message;
        public boolean success;

        public ApiResponse(String message, boolean success) {
            this.message = message;
            this.success = success;
        }
    }

     /**
     * POST /api/users/{userId}/owned-books/{bookIsbn}
     * Ajoute un livre à la bibliothèque personnelle de l'utilisateur
     */
     @PostMapping("/{userId}/owned-books/{bookIsbn}")
     public ResponseEntity<ApiResponse> addOwnedBook(
             @PathVariable Long userId,
             @PathVariable String bookIsbn) {
         try {
             userService.addOwnedBook(userId, bookIsbn);
             return ResponseEntity.ok(
                     new ApiResponse("Livre ajouté à votre bibliothèque personnelle", true)
             );
         } catch (IllegalArgumentException e) {
             return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body(new ApiResponse(e.getMessage(), false));
         }
     }
 
     /**
      * DELETE /api/users/{userId}/owned-books/{bookIsbn}
      * Retire un livre de la bibliothèque personnelle
      */
     @DeleteMapping("/{userId}/owned-books/{bookIsbn}")
     public ResponseEntity<ApiResponse> removeOwnedBook(
             @PathVariable Long userId,
             @PathVariable String bookIsbn) {
         try {
             userService.removeOwnedBook(userId, bookIsbn);
             return ResponseEntity.ok(
                     new ApiResponse("Livre retiré de votre bibliothèque personnelle", true)
             );
         } catch (IllegalArgumentException e) {
             return ResponseEntity
                     .status(HttpStatus.BAD_REQUEST)
                     .body(new ApiResponse(e.getMessage(), false));
         }
     }
 
     /**
      * GET /api/users/{userId}/owned-books
      * Récupère tous les livres possédés par l'utilisateur
      */
     @GetMapping("/{userId}/owned-books")
     public ResponseEntity<Set<Book>> getOwnedBooks(@PathVariable Long userId) {
         try {
             Set<Book> owned = userService.getOwnedBooksOptimized(userId);
             return ResponseEntity.ok(owned);
         } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
         }
     }
}
