

const { Resource } = require('./lib/resource');

exports.handler = async function (context, input, cb) {
  const resource = new Resource(context, input);
  const result = await resource.create();
  cb(null, result);
};
