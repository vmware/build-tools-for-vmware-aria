# v2.25.1



## **Breaking Changes**
*NONE*


## Deprecations
*NONE*

## Features:

### *JSDoc parameters with properties*
JSDoc now supports parameters with properties like:
```javascript
/**
 * @param {object} args
 * @param {string} args.url #This
 */
(function (args) {
	return args.url;
})
```
Note that arguments with `.` will be ignored completely.

Parameter names will go through the following regex to determine if they should be ignored: `^[a-zA-Z0-9_$]+$`

#### Relevant Documentation:
- [Actions](./Components/Archetypes/typescript/Components/Actions.md) - for more information about parameters

## Improvements
### *Fixed IAC-592 vroes fails to import actions directly under src*


## Upgrade procedure:
*NONE*
