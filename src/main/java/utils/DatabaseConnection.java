//package utils;
//
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Properties;
//import java.util.Set;
//
//import com.relevantcodes.extentreports.ExtentTest;
//import com.relevantcodes.extentreports.LogStatus;
//
//public class DatabaseConnection {
//	public static List<String> cvp_colums;
//	static Properties prop = new Properties();
//	public static Connection connection;
//
//	public static void establishDBConnection(String dbName, ExtentTest test) throws SQLException, IOException {
//		prop = Common.readPropertyFile();
//		switch (dbName.toLowerCase()) {
//		case "oracle":
//			try {
//				Class.forName("oracle.jdbc.driver.OracleDriver");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "Oracle Driver not found", e);
//				return;
//			}
//			try {
//				connection = DriverManager.getConnection(prop.getProperty("Oracle_HostName"),
//						prop.getProperty("Oracle_UserName"), prop.getProperty("Oracle_Password"));
//			} catch (SQLException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "Oracle Connection Failed! Check output console", e);
//				return;
//			}
//			if (connection != null) {
//				test.log(LogStatus.INFO,
//						"connected Oracle Database username is " + prop.getProperty("Oracle_UserName"));
//				test.log(LogStatus.PASS, "Oracle Database is connected");
//			} else {
//				test.log(LogStatus.FAIL, "Failed to make connection to Oracle Database!");
//			}
//			break;
//
//		case "mysql":
//			try {
//				Class.forName("com.mysql.cj.jdbc.Driver");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "MySQL Driver not found", e);
//				return;
//			}
//			try {
//				connection = DriverManager.getConnection(prop.getProperty("mysql_HostName"),
//						prop.getProperty("mysql_UserName"), prop.getProperty("mysql_Password"));
//			} catch (SQLException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "MySQL Connection Failed! Check output console", e);
//				return;
//			}
//			if (connection != null) {
//				test.log(LogStatus.PASS, "MySQL Database is connected");
//			} else {
//				test.log(LogStatus.FAIL, "Failed to make connection to MySQL Database!");
//			}
//			break;
//
//		case "sqlserver":
//			try {
//				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "SQLServer Driver not found", e);
//				return;
//			}
//			try {
//				connection = DriverManager.getConnection(prop.getProperty("sqlserver_hostname"),
//						prop.getProperty("sqlserver_username"), prop.getProperty("sqlserver_password"));
//			} catch (SQLException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "SQLServer Connection Failed! Check output console", e);
//				return;
//			}
//			if (connection != null) {
//				test.log(LogStatus.PASS, "SQLServer Database is connected");
//			} else {
//				test.log(LogStatus.FAIL, "Failed to make connection to SQLServer Database!");
//			}
//			break;
//
//		case "db2":
//			try {
//				Class.forName("com.ibm.db2.jcc.DB2Driver");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "DB2 Driver not found", e);
//				return;
//			}
//			try {
//				connection = DriverManager.getConnection(prop.getProperty("DB2_HostName"),
//						prop.getProperty("DB2_username"), prop.getProperty("DB2_password"));
//			} catch (SQLException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "DB2 Connection Failed! Check output console", e);
//				return;
//			}
//			if (connection != null) {
//				test.log(LogStatus.PASS, "DB2 Database is connected");
//			} else {
//				test.log(LogStatus.FAIL, "Failed to make connection to DB2 Database!");
//			}
//			break;
//
//		case "postgresql":
//			try {
//				Class.forName("org.postgresql.Driver");
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "PostgreSQL Driver not found", e);
//				return;
//			}
//			try {
//				connection = DriverManager.getConnection(prop.getProperty("Postgres_HostName"),
//						prop.getProperty("Postgres_username"), prop.getProperty("Postgres_Password"));
//			} catch (SQLException e) {
//				e.printStackTrace();
//				test.log(LogStatus.FAIL, "PostgreSQL Connection Failed! Check output console", e);
//				return;
//			}
//			if (connection != null) {
//				test.log(LogStatus.PASS, "PostgreSQL Database is connected");
//			} else {
//				test.log(LogStatus.FAIL, "Failed to make connection to PostgreSQL Database!");
//			}
//			break;
//
//		default:
//			test.log(LogStatus.FAIL, "Database type not supported!");
//		}
//	}
//
//	public static void closeDBConnection(ExtentTest test) throws SQLException {
//		if (connection != null) {
//			connection.close();
//			test.log(LogStatus.PASS, "Database connection closed successfully");
//		} else {
//			test.log(LogStatus.FAIL, "No active database connection to close");
//		}
//	}
//
//	private static int storedMaxRecordID = 0;
//
//	public static void printCVP(ExtentTest test, boolean isInitial) {
//		if (isInitial) {
//			// For initial runs, set the stored max record ID to 0
//			storedMaxRecordID = getMaxRecordID();
//		}
//
//		try {
//			List<List<String>> cvpData = returnCVP(); // Fetch all records from the table
//
//			for (List<String> cvpDetails : cvpData) {
//				String customerUniqueID = cvpDetails.get(0);
//				String recordID = cvpDetails.get(1);
//				String nspkey = cvpDetails.get(2);
//				String insertTime = cvpDetails.get(3);
//				String partitionNumber = cvpDetails.get(4);
//
//				if (isInitial) {
//					test.log(LogStatus.INFO,
//							"CUST_UNQ_ID: " + customerUniqueID + "  NSPKEY: " + nspkey + "  RECORD_ID: " + recordID
//									+ "  INSERT_TIME: " + insertTime + "  PARTITION_NUMBER: " + partitionNumber);
//				}
//			}
//
//			if (!isInitial) {
//				logIncrementalRecords(test);
//			}
//		} catch (Exception e) {
//			test.log(LogStatus.FAIL, "Failed to retrieve data from PSX_CUSTUNQID_VS_PARTITIONNO table", e);
//		}
//	}
//
//	public static List<List<String>> returnCVP() throws SQLException {
//		List<List<String>> cvpData = new ArrayList<>();
//		String query = "SELECT * FROM PSX_CUSTUNQID_VS_PARTITIONNO ORDER BY RECORD_ID ASC";
//
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//
//			while (resultSet.next()) {
//				List<String> cvpDetails = new ArrayList<>();
//				cvpDetails.add(resultSet.getString("CUST_UNQ_ID"));
//				cvpDetails.add(resultSet.getString("RECORD_ID"));
//				cvpDetails.add(resultSet.getString("NSPKEY"));
//				cvpDetails.add(resultSet.getString("INSERT_TIME"));
//				cvpDetails.add(resultSet.getString("PARTITION_NUMBER"));
//
//				cvpData.add(cvpDetails);
//			}
//		}
//
//		return cvpData;
//	}
//
//	private static int getMaxRecordID() {
//		int maxRecordID = 0;
//		String query = "SELECT MAX(RECORD_ID) AS max_id FROM PSX_CUSTUNQID_VS_PARTITIONNO";
//
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//
//			if (resultSet.next()) {
//				maxRecordID = resultSet.getInt("max_id");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace(); // Handle SQLException appropriately
//		}
//
//		return maxRecordID;
//	}
//
//	private static void logIncrementalRecords(ExtentTest test) {
//		String query = "SELECT * FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE RECORD_ID > " + storedMaxRecordID;
//
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//
//			while (resultSet.next()) {
//				String customerUniqueID = resultSet.getString("CUST_UNQ_ID");
//				String recordID = resultSet.getString("RECORD_ID");
//				String nspkey = resultSet.getString("NSPKEY");
//				String insertTime = resultSet.getString("INSERT_TIME");
//				String partitionNumber = resultSet.getString("PARTITION_NUMBER");
//
//				test.log(LogStatus.INFO,
//						"Incremental record: CUST_UNQ_ID: " + customerUniqueID + "  NSPKEY: " + nspkey + "  RECORD_ID: "
//								+ recordID + "  INSERT_TIME: " + insertTime + "  PARTITION_NUMBER: " + partitionNumber);
//
//				// Update the storedMaxRecordID to the latest record ID
//				storedMaxRecordID = Math.max(storedMaxRecordID, Integer.parseInt(recordID));
//			}
//		} catch (SQLException e) {
//			e.printStackTrace(); // Handle SQLException appropriately
//		}
//	}
//	private static int storedMaxRecordID_get = 0;
//
//	public static List<String> getCustomerUniqueIDs(boolean isInitial) throws Exception {
//	    List<String> customerUniqueIDs = new ArrayList<>();
//
//	    if (isInitial) {
//	        // Fetch all customer unique IDs for initial run
//	        List<List<String>> cvpData = returnCVP();
//
//	        for (List<String> cvpDetails : cvpData) {
//	            customerUniqueIDs.add(cvpDetails.get(0));
//	        }
//
//	        // Update storedMaxRecordID after initial run
//	        storedMaxRecordID_get = getMaxRecordID();
//	    } else {
//	        // Fetch customer unique IDs starting from storedMaxRecordID for incremental run
//	        String query = "SELECT CUST_UNQ_ID FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE RECORD_ID > " + storedMaxRecordID_get;
//
//	        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//
//	            while (resultSet.next()) {
//	                String customerUniqueID = resultSet.getString("CUST_UNQ_ID");
//	                customerUniqueIDs.add(customerUniqueID);
//	            }
//	        } catch (SQLException e) {
//	            e.printStackTrace(); // Handle SQLException appropriately
//	        }
//	    }
//
//	    return customerUniqueIDs;
//	}
//
//
//	public static void validateTableCounts(ExtentTest test, boolean isInitial) throws Exception {
//		int count1 = getTableRowCount("PSX_CUSTUNQID_VS_PARTITIONNO", isInitial);
//		int count2 = getTableRowCount("PSX_CVP_STAGING", isInitial);
//		int count3 = getTableRowCount("PSX_CVP_TEMP", isInitial);
//		int count4 = getTableRowCount("PSX_IDASSIGNER_INPUT", isInitial);
//
//		if (isInitial) {
//			// Condition for parentTableAsABatchInitial
//			if (count1 == count2 && count2 == count4 && count1 == Common.FileCount) {
//				test.log(LogStatus.INFO,
//						"Initial: All tables have the same number of rows: PSX_CUSTUNQID_VS_PARTITIONNO: " + count1
//								+ ", PSX_CVP_STAGING: " + count2 + ", PSX_IDASSIGNER_INPUT: " + count4 + ", FileCount: "
//								+ Common.FileCount);
//				test.log(LogStatus.PASS, "Initial: All tables have the same number of rows: " + count1
//						+ " and Total count from file: " + Common.FileCount);
//			} else {
//				test.log(LogStatus.FAIL,
//						"Initial: Row counts are different: PSX_CUSTUNQID_VS_PARTITIONNO: " + count1
//								+ ", PSX_CVP_STAGING: " + count2 + ", PSX_CVP_TEMP: " + count3
//								+ ", PSX_IDASSIGNER_INPUT: " + count4 + ", FileCount: " + Common.FileCount);
//			}
//		} else {
//			// Condition for parentTableAsABatchIncremental
//			String NextInitialInsertTime = getNextInitialInsertTime();
//
//			System.out.println("lastInitialTime : " + NextInitialInsertTime);
//			int initialCount = getTableRowCountWithCondition("PSX_CUSTUNQID_VS_PARTITIONNO",
//					"insert_time <= TO_TIMESTAMP('" + NextInitialInsertTime + "', 'DD-MM-YYYY HH24:MI:SS')");
//			int incrementalCount = getTableRowCountWithCondition("PSX_CUSTUNQID_VS_PARTITIONNO",
//					"insert_time > TO_TIMESTAMP('" + NextInitialInsertTime + "', 'DD-MM-YYYY HH24:MI:SS')");
//
//			if (incrementalCount == count2 && count2 == count3 && count3 == count4 && count2 == Common.FileCount) {
//				test.log(LogStatus.INFO,
//						"Incremental: All tables have the same number of rows: PSX_CUSTUNQID_VS_PARTITIONNO: "
//								+ incrementalCount + ", PSX_CVP_STAGING: " + count2 + ", PSX_CVP_TEMP: " + count3
//								+ ", PSX_IDASSIGNER_INPUT: " + count4 + ", FileCount: " + Common.FileCount);
//				test.log(LogStatus.PASS, "Incremental: All tables have the same number of rows: " + incrementalCount
//						+ " and Total count from file: " + Common.FileCount);
//			} else {
//				test.log(LogStatus.FAIL,
//						"Incremental: Row counts are different: PSX_CUSTUNQID_VS_PARTITIONNO: " + incrementalCount
//								+ ", PSX_CVP_STAGING: " + count2 + ", PSX_CVP_TEMP: " + count3
//								+ ", PSX_IDASSIGNER_INPUT: " + count4 + ", FileCount: " + Common.FileCount);
//			}
//		}
//	}
//
//	private static int getTableRowCount(String tableName, boolean isInitial) {
//		return getTableRowCountWithCondition(tableName, null);
//	}
//
//	private static int getTableRowCountWithCondition(String tableName, String condition) {
//		String query = "SELECT COUNT(*) AS rowcount FROM " + tableName;
//		if (condition != null && !condition.isEmpty()) {
//			query += " WHERE " + condition;
//		}
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			if (resultSet.next()) {
//				return resultSet.getInt("rowcount");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return -1;
//	}
//
//	private static String getLastInitialInsertTime() {
//		String query = "SELECT TO_CHAR(MAX(insert_time), 'DD-MM-YYYY HH24:MI:SS') AS lastInitialTime "
//				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO "
//				+ "WHERE insert_time < (SELECT MAX(insert_time) FROM PSX_CUSTUNQID_VS_PARTITIONNO)";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			if (resultSet.next()) {
//				return resultSet.getString("lastInitialTime");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static String getNextInitialInsertTime() {
//		String query = "SELECT TO_CHAR(MAX(insert_time), 'DD-MM-YYYY HH24:MI:SS') AS lastInitialTime "
//				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO "
//				+ "WHERE insert_time < (SELECT MAX(insert_time) FROM PSX_CUSTUNQID_VS_PARTITIONNO)";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			if (resultSet.next()) {
//				String lastInitialTime = resultSet.getString("lastInitialTime");
//				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//				Calendar cal = Calendar.getInstance();
//				cal.setTime(sdf.parse(lastInitialTime));
//
//				// Increment seconds by 1
//				cal.add(Calendar.SECOND, 1);
//
//				// Adjust seconds if they exceed 60
//				int seconds = cal.get(Calendar.SECOND);
//				if (seconds >= 60) {
//					cal.add(Calendar.MINUTE, 1);
//					cal.set(Calendar.SECOND, 0);
//				}
//
//				return sdf.format(cal.getTime());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static void validateMaxMinRecordID(ExtentTest test) {
//		String query = "SELECT MIN(RECORD_ID) AS min_record_id, MAX(RECORD_ID) AS max_record_id, COUNT(*) AS count, PARTITION_NUMBER "
//				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO GROUP BY PARTITION_NUMBER";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			boolean conditionFailed = false;
//			boolean orderFailed = false;
//			Set<Integer> allRecordIDs = new HashSet<>();
//
//			while (resultSet.next()) {
//				int minRecordID = resultSet.getInt("min_record_id");
//				int maxRecordID = resultSet.getInt("max_record_id");
//				int count = resultSet.getInt("count");
//				String partitionNumber = resultSet.getString("PARTITION_NUMBER");
//
//				test.log(LogStatus.INFO, "Partition Number: " + partitionNumber + " Min Record ID: " + minRecordID
//						+ " Max Record ID: " + maxRecordID + " Count: " + count);
//
//				if (minRecordID != 0 || maxRecordID != count - 1) {
//					conditionFailed = true;
//				}
//
//				String orderQuery = "SELECT RECORD_ID FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE PARTITION_NUMBER = ? ORDER BY RECORD_ID";
//				try (PreparedStatement orderStatement = connection.prepareStatement(orderQuery)) {
//					orderStatement.setString(1, partitionNumber);
//					try (ResultSet orderResultSet = orderStatement.executeQuery()) {
//						while (orderResultSet.next()) {
//							int recordID = orderResultSet.getInt("RECORD_ID");
//							if (!allRecordIDs.add(recordID)) {
//								orderFailed = true;
//								test.log(LogStatus.FAIL, "Duplicate RECORD_ID found across partitions: " + recordID);
//							}
//						}
//					}
//				}
//			}
//			// Check if all RECORD_IDs are continuous from 0 to n-1 globally
//			List<Integer> sortedRecordIDs = new ArrayList<>(allRecordIDs);
//			Collections.sort(sortedRecordIDs);
//			for (int i = 0; i < sortedRecordIDs.size(); i++) {
//				if (sortedRecordIDs.get(i) != i) {
//					orderFailed = true;
//					test.log(LogStatus.FAIL, "RECORD_IDs are not continuous across all partitions.");
//					break;
//				}
//			}
//
//			if (conditionFailed) {
//				test.log(LogStatus.FAIL,
//						"Condition failed: One or more partitions have RECORD_IDs not starting from zero or not consecutively increasing by 1.");
//			} else {
//				test.log(LogStatus.PASS,
//						"Condition passed: All partitions have RECORD_IDs starting from zero and consecutively increasing by 1.");
//			}
//
//			if (orderFailed) {
//				test.log(LogStatus.FAIL,
//						"Order condition failed: RECORD_IDs are not unique or not continuous across all partitions.");
//			} else {
//				test.log(LogStatus.PASS,
//						"Order condition passed: RECORD_IDs are unique and continuous across all partitions.");
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during validation", e);
//		}
//	}
//
//	public static void validateMaxMinNSPKey(ExtentTest test) {
//		String query = "SELECT MIN(NSPKEY) AS min_nspkey_id, MAX(NSPKEY) AS max_nspkey_id, COUNT(*) AS count, PARTITION_NUMBER "
//				+ "FROM PSX_CUSTUNQID_VS_PARTITIONNO GROUP BY PARTITION_NUMBER";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			boolean conditionFailed = false;
//			while (resultSet.next()) {
//				int minNSPKeyID = resultSet.getInt("min_nspkey_id");
//				int maxNSPKeyID = resultSet.getInt("max_nspkey_id");
//				int count = resultSet.getInt("count");
//				String partitionNumber = resultSet.getString("PARTITION_NUMBER");
//
//				test.log(LogStatus.INFO, "Partition Number: " + partitionNumber + " Min NSPKey: " + minNSPKeyID
//						+ " Max NSPKey: " + maxNSPKeyID + "Total Count: " + count);
//
//				if (minNSPKeyID != 0) {
//					conditionFailed = true;
//				}
//				if (maxNSPKeyID != count - 1) {
//					conditionFailed = true;
//				}
//			}
//
//			if (conditionFailed) {
//				test.log(LogStatus.FAIL,
//						"Condition failed: One or more partitions have NSPKEYs not starting from zero or not consecutively increasing by 1.");
//			} else {
//				test.log(LogStatus.PASS,
//						"Condition passed: All partitions have NSPKEYs starting from zero and consecutively increasing by 1.");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during validation", e);
//		}
//	}
//
//	public static void checkDuplicateRecordIDs(ExtentTest test) {
//		String query = "SELECT RECORD_ID, COUNT(*) FROM PSX_CUSTUNQID_VS_PARTITIONNO "
//				+ "GROUP BY RECORD_ID HAVING COUNT(*) > 1";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			if (!resultSet.next()) {
//				test.log(LogStatus.PASS, "No duplicate RECORD_IDs found.");
//			} else {
//				do {
//					String recordID = resultSet.getString("RECORD_ID");
//					int count = resultSet.getInt("COUNT(*)");
//					test.log(LogStatus.FAIL, "Duplicate RECORD_ID found: " + recordID + " Count: " + count);
//				} while (resultSet.next());
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during duplicate RECORD_ID check", e);
//		}
//	}
//
//	public static void checkDuplicateNSPKeys(ExtentTest test) {
//		String query = "SELECT NSPKEY, COUNT(*) FROM PSX_CUSTUNQID_VS_PARTITIONNO "
//				+ "GROUP BY NSPKEY HAVING COUNT(*) > 1";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//			if (!resultSet.next()) {
//				test.log(LogStatus.PASS, "No duplicate NSPKEYs found.");
//			} else {
//				do {
//					String nspkey = resultSet.getString("NSPKEY");
//					int count = resultSet.getInt("COUNT(*)");
//					test.log(LogStatus.FAIL, "Duplicate NSPKEY found: " + nspkey + " Count: " + count);
//				} while (resultSet.next());
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during duplicate NSPKEY check", e);
//		}
//	}
//
//	public static void checkUpdatesCount(ExtentTest test) {
//		String query = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME IS NOT NULL";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//
//			if (resultSet.next()) {
//				int count = resultSet.getInt("update_count");
//				if (count == Common.IOrUCount) {
//					test.log(LogStatus.INFO, "PSX_CUSTUNQID_VS_PARTITIONNO updates count: " + count);
//					test.log(LogStatus.PASS, "same number of rows exist in file and table: " + count);
//				} else {
//					test.log(LogStatus.FAIL, "Expected count: " + Common.FileCount + ", Actual count: " + count);
//				}
//			} else {
//				test.log(LogStatus.FAIL, "No data found in the result set");
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during query execution", e);
//		}
//	}
//
//	public static void checkDeletesCount(ExtentTest test) {
//		String query = "SELECT COUNT(*) AS delete_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE ISDELETED = 'Y'";
//		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
//
//			if (resultSet.next()) {
//				int count = resultSet.getInt("delete_count");
//				if (count == Common.DCount) {
//					test.log(LogStatus.INFO, "PSX_CUSTUNQID_VS_PARTITIONNO deletes count: " + count);
//					test.log(LogStatus.PASS, "Same number of rows exist in file and table: " + count);
//				} else {
//					test.log(LogStatus.FAIL, "Expected count: " + Common.DCount + ", Actual count: " + count);
//				}
//			} else {
//				test.log(LogStatus.FAIL, "No data found in the result set");
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during query execution: " + e.getMessage());
//		}
//	}
//
//	// private static Connection connection;
//
//	public static void checkUpdates_Deletes(ExtentTest test) {
//		String updateQuery = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME IS NOT NULL and ISDELETED is null";
//
////		String updateQuery = "SELECT COUNT(*) AS update_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE UPDATED_TIME = (SELECT MAX(UPDATED_TIME) FROM PSX_CUSTUNQID_VS_PARTITIONNO)";
//		String deleteQuery = "SELECT COUNT(*) AS delete_count FROM PSX_CUSTUNQID_VS_PARTITIONNO WHERE ISDELETED='Y'";
//
//		try (Statement statement = connection.createStatement()) {
//			// Execute update count query
//			try (ResultSet updateResultSet = statement.executeQuery(updateQuery)) {
//				if (updateResultSet.next()) {
//					int updateCount = updateResultSet.getInt("update_count");
//					if (updateCount == Common.IOrUCount) {
//						test.log(LogStatus.INFO, "PSX_CUSTUNQID_VS_PARTITIONNO updates count: " + updateCount);
//						test.log(LogStatus.PASS,
//								"Same number of rows exist in file and table for updates: " + updateCount);
//					} else {
//						test.log(LogStatus.FAIL,
//								"Expected update count: " + Common.IOrUCount + ", Actual update count: " + updateCount);
//					}
//				} else {
//					test.log(LogStatus.FAIL, "No data found for updates in the result set");
//				}
//			}
//
//			// Execute delete count query
//			try (ResultSet deleteResultSet = statement.executeQuery(deleteQuery)) {
//				if (deleteResultSet.next()) {
//					int deleteCount = deleteResultSet.getInt("delete_count");
//					// Assuming Common.DeleteFileCount is the expected delete count from the file
//					if (deleteCount == Common.DCount) {
//						test.log(LogStatus.INFO, "PSX_CUSTUNQID_VS_PARTITIONNO deletes count: " + deleteCount);
//						test.log(LogStatus.PASS,
//								"Same number of rows exist in file and table for deletes: " + deleteCount);
//					} else {
//						test.log(LogStatus.FAIL,
//								"Expected delete count: " + Common.DCount + ", Actual delete count: " + deleteCount);
//					}
//				} else {
//					test.log(LogStatus.FAIL, "No data found for deletes in the result set");
//				}
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//			test.log(LogStatus.FAIL, "Exception occurred during query execution", e);
//		}
//	}
//}
package utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import io.qameta.allure.Step;

public class DatabaseConnection {
	public static List<String> cvp_colums;
	static Properties prop = new Properties();
	public static Connection connection;

	@Step("Establishing DB Connection for {dbName}")
	public static void establishDBConnection(String dbName) throws SQLException, IOException {
		prop = Common.readPropertyFile();
		switch (dbName.toLowerCase()) {
		case "oracle":
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Oracle Driver not found", e);
			}
			try {
				connection = DriverManager.getConnection(prop.getProperty("Oracle_HostName"),
						prop.getProperty("Oracle_UserName"), prop.getProperty("Oracle_Password"));
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Oracle Connection Failed! Check output console", e);
			}
			if (connection != null) {
				System.out.println("connected Oracle Database username is " + prop.getProperty("Oracle_UserName"));
			} else {
				throw new RuntimeException("Failed to make connection to Oracle Database!");
			}
			break;

		case "mysql":
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("MySQL Driver not found", e);
			}
			try {
				connection = DriverManager.getConnection(prop.getProperty("mysql_HostName"),
						prop.getProperty("mysql_UserName"), prop.getProperty("mysql_Password"));
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("MySQL Connection Failed! Check output console", e);
			}
			if (connection != null) {
				System.out.println("MySQL Database is connected");
			} else {
				throw new RuntimeException("Failed to make connection to MySQL Database!");
			}
			break;

		case "sqlserver":
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("SQLServer Driver not found", e);
			}
			try {
				connection = DriverManager.getConnection(prop.getProperty("sqlserver_hostname"),
						prop.getProperty("sqlserver_username"), prop.getProperty("sqlserver_password"));
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("SQLServer Connection Failed! Check output console", e);
			}
			if (connection != null) {
				System.out.println("SQLServer Database is connected");
			} else {
				throw new RuntimeException("Failed to make connection to SQLServer Database!");
			}
			break;

		case "db2":
			try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("DB2 Driver not found", e);
			}
			try {
				connection = DriverManager.getConnection(prop.getProperty("DB2_HostName"),
						prop.getProperty("DB2_username"), prop.getProperty("DB2_password"));
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("DB2 Connection Failed! Check output console", e);
			}
			if (connection != null) {
				System.out.println("DB2 Database is connected");
			} else {
				throw new RuntimeException("Failed to make connection to DB2 Database!");
			}
			break;

		case "postgresql":
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("PostgreSQL Driver not found", e);
			}
			try {
				connection = DriverManager.getConnection(prop.getProperty("Postgres_HostName"),
						prop.getProperty("Postgres_username"), prop.getProperty("Postgres_Password"));
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("PostgreSQL Connection Failed! Check output console", e);
			}
			if (connection != null) {
				System.out.println("PostgreSQL Database is connected");
			} else {
				throw new RuntimeException("Failed to make connection to PostgreSQL Database!");
			}
			break;

		default:
			throw new RuntimeException("Database type not supported!");
		}
	}
	
	 public static Connection getConnection() {
	        return connection;
	    }

	@Step("Closing DB Connection")
	public static void closeDBConnection() throws SQLException {
		if (connection != null) {
			connection.close();
			System.out.println("Database connection closed successfully");
		} else {
			throw new RuntimeException("No active database connection to close");
		}
	}

}