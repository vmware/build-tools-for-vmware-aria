package com.vmware.pscoe.iac.artifact.helpers.vro;
import com.vmware.pscoe.iac.artifact.rest.RestClientVro;
import com.vmware.pscoe.iac.artifact.configuration.ConfigurationVro;

import org.springframework.web.client.RestTemplate;

import java.util.Properties;

public class RestClientVroTestDouble extends RestClientVro {
	public RestClientVroTestDouble(ConfigurationVro configuration, RestTemplate restTemplate) {
		super(configuration, restTemplate);
	}

    @Override
	public Properties getInputParametersTypes(String workflowId){
		return super.getInputParametersTypes(workflowId);
	}

    @Override
    public String buildParametersJson(Properties params, Properties inputParametersTypes) {
		return super.buildParametersJson(params, inputParametersTypes);
	}
}
