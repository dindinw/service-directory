/**
 * Copyright (c) 2013-2014 by Cisco Systems, Inc. 
 * All rights reserved. 
 */
package com.cisco.oss.foundation.directory.impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.oss.foundation.directory.exception.DirectoryServerClientException;
import com.cisco.oss.foundation.directory.exception.ErrorCode;
import com.cisco.oss.foundation.directory.exception.ServiceDirectoryError;
import com.cisco.oss.foundation.directory.utils.HttpResponse;
import com.cisco.oss.foundation.directory.utils.HttpUtils;
import com.cisco.oss.foundation.directory.utils.HttpUtils.HttpMethod;
import com.cisco.oss.foundation.directory.utils.JsonSerializer;

/**
 * It is the HTTP invoker to the ServiceDirectory ServerNode.
 * 
 * It wraps the complicity of HttpClient and exposes a easy method to invoke RESTful services.
 * 
 * @author zuxiang
 *
 */
public class DirectoryInvoker {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DirectoryInvoker.class);

	private final JsonSerializer serializer ;
	private String directoryAddresses;
	private HttpUtils httpUtils = null;

	/**
	 * Constructor.
	 * 
	 * @param directoryAddresses
	 * 		The remote ServiceDirectory node address array, in the format of http://<host>:<port>
	 */
	public DirectoryInvoker(String directoryAddresses, JsonSerializer serializer) {
		this.directoryAddresses = directoryAddresses;
		this.serializer = serializer;
		this.httpUtils = HttpUtils.getInstance();
	}

	/**
	 * Invoke the HTTP RESTful Service.
	 * 
	 * @param uri		The URI of the RESTful service.
	 * @param payload	The HTTP body String.
	 * @param method	THe HTTP method.
	 * @return
	 * 		the HttpResponse.
	 */
	public HttpResponse invoke(String uri, String payload, HttpMethod method) {
		HttpResponse result = null;
		String url = directoryAddresses + uri;
		try {
			if (method == null || method == HttpMethod.GET) {
				result = getHttpUtils().getJson(url);
			} else if (method == HttpMethod.POST) {
				result = getHttpUtils().postJson(url, payload);
			} else if (method == HttpMethod.PUT) {
				result = getHttpUtils().putJson(url, payload);
			} else if (method == HttpMethod.DELETE) {
				result = getHttpUtils().deleteJson(url);
			}
		} catch (IOException e) {
			String errMsg = "Send HTTP Request to remote Directory Server failed";
			LOGGER.error(errMsg);
			LOGGER.debug(errMsg, e);
			ServiceDirectoryError sde = new ServiceDirectoryError(ErrorCode.HTTP_CLIENT_ERROR, errMsg);
			throw new DirectoryServerClientException(sde, e);
		}
		
		if (result.getHttpCode() < 200 || result.getHttpCode() >= 300) {
			String errorBody = result.getRetBody();
			
			if(errorBody == null || errorBody.isEmpty()){
				LOGGER.error("Invoke remote directory server failed, status=" + result.getHttpCode() 
						+ ", Error Message body is empty.");
				ServiceDirectoryError sde = new ServiceDirectoryError(
						ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, "Error Message body is empty.");
				throw new DirectoryServerClientException(sde);
			}
			ServiceDirectoryError sde = null;
			try {
				sde = (ServiceDirectoryError) serializer
						.deserialize(errorBody.getBytes(),
								ServiceDirectoryError.class);
			} catch (IOException  e) {
				String errMsg = "Deserialize error body message failed";
				LOGGER.error(errMsg);
				LOGGER.debug(errMsg + ", messageBody=" + errorBody, e);
				ServiceDirectoryError sde1 = new ServiceDirectoryError(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, errMsg);
				throw new DirectoryServerClientException(sde1, e);
			}

			if (sde != null) {
				throw new DirectoryServerClientException(sde);
			}
		}
		return result;
	}
	
	/**
	 * Invoke the HTTP RESTful Service.
	 * 
	 * @param uri		The URI of the RESTful service.
	 * @param payload	The HTTP body string.
	 * @param method	The HTTP method.
	 * @param headers	The HTTP headers.
	 * @return
	 * 		the HttpResponse.
	 */
	public HttpResponse invoke(String uri, String payload, HttpMethod method, Map<String, String> headers) {
		HttpResponse result = null;
		String url = directoryAddresses + uri;
		try {
			if (method == HttpMethod.PUT) {
				result = getHttpUtils().put(url, payload, headers);
			} 
		} catch (IOException e) {
			String errMsg = "Send HTTP Request to remote Directory Server failed";
			LOGGER.error(errMsg);
			LOGGER.debug(errMsg, e);
			ServiceDirectoryError sde = new ServiceDirectoryError(ErrorCode.HTTP_CLIENT_ERROR, errMsg);
			throw new DirectoryServerClientException(sde, e);
		}
		
		if (result.getHttpCode() < 200 || result.getHttpCode() >= 300) {
			String errorBody = result.getRetBody();
			
			if(errorBody == null || errorBody.isEmpty()){
				LOGGER.error("Invoke remote directory server failed, status=" + result.getHttpCode() 
						+ ", Error Message body is empty.");
				ServiceDirectoryError sde = new ServiceDirectoryError(
						ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, "Error Message body is empty.");
				throw new DirectoryServerClientException(sde);
			}
			ServiceDirectoryError sde = null;
			try {
				sde = (ServiceDirectoryError) serializer
						.deserialize(errorBody.getBytes(),
								ServiceDirectoryError.class);
			} catch (IOException  e) {
				String errMsg = "Deserialize error body message failed";
				LOGGER.error(errMsg);
				LOGGER.debug(errMsg + ", messageBody=" + errorBody, e);
				ServiceDirectoryError sde1 = new ServiceDirectoryError(ErrorCode.REMOTE_DIRECTORY_SERVER_ERROR, errMsg);
				throw new DirectoryServerClientException(sde1, e);
			}

			if (sde != null) {
				throw new DirectoryServerClientException(sde);
			}
		}
		return result;
	}

	private HttpUtils getHttpUtils(){
		return this.httpUtils;
	}
	
	/**
	 * Keep it default for unit test.
	 * 
	 * @param httpUtils
	 * 		the customer HttpUtils.
	 */
	void setHttpUtils(HttpUtils httpUtils){
		this.httpUtils = httpUtils;
	}
}
