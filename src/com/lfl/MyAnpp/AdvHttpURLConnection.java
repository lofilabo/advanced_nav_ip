package com.lfl.MyAnpp;
 
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class AdvHttpURLConnection {

	private final String USER_AGENT = "Mozilla/5.0";
	 
 
	// HTTP GET request
	public void sendGet(String urlBasis, String appendable_data) throws Exception {
		
        StringBuilder stringBuilder = new StringBuilder(urlBasis);
        stringBuilder.append( appendable_data );
        //stringBuilder.append(URLEncoder.encode( "NEKRO" , "UTF-8"));
        URL obj = new URL(stringBuilder.toString());
        
        
        try {
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Charset", "UTF-8");
			/*
			System.out.println("\nSending request to URL : " + obj);
			System.out.println("Response Code : " + con.getResponseCode());
			System.out.println("Response Message : " + con.getResponseMessage());
	 		*/
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
	 
			while ((line = in.readLine()) != null) {
				response.append(line);
			}
			in.close();
	 		//System.out.println(response.toString());
	 		
        
        }catch(Exception e) {
        	System.err.println("No Socket. Continuing as normal.");
        } 
	}
 
	public void sendPost() throws Exception {
	    
		StringBuilder tokenUri=new StringBuilder("param1=");
		tokenUri.append(URLEncoder.encode("value1","UTF-8"));
		tokenUri.append("&param2=");
		tokenUri.append(URLEncoder.encode("value2","UTF-8"));
		tokenUri.append("&param3=");
		tokenUri.append(URLEncoder.encode("value3","UTF-8"));
 
		String url = "https://example.com";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "UTF-8");
 
		con.setDoOutput(true);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
        outputStreamWriter.write(tokenUri.toString());
        outputStreamWriter.flush();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + "?n=v");
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		System.out.println(response.toString());
 
	}
 
}	
	

