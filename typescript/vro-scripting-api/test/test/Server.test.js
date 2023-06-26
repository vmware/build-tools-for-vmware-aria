describe('vRO Server API', () => {
	it('Global class definition is available', () => {
	  expect(Server).toBeDefined()
	  expect(Server).not.toBeNull()
	})

	describe('Configuration elements are supported', () => {
	  it('Root Configuration element categories list is returned', () => {
		const rootCategories = Server.getAllConfigurationElementCategories()
		expect(rootCategories).toBeDefined()
		expect(rootCategories).not.toBeNull()
	  })
	  it('Root Configuration element categories list has expected size', () => {
		// This method returns the top level categories only
		expect(Server.getAllConfigurationElementCategories().length).toEqual(1)
	  })
	  it('Fetching existing configuration category by path', () => {
		const category = Server.getConfigurationElementCategoryWithPath('PSCoE/Tests')
		expect(category).toBeDefined()
		expect(category).not.toBeNull()
	  })
	  it('Fetching non-existing configuration category by path', () => {
		const category = Server.getConfigurationElementCategoryWithPath('Foo')
		expect(category == null).toBeTruthy
	  })
	  it('Configuration category has available elements', () => {
		const category = Server.getConfigurationElementCategoryWithPath('PSCoE/Tests')
		expect(category).toBeDefined()
		expect(category).not.toBeNull()

		// Element container properties exist
		expect(category.allConfigurationElements).toBeDefined()
		expect(category.allConfigurationElements).not.toBeNull()
		expect(category.configurationElements).toBeDefined()
		expect(category.configurationElements).not.toBeNull()

		// ... and have expected lenghts
		expect(category.allConfigurationElements.length).toEqual(1)
		expect(category.configurationElements.length).toEqual(1)

		// Elements are instances of the expected class
		expect(category.configurationElements[0]).toBeInstanceOf(ConfigurationElement)
		expect(category.allConfigurationElements[0]).toBeInstanceOf(ConfigurationElement)
	  })
	  it('Create and delete configuration categories', () => {
		// Category does not exist
		expect(Server.getConfigurationElementCategoryWithPath('PSCoE/ToBeDeleted') == null).toBeTruthy()

		// Create element will also create the category if not exist.
		Server.createConfigurationElement('PSCoE/ToBeDeleted', 'Test Element')

		// Validate category exist
		const category = Server.getConfigurationElementCategoryWithPath('PSCoE/ToBeDeleted')
		expect(category).toBeDefined()
		expect(category).not.toBeNull()

		// Delete category
		Server.removeConfigurationElementCategory(category)

		// Category does not exist
		expect(Server.getConfigurationElementCategoryWithPath('PSCoE/ToBeDeleted') == null).toBeTruthy()
	  })
	  it('Create and delete configuration elements', () => {
		const category = Server.getConfigurationElementCategoryWithPath('PSCoE/Tests')
		expect(category).toBeDefined()
		expect(category).not.toBeNull()

		// There are the default, expected number of configuration elements
		expect(category.allConfigurationElements.length).toEqual(1)
		expect(category.configurationElements.length).toEqual(1)

		// Creating element works
		// ... with category reference
		const byRef = Server.createConfigurationElement(category, 'test-by-cat-ref')
		expect(byRef).toBeInstanceOf(ConfigurationElement)
		expect(byRef.name).toEqual('test-by-cat-ref')

		// ... with category name
		const byName = Server.createConfigurationElement('PSCoE/Tests', 'test-by-cat-name')
		expect(byName).toBeInstanceOf(ConfigurationElement)
		expect(byName.name).toEqual('test-by-cat-name')

		// Creating elements increases the size of the container properties
		expect(category.allConfigurationElements.length).toEqual(3)
		expect(category.configurationElements.length).toEqual(3)

		// Removing element works
		Server.removeConfigurationElement(byRef)

		// ... element is no longer present
		expect(category.configurationElements.filter(e => e.name === 'test-by-cat-ref').length).toEqual(0)
		expect(category.allConfigurationElements.filter(e => e.name === 'test-by-cat-ref').length).toEqual(0)

		// ... elements container properties have decreased size
		expect(category.allConfigurationElements.length).toEqual(2)
		expect(category.configurationElements.length).toEqual(2)
	  })
	  it('Manipulate configuration element values', () => {
		// A function to test configuration element methods,
		// all elements tested with this function should have the
		// required properties in order to pass the assertions
		// this intends to check loading different types of sources - local vs external
		function testElement (confEl) {
		  // ... default values
		  // ... strings
		  if (confEl.getAttributeWithKey('stringAttr') !== null) {
			expect(confEl.getAttributeWithKey('stringAttr')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('stringAttr').value).toEqual('String value')
		  }

		  // ... numbers
		  if (confEl.getAttributeWithKey('numberAttr') !== null) {
			expect(confEl.getAttributeWithKey('numberAttr')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('numberAttr').value).toEqual(1)
		  }
		  // ... booleans
		  if (confEl.getAttributeWithKey('booleanAttr') !== null) {
			expect(confEl.getAttributeWithKey('booleanAttr')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('booleanAttr').value).toEqual(true)
		  }
		  // ... RESTHost - no value supported
		  if (confEl.getAttributeWithKey('restHost') !== null) {
			expect(confEl.getAttributeWithKey('restHost')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('restHost').value).toEqual(null)
		  }
		  // ... Array of strings
		  // includes "#" character - used for constructing array of values by vRO
		  if (confEl.getAttributeWithKey('strArray') !== null) {
			expect(confEl.getAttributeWithKey('strArray')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('strArray').value).toEqual(['str1', 'str 2', 'str #3'])
		  }
		  // ... Array of booleans
		  if (confEl.getAttributeWithKey('boolArray') !== null) {
			expect(confEl.getAttributeWithKey('boolArray')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('boolArray').value).toEqual([true, false, true])
		  }
		  // ... Array of numbers
		  if (confEl.getAttributeWithKey('numArray') !== null) {
			expect(confEl.getAttributeWithKey('numArray')).toBeInstanceOf(Attribute)
			expect(confEl.getAttributeWithKey('numArray').value).toEqual([1, 2, 3])
		  }
		  // ... values set in the runtime
		  // ... string
		  confEl.setAttributeWithKey('customStrAttr', 'value', 'string')
		  expect(confEl.getAttributeWithKey('customStrAttr').value).toEqual('value')

		  // ... number
		  confEl.setAttributeWithKey('customNumAttr', 1, 'number')
		  expect(confEl.getAttributeWithKey('customNumAttr').value).toEqual(1)

		  // ... boolean
		  confEl.setAttributeWithKey('customBoolAttr', true, 'boolean')
		  expect(confEl.getAttributeWithKey('customBoolAttr').value).toEqual(true)

		  // ... Array of strings
		  confEl.setAttributeWithKey('customStrArr', ['value'], 'string')
		  expect(confEl.getAttributeWithKey('customStrArr').value).toEqual(['value'])

		  // ... Array of numbers
		  confEl.setAttributeWithKey('customNumArr', [1], 'number')
		  expect(confEl.getAttributeWithKey('customNumArr').value).toEqual([1])

		  // ... Array of booleans
		  confEl.setAttributeWithKey('customBoolArr', [true], 'boolean')
		  expect(confEl.getAttributeWithKey('customBoolArr').value).toEqual([true])
		}

		// For local package configuration values
		const localCat = Server.getConfigurationElementCategoryWithPath('PSCoE/Tests')
		const localConf = localCat.configurationElements != null ? localCat.configurationElements.find(e => e.name === 'Sample Config') : []
		testElement(localConf)

		// For values of configuration elements created during the runtime execution
		// Creating a new configuration element instance
		const runtimeElement = Server.createConfigurationElement(localCat, 'test-runtime-conf-element')
		runtimeElement.setAttributeWithKey('stringAttr', 'String value', 'string')
		runtimeElement.setAttributeWithKey('numberAttr', 1, 'number')
		runtimeElement.setAttributeWithKey('booleanAttr', true, 'boolean')
		runtimeElement.setAttributeWithKey('restHost', null, 'REST:RESTHost')
		runtimeElement.setAttributeWithKey('strArray', ['str1', 'str 2', 'str #3'], 'Array/string')
		runtimeElement.setAttributeWithKey('boolArray', [true, false, true], 'Array/boolean')
		runtimeElement.setAttributeWithKey('numArray', [1, 2, 3], 'Array/number')
		testElement(runtimeElement)
	  })
	})

	describe('Resource elements are supported', () => {
	  it('Root Resource element categories list has expected size', () => {
		expect(Server.getAllResourceElementCategories().length).toEqual(1)
	  })
	  it('Fetching existing resource category by path', () => {
		const category = Server.getResourceElementCategoryWithPath('PSCoE/Tests')
		expect(category).toBeDefined()
		expect(category).not.toBeNull()
	  })
	  it('Fetching non-existing resource category by path', () => {
		const category = Server.getConfigurationElementCategoryWithPath('Foo')
		expect(category == null).toBeTruthy()
	  })
	  it('Resource category properties are available', () => {
		const pscoeCategory = Server.getResourceElementCategoryWithPath('PSCoE')
		expect(pscoeCategory).toBeDefined()
		expect(pscoeCategory).not.toBeNull()
		expect(pscoeCategory.subCategories).toBeDefined()
		expect(pscoeCategory.subCategories).not.toBeNull()
		expect(pscoeCategory.subCategories.length).toEqual(1)
		expect(pscoeCategory.subCategories.length).toEqual(1)
		expect(pscoeCategory.subCategories[0].name).toEqual('Tests')

		const testCategory = Server.getResourceElementCategoryWithPath('PSCoE/Tests')
		expect(testCategory).toBeDefined()
		expect(testCategory).not.toBeNull()
		expect(testCategory.parent).toBeDefined()
		expect(testCategory.parent).not.toBeNull()
		expect(testCategory.parent.name).toEqual('PSCoE')
	  })
	  it('Resource element category has available elements', () => {
		const category = Server.getResourceElementCategoryWithPath('PSCoE/Tests')
		expect(category).toBeDefined()
		expect(category).not.toBeNull()

		// Element container properties exist
		expect(category.allResourceElements).toBeDefined()
		expect(category.allResourceElements).not.toBeNull()
		expect(category.resourceElements).toBeDefined()
		expect(category.resourceElements).not.toBeNull()

		// ... and have expected lenghts
		expect(category.allResourceElements.length).toEqual(4)
		expect(category.resourceElements.length).toEqual(4)

		// Elements are instances of the expected class
		expect(category.resourceElements[0]).toBeInstanceOf(ResourceElement)
		expect(category.allResourceElements[0]).toBeInstanceOf(ResourceElement)
	  })
	  it('Read resource elements', () => {
		const category = Server.getResourceElementCategoryWithPath('PSCoE/Tests')
		expect(category.allResourceElements.length).toEqual(4)
		expect(category.resourceElements.length).toEqual(4)

		const element = category.resourceElements != null ? category.resourceElements.find(e => e.name === 'text.txt') : null
		expect(element.mimeType).toEqual('text/plain')
		expect(element.getContentAsMimeAttachment()).toBeInstanceOf(MimeAttachment)
		expect(element.getContentAsMimeAttachment().content.trim()).toEqual('The quick brown fox jumps over the lazy dog')
	  })
	  it('Update resource elements', () => {
		const category = Server.getResourceElementCategoryWithPath('PSCoE/Tests')
		expect(category.allResourceElements.length).toEqual(4)
		expect(category.resourceElements.length).toEqual(4)

		const element = Server.createResourceElement(category, 'test-elem1')
		expect(element.getContentAsMimeAttachment().content == null).toBeTruthy()

		// Updating content
		const content = new MimeAttachment()
		content.mimeType = 'text/plain'
		content.content = 'Example content'
		element.setContentFromMimeAttachment(content)

		expect(element.mimeType).toEqual('text/plain')
		expect(element.getContentAsMimeAttachment()).toBeInstanceOf(MimeAttachment)
		expect(element.getContentAsMimeAttachment().content).toEqual('Example content')
		expect(element.getContentAsMimeAttachment().mimeType).toEqual('text/plain')

		// Delete
		Server.removeResourceElement(element)
		expect(category.allResourceElements.length).toEqual(4)
		expect(category.resourceElements.length).toEqual(4)
	  })
	  it('Create and delete resource elements', () => {
		const category = Server.getResourceElementCategoryWithPath('PSCoE/Tests')
		expect(category.allResourceElements.length).toEqual(4)
		expect(category.resourceElements.length).toEqual(4)

		// Creating element works
		const content = new MimeAttachment()
		content.mimeType = 'text/plain'
		content.content = 'Example content'

		// ... with category reference
		const byRef = Server.createResourceElement(category, 'test-by-cat-ref', content)
		expect(byRef).toBeInstanceOf(ResourceElement)
		expect(byRef.name).toEqual('test-by-cat-ref')
		expect(byRef.mimeType).toEqual('text/plain')
		expect(byRef.getContentAsMimeAttachment).toBeDefined()
		expect(byRef.getContentAsMimeAttachment()).toBeInstanceOf(MimeAttachment)
		expect(byRef.getContentAsMimeAttachment().content).toEqual('Example content')
		expect(byRef.getContentAsMimeAttachment().mimeType).toEqual('text/plain')

		// Creating elements increases the size of the container properties
		expect(category.allResourceElements.length).toEqual(5)
		expect(category.resourceElements.length).toEqual(5)

		// ... with category name
		const byName = Server.createResourceElement('PSCoE/Tests', 'test-by-cat-name', content, 'text/plain')
		expect(byName).toBeInstanceOf(ResourceElement)
		expect(byName.name).toEqual('test-by-cat-name')
		expect(byName.mimeType).toEqual('text/plain')
		expect(byName.getContentAsMimeAttachment).toBeDefined()
		expect(byName.getContentAsMimeAttachment()).toBeInstanceOf(MimeAttachment)
		expect(byName.getContentAsMimeAttachment().content).toEqual('Example content')
		expect(byName.getContentAsMimeAttachment().mimeType).toEqual('text/plain')

		// Creating elements increases the size of the container properties
		expect(category.allResourceElements.length).toEqual(6)
		expect(category.resourceElements.length).toEqual(6)

		// Removing element works
		Server.removeResourceElement(byRef)
		// ... element is no longer present
		expect(category.resourceElements.filter(e => e.name === 'test-by-cat-ref').length).toEqual(0)
		expect(category.allResourceElements.filter(e => e.name === 'test-by-cat-ref').length).toEqual(0)
		// ... elements container properties have decreased size
		expect(category.allResourceElements.length).toEqual(5)
		expect(category.resourceElements.length).toEqual(5)

		Server.removeResourceElement(byName)
		// ... element is no longer present
		expect(category.resourceElements.filter(e => e.name === 'test-by-cat-name').length).toEqual(0)
		expect(category.allResourceElements.filter(e => e.name === 'test-by-cat-name').length).toEqual(0)
		// ... elements container properties have decreased size
		expect(category.allResourceElements.length).toEqual(4)
		expect(category.resourceElements.length).toEqual(4)
	  })
	})
  })
