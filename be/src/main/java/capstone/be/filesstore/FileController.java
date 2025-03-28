package capstone.be.filesstore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import capstone.be.config.GetVariables;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import org.springframework.data.web.PagedResourcesAssembler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<FileEntity> pagedResourcesAssembler;

    @Operation(summary = "Carica un file", description = "Caricamento di un file associato a un'entità padre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File caricato correttamente"),
            @ApiResponse(responseCode = "400", description = "Richiesta non valida"),
            @ApiResponse(responseCode = "500", description = "Errore interno del server")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<FileEntity> uploadFile(
            @Parameter(description = "File upload", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type of father entity (ex. user, contract)", required = true) @RequestParam("fatherType") String fatherType,
            @Parameter(description = "ID of father entity", required = true) @RequestParam("fatherId") Long fatherId) {
        FileEntity savedFile = fileService.saveFile(file, fatherType, fatherId);
        return ResponseEntity.ok(savedFile);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<FileEntity>>> getFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "originalFilename") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false) String q) {

        log.debug("Fetching files, page: {}, sortBy: {}, direction: {}, query: {}", page, sortBy, direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<FileEntity> files = fileService.getAllFiles(pageable, q);
        PagedModel<EntityModel<FileEntity>> pagedModel = pagedResourcesAssembler.toModel(files);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileEntity> getFile(@PathVariable UUID id) {
        FileEntity file = fileService.getFile(id);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download the file: ", description = "Download the file by its ID")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
        Resource fileResource = fileService.downloadFile(id);

        // Set headers for file download
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean forceDelete) {
        fileService.deleteFile(id, forceDelete);
        return ResponseEntity.noContent().build();
    }
}
