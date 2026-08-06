/**
 * Contains functions to communicate with any SQL DB To provide DB specific
 * functionality extend this class and override the corresponding methods.
 */
(function() {
	var Class = System.getModule("com.vmware.pscoe.library.class").Class();
	var Singleton = Class.define(function Singleton(){
		var i = 0;
		this.getNextValue = function() {
			return i++;
		}
	})
	var instance = null;
	return {
		getInstance : function() {
			if (!instance) {
				instance = new Singleton();
			}
			return instance;
		}
	}
});
