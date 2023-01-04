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
