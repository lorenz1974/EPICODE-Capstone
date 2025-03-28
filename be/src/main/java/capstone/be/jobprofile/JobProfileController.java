package capstone.be.jobprofile;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RestController
@RequestMapping("/api/job-profiles")
@RequiredArgsConstructor
@Slf4j
public class JobProfileController {

    private final JobProfileService jobProfileService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<JobProfile> pagedResourcesAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<JobProfile>>> getJobProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "false") boolean alsoDeleted,
            @RequestParam(required = false) String q) {

        log.info("Fetching job profiles, page: {}, sortBy: {}, direction: {}, alsoDeleted: {}, query: {}", page,
                sortBy, direction, alsoDeleted, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<JobProfile> jobProfiles = jobProfileService.getAllJobProfiles(pageable, q, alsoDeleted);
        PagedModel<EntityModel<JobProfile>> pagedModel = pagedResourcesAssembler.toModel(jobProfiles);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JobProfile> getJobProfileById(@PathVariable Long id) {
        JobProfile jobProfile = jobProfileService.getJobProfileById(id);
        return ResponseEntity.ok(jobProfile);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public JobProfile createJobProfile(@Valid @RequestBody JobProfileRequest jobProfileRequest) {
        return jobProfileService.createJobProfile(jobProfileRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JobProfile> updateJobProfile(@PathVariable Long id,
            @Valid @RequestBody JobProfileRequest jobProfileRequest) {
        JobProfile updatedJobProfile = jobProfileService.updateJobProfile(id, jobProfileRequest);
        return ResponseEntity.ok(updatedJobProfile);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<JobProfile> deleteJobProfile(@PathVariable Long id) {
        jobProfileService.deleteJobProfile(id);
        return ResponseEntity.noContent().build();
    }
}
