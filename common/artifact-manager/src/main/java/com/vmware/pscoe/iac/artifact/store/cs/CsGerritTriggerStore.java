package com.vmware.pscoe.iac.artifact.store.cs;

import java.io.File;
import java.nio.file.Paths;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CsGerritTriggerStore extends AbstractCsStore {
	private static final String DIR_TRIGGERS = "gerrit-triggers";
	private final Logger logger = LoggerFactory.getLogger(CsGitWebhookStore.class);
	private List<JsonObject> items;

	/**
	* Exporting the contents of all blueprints listed in the content.yaml file, available for the configured project
	*/
	public void exportContent() {
		List<String> hookNames = this.descriptor.getGerritTrigger();
		if (hookNames == null) {
			logger.info("No triggers found in content.yaml");
			return;
		}
		getAllItems().stream()
				.filter(el -> hookNames.contains(el.get("name").getAsString()))
				.forEach(this::exportTrigger);
	}

	/**
	 * Importing content into vRA target environment
	 * @param File sourceDirectory
	 */
	public void importContent(File sourceDirectory) {
		File triggersFolder = Paths.get(sourceDirectory.getPath(), DIR_TRIGGERS).toFile();
		if (!triggersFolder.exists()) {
			return;
		}
		Collection<File> triggerFiles = FileUtils.listFiles(triggersFolder, new String[] {"yaml"}, false);
		if (triggerFiles == null || triggerFiles.isEmpty()) {
			return;
		}
		triggerFiles.stream().forEach(this::importTrigger);
	}


	private void exportTrigger(JsonObject obj) {
		String hookName = obj.get("name").getAsString();
		logger.info("Exporting Gerrit Listeners : {}", hookName);
		CsStoreHelper.sanitizeDefaultProperties(obj);
		String jsonString = obj.toString();
		CsStoreHelper.storeToYamlFile(csPackage.getFilesystemPath(), DIR_TRIGGERS, hookName, jsonString);
		CsStoreHelper.addVarsToExtractionContext(jsonString, this.descriptor);
	}


	private void importTrigger(File triggerFile) {
		String jsonString = CsStoreHelper.loadFromYamlFile(triggerFile);
		JsonObject obj = JsonParser.parseString(jsonString).getAsJsonObject();
		String name = obj.get("name").getAsString();
		obj.addProperty("project", restClient.getProjectName());
		Optional<JsonObject> optional = CsStoreHelper.findObjectByName(getAllItems(), name);
		if (optional.isPresent()) {
			// obj.addProperty("id", optional.get().getAsJsonObject().get("id").getAsString());
			restClient.updateGerritTrigger(name, obj);
		} else {
			restClient.createGerritTrigger(obj);

		}
	}


	List<JsonObject> getAllItems() {
		if (items == null) {
			this.items = this.restClient.getProjectGerritTriggers();
		}
		return this.items;

	}
}
