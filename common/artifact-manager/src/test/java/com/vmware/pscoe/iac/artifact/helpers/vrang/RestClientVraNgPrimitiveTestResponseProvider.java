package com.vmware.pscoe.iac.artifact.helpers.vrang;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestClientVraNgPrimitiveTestResponseProvider {


	/**
	 * Generates a page of vRA projects
	 * Refer to "/iaas/api/projects"
	 * @param totalElements sum of all elements
	 * @param pageSize maximum elements on a page
	 * @param page page number starting from 0
	 * @return ResponseEntity containing a page with the requested number of projects
	 */
	public static ResponseEntity<String> getPaginatedProjectResponse(int totalElements, int pageSize, int page) {

		StringBuilder builder = new StringBuilder();
		builder.append("{\"content\": [");

		// generate elements for the current page
		int skip = page * pageSize;
		int max = Math.min(skip + pageSize, totalElements);
		for (int i = skip; i < max; i++) {
			builder.append(
				String.format("{\"name\": \"project\", \"id\": \"%d\"}%s", i, (i == max - 1 ? "" : ",")));
		}

		int numberOfElements = Math.min((totalElements - (page * pageSize)), pageSize);

		builder.append("],");
		builder.append(String.format("\"totalElements\": %d,", totalElements));
		builder.append(String.format("\"numberOfElements\": %d", numberOfElements));
		builder.append("}");

		return new ResponseEntity<>(builder.toString(), HttpStatus.OK);
	}
}
