$ErrorActionPreference= 'silentlycontinue'

[regex]$regex = "\d+\.\d+\.*\d*"
$yes = "`u{2714} "
$no  = "`u{274c}"

$allChecks = $true

Write-Host "Starting Build Tools for VMware Aria Checks..."

# version should be at lest <major.minor>
# OpenSSL is not following the guidelines
@(
	@{ name = "Java";    cmd="java --version";   min="17.0"; max="24.0" },
	@{ name = "Maven";   cmd="mvn --version";    min="3.2";  max="4.0"  },
	@{ name = "Node.js"; cmd="node --version";   min="12.0"; max="24.0" },
	@{ name = "Python";  cmd="python --version"; min="3.2";  max="3.14" },
	@{ name = "Pip";     cmd="pip --version";    min="25.0"; max="26.0" },
	@{ name = "OpenSSL"; cmd="openssl version";  min="10.0"; max="17.0" }
) | ForEach-Object {
	$name = $_.name
	$command = $_.cmd
	$min = [Version]::Parse($_.min)
	$max = [Version]::Parse($_.max)

	$result = (Invoke-Expression $command)
	if ($result -eq $null)
	{
		Write-Host "$no $name is not installed" -ForegroundColor Red
		$allChecks = $false
		return 
	}

	$versionString = $result.Split([Environment]::NewLine)[0]
	$version = [Version]::Parse($regex.Matches($versionString)[0].Value)

	if ($version.CompareTo($min) -ge 0 -and $version.CompareTo($max) -le 0)
	{
		Write-Host "$yes $name version '$version' is within the required range ($min - $max)." -ForegroundColor Green
	} else {
		$allChecks=$false
		Write-Host "$no $name version '$version' is outside of the range ($min - $max)." -ForegroundColor Red
	}
	$result = $null
}

if ( $all_checks_passed ) {
	Write-Host "All checks passed successfully." -ForegroundColor Green
} else {
	Write-Host "Some checks failed. Please review the above messages." -ForegroundColor Red
}
