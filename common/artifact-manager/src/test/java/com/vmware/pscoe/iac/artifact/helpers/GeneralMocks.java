/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.helpers;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class GeneralMocks {
	private static final int PRIME_NUMBER_200 = 200;

	private GeneralMocks() {
	}

	/**
	 * Response entity mock with a status code of 200 and no headers
	 *
	 * @param body - Body of response
	 * @param <T>  - Response type
	 *
	 * @return ResponseEntity<T>
	 */
	public static <T> ResponseEntity<T> mockResponseEntity(T body) {
		return mockResponseEntity(body, PRIME_NUMBER_200);
	}

	/**
	 * Response entity mock with no headers
	 *
	 * @param body       - Body of response
	 * @param statusCode - status code of the response
	 * @param <T>        - Response type
	 *
	 * @return ResponseEntity<T>
	 */
	public static <T> ResponseEntity<T> mockResponseEntity(T body, int statusCode) {
		Map<String, String> headers = new HashMap<>();

		return mockResponseEntity(body, statusCode, headers);
	}

	/**
	 * Mocks a response entity
	 * You can pass a status code and it will create a HttpStatus object.
	 *
	 * @return ResponseEntity
	 */
	public static <T> ResponseEntity<T> mockResponseEntity(
			T body,
			int statusCode,
			Map<String, String> headers) {
		ResponseEntity<T> response = Mockito.mock(ResponseEntity.class);
		HttpStatus statusCodeObj = HttpStatus.valueOf(statusCode);
		HttpHeaders responseHeaders = new HttpHeaders();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			responseHeaders.set(entry.getKey(), entry.getValue());
		}

		when(response.getBody()).thenReturn(body);
		when(response.getStatusCode()).thenReturn(statusCodeObj);
		when(response.getHeaders()).thenReturn(responseHeaders);

		return response;
	}
}
