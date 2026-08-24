@echo off
set "DIR=%~dp0"
set "BASEDIR=%DIR%.."
set "REPO=%BASEDIR%\repo"

if "%JAVACMD%"=="" set JAVACMD=java

%JAVACMD% %JAVA_OPTS% -Djline.expandevents="false" -Djdk.tls.client.protocols="TLSv1,TLSv1.1,TLSv1.2" -Dapp.name="installer" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" -cp "%REPO%\*" com.vmware.pscoe.iac.installer.Installer %*
