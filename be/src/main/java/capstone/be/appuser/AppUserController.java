package capstone.be.appuser;

import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import capstone.be.config.GetVariables;
import capstone.be.signrequest.SignRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
@Validated
public class AppUserController {

    private final AppUserService appUserService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<AppUserGetAllResponse> pagedResourcesAssembler;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUser> me(@AuthenticationPrincipal UserDetails userDetails) {
        AppUser appUser = (AppUser) userDetails;
        // appUser.eraseCredentials();
        return ResponseEntity.ok(appUser);
    }

    @PostMapping("/register")
    @PreAuthorize("!hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser register(@Valid @RequestBody AppUserRegistrationRequest appUserRegistrationRequest) {
        return appUserService.registerUser(appUserRegistrationRequest);
    }

    @PostMapping("/authentication")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUserAuthResponse> login(@Valid @RequestBody AppUserLoginRequest loginRequest) {
        AppUserAuthResponse appUserAuthResponse = appUserService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        return ResponseEntity.ok(appUserAuthResponse);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<AppUserGetAllResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "surname") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "false") boolean alsoDeleted,
            @RequestParam(required = false) String q) {
        log.debug("Fetching users, page: {}, sortBy: {}, direction: {}, query: {}, alsoDeleted: {}", page, sortBy,
                direction, q, alsoDeleted);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<AppUserGetAllResponse> appUsersLight = appUserService.getAllUsers(pageable, q, alsoDeleted);
        PagedModel<EntityModel<AppUserGetAllResponse>> pagedModel = pagedResourcesAssembler.toModel(appUsersLight);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUser> getUserById(@PathVariable Long id) {
        AppUser user = appUserService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id,
            @Valid @RequestBody AppUserUpdateRequest appUserRegistrationRequest) {
        AppUser updatedUser = appUserService.updateUser(id, appUserRegistrationRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> deleteUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean forceDeletion) {
        appUserService.deleteUser(id, forceDeletion);
        return ResponseEntity.noContent().build();
    }
}