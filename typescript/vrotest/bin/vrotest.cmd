@SETLOCAL
@SET PATHEXT=%PATHEXT:;.JS;=;%
node "%~dp0\..\lib\vrotest.js" %*
