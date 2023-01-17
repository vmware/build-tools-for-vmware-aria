/*
 * #%L
 * o11n-xml-package-maven-plugin
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
File echoFile = new File(basedir, "src/main/resources/ScriptModule/org/test/advanced/echo.xml")
assert !echoFile.exists()

File echoInfo = new File(basedir, "src/main/resources/ScriptModule/org/test/advanced/echo.element_info.xml")
assert !echoInfo.exists()

File actionFile = new File(basedir, "src/main/resources/ScriptModule/org/test/advanced/increment.xml")
assert actionFile.exists()

File wfFile = new File(basedir, "src/main/resources/Workflow/Test/Advanced/Switch.xml")
assert wfFile.exists()

File wfInfoFile = new File(basedir, "src/main/resources/Workflow/Test/Advanced/Switch.element_info.xml")
assert wfInfoFile.exists()