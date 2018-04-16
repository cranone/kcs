package com.shadego.kcs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class HttpUtil {
	private static final String USER_AGENT="Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36";
	
	public static String sendGet(String url, String param) {
		String result = "";
		try {
			String urlNameString = url + "?" + param;
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", USER_AGENT);
			// 建立实际的连接
			conn.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			/*
			 * for (String key : map.keySet()) { System.out.println(key + "--->"
			 * + map.get(key)); }
			 */
			// 定义 BufferedReader输入流来读取URL的响应
			try(BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()))){
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String sendPost(String url, String param) {
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			try(PrintWriter out = new PrintWriter(conn.getOutputStream())){
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
				// 获取URLConnection对象对应的输出流
				// 定义BufferedReader输入流来读取URL的响应
				try(BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()))){
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
