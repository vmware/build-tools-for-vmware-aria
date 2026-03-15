```bash
# Example properties file
ignore_ssl_certificate_verification=true
ignore_ssl_host_verification=true

vro_import_packages=true
vro_enable_backup=true
vro_server=vra-l-01a.corp.local
vro_port=443
vro_auth=basic
vro_tenant=vsphere.local
vro_username=administrator@vsphere.local
vro_password={ENCODED_PASSWORD_PLACEHOLDER}

vro_import_old_versions=true
vro_import_configuration_attribute_values=false
vro_import_configuration_secure_attribute_values=false
vro_delete_old_versions=true

# Skip workflow execution in shared environments
vro_run_workflow=false
```
