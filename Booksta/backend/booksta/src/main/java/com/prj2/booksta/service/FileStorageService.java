package com.prj2.booksta.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${app.backend.url}")
    private String backendUrl;

    @Value("${s3.bucket:}")
    private String bucket;

    @Value("${s3.public-url:}")
    private String s3PublicUrl;

    @Autowired(required = false)
    private S3Client s3Client;

    private final Path booksDir = Paths.get("uploads/books").toAbsolutePath().normalize();
    private final Path authorsDir = Paths.get("uploads/authors").toAbsolutePath().normalize();
    private final Path collectionsDir = Paths.get("uploads/collections").toAbsolutePath().normalize();

    public FileStorageService() throws IOException {
        Files.createDirectories(booksDir);
        Files.createDirectories(authorsDir);
        Files.createDirectories(collectionsDir);
    }

    private boolean useS3() {
        return s3Client != null && !bucket.isEmpty();
    }

    public String saveBookImage(MultipartFile file, String isbn) throws IOException {
        String key = "books/" + isbn + ".png";
        if (useS3()) {
            uploadToS3(file, key);
            return getS3Url(key);
        } else {
            saveFile(file, booksDir, isbn + ".png");
            return backendUrl + "/api/images/books/" + isbn;
        }
    }

    public String saveAuthorImage(MultipartFile file, Long id) throws IOException {
        String key = "authors/" + id + ".png";
        if (useS3()) {
            uploadToS3(file, key);
            return getS3Url(key);
        } else {
            saveFile(file, authorsDir, id + ".png");
            return backendUrl + "/api/images/authors/" + id;
        }
    }

    public String saveCollectionImage(MultipartFile file, Long id) throws IOException {
        String key = "collections/" + id + ".png";
        if (useS3()) {
            uploadToS3(file, key);
            return getS3Url(key);
        } else {
            saveFile(file, collectionsDir, id + ".png");
            return backendUrl + "/api/images/collections/" + id;
        }
    }

    public void deleteCollectionImage(Long id) throws IOException {
        if (useS3()) {
            deleteFromS3("collections/" + id + ".png");
        } else {
            Path target = collectionsDir.resolve(id + ".png");
            Files.deleteIfExists(target);
        }
    }

    public void deleteBookImage(String isbn) throws IOException {
        if (useS3()) {
            deleteFromS3("books/" + isbn + ".png");
        } else {
            Path target = booksDir.resolve(isbn + ".png");
            Files.deleteIfExists(target);
        }
    }

    public void deleteAuthorImage(Long id) throws IOException {
        if (useS3()) {
            deleteFromS3("authors/" + id + ".png");
        } else {
            Path target = authorsDir.resolve(id + ".png");
            Files.deleteIfExists(target);
        }
    }

    public InputStream getImage(String type, String id) throws IOException {
        if (useS3()) {
            String key = type + "/" + id + ".png";
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } else {
            Path dir = switch (type) {
                case "books" -> booksDir;
                case "authors" -> authorsDir;
                case "collections" -> collectionsDir;
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            };
            Path filePath = dir.resolve(id + ".png");
            if (Files.exists(filePath)) {
                return Files.newInputStream(filePath);
            }
            return null;
        }
    }

    private void uploadToS3(MultipartFile file, String key) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    private void deleteFromS3(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    private String getS3Url(String key) {
        if (!s3PublicUrl.isEmpty()) {
            return s3PublicUrl + "/" + key;
        }
        return backendUrl + "/api/images/" + key.replace(".png", "");
    }

    private void saveFile(MultipartFile file, Path dir, String fileName) throws IOException {
        Path target = dir.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
    }
}
