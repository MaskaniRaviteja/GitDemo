package main_suites;

import java.awt.AWTException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;
import processinitiation.ProcessInitiation;
import services.ServiceController;
import utils.AllureLogger;
import utils.Common;
import utils.DataBaseUtils;
import utils.DatabaseConnection;

public class RestAssuredlog4j {
	private static final Logger logger = LogManager.getLogger(RestAssuredlog4j.class);

	static {
		Configurator.initialize(null,
				"file:///C:/Users/ptpl-395.POSIDEX/eclipse-workspace/Prime360CloudV1/src/resources/log4j2.xml");
	}

	Properties prop = new Properties();
	public static String currentTestName;
	public static String baseURI;
	public static String cidFileName;

	@BeforeClass
	public void setup() throws IOException, SQLException {
	    AllureLogger.logMethodStart("setup");
	    logger.info("Starting setup process.");

	    try {
	        prop = Common.readPropertyFile();
	        baseURI = prop.getProperty("baseuri");
	        currentTestName = prop.getProperty("BVT");
	        DatabaseConnection.establishDBConnection(prop.getProperty("dbname"));
	        DataBaseUtils.initializeConnection();

	        logger.info("Setup process completed successfully.");
	    } catch (Exception e) {
	        logger.error("Setup process failed: ", e);
	        Allure.step("Setup process failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd("setup");
	    }
	}

	@AfterTest
	public void closeApp() throws SQLException {
	    AllureLogger.logMethodStart("closeApp");
	    logger.info("Starting application closure process.");

	    try {
	        DatabaseConnection.closeDBConnection();
	        logger.info("Application closure process completed successfully.");
	    } catch (Exception e) {
	        logger.error("Application closure process failed: ", e);
	        Allure.step("Application closure process failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd("closeApp");
	    }
	}

	@Test(priority = 1, enabled = true)
	@Step("Test Activate Service")
	@Description("This test activates services defined in the properties file.")
	public void TC001_TestActivateService() throws IOException, InterruptedException, AWTException {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC001_TestActivateService.");

	    try {
	        ServiceController serviceController = new ServiceController();
	        Properties properties = Common.readPropertyFile();
	        String[] serviceNames = properties.getProperty("serviceNames").split(",");

	        for (String serviceName : serviceNames) {
	            Allure.step("Activating service: " + serviceName, () -> {
	                serviceController.openCMD();
	                serviceController.activateService(serviceName);
	            });
	            Thread.sleep(14000); // Wait for service activation
	        }

	        logger.info("All services activated successfully.");
	    } catch (Exception e) {
	        logger.error("Service activation failed: ", e);
	        Allure.step("Service activation failed for: " + currentTestName + " with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 2, enabled = true)
	@Step("Parent Table As A Batch - Initial")
	@Description("This test processes the initial batch for the parent table.")
	public void TC002_ParentTableAsABatchInitial() throws Exception {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC002_ParentTableAsABatchInitial.");

	    try {
	        cidFileName = prop.getProperty("initial_file");
	        Allure.step("Processing initial batch for the parent table", () -> {
	            ProcessInitiation.processInitiate(cidFileName, logger, true); // Updated method call
	            DataBaseUtils.printCVP(true);
	        });

	        logger.info("Initial batch processing for parent table completed successfully.");
	    } catch (Exception e) {
	        logger.error("Initial batch processing for parent table failed: ", e);
	        Allure.step("Initial batch processing for parent table failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 3, enabled = true)
	@Step("Validate Table Counts - Initial")
	@Description("This test validates table counts for the initial batch.")
	public void TC003_ValidateTableCountsForInitial() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC003_ValidateTableCountsForInitial.");

	    try {
	        Allure.step("Getting total count from files", () -> {
	            Common.getTotalCountFromFiles(cidFileName, 1);
	        });

	        Allure.step("Validating table counts", () -> {
	            DataBaseUtils.validateTableCounts(true);
	        });

	        logger.info("Validation of table counts for initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Validation of table counts for initial batch failed: ", e);
	        Allure.step("Validation of table counts for initial batch failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 4, enabled = true)
	@Step("Validate Max Min Record ID - Initial")
	@Description("This test validates the max and min record IDs for the initial batch.")
	public void TC004_ValidateInitialMaxMinRecordID() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC004_ValidateInitialMaxMinRecordID.");

	    try {
	        Allure.step("Validating max and min record IDs", () -> {
	            DataBaseUtils.validateMaxMinRecordID();
	        });

	        logger.info("Validation of Max and Min Record IDs for initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Validation of Max and Min Record IDs for initial batch failed: ", e);
	        Allure.step("Validation of Max and Min Record IDs for initial batch failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 5, enabled = true)
	@Step("Validate Max Min NSP Key - Initial")
	@Description("This test validates the max and min NSP keys for the initial batch.")
	public void TC005_ValidateInitialMaxMinNSPKey() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC005_ValidateInitialMaxMinNSPKey.");

	    try {
	        Allure.step("Validating max and min NSP keys", () -> {
	            DataBaseUtils.validateMaxMinNSPKey();
	        });

	        logger.info("Validation of Max and Min NSP Keys for initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Validation of Max and Min NSP Keys for initial batch failed: ", e);
	        Allure.step("Validation of Max and Min NSP Keys for initial batch failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 6, enabled = true)
	@Step("Check Duplicate NSP Keys - Initial")
	@Description("This test checks for duplicate NSP keys in the initial batch.")
	public void TC006_CheckInitialDuplicateNSPKeys() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC006_CheckInitialDuplicateNSPKeys.");

	    try {
	        Allure.step("Checking duplicate NSP keys", () -> {
	            DataBaseUtils.checkDuplicateNSPKeys();
	        });

	        logger.info("Check for duplicate NSP Keys in initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Check for duplicate NSP Keys in initial batch failed: ", e);
	        Allure.step("Check for duplicate NSP Keys in initial batch failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 7, enabled = true)
	@Step("Check Duplicate Record IDs - Initial")
	@Description("This test checks for duplicate record IDs in the initial batch.")
	public void TC007_CheckInitialDuplicateRecordIDs() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC007_CheckInitialDuplicateRecordIDs.");

	    try {
	        Allure.step("Checking duplicate record IDs", () -> {
	            DataBaseUtils.checkDuplicateRecordIDs();
	        });

	        logger.info("Check for duplicate Record IDs in initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Check for duplicate Record IDs in initial batch failed: ", e);
	        Allure.step("Check for duplicate Record IDs in initial batch failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 8, enabled = true)
	@Step("Check Duplicate Cust_Unq_Id's - Initial")
	@Description("This test checks for duplicate Cust_Unq_Id's in the Initial batch.")
	public void TC008_CheckInitialDuplicateCustUnqIDs() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC008_CheckInitialDuplicateCustUnqIDs.");

	    try {
	        Allure.step("Checking duplicate Cust_Unq_Id's for the Initial batch", () -> {
	            DataBaseUtils.checkDuplicateCustUnqIDs();
	        });

	        logger.info("Check for duplicate Cust_Unq_Id's in initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Check for duplicate Cust_Unq_Id's in initial batch failed: ", e);
	        Allure.step("Check for duplicate Cust_Unq_Id's in initial batch failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}
	@Test(priority = 9, enabled = true)
	@Step("Get Existing As A Batch - Initial")
	@Description("This test retrieves existing records as a batch for the initial batch.")
	public void TC009_GetExistingAsABatchInitial() throws Exception {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC009_GetExistingAsABatchInitial.");

	    try {
	        cidFileName = prop.getProperty("initial_file");

	        Allure.step("Retrieving existing records as a batch", () -> {
	            ProcessInitiation.getExistingAsABatch(logger, true, cidFileName, false);
	        });

	        logger.info("Retrieval of existing records as a batch for initial batch completed successfully.");
	    } catch (Exception e) {
	        logger.error("Retrieval of existing records as a batch for initial batch failed: ", e);
	        Allure.step("Retrieval of existing records as a batch for initial batch failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 10, enabled = true)
	@Step("Parent Table As A Batch - Incremental")
	@Description("This test processes the incremental batch for the parent table.")
	public void TC010_ParentTableAsABatchIncremental() throws Exception {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC010_ParentTableAsABatchIncremental.");

	    try {
	        cidFileName = prop.getProperty("incremental_file");
	        Thread.sleep(5000);

	        Allure.step("Processing incremental batch for the parent table", () -> {
	            ProcessInitiation.processInitiate(cidFileName, logger, false);
	            DataBaseUtils.printCVP(false);
	        });

	        logger.info("ParentTableAsABatchIncremental processed successfully.");
	    } catch (Exception e) {
	        logger.error("ParentTableAsABatchIncremental failed: ", e);
	        Allure.step("ParentTableAsABatchIncremental failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 11, enabled = true)
	@Step("Validate Table Counts - Incremental")
	@Description("This test validates table counts for the incremental batch.")
	public void TC011_ValidateTableCountsForIncremental() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC011_ValidateTableCountsForIncremental.");

	    try {
	        Allure.step("Validating table counts for the incremental batch", () -> {
	            DataBaseUtils.validateTableCounts(false);
	        });

	        logger.info("ValidateTableCounts completed successfully.");
	    } catch (Exception e) {
	        logger.error("ValidateTableCounts failed: ", e);
	        Allure.step("ValidateTableCounts failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 12, enabled = true)
	@Step("Validate Max Min NSP Key - Incremental")
	@Description("This test validates the max and min NSP keys for the incremental batch.")
	public void TC012_ValidateIncrementalMaxMinNSPKey() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC012_ValidateIncrementalMaxMinNSPKey.");

	    try {
	        Allure.step("Validating max and min NSP keys for the incremental batch", () -> {
	            DataBaseUtils.validateMaxMinNSPKey();
	        });

	        logger.info("ValidateMaxMinNSPKey completed successfully.");
	    } catch (Exception e) {
	        logger.error("ValidateMaxMinNSPKey failed: ", e);
	        Allure.step("ValidateMaxMinNSPKey failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 13, enabled = true)
	@Step("Check Duplicate NSP Keys - Incremental")
	@Description("This test checks for duplicate NSP keys in the incremental batch.")
	public void TC013_CheckIncrementalDuplicateNSPKeys() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC013_CheckIncrementalDuplicateNSPKeys.");

	    try {
	        Allure.step("Checking duplicate NSP keys for the incremental batch", () -> {
	            DataBaseUtils.checkDuplicateNSPKeys();
	        });

	        logger.info("CheckDuplicateNSPKeys completed successfully.");
	    } catch (Exception e) {
	        logger.error("CheckDuplicateNSPKeys failed: ", e);
	        Allure.step("CheckDuplicateNSPKeys failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 14, enabled = true)
	@Step("Check Duplicate Record IDs - Incremental")
	@Description("This test checks for duplicate record IDs in the incremental batch.")
	public void TC014_CheckIncrementalDuplicateRecordIDs() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC014_CheckIncrementalDuplicateRecordIDs.");

	    try {
	        Allure.step("Checking duplicate record IDs for the incremental batch", () -> {
	            DataBaseUtils.checkDuplicateRecordIDs();
	        });

	        logger.info("CheckDuplicateRecordIDs completed successfully.");
	    } catch (Exception e) {
	        logger.error("CheckDuplicateRecordIDs failed: ", e);
	        Allure.step("CheckDuplicateRecordIDs failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 15, enabled = true)
	@Step("Check Duplicate Cust_Unq_Id's - Incremental")
	@Description("This test checks for duplicate Cust_Unq_Id's in the incremental batch.")
	public void TC015_CheckIncrementalDuplicateCustUnqIDs() throws Throwable {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC015_CheckIncrementalDuplicateCustUnqIDs.");

	    try {
	        Allure.step("Checking duplicate Cust_Unq_Id's for the incremental batch", () -> {
	            DataBaseUtils.checkDuplicateCustUnqIDs();
	        });

	        logger.info("CheckDuplicateCustUnqIDs completed successfully.");
	    } catch (Exception e) {
	        logger.error("CheckDuplicateCustUnqIDs failed: ", e);
	        Allure.step("CheckDuplicateCustUnqIDs failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 16, enabled = true)
	@Step("Get Existing As A Batch - Incremental")
	@Description("This test retrieves existing records as a batch for the incremental batch.")
	public void TC016_GetExistingAsABatchIncremental() throws Exception {
	    currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC016_GetExistingAsABatchIncremental.");

	    try {
	        cidFileName = prop.getProperty("incremental_file");

	        Allure.step("Retrieving existing records as a batch", () -> {
	            ProcessInitiation.getExistingAsABatch(logger, false, cidFileName, false);
	        });

	        logger.info("Get Existing As A Batch - Incremental completed successfully.");
	    } catch (Exception e) {
	        logger.error("Get Existing As A Batch - Incremental failed: ", e);
	        Allure.step("Get Existing As A Batch - Incremental failed: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	
	
	@Test(priority = 17, enabled = true)
	@Step("Get AddTarget NewOrUpdate")
	@Description("This test processes the Get AddTarget NewOrUpdate.")
	public void TC017_getAddTargetNewOrUpdate() throws Exception {
	    String currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC017_getAddTargetNewOrUpdate.");

	    try {
	        String addTargetFile = prop.getProperty("Addtarget");

	        Allure.step("Processing Get AddTarget NewOrUpdate with file: " + addTargetFile, () -> {
	            ProcessInitiation.postAddTargetNewOrUpdate(addTargetFile, logger);
	        });

	        logger.info("Get AddTarget NewOrUpdate completed successfully.");
	        Allure.step("Successfully processed Get AddTarget NewOrUpdate with file: " + addTargetFile);
	    } catch (Exception e) {
	        logger.error("Processing of Get AddTarget NewOrUpdate failed: ", e);
	        Allure.step("Processing of Get AddTarget NewOrUpdate failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 18, enabled = true)
	@Step("Get Existing As A Batch - AddTarget")
	@Description("This test retrieves existing records as a batch for the AddTarget batch.")
	public void TC018_GetExistingAsABatchAddTarget() throws Exception {
	    String currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC018_GetExistingAsABatchAddTarget.");

	    try {
	        String cidFileName = prop.getProperty("Addtarget");

	        Allure.step("Retrieving existing records as a batch for file: " + cidFileName, () -> {
	            ProcessInitiation.getExistingAsABatch(logger, false, cidFileName, true);
	        });

	        logger.info("Get Existing As A Batch - AddTarget completed successfully.");
	    } catch (Exception e) {
	        logger.error("Get Existing As A Batch - AddTarget failed: ", e);
	        Allure.step("Get Existing As A Batch - AddTarget failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}
	@Test(priority = 19, enabled = true)
	@Step("Check Updates - Incremental")
	@Description("This test checks updates in the incremental batch.")
	public void TC019_CheckIncrementalUpdates() throws Throwable {
	    String currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC019_CheckIncrementalUpdates.");

	    try {
	        Allure.step("Executing incremental updates.", () -> {
	            ProcessInitiation.processInitiate(prop.getProperty("incremental_Updates"), logger, false);
	        });
	        Allure.step("Checking updates count", () -> {
	            DataBaseUtils.checkUpdatesCount();
	        });

	        logger.info("CheckIncrementalUpdates completed successfully.");
	    } catch (Exception e) {
	        logger.error("CheckIncrementalUpdates failed: ", e);
	        Allure.step("CheckIncrementalUpdates failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 20, enabled = true)
	@Step("Check Deletes - Incremental")
	@Description("This test checks deletes in the incremental batch.")
	public void TC020_CheckIncrementalDeletes() throws Throwable {
	    String currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC020_CheckIncrementalDeletes.");

	    try {
	        Allure.step("Executing incremental deletes.", () -> {
	            ProcessInitiation.processInitiate(prop.getProperty("IncrementalDeletes"), logger, false);
	        });
	        Allure.step("Checking deletes count", () -> {
	            DataBaseUtils.checkDeletesCount();
	        });

	        logger.info("CheckIncrementalDeletes completed successfully.");
	    } catch (Exception e) {
	        logger.error("CheckIncrementalDeletes failed: ", e);
	        Allure.step("CheckIncrementalDeletes failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}
	@Test(priority = 21, enabled = true)
	@Step("Check Updates and Deletes - Incremental")
	@Description("This test checks updates and deletes in the incremental batch.")
	public void TC021_CheckUpdates_Deletes() throws Throwable {
	    String currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC021_CheckUpdates_Deletes.");

	    try {
	        Allure.step("Executing incremental updates and deletes.", () -> {
	            ProcessInitiation.processInitiate(prop.getProperty("INCREMENTAL_UD"), logger, true);
	            ProcessInitiation.processInitiate(prop.getProperty("INCREMENTAL_Updates_Deletes"), logger, false);
	        });
	        Allure.step("Checking updates and deletes count", () -> {
	            DataBaseUtils.checkUpdates_Deletes(false);
	        });

	        logger.info("Check Updates_Deletes completed successfully.");
	    } catch (Exception e) {
	        logger.error("Check Updates_Deletes failed: ", e);
	        Allure.step("Check Updates_Deletes failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

	@Test(priority = 22, enabled = true)
	@Step("Check Updates, Deletes, and New Inserts - Incremental")
	@Description("This test checks updates, deletes, and new inserts in the incremental batch.")
	public void TC022_CheckUpdates_Deletes_NewInserts() throws Throwable {
	    String currentTestName = new Exception().getStackTrace()[0].getMethodName();
	    AllureLogger.logMethodStart(currentTestName);
	    logger.info("Starting TC022_CheckUpdates_Deletes_NewInserts.");

	    try {
	        Allure.step("Executing incremental updates, deletes, and new inserts.", () -> {
	            ProcessInitiation.processInitiate(prop.getProperty("INCREMENTAL_UDN"), logger, true);
	            ProcessInitiation.processInitiate(prop.getProperty("INCREMENTAL_Updates_Deletes_Newinserts"), logger, false);
	        });
	        Allure.step("Checking updates, deletes, and new inserts count", () -> {
	            DataBaseUtils.checkUpdates_Deletes(true);
	        });

	        logger.info("Check Updates_Deletes_NewInserts completed successfully.");
	    } catch (Exception e) {
	        logger.error("Check Updates_Deletes_NewInserts failed: ", e);
	        Allure.step("Check Updates_Deletes_NewInserts failed with exception: " + e.getMessage(), Status.FAILED);
	        throw e;
	    } finally {
	        AllureLogger.logMethodEnd(currentTestName);
	    }
	}

}
