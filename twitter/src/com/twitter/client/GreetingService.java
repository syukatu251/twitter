package com.twitter.client;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException, UnsupportedEncodingException, GeneralSecurityException, Exception;
}
