@echo off
echo Building .exe...
"C:\Program Files (x86)\JSmooth 0.9.9-7\jsmoothcmd.exe" "C:\Users\Shwam\Documents\GitHub\EastAngliaSignalMapClient\build.jsmooth"

echo Copying .exe...
copy "C:\Users\Shwam\Documents\GitHub\EastAngliaSignalMapClient\EastAngliaMapClient - new.exe" "C:\Users\Shwam\Documents\GitHub\EastAngliaSignalMapClient\EastAngliaMapClient.exe" /Y

echo Moving .exe...
move /Y "C:\Users\Shwam\Documents\GitHub\EastAngliaSignalMapClient\EastAngliaMapClient - new.exe" "C:\Users\Shwam\Copy cambird@f2s.com\EastAngliaSignalMap.exe"

echo Finished.