param(
	[switch]$contribute
)

$ErrorActionPreference = 'silentlycontinue'
[regex]$regex = "\d+\.\d+\.*\d*"
$hasErrors = $false
$color = [System.ConsoleColor]::Red
if ( $PSVersionTable.PSVersion.Major -eq 5 ) {
	$yes = [char]0x221A
	$no  = [char]0x03A7
} else {
	$yes = "`u{2714} "
	$no  = "`u{274c}"
}

Write-Host "Starting Build Tools for VMware Aria Checks..."

# version should be at lest <major.minor>
@(
	@{ name = "Java";       cmd = "java";    min = "17.0"; max = "24.0"; project = "all" },
	@{ name = "Maven";      cmd = "mvn";     min = "3.9";  max = "";     project = "all" },
	@{ name = "Node.js";    cmd = "node";    min = "22.0"; max = "";     project = "all" },
	@{ name = "OpenSSL";    cmd = "openssl"; min = "3.0";  max = "";     project = "polyglot" },
	@{ name = "Python";     cmd = "python";  min = "3.7";  max = "3.10"; project = "polyglot" },
	@{ name = "Pip";        cmd = "pip";     min = "25.0"; max = "26.0"; project = "polyglot" },
	@{ name = "PowerShell"; cmd = "pwsh";    min = "7.1";  max = "7.4";  project = "polyglot" }
) |
Group-Object -Property { $_.project } |
ForEach-Object {
	$project = $_.Name
	if ($contribute -eq $false -and $project -ne "all") {
		return
	}
	else {
		if($project -ne "all") {
			Write-Host
			Write-Host "Needed for '$($_.Name)' project" -ForegroundColor Yellow
		}
	}

	$_.Group | ForEach-Object {
		$version = $min = $max = $result = $null
		$name = $_.name
		$command = "$($_.cmd) --version"
		$min = [Version]::Parse($_.min)
		$max = [Version]::Parse($_.max)
		$project = $_.project
		$result = (Invoke-Expression $command)
			
		if ($result -eq $null) {
			Write-Host "$no $name is not installed" -ForegroundColor $color
			if ($project -eq "all") {
				$hasErrors = $true
			}
			return 
		}

		$versionString = $result.Split([Environment]::NewLine)[0]
		$version = [Version]::Parse($regex.Matches($versionString)[0].Value)

		if ( ($version.CompareTo($min) -ge 0 -or $min -eq $null) -and ($version.CompareTo($max) -le 0 -or $max -eq $null) ) {
			Write-Host "$yes $name version '$version' is within the required range ($min - $max)." -ForegroundColor Green
		}
		elseif ($project -eq "all") {
			$hasErrors = $true
			Write-Host "$no $name version '$version' is outside of the range ($min - $max)." -ForegroundColor Red
		}
		else {
			Write-Host "$no $name version '$version' is outside of the range ($min - $max)." -ForegroundColor Yellow
		}
	}
}

Write-Host
if ( $hasErrors -eq $true ) {
	Write-Host "Some checks failed. Please review the above messages." -ForegroundColor Red
}
else {
	Write-Host "All mandatory checks passed successfully." -ForegroundColor Green
}

Write-Host
Write-Host "If your project is of type polyglot there might be additional dependencies required based on your selected runtime environment (e.g. Python or PowerShell)." -ForegroundColor Yellow
Write-Host
