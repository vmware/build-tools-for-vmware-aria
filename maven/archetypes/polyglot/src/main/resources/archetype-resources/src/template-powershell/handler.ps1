function handler($context, $inputs) {
    $inputsString = $inputs | ConvertTo-Json -Compress

    Write-Host "Inputs were $inputsString"

    $output = @("result", "done")

    return $output
}
