File echoFile = new File(basedir, "src/main/resources/org/example/echo.js")
assert !echoFile.exists()

File actionFile = new File(basedir, "src/main/resources/org/test/advanced/increment.js")
assert actionFile.exists()
