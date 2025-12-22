@echo off
REM Basit git add/commit/push betiği. Çalıştırmak için:
REM D:\UcusYonetimTest\scripts\git-push.bat "Commit mesajı"

if "%~1"=="" (
  echo Hata: Lütfen commit mesajı verin.
  echo Kullanım: %~nx0 "Commit mesajı"
  exit /b 1
)

REM Proje köküne geç (bu dosyanın bulunduğu dizinin bir üstü)
cd /d "%~dp0\.."

echo Çalışma dizini: %CD%
git status --porcelain

REM Tüm değişiklikleri ekle
git add -A

REM Commit oluştur (başarısız olursa devam etme)
git commit -m "%~1"
if ERRORLEVEL 1 (
  echo Commit basarisiz veya degisiklik yok.
  REM Eğer commit yoksa yine de push yapmak istemiyorsanız exit edebilirsiniz:
  REM exit /b 1
)

REM Aktif branch'i al
for /f "tokens=2 delims=*" %%b in ('git rev-parse --abbrev-ref HEAD 2^>nul') do set GIT_BRANCH=%%b
if "%GIT_BRANCH%"=="" set GIT_BRANCH=master

echo Push yapiliyor -> origin/%GIT_BRANCH%
git push origin %GIT_BRANCH%
if ERRORLEVEL 1 (
  echo Push basarisiz. Uzak repository veya kimlik dogrulama sorunlarini kontrol edin.
  exit /b 1
)

echo Tamamlandi.

