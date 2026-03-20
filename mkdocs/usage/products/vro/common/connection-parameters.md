## Environment Connection Parameters

The following need to be added to the profile that you intend to use:

``` xml
<!-- (1)! -->
<profile>
    <!--    ..... OTHER DIRECTIVES .....  -->
    <vro.host>flt-auto01.corp.internal</vro.host>
    <vro.auth>vra|basic</vro.auth>
    <vro.authHost>flt-auto01.corp.internal</vro.authHost>
    <vro.authPort>443</vro.authPort>
    <vro.port>443</vro.port>
    <vro.username>configurationadmin</vro.username>
    <vro.password>someSecurePassword</vro.password>
</profile>
```

1.  {{ archetype.customer_project.maven_settings_location_hint}}

Configuration:

- `vro.username` - For {{ extra.products.vro_9_full_name }} (9.x) you need to provide username in the following format: user@domain.
    - admin@System - Provider admin.
    - configurationadmin@Classic - Classic organization admin.

- `vro.auth` - Defines the authentication type used for REST API communication.
      - Supported values: `vra`, `basic` (depending on {{ products.vro_short_name }} version might need to be explicitly enabled in the product).
      - If set to `vra`, `vro.authHost` and `vro.authPort` need to be provided with the hostname and port of the target {{ products.vra_9_short_name }} server.

Use the profile by passing it with `-P`, e.g.:
``` bash
mvn vrealize:push -P{{ archetype.customer_project.maven_profile_name}}
```
