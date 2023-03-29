# Set-up continuous deployment project in Bamboo


## Table of Contents


### Requirements
- Make sure that the artefacts in the Build plan are shared:
- Java JDK 8+ installed on the hatchery vm



## Environment setup
1. Open already created Bamboo project.
2. Click Create → Create deployment project.
2. Create a new local repository (e.g. **vro-local**) and add it to the virtual release repository (e.g. **libs-release**).
3. Add Name and select Build Plan with the shared artefacts (project build plan, will automatically appear in the drop-down form).
4. Click Add Environment.
5. Add the Environment name (leave the agent selection) and click Create. 
6. Click on Set up tasks:
7. Add an Artefact download task and select the artefact you want to run with the installer (vRO package OR vRA package). Click Save.  
8. Add SCP Task and provide the IP address of the hatchery  ( Host field ).  Provide login credentials (Username and Password ). Select again the artefact which will be copied and installed. Add Remote Path. For example /tmp/vra.zip
9. Add SSH Task. The task will connect to the hatchery and install the artefact. 
The SSH command/script ( environment.properties section have to be modified accordingly to the specific project) 


```
echo "On the hatchery server"

if [ ! -f /tmp/vra.zip ] ; then
echo "vRA deployment package bundle cannot be found as /tmp/vra.zip"
exit 1;
fi
rm -rf /tmp/vra-deployment
mkdir -p /tmp/vra-deployment
mv /tmp/vra.zip /tmp/vra-deployment
cd /tmp/vra-deployment
unzip vra.zip
if which java ; then
echo "Java is already available. No need to install it"
else
echo "No Java found. Trying to install it."
apt update
apt install default-jre -y
fi
java -version
mkdir -p /tmp/vra-deployment/bin

cd /tmp/vra-deployment/bin
cat << EOT > /tmp/vra-deployment/bin/environment.properties
http_connection_timeout=360
ignore_ssl_host_verification=false
vrang_port=443
vrang_org_id=9decff24-72f7-4b75-bc6d-804826c3d641
vrang_vro_integration_name=embedded-VRO
vrang_import_overwrite_mode=SKIP,OVERWRITE
http_socket_timeout=360
vrang_auth_with_refresh_token=false
vro_run_workflow=false
skip_vro_import_old_versions=true
vrang_username=configurationadmin
vro_delete_old_versions=false
vro_delete_include_dependencies=false
vra_ng_import_packages=true
vro_embedded=true
vrang_project.id=5a92f54e-0787-4034-9432-6b96d5477496
vrang_host=vra-l-01a.corp.local
vro_import_configuration_attribute_values=false
vrang_csp_host=vra-l-01a.corp.local
vro_import_packages=true
vro_enable_backup=true
ignore_ssl_certificate_verification=true
vrang_org_name=vidm-l-01a
vro_import_old_versions=false
vrang_project_name=Etisalat Private Cloud v3
vro_import_configuration_secure_attribute_values=false
vrang_password={PASS}Vk13YXJlMSE\=
EOT

echo "Using the following properties:"
cat /tmp/vra-deployment/bin/environment.properties

if [ ! -f /tmp/vra-deployment/bin/installer ] ; then
echo "/bin/install script does not exist in vra.zip deployment package."
exit 2
fi
chmod a+x /tmp/vra-deployment/bin/installer
cd /tmp/vra-deployment/bin/
./installer environment.properties

exitcode=$?

echo "Deployment finished with exit code $exitcode"

cd /tmp
if [ $exitcode -eq 0 ] ; then
rm -rf /tmp/vra-deployment
rm -rf /tmp/vra.zip
else
echo "Deployment finished with errors. Please review package content in folder /tmp/vra-deployment on the hatchery server"
fi

exit $exitcode
```
10. Optional: Trigger deployment after successful build:
Click Add trigger → Select trigger: After successful build plan.
On the next screen provide Trigger description, select the branch and click Save trigger.
