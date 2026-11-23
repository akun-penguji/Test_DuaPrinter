@echo off
REM ============================================================
REM  Menjalankan Anjungan Pasien Mandiri sambil MEREKAM SEMUA
REM  keluaran (termasuk stack trace) ke berkas log.
REM
REM  Dipakai saat aplikasi tampil fullscreen sehingga jendela
REM  konsol tidak terlihat dan error tidak bisa dibaca.
REM
REM  Cara pakai: taruh berkas ini SATU FOLDER dengan
REM  AnjunganMandiri2026.jar, lalu klik dua kali.
REM  Hasilnya: log\anjungan-console.log
REM ============================================================

cd /d "%~dp0"

if not exist "log" mkdir "log"

set STAMP=%date:~-4%-%date:~4,2%-%date:~7,2%
set LOGFILE=log\anjungan-console-%STAMP%.log

echo. >> "%LOGFILE%"
echo ================================================== >> "%LOGFILE%"
echo Dijalankan pada %date% %time% >> "%LOGFILE%"
echo ================================================== >> "%LOGFILE%"

REM  PENTING: pakai "java", BUKAN "javaw".
REM  javaw tidak punya konsol sehingga keluaran tidak bisa dialihkan.
java -jar AnjunganMandiri2026.jar >> "%LOGFILE%" 2>&1

echo. >> "%LOGFILE%"
echo Aplikasi berhenti pada %date% %time% (exit code %ERRORLEVEL%) >> "%LOGFILE%"

REM  Jendela ditahan supaya pesan terakhir sempat terbaca kalau
REM  aplikasi langsung tertutup saat start.
echo.
echo Aplikasi berhenti. Log tersimpan di: %LOGFILE%
echo.
pause
