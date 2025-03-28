package capstone.be.signrequestchronology;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import capstone.be.config.GetVariables;
import capstone.be.signrequest.SignRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sign-request-chronologies")
@RequiredArgsConstructor
@Slf4j
public class SignRequestChronologyController {

        private final SignRequestService signRequestService;
        private final GetVariables getVariables;
        private final PagedResourcesAssembler<SignRequestChronology> pagedResourcesAssembler;
        private final SignRequestChronologyService signRequestChronologyService;

        @GetMapping
        public ResponseEntity<PagedModel<EntityModel<SignRequestChronology>>> getSignRequests(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "25") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                        @RequestParam(defaultValue = "false") boolean alsoDeleted,
                        @RequestParam(defaultValue = "") String q) {

                log.info("Fetching sign requests, page: {}, sortBy: {}, direction: {}, query: {}, alsoDeleted: {}",
                                page,
                                sortBy, direction, q, alsoDeleted);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
                Page<SignRequestChronology> signRequestsChronologies = signRequestChronologyService
                                .getAllSignRequestChronologies(pageable, q, alsoDeleted); // added alsoDeleted here
                PagedModel<EntityModel<SignRequestChronology>> pagedModel = pagedResourcesAssembler.toModel(
                                signRequestsChronologies);
                return ResponseEntity.ok(pagedModel);
        }

        @GetMapping("/{id}")
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<SignRequestChronology> getById(@PathVariable Long id) {
                SignRequestChronology signRequestChronology = signRequestChronologyService
                                .getSignRequestChronologyById(id);
                return ResponseEntity.ok(signRequestChronology);
        }

        //
        //
        // @PostMapping, @PutMapping and @DeleteMapping are not used, as chronologies
        // are always managed
        // by the system
        //
        //

        // @DeleteMapping("/{id}")
        // @ResponseStatus(HttpStatus.NO_CONTENT)
        // public ResponseEntity<Void> delete(@PathVariable Long id) {
        // signRequestChronologyService.deleteSignRequestChronology(id);
        // return ResponseEntity.noContent().build();
        // }
}
