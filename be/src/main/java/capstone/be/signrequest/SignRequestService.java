package capstone.be.signrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserService;
import capstone.be.auth.SecurityUtils;
import capstone.be.doctemplate.DocTemplate;
import capstone.be.doctemplate.DocTemplateService;
import capstone.be.pusher.SignRequestPusher;
import capstone.be.signrequestchronology.SignRequestChronologyService;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignRequestService {

    private final SecurityUtils securityUtils;
    private final SignRequestRepository signRequestRepository;
    private final SignRequestChronologyService signRequestChronologyService;
    private final AppUserService appUserService;
    private final DocTemplateService docTemplateService;
    private final SignRequestPusher signRequestPusher;

    public Page<SignRequest> getAllSignRequests(Pageable pageable, String q, boolean alsoDeleted) {
        log.debug("Fetching sign requests with query '{}'", q);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort());

        if (q == null || q.isEmpty()) {
            return signRequestRepository.findAll(sortedPageable);
        }
        return signRequestRepository.omniSearch(q, sortedPageable);
    }

    public SignRequest getSignRequestById(Long id) {
        SignRequest signRequest = signRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SignRequest not found"));
        return signRequest;
    }

    public SignRequest saveSignRequest(SignRequest signRequest) {
        // No need to check, the checkSignRequest method will throw an exception if the
        // entities are not found
        SignRequest savedSignRequest = null;
        try {
            savedSignRequest = signRequestRepository.save(signRequest);
        } catch (Exception e) {
            String message = String.format("Error saving sign request: %s", e.getMessage());
            log.error(message);
            throw new RuntimeException(message);
        }

        // Return the saved sign request
        return savedSignRequest;
    }

    public SignRequest createSignRequest(SignRequestRequest signRequestRequest) {

        // Do all the checks on the entities
        SignRequest signRequest = checkSignRequest(null, signRequestRequest);

        // ... then call the save method
        SignRequest savedSignRequest = saveSignRequest(signRequest);

        // Add chronology
        signRequestChronologyService.addChronology(savedSignRequest, "CREATION", "COMPLETED", null);

        // ... then push it to the provider
        try {
            // Add chronology
            signRequestChronologyService.addChronology(savedSignRequest, "PUSHING", "STARTED", null);

            // Push the request to the provider
            //
            //
            signRequestPusher.pushSignRequest(savedSignRequest);
            //
            //
            //

            // Add chronology
            signRequestChronologyService.addChronology(savedSignRequest, "PUSHING", "COMPLETED", null);

        } catch (Exception e) {

            String message = String.format(
                    "The sign request with id '%s' was saved but there was an error pushing sign request: %s",
                    savedSignRequest.getId(), e.getMessage());

            log.error(message);

            // Add chronology
            signRequestChronologyService.addChronology(savedSignRequest, "PUSHING", "FAILED", message);

            throw new RuntimeException(message);
        }

        //
        // After all, return the saved sign request if everything is ok
        //
        return savedSignRequest;
    }

    public SignRequest updateSignRequest(Long id, SignRequestRequest signRequestRequest) {

        // Get the existing sign request (... only to obtain an exception if the sign
        // request does not exist)
        SignRequest existingSignRequest = getSignRequestById(id);

        // Add chronology
        signRequestChronologyService.addChronology(existingSignRequest, "UPDATE", "IN_PROGRESS", null);

        // Do all the checks on the entities
        SignRequest updatedSignRequest = checkSignRequest(id, signRequestRequest);
        // ...then set the id of the sign request
        updatedSignRequest.setId(id);
        // ... then call the save method
        SignRequest savedSignRequest = saveSignRequest(updatedSignRequest);

        // Add chronology
        signRequestChronologyService.addChronology(savedSignRequest, "UPDATE", "COMPLETED", null);

        return savedSignRequest;
    }

    public void deleteSignRequest(Long id) {
        SignRequest signRequest = getSignRequestById(id);

        // Add chronology
        signRequestChronologyService.addChronology(signRequest, "DELETION", "IN_PROGRESS", null);
        //
        // SignRequest must never be hard deleted from the database
        //
        signRequest.setDeleted(true);
        signRequestRepository.save(signRequest);

        // Add chronology
        signRequestChronologyService.addChronology(signRequest, "DELETION", "COMPLETED", null);

    }

    public List<SignRequest> getSignRequestByStatus(List<SignRequestStatus> statuses) {
        return signRequestRepository.findByStatusIn(statuses);
    }

    public List<SignRequestSimpleStats> getSimpleStats(Long userId) {
        if (userId != null) {
            return signRequestRepository.getSimpleStats(userId);
        }
        return signRequestRepository.getSimpleStats();
    }

    //
    // Long id is used to determine if the sign request is being created or
    // updated. This is fundamental to determine the business logic to be applied
    // because SignerByAppUser and DocTemplate could never be changed even from
    // Administrators.
    // CreatedByAppUser could be changed by only from Administrators.
    //
    private SignRequest checkSignRequest(Long id, SignRequestRequest signRequestRequest) {

        SignRequest newSignRequest = new SignRequest();

        //
        // Check if the signedByAppUser exists, if not signedByAppUserService will throw
        // an exception
        //
        if (id != null) {
            // Check if the SignRequest exists else throw an exception
            SignRequest signRequest = this.getSignRequestById(id);

            // Add chronology
            signRequestChronologyService.addChronology(signRequest, "CHECK", "IN_PROGRESS", null);

            //
            // SignerByAppUser could never be changed
            //
            if (signRequestRequest.getSignedByAppUserId() != 0 &&
                    signRequestRequest.getSignedByAppUserId() != signRequest.getSignedByAppUser().getId()) {
                throw new IllegalArgumentException("SignedByAppUser could never be changed");
            }

            //
            // DocTemplate could never be changed
            //
            if (signRequestRequest.getDocTemplateId() != 0
                    && signRequestRequest.getDocTemplateId() != signRequest.getDocTemplate().getId()) {
                throw new IllegalArgumentException("DocTemplate could never be changed");
            }

            //
            // Get authenticated user's role and check if it is an administrator
            // If it is an administrator, then the createdByAppUser could be changed
            //
            if (!securityUtils.isAdmin() &&
                    signRequestRequest.getCreatedByAppUserId() != signRequest.getCreatedByAppUser().getId()) {
                // .. is not an administrator, then the createdByAppUser is the same as the
                throw new IllegalArgumentException("CreatedByAppUser could be changed only from administrators");

            } else if (securityUtils.isAdmin() &&
                    signRequestRequest.getCreatedByAppUserId() != signRequest.getCreatedByAppUser().getId()) {
                // .. is an administrator, then the createdByAppUser could be changed
                // Check if the createdByAppUser exists, if not createdByAppUserService will
                // throw an exception
                AppUser newCreatedByAppUser = appUserService.getUserById(signRequestRequest.getCreatedByAppUserId());
                newSignRequest.setCreatedByAppUser(newCreatedByAppUser);
            } else if (signRequestRequest.getCreatedByAppUserId() == signRequest.getCreatedByAppUser().getId()) {
                // Not chenged, set the existing createdByAppUser
                newSignRequest.setCreatedByAppUser(signRequest.getCreatedByAppUser());
            }
            // Populate the newSignRequest with the existing signRequest
            // it does one by one to avoid the possibility of changing the signedByAppUser
            // and docTemplate
            newSignRequest.setId(signRequest.getId());
            newSignRequest.setDocumentPath(signRequest.getDocumentPath());
            newSignRequest.setSignedByAppUser(signRequest.getSignedByAppUser());
            newSignRequest.setDocTemplate(signRequest.getDocTemplate());
            newSignRequest.setStatus(signRequest.getStatus());

            // Add chronology
            signRequestChronologyService.addChronology(signRequest, "CHECK", "COMPLETED", null);
        } else {

            // Check if the signedByAppUser exists, if not signedByAppUserService will throw
            // an exception
            AppUser signedByAppUser = appUserService.getUserById(signRequestRequest.getSignedByAppUserId());

            // If the authenticated user is an administrator, then the createdByAppUser
            // could
            // be changed
            AppUser createdByAppUser = null;
            if (securityUtils.isAdmin() &&
                    signRequestRequest.getCreatedByAppUserId() != 0) {
                // Check if the createdByAppUser exists, if not createdByAppUserService will
                // throw an exception
                createdByAppUser = appUserService.getUserById(signRequestRequest.getCreatedByAppUserId());
            } else {
                // If the authenticated user is not an administrator, then the createdByAppUser
                // is the same as the signedByAppUser
                String username = securityUtils.getUsername();
                createdByAppUser = appUserService.getUserByIdOrUsernameOrEmail(username);
            }

            // Check if the docTemplate exists, if not docTemplateService will throw an
            // exception
            DocTemplate docTemplate = docTemplateService.getDocTemplateById(signRequestRequest.getDocTemplateId());

            newSignRequest.setSignedByAppUser(signedByAppUser);
            newSignRequest.setCreatedByAppUser(createdByAppUser);
            newSignRequest.setDocTemplate(docTemplate);
            newSignRequest.setStatus(SignRequestStatus.CREATED);
        }

        return newSignRequest;
    }

}
