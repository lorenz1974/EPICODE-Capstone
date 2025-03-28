package capstone.be.signrequestchronology;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import capstone.be.auth.SecurityUtils;
import capstone.be.signrequest.SignRequest;
import capstone.be.utility.ObjectToJSONConverter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SignRequestChronologyService {

    private final SignRequestChronologyRepository repository;
    private final SecurityUtils securityUtils;

    public Page<SignRequestChronology> getAllSignRequestChronologies(Pageable pageable, String q,
            boolean alsoDeleted) {
        log.debug("Fetching getAllSignRequestChronologies requests with query '{}' and includeDeleted='{}'", q,
                alsoDeleted);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort());
        if (q == null || q.isEmpty()) {
            return repository.findAll(sortedPageable);
        }
        return repository.omniSearch(q, sortedPageable);
    }

    public SignRequestChronology getSignRequestChronologyById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("SignRequestChronology not found"));
    }

    public SignRequestChronology createSignRequestChronology(SignRequestChronology chronology) {
        return repository.save(chronology);
    }

    public void deleteSignRequestChronology(Long id) {
        //
        // Never hard delete, always soft delete
        //
        SignRequestChronology chronology = getSignRequestChronologyById(id);
        chronology.setDeleted(true);

        repository.save(chronology);
    }

    // Method without eventData object
    public void addChronology(SignRequest signRequest, String type, String status, String message) {
        addChronology(signRequest, type, status, null, message);
    }

    // Method with eventData object
    public void addChronology(SignRequest signRequest, String type, String status, Object eventData, String message) {

        SignRequestChronology signRequestChronology = new SignRequestChronology();

        type = type.toUpperCase();
        status = status.toUpperCase();

        signRequestChronology.setEventType(ChronologyType.valueOf(type));
        signRequestChronology.setStatus(ChronologyStatus.valueOf(status));

        String userName = securityUtils.getUsername() != null ? securityUtils.getUsername() : "System";

        // Set the message based on the type
        if (message != null && !message.isEmpty()) {
            signRequestChronology.setMessage(message);
        } else {
            switch (type) {
                case "CREATION":
                    signRequestChronology.setMessage("Sign request created by user '" +
                            userName + "'");
                    break;
                case "UPDATE":
                    signRequestChronology.setMessage("Sign request updated by user '" +
                            userName + "'");
                    break;
                case "DELETION":
                    signRequestChronology.setMessage("Sign request deleted by user '" +
                            userName + "'");
                    break;
                case "CHECK":
                    signRequestChronology.setMessage("Sign request checked by user '" +
                            userName + "'");
                    break;
                case "RETRIEVING":
                    signRequestChronology.setMessage("Sign request retrieved from the database by user '" +
                            userName + "'");
                    break;
                case "POLLING":
                    signRequestChronology.setMessage("Sign request polled by user '" +
                            userName + "'");
                    break;
                case "TEMPLATE_BINDING":
                    break;
                case "DATA_PREPARATION":
                    break;
                case "PUSHING":
                    signRequestChronology.setMessage("Sign request pushed by user '" +
                            userName + "'");
                    break;
                case "PROVIDER_ACK":
                    break;
                case "SIGNATURE_STARTED":
                    break;
                case "SIGNATURE_COMPLETED":
                    break;
                case "SIGNATURE_FAILED":
                    break;
                case "CANCELLATION":
                    break;
                case "EXPIRATION":
                    break;
                case "NOTIFICATION_SENT":
                    break;
                case "STORAGE":
                    break;
                case "ERROR_HANDLING":
                    break;
                default:
                    signRequestChronology.setMessage("Type not defined by user '" +
                            securityUtils.getUsername() + "'");
                    break;
            }
        }

        // Try to record the sign request data in the JSON format
        try {
            signRequestChronology.setEventData(ObjectToJSONConverter.convert(eventData));
        } catch (Exception e) {
            log.error("Error converting EventData Object to JSON: " + e.getMessage());
        }
        // Set the sign request in the chronology
        signRequestChronology.setSignRequest(signRequest);
        // Save the chronology with the sign request chronology service
        try {
            createSignRequestChronology(signRequestChronology);
        } catch (Exception e) {
            log.error("Error saving sign request chronology: " + e.getMessage());
        }
    }
}
