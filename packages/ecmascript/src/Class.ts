/*-
 * #%L
 * ecmascript
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
interface ClassConstructor extends Function {
	__descriptor: any;
}

export default function defineClass(ctor: ClassConstructor, instanceMembers?: Object, staticMembers?: Object, ctorBase?: Function) {
	// Setup inheritance
	ctor.prototype = Object.create(ctorBase ? ctorBase.prototype : {}, {
		constructor: {
			value: ctor,
			enumerable: false,
			writable: true,
			configurable: true
		}
	});

	// Add toString method if it doesn't exist
	if (!ctorBase || ctorBase.prototype.toString === undefined) {
		ctor.prototype.toString = function toString() {
			return "[object " + this.constructor.name + "]";
		}
	}

	// Copy instance members
	for (let name in instanceMembers) {
		ctor.prototype[name] = instanceMembers[name];
	}

	// Copy static members
	if (ctorBase) {
		for (let name in ctorBase) {
			if (!ctor.hasOwnProperty(name)) {
				ctor[name] = ctorBase[name];
			}
		}
	}

	for (let name in staticMembers) {
		ctor[name] = staticMembers[name];
	}

	// descriptor property definition
	ctor.__descriptor = {
		name: ctor.name,
	};

	return ctor;
}
