function handler($context, $inputs) {
    $inputsString = $inputs | ConvertTo-Json -Compress

    Write-Host "Inputs were $inputsString"

    $output=@{status = 'done'}

    return $output
}
