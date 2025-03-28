package capstone.be.branch;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserDetailsShortResponse;
import capstone.be.appuser.AppUserService;
import capstone.be.config.GetVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Slf4j
public class BranchController {

    private final BranchService branchService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<Branch> pagedResourcesAssembler;
    private final PagedResourcesAssembler<AppUser> appUserPagedResourcesAssembler;
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Branch>>> getBranches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "description") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false) String q) {

        log.info("Fetching branches, page: {}, sortBy: {}, direction: {}, query: {}", page, sortBy, direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Branch> branches = branchService.getAllBranches(pageable, q);
        PagedModel<EntityModel<Branch>> pagedModel = pagedResourcesAssembler.toModel(branches);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Branch> getBranchById(@PathVariable Long id) {
        Branch branch = branchService.getBranchById(id);
        return ResponseEntity.ok(branch);
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<PagedModel<EntityModel<AppUser>>> getEmployeesByBranchId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "surnameName") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<AppUser> users = appUserService.getBranchById(pageable, id);
        PagedModel<EntityModel<AppUser>> pagedModel = appUserPagedResourcesAssembler.toModel(users);
        return ResponseEntity.ok(pagedModel);
    }
}
