/**
 * Example NodeJS-based ABX action
 * @abx_type         abx
 * @abx_name         Example JS ABX Action
 * @abx_project      ref:name:Development
 * @abx_entrypoint   handler
 * @abx_input        {constant} username usernameValue
 * @abx_input        {encryptedConstant} password
 * @abx_input        {string} customFlag
 */
async function handler(context, inputs) {
  console.log(inputs);
  return ['foo', 'bar'];
}

exports.handler = handler;
