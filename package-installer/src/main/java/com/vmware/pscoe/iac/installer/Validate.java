/*
 * #%L
 * installer
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
package com.vmware.pscoe.iac.installer;

import org.beryx.textio.TextIO;
import org.json.simple.JSONValue;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class Validate {

	private Validate() {
	}

	public static boolean host(String host, TextIO input) {
		try {
			InetAddress inet = InetAddress.getByName(host);
			input.getTextTerminal()
					.println("  Using: " + inet.getHostName() + " with address: " + inet.getHostAddress());
			return true;
		} catch (UnknownHostException unknown) {
			input.getTextTerminal()
					.println("  WARNING: Unknown host \"" + host + "\" " + unknown.getLocalizedMessage());
			return false;
		}
	}

	public static boolean timeout(String timeout, TextIO input) {
		try {
			Integer.parseInt(timeout);
			return true;
		} catch (NumberFormatException nfe) {
			input.getTextTerminal()
					.println("  WARNING: Unknown value for timeout \"" + timeout + "\" " + nfe.getLocalizedMessage());
			return false;
		}
	}

	public static boolean port(int port, TextIO input) {
		if (port < 0 || port > 0x00FFFF) {
			input.getTextTerminal()
					.println("  WARNING: Port " + port + " is outside allowed range: 0 - " + (int) 0x00FFFF);
			return false;
		}
		return true;
	}

	public static boolean hostAndPort(String host, int port, TextIO input) {
		try (Socket socket = new Socket(host, port)) {
			return true;
		} catch (IOException e) {
			input.getTextTerminal().println(
					"  WARNING: Cannot open connection to " + host + ":" + port + " : " + e.getLocalizedMessage());
			return false;
		}
	}

	public static String vrang(String csp, int port, String user, String pass, TextIO input) {
		String urlString = "https://" + csp + ":" + port + "/csp/gateway/am/api/login?access_token";
		try {
			URL url = new URL(urlString);
			HttpsURLConnection https = (HttpsURLConnection) (url.openConnection());
			disableSecurity(https);
			https.setConnectTimeout(5000); // millis
			https.setReadTimeout(5000); // millis
			https.setDoOutput(true);
			https.setDoInput(true);
			https.setRequestMethod("POST");
			https.setRequestProperty("Content-Type", "application/json; utf-8");
			String body = "{ \"username\": \"" + user + "\", \"password\": \"" + pass + "\" }";
			OutputStream out = https.getOutputStream();
			out.write(body.getBytes(StandardCharsets.UTF_8));
			out.flush();
			out.close();

			int code = https.getResponseCode();
			String message = https.getResponseMessage();
			if (code != HttpURLConnection.HTTP_OK) {
				if (input != null) {
					input.getTextTerminal().println("  WARNING: Cannot successfully login with \"" + user
							+ "\" against \"" + url.toString() + "\". " + code + " " + message);
				}
				return null;
			}
			Map<?, ?> response = jsonParse(readFully(https.getInputStream()));
			String refreshToken = "" + response.get("refresh_token");
			return refreshToken;
		} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
			if (input != null) {
				input.getTextTerminal()
						.println("  WARNING: Cannot successfully login with \"" + user + "\" against \"" + urlString
								+ "\" : " + e.getClass().getName()
								+ " : " + e.getLocalizedMessage());
			}
			return null;
		}
	}

	public static String token(String csp, int port, String refresh, TextIO input) {
		String urlString = "https://" + csp + ":" + port + "/iaas/api/login";
		try {
			URL url = new URL(urlString);
			HttpsURLConnection https = (HttpsURLConnection) (url.openConnection());
			disableSecurity(https);
			https.setConnectTimeout(5000); // millis
			https.setReadTimeout(5000); // millis
			https.setDoOutput(true);
			https.setDoInput(true);
			https.setRequestMethod("POST");
			https.setRequestProperty("Content-Type", "application/json; utf-8");
			String body = "{\"refreshToken\": \"" + refresh.trim() + "\"}";
			OutputStream out = https.getOutputStream();
			out.write(body.getBytes(StandardCharsets.UTF_8));
			out.flush();
			out.close();

			int code = https.getResponseCode();
			String message = https.getResponseMessage();
			if (code != HttpURLConnection.HTTP_OK) {
				if (input != null) {
					input.getTextTerminal().println("  WARNING: Cannot successfully authenticate with token against \""
							+ url.toString() + "\". " + code + " " + message);
				}
				return null;
			}
			Map<?, ?> response = jsonParse(readFully(https.getInputStream()));
			String accessToken = "" + response.get("token");
			return accessToken;
		} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
			if (input != null) {
				input.getTextTerminal()
						.println("  WARNING: Cannot successfully authenticate with token against \"" + urlString
								+ "\" : " + e.getClass().getName()
								+ " : " + e.getLocalizedMessage());
			}
			return null;
		}
	}

	public static boolean vroauth(String host, int port, String user, String pass, TextIO input) {
		String baseString = "https://" + host + ":" + port;
		String urlString = baseString + "/vco/api/users/";
		try {
			URL url = new URL(urlString);
			HttpsURLConnection https = (HttpsURLConnection) (url.openConnection());
			disableSecurity(https);
			https.setConnectTimeout(5000); // millis
			https.setReadTimeout(5000); // millis
			https.setDoOutput(false);
			https.setDoInput(true);
			https.setRequestMethod("GET");
			https.setRequestProperty("Content-Type", "application/json; utf-8");
			int index = user == null ? -1 : user.indexOf("@");
			if (index >= 0) {
				user = user.substring(0, index);
			}
			String credentials = user + ":" + pass;
			https.setRequestProperty("Authorization",
					"Basic " + new String(Base64.getEncoder().encode(credentials.getBytes(StandardCharsets.UTF_8))));

			int code = https.getResponseCode();
			String message = https.getResponseMessage();
			if (code != HttpURLConnection.HTTP_OK) {
				if (input != null) {
					input.getTextTerminal().println("  WARNING: Cannot successfully login with \"" + user
							+ "\" against \"" + url.toString() + "\". " + code + " " + message);
				}
				return false;
			}
			input.getTextTerminal()
					.println("  Using: Basic Authentication with user \"" + user + "\" agains \"" + baseString + "\".");
			return true;
		} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
			if (input != null) {
				input.getTextTerminal()
						.println("  WARNING: Cannot successfully login with \"" + user + "\" against \"" + urlString
								+ "\" : " + e.getClass().getName()
								+ " : " + e.getLocalizedMessage());
			}
			return false;
		}
	}

	static class ProjectAndOrg {
		public String projectId = null;
		public String org = null;
		public String orgId = null;

		ProjectAndOrg() {
		}

		ProjectAndOrg(String projectId, String org, String orgId) {
			this.projectId = projectId;
			this.org = org;
			this.orgId = orgId;
		}
	}

	public static ProjectAndOrg project(Properties props, String project, TextIO input) {
		String host = null;
		int port = -1;
		String csp = null;
		String refresh = null;
		String user = null;
		String pass = null;
		String access = null;
		try {
			host = props.getProperty("vrang_host");
			port = Integer.parseInt(props.getProperty("vrang_port"));
			csp = props.getProperty("vrang_csp_host");
			refresh = props.getProperty("vrang_refresh_token");
			user = props.getProperty("vrang_username");
			pass = props.getProperty("vrang_password");
			csp = csp == null || csp.trim().length() <= 0 ? host : csp;
			if (refresh == null) {
				refresh = "" + vrang(csp, port, user, pass, null);
			}
			access = token(csp, port, refresh, null);
		} catch (Throwable t) {
			return new ProjectAndOrg();
		}

		String urlString = "https://" + host + ":" + port + "/iaas/api/projects";
		try {
			URL url = new URL(urlString);
			HttpsURLConnection https = (HttpsURLConnection) (url.openConnection());
			disableSecurity(https);
			https.setConnectTimeout(5000); // millis
			https.setReadTimeout(5000); // millis
			https.setDoOutput(false);
			https.setDoInput(true);
			https.setRequestMethod("GET");
			https.setRequestProperty("Accept", "application/json");
			https.setRequestProperty("Authorization", "Bearer " + access);

			int code = https.getResponseCode();
			String message = https.getResponseMessage();
			if (code != HttpURLConnection.HTTP_OK) {
				return new ProjectAndOrg();
			}
			Map<?, ?> response = jsonParse(readFully(https.getInputStream()));
			List<Map<?, ?>> content = (List<Map<?, ?>>) response.get("content");
			String projectList = "";
			for (Map<?, ?> prj : content) {
				String name = "" + prj.get("name");
				projectList += name + "\t";
				if (name != null && name.equals(project.trim())) {
					String id = "" + prj.get("id");
					String orgId = "" + prj.get("orgId");
					input.getTextTerminal()
							.println("  Using project \"" + name + "\" (" + id + ") Organization Id: " + orgId);
					ProjectAndOrg result = getOrgFromId(props, access, orgId);
					result.projectId = id;
					return result;
				}
			}
			input.getTextTerminal().println(
					"  WARNING: There is no project with the name \"" + project + "\". Possible value: " + projectList);
			return new ProjectAndOrg();
		} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
			input.getTextTerminal()
					.println("  WARNING: Cannot successfully authenticate with token against \"" + urlString + "\" : "
							+ e.getClass().getName()
							+ " : " + e.getLocalizedMessage());
			return new ProjectAndOrg();
		}
	}

	public static ProjectAndOrg getOrgFromId(Properties props, String access, String orgId) {
		String host = null;
		int port = -1;
		String csp = null;
		try {
			host = props.getProperty("vrang_host");
			port = Integer.parseInt(props.getProperty("vrang_port"));
			csp = props.getProperty("vrang_csp_host");
		} catch (Throwable t) {
			return new ProjectAndOrg(null, null, orgId);
		}

		String urlString = "https://" + host + ":" + port + "/csp/gateway/am/api/orgs/" + orgId;
		try {
			URL url = new URL(urlString);
			HttpsURLConnection https = (HttpsURLConnection) (url.openConnection());
			disableSecurity(https);
			https.setConnectTimeout(5000); // millis
			https.setReadTimeout(5000); // millis
			https.setDoOutput(false);
			https.setDoInput(true);
			https.setRequestMethod("GET");
			https.setRequestProperty("Accept", "application/json");
			https.setRequestProperty("Authorization", "Bearer " + access);

			int code = https.getResponseCode();
			String message = https.getResponseMessage();
			if (code != HttpURLConnection.HTTP_OK) {
				return new ProjectAndOrg(null, null, orgId);
			}
			Map<?, ?> response = (Map<?, ?>) jsonParse(readFully(https.getInputStream()));
			String name = "" + response.get("name");
			return new ProjectAndOrg(null, name, orgId);
		} catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
			return new ProjectAndOrg(null, null, orgId);
		}
	}

	private static void disableSecurity(HttpsURLConnection https)
			throws NoSuchAlgorithmException, KeyManagementException {
		https.setHostnameVerifier(new HostnameVerifier() {
			public boolean verify(String host, SSLSession session) {
				return true;
			}
		});
		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		SSLContext tls = SSLContext.getInstance("TLS");
		tls.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = tls.getSocketFactory();
		https.setSSLSocketFactory(factory);
	}

	private static Map jsonParse(String str) {
		return (Map) JSONValue.parse(str);
	}

	private static String readFully(InputStream stream) throws IOException {
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
		int charsRead;
		while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
			out.append(buffer, 0, charsRead);
		}
		return out.toString();
	}

}
