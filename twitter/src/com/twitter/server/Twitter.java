package com.twitter.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Twitter {
	public String getUsersShow(String screen_name)
			throws InvalidKeyException, NoSuchAlgorithmException, MalformedURLException, IOException {
		String method = "GET";
		String url = "https://api.twitter.com/1.1/users/show.json";
		Map<String, String> paramMap = getUsersShowParamMap(screen_name);
		Map<String, String> oAuthParamMap = getOAuthParamMap();
		
		String urlWithParams = getUrlWithParams(url, paramMap);
		String signatureBaseString = getSignatureBaseString(method, url, paramMap, oAuthParamMap);
		String authorizationHeaderValue = getAuthorizationHeaderValue(signatureBaseString, oAuthParamMap);
	    
		return request(urlWithParams, authorizationHeaderValue);
	}
	
	private String request(String urlWithParams, String authorizationHeaderValue)
			throws MalformedURLException, IOException {
		URLConnection urlConnection = new URL(urlWithParams).openConnection();
	    urlConnection.setRequestProperty("Authorization", authorizationHeaderValue);
	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = br.readLine()) != null) {
	    	sb.append(line);
	    }

	    br.close();
	    
		return sb.toString();
	}
	
	private Map<String, String> getUsersShowParamMap(String screen_name) {
	    Map<String, String> urlParamMap = new HashMap<String, String>();
	    urlParamMap.put("screen_name",screen_name);
	    
	    return urlParamMap;
	}
	
	private Map<String, String> getOAuthParamMap() {
		String oAuthConsumerKey = "TnPt02oenGbSOaYRxWKSTw";
	    String oAuthAccessToken = "1615009260-oGtsMNOHlhLPKkFBUWdzlXE1mD86rQzWLuWXPhB";
	    String oAuthNonce = String.valueOf(System.currentTimeMillis());
	    String oAuthSignatureMethod = "HMAC-SHA1";
	    String oAuthTimestamp = getTimestamp();
	    String oAuthVersion = "1.0";
	    
	    Map<String, String> paramMap = new HashMap<String, String>();
	    
	    paramMap.put("oauth_consumer_key", oAuthConsumerKey);
	    paramMap.put("oauth_nonce", oAuthNonce);
	    paramMap.put("oauth_signature_method", oAuthSignatureMethod);
	    paramMap.put("oauth_timestamp", oAuthTimestamp);
	    paramMap.put("oauth_token", oAuthAccessToken);
	    paramMap.put("oauth_version", oAuthVersion);

	    return paramMap;
	}
	
	private String getSignatureBaseString(String method, String url,
			Map<String, String> urlParamMap, Map<String, String> oAuthParamMap) throws UnsupportedEncodingException {
		TreeMap<String, String> sortedParamMap = new TreeMap<String, String>();
		sortedParamMap.putAll(urlParamMap);
		sortedParamMap.putAll(oAuthParamMap);
		
		StringBuffer paramStringBuffer = new StringBuffer();
		for (Entry<String, String> paramEntry : sortedParamMap.entrySet()) {
			if (!paramEntry.equals(sortedParamMap.firstEntry())) {
				paramStringBuffer.append("&");
	        }
			paramStringBuffer.append(paramEntry.getKey() + "=" + paramEntry.getValue());
		}
		
		String signatureBaseStringTemplate = "%s&%s&%s";
	    String signatureBaseString =  String.format(
	    		signatureBaseStringTemplate, 
	    		URLEncoder.encode(method, "UTF-8"), 
	    		URLEncoder.encode(url, "UTF-8"),
	    		URLEncoder.encode(paramStringBuffer.toString(), "UTF-8"));
	    
	    return signatureBaseString;
	}
	
	private String getAuthorizationHeaderValue(String signatureBaseString, Map<String, String> oAuthParamMap)
			throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
		String oAuthConsumerSecret = "kfhxPyTCSGgZ49RDJzWpvEHeCxMmoykti2MWxpfBtXU";
	    String oAuthAccessTokenSecret = "LPiHyCWYTBHMWlwZXhHRKid5ls0mcIaeRh0xHMAto8Q";
		String compositeKey = URLEncoder.encode(oAuthConsumerSecret, "UTF-8") + "&" 
				+ URLEncoder.encode(oAuthAccessTokenSecret, "UTF-8");

	    String oAuthSignature =  computeSignature(signatureBaseString, compositeKey);
	    String oAuthSignatureEncoded = URLEncoder.encode(oAuthSignature, "UTF-8");
	    
	    String authorizationHeaderValueTempl = 
	    		"OAuth oauth_consumer_key=\"%s\", oauth_nonce=\"%s\", oauth_signature=\"%s\", " + 
	    		"oauth_signature_method=\"%s\", oauth_timestamp=\"%s\", oauth_token=\"%s\", oauth_version=\"%s\"";
	    String authorizationHeaderValue = String.format(
	    		authorizationHeaderValueTempl,
	    		oAuthParamMap.get("oauth_consumer_key"),
	    		oAuthParamMap.get("oauth_nonce"),
	    		oAuthSignatureEncoded,
	    		oAuthParamMap.get("oauth_signature_method"),
	    		oAuthParamMap.get("oauth_timestamp"),
	    		oAuthParamMap.get("oauth_token"),
	    		oAuthParamMap.get("oauth_version"));
	    
	    return authorizationHeaderValue;
	}
	
	private String getUrlWithParams(String url, Map<String, String> paramMap) {
		StringBuffer urlWithParams = new StringBuffer(url);
		TreeMap<String, String> treeMap = new TreeMap<String, String>();
		treeMap.putAll(paramMap);
		for (Entry<String, String> paramEntry : treeMap.entrySet()) {
			if (paramEntry.equals(treeMap.firstEntry())) {
				urlWithParams.append("?");
			} else {
				urlWithParams.append("&");
			}
			urlWithParams.append(paramEntry.getKey() + "=" + paramEntry.getValue());
		}
		
		return urlWithParams.toString();
	}
	
	private static String computeSignature(String baseString, String keyString)
			throws NoSuchAlgorithmException, InvalidKeyException
	{
	    SecretKey secretKey = null;

	    byte[] keyBytes = keyString.getBytes();
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    Mac mac = Mac.getInstance("HmacSHA1");

	    mac.init(secretKey);

	    byte[] text = baseString.getBytes();

	    return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
	}

	private String getTimestamp() {
	    long millis = System.currentTimeMillis();
	    long secs = millis / 1000;
	    return String.valueOf( secs );
	}
}
