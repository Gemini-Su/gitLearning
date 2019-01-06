package com.http.simpleTest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestHttpInterfaceTest {
    public static HttpInterfaceTest ht;
    ExcelReader ex;
    static ExcelUtil excelUtil;
    @BeforeTest
    public void init(){
        String ExcelFilePath = "D:\\IdeaProjects\\simpleTest\\TestCase\\simpleHttpTest.xlsx";
        String sheetName = "Sheet1";
        ht = new HttpInterfaceTest();
        ex = new ExcelReader(ExcelFilePath, sheetName);
        try {
            excelUtil.setExcelFile(ExcelFilePath, sheetName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "dp")
    public void testSendPost(String rowNum, String Url, String paras) throws Exception{
        System.out.println("rowNum=" + rowNum + "；  URL=" + Url + " ;   paras=" + paras);
        Integer it = new Integer(rowNum);
        int row = it.intValue();
        if (paras.contains("&")){
            String s1 = ht.sendPost(Url, paras);
            excelUtil.setCellData(row, 3, s1);
            System.out.println(s1);
        }else {
            try {
                JSONObject jsonObject = JSONObject.fromObject(paras);
                String s = ht.sendPost(Url, jsonObject.toString());
                excelUtil.setCellData(row, 3, s);
                System.out.println(s);
            }catch (JSONException jsonException){
                System.out.println("标题行不能进行转换！");
            }
        }
    }

    @DataProvider
    public Object[][] dp(){
        Object[][] sheetData2 = ex.getSheetData2();
        /*System.out.println(sheetData2.length + "-------");
        for (int i = 0; i < sheetData2.length; i++) {
            for (int j = 0; j < sheetData2[i].length; j++) {
                System.out.print(sheetData2[i][j] + "|");
            }
            System.out.println();
        }*/
        return sheetData2;
    }
}
