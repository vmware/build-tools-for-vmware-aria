package com.vmware.pscoe.iac.artifact.rest.vrang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVraNg;
import com.vmware.pscoe.iac.artifact.helpers.stubs.AbxActionMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.CustomResourceMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.stubs.PropertyGroupMockBuilder;
import com.vmware.pscoe.iac.artifact.helpers.vrang.RestClientVraNgPrimitiveTestDouble;
import com.vmware.pscoe.iac.artifact.helpers.vrang.RestClientVraNgPrimitiveTestResponseProvider;
import com.vmware.pscoe.iac.artifact.model.abx.AbxAction;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomForm;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCustomResource;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgProject;
import com.vmware.pscoe.iac.artifact.model.vrang.VraNgPropertyGroup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestClientVraNgPrimitiveTest {

	protected RestTemplate restTemplate;
	protected ConfigurationVraNg config;
	protected RestClientVraNgPrimitiveTestDouble restClient;

	@BeforeEach
	void init() {
		restTemplate = Mockito.mock(RestTemplate.class);
		config = Mockito.mock(ConfigurationVraNg.class);
		restClient = new RestClientVraNgPrimitiveTestDouble(config, restTemplate);
		when(config.getHost()).thenReturn("vra-l-01a.corp.local");
		when(config.getPort()).thenReturn(443);
	}

	@Test
	void testGetProjectsPrimitiveEmptyFirstPage() {
		// GIVEN
		int totalElements = 0;
		int pageSize = 500;
		int page = 0;

		// WHEN
		when(
			restTemplate.exchange(
				any(URI.class),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, page));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveNonEmptyFirstPage() {
		// GIVEN
		int totalElements = 51;
		int pageSize = 500;
		int page = 0;

		// WHEN
		when(
			restTemplate.exchange(
				any(URI.class),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, page));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveFirstPageEdgeCase() {
		// GIVEN
		int totalElements = 500;
		int pageSize = 500;
		int page = 0;

		// WHEN
		when(
			restTemplate.exchange(
				any(URI.class),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, page));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveSecondPageWithSingleElement() throws URISyntaxException {
		// GIVEN
		int totalElements = 501;
		int pageSize = 500;
		int firstPage = 0;
		int secondPage = 1;

		// WHEN
		when(
			restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d", pageSize, (pageSize * firstPage)))),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, firstPage));

		when(
			restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d", pageSize, (pageSize * secondPage)))),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, secondPage));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveSecondPageWithMultipleElements() throws URISyntaxException {
		// GIVEN
		int totalElements = 755;
		int pageSize = 500;
		int firstPage = 0;
		int secondPage = 1;

		// WHEN
		when(
			restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d", pageSize, (pageSize * firstPage)))),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, firstPage));

		when(
			restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d", pageSize, (pageSize * secondPage)))),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, secondPage));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testGetProjectsPrimitiveSecondPageEdgeCase() throws URISyntaxException {
		// GIVEN
		int totalElements = 1000;
		int pageSize = 500;
		int firstPage = 0;
		int secondPage = 1;

		// WHEN
		when(
			restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d", pageSize, (pageSize * firstPage)))),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, firstPage));

		when(
			restTemplate.exchange(
				eq(new URI(String.format("https://vra-l-01a.corp.local/iaas/api/projects?%%24top=%d&%%24skip=%d", pageSize, (pageSize * secondPage)))),
				any(HttpMethod.class),
				any(HttpEntity.class),
				any(Class.class)
			)
		).thenReturn(RestClientVraNgPrimitiveTestResponseProvider.getPaginatedProjectResponse(totalElements, pageSize, secondPage));
		List<VraNgProject> projects = restClient.testGetProjectsPrimitive();

		// THEN
		assertEquals(totalElements, projects.size());
	}

	@Test
	void testImportCustomFormPrimitiveAlwaysCustomFormFormatNull() {
		VraNgCustomForm parameter = new VraNgCustomForm(
			"id",
			"name",
			"form",
			"styles",
			"VRO",
			"VRO",
			"OK",
			null
		);
		String sourceId = "sourceId";

		Assertions.assertDoesNotThrow(()->{
			this.restClient.importCustomFormPrimitive(parameter,sourceId);
		});
	}

	@Test
	void testImportCustomFormPrimitiveAlwaysCustomFormFormatNotNull() {
		VraNgCustomForm parameter = new VraNgCustomForm(
			"id",
			"name",
			"form",
			"styles",
			"VRO",
			"VRO",
			"OK",
			"JSON"
		);
		String sourceId = "sourceId";

		Assertions.assertDoesNotThrow(()->{
			this.restClient.importCustomFormPrimitive(parameter,sourceId);
		});
	}

	@Test
	void testJSONObjectEmptyShouldBeTrue() throws NullPointerException {
		JsonObject ob = new JsonObject();
		ob.add("mapping",new JsonObject());
		ob.add("_links", new JsonObject());
		boolean jsonEmpty = this.restClient.jsonObjectValid(ob);

		Assertions.assertTrue(jsonEmpty);
	}

	@Test
	void testJSONObjectEmptyShouldNotBeTrue() throws NullPointerException {
		JsonObject ob = new JsonObject();
		JsonObject song = new JsonObject();
		song.add("temp",new JsonObject());
		ob.add("mapping",song);
		ob.add("_links", song);
		boolean jsonEmpty = this.restClient.jsonObjectValid(ob);

		Assertions.assertFalse(jsonEmpty);
	}

	@Test
	void testGetAllImageMappingsByRegionPrimitiveShouldBeEmpty(){
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/iaas/api/images"));
		when(
			this.restTemplate.exchange(uri,HttpMethod.GET,
			RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
			String.class)
		).thenReturn(
			new ResponseEntity<>("{\"content\":[{\"mapping\":{},\"_links\":{}}]}", HttpStatus.OK)
		);

		Assertions.assertDoesNotThrow(()->{
			this.restClient.getAllImageMappingsByRegionPrimitive();
		});
	}

	@Test
	void testGetAllFlavorProfilesByRegionPrimitiveShouldBeEmpty(){
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/iaas/api/flavor-profiles"));
		when(
			this.restTemplate.exchange(uri,HttpMethod.GET,
				RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class)
		).thenReturn(
			new ResponseEntity<>("{\"content\":[{\"mapping\":{},\"_links\":{}}]}", HttpStatus.OK)
		);

		Assertions.assertDoesNotThrow(()->{
			this.restClient.getAllFlavorProfilesByRegionPrimitive();
		});
	}

	@Test
	void testGetAllStorageProfilesByRegionPrimitiveShouldBeEmpty(){
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/iaas/api/storage-profiles"));
		when(
			this.restTemplate.exchange(uri,HttpMethod.GET,
				RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class)
		).thenReturn(
			new ResponseEntity<>("{\"content\":[{\"mapping\":{},\"_links\":{}}]}", HttpStatus.OK)
		);

		Assertions.assertDoesNotThrow(()->{
			this.restClient.getAllStorageProfilesByRegionPrimitive();
		});
	}

	@Test
	void testGetAllImageProfilesByRegionPrimitiveShouldBeEmpty(){
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/iaas/api/image-profiles"));
		when(
			this.restTemplate.exchange(uri,HttpMethod.GET,
				RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class)
		).thenReturn(
			new ResponseEntity<>("{\"content\":[{\"mapping\":{},\"_links\":{}}]}", HttpStatus.OK)
		);

		Assertions.assertDoesNotThrow(()->{
			this.restClient.getAllImageProfilesByRegionPrimitive();
		});
	}

	@Test
	void testGetAllFlavorMappingsByRegionPrimitiveShouldBeEmpty(){
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/iaas/api/flavors"));
		when(
			this.restTemplate.exchange(uri,HttpMethod.GET,
				RestClientVraNgPrimitiveTestDouble.getDefaultHttpEntity(),
				String.class)
		).thenReturn(
			new ResponseEntity<>("{\"content\":[{\"mapping\":{},\"_links\":{}}]}", HttpStatus.OK)
		);

		Assertions.assertDoesNotThrow(()->{
			this.restClient.getAllFlavorMappingsByRegionPrimitive();
		});
	}

	@Test
	void testCreatePropertyGroupPrimitive() throws IOException{
		PropertyGroupMockBuilder propertyGroupBuilder = new PropertyGroupMockBuilder();
		VraNgPropertyGroup pg = propertyGroupBuilder.setName( "memory" ).build();
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/properties/api/property-groups"));	
		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		restClient.createPropertyGroupPrimitive(pg);

		verify( restTemplate, times(1) ).exchange(
			argCaptorUri.capture(),
			argCaptorMethod.capture(),
			argCaptorEntity.capture(),
			any(Class.class)
		);

		assertEquals(uri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());
		assertEquals(pg.getRawData(), argCaptorEntity.getValue().getBody());
	}

	@Test
	void testUpdatePropertyGroupPrimitive() throws IOException{
		PropertyGroupMockBuilder propertyGroupBuilder = new PropertyGroupMockBuilder();
		VraNgPropertyGroup pg = propertyGroupBuilder.setName( "memory" ).build();
		URI uri = restClient.getURI(restClient.getURIBuilder().setPath("/properties/api/property-groups/" + pg.getId()));		
		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		restClient.updatePropertyGroupPrimitive(pg);

		verify( restTemplate, times(1) ).exchange(
			argCaptorUri.capture(),
			argCaptorMethod.capture(),
			argCaptorEntity.capture(),
			any(Class.class)
		);

		assertEquals(uri, argCaptorUri.getValue());
		assertEquals(HttpMethod.PUT, argCaptorMethod.getValue());
		assertEquals(pg.getRawData(), argCaptorEntity.getValue().getBody());
	}

	@Test
	void testImportCustomResourcePrimitive() throws URISyntaxException, IOException {
		// GIVEN
		CustomResourceMockBuilder customResourceMockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockCustomResource = customResourceMockBuilder.build();

		URI expectedUri = restClient.getURI(restClient.getURIBuilder().setPath("/form-service/api/custom/resource-types"));

		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		ResponseEntity response = Mockito.mock( ResponseEntity.class );
		when( response.getStatusCode() ).thenReturn( HttpStatus.valueOf( 200 ) );
		when( response.getBody() ).thenReturn( mockCustomResource.getJson() );

		when(
			restTemplate.exchange(
				argCaptorUri.capture(),
				argCaptorMethod.capture(),
				argCaptorEntity.capture(),
				any(Class.class)
			)
		).thenReturn( response );

		// WHEN
		restClient.importCustomResourcePrimitive( mockCustomResource.getJson() );

		// THEN
		assertEquals(expectedUri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());

		VraNgCustomResource expectedMockCustomResource = customResourceMockBuilder.setAdditionalActions( new JsonArray() ).build();

		// First time we expect the same CR without any `additionalActions`
		assertEquals(
			JsonParser.parseString( expectedMockCustomResource.getJson() ).getAsJsonObject(),
			JsonParser.parseString(argCaptorEntity.getAllValues().get(0).getBody().toString()).getAsJsonObject()
		);

		// Second time we expect the CR with the `additionalActions` back
		assertEquals(
			JsonParser.parseString( mockCustomResource.getJson() ).getAsJsonObject(),
			JsonParser.parseString(argCaptorEntity.getAllValues().get(1).getBody().toString()).getAsJsonObject()
		);
	}

	@Test
	void testImportCustomResourcePrimitiveWithNoAdditionalActions() throws URISyntaxException, IOException {
		// GIVEN
		CustomResourceMockBuilder customResourceMockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockCustomResource = customResourceMockBuilder.setAdditionalActions( new JsonArray() ).build();

		URI expectedUri = restClient.getURI(restClient.getURIBuilder().setPath("/form-service/api/custom/resource-types"));

		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		ResponseEntity response = Mockito.mock( ResponseEntity.class );
		when( response.getStatusCode() ).thenReturn( HttpStatus.valueOf( 200 ) );
		when( response.getBody() ).thenReturn( mockCustomResource.getJson() );

		when(
			restTemplate.exchange(
				argCaptorUri.capture(),
				argCaptorMethod.capture(),
				argCaptorEntity.capture(),
				any(Class.class)
			)
		).thenReturn( response );

		// WHEN
		restClient.importCustomResourcePrimitive( mockCustomResource.getJson() );

		// THEN
		verify(
			restTemplate,
			times( 1 )
		).exchange( any(), any(), any(), any(Class.class) );

		assertEquals(expectedUri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());

		VraNgCustomResource expectedMockCustomResource = customResourceMockBuilder.setAdditionalActions( new JsonArray() ).build();

		// First time we expect the same CR without any `additionalActions`
		assertEquals(
			JsonParser.parseString( expectedMockCustomResource.getJson() ).getAsJsonObject(),
			JsonParser.parseString(argCaptorEntity.getValue().getBody().toString()).getAsJsonObject()
		);
	}

	@Test
	void testImportCustomResourcePrimitiveThrowsIfCannotImportCustomResourceActions() throws URISyntaxException, IOException {
		// GIVEN
		CustomResourceMockBuilder customResourceMockBuilder = new CustomResourceMockBuilder();
		VraNgCustomResource mockCustomResource = customResourceMockBuilder.build();

		URI expectedUri = restClient.getURI(restClient.getURIBuilder().setPath("/form-service/api/custom/resource-types"));

		ArgumentCaptor<URI> argCaptorUri = ArgumentCaptor.forClass(URI.class);
		ArgumentCaptor<HttpMethod> argCaptorMethod = ArgumentCaptor.forClass(HttpMethod.class);
		ArgumentCaptor<HttpEntity> argCaptorEntity = ArgumentCaptor.forClass(HttpEntity.class);

		ResponseEntity response = Mockito.mock( ResponseEntity.class );
		when( response.getStatusCode() ).thenReturn( HttpStatus.valueOf( 200 ) ).thenReturn( HttpStatus.valueOf( 400 ) );
		when( response.getBody() ).thenReturn( mockCustomResource.getJson() );

		when(
			restTemplate.exchange(
				argCaptorUri.capture(),
				argCaptorMethod.capture(),
				argCaptorEntity.capture(),
				any(Class.class)
			)
		).thenReturn( response );

		// WHEN
		Exception exception = assertThrows(
			RuntimeException.class, () -> {
				restClient.importCustomResourcePrimitive( mockCustomResource.getJson() );
			}
		);

		// THEN
		assertTrue( exception.getMessage().contains( "Unable to import additionalActions for Avi Load Balancer L3DSR" ));

		assertEquals(expectedUri, argCaptorUri.getValue());
		assertEquals(HttpMethod.POST, argCaptorMethod.getValue());

		VraNgCustomResource expectedMockCustomResource = customResourceMockBuilder.setAdditionalActions( new JsonArray() ).build();

		// First time we expect the same CR without any `additionalActions`
		assertEquals(
			JsonParser.parseString( expectedMockCustomResource.getJson() ).getAsJsonObject(),
			JsonParser.parseString(argCaptorEntity.getAllValues().get(0).getBody().toString()).getAsJsonObject()
		);

		// Second time we expect the CR with the `additionalActions` back
		assertEquals(
			JsonParser.parseString( mockCustomResource.getJson() ).getAsJsonObject(),
			JsonParser.parseString(argCaptorEntity.getAllValues().get(1).getBody().toString()).getAsJsonObject()
		);
	}


	// =================================================
	// ABX ACTIONS
	// =================================================

	@ParameterizedTest
	@MethodSource("abxActionArgs")
	void testCreateAbxActionMap(Map<String, Object> expectedResult, AbxAction abxAction) throws IOException {
		
		Map<String, Object> actualResult = restClient.createAbxActionMap(abxAction);		
		assertEquals(expectedResult, actualResult);
	}

	private static Stream<Arguments> abxActionArgs() throws IOException  {
		
		List<Arguments> arguments = new ArrayList<Arguments>();

		Map<String, Object>  expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		AbxAction abxAction = AbxActionMockBuilder.buildAbxAction();

		// Providers, Timeout, MemoryLimit all are empty
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Memory Limit equal to zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.platform.memoryLimitMb = 0;
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Memory Limit Greater Than zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.platform.memoryLimitMb = AbxActionMockBuilder.MEMORY_LIMIT;
		expectedResult.put("memoryInMB", AbxActionMockBuilder.MEMORY_LIMIT);
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Timeout equal to zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.platform.timeoutSec = 0;
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Timeout greater than zero
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.platform.timeoutSec = AbxActionMockBuilder.TIMEOUT;
		expectedResult.put("timeoutSeconds", AbxActionMockBuilder.TIMEOUT);
		arguments.add(Arguments.of(expectedResult, abxAction));

		// Faas Provider Name Is Correct
		abxAction = AbxActionMockBuilder.buildAbxAction();
		expectedResult = AbxActionMockBuilder.buildAbxActionMap();
		abxAction.platform.provider = AbxActionMockBuilder.FAAS_PROVIDER;
		expectedResult.put("provider", AbxActionMockBuilder.FAAS_PROVIDER);
		arguments.add(Arguments.of(expectedResult, abxAction));
		
		return arguments.stream();
	}

	@Test
	void testCreateAbxActionMapThrownExceptionWhenFaasProviderNameIsNotCorrect() throws IOException {
		AbxAction abxAction = AbxActionMockBuilder.buildAbxAction();
		abxAction.platform.provider = "Non-existent provider";

		Exception exception = assertThrows(
			RuntimeException.class, () -> {
				restClient.createAbxActionMap(abxAction);				
			}
		);

		assertTrue(exception.getMessage().contains("Faas provider name is not correct"));
	}
}
