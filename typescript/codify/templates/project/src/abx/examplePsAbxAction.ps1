

<#
/**
Example PowerShell-based ABX action
@abx_type         abx
@abx_name         Example PS ABX Action
@abx_project      ref:name:Development
@abx_entrypoint   handler
@abx_input        {string} username username
@abx_input        {string} password password
@abx_input        {string} customFlag
*/
#>

function Handler($context, $inputs) {

    Write-Host $inputs

    [String[]]$output = "foo", "bar"

    return $output
}
