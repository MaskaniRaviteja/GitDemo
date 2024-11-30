package utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;

public class DataBaseUtils {

	private static int storedMaxRecordID = 0;
	private static int maxRecordID = 0;
	private static Connection connection; // Ensure this is initialized properly
	static Properties prop = new Properties();

	public static void initializeConnection() {
		connection = DatabaseConnection.getConnection();
	}

	public static void printCVP(boolean isInitial) {
		if (isInitial) {
			// For initial runs, set the stored max record ID to 0
			storedMaxRecordID = getMaxRecordID();
		}

		try {
			List<List<String>> cvpData = returnCVP(); // Fetch all records from the table
			Thread.sleep(1000);

			for (List<String> cvpDetails : cvpData) {
				String customerUniqueID = cvpDetails.get(0);
				String recordID = cvpDetails.get(1);
				String nspkey = cvpDetails.get(2);
				String insertTime = cvpDetails.get(3);
				String partitionNumber = cvpDetails.get(4);

				if (isInitial) {
					logInitialRecord(customerUniqueID, nspkey, recordID, insertTime, partitionNumber);
				}
			}

			if (!isInitial) {
				logIncrementalRecords();
			}
		} catch (Exception e) {
			Allure.step("Failed to retrieve data from PSX_CUSTUNQID_VS_PARTITIONNO table", () -> {
				throw new RuntimeException(e);
			});
		}
	}

	@Step("CUST_UNQ_ID: {customerUniqueID}  NSPKEY: {nspkey}  RECORD_ID: {recordID}  INSERT_TIME: {insertTime}  PARTITION_NUMBER: {partitionNumber}")
	private static void logInitialRecord(String customerUniqueID, String nspkey, String recordID, String insertTime,
			String partitionNumber) {
		Allure.step("Initial record: CUST_UNQ_ID: " + customerUniqueID + "  NSPKEY: " + nspkey + "  RECORD_ID: "
				+ recordID + "  INSERT_TIME: " + insertTime + "  PARTITION_NUMBER: " + partitionNumber);

		// This method will log the initial record details
	}

	@Step("Logging incremental records")
	private static void logIncrementalRecords() {
		String query = "SELECT * FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE RECORD_ID > " + storedMaxRecordID;

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				String customerUniqueID = resultSet.getString("CUST_UNQ_ID");
				String recordID = resultSet.getString("RECORD_ID");
				String nspkey = resultSet.getString("NSPKEY");
				String insertTime = resultSet.getString("INSERT_TIME");
				String partitionNumber = resultSet.getString("PARTITION_NUMBER");

				Allure.step(
						"Incremental record: CUST_UNQ_ID: " + customerUniqueID + "  NSPKEY: " + nspkey + "  RECORD_ID: "
								+ recordID + "  INSERT_TIME: " + insertTime + "  PARTITION_NUMBER: " + partitionNumber);

				// Update the storedMaxRecordID to the latest record ID
				storedMaxRecordID = Math.max(storedMaxRecordID, Integer.parseInt(recordID));
			}
		} catch (SQLException e) {
			e.printStackTrace(); // Handle SQLException appropriately
		}
	}

	private static List<List<String>> returnCVP() throws SQLException {
		List<List<String>> cvpData = new ArrayList<>();
		String query = "SELECT * FROM PSX_CUSTUNQID_VS_PARTITIONNO ORDER BY RECORD_ID ASC";

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

			while (resultSet.next()) {
				List<String> cvpDetails = new ArrayList<>();
				cvpDetails.add(resultSet.getString("CUST_UNQ_ID"));
				cvpDetails.add(resultSet.getString("RECORD_ID"));
				cvpDetails.add(resultSet.getString("NSPKEY"));
				cvpDetails.add(resultSet.getString("INSERT_TIME"));
				cvpDetails.add(resultSet.getString("PARTITION_NUMBER"));

				cvpData.add(cvpDetails);
			}
		}

		return cvpData;
	}

	public static int getMaxRecordID() {
		String query = "SELECT MAX(RECORD_ID) AS max_id FROM PSX_CUSTUNQID_VS_PARTITIONNO";

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				maxRecordID = resultSet.getInt("max_id");
			}
		} catch (SQLException e) {
			e.printStackTrace(); // Handle SQLException appropriately
		}

		return maxRecordID;
	}
	
	
	public static List<String> getCustomerUniqueIDs(boolean isInitial, boolean isAddTarget) throws Exception {
	    List<String> customerUniqueIDs = new ArrayList<>();

	    if (isInitial) {
	        // Fetch all customer unique IDs for initial run
	        List<List<String>> cvpData = returnCVP();
	        Thread.sleep(1000);

	        for (List<String> cvpDetails : cvpData) {
	            customerUniqueIDs.add(cvpDetails.get(0));
	        }
	        getMaxRecordID();
	        // Update storedMaxRecordID after initial run
//	        storedMaxRecordID_get = 
	    } else if (isAddTarget) {
	        Thread.sleep(1000);

	        // Fetch customer unique IDs for addtarget
	        String query = "SELECT CUST_UNQ_ID FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE RECORD_ID > "
	                + maxRecordID;
	        Thread.sleep(1000); 

	        try (Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery(query)) {

	            while (resultSet.next()) {
	                String customerUniqueID = resultSet.getString("CUST_UNQ_ID");
	                customerUniqueIDs.add(customerUniqueID);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace(); // Handle SQLException appropriately
	        }
	        getMaxRecordID();
	    } else {
	        Thread.sleep(1000);

	        // Fetch customer unique IDs starting from storedMaxRecordID for incremental run
	        String query = "SELECT CUST_UNQ_ID FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE RECORD_ID > "
	                + maxRecordID;
	        Thread.sleep(1000);

	        try (Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery(query)) {

	            while (resultSet.next()) {
	                String customerUniqueID = resultSet.getString("CUST_UNQ_ID");
	                customerUniqueIDs.add(customerUniqueID);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace(); // Handle SQLException appropriately
	        }
	        getMaxRecordID();
	    }

	    return customerUniqueIDs;
	}


	public static void validateTableCounts(boolean isInitial) throws Exception {
		int count1 = getTableRowCount("PSX_CUSTUNQID_VS_PARTITIONNO", isInitial);
		int count2 = getTableRowCount("PSX_CVP_STAGING", isInitial);
		int count3 = getTableRowCount("PSX_CVP_TEMP", isInitial);
		int count4 = getTableRowCount("PSX_IDASSIGNER_INPUT", isInitial);

		if (isInitial) {
			validateInitialCounts(count1, count2, count3, count4);
		} else {
			validateIncrementalCounts(count1, count2, count3, count4);
		}
	}

	@Step("Validating initial table counts")
	private static void validateInitialCounts(int count1, int count2, int count3, int count4) {
		if (count1 == count2 && count2 == count4 && count1 == Common.FileCount) {
			Allure.step("Initial: All tables have the same number of rows: PSX_CUSTUNQID_VS_PARTITIONNO: " + count1
					+ ", PSX_CVP_STAGING: " + count2 + ", PSX_IDASSIGNER_INPUT: " + count4 + ", FileCount: "
					+ Common.FileCount, Status.PASSED);
		} else {
			Allure.step("Initial: Row counts are different: PSX_CUSTUNQID_VS_PARTITIONNO: " + count1
					+ ", PSX_CVP_STAGING: " + count2 + ", PSX_CVP_TEMP: " + count3 + ", PSX_IDASSIGNER_INPUT: " + count4
					+ ", FileCount: " + Common.FileCount, Status.FAILED);
		}
	}

	@Step("Validating incremental table counts")
	private static void validateIncrementalCounts(int count1, int count2, int count3, int count4) {
		String nextInitialInsertTime = getNextInitialInsertTime();
		System.out.println("lastInitialTime : " + nextInitialInsertTime);

		int initialCount = getTableRowCountWithCondition("PSX_CUSTUNQID_VS_PARTITIONNO",
				"insert_time <= TO_TIMESTAMP('" + nextInitialInsertTime + "', 'DD-MM-YYYY HH24:MI:SS')");
		int incrementalCount = getTableRowCountWithCondition("PSX_CUSTUNQID_VS_PARTITIONNO",
				"insert_time > TO_TIMESTAMP('" + nextInitialInsertTime + "', 'DD-MM-YYYY HH24:MI:SS')");

		if (incrementalCount == count2 && count2 == count3 && count3 == count4 && count2 == Common.FileCount) {
			Allure.step("Incremental: All tables have the same number of rows: PSX_CUSTUNQID_VS_PARTITIONNO: "
					+ incrementalCount + ", PSX_CVP_STAGING: " + count2 + ", PSX_CVP_TEMP: " + count3
					+ ", PSX_IDASSIGNER_INPUT: " + count4 + ", FileCount: " + Common.FileCount, Status.PASSED);
		} else {
			Allure.step("Incremental: Row counts are different: PSX_CUSTUNQID_VS_PARTITIONNO: " + incrementalCount
					+ ", PSX_CVP_STAGING: " + count2 + ", PSX_CVP_TEMP: " + count3 + ", PSX_IDASSIGNER_INPUT: " + count4
					+ ", FileCount: " + Common.FileCount, Status.FAILED);
		}
	}

	private static int getTableRowCount(String tableName, boolean isInitial) {
		return getTableRowCountWithCondition(tableName, null);
	}

	private static int getTableRowCountWithCondition(String tableName, String condition) {
		String query = "SELECT COUNT(*) AS rowcount FROM " + tableName;
		if (condition != null && !condition.isEmpty()) {
			query += " WHERE " + condition;
		}
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			if (resultSet.next()) {
				return resultSet.getInt("rowcount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static String getNextInitialInsertTime() {
		String query = "SELECT TO_CHAR(MAX(insert_time), 'DD-MM-YYYY HH24:MI:SS') AS lastInitialTime "
				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO "
				+ "WHERE insert_time < (SELECT MAX(insert_time) FROM PSX_CUSTUNQID_VS_PARTITIONNO)";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			if (resultSet.next()) {
				String lastInitialTime = resultSet.getString("lastInitialTime");
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(lastInitialTime));

				// Increment seconds by 1
				cal.add(Calendar.SECOND, 1);

				// Adjust seconds if they exceed 60
				int seconds = cal.get(Calendar.SECOND);
				if (seconds >= 60) {
					cal.add(Calendar.MINUTE, 1);
					cal.set(Calendar.SECOND, 0);
				}

				return sdf.format(cal.getTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Step("Validating Max and Min Record ID")
	public static void validateMaxMinRecordID() {
		String query = "SELECT MIN(RECORD_ID) AS min_record_id, MAX(RECORD_ID) AS max_record_id, COUNT(*) AS count, PARTITION_NUMBER "
				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO GROUP BY PARTITION_NUMBER";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			boolean conditionFailed = false;
			boolean orderFailed = false;
			Set<Integer> allRecordIDs = new HashSet<>();

			while (resultSet.next()) {
				int minRecordID = resultSet.getInt("min_record_id");
				int maxRecordID = resultSet.getInt("max_record_id");
				int count = resultSet.getInt("count");
				String partitionNumber = resultSet.getString("PARTITION_NUMBER");

				Allure.step("Partition Number: " + partitionNumber + " Min Record ID: " + minRecordID
						+ " Max Record ID: " + maxRecordID + " Count: " + count);

				if (minRecordID != 0 || maxRecordID != count - 1) {
					conditionFailed = true;
				}

				String orderQuery = "SELECT RECORD_ID FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE PARTITION_NUMBER = ? ORDER BY RECORD_ID";
				try (PreparedStatement orderStatement = connection.prepareStatement(orderQuery)) {
					orderStatement.setString(1, partitionNumber);
					try (ResultSet orderResultSet = orderStatement.executeQuery()) {
						while (orderResultSet.next()) {
							int recordID = orderResultSet.getInt("RECORD_ID");
							if (!allRecordIDs.add(recordID)) {
								orderFailed = true;
								Allure.step("Duplicate RECORD_ID found across partitions: " + recordID, Status.FAILED);
							}
						}
					}
				}
			}

			// Check if all RECORD_IDs are continuous from 0 to n-1 globally
			List<Integer> sortedRecordIDs = new ArrayList<>(allRecordIDs);
			Collections.sort(sortedRecordIDs);
			for (int i = 0; i < sortedRecordIDs.size(); i++) {
				if (sortedRecordIDs.get(i) != i) {
					orderFailed = true;
					Allure.step("RECORD_IDs are not continuous across all partitions.", Status.FAILED);
					break;
				}
			}

			if (conditionFailed) {
				Allure.step(
						"Condition failed: One or more partitions have RECORD_IDs not starting from zero or not consecutively increasing by 1.",
						Status.FAILED);
			} else {
				Allure.step(
						"Condition passed: All partitions have RECORD_IDs starting from zero and consecutively increasing by 1.",
						Status.PASSED);
			}

			if (orderFailed) {
				Allure.step(
						"Order condition failed: RECORD_IDs are not unique or not continuous across all partitions.",
						Status.FAILED);
			} else {
				Allure.step("Order condition passed: RECORD_IDs are unique and continuous across all partitions.",
						Status.PASSED);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Allure.step("Exception occurred during validation", Status.FAILED);
		}
	}

	@Step("Validating Max and Min NSPKey")
	public static void validateMaxMinNSPKey() {
		String query = "SELECT MIN(NSPKEY) AS min_nspkey_id, MAX(NSPKEY) AS max_nspkey_id, COUNT(*) AS count, PARTITION_NUMBER "
				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO GROUP BY PARTITION_NUMBER";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			boolean conditionFailed = false;
			while (resultSet.next()) {
				int minNSPKeyID = resultSet.getInt("min_nspkey_id");
				int maxNSPKeyID = resultSet.getInt("max_nspkey_id");
				int count = resultSet.getInt("count");
				String partitionNumber = resultSet.getString("PARTITION_NUMBER");

				Allure.step("Partition Number: " + partitionNumber + " Min NSPKey: " + minNSPKeyID + " Max NSPKey: "
						+ maxNSPKeyID + " Total Count: " + count);

				if (minNSPKeyID != 0 || maxNSPKeyID != count - 1) {
					conditionFailed = true;
				}
			}

			if (conditionFailed) {
				Allure.step(
						"Condition failed: One or more partitions have NSPKEYs not starting from zero or not consecutively increasing by 1.",
						Status.FAILED);
			} else {
				Allure.step(
						"Condition passed: All partitions have NSPKEYs starting from zero and consecutively increasing by 1.",
						Status.PASSED);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Allure.step("Exception occurred during validation", Status.FAILED);
		}
	}

	@Step("Checking Duplicate RECORD_IDs")
	public static void checkDuplicateRecordIDs() {
		String query = "SELECT RECORD_ID, COUNT(*) FROM PSX_CUSTUNQID_VS_PARTITIONNO "
				+ "GROUP BY RECORD_ID HAVING COUNT(*) > 1";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
			if (!resultSet.next()) {
				Allure.step("No duplicate RECORD_IDs found.", Status.PASSED);
			} else {
				do {
					String recordID = resultSet.getString("RECORD_ID");
					int count = resultSet.getInt("COUNT(*)");
					Allure.step("Duplicate RECORD_ID found: " + recordID + " Count: " + count, Status.FAILED);
				} while (resultSet.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			Allure.step("Exception occurred during duplicate RECORD_ID check", Status.FAILED);
		}
	}

//	public static int readPropertyfile() throws IOException {
//		prop = Common.readPropertyFile();
//
//		String setup = prop.getProperty("InstallationType");
//
//		int partitionCount;
//		if (setup == "HS") {
//			partitionCount = 2;
//		} else {
//			partitionCount = 1;
//		}
//
////		int partitionCount = Integer.parseInt(partitions);
//		return partitionCount;
//
//	}
//
//	@Step("Checking Duplicate NSPKEYs")
//	public static void checkDuplicateNSPKeys() {
//
//		String query = "SELECT NSPKEY, COUNT(*) FROM PSX_CUSTUNQID_VS_PARTITIONNO "
//				+ "GROUP BY NSPKEY HAVING readPropertyfile()";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			if (!resultSet.next()) {
//				Allure.step("No duplicate NSPKEYs found.", Status.PASSED);
//			} else {
//				do {
//					String nspkey = resultSet.getString("NSPKEY");
//					int count = resultSet.getInt("COUNT(*)");
//					Allure.step("Duplicate NSPKEY found: " + nspkey + " Count: " + count, Status.FAILED);
//				} while (resultSet.next());
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			Allure.step("Exception occurred during duplicate NSPKEY check", Status.FAILED);
//		}
//	}
	public static int readPropertyfile() throws IOException {
        prop = Common.readPropertyFile();

        String setup = prop.getProperty("InstallationType");

        int partitionCount;
        if ("HS".equals(setup)) {
            partitionCount = 2;
        } else {
            partitionCount = 1;
        }

        return partitionCount;
    }

    @Step("Checking Duplicate NSPKEYs")
    public static void checkDuplicateNSPKeys() {
        int partitionCount;
        try {
            partitionCount = readPropertyfile();
        } catch (IOException e) {
            e.printStackTrace();
            Allure.step("Exception occurred while reading property file", Status.FAILED);
            return;
        }

        String query = "SELECT NSPKEY, COUNT(*) FROM PSX_CUSTUNQID_VS_PARTITIONNO "
                + "GROUP BY NSPKEY HAVING COUNT(*) > " + partitionCount;
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            if (!resultSet.next()) {
                Allure.step("No duplicate NSPKEYs found.", Status.PASSED);
            } else {
                do {
                    String nspkey = resultSet.getString("NSPKEY");
                    int count = resultSet.getInt("COUNT(*)");
                    Allure.step("Duplicate NSPKEY found: " + nspkey + " Count: " + count, Status.FAILED);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Allure.step("Exception occurred during duplicate NSPKEY check", Status.FAILED);
        }
    }
    
    @Step("Checking Duplicate Cust_Unq_Id's")
    public static void checkDuplicateCustUnqIDs() {
        int partitionCount;
        try {
            partitionCount = readPropertyfile();
        } catch (IOException e) {
            e.printStackTrace();
            Allure.step("Exception occurred while reading property file", Status.FAILED);
            return;
        }

        String query = "SELECT Cust_Unq_Id, COUNT(*) FROM PSX_CUSTUNQID_VS_PARTITIONNO "
                + "GROUP BY Cust_Unq_Id HAVING COUNT(*) >1";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            if (!resultSet.next()) {
                Allure.step("No duplicate Cust_Unq_Id's found.", Status.PASSED);
            } else {
                do {
                    String custUnqId = resultSet.getString("Cust_Unq_Id");
                    int count = resultSet.getInt("COUNT(*)");
                    Allure.step("Duplicate Cust_Unq_Id found: " + custUnqId + " Count: " + count, Status.FAILED);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Allure.step("Exception occurred during duplicate Cust_Unq_Id check", Status.FAILED);
        }
    }
    
	@Step("Checking updates count")
	public static void checkUpdatesCount() {
		String query = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME IS NOT NULL";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				int count = resultSet.getInt("update_count");
				if (count == Common.UCount) {
					Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO updates count: " + count, Status.PASSED);
					Allure.step("Same number of rows exist in file and table: " + count, Status.PASSED);
				} else {
					Allure.step("Expected count: " + Common.UCount + ", Actual count: " + count, Status.FAILED);
				}
			} else {
				Allure.step("No data found in the result set", Status.FAILED);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Allure.step("Exception occurred during query execution: " + e.getMessage(), Status.FAILED);
		}
	}

	@Step("Checking deletes count")
	public static void checkDeletesCount() {
		String query = "SELECT COUNT(*) AS delete_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE ISDELETED = 'Y'";
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				int count = resultSet.getInt("delete_count");
				if (count == Common.DCount) {
					Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO deletes count: " + count, Status.PASSED);
					Allure.step("Same number of rows exist in file and table: " + count, Status.PASSED);
				} else {
					Allure.step("Expected count: " + Common.DCount + ", Actual count: " + count, Status.FAILED);
				}
			} else {
				Allure.step("No data found in the result set", Status.FAILED);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			Allure.step("Exception occurred during query execution: " + e.getMessage(), Status.FAILED);
		}
	}

//	@Step("Checking updates and deletes count")
//	public static void checkUpdates_Deletes() {
//		String updateQuery = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME IS NOT NULL and ISDELETED is null";
//		String deleteQuery = "SELECT COUNT(*) AS delete_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE ISDELETED='Y'";
//
//		try (Statement statement = connection.createStatement()) {
//			// Execute update count query
//			try (ResultSet updateResultSet = statement.executeQuery(updateQuery)) {
//				if (updateResultSet.next()) {
//					int updateCount = updateResultSet.getInt("update_count");
//					if (updateCount == Common.UCount) {
//						Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO updates count: " + updateCount, Status.PASSED);
//						Allure.step("Same number of rows exist in file and table for updates: " + updateCount,
//								Status.PASSED);
//					} else {
//						Allure.step(
//								"Expected update count: " + Common.UCount + ", Actual update count: " + updateCount,
//								Status.FAILED);
//					}
//				} else {
//					Allure.step("No data found for updates in the result set", Status.FAILED);
//				}
//			}
//
//			// Execute delete count query
//			try (ResultSet deleteResultSet = statement.executeQuery(deleteQuery)) {
//				if (deleteResultSet.next()) {
//					int deleteCount = deleteResultSet.getInt("delete_count");
//					if (deleteCount == Common.DCount) {
//						Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO deletes count: " + deleteCount, Status.PASSED);
//						Allure.step("Same number of rows exist in file and table for deletes: " + deleteCount,
//								Status.PASSED);
//					} else {
//						Allure.step("Expected delete count: " + Common.DCount + ", Actual delete count: " + deleteCount,
//								Status.FAILED);
//					}
//				} else {
//					Allure.step("No data found for deletes in the result set", Status.FAILED);
//				}
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			Allure.step("Exception occurred during query execution: " + e.getMessage(), Status.FAILED);
//		}
//	}
//
//	@Step("Checking updates, deletes, and new inserts count")
//	public static void checkUpdates_Deletes_NewInserts() {
//
//	    String nextInitialInsertTime = getNextInitialInsertTime();
//
//	    String updateQuery = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME IS NOT NULL AND ISDELETED IS NULL";
//	    String deleteQuery = "SELECT COUNT(*) AS delete_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE ISDELETED='Y'";
//	    String newInsertsQuery = "SELECT COUNT(*) AS new_inserts_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE INSERT_TIME > TO_TIMESTAMP('" + nextInitialInsertTime + "', 'DD-MM-YYYY HH24:MI:SS')";
//
//	    try (Statement statement = connection.createStatement()) {
//	        // Execute update count query
//	        try (ResultSet updateResultSet = statement.executeQuery(updateQuery)) {
//	            if (updateResultSet.next()) {
//	                int updateCount = updateResultSet.getInt("update_count");
//	                if (updateCount == Common.UCount) {
//	                    Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO updates count: " + updateCount, Status.PASSED);
//	                    Allure.step("Same number of rows exist in file and table for updates: " + updateCount, Status.PASSED);
//	                } else {
//	                    Allure.step("Expected update count: " + Common.UCount + ", Actual update count: " + updateCount, Status.FAILED);
//	                }
//	            } else {
//	                Allure.step("No data found for updates in the result set", Status.FAILED);
//	            }
//	        }
//
//	        // Execute delete count query
//	        try (ResultSet deleteResultSet = statement.executeQuery(deleteQuery)) {
//	            if (deleteResultSet.next()) {
//	                int deleteCount = deleteResultSet.getInt("delete_count");
//	                if (deleteCount == Common.DCount) {
//	                    Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO deletes count: " + deleteCount, Status.PASSED);
//	                    Allure.step("Same number of rows exist in file and table for deletes: " + deleteCount, Status.PASSED);
//	                } else {
//	                    Allure.step("Expected delete count: " + Common.DCount + ", Actual delete count: " + deleteCount, Status.FAILED);
//	                }
//	            } else {
//	                Allure.step("No data found for deletes in the result set", Status.FAILED);
//	            }
//	        }
//
//	        // Execute new inserts count query
//	        try (ResultSet newInsertsResultSet = statement.executeQuery(newInsertsQuery)) {
//	            if (newInsertsResultSet.next()) {
//	                int newInsertsCount = newInsertsResultSet.getInt("new_inserts_count");
//	                if (newInsertsCount == Common.NewInsertCount) {
//	                    Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO new inserts count: " + newInsertsCount, Status.PASSED);
//	                    Allure.step("Same number of rows exist in file and table for new inserts: " + newInsertsCount, Status.PASSED);
//	                } else {
//	                    Allure.step("Expected new inserts count: " + Common.NewInsertCount + ", Actual new inserts count: " + newInsertsCount, Status.FAILED);
//	                }
//	            } else {
//	                Allure.step("No data found for new inserts in the result set", Status.FAILED);
//	            }
//	        }
//
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	        Allure.step("Exception occurred during query execution: " + e.getMessage(), Status.FAILED);
//	    }
//	}
//

	@Step("Checking updates, deletes, and optionally new inserts count")
	public static void checkUpdates_Deletes(boolean includeNewInserts) {

	    String nextInitialInsertTime = includeNewInserts ? getNextInitialInsertTime() : null;

	    String updateQuery = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME IS NOT NULL AND ISDELETED IS NULL";
	    String deleteQuery = "SELECT COUNT(*) AS delete_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE ISDELETED='Y'";
	    String newInsertsQuery = includeNewInserts ? "SELECT COUNT(*) AS new_inserts_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE INSERT_TIME > TO_TIMESTAMP('" + nextInitialInsertTime + "', 'DD-MM-YYYY HH24:MI:SS')" : null;

	    try (Statement statement = connection.createStatement()) {
	        // Execute update count query
	        try (ResultSet updateResultSet = statement.executeQuery(updateQuery)) {
	            if (updateResultSet.next()) {
	                int updateCount = updateResultSet.getInt("update_count");
	                if (updateCount == Common.UCount) {
	                    Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO updates count: " + updateCount, Status.PASSED);
	                    Allure.step("Same number of rows exist in file and table for updates: " + updateCount, Status.PASSED);
	                } else {
	                    Allure.step("Expected update count: " + Common.UCount + ", Actual update count: " + updateCount, Status.FAILED);
	                }
	            } else {
	                Allure.step("No data found for updates in the result set", Status.FAILED);
	            }
	        }

	        // Execute delete count query
	        try (ResultSet deleteResultSet = statement.executeQuery(deleteQuery)) {
	            if (deleteResultSet.next()) {
	                int deleteCount = deleteResultSet.getInt("delete_count");
	                if (deleteCount == Common.DCount) {
	                    Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO deletes count: " + deleteCount, Status.PASSED);
	                    Allure.step("Same number of rows exist in file and table for deletes: " + deleteCount, Status.PASSED);
	                } else {
	                    Allure.step("Expected delete count: " + Common.DCount + ", Actual delete count: " + deleteCount, Status.FAILED);
	                }
	            } else {
	                Allure.step("No data found for deletes in the result set", Status.FAILED);
	            }
	        }

	        // Conditionally execute new inserts count query
	        if (includeNewInserts && newInsertsQuery != null) {
	            try (ResultSet newInsertsResultSet = statement.executeQuery(newInsertsQuery)) {
	                if (newInsertsResultSet.next()) {
	                    int newInsertsCount = newInsertsResultSet.getInt("new_inserts_count");
	                    if (newInsertsCount == Common.NewInsertCount) {
	                        Allure.step("PSX_CUSTUNQID_VS_PARTITIONNO new inserts count: " + newInsertsCount, Status.PASSED);
	                        Allure.step("Same number of rows exist in file and table for new inserts: " + newInsertsCount, Status.PASSED);
	                    } else {
	                        Allure.step("Expected new inserts count: " + Common.NewInsertCount + ", Actual new inserts count: " + newInsertsCount, Status.FAILED);
	                    }
	                } else {
	                    Allure.step("No data found for new inserts in the result set", Status.FAILED);
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        Allure.step("Exception occurred during query execution: " + e.getMessage(), Status.FAILED);
	    }
	}

}
