//package utils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Properties;
//
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;
//
//import com.relevantcodes.extentreports.ExtentTest;
//import com.relevantcodes.extentreports.LogStatus;
//
//public class Common {
//	
//	public static int FileCount;
//	public static int DCount;
//	public static int IOrUCount;
//	public static Properties readPropertyFile() throws IOException {
//		File file = new File(System.getProperty("user.dir") + "\\data.properties");
//		FileInputStream fi = new FileInputStream(file);
//		Properties prop = new Properties();
//		prop.load(fi);
//		return prop;
//	}
//	
//
//	  
//
//	public static String dateNtime() {
//		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
//		String Neglect = ".";
//		String FinalTimeStamp = timeStamp.replace(Neglect, "");
//		// System.out.println(FinalTimeStamp);
//		return FinalTimeStamp;
//	}
//
//
//
//	    public static void getTotalCountFromFiles(ExtentTest test, String file, int columnIndex) throws IOException {
//	        try {
//	            FileCount = getCSVFileRowCount(file, columnIndex);
//	            System.out.println("Total count from file " + FileCount);
////	            test.log(LogStatus.PASS, "Total count from file " + FileCount);
////	            test.log(LogStatus.PASS, "Total D count from file " + DCount);
////	            test.log(LogStatus.PASS, "Total I_OR_U count from file " + IOrUCount);
//	        } catch (Exception e) {
//	            System.out.println("getTotalCountFromFiles Failed: " + e);
//	            test.log(LogStatus.FAIL, "getTotalCountFromFiles Failed: " + e);
//	        }
//	    }
//
//	    private static int getCSVFileRowCount(String filePath, int columnIndex) throws IOException {
//	        int rowCount = 0;
//	        DCount = 0;
//	        IOrUCount = 0;
//
//	        try (FileReader reader = new FileReader(filePath)) {
//	            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
//	            for (CSVRecord record : records) {
//	                rowCount++;
//	                String columnValue = record.get(columnIndex).trim();
//	                if ("D".equalsIgnoreCase(columnValue)) {
//	                    DCount++;
////	                } else if ("I_OR_U".equalsIgnoreCase(columnValue) || "U".equalsIgnoreCase(columnValue)) {
//
//	                } else if ("I_OR_U".equalsIgnoreCase(columnValue)) {
//	                    IOrUCount++;
//	                }
//	            }
//	        }
//	        return rowCount;
//	    }
//
//}

package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import io.qameta.allure.model.Status;


public class Common {

	public static int FileCount;
	public static int DCount;
	public static int UCount;
	public static int NewInsertCount;

	public static Properties readPropertyFile() throws IOException {
		File file = new File(System.getProperty("user.dir") + "\\data.properties");
		try (FileInputStream fi = new FileInputStream(file)) {
			Properties prop = new Properties();
			prop.load(fi);
			return prop;
		}
	}

	public static String dateNtime() {
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		return timeStamp.replace(".", "");
	}

	 @Step("Counting rows in Excel file: {filePath}")
	    public static int countRowsInExcel(String filePath) throws IOException {
	        try (FileInputStream fis = new FileInputStream(new File(filePath)); Workbook workbook = new XSSFWorkbook(fis)) {
	            Sheet sheet = workbook.getSheetAt(0);
	            // Subtract 1 to ignore the header row
	            int rowCount = sheet.getPhysicalNumberOfRows() - 1;
	            Allure.step("Total number of rows in the Excel file (excluding header): " + rowCount);
	            return rowCount;
	        }
	    }
	@Step("Getting total count from file: {file}")
	public static void getTotalCountFromFiles(String file, int columnIndex) throws IOException {
		try {
			FileCount = getCSVFileRowCount(file, columnIndex);
			Allure.step("Total count from file: " + FileCount);
//          Allure.step("Total D count from file: " + DCount);
//          Allure.step("Total I_OR_U count from file: " + IOrUCount);
		} catch (Exception e) {
			String errorMessage = "getTotalCountFromFiles Failed: " + e.getMessage();
			logError(errorMessage);
			Allure.step(errorMessage, Status.FAILED);
		}
	}

	private static int getCSVFileRowCount(String filePath, int columnIndex) throws IOException {
		int rowCount = 0;
		DCount = 0;
		UCount = 0;
		NewInsertCount = 0;

		try (FileReader reader = new FileReader(filePath)) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);
			for (CSVRecord record : records) {
				rowCount++;
				String columnValue = record.get(columnIndex).trim();
				if ("D".equalsIgnoreCase(columnValue)) {
					DCount++;
				} else if ("U".equalsIgnoreCase(columnValue)) {
					UCount++;
				} else if ("I_OR_U".equalsIgnoreCase(columnValue)) {
					NewInsertCount++;
				}
			}
		}
		return rowCount;
	}
	public static List<Object[]> getTestData(String filePath, String sheetName) {
	    List<Object[]> data = new ArrayList<>();
	    FileInputStream file = null;
	    Workbook workbook = null;

	    try {
//	        Allure.step("Opening file: " + filePath + " and reading sheet: " + sheetName);
	        file = new FileInputStream(new File(filePath));
	        workbook = new XSSFWorkbook(file);
	        Sheet sheet = workbook.getSheet(sheetName);

	        if (sheet == null) {
	            String errorMessage = "Sheet " + sheetName + " does not exist in " + filePath;
	            Allure.step(errorMessage, Status.FAILED);
	            throw new IllegalArgumentException(errorMessage);
	        }

	        int rows = sheet.getLastRowNum();
	        int cols = sheet.getRow(0).getLastCellNum();

//	        Allure.step("Reading data from rows: 1 to " + rows + " and columns: 0 to " + (cols - 1));

	        for (int i = 1; i <= rows; i++) {
	            Row row = sheet.getRow(i);
	            Object[] rowData = new Object[cols];
	            for (int j = 0; j < cols; j++) {
	                Cell cell = row.getCell(j);
	                if (cell == null) {
	                    rowData[j] = null;
	                    continue;
	                }
	                switch (cell.getCellType()) {
	                    case STRING:
	                        rowData[j] = cell.getStringCellValue();
	                        break;
	                    case NUMERIC:
	                        rowData[j] = cell.getNumericCellValue();
	                        break;
	                    default:
	                        rowData[j] = null;
	                }
	            }
	            data.add(rowData);
	        }
//	        Allure.step("Successfully read data from the Excel sheet.");
	    } catch (IOException e) {
	        Allure.step("IOException occurred: " + e.getMessage(), Status.FAILED);
	        e.printStackTrace();
	    } catch (IllegalArgumentException e) {
	        Allure.step("IllegalArgumentException occurred: " + e.getMessage(), Status.FAILED);
	        System.err.println(e.getMessage());
	    } finally {
	        try {
	            if (workbook != null) {
	                workbook.close();
//	                Allure.step("Closed workbook.");
	            }
	            if (file != null) {
	                file.close();
//	                Allure.step("Closed file stream.");
	            }
	        } catch (IOException e) {
	            Allure.step("IOException occurred while closing resources: " + e.getMessage(), Status.FAILED);
	            e.printStackTrace();
	        }
	    }
	    return data;
	}

	 
	 
//	 @Step("Extracting first column data from Excel file: {filePath}, Sheet: {sheetName}")
	 public static List<String> fileFirstColumnData(String filePath, String sheetName) throws EncryptedDocumentException, FileNotFoundException, IOException {
	     List<String> fileFirstColumnData = new ArrayList<>();
	     try (Workbook workbook = WorkbookFactory.create(new FileInputStream(filePath))) {
	         Sheet sheet = workbook.getSheet(sheetName); // Use the provided sheet name
	         if (sheet == null) {
	             throw new IllegalArgumentException("Sheet " + sheetName + " does not exist in " + filePath);
	         }
	         int rowCount = sheet.getPhysicalNumberOfRows() - 1; // Subtract 1 to ignore the header row
//	         Allure.step("Total number of rows in the Excel sheet (excluding header): " + rowCount);
	         
	         // Iterate from the second row to skip the header
	         for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
	             Row row = sheet.getRow(i);
	             if (row != null) {
	                 Cell cell = row.getCell(0); // Get the first column
	                 if (cell != null) {
	                     fileFirstColumnData.add(cell.getStringCellValue());
	                 }
	             }
	         }
//	         Allure.step("Extracted first column data: " + fileFirstColumnData);
	     } catch (Exception e) {
	         e.printStackTrace();
	         Allure.step("Error while reading first column data from the Excel file: " + e.getMessage(), Status.FAILED);
	         throw new RuntimeException("Error while reading first column data from the Excel file: " + e.getMessage(), e);
	     }
	     return fileFirstColumnData; // Return the list with the data
	 }



	@Step("{message}")
	private static void logMessage(String message) {
		Allure.step(message);
	}

	@Step("Error: {message}")
	@Attachment(value = "Error Log", type = "text/plain")
	private static String logError(String message) {
		return message;
	}
}
