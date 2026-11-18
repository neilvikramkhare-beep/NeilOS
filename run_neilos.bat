@echo off
setlocal
cd /d "%~dp0"
call mvn -q compile exec:java