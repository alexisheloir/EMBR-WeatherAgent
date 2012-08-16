@echo off

REM Start script for BehaviorBuilder under Windows - not tested yet!! (MK)

set BINDIR=%~dp0%
cd %BINDIR%

java -classpath ../dist/EMBOTS.jar de.dfki.embots.behaviorbuilder.BehaviorBuilder