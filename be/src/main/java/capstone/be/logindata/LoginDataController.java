package capstone.be.logindata;

import capstone.be.config.GetVariables;
import capstone.be.configmanager.ConfigManagerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;

@RestController
@RequestMapping("/api/logindata")
@DependsOn("configManager")
@RequiredArgsConstructor
@Slf4j
public class LoginDataController {

    private final LoginDataService loginDataService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<LoginData> pagedResourcesAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<LoginData>>> getAllLoginData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "false") boolean alsoDeleted,
            @RequestParam(defaultValue = "") String q) {

        log.info("Fetching login data, page: {}, size: {}, sortBy: {}, direction: {}, alsoDeleted: {}, query: {}",
                page, size, sortBy, direction, alsoDeleted, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<LoginData> loginData = loginDataService.getAllLoginData(pageable, q, alsoDeleted);
        PagedModel<EntityModel<LoginData>> pagedModel = pagedResourcesAssembler.toModel(loginData);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/appusers/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<LoginData>>> getAllLoginDataByAppUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @PathVariable String username) {

        log.debug("Fetching login data for user {}, page: {}, size: {}, sortBy: {}", username, page, size, sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<LoginData> loginData = loginDataService.findByUsername(pageable, username);
        PagedModel<EntityModel<LoginData>> pagedModel = pagedResourcesAssembler.toModel(loginData);
        return ResponseEntity.ok(pagedModel);
    }
}
