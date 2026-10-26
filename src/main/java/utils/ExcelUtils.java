package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * ExcelUtils for reading Excel test data files (.xlsx) dynamically using Apache POI.
 * Enhanced with resilient date-column parsing, serial conversion, and validation.
 */
public class ExcelUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,                    // yyyy-MM-dd
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
    );

    /**
     * Identifies if a column header represents a date field.
     */
    public static boolean isDateColumn(String headerName) {
        if (headerName == null) return false;
        String lower = headerName.trim().toLowerCase();
        return lower.contains("date") || lower.contains("available") || lower.contains("from") || lower.contains("till");
    }

    /**
     * Resiliently parses a date string or Excel serial number into a LocalDate.
     */
    public static LocalDate parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        String trimmed = dateStr.trim();

        // Check if numeric serial string (e.g. "46296")
        if (trimmed.matches("^\\d{5}$")) {
            try {
                double serial = Double.parseDouble(trimmed);
                if (DateUtil.isValidExcelDate(serial)) {
                    return DateUtil.getLocalDateTime(serial).toLocalDate();
                }
            } catch (Exception ignored) {}
        }

        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    /**
     * Validates that a date value from Excel is non-empty, parseable, and has a 4-digit year.
     * Fails with descriptive exception including sheet, row, and column if invalid.
     */
    public static LocalDate parseAndValidateDate(String dateValue, String sheetName, int rowNum, String colName) {
        if (dateValue == null || dateValue.trim().isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Date cell is blank in Sheet: '%s', Row: %d, Column: '%s'",
                    sheetName, rowNum, colName));
        }
        String trimmed = dateValue.trim();
        LocalDate date = parseDateSafely(trimmed);
        if (date == null) {
            throw new IllegalArgumentException(String.format(
                    "Invalid date format '%s' in Sheet: '%s', Row: %d, Column: '%s'. Expected format yyyy-MM-dd.",
                    trimmed, sheetName, rowNum, colName));
        }
        int year = date.getYear();
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException(String.format(
                    "Invalid year '%d' (must be 4 digits between 1900 and 2100) in Sheet: '%s', Row: %d, Column: '%s'. Raw value: '%s'",
                    year, sheetName, rowNum, colName, trimmed));
        }
        return date;
    }

    /**
     * Reads all data rows from a sheet as a 2D Object array suitable for TestNG DataProvider.
     * Excludes header row.
     */
    public static Object[][] getSheetData(String filePath, String sheetName) {
        List<Map<String, String>> rowsList = getSheetDataAsMap(filePath, sheetName);
        Object[][] data = new Object[rowsList.size()][1];
        for (int i = 0; i < rowsList.size(); i++) {
            data[i][0] = rowsList.get(i);
        }
        return data;
    }

    /**
     * Reads sheet data into a List of Maps (Header Name -> Cell Value).
     */
    public static List<Map<String, String>> getSheetDataAsMap(String filePath, String sheetName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            LoggerUtils.info(ExcelUtils.class, "Excel file does not exist at " + file.getAbsolutePath() + ". Generating from specification template...");
            try {
                ExcelDataInitializer.main(new String[0]);
            } catch (Exception e) {
                LoggerUtils.warn(ExcelUtils.class, "Could not auto-generate testdata.xlsx: " + e.getMessage());
            }
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                LoggerUtils.warn(ExcelUtils.class, "Sheet '" + sheetName + "' not found in workbook: " + filePath);
                return dataList;
            }

            int totalRows = sheet.getLastRowNum();
            if (totalRows < 1) {
                return dataList; // No data rows
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return dataList;
            }

            int totalCols = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int col = 0; col < totalCols; col++) {
                Cell cell = headerRow.getCell(col);
                headers.add(getCellValueAsString(cell, null).trim());
            }

            for (int r = 1; r <= totalRows; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int col = 0; col < totalCols; col++) {
                    String header = headers.get(col);
                    if (header.isEmpty()) {
                        continue;
                    }
                    Cell cell = row.getCell(col);
                    String val = getCellValueAsString(cell, header);
                    rowMap.put(header, val);
                }
                dataList.add(rowMap);
            }

        } catch (IOException e) {
            LoggerUtils.error(ExcelUtils.class, "Error reading Excel file: " + e.getMessage(), e);
        }

        return dataList;
    }

    /**
     * Reads sheet rows as a 2D Object array of String columns (row x column).
     */
    public static Object[][] getSheetDataAsArray(String filePath, String sheetName) {
        List<Map<String, String>> maps = getSheetDataAsMap(filePath, sheetName);
        if (maps.isEmpty()) {
            return new Object[0][0];
        }

        int colCount = maps.get(0).size();
        Object[][] result = new Object[maps.size()][colCount];

        for (int i = 0; i < maps.size(); i++) {
            Map<String, String> row = maps.get(i);
            int j = 0;
            for (String val : row.values()) {
                result[i][j++] = val;
            }
        }
        return result;
    }

    private static String getCellValueAsString(Cell cell, String headerName) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        switch (cellType) {
            case STRING:
                String text = cell.getStringCellValue().trim();
                if (isDateColumn(headerName) && !text.isEmpty()) {
                    LocalDate parsedDate = parseDateSafely(text);
                    if (parsedDate != null) {
                        return parsedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                }
                return text;

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    try {
                        return DateUtil.getLocalDateTime(cell.getNumericCellValue()).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                    } catch (Exception e) {
                        Date date = cell.getDateCellValue();
                        return date != null ? DATE_FORMAT.format(date) : "";
                    }
                } else {
                    double num = cell.getNumericCellValue();
                    if (isDateColumn(headerName) && DateUtil.isValidExcelDate(num) && num >= 1) {
                        try {
                            return DateUtil.getLocalDateTime(num).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
                        } catch (Exception ignored) {}
                    }
                    if (num == Math.floor(num) && !Double.isInfinite(num)) {
                        return String.valueOf((long) num);
                    } else {
                        return String.valueOf(num);
                    }
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
                return "";

            default:
                return "";
        }
    }

    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell, null).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
