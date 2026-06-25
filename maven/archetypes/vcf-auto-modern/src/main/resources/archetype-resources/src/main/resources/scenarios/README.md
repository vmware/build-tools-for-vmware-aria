# Scenarios

This directory contains scenario definitions for VCF Automation.

Scenarios are used for testing and validating Automation configurations.
They define test cases that can be executed against the VCF Automation platform.

## File Naming

Each scenario should be stored in its seperate subfolder named the way the scenario is named.Inside a JSON file named details.json contains the scenario details and a template.html contains the email's HTML that is going to be sent:
- Alert Raised (Notification sent to Organization Administrator)
    - details.json
    - template.html

## Example Structure

```
scenarios/
├── Alert Raised (Notification sent to Organization Administrator)
├────── details.json 
├────── template.html
```

## References

- [VCF Automation Documentation](https://techdocs.broadcom.com/us/en/vmware-cis/vcf.html)
