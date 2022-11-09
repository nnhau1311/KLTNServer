package com.example.childrenhabitsserver.storage;

import com.example.childrenhabitsserver.entity.FileDataStorage;
import com.example.childrenhabitsserver.repository.FileDataStorageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation;
    private final FileDataStorageRepository fileDataStorageRepository;

    public FileSystemStorageService(StorageProperties properties, FileDataStorageRepository fileDataStorageRepository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.fileDataStorageRepository = fileDataStorageRepository;
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
//            Random random = new Random();
//            String expandName = String.valueOf(random.nextInt(1000));
//            Path destinationFile = this.rootLocation.resolve(Paths.get(expandName + file.getOriginalFilename()))
//                    .normalize()
//                    .toAbsolutePath();
            String fileId = UUID.randomUUID().toString();
            Path destinationFile = this.rootLocation.resolve(Paths.get(fileId))
                    .normalize()
                    .toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                log.error("Cannot store file outside current directory.");
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                FileDataStorage fileDataStorage = FileDataStorage.builder()
                        .id(fileId)
                        .fileName(file.getOriginalFilename())
                        .fileNameInServer(destinationFile.getFileName().toString())
                        .pathFile(destinationFile.normalize().toAbsolutePath().toString())
                        .build();
                fileDataStorageRepository.save(fileDataStorage);
                return fileId;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public ResponseEntity<Resource> loadAsResource(String fileId) {
        Optional<FileDataStorage> fileDataStorageOptional = fileDataStorageRepository.findById(fileId);
        if (!fileDataStorageOptional.isPresent()) {

        }
        FileDataStorage fileDataStorage = fileDataStorageOptional.get();
        try {
            Path file = load(fileDataStorage.getFileNameInServer());
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDataStorage.getFileName() + "\"")
                        .body(resource);
//                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + fileId);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + fileId, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
