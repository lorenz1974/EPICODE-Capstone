package capstone.be.pusher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import capstone.be.configmanager.ConfigManager;
import capstone.be.doctemplate.DocTemplate;
import capstone.be.restapi.RestApiClient;
import capstone.be.signrequest.SignRequest;
import capstone.be.signrequest.SignRequestRepository;
import capstone.be.signrequest.SignRequestStatus;
import capstone.be.signrequestchronology.SignRequestChronologyService;
import capstone.be.utility.DynamicJsonParser;
import capstone.be.utility.DynamicQueryExecutor;
import capstone.be.utility.FilePath;
import capstone.be.utility.ReplacePlaceHolders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignRequestPusher {

    private final DynamicQueryExecutor dynamicQueryExecutor;
    private final DynamicJsonParser dynamicJsonParser;
    private final RestApiClient restApiClient;
    private final ReplacePlaceHolders replacePlaceHolders;
    private final SignRequestChronologyService signRequestChronologyService;
    private final ConfigManager configManager;

    // Use the repository and not the service to avoid circular dependencies
    // Here why have nothing to check, so it's ok
    private final SignRequestRepository signRequestRepository;

    public void pushSignRequest(long Id) {
        // Get the sign request from the repository
        SignRequest signRequest = signRequestRepository.findById(Id).orElseThrow();

        // Push the sign request
        pushSignRequest(signRequest);
    }

    public void pushSignRequest(SignRequest signRequest) {

        // Create a new Query class
        // This class will store the SQL query, the status of the query and the results
        // of the query
        Query query = new Query();

        // Get the Id of the SignRequest
        long signRequestId = signRequest.getId();
        // Get the doc template of the SignRequest
        DocTemplate docTemplate = signRequest.getDocTemplate();

        // Add a chronology to the SignRequest
        String message = String.format("The request with id %s is being pushed to the provider", signRequestId);
        signRequestChronologyService.addChronology(signRequest, "PUSHING", "IN_PROGRESS", message);

        // Set the SQL string from the template and set it to the Query class
        query.setSql(createSqlQuery(signRequest));

        // Set the results to the Query class
        List<Map<String, Object>> queryResults = dynamicQueryExecutor.executeNativeQuery(query.getSql());
        query.setResults(queryResults);

        // Check if the results are ok and if not, return
        query = checkQueryResults(signRequest, query);

        // Check if results are in error and throws an exception
        if (query.isError()) {
            message = "Error in the query: " + query.getStatusMessage();
            signRequestChronologyService.addChronology(signRequest, "PUSHING", "FAILED", query, message);
            // Set the status of the signRequest to ERROR
            setSignRequestStatus(signRequest, SignRequestStatus.ERROR, message);

            log.error(message);

            // Throw an exception
            throw new RuntimeException(message);
        }

        // Push the request to the provider and return the response in the form of a
        // dynamic object
        Object pushApiResult = pushToTheProvider(query, docTemplate);

        // 3. if the pushApiResult is null, update the status of the sign request to
        // ANOMALY
        if (pushApiResult == null) {
            message = String.format("The sign request '%s' has a null push Api response from the provider",
                    signRequest.getId());
            signRequestChronologyService.addChronology(signRequest, "PUSHING", "FAILED", query, message);
            setSignRequestStatus(signRequest, SignRequestStatus.ANOMALY, message);
            // Close the chronology
            signRequestChronologyService.addChronology(signRequest, "PUSHING", "COMPLETED", message);

            log.warn(message);

            // Throw an exception
            throw new RuntimeException(message);
        }

        // Check if the pushApiResult is in error and throws an exception
        query = checkPushApiResult(signRequest, pushApiResult, query);
        if (query.isError()) {
            message = "Error in the push to the provider: " + query.getStatusMessage();
            signRequestChronologyService.addChronology(signRequest, "PUSHING", "FAILED", pushApiResult, message);
            // Set the status of the signRequest to ERROR
            setSignRequestStatus(signRequest, SignRequestStatus.ERROR, message);

            log.error(message);

            // Throw an exception
            throw new RuntimeException(message);
        }

        // Check if the query contains the providerFieldValue and set it to the
        // signRequest. Set also the status of the signRequest to SENT.
        // If not, throw an exception
        if (query.getProviderFieldValue() != null) {
            signRequest = setEnvelopeIdAndSent(signRequest, query.getProviderFieldValue());

            message = String.format("The request with id %s has an envelope id of %s",
                    signRequest.getId(), query.getProviderFieldValue());
            signRequestChronologyService.addChronology(signRequest, "PUSHING", "COMPLETED", pushApiResult, message);
        } else {
            message = String.format("The request with id %s has no envelope id and it should have it at this point!",
                    signRequest.getId());
            signRequestChronologyService.addChronology(signRequest, "PUSHING", "FAILED", pushApiResult, message);
            // Set the status of the signRequest to ERROR
            setSignRequestStatus(signRequest, SignRequestStatus.ERROR, message);

            log.error(message);

            // Throw an exception
            throw new RuntimeException(message);
        }

        // Close the Pushing to the provider
        message = String.format("The request with id %s has been successfully pushed to the provider",
                signRequest.getId());
        signRequestChronologyService.addChronology(signRequest, "PUSHING", "COMPLETED", message);

        log.info("--- Sign request with id: {}", signRequest.getId());
        log.info("    Created by the user : {}", signRequest.getCreatedByAppUser().getNameSurname());
        log.info("    For the user        : {}", signRequest.getSignedByAppUser().getNameSurname());
        log.info("    With the envelopeId : {}", signRequest.getProviderEnvelopeId());
        log.info("    Has been successfully pushed to the provider.");

        //
        // Then return ...
        return;
    }

    //
    // Push the request to the provider and manage the exception
    // The method returns the response from the provider
    //
    // The method receives a Query object and a DocTemplate object
    // The Query object contains the SQL query, the status of the query and the
    // results of the query
    // The DocTemplate object contains the provider and the URL of the provider
    //
    private Object pushToTheProvider(Query query, DocTemplate docTemplate) {

        // 1. Replace placeholders in the results populating resultsReplaced
        //
        Query replacedQuery = replacePlaceHoldersInResults(query);

        // 2. Retrieve the provider token from the configuration
        //
        String provider = docTemplate.getTemplateProvider();
        String token = configManager.getConfVariable(provider.toLowerCase() + "Token").toString();
        log.debug("Provider: {}", provider);
        log.debug("Provider token: {}", token);

        // 3. Retrieve the URL from the configuration
        //
        String url = docTemplate.getTemplateProviderApiEndpoint();
        log.debug("Provider URL: {}", url);

        // 4. Retrive the JSON object from the results
        //
        String jsonTemplate = replacedQuery.getResultsReplaced().get(0).get("templatejson").toString();
        log.debug("JSON template: {}", jsonTemplate);

        // 5. Push the request to the provider and manage the exception
        //
        String pushApiResponse;
        try {
            // POST the data to the provider
            pushApiResponse = restApiClient.postDataToApi(url, jsonTemplate, token);
            log.debug("Response from the provider service: {}", pushApiResponse);
        } catch (Exception e) {
            String message = String.format(
                    "Error in pushing the request to the provider: URL: {}, Token: {}, Error: {}", url, token,
                    e.getMessage());
            log.error(message);
            throw new RuntimeException(message);
        }

        Object pushApiResult;
        try {
            pushApiResult = dynamicJsonParser.parseJsonToClass("PushApiResult", pushApiResponse);
            log.debug("PushApiResult: {}", pushApiResult);
        } catch (Exception e) {
            log.error("Error trying to convert pushApiResponse to PushApiResult object, error: {}",
                    e.getMessage());
            return null;
        }
        return pushApiResult;
    }

    //
    // PushDocTemplateFile
    // Before push the request to the provider, it's needed to push the template
    // file the end user have to sign.
    // The method receives a DocTemplate object, extract the provider, the file
    // path, the endpoint where to push the file and return the file id to be used
    // in the request.
    // If there is an error it throws an exception.
    //
    public String pushDocTemplateFile(DocTemplate docTemplate) {
        // Get the provider token from the configuration
        String provider = docTemplate.getTemplateProvider();
        String token = configManager.getConfVariable(provider.toLowerCase() + "Token").toString();
        log.debug("pushDocTemplateFile - Provider: {}", provider);
        log.debug("pushDocTemplateFile - Provider token: {}", token);

        // Get the URL from the configuration
        String url = docTemplate.getTemplateProviderFileApiEndpoint();
        log.debug("pushDocTemplateFile - Provider URL: {}", url);

        // Get the file path from the configuration
        String filePath = docTemplate.getTemplatePath();
        log.debug("pushDocTemplateFile - File path: {}", filePath);

        // Get the file archive root path from the configuration
        String filesArchiveRootPath = configManager.getConfVariable("filesArchiveRootPath").toString();
        log.debug("pushDocTemplateFile - Files archive root path: {}", filesArchiveRootPath);

        // Create the file object
        File file = new File(filesArchiveRootPath, filePath);
        log.debug("pushDocTemplateFile - Full file path: {}", file.getAbsolutePath());

        // Verify if the file to push exists
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file path: " + filePath);
        }

        // Get the file id field to search for the answer
        String fileIdField = docTemplate.getTemplateProviderFileIdField();
        log.debug("pushDocTemplateFile - File id field: {}", fileIdField);

        try {
            // POST the file to the provider
            // Push the file to the provider and manage the exception
            String pushApiResponse = restApiClient.postFileToApi(url, file, token);
            log.debug("pushDocTemplateFile - Response from the provider service: {}", pushApiResponse);
        } catch (Exception e) {
            String message = String.format(
                    "pushDocTemplateFile - Error pushing the file to the provider: URL: %s, Token: %s, Error: %s",
                    url,
                    token, e.getMessage());
            log.error(message);
            throw new RuntimeException(message);
        }

        return "Push result";
    }

    // Check all possible errors and set the query status
    private Query checkQueryResults(SignRequest signRequest, Query query) {

        List<Map<String, Object>> queryResults = query.getResults();

        if (queryResults.isEmpty()) {
            query.setStatus("error");
            query.setStatusMessage(
                    String.format("No results found to populate the request id %s for the provider",
                            signRequest.getId()));
            return query;
        } else if (queryResults.size() == 1) {
            query.setStatus("ok");
            query.setStatusMessage("One single result found");
            return query;
        } else {
            query.setStatus("error");
            query.setStatusMessage(
                    String.format("More than one result found to populate the request id %s for the provider",
                            signRequest.getId()));
            return query;
        }
    }

    //
    // Replace placeholders in the results populating resultsReplaced
    // this is because in the database are store 'templates' of the request to push
    // to the provider. Before to send it, it's needed to replace the placeholders
    // with the real values
    //
    private Query replacePlaceHoldersInResults(Query query) {

        /*
         * DATA STRUCTURE OF THE QUERY RESULTS
         *
         * Query results: [{envelopname=Richiesta firma per documento generico per
         * {{SignerName}} {{SignerSurname}}, emailsubject=Richiesta firma per documento
         * generico per {{SignerName}} {{SignerSurname}}, creatorcellphone=325-989-1636,
         * signeremail=rosaria.serr@lombardi.it, creatoremail=sibilla.piras@caruso.net,
         * creatorname=Sibilla, signercellphone=(598) 491-6880, signername=Rosaria,
         * creatorsurname=Piras, signersurname=Serr, templatejson={...}}]
         */

        // Get the first result from the query
        // There is always only one result in the Query
        List<Map<String, Object>> results = query.getResults();

        // Create a new list to store the replaced results
        List<Map<String, Object>> resultReplaced = new ArrayList<>();
        Map<String, Object> replacedEntry = new HashMap<>();

        // Loop through all fields in the result (just one by design)
        Map<String, Object> result = results.get(0);
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Now it replaces the placeholders in all fields of the result
            // with the value of all other fields in the result
            // log.info("Field: {} = {}", key, value);
            replacedEntry.put(key, replacePlaceHolders.of(value.toString(), results));
            resultReplaced.add(replacedEntry);
        }

        // Set the replaced results to the Query class
        query.setResultsReplaced(resultReplaced);

        log.debug("Replaced results: {}", resultReplaced);

        // return the replaced results
        return query;
    }

    private String createSqlQuery(SignRequest signRequest) {
        // Define the DocTemplate of the request
        DocTemplate docTemplate = signRequest.getDocTemplate();

        // Get the SQL string from the template e set it to the Query class
        // replacing the placeholders with the SignRequest Id
        String sqlQuery = docTemplate.getTemplateSqlQuery();
        sqlQuery = replacePlaceHolders.of(sqlQuery, "{{signRequest.Id}}", String.valueOf(signRequest.getId()));

        String message = String.format("Template SQL query is: %s", sqlQuery.replace("\n", sqlQuery));
        log.debug(message);

        return sqlQuery;
    }

    private Query checkPushApiResult(SignRequest signRequest, Object pushApiResult, Query query) {

        // Get the doc template of the SignRequest
        DocTemplate docTemplate = signRequest.getDocTemplate();

        // Get the name of the field to check the EnvelopeId and the status values from
        // the template
        String fieldToCheck = docTemplate.getTemplateProviderApiEndpointIdField();

        // Get the error field and the error values from the template
        String errorField = docTemplate.getTemplateApiCrudErrorField();
        String errorMessageField = docTemplate.getTemplateApiCrudErrorMessageField();
        Set<String> errorValues = docTemplate.getTemplateApiCrudErrorValues();

        //
        // CHECK IF THE API RESPONSE CONTAINS THE ENVELOPE ID FIELD
        //
        // Check if the field exists in the pushApiResult
        Object fieldValeuToCheck = dynamicJsonParser.getValue(pushApiResult,
                "get" + String.valueOf(fieldToCheck));

        if (fieldValeuToCheck != null) {
            log.debug("Provider Envelope ID: {}", fieldValeuToCheck);

            query.setStatus("ok");
            query.setStatusMessage("Provider Envelope ID found: EnvelopeId is " + fieldValeuToCheck.toString());
            query.setProviderFieldValue(fieldValeuToCheck.toString());
            return query;
        } else {
            log.warn("Provider Envelope ID not found in the pushApiResult for the request with id: {}",
                    signRequest.getId());
        }

        //
        // CHECK IF STATUS IS IN 'ERROR'
        //
        // Check if the error field exists in the envelopePollResponse
        Object providerErrorValue = dynamicJsonParser.getValue(
                pushApiResult, "get" + String.valueOf(errorField));
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
                Object providerErrorMessageValue = dynamicJsonParser.getValue(pushApiResult,
                        "get" + String.valueOf(errorMessageField));

                query.setStatus("error");
                query.setStatusMessage(providerErrorMessageValue != null
                        ? providerErrorMessageValue.toString()
                        : "Push status is: " + providerErrorValueString + ". Check the documentation for more info");
                return query;
            }
        }

        // In any case return the query
        return query;
    }

    //
    // Set the status of a sign request
    //
    private void setSignRequestStatus(SignRequest signRequest, SignRequestStatus signRequestStatus,
            String statusMessage) {
        // Set the status of the signRequest and save it
        signRequest.setStatus(signRequestStatus);
        SignRequest savedSignRequest = signRequestRepository.save(signRequest);

        // Set the chronology of the signRequest
        String message = statusMessage == null
                ? String.format("The sign request '%d' has a status of '%s'", savedSignRequest.getId(),
                        signRequestStatus.toString())
                : statusMessage;
        signRequestChronologyService.addChronology(signRequest, "PUSHING", "IN_PROGRESS", message);

        // Log the message
        log.debug(message);
    }

    private SignRequest setEnvelopeIdAndSent(SignRequest signRequest, String providerFieldValue) {
        // Set the status of the signRequest and save it
        signRequest.setProviderEnvelopeId(providerFieldValue);
        signRequest.setStatus(SignRequestStatus.SENT);
        SignRequest savedSignRequest = signRequestRepository.save(signRequest);

        // Set the chronology of the signRequest
        String message = String.format("The sign request '%d' has a status of 'SENT' with envelope id '%s'",
                savedSignRequest.getId(), providerFieldValue);
        signRequestChronologyService.addChronology(savedSignRequest, "PUSHING", "IN_PROGRESS", message);

        // Log the message
        log.debug(message);

        return savedSignRequest;
    }
}
