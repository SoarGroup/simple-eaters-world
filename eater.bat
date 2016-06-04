
set THISDIR=%~dp0
set PATH=%THISDIR%\lib\soar;%PATH%

copy %THISDIR%\lib\soar\java\swt-win64.jar %THISDIR%\lib\soar\java\swt.jar

cd %THISDIR%
java -cp %THISDIR%\bin;%THISDIR%\lib\stdlib-package.jar;%THISDIR%\lib\soar\java\sml.jar edu.umich.eecs.soar.tutorial.SimpleEaters
