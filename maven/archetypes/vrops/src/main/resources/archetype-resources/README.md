New Project of type VROPS
=========================

Congratulations, you have just created a new project of type VROPS.
This project will let you locally define/develop views and dashboards for [vRelize Operations](https://www.vmware.com/products/vrealize-operations.html) (vROps)
and then be able to push your work to a running vROps instance or pull changes that have been directly made on the vROps to your loca project. 


One of the main goals for this project type is to allow you to store your changes in a Control Version Repository and thus be able to 
benefit from all standard tools and processes for project delivery like: 

 - Version control        (be able to track versions, who did what change and when; be able to restore a specific version back in time)
 - Code review process    (Do not allow a change to get its place unless it is approved)
 - Teamwork               (Allow more than one person to work on the project and commit changes to it)
 - Continuous Integration (Automate regular deployments)

 Please note that the availability of thus project is just the enablement for all these processes. It does not enforce them. 
 In order to be able to setup a complete pipeline, you need to integrate with Git, Bamboo or Jenkins and others. 

 What to do next
 
 ===============

 Please review your `content.yaml` file and fill one or more of the following that will be managed by this project:
 * view:
 * dashboard:
 * alert-definition:
 * symptom-definition:
 * policy:
 * custom-group:
 * recommendation:
 * super-metric:
 * metric-config:
 * report:
 
Notes: 

During vROPs:push goal execution for the following of the assets there will be an automated check on the target system whether the dependent assets are present there (and such dependency is defined on the source system) in order the correct id-s to be set correctly. If the dependent assets are not present on the target system an error will be thrown:
 
 * alert-definition
 * symptom-definition
 * custom-group
 * policy

During vROPs:push goal the alert definition - symptom definition dependency check will be done automatically on the target system in order the corresponding id-s to be set correctly (if such dependency is defined on the source system). If the dependent assets are not present on the target system an error will be thrown.

Custom group - policy assignment will be done automatically on the target system depending on whether the custom group has a policy assignment on the source system.

If the following assets are existent on the target system they will be automatically updated:
 
 * view:
 * dashboard:
 * alert-definition:
 * symptom-definition:
 * policy:
 * custom-group:
 * recommendation:
 * super-metric:
 * metric-config:
 * report:
 
   
 Build
 
 ======

 ```
     mvn clean install 
 ```

 
