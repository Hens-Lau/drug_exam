package com.here.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.math.BigDecimal;

public class PoiUtils {

    public static boolean isExcel2003(String fileName){
        return fileName.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String fileName){
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }
    /**
     * 获取单元格的字符信息
     * @param cell
     * @return
     */
    public static String getCellStringValue(Cell cell){
        if(cell==null){
            return "";
        }
        String val = "";
        switch (cell.getCellType()){
            case Cell.CELL_TYPE_STRING:
               val = cell.getStringCellValue();
               if(StringUtils.isBlank(val)){
                   val = "";
               }
               break;
            case Cell.CELL_TYPE_NUMERIC:
                val = BigDecimal.valueOf(cell.getNumericCellValue()).toString();
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                val = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                val = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                val = "";
                break;
            case Cell.CELL_TYPE_ERROR:
                val = "";
                break;
            default:
                break;
        }
        return val;
    }
}
