@echo off
set BINDIR=%~dp0%

REM set MARYDIR="%BINDIR%\..\MARY"
REM set MARYDIR="C:\Programme\MARY4.3.0"
set MARYDIR="C:\Users\aheloir\projects\programming\MARY-4.3"

if not exist %MARYDIR% (
  echo.MARY directory not found in %MARYDIR%. Cannot start.
  goto end
)

set JARDIR=%BINDIR%..\lib
set DISTDIR=%BINDIR%..\dist

if not exist %JARDIR%\semaine.jar (
  echo.No semaine.jar -- you need to do 'ant jars' in the java folder first!
  goto end
)

if %1%a==a (
  set CONFIG=%BINDIR%..\config\embots_complex.config
) else (
  set CONFIG=%1%
)

if not %CMS_URL%a==a (
  echo.Connecting to JMS server at %CMS_URL%
  set JMS_URL_SETTING="-Djms.url=%CMS_URL%"
)

REM Option Xss512k is necessary for JaCop constraint solver to avoid stack overflow!

java -Xss512k -Xmx1000m %JMS_URL_SETTING% -classpath %DISTDIR%\embots.jar;%JARDIR%\JCup.jar;%JARDIR%\JFlex.jar;%JARDIR%\JOSC.jar;%JARDIR%\SceneMaker3.jar;%JARDIR%\jama.jar;%JARDIR%\jar173_1.0_api.jar;%JARDIR%\xml-text-editor-0.0.2.jar;%JARDIR%\semaine.jar;%JARDIR%\semaine-mary.jar;%JARDIR%\semaine-dialogue.jar;%JARDIR%\JaCoP-3.1.jar;%JARDIR%\jdom.jar;%JARDIR%\log4j-1.2.15.jar;%JARDIR%\commons-logging-1.1.1.jar;%JARDIR%\activemq-all-5.1.0.jar;%JARDIR%\jgraph.jar;%JARDIR%\affect.jar -Dmary.base="%MARYDIR%" eu.semaine.system.ComponentRunner %CONFIG%

:: goto target:

:end
set /P DUMMY=Press return to continue...
