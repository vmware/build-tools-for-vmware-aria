

class Resource {
  constructor(context, input) {
    this.context = context;
    this.input = input;
  }

  /**
   * Create a new resource.
   * @returns operation status
   */
  async create() {
    return {
      status: `Resource ${this.input.name} created`,
    };
  }

  /**
   * Remove existing resource.
   * @returns operation status
   */
  async remove() {
    return {
      status: `Resource ${this.input.name} removed`,
    };
  }

  /**
   * Create an internal GET reqest and return the response.
   * @returns the response
   */
  async makeInternalRequest() {
    const data = await this.context.request('/deployment/api/deployments', 'GET');
    return data.content;
  }

  /**
   * Create an external POST request and return the response.
   * @returns the response
   */
  async makeExternalRequest() {
    const payload = {
      userId: 1,
      title: 'My TODO item',
      completed: false,
    };
    const data = await this.context.request('https://jsonplaceholder.typicode.com/todos', 'POST', payload);
    return data.content;
  }
}

exports.Resource = Resource;
