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