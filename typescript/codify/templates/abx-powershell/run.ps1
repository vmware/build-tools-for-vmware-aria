Param(
    [Parameter(Mandatory = $True, Position = 0)]
    [string]$entrypoint,
    [Parameter(Mandatory = $True, Position = 1)]
    [string]$inputs
)

# ======================================
# ABX Context
# ======================================

class AbxContext {
    [string] $runId

    AbxContext([string] $runId) {
        $this.runId = $runId
    }
}

function buildLocalRequest {
    Param(
        [string]$targetLink
    )

    # TODO: take this from .env
    $uriHead = 'https://vra-host'
    # TODO: authenticate

    return ( -join ($uriHead, $targetLink))
}

function request {
    Param(
        [string]$runId,
        [string]$link,
        [string]$methodType,
        [string]$body,
        [hashtable]$passedHeaders,
        [boolean] $privileged
    )

    $resp = ''
    $headers = @{
        "Accept" = "application/json"
    }

    if ($privileged) {
        $headers['x-abx-privileged'] = 'true'
    }

    # Apply additional headers
    if ($null -ne $passedHeaders) {
        ForEach ($header in $passedHeaders.Keys) {
            $headers[$header] = $( $passedHeaders["$header"] )
        }
    }

    Try {
        If (($Null -ne $link) -and $link.startsWith("/")) {
            # Handle internal calls
            $targetUri = buildLocalRequest $link
            $resp = (Invoke-WebRequest -Uri $targetUri -SkipCertificateCheck -Headers $headers -Method $methodType -ContentType "application/json" -Body $body)
        }
        Else {
            # Handle external calls
            $resp = (Invoke-WebRequest -Uri $link  -Headers $headers -Method $methodType -Body $body)
        }

        $response = @{
            Headers    = $resp.Headers
            Content    = $resp.Content
            StatusCode = $resp.StatusCode
        }
        return $response
    }
    Catch [System.Exception] {
        $response = @{
            Headers    = $_.Exception.Response.Headers
            StatusCode = $_.Exception.Response.StatusCode
            Content    = $_.ErrorDetails.Message
        }

        return $response
    }
}

function getSecret {
    Param(
        [string]$runId,
        [hashtable]$abxInputs,
        [string]$value
    )
    return $value
}

function buildContext {
    Param(
        [string]$runId,
        [hashtable]$abxInputs
    )

    $ctx = [AbxContext]::new($runId)

    $initRequest = {
        Param(
            [string]$link,
            [string]$methodType,
            [string]$body,
            [hashtable]$passedHeaders,
            [boolean] $privileged
        )

        return request $runId $link $methodType $body $passedHeaders $privileged
    }

    $initGetSecret = {
        Param(
            [string]$value
        )

        return getSecret $runId $abxInputs $value
    }

    $ctx | Add-Member -MemberType ScriptMethod -Name "request" -Value $initRequest
    $ctx | Add-Member -MemberType ScriptMethod -Name "getSecret" -Value $initGetSecret

    return $ctx
}


# ======================================
# Runner
# ======================================

function getAndRemoveKey {
    param (
        [Parameter(Mandatory = $True, Position = 0)]
        [string]$key,
        [Parameter(Mandatory = $True, Position = 1)]
        [hashtable]$hashtable
    )

    $value = $Null
    If ($hashtable.ContainsKey($key)) {
        $value = $hashtable[$key]
        $hashtable.Remove($key)
    }
    return $value
}

function flushOutStreams {
    [Console]::Out.Flush()
    [Console]::Error.Flush()
}

function writeOutput {
    param (
        [Parameter(Mandatory = $True, Position = 1)]
        [PSObject]$object
    )
    $jsonString = toJson $object
    Write-Host "Result:" $jsonString
}

function handleError {
    param (
        [Parameter(Mandatory = $True, Position = 0)]
        [string]$errMsg
    )

    $result = New-Object system.collections.hashtable;
    $result.Add('error', $errMsg)
    Write-Host $errMsg
    flushOutStreams
}

function toJson {
    param (
        [Parameter(Mandatory = $True, Position = 1)]
        [PSObject]$object
    )
    ConvertTo-Json -depth 100 $object
}

# ====================================
# Script starts here
# ====================================

# Load inputs from JSON file
If (Test-Path $inputs) {
    $inputsContent = Get-Content $inputs | Out-String
    $abxInputs = $inputsContent | ConvertFrom-Json -AsHashtable
}
Else {
    handleError "Inputs file does not exist:" $inputs
    Exit 1
}

$env:PSModulePath = $env:PSModulePath + "$( [System.IO.Path]::PathSeparator )$PSScriptRoot"

$WarningPreference = "SilentlyContinue"
$ProgressPreference = "SilentlyContinue"

$PSDefaultParameterValues['Out-File:Encoding'] = 'utf8'
$PSDefaultParameterValues['*:Encoding'] = 'utf8'
$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::UTF8

$moduleFile = $Null
$scriptFile = $Null
$module = $Null
$functionName = $Null
$moduleName = $Null

# Get the entrypoint
If ($Null -ne $entrypoint) {
    $parts = $entrypoint.Split('.')
    If ($parts.Length -eq 2) {
        $moduleName = $parts[0]
        $functionName = $parts[1]
        $moduleFile = $moduleName + ".psm1"
        $scriptFile = $moduleName + ".ps1"
    }
    ElseIf ($parts.Length -eq 1) {
        $moduleName = "handler"
        $functionName = $parts[0]
        $scriptFile = $moduleName + ".ps1"
    }
    Else {
        handleError "Invalid entrypoint"
        Exit 1
    }
}
Else {
    handleError "Entrypoint is not specified"
    Exit 1
}

# Import client module/script
Try {
    $functionFolder = "src"
    Set-Location -Path $functionFolder

    # Checks if paths exist
    If (Test-Path $moduleName) {
        If (Test-Path $moduleName/$moduleFile -PathType Leaf) {
            $module = Import-Module $PSScriptRoot/$functionFolder/$moduleName/$moduleFile -PassThru -ErrorAction Stop
        }
        ElseIf (Test-Path $moduleName/$scriptFile -PathType Leaf) {
            $module = Import-Module $PSScriptRoot/$functionFolder/$moduleName/$scriptFile -PassThru -ErrorAction Stop
        }
        Else {
            Throw "Invalid main function (entrypoint) provided: " + ($moduleName + "." + $functionName)
        }
    }
    Else {
        If (Test-Path $moduleFile -PathType Leaf) {
            $module = Import-Module $PSScriptRoot/$functionFolder/$moduleFile -PassThru -ErrorAction Stop
        }
        ElseIf (Test-Path $scriptFile -PathType Leaf) {
            $module = Import-Module $PSScriptRoot/$functionFolder/$scriptFile -PassThru -ErrorAction Stop
        }
        Else {
            Throw "Invalid main function (entrypoint) provided: " + $functionName
        }
    }
}
Catch {
    handleError $_.Exception.Message
    Exit 1
}

# Verify the imported client command was successfully imported
Try {
    [void](Get-Command $functionName -ErrorAction Stop)
}
Catch {
    handleError "Function with name $($functionName) does not exist"
    Exit 1
}

function runHandler($abxInputs, $function) {
    [CmdletBinding()]

    # Handle preferenced streams
    $WarningPreference = "Continue"
    $DebugPreference = 'Continue'
    $InformationPreference = 'Continue'
    $VerbosePreference = 'Continue'
    # surpress progress stream
    $ProgressPreference = "SilentlyContinue"

    Try {
        $runId = [guid]::NewGuid().ToString()
        $ctx = buildContext $runId $abxInputs

        Write-Host "==================================="
        Write-Host "ACTION START"
        Write-Host "==================================="

        $startDate=(GET-DATE)
        $result = & $function $ctx $abxInputs
        $endDate=(GET-DATE)
        $duration = NEW-TIMESPAN -Start $startDate -End $endDate

        Write-Host "==================================="
        Write-Host "Action completed in" $duration.TotalSeconds "seconds"

        # Prepare result
        $res = New-Object system.collections.hashtable;

        If ($result -is [array] -And $result.length -gt 0) {
            $res = $result[$result.length - 1]
        }
        ElseIf ($result -is [array]) {
            $res = $result[0]
        }
        Else {
            $res = $result
        }

        If ($Null -eq $result) {
            $res = "{}"
        }

        writeOutput $res

        Write-Host "==================================="
    }
    Catch {
        handleError $_.Exception.Message
        Throw $_.Exception
    }
    Finally {
        Write-Host "Finished running action code."
        flushOutStreams
        Write-Host "Exiting powershell process."
    }
}

runHandler $abxInputs $functionName
