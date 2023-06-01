using module .\lib\resource.psm1

function handler($context, $inputs) {
    $resource = [Resource]::new($context, $inputs)
    return $resource.remove()
}
