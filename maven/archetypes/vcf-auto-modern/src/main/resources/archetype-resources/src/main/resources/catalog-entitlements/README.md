# Catalog Entitlements

This directory contains catalog entitlement definitions for VCF Automation.

Catalog entitlements define which catalog items and content sources are visible
to specific projects or users within the organization.

## File Naming

Each entitlement should be in its own folder named after the entitlement:
- `<entitlement-name>/content.yaml` - Entitlement metadata
- `<entitlement-name>/details.json` - Entitlement details

## Example Structure

```
catalog-entitlements/
├── Content Source Entitlement/
│   ├── content.yaml
│   └── details.json
└── Project A Entitlement/
    ├── content.yaml
    └── details.json
```

## References

- [VCF Automation Documentation](https://techdocs.broadcom.com/us/en/vmware-cis/vcf.html)
