

<#
/**
Example PowerShell-based Polyglot action
@vro_type         polyglot
@vro_entrypoint   handler.handler
@vro_version      1.0.0
@vro_input        {string} username Service user's username
@vro_input        {SecureString} password Service user's password
@vro_input        {boolean} customFlag
@vro_output       {Array/string} operation results
*/
#>

function Handler($context, $inputs) {

    Write-Host $inputs

    [String[]]$output = "foo", "bar"

    return $output
}
