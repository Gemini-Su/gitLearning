package com.http.simpleTest;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpInterfaceTest {

    /*
    *发送GET方法的请求
    */
    public String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlName = url + "?" + param;
            System.out.println("Get请求接口：" + urlName);
            URL realUrl = new URL(urlName);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0");
            // 建立实际的连接
            conn.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "---->" + map.get(key));
            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        } finally { // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /*
    *向指定URL发送POST方法的请求
    */
    public String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        }catch (Exception e){
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args){

        HttpInterfaceTest httpInterfaceTest = new HttpInterfaceTest();

        // 调用天气预报接口请求参数方式一
        String postUrl = "http://op.juhe.cn/onebox/weather/query";
        String postParamsOne = "&cityname=上海市" + "&key=1234567890";
        // 调用天气预报接口请求参数方式二
        String postParamsTwo = "{'cityname':'上海市'," + "'key':'1234567890'}";
        JSONObject jsonPostParamsTwo = JSONObject.fromObject(postParamsTwo);
        System.out.println("------------");
        // 发送post请求
        String postResultOne = httpInterfaceTest.sendPost(postUrl, postParamsOne);
        System.out.println("POST请求参数一：" + postParamsOne);
        System.out.println("POST请求响应结果：" + postResultOne);
        // 发送post请求
        String postResultTwo = httpInterfaceTest.sendPost(postUrl, jsonPostParamsTwo.toString());
        System.out.println("POST请求参数二：" + jsonPostParamsTwo);
        System.out.println("POST请求响应结果：" + postResultTwo);

        JSONObject jsonObject = JSONObject.fromObject(postResultTwo);
        Object resultcode = jsonObject.get("resultcode");
        Object reason = jsonObject.get("reason");
        Object error_code = jsonObject.get("error_code");
        System.out.println("resultcode==" + resultcode + "|  reason=" + reason + "    | error_code= " + error_code);
    }
}
