package capstone.be.filesstore;

import capstone.be.configmanager.ConfigManager;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@DependsOn("configManager")
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final ConfigManager configManager;
    private final FileRepository fileRepository;
    private final Tika tika = new Tika();

    // Root path where files will be stored, loaded from configuration
    private String filesArchiveRootPath;

    @PostConstruct
    public void init() {
        // Load the root path from configuration at startup
        filesArchiveRootPath = (String) configManager.getConfVariable("filesArchiveRootPath");
    }

    // Saves a file to disk and stores its metadata in the database
    public FileEntity saveFile(MultipartFile file, String fatherType, Long fatherId) {
        // Basic validation
        if (file.isEmpty() || fatherType == null || fatherId == null) {
            log.warn("Invalid file upload request: fileEmpty={}, fatherType={}, fatherId={}",
                    file.isEmpty(), fatherType, fatherId);
            throw new IllegalArgumentException("File, fatherType, and fatherId cannot be null or empty.");
        }

        // Create entity and persist metadata
        FileEntity storedFile = new FileEntity();

        try {
            // Generate unique ID for the file
            UUID fileUuid = UUID.randomUUID();

            // Extract metadata
            String originalFilename = file.getOriginalFilename();
            String extension = extractExtension(originalFilename);
            String mimeType = Optional.ofNullable(tika.detect(file.getInputStream()))
                    .orElse("application/octet-stream");

            // Compute destination path
            String relativePath = buildStoragePath();
            Path storageDirectory = Paths.get(filesArchiveRootPath, relativePath);
            Files.createDirectories(storageDirectory); // Create directory if needed

            // Build full destination path
            String storedFileName = fileUuid + (extension.isEmpty() ? "" : "." + extension);
            Path storedFilePath = storageDirectory.resolve(storedFileName);

            // Save file on disk
            file.transferTo(storedFilePath.toFile());

            storedFile.setId(fileUuid);
            storedFile.setOriginalFilename(originalFilename);
            storedFile.setExtension(extension);
            storedFile.setMimeType(mimeType);
            storedFile.setStoragePath(relativePath);
            storedFile.setFatherType(fatherType);
            storedFile.setFatherId(fatherId);

        } catch (IOException e) {
            log.error("Error saving file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Error storing the file.", e);
        }

        log.info("File {} saved successfully", storedFile);
        // Store the metadata in the database
        return fileRepository.save(storedFile);
    }

    // Retrieves a file's metadata from the database by ID
    public FileEntity getFile(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found. Literally! :) "));
    }

    // Returns a paginated list of files with optional full-text search
    public Page<FileEntity> getAllFiles(Pageable pageable, String q) {
        log.debug("Searching files with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return fileRepository.findAll(pageable);
        }

        return fileRepository.omniSearch(q.toLowerCase(), pageable);
    }

    // Deletes a file both from disk and database
    public void deleteFile(UUID id, boolean forceDelete) {
        Optional<FileEntity> fileOpt = fileRepository.findById(id);

        if (fileOpt.isEmpty()) {
            log.warn("Attempted to delete non-existent file with ID: {}", id);
            throw new IllegalArgumentException("File not found with ID: " + id);
        }

        if (forceDelete) {
            FileEntity file = fileOpt.get();
            Path filePath = Paths.get(filesArchiveRootPath, file.getFullFilePath());

            try {
                // Delete physical file (if it exists)
                Files.deleteIfExists(filePath);

                // Remove metadata from database
                fileRepository.deleteById(id);

                log.info("File {} deleted successfully", filePath);
            } catch (IOException e) {
                log.error("Error deleting file {} from disk.", id, e);
                throw new RuntimeException("Error deleting the file.", e);
            }
        } else {
            // Soft delete
            FileEntity file = fileOpt.get();
            file.setDeleted(true);
            fileRepository.save(file);
        }
    }

    // Builds a relative path for storage based on the current year and month (e.g.,
    // "2025/03")
    private String buildStoragePath() {
        LocalDate today = LocalDate.now();
        return String.format("%d/%02d", today.getYear(), today.getMonthValue());
    }

    // Extracts the file extension from the original filename
    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    // Downloads a file from disk using its ID
    public Resource downloadFile(UUID id) {
        // Retrieve file metadata from the database
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found with ID: " + id));

        // Construct the full file path
        Path filePath = Paths.get(filesArchiveRootPath, fileEntity.getFullFilePath());

        try {
            // Load the file as a resource
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable: " + filePath);
            }
            return resource;
        } catch (IOException e) {
            log.error("Error loading file with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error loading the file.", e);
        }
    }
}
