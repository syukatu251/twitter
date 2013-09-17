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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gwt.dev.util.collect.HashMap;

public class Twitter {
	public String getUsersShow(String screen_name) throws InvalidKeyException, NoSuchAlgorithmException, MalformedURLException, IOException {
		String method = "GET";
	    String url = "https://api.twitter.com/1.1/users/show.json";
	    List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
	    urlParams.add( new BasicNameValuePair("screen_name",screen_name) );

	    String oAuthConsumerKey = "TnPt02oenGbSOaYRxWKSTw";
	    String oAuthConsumerSecret = "kfhxPyTCSGgZ49RDJzWpvEHeCxMmoykti2MWxpfBtXU"; //<--- DO NOT SHARE THIS VALUE

	    String oAuthAccessToken = "1615009260-oGtsMNOHlhLPKkFBUWdzlXE1mD86rQzWLuWXPhB";
	    String oAuthAccessTokenSecret = "LPiHyCWYTBHMWlwZXhHRKid5ls0mcIaeRh0xHMAto8Q"; //<--DO NOT SHARE THIS VALUE

	    String oAuthNonce = String.valueOf(System.currentTimeMillis());
	    String oAuthSignatureMethod = "HMAC-SHA1";
	    String oAuthTimestamp = time();
	    String oAuthVersion = "1.0";

	    String signatureBaseString1 = method;
	    String signatureBaseString2 = url;

	    List<NameValuePair> allParams = new ArrayList<NameValuePair>();
	    allParams.add(new BasicNameValuePair("oauth_consumer_key", oAuthConsumerKey));
	    allParams.add(new BasicNameValuePair("oauth_nonce", oAuthNonce));
	    allParams.add(new BasicNameValuePair("oauth_signature_method", oAuthSignatureMethod));
	    allParams.add(new BasicNameValuePair("oauth_timestamp", oAuthTimestamp));
	    allParams.add(new BasicNameValuePair("oauth_token", oAuthAccessToken));
	    allParams.add(new BasicNameValuePair("oauth_version", oAuthVersion));
	    allParams.addAll(urlParams);

	    Collections.sort(allParams, new NvpComparator());

	    StringBuffer signatureBaseString3 = new StringBuffer();
	    for(int i=0;i<allParams.size();i++)
	    {
	        NameValuePair nvp = allParams.get(i);
	        if (i>0) {
	            signatureBaseString3.append("&");
	        }
	        signatureBaseString3.append(nvp.getName() + "=" + nvp.getValue());
	    }

	    String signatureBaseStringTemplate = "%s&%s&%s";
	    String signatureBaseString =  String.format(signatureBaseStringTemplate, 
	                                                                URLEncoder.encode(signatureBaseString1, "UTF-8"), 
	                                                                URLEncoder.encode(signatureBaseString2, "UTF-8"),
	                                                                URLEncoder.encode(signatureBaseString3.toString(), "UTF-8"));

	    System.out.println("signatureBaseString: "+signatureBaseString);

	    String compositeKey = URLEncoder.encode(oAuthConsumerSecret, "UTF-8") + "&" + URLEncoder.encode(oAuthAccessTokenSecret, "UTF-8");

	    String oAuthSignature =  computeSignature(signatureBaseString, compositeKey);
	    System.out.println("oAuthSignature       : "+oAuthSignature);

	    String oAuthSignatureEncoded = URLEncoder.encode(oAuthSignature, "UTF-8");
	    System.out.println("oAuthSignatureEncoded: "+oAuthSignatureEncoded);

	    String authorizationHeaderValueTempl = "OAuth oauth_consumer_key=\"%s\", oauth_nonce=\"%s\", oauth_signature=\"%s\", oauth_signature_method=\"%s\", oauth_timestamp=\"%s\", oauth_token=\"%s\", oauth_version=\"%s\"";

	    String authorizationHeaderValue = String.format(authorizationHeaderValueTempl,
	                                                        oAuthConsumerKey,
	                                                        oAuthNonce,
	                                                        oAuthSignatureEncoded,
	                                                        oAuthSignatureMethod,
	                                                        oAuthTimestamp,
	                                                        oAuthAccessToken,
	                                                        oAuthVersion);
	    System.out.println("authorizationHeaderValue: "+authorizationHeaderValue);

	    StringBuffer urlWithParams = new StringBuffer(url);
	    for(int i=0;i<urlParams.size();i++) {
	        if(i==0) 
	        {
	            urlWithParams.append("?");
	        }
	        else
	        {
	            urlWithParams.append("&");
	        }
	        NameValuePair urlParam = urlParams.get(i);
	        urlWithParams.append(urlParam.getName() + "=" + urlParam.getValue());
	    }

	    System.out.println("urlWithParams: "+urlWithParams.toString());
	    System.out.println("authorizationHeaderValue:"+authorizationHeaderValue);
	    
	    URLConnection urlConnection = new URL(urlWithParams.toString()).openConnection();
	    urlConnection.setRequestProperty("Authorization", authorizationHeaderValue);
	    
	    
	    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

	    StringBuilder sb = new StringBuilder();

	    String line;

	    while ((line = br.readLine()) != null) {
	    	sb.append(line);
	    }

	    System.out.println(sb.toString());

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
	    String oAuthTimestamp = time();
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
			if (!paramEntry.getKey().equals(sortedParamMap.firstKey())) {
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
	
	private String getAuthorizationHeaderValue(String signatureBaseString)
			throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
		String oAuthConsumerSecret = "kfhxPyTCSGgZ49RDJzWpvEHeCxMmoykti2MWxpfBtXU";
	    String oAuthAccessTokenSecret = "LPiHyCWYTBHMWlwZXhHRKid5ls0mcIaeRh0xHMAto8Q";
		String compositeKey = URLEncoder.encode(oAuthConsumerSecret, "UTF-8") + "&" 
				+ URLEncoder.encode(oAuthAccessTokenSecret, "UTF-8");

	    String oAuthSignature =  computeSignature(signatureBaseString, compositeKey);
	    System.out.println("oAuthSignature       : "+oAuthSignature);

	    String oAuthSignatureEncoded = URLEncoder.encode(oAuthSignature, "UTF-8");
	    System.out.println("oAuthSignatureEncoded: "+oAuthSignatureEncoded);
	    
	    Map<String, String> oAuthParamMap = getOAuthParamMap();

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

	private String time() {
	    long millis = System.currentTimeMillis();
	    long secs = millis / 1000;
	    return String.valueOf( secs );
	}
	
	private class NvpComparator implements Comparator<NameValuePair> {

		public int compare(NameValuePair arg0, NameValuePair arg1) {
		    String name0 = arg0.getName();
		    String name1 = arg1.getName();
		    return name0.compareTo(name1);
		}
	}
}
