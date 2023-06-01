class Resource {

    [object] $context
    [object] $inputs

    # Constructor
    Resource([object] $context, [object] $inputs) {
        $this.context = $context
        $this.inputs = $inputs
    }

    # Create a new resource
    [object] create() {
        return @{
            status = "Resource '" + $this.inputs.name + "' created" ;
        }
    }

    # Remove existing resource
    [object] remove() {
        return @{
            status = "Resource '" + $this.inputs.name + "' removed" ;
        }
    }

    # Create an external POST reqest and return the response
    [object] makeExternalRequest() {
        $payload = @{
            userId = 1;
            title = 'My TODO item';
            completed = $false;
        }
        $data = $this.context.request('https://jsonplaceholder.typicode.com/todos', 'POST', $payload)
        return ($data.content | ConvertFrom-Json)
    }

}
