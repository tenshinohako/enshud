@echo off
cd /d %~dp0

echo ^>^> Removing write attribute for read-only files...
attrib +R ../src/main/java/enshud/Main.java
attrib +R ../src/main/java/enshud/casl/CaslSimulator.java
attrib +R ../src/test/java/enshud/s0/trial/TrialTest.java
attrib +R ../src/test/java/enshud/s1/lexer/LexerTest.java
attrib +R ../src/test/java/enshud/s2/parser/ParserTest.java
attrib +R ../src/test/java/enshud/s3/checker/CheckerTest.java
attrib +R ../src/test/java/enshud/s4/compiler/CompilerTest.java
attrib +R ../build.xml
attrib +R ../data/pas/*
attrib +R ../data/ts/*
attrib +R ../data/ans/*
attrib +R ../data/cas/*

echo ^>^> Copying pre-commit hook...
cp pre-commit ..\.git\hooks\

echo ^>^> Initializing git default user...
rem Search uid from .git/config
setlocal enabledelayedexpansion
for /f "tokens=*" %%a in (..\.git\config) do (
  set d=%%a
  for %%f in ("!d:/=" "!" "%") do (
    for /f %%n in ('echo %%f^|findstr /b /e /r "\"09B[0-9]*\""') do (
      set uid=%%~n
    )
  )
)

rem Write uid to .git/config as user.name
(
  echo [user]
  echo 	name = %uid%
  echo 	email = %uid%@localhost
) >> ..\.git\config

echo     enshud project successfully initialized.
pause
