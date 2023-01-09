package com.vmware.pscoe.iac.artifact.rest.helpers;

import java.nio.charset.Charset;

import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

public class VcdApiHelper {
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public static MediaType buildMediaType(String contentType, String apiVersion) {
		if(apiVersion == null) {
			return MediaType.parseMediaType(contentType);
		} else {
			return MediaType.parseMediaType(contentType + ";version=" + apiVersion);
		}
	}

	public static String buildBearerToken(String token) {
		return "Bearer " + token;
	}

	public static String buildBasicAuth(String user, String domain, String password) {
		if (StringUtils.isEmpty(domain)) {
			throw new RuntimeException("Please provide a user with an organization in the form [user@org]");
		}

		String username = user + "@" + domain;
		String auth = Base64Utils.encodeToString((username + ":" + password).getBytes(UTF_8));
		return "Basic " + auth;
	}
}
