package processinitiation;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import main_suites.RequestBodyBuilder;
import main_suites.RestAssuredlog4j;
import utils.Common;
import utils.DataBaseUtils;

public class ProcessInitiation {
	private static final String baseURI = RestAssuredlog4j.baseURI;
	@Step("Starting processInitiate for {batchType} batch with file: {cidFileName}")
	public static void processInitiate(String cidFileName, Logger logger, boolean isInitial) throws Exception {
	    String batchType = isInitial ? "initial" : "incremental";
	    logger.info("Starting processInitiate for " + batchType + " batch with file: " + cidFileName);
	    Allure.step("Starting processInitiate for " + batchType + " batch with file: " + cidFileName);

	    try {
	        Thread.sleep(4000); // Ensure this delay is necessary for your use case
	        Common.getTotalCountFromFiles(cidFileName, 1);

	        String psxBatchID = Common.dateNtime();
	        String requestBody = RequestBodyBuilder.createRequestBody(isInitial, cidFileName, psxBatchID);
	        logger.debug("Request Body: " + requestBody);
	        logRequestBody("Request Body", requestBody);

	        Response response = given()
	            .baseUri(baseURI)
	            .contentType(ContentType.JSON)
	            .body(requestBody)
	            .when()
	            .post("/idassigner/assignNSPKeysForParentTableAsABatch");

	        int statusCode = response.getStatusCode();
	        String responseBody = response.getBody().asString();

	        logger.info("Received status code: " + statusCode);
	        logger.debug("Response Body: " + responseBody);

	        assertEquals(statusCode, 200, "Expected status code 200 but found " + statusCode);
	        Allure.step("Received status code: " + statusCode);
	        logResponseBody("Response Body", responseBody);
	        Allure.step("Request processed successfully with status code: " + statusCode);

	    } catch (Exception e) {
	        handleException("processInitiate for " + batchType + " batch", e);
	        throw e;
	    } finally {
	        logger.info("Completed processInitiate for " + batchType + " batch with file: " + cidFileName);
	        Allure.step("Completed processInitiate for " + batchType + " batch with file: " + cidFileName);
	    }
	}

	@Step("Handle exception for action: {action}")
	private static void handleException(String action, Exception e) {
	    Allure.step("Failed during " + action + ": " + e.getMessage(), Status.FAILED);
	    e.printStackTrace(); // Log the exception stack trace for further details
	}

	

	@Step("Starting getExistingAsABatch method execution for {batchType} batch.")
	public static void getExistingAsABatch(Logger logger, boolean isInitial, String cidFileName, boolean isAddtarget) throws Exception {
	    Thread.sleep(1000);
	    List<String> customerUniqueIDs = DataBaseUtils.getCustomerUniqueIDs(isInitial, isAddtarget);

	    int fileCount = 0;  // Initialize fileCount
	    int customerUniqueIDsCount = customerUniqueIDs.size();
	    String batchType;

	    if (isAddtarget) {
	        batchType = "AddTarget";
	        fileCount = Common.countRowsInExcel(cidFileName);
	    } else if (isInitial) {
	        batchType = "initial";
	        Common.getTotalCountFromFiles(cidFileName, 1);
	        fileCount = Common.FileCount;
	    } else {
	        batchType = "incremental";
	        Common.getTotalCountFromFiles(cidFileName, 1);
	        fileCount = Common.FileCount;
	    }

	    logger.info("Starting the execution of getExistingAsABatch method for the " + batchType + " batch.");
	    Allure.step("Starting the execution of getExistingAsABatch method for the " + batchType + " batch.");
	    Thread.sleep(1000);

	    for (String customerUniqueID : customerUniqueIDs) {
	        Thread.sleep(1000);

	        try {
	            // Create request body for the current customer unique ID
	            String requestBody = RequestBodyBuilder.creategetExistingAsABatchRequestBody(customerUniqueID);
	            logger.debug("Request Body for CustUnqID " + customerUniqueID + ": " + requestBody);
	            logRequestBody("Request Body for CustUnqID " + customerUniqueID, requestBody);

	            // Send the request and receive the response
	            Response response = given().baseUri(baseURI).contentType(ContentType.JSON).body(requestBody).when()
	                    .post("/idassigner/getExistingAsABatch");

	            int statusCode = response.getStatusCode();
	            String responseBody = response.getBody().asString();

	            // Parse and log the response body
	            JsonElement jsonElement = JsonParser.parseString(responseBody);
	            Gson gson = new GsonBuilder().setPrettyPrinting().create();
	            String prettyJsonString = gson.toJson(jsonElement);

	            logger.info("Received status code: " + statusCode);
	            logger.info("Response Body: " + prettyJsonString);

	            response.then().statusCode(200);
	            assertEquals(statusCode, 200, "Expected status code 200 but found " + statusCode);
	            Allure.step("Received status code: " + statusCode);
	            logResponseBody("Response Body", prettyJsonString);
	            Thread.sleep(1000);
	            String custUnqID = null;

	            if (jsonElement.isJsonObject()) {
	                JsonObject jsonObject = jsonElement.getAsJsonObject();
	                if (jsonObject.has("custUNQIDs")) {
	                    JsonArray custUNQIDsArray = jsonObject.getAsJsonArray("custUNQIDs");
	                    if (custUNQIDsArray.size() > 0) {
	                        JsonObject custUNQIDObject = custUNQIDsArray.get(0).getAsJsonObject();
	                        if (custUNQIDObject.has("custUnqID")) {
	                            custUnqID = custUNQIDObject.get("custUnqID").getAsString();
	                            System.out.println(custUnqID);
	                            Allure.step("Request processed successfully for custUnqID: " + custUnqID);
	                        } else {
	                            logger.warn("custUnqID not found in the response.");
	                        }
	                    }
	                }
	            }

	            if (custUnqID != null && custUnqID.equals(customerUniqueID)) {
	                String successCustUnqIDMessage = "Response custUnqID: " + custUnqID + " matches input custUnqID: " + customerUniqueID;
	                logger.info(successCustUnqIDMessage);
	                Allure.step(successCustUnqIDMessage);
	            } else {
	                String errorCustUnqIDMessage = "Mismatch between input custUnqID and response custUnqID: Input custUnqID = " 
	                        + customerUniqueID + ", Response custUnqID = " + custUnqID;
	                logger.error(errorCustUnqIDMessage);
	                Allure.step(errorCustUnqIDMessage, Status.FAILED);
	                throw new AssertionError(errorCustUnqIDMessage);
	            }

	           

	            Allure.step("Successfully processed retrieval of existing records as a batch for " + batchType
	                    + " for custUnqID: " + customerUniqueID);

	        } catch (Exception e) {
	            handleException("Retrieval of existing records as a batch for " + batchType, e, customerUniqueID);
	        }
	    }

	    if (customerUniqueIDsCount == fileCount) {
	        String successMessage = "CVP table custUnqIDs count: " + customerUniqueIDsCount + " === " + cidFileName
	                + " file count: " + fileCount;
	        logger.info(successMessage);
	        Allure.step(successMessage);
	    } else {
	        String errorMessage = "Mismatch between CVP table custUnqIDs count and file count: CVP table count = " 
	                + customerUniqueIDsCount + ", file count = " + fileCount;
	        logger.error(errorMessage);  
	        Allure.step(errorMessage, Status.FAILED);
	        throw new AssertionError(errorMessage);
	    }

	    logger.info("Completed execution of getExistingAsABatch method for the " + batchType + " batch.");
	    Allure.step("Completed execution of getExistingAsABatch method for the " + batchType + " batch.");
	}


	
	@Step("Handle exception for action: {action} and CustUnqID: {customerUniqueID}")
	private static void handleException(String action, Exception e, String customerUniqueID) {
	    String message = action + (customerUniqueID != null ? " for CustUnqID: " + customerUniqueID : "") + " failed";
	    Allure.step(message,Status.FAILED);
	    logError("Exception: " + e.getMessage());
	    logStackTrace(e);
	}

	
	@Step("Starting processing the Get AddTarget NewOrUpdate.")
	public static void postAddTargetNewOrUpdate(String cidFileName, Logger logger) throws Exception {
	    try {
	        Thread.sleep(1000);

	        logger.debug("Reading first column data from the Excel file: " + cidFileName);

	        // Read the first column data from the Excel file
	        List<String> fileFirstColumnData = Common.fileFirstColumnData(cidFileName, "Sheet1"); // Update the sheet name if necessary

	        // Check if fileFirstColumnData is null
	        if (fileFirstColumnData == null) {
	            throw new NullPointerException("fileFirstColumnData is null. Please check the Excel file and sheet name.");
	        }

	        logger.debug("First column data read successfully from the Excel file: " + cidFileName);
	        logger.debug("First column data: " + fileFirstColumnData);

	        // Get test data
	        List<Object[]> testData = Common.getTestData(cidFileName, "Sheet1"); // Update the sheet name if necessary

	        // Check if testData is null or empty
	        if (testData == null || testData.isEmpty()) {
	            throw new NullPointerException("testData is null or empty. Please check the Excel file and sheet name.");
	        }

	        for (int i = 0; i < testData.size(); i++) {
	            Object[] dataRow = testData.get(i);
	            String fileFirstColumn = fileFirstColumnData.get(i);

	            Thread.sleep(1000);
	            String requestBody = RequestBodyBuilder.createRequestBodyFromData(dataRow);
	            logger.debug("Request Body: " + requestBody);
	            logRequestBody("Request Body", requestBody);

	            Response response = given().baseUri(baseURI).contentType("application/json").body(requestBody).when()
	                    .post("/idassigner/getAddTargetNewOrUpdate");

	            int statusCode = response.getStatusCode();
	            String responseBody = response.getBody().asString();

	            logger.info("Received status code: " + statusCode);
	            logger.debug("Response Body: " + responseBody);

	            response.then().statusCode(200);
	            assertEquals(statusCode, 200, "Expected status code 200 but found " + statusCode);

	            Allure.step("Received status code: " + statusCode);
	            logResponseBody("Response Body", responseBody);

	            // Parse the response body to extract custUnqID
	            JsonElement jsonElement = JsonParser.parseString(responseBody);
	            String custUnqID = null;
	            if (jsonElement.isJsonObject()) {
	                JsonObject jsonObject = jsonElement.getAsJsonObject();
	                if (jsonObject.has("custUnqID")) {
	                    custUnqID = jsonObject.get("custUnqID").getAsString();
	                    Allure.step("Request processed successfully for custUnqID: " + custUnqID);
	                } else {
	                    logger.warn("custUnqID not found in the response.");
	                }
	            } else {
	                logger.warn("Response is not a valid JSON object.");
	            }

	            if (custUnqID != null && custUnqID.equals(fileFirstColumn)) {
	                String successCustUnqIDMessage = "Response custUnqID: " + custUnqID + " matches input custUnqID: " + fileFirstColumn;
	                logger.info(successCustUnqIDMessage);
	                Allure.step(successCustUnqIDMessage);
	            } else {
	                String errorCustUnqIDMessage = "Mismatch between input custUnqID and response custUnqID: Input custUnqID = " 
	                        + fileFirstColumn + ", Response custUnqID = " + custUnqID;
	                logger.error(errorCustUnqIDMessage);
	                Allure.step(errorCustUnqIDMessage, Status.FAILED);
	                throw new AssertionError(errorCustUnqIDMessage);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        Allure.step("Exception occurred during request processing: " + e.getMessage(),Status.FAILED);
	        logger.error("Exception occurred during request processing: ", e);
	    }
	}


	private static void logRequestBody(String title, String requestBody) {
		Allure.addAttachment(title, "application/json", requestBody);
	}

	private static void logResponseBody(String title, String responseBody) {
		Allure.addAttachment(title, "application/json", responseBody);
	}

	@Step("{message}")
	private static void logError(String message) {
		Allure.step(message);
	}

	@Attachment(value = "Stack Trace", type = "text/plain")
	private static String logStackTrace(Exception e) {
		StringBuilder stackTrace = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			stackTrace.append(element.toString()).append("\n");
		}
		return stackTrace.toString();
	}

//	@Attachment(value = "{name}", type = "application/json")
//	private static String logRequestBody(String name, String requestBody) {
//		return requestBody;
//	}
//
//	@Attachment(value = "{name}", type = "application/json")
//	private static String logResponseBody(String name, String responseBody) {
//		return responseBody;
//	}
}
