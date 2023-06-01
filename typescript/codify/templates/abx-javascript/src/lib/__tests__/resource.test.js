

const { Resource } = require('../resource');

// Getting started with Jest: https://jestjs.io/docs/getting-started

describe('Resources', () => {
  it('Create resource', async () => {
    const resource = new Resource(null, { name: 'Test Resource' });
    const result = await resource.create();
    expect(result).toEqual({ status: expect.any(String) });
  });

  it('Remove resource', async () => {
    const resource = new Resource(null, { name: 'Test Resource' });
    const result = await resource.remove();
    expect(result).toEqual({ status: expect.any(String) });
  });

  it('Make external request', async () => {
    const context = {
      // Learn more about mocking functions https://jestjs.io/docs/mock-functions
      request: jest.fn().mockResolvedValue({ content: { name: 'Mocked Value' } }),
    };

    const resource = new Resource(context, { name: 'Test Resource' });
    const result = await resource.makeExternalRequest();
    expect(result).toEqual({ name: expect.any(String) });
  });

  it('Make internal request', async () => {
    const context = {
      request: jest.fn().mockResolvedValue({ content: { id: 42 } }),
    };

    const resource = new Resource(context, { name: 'Test Resource' });
    const result = await resource.makeInternalRequest();
    expect(result).toEqual({ id: expect.any(Number) });
  });
});
