package com.http.simpleTest;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    private String filePath;
    private String sheetName;
    private Workbook workbook;
    private Sheet sheet;
    private List<String> columnHeaderList;
    private List<List<String>> listData;
    private List<Map<String,String>> mapData;
    private boolean flag;
    public Object[][] results;

    //含参的构造方法（与类同名）
    public ExcelReader(String filePath, String sheetName){
        //this关键字作为方法名来初始化对象
        this.filePath = filePath;
        this.sheetName = sheetName;
        this.flag = false;
        this.load();//通过this关键字调用类ExcelReader的方法load
    }

    //定义load方法，加载Excel文件
    private void load(){
        FileInputStream inStream = null;//声明对象（FileInputStream(文件字节读取流)）
        try {
            inStream = new FileInputStream(new File(filePath));//实例化对象
            workbook = WorkbookFactory.create(inStream);//创建工作薄
            sheet = workbook.getSheet(sheetName);//通过sheet名称获取Excel中的某一个数据表
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (inStream != null){
                    inStream.close();//关闭文件流
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    //根据Cell类型设置数据
    private String getCellValue(Cell cell){
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null){
            //判断当前Cell的Type
            switch (cell.getCellType()){
                //当前Cell的Type为NUMERIC-数字（分为数字格式和日期格式）
                case Cell.CELL_TYPE_NUMERIC:
                    //判断当前的cell是否为Date（日期格式）
                    if (DateUtil.isCellDateFormatted(cell)){
                        cellValue = formatter.formatCellValue(cell);//使用formatCellValue方法读取日期类型单元格
                    }else { //数字格式（double or int）
                        double value = cell.getNumericCellValue();//使用getNumericCellValue方法读取数字类型单元格
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                //String-字符串
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();//获取当前的Cell字符串
                    break;
                //Boolean-布尔
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                //FORMULA-公式
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = String.valueOf(cell.getCellFormula());
                    break;
                //BLANK-空值
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                //ERROR-故障
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }

    private void getSheetData(){
        listData = new ArrayList<>();
        mapData = new ArrayList<>();
        columnHeaderList = new ArrayList<>();
        int numOfRows = sheet.getLastRowNum() + 1;
        for (int i = 0; i < numOfRows; i++){
            Row row = sheet.getRow(i);
            Map<String,String> map = new HashMap<>();
            List<String> list = new ArrayList<>();

            if (row != null){
                for (int j =0; j < row.getLastCellNum(); j++){
                    Cell cell = row.getCell(j);
                    if (i == 0){
                        columnHeaderList.add(getCellValue(cell));
                    }else {
                        map.put(columnHeaderList.get(j),this.getCellValue(cell));
                    }
                    list.add(this.getCellValue(cell));
                }
            }
            if (i > 0){
                mapData.add(map);
            }
            listData.add(list);
        }
        flag = true;

        for (int i = 0; i  < listData.size(); i++){
            for (int j = 0; j < listData.get(i).size(); j++){
                System.out.println(listData.get(i).get(j).toString());
            }
        }
    }

    public String getCellData(int row, int col){
        if (row <= 0 || col <= 0){
            return null;
        }
        if (!flag){
            this.getSheetData();
        }
        if (listData.size() >= row && listData.get(row - 1).size() >= col){
            return listData.get(row - 1).get(col - 1);
        }else {
            return null;
        }
    }

    public String getCellData(int row, String headerName){
        if (row <= 0){
            return null;
        }
        if (!flag){
            this.getSheetData();
        }
        if (mapData.size() >= row && mapData.get(row - 1).containsKey(headerName)){
            return mapData.get(row - 1).get(headerName);
        }else {
            return null;
        }
    }

    public Object[][] getSheetData2() {
        List<Object[]> result = new ArrayList<>();
        listData = new ArrayList<>();
        mapData = new ArrayList<>();
        columnHeaderList = new ArrayList<>();

        int numOfRows = sheet.getLastRowNum() + 1;
        System.out.println("总共有" + numOfRows + "行！");
        for (int i = 0; i < numOfRows; i++){
            Row row = sheet.getRow(i);
            Map<String, String> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            Object[] ol = new Object[row.getLastCellNum()];

            if (row  != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    //System.out.println("第"+i+"行--- row.getLastCellNum()==="+row.getLastCellNum());
                    Cell cell = row.getCell(j);
                    if (i == 0) {
                        ol[j] = this.getCellValue(cell);
                        //System.out.println(j+"--------this.getCellValue(cell)="+this.getCellValue(cell));
                        columnHeaderList.add(getCellValue(cell));
                    } else {
                        ol[j] = this.getCellValue(cell);
                        //System.out.println(j+"--------this.getCellValue(cell)="+this.getCellValue(cell));
                        map.put(columnHeaderList.get(j), this.getCellValue(cell));
                    }
                    list.add(this.getCellValue(cell));
                }
            }
                if (i > 0){
                    mapData.add(map);
                }
                result.add(ol);
                listData.add(list);
        }
        //测试数据excel数据用：
            /*for (int i = 0; i < result.size(); i++){
                for (int j = 0; j < result.get(i).length; j++){
                    System.out.print(result.get(i)[j]+"|");
                }
                System.out.println();
            }*/
            results = new Object[result.size()][];

            for (int i = 0; i < result.size(); i++){
                results[i] = result.get(i);
            }
            flag = true;
            System.out.println("results.length==" + results.length);
            return results;
    }

    public static void main(String[] args){
        /*Object[][] obj1;
        ExcelReader eh = new ExcelReader("D:\\IdeaProjects\\simpleTest\\TestCase\\simpleHttpTest.xlsx", "Sheet1");
        Object[][] sheetData2 = eh.getSheetData2();
        System.out.println(sheetData2.length + "-------");
        for (int i = 0; i < sheetData2.length; i++){
            for (int j = 0; j < sheetData2[i].length; j++){
                System.out.print(sheetData2[i][j] + "|");
            }
            System.out.println();
        }*/
    }
}
