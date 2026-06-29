param([switch]$Cuda)

$ErrorActionPreference = 'Stop'

$serviceDir = $PSScriptRoot
$venvDir = Join-Path $serviceDir '.venv'
$cacheDir = Join-Path $serviceDir '.pip-cache'
$tempDir = Join-Path $serviceDir '.tmp'

New-Item -ItemType Directory -Force -Path $cacheDir, $tempDir | Out-Null
$env:PIP_CACHE_DIR = $cacheDir
$env:TEMP = $tempDir
$env:TMP = $tempDir

if (-not (Test-Path (Join-Path $venvDir 'Scripts/python.exe'))) {
    python -m venv $venvDir
}

$python = Join-Path $venvDir 'Scripts/python.exe'

function Invoke-Pip {
    & $python -m pip @args
    if ($LASTEXITCODE -ne 0) { throw "pip failed with exit code $LASTEXITCODE" }
}

Invoke-Pip install --upgrade pip
if ($Cuda) {
    Invoke-Pip install torch torchvision --index-url https://download.pytorch.org/whl/cu128
} else {
    Invoke-Pip install torch torchvision
}
Invoke-Pip install -r (Join-Path $serviceDir 'requirements.txt')

& $python -c "import torch; print('CUDA available:', torch.cuda.is_available()); print('Device:', torch.cuda.get_device_name(0) if torch.cuda.is_available() else 'cpu')"
if ($LASTEXITCODE -ne 0) { throw 'Python environment verification failed' }
