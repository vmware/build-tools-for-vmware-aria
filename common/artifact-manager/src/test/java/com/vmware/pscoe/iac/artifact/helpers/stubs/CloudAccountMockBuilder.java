package com.vmware.pscoe.iac.artifact.helpers.stubs;

import java.util.ArrayList;
import java.util.List;

import com.vmware.pscoe.iac.artifact.model.vrang.VraNgCloudAccount;

public class CloudAccountMockBuilder {
/*{
  "numberOfElements": 1,
  "content": [
    {
      "owner": "csp@vmware.com",
      "cloudAccountProperties": "{ \"hostName\": \"vcenter.mycompany.com\" }",
      "enabledRegionIds": "[ \"us-east-1\", \"ap-northeast-1\" ]",
      "_links": {
        "additionalProp1": {
          "hrefs": [
            "string"
          ],
          "href": "string"
        },
        "additionalProp2": {
          "hrefs": [
            "string"
          ],
          "href": "string"
        },
        "additionalProp3": {
          "hrefs": [
            "string"
          ],
          "href": "string"
        }
      },
      "cloudAccountType": "vsphere, aws, azure, nsxv, nsxt",
      "description": "my-description",
      "orgId": "9e49",
      "tags": "[ { \"key\" : \"env\", \"value\": \"dev\" } ]",
      "organizationId": "deprecated",
      "createdAt": "2012-09-27",
      "customProperties": "{ \"isExternal\" : \"false\" }",
      "name": "my-name",
      "id": "9e49",
      "updatedAt": "2012-09-27"
    }
  ],
  "totalElements": 1
}*/

	private String id = "mockedCloudAccountId";
	private String name = "mockedCloudAccount";
	private String type = "vsphere, aws, azure, nsxv, nsxt";
	private List<String> regionIds;
  private List<String> tags;

	public CloudAccountMockBuilder() {
		this.regionIds = new ArrayList<String>();
		this.tags = new ArrayList<String>();
	}

	public CloudAccountMockBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public CloudAccountMockBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public CloudAccountMockBuilder setType(String type) {
		this.type = type;
		return this;
	}

	public CloudAccountMockBuilder setRegionIds(List<String> regionIds) {
		this.regionIds = regionIds;
		return this;
	}

	public CloudAccountMockBuilder setTags(List<String> tags) {
		this.tags = tags;
		return this;
	}

	public VraNgCloudAccount build(){
		return new VraNgCloudAccount(this.id, this.name, this.type, this.regionIds, this.tags);
	}
}
