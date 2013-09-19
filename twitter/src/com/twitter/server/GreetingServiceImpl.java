package com.twitter.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.twitter.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws InvalidKeyException, NoSuchAlgorithmException, MalformedURLException, IOException {
		return input;
/*		Twitter twitter = new Twitter();
		return twitter.getUsersShow(input);*/
	}   

}
