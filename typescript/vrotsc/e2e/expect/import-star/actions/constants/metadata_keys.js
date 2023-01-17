/**
 * @return {Any}
 */
(function () {
    var exports = {};
    exports.NAMED_TAG = "named";
    var NAMED_TAG = exports.NAMED_TAG;
    exports.NAME_TAG = "name";
    var NAME_TAG = exports.NAME_TAG;
    exports.UNMANAGED_TAG = "unmanaged";
    var UNMANAGED_TAG = exports.UNMANAGED_TAG;
    exports.OPTIONAL_TAG = "optional";
    var OPTIONAL_TAG = exports.OPTIONAL_TAG;
    exports.INJECT_TAG = "inject";
    var INJECT_TAG = exports.INJECT_TAG;
    exports.MULTI_INJECT_TAG = "multi_inject";
    var MULTI_INJECT_TAG = exports.MULTI_INJECT_TAG;
    exports.TAGGED = "inversify:tagged";
    var TAGGED = exports.TAGGED;
    exports.TAGGED_PROP = "inversify:tagged_props";
    var TAGGED_PROP = exports.TAGGED_PROP;
    exports.PARAM_TYPES = "inversify:paramtypes";
    var PARAM_TYPES = exports.PARAM_TYPES;
    exports.DESIGN_PARAM_TYPES = "design:paramtypes";
    var DESIGN_PARAM_TYPES = exports.DESIGN_PARAM_TYPES;
    exports.POST_CONSTRUCT = "post_construct";
    var POST_CONSTRUCT = exports.POST_CONSTRUCT;
    return exports;
});
