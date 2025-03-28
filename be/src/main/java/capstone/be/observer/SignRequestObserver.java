package capstone.be.observer;

import capstone.be.auth.SecurityUtils;
import capstone.be.configmanager.ConfigManager;
import capstone.be.doctemplate.DocTemplate;
import capstone.be.restapi.RestApiClient;
import capstone.be.signrequest.SignRequest;
import capstone.be.signrequest.SignRequestService;
import capstone.be.signrequest.SignRequestStatus;
import capstone.be.signrequestchronology.SignRequestChronologyService;
import capstone.be.utility.DynamicJsonParser;

import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignRequestObserver {

    private final ConfigManager configManager;
    private final RestApiClient restApiClient;
    private final DynamicJsonParser dynamicJsonParser;
    private final SignRequestService signRequestService;
    private final SignRequestChronologyService signRequestChronologyService;
    private final SecurityUtils securityUtils;

    // The list of the requestStatus to be checked
    private final List<SignRequestStatus> signRequestStatuses = List.of(
            SignRequestStatus.SENT,
            SignRequestStatus.WAITING);

    //
    // ----------------------------------------------
    // Check the signRequests every 5 minutes
    // ----------------------------------------------
    //
    @Scheduled(fixedRate = 300000) // 5 minutes
    private void loopAndCheckSignRequests() {

        log.info("Checking sign requests...");

        // Get the list of the signRequest to be polled
        List<SignRequest> signRequests = signRequestService.getSignRequestByStatus(signRequestStatuses);

        // Verify if there is something to process
        if (signRequests.isEmpty()) {
            log.info("No sign requests to process");
            return;
        }
        // Process the sigRequest list
        for (SignRequest signRequest : signRequests) {
            pollSignRequest(signRequest);
        }
    }

    //
    // Not used for the moment
    //
    public void poolSignRequestById(long id) {
        // It will throw an exception if the sign request does not exist
        SignRequest signRequest = signRequestService.getSignRequestById(id);

        // It polls the sign request only if the status is to be polled
        if (signRequestStatuses.contains(signRequest.getStatus())) {
            pollSignRequest(signRequest);
        }
    }

    //
    // Poll the single signRequest
    //
    public void pollSignRequest(SignRequest signRequest) {

        log.info("Polling sign request: {}", signRequest.getId());

        log.info("Sign request: {}", signRequest);

        // It polls the sign request only if the status is to be polled
        // it's necessary because pollSignRequest could be called also directly from the
        // controller, not only from the scheduled checkSignRequests
        if (!signRequestStatuses.contains(signRequest.getStatus())) {
            log.warn("The sign request {} is not in the list of the statuses to be polled", signRequest.getId());
            return;
        }

        // Define the username, if anyy (that's because the user could force the polling
        // from the signRequestController)
        String username = securityUtils.getUsername() == null ? "system" : securityUtils.getUsername();

        // Poll the sign request
        //
        String startMessage = String.format("Polling started by the user '%s' for the sign request '%s'", username,
                signRequest.getId());
        signRequestChronologyService.addChronology(signRequest, "POLLING", "STARTED", startMessage);

        //
        // 1. verify if the signRequest.providerEnvelopeId exists in the current
        // signRequest, if not, update the status of the sign request to ANOMALY
        if (signRequest.getProviderEnvelopeId() == null && signRequest.getStatus() != SignRequestStatus.SENT) {
            String message = String.format("The sign request '%s' does not have a providerEnvelopeId",
                    signRequest.getId());
            setSignRequestStatus(signRequest, SignRequestStatus.ANOMALY, message);

            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", message);
            return;
        }

        // 2. if the signRequest.providerEnvelopeId exists, poll the provider service to
        // get the status of the envelope
        signRequestChronologyService.addChronology(signRequest, "POLLING", "IN_PROGRESS",
                "Begin polling the provider for the sign request");
        // get the status of the sign request
        Object envelopePollResponse = getEnvelopeStatus(signRequest);

        // 3. if the response is null, update the status of the sign request to ANOMALY
        if (envelopePollResponse == null) {
            String message = String.format("The sign request '%s' has a null response from the provider",
                    signRequest.getId());
            signRequestChronologyService.addChronology(signRequest, "POLLING", "FAILED", message);
            setSignRequestStatus(signRequest, SignRequestStatus.ANOMALY, message);
            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", message);
            return;
        }

        // 4. if the response is not null, set the status of the sign request in the
        // Class ProviderStatus
        ProviderStatus providerStatus = setProviderStatus(signRequest, envelopePollResponse);

        // isError?
        if (providerStatus.isError()) {
            String message = String.format("The sign request '%s' has encountered an error: %s", signRequest.getId(),
                    providerStatus.getStatusMessage());
            setSignRequestStatus(signRequest, SignRequestStatus.ERROR, message);

            log.error(message);

            // Do some other actions here...
            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", message);
        }
        // isFailed?
        else if (providerStatus.isFailed()) {
            String message = String.format("The sign request '%s' has failed: %s", signRequest.getId(),
                    providerStatus.getStatusMessage());
            setSignRequestStatus(signRequest, SignRequestStatus.FAILED, message);

            log.warn(message);

            // Do some other actions here...
            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", message);
        }
        // isCompleted?
        else if (providerStatus.isCompleted()) {
            String message = String.format("The sign request '%s' is completed", signRequest.getId());
            setSignRequestStatus(signRequest, SignRequestStatus.COMPLETED, message);

            log.info(message);

            // Do some other actions here...
            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", message);
        }
        // isWaiting
        else if (providerStatus.isWaiting()) {
            String message = String.format("The sign request '%s' is waiting", signRequest.getId());
            setSignRequestStatus(signRequest, SignRequestStatus.WAITING, message);

            log.info(message);

            // Do some other actions here...
            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", message);
        }

        // 5. if the status is not 'error', 'failed' or 'completed', update the
        // chronology and return
        else {
            SignRequestStatus signRequestStatus = SignRequestStatus.valueOf(providerStatus.getStatus().toUpperCase());
            String message = String.format("The sign request '%s' has the status of '%s'", signRequest.getId(),
                    signRequestStatus);
            setSignRequestStatus(signRequest, signRequestStatus, message);

            log.info(message);

            signRequestChronologyService.addChronology(signRequest, "POLLING", "COMPLETED", null);
        }

        // Give the status of the request
        log.info("Sign request {} has been polled. The status is: {}", signRequest.getId(), signRequest.getStatus());
        // ... and, in the end, it returns
        return;
    }

    //
    // Set the status of a sign request
    //
    private void setSignRequestStatus(SignRequest signRequest, SignRequestStatus signRequestStatus,
            String statusMessage) {
        // Set the status of the signRequest and save it
        signRequest.setStatus(signRequestStatus);
        SignRequest savedSignRequest = signRequestService.saveSignRequest(signRequest);

        // Set the chronology of the signRequest
        String message = statusMessage == null
                ? String.format("The sign request '%d' has a status of '%s'", savedSignRequest.getId(),
                        signRequestStatus.toString())
                : statusMessage;
        signRequestChronologyService.addChronology(signRequest, "POLLING", "IN_PROGRESS", message);

        // Log the message
        log.debug(message);
    }

    //
    // Poll the provider service to get the status of the envelope
    //
    private Object getEnvelopeStatus(SignRequest signRequest) {

        // 1. Retrieve the sign request DocTemplate to retrieve the information to poll
        // the provider
        //
        DocTemplate docTemplate = signRequest.getDocTemplate();
        log.debug("For sign request id {}, DocTemplate: {}", signRequest.getId(), docTemplate);

        // 2. Retrieve the provider token from the configuration
        String provider = docTemplate.getTemplateProvider();
        String token = configManager.getConfVariable(provider.toLowerCase() + "Token").toString();
        log.debug("Provider: {}", provider);
        log.debug("Provider token: {}", token);

        // 3. Prepare the URL and the token to call the provider service
        //
        String url = docTemplate.getTemplateApiCrudCheckEndpoint().replace("{{signRequest.Id}}",
                signRequest.getProviderEnvelopeId());
        log.debug("URL: {}", url);

        // 4. Call the provider service to get the status of the envelope
        //
        String envelopeStatusResponse;
        try {
            envelopeStatusResponse = restApiClient.getDataFromApi(url, token);
            log.debug("Response from the provider service: {}", envelopeStatusResponse);

        } catch (Exception e) {
            log.error("Error trying to call the provider service {}, with the token {}, error:{}", url, token,
                    e.getMessage());
            return null;
        }

        // 5. Parse the response to get the envelope status
        //
        Object envelopePollResponse;
        try {
            envelopePollResponse = dynamicJsonParser.parseJsonToClass("EnvelopePollResponse",
                    envelopeStatusResponse);
        } catch (Exception e) {
            log.error("Error trying to convert envelopeStatusResponse to EnvelopePollResponse object, error: {}",
                    e.getMessage());
            return null;
        }

        return envelopePollResponse;
    }

    //
    // The method verifies if the provider status is in error
    //
    private ProviderStatus setProviderStatus(SignRequest signRequest, Object envelopePollResponse) {

        ProviderStatus providerStatus = new ProviderStatus();

        // Get the template from the sign request
        DocTemplate docTemplate = signRequest.getDocTemplate();

        // Get the status field and the status values from the template
        String statusField = docTemplate.getTemplateApiCrudCheckStatusField();
        Set<String> completeValues = docTemplate.getTemplateApiCrudCheckCompletedValues();
        Set<String> failFieldValues = docTemplate.getTemplateApiCrudCheckFailedValues();
        Set<String> waitingValues = docTemplate.getTemplateApiCrudCheckWaitingValues();

        // Get the error field and the error values from the template
        String errorField = docTemplate.getTemplateApiCrudErrorField();
        String errorMessageField = docTemplate.getTemplateApiCrudErrorMessageField();
        Set<String> errorValues = docTemplate.getTemplateApiCrudErrorValues();

        log.debug("-- Status field: {}", statusField);
        log.debug("-- Complete values: {}", completeValues);
        log.debug("-- Fail field values: {}", failFieldValues);
        log.debug("-- Waiting values: {}", waitingValues);
        log.debug("-- Error field: {}", errorField);
        log.debug("-- Error values: {}", errorValues);

        //
        // CHECK IF STATUS IS IN 'ERROR'
        //
        // Check if the error field exists in the envelopePollResponse
        Object providerErrorValue = dynamicJsonParser.getValue(envelopePollResponse,
                "get" + String.valueOf(errorField));
        // If the error field does not exist, return false
        if (providerErrorValue != null) {
            // If the error field exists, check if the error value is in the set of the
            // error values
            String providerErrorValueString = providerErrorValue.toString();
            log.error("Provider error value: {}", providerErrorValueString);
            boolean isError = errorValues.stream()
                    .anyMatch(errorValue -> providerErrorValueString.toLowerCase().contains(errorValue.toLowerCase()));

            if (isError) {
                // Retrieve the error message
                Object providerErrorMessageValue = dynamicJsonParser.getValue(envelopePollResponse,
                        "get" + String.valueOf(errorMessageField));

                providerStatus.setStatus("error");
                providerStatus.setStatusMessage(providerErrorMessageValue != null
                        ? providerErrorMessageValue.toString()
                        : "The envelope status is: " + providerErrorValueString
                                + ". Check the documentation for more info");
                return providerStatus;
            }
        }

        //
        // CHECK IF STATUS IS 'COMPLETE' OR 'FAILED'
        //
        // Check if the status field exists in the envelopePollResponse
        Object providerStatusValue = dynamicJsonParser.getValue(envelopePollResponse,
                "get" + String.valueOf(statusField));
        // If the status field exists in the envelopePollResponse, check if the status
        // value is in the set of the completed values or failed values
        if (providerStatusValue != null) {
            String providerStatusValueString = providerStatusValue.toString();

            log.debug("Provider status value: {}", providerStatusValueString);

            // If the status value is in the set of the failed values, return true
            // The comparison is case insensitive
            boolean isFailed = failFieldValues.stream()
                    .anyMatch(
                            failedValue -> providerStatusValueString.toLowerCase().contains(failedValue.toLowerCase()));
            // Set the status and the status message
            if (isFailed) {
                providerStatus.setStatus("failed");
                providerStatus.setStatusMessage(providerStatusValueString);
                return providerStatus;
            }

            // If the status value is in the set of the completed values, return true
            // The comparison is case insensitive
            boolean isCompleted = completeValues.stream()
                    .anyMatch(completedValue -> providerStatusValueString.toLowerCase()
                            .contains(completedValue.toLowerCase()));
            if (isCompleted) {
                providerStatus.setStatus("completed");
                providerStatus.setStatusMessage(providerStatusValueString);
                return providerStatus;
            }

            // If the status value is in the set of the completed values, return true
            // The comparison is case insensitive
            boolean isWaiting = waitingValues.stream()
                    .anyMatch(
                            waitingValue -> providerStatusValueString.toLowerCase()
                                    .contains(waitingValue.toLowerCase()));
            if (isWaiting) {
                providerStatus.setStatus("waiting");
                providerStatus.setStatusMessage(providerStatusValueString);
                return providerStatus;
            }

            // If the status value is not in the set of the completed, failed or waiting
            // values, return the status and the status message from the provider
            providerStatus.setStatus(providerStatusValueString.toLowerCase());
            providerStatus.setStatusMessage(
                    "The envelope status is: " + providerStatusValueString + ". Check the documentation for more info");
        } else {
            providerStatus.setStatus("error");
            providerStatus.setStatusMessage("The status field is not present in the response");
        }

        // Return the status class
        return providerStatus;
    }
}
