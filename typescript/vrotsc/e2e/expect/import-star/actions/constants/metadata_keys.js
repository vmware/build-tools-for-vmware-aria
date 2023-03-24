/**
 * @return {Any}
 */
(function () {
  const exports = {}
  exports.NAMED_TAG = 'named'
  const NAMED_TAG = exports.NAMED_TAG
  exports.NAME_TAG = 'name'
  const NAME_TAG = exports.NAME_TAG
  exports.UNMANAGED_TAG = 'unmanaged'
  const UNMANAGED_TAG = exports.UNMANAGED_TAG
  exports.OPTIONAL_TAG = 'optional'
  const OPTIONAL_TAG = exports.OPTIONAL_TAG
  exports.INJECT_TAG = 'inject'
  const INJECT_TAG = exports.INJECT_TAG
  exports.MULTI_INJECT_TAG = 'multi_inject'
  const MULTI_INJECT_TAG = exports.MULTI_INJECT_TAG
  exports.TAGGED = 'inversify:tagged'
  const TAGGED = exports.TAGGED
  exports.TAGGED_PROP = 'inversify:tagged_props'
  const TAGGED_PROP = exports.TAGGED_PROP
  exports.PARAM_TYPES = 'inversify:paramtypes'
  const PARAM_TYPES = exports.PARAM_TYPES
  exports.DESIGN_PARAM_TYPES = 'design:paramtypes'
  const DESIGN_PARAM_TYPES = exports.DESIGN_PARAM_TYPES
  exports.POST_CONSTRUCT = 'post_construct'
  const POST_CONSTRUCT = exports.POST_CONSTRUCT
  return exports
})
