package com.vmware.pscoe.iac.artifact.helpers;

import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class GeneralMocks {
	/**
	 * Response entity mock with a status code of 200 and no headers
	 *
	 * @param	body - Body of response
	 * @param	<T> - Response type
	 *
	 * @return	ResponseEntity<T>
	 */
	public static <T> ResponseEntity<T> mockResponseEntity( T body ) {
		return mockResponseEntity( body, 200 );
	}

	/**
	 * Response entity mock with no headers
	 *
	 * @param	body - Body of response
	 * @param	statusCode - status code of the response
	 * @param	<T> - Response type
	 *
	 * @return	ResponseEntity<T>
	 */
	public static <T> ResponseEntity<T> mockResponseEntity( T body, int statusCode ) {
		Map<String, String> headers	= new HashMap<>();

		return mockResponseEntity( body, statusCode, headers );
	}

	/**
	 * Mocks a response entity
	 * You can pass a status code and it will create a HttpStatus object.
	 *
	 * @return	ResponseEntity
	 */
	public static <T> ResponseEntity<T> mockResponseEntity(
		T body,
		int statusCode,
		Map<String, String> headers
	) {
		ResponseEntity<T> response	= Mockito.mock( ResponseEntity.class );
		HttpStatus statusCodeObj	= HttpStatus.valueOf( statusCode );
		HttpHeaders responseHeaders	= new HttpHeaders();

		for ( Map.Entry<String, String> entry : headers.entrySet() ) {
			responseHeaders.set( entry.getKey(), entry.getValue() );
		}

		when( response.getBody() ).thenReturn( body );
		when( response.getStatusCode() ).thenReturn( statusCodeObj );
		when( response.getStatusCodeValue() ).thenReturn( statusCode );
		when( response.getHeaders() ).thenReturn( responseHeaders );

		return response;
	}
}
