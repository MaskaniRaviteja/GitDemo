//package main_suites;
//
//import static io.restassured.RestAssured.given;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.Properties;
//
//import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import com.relevantcodes.extentreports.ExtentReports;
//import com.relevantcodes.extentreports.ExtentTest;
//import com.relevantcodes.extentreports.LogStatus;
//
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import utils.Common;
//import utils.DatabaseConnection;
//
//public class RestAssuredTests {
//	Properties prop = new Properties();
//	public static ExtentReports extent;
//	public static ExtentTest test;
//	public static String currentTestName;
//	public static String baseURI;
//
//	@BeforeClass
//	public void setup() throws IOException, SQLException {
//		prop = Common.readPropertyFile();
//		baseURI = prop.getProperty("baseuri");
//		currentTestName = prop.getProperty("BVT");
//		extent = new ExtentReports(
//				prop.getProperty("Extentreports") + currentTestName + Common.dateNtime() + "_report" + ".html", true);
//		test = extent.startTest("Database Connection");
//		DatabaseConnection.establishDBConnection(prop.getProperty("dbname"), test);
//		test.log(LogStatus.PASS, "Database connected successfully.");
//	}
//
//	@AfterTest
//	public void closeApp() throws SQLException {
//		test = extent.startTest("Close Database Connection");
//		DatabaseConnection.closeDBConnection(test);
//		extent.flush();
//	}
//
//	@Test(priority = 1, enabled = true)
//	public void parentTableAsABatchInitial() throws Exception {
//		try {
//			System.out.println("Executing: parentTableAsABatchInitial");
//			test = extent.startTest("Parent Table As A Batch - Initial");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//
//			String cidFileName = prop.getProperty("initial_file");
//			Common.getTotalCountFromFiles(test, cidFileName,1);
//			String psxBatchID = Common.dateNtime();
//			String requestBody = RequestBodyBuilder.createRequestBody(true, cidFileName, psxBatchID);
//			System.out.println("Request Body: " + requestBody);
//			Response response = given()
//					.baseUri(baseURI)
//					.contentType(ContentType.JSON)
//					.body(requestBody)
//					.when()
//					.post("/idassigner/assignNSPKeysForParentTableAsABatch");
//			System.out.println("Status code: " + response.getStatusCode());
//			response.then().statusCode(200);
//			DatabaseConnection.printCVP(test);
//			test.log(LogStatus.PASS, "parentTableAsABatchInitial processed successfully.");
//
//		} catch (Exception e) {
//			System.out.println("parentTableAsABatchInitial posting failed: " + e);
//			test.log(LogStatus.INFO, "parentTableAsABatchInitial posting failed.", e);
//			test.log(LogStatus.FAIL, "parentTableAsABatchInitial failed.");
//		}
//	}
//
//	@Test(priority = 2, enabled = true)
//	public void validateTableCountsForInitial() throws Throwable {
//		try {
//			System.out.println("Executing: validateTableCountsForInitial");
//			test = extent.startTest("Validate Table Counts - Initial");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.validateTableCounts(test, true);
//			test.log(LogStatus.PASS, "validateTableCounts completed successfully.");
//		} catch (Exception e) {
//			System.out.println("validateTableCounts posting failed: " + e);
//			test.log(LogStatus.INFO, "validateTableCounts posting failed.", e);
//			test.log(LogStatus.FAIL, "validateTableCounts failed.");
//		}
//	}
//
//	@Test(priority = 3, enabled = true)
//	public void validateInitialMaxMinRecordID() throws Throwable {
//		try {
//			System.out.println("Executing: validateMaxMinRecordID");
//			test = extent.startTest("Validate Max Min Record ID - Initial");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.validateMaxMinRecordID(test);
//			test.log(LogStatus.PASS, "validateMaxMinRecordID completed successfully.");
//		} catch (Exception e) {
//			System.out.println("validateMaxMinRecordID posting failed: " + e);
//			test.log(LogStatus.INFO, "validateMaxMinRecordID posting failed.", e);
//			test.log(LogStatus.FAIL, "validateMaxMinRecordID failed.");
//		}
//	}
//
//	@Test(priority = 4, enabled = true)
//	public void validateInitialMaxMinNSPKey() throws Throwable {
//		try {
//			System.out.println("Executing: validateMaxMinNSPKey");
//			test = extent.startTest("Validate Max Min NSP Key - Initial");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.validateMaxMinNSPKey(test);
//			test.log(LogStatus.PASS, "validateMaxMinNSPKey completed successfully.");
//		} catch (Exception e) {
//			System.out.println("validateMaxMinNSPKey posting failed: " + e);
//			test.log(LogStatus.INFO, "validateMaxMinNSPKey posting failed.", e);
//			test.log(LogStatus.FAIL, "validateMaxMinNSPKey failed.");
//		}
//	}
//
//	@Test(priority = 5, enabled = true)
//	public void checkInitialDuplicateNSPKeys() throws Throwable {
//		try {
//			System.out.println("Executing: checkDuplicateNSPKeys");
//			test = extent.startTest("Check Duplicate NSP Keys - Initial");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.checkDuplicateNSPKeys(test);
//			test.log(LogStatus.PASS, "checkDuplicateNSPKeys completed successfully.");
//		} catch (Exception e) {
//			System.out.println("checkDuplicateNSPKeys posting failed: " + e);
//			test.log(LogStatus.INFO, "checkDuplicateNSPKeys posting failed.", e);
//			test.log(LogStatus.FAIL, "checkDuplicateNSPKeys failed.");
//		}
//	}
//
//	@Test(priority = 6, enabled = true)
//	public void checkInitialDuplicateRecordIDs() throws Throwable {
//		try {
//			System.out.println("Executing: checkDuplicateRecordIDs");
//			test = extent.startTest("Check Duplicate Record IDs - Initial");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.checkDuplicateRecordIDs(test);
//			test.log(LogStatus.PASS, "checkDuplicateRecordIDs completed successfully.");
//		} catch (Exception e) {
//			System.out.println("checkDuplicateRecordIDs posting failed: " + e);
//			test.log(LogStatus.INFO, "checkDuplicateRecordIDs posting failed.", e);
//			test.log(LogStatus.FAIL, "checkDuplicateRecordIDs failed.");
//		}
//	}
////
////	@Test(priority = 7, enabled = true)
////	public void parentTableAsABatchIncremental() throws Exception {
////		parentTableAsABatchIncremental(prop.getProperty("incremental_file"));
////	}
////
////	public void parentTableAsABatchIncremental(String cidFileName) throws Exception {
////		try {
////			System.out.println("Executing: parentTableAsABatchIncremental");
////			test = extent.startTest("Parent Table As A Batch - Incremental");
////			currentTestName = new Exception().getStackTrace()[0].getMethodName();
////			Thread.sleep(2000);
////
////			Common.getTotalCountFromFiles(test, cidFileName, 1);
////			String psxBatchID = Common.dateNtime();
////			String requestBody = RequestBodyBuilder.createRequestBody(false, cidFileName, psxBatchID);
////			System.out.println("Request Body: " + requestBody);
//	
//	
//	@Test(priority = 7, enabled = true)
//	public void parentTableAsABatchIncremental() throws Exception {
//	    test = extent.startTest("Parent Table As A Batch - Incremental");
//	    parentTableAsABatchIncremental(prop.getProperty("incremental_file"), test);
//	}
//
//	public void parentTableAsABatchIncremental(String cidFileName, ExtentTest test) throws Exception {
//	    try {
//	        System.out.println("Executing: parentTableAsABatchIncremental");
//	        currentTestName = new Exception().getStackTrace()[0].getMethodName();
//	        Thread.sleep(2000);
//			Common.getTotalCountFromFiles(test, cidFileName,1);
//
////	        Commoes(test, cidFileName, 1);
//	        String psxBatchID = Common.dateNtime();
//	        String requestBody = RequestBodyBuilder.createRequestBody(false, cidFileName, psxBatchID);
//	        System.out.println("Request Body: " + requestBody);
//	        
//			Response response = given()
//					.baseUri(baseURI)
//					.contentType(ContentType.JSON)
//					.body(requestBody)
//					.when()
//					.post("/idassigner/assignNSPKeysForParentTableAsABatch");
//			System.out.println("Status code: " + response.getStatusCode());
//			response.then().statusCode(200);
//			DatabaseConnection.printCVP(test);
//			test.log(LogStatus.PASS, "parentTableAsABatchIncremental processed successfully.");
//
//		} catch (Exception e) {
//			System.out.println("parentTableAsABatchIncremental posting failed: " + e);
//			test.log(LogStatus.INFO, "parentTableAsABatchIncremental posting failed.", e);
//			test.log(LogStatus.FAIL, "parentTableAsABatchIncremental failed.");
//		}
//	}
//	@Test(priority = 8, enabled = true)
//	public void validateTableCountsForIncremental() throws Throwable {
//		try {
//			System.out.println("Executing: validateTableCountsForIncremental");
//			test = extent.startTest("Validate Table Counts - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.validateTableCounts(test, false);
//			test.log(LogStatus.PASS, "validateTableCounts completed successfully.");
//		} catch (Exception e) {
//			System.out.println("validateTableCounts posting failed: " + e);
//			test.log(LogStatus.INFO, "validateTableCounts posting failed.", e);
//			test.log(LogStatus.FAIL, "validateTableCounts failed.");
//		}
//	}
//
//	@Test(priority = 9, enabled = true)
//	public void validateIncrementalMaxMinRecordID() throws Throwable {
//		try {
//			System.out.println("Executing: validateMaxMinRecordID");
//			test = extent.startTest("Validate Max Min Record ID - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.validateMaxMinRecordID(test);
//			test.log(LogStatus.PASS, "validateMaxMinRecordID completed successfully.");
//		} catch (Exception e) {
//			System.out.println("validateMaxMinRecordID posting failed: " + e);
//			test.log(LogStatus.INFO, "validateMaxMinRecordID posting failed.", e);
//			test.log(LogStatus.FAIL, "validateMaxMinRecordID failed.");
//		}
//	}
//
//	@Test(priority = 10, enabled = true)
//	public void validateIncrementalMaxMinNSPKey() throws Throwable {
//		try {
//			System.out.println("Executing: validateMaxMinNSPKey");
//			test = extent.startTest("Validate Max Min NSP Key - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.validateMaxMinNSPKey(test);
//			test.log(LogStatus.PASS, "validateMaxMinNSPKey completed successfully.");
//		} catch (Exception e) {
//			System.out.println("validateMaxMinNSPKey posting failed: " + e);
//			test.log(LogStatus.INFO, "validateMaxMinNSPKey posting failed.", e);
//			test.log(LogStatus.FAIL, "validateMaxMinNSPKey failed.");
//		}
//	}
//
//	@Test(priority = 11, enabled = true)
//	public void checkIncrementalDuplicateNSPKeys() throws Throwable {
//		try {
//			System.out.println("Executing: checkDuplicateNSPKeys");
//			test = extent.startTest("Check Duplicate NSP Keys - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.checkDuplicateNSPKeys(test);
//			test.log(LogStatus.PASS, "checkDuplicateNSPKeys completed successfully.");
//		} catch (Exception e) {
//			System.out.println("checkDuplicateNSPKeys posting failed: " + e);
//			test.log(LogStatus.INFO, "checkDuplicateNSPKeys posting failed.", e);
//			test.log(LogStatus.FAIL, "checkDuplicateNSPKeys failed.");
//		}
//	}
//
//	@Test(priority = 12, enabled = true)
//	public void checkIncrementalDuplicateRecordIDs() throws Throwable {
//		try {
//			System.out.println("Executing: checkDuplicateRecordIDs");
//			test = extent.startTest("Check Duplicate Record IDs - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//			DatabaseConnection.checkDuplicateRecordIDs(test);
//			test.log(LogStatus.PASS, "checkDuplicateRecordIDs completed successfully.");
//		} catch (Exception e) {
//			System.out.println("checkDuplicateRecordIDs posting failed: " + e);
//			test.log(LogStatus.INFO, "checkDuplicateRecordIDs posting failed.", e);
//			test.log(LogStatus.FAIL, "checkDuplicateRecordIDs failed.");
//		}
//	}
//	@Test(priority = 13, enabled = true)
//	public void checkIncrementalUpdates() throws Throwable {
//		try {
//			System.out.println("Executing: checkIncrementalUpdates");
//			test = extent.startTest("Check Updates - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//
//			String cidFileName = prop.getProperty("incremental_Updates");
//			Common.getTotalCountFromFiles(test, cidFileName,1);
//			String psxBatchID = Common.dateNtime();
//			String requestBody = RequestBodyBuilder.createRequestBody(false, cidFileName, psxBatchID);
//			System.out.println("Request Body: " + requestBody);
//			Response response = given()
//					.baseUri(baseURI)
//					.contentType(ContentType.JSON)
//					.body(requestBody)
//					.when()
//					.post("/idassigner/assignNSPKeysForParentTableAsABatch");
//			System.out.println("Status code: " + response.getStatusCode());
//			response.then().statusCode(200);
//
//			DatabaseConnection.checkUpdatesCount(test);
//			test.log(LogStatus.PASS, "checkIncrementalUpdates completed successfully.");
//		} catch (Exception e) {
//			System.out.println("checkIncrementalUpdates posting failed: " + e);
//			test.log(LogStatus.INFO, "checkIncrementalUpdates posting failed.", e);
//			test.log(LogStatus.FAIL, "checkIncrementalUpdates failed.");
//		}
//	}
//	@Test(priority = 14, enabled = true)
//	public void checkIncrementalDeletes() throws Throwable {
//		try {
//			System.out.println("Executing: checkIncrementalDeletes");
//			test = extent.startTest("Check Deletes - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//
//			String cidFileName = prop.getProperty("IncrementalDeletes");
//			Common.getTotalCountFromFiles(test, cidFileName,1);
//			String psxBatchID = Common.dateNtime();
//			String requestBody = RequestBodyBuilder.createRequestBody(false, cidFileName, psxBatchID);
//			System.out.println("Request Body: " + requestBody);
//			Response response = given()
//					.baseUri(baseURI)
//					.contentType(ContentType.JSON)
//					.body(requestBody)
//					.when()
//					.post("/idassigner/assignNSPKeysForParentTableAsABatch");
//			System.out.println("Status code: " + response.getStatusCode());
//			response.then().statusCode(200);
//
//			DatabaseConnection.checkUpdatesCount(test);
//			test.log(LogStatus.PASS, "checkIncrementalDeletes completed successfully.");
//		} catch (Exception e) {
//			System.out.println("checkIncrementalDeletes posting failed: " + e);
//			test.log(LogStatus.INFO, "checkIncrementalDeletes posting failed.", e);
//			test.log(LogStatus.FAIL, "checkIncrementalDeletes failed.");
//		}
//	}
//	@Test(priority = 15, enabled = false)
//	public void checkUpdates_Deletes() throws Throwable {
//		try {
//			parentTableAsABatchIncremental(prop.getProperty("INCREMENTAL_UD"), test);	
//			System.out.println("Executing: checkIncrementalUpdates_Deletes");
//			test = extent.startTest("Check Updates_Deletes - Incremental");
//			currentTestName = new Exception().getStackTrace()[0].getMethodName();
//
//			String cidFileName = prop.getProperty("INCREMENTAL_Updates_Deletes");
//			Common.getTotalCountFromFiles(test, cidFileName,1);
//			String psxBatchID = Common.dateNtime();
//			Thread.sleep(2000);
//
//			String requestBody = RequestBodyBuilder.createRequestBody(false, cidFileName, psxBatchID);
//			System.out.println("Request Body: " + requestBody);
//			Response response = given()
//					.baseUri(baseURI)
//					.contentType(ContentType.JSON)
//					.body(requestBody)
//					.when()
//					.post("/idassigner/assignNSPKeysForParentTableAsABatch");
//			System.out.println("Status code: " + response.getStatusCode());
//			response.then().statusCode(200);
//			Thread.sleep(10000);
//
//			DatabaseConnection.checkUpdates_Deletes(test);
//			test.log(LogStatus.PASS, "Check Updates_Deletes completed successfully.");
//		} catch (Exception e) {
//			System.out.println("Check Updates_Deletes posting failed: " + e);
//			test.log(LogStatus.INFO, "Check Updates_Deletes posting failed.", e);
//			test.log(LogStatus.FAIL, "Check Updates_Deletes failed.");
//		}
//	}
//
//}
