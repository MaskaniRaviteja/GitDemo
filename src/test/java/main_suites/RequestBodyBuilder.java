package main_suites;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

public class RequestBodyBuilder {

    @Step("Creating request body with initial={initial}, cidFileName={cidFileName}, psxBatchID={psxBatchID}")
    public static String createRequestBody(boolean initial, String cidFileName, String psxBatchID) {
        String requestBody = "{\r\n" +
                "    \"initial\": " + initial + ",\r\n" +
                "    \"numberOfPartitions\": \"1\",\r\n" +
                "    \"cidFileNames\": [\r\n" +
                "        \"" + cidFileName + "\"\r\n" +
                "    ],\r\n" +
                "    \"psxBatchID\": \"" + psxBatchID + "\",\r\n" +
                "    \"sourceSystemName\": \"Customer\",\r\n" +
                "    \"clipbean\": \"true\"\r\n" +
                "}";
        logRequestBody("Request Body", requestBody);
        return requestBody;
    }

    @Step("Creating get existing as a batch request body with custUNQID={inputpcustunqid}")
    public static String creategetExistingAsABatchRequestBody(String inputpcustunqid) {
        String requestBody = "{\r\n" +
                "    \"sourceSystemName\": \"Customer\",\r\n" +
                "    \"custUNQIDs\": [\r\n" +
                "        {\"custUnqID\": \"" + inputpcustunqid + "\"}\r\n" +
                "    ]\r\n" +
                "}";
        logRequestBody("Get Existing As A Batch Request Body", requestBody);
        return requestBody;
    }



    public static String createRequestBodyFromData(Object[] dataRow) {
        String requestBodyTemplate = "{\n" +
                "\"lchgTime\": \"%s\",\n" +
                "\"partitionNumber\": \"%s\",\n" +
                "\"sourceSystemName\": \"%s\",\n" +
                "\"numberOfPartitions\": %s,\n" +
                "\"custUnqID\": \"%s\",\n" +
                "\"clipBeanNames\": \"%s\"\n" +
                "}";

        return String.format(requestBodyTemplate,
                dataRow[1], // lchgTime
                dataRow[2], // partitionNumber
                dataRow[3], // sourceSystemName
                dataRow[4].toString().replace(".0", ""), // numberOfPartitions
                dataRow[0], // custUnqID
                dataRow[5] // clipBeanNames
        );
    }

    @Step("Logging request body")
    @Attachment(value = "{name}", type = "application/json")
    private static String logRequestBody(String name, String requestBody) {
        return requestBody;
    }
    
    
}
