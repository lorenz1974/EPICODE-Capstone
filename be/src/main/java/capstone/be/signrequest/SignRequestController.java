package capstone.be.signrequest;

import capstone.be.appuser.AppUser;
import capstone.be.config.GetVariables;
import capstone.be.observer.SignRequestObserver;
import capstone.be.pusher.SignRequestPusher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;
import java.util.List;

@RestController
@RequestMapping("/api/signrequests")
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
@Validated
public class SignRequestController {

    private final SignRequestService signRequestService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<SignRequest> pagedResourcesAssembler;
    private final SignRequestObserver signRequestObserver;
    private final SignRequestPusher signRequestPusher;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SignRequest>>> getSignRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction,
            @RequestParam(defaultValue = "false") boolean alsoDeleted,
            @RequestParam(defaultValue = "") String q) {

        log.debug("Fetching sign requests, page: {}, sortBy: {}, direction: {}, query: {}, alsoDeleted: {}", page,
                sortBy, direction, q, alsoDeleted);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<SignRequest> signRequests = signRequestService.getAllSignRequests(pageable, q, alsoDeleted);
        PagedModel<EntityModel<SignRequest>> pagedModel = pagedResourcesAssembler.toModel(signRequests);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SignRequest> getSignRequestById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean forcePush,
            @RequestParam(defaultValue = "false") boolean forcePoll) {
        SignRequest signRequest = signRequestService.getSignRequestById(id);

        //
        // To force the push of the request to send the request to the provider. For
        // instance if the signer details are changed and the request needs to be sent
        // because refused by the provider.
        //
        if (forcePush) {
            signRequestPusher.pushSignRequest(signRequest);
        }
        //
        // To force the poll of the request to refresh the result from the provider.
        //
        if (forcePoll) {
            signRequestObserver.pollSignRequest(signRequest);
        }

        return ResponseEntity.ok(signRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SignRequest> updateSignRequest(@PathVariable Long id,
            @Valid @RequestBody SignRequestRequest signRequestRequest) {
        SignRequest updatedSignRequest = signRequestService.updateSignRequest(id, signRequestRequest);
        return ResponseEntity.ok(updatedSignRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SignRequest> createSignRequest(@Valid @RequestBody SignRequestRequest signRequestRequest) {
        log.debug("Creating new sign request: {}", signRequestRequest);
        SignRequest createdSignRequest = signRequestService.createSignRequest(signRequestRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSignRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> deleteSignRequest(@PathVariable Long id) {
        signRequestService.deleteSignRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<SignRequestSimpleStats> getSimpleStats(
            @RequestParam(required = false) Long userId) {
        return signRequestService.getSimpleStats(userId);
    }
}
