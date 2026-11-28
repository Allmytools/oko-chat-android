param(
    [Parameter(Mandatory)][string] $Application,
    [Parameter(Mandatory)][string] $Installer,
    [Parameter(Mandatory)][string] $DesignIcon,
    [Parameter(Mandatory)][string] $InstallerGif
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
foreach ($executable in $Application, $Installer) {
    $actualIcon = [Drawing.Icon]::ExtractAssociatedIcon((Resolve-Path -LiteralPath $executable).Path)
    if (-not $actualIcon) { throw "No icon in $executable" }
    $reference = [Drawing.Icon]::new((Resolve-Path -LiteralPath $DesignIcon).Path, $actualIcon.Width, $actualIcon.Height)
    $expected = $reference.ToBitmap()
    $actual = $actualIcon.ToBitmap()
    try {
        for ($x = 0; $x -lt $actual.Width; $x++) {
            for ($y = 0; $y -lt $actual.Height; $y++) {
                if ($actual.GetPixel($x, $y).ToArgb() -ne $expected.GetPixel($x, $y).ToArgb()) {
                    throw "B5c icon differs at ($x,$y) in $executable"
                }
            }
        }
        Write-Output "B5c shell icon verified in $executable"
    } finally {
        $actual.Dispose()
        $expected.Dispose()
        $actualIcon.Dispose()
        $reference.Dispose()
    }
}

$animation = [Drawing.Image]::FromFile((Resolve-Path -LiteralPath $InstallerGif).Path)
try {
    if ($animation.Width -ne 320 -or $animation.Height -ne 320 -or $animation.GetFrameCount([Drawing.Imaging.FrameDimension]::Time) -ne 48) {
        throw "Unexpected installer animation dimensions or frame count"
    }
    Write-Output 'Verified installer GIF: 320 x 320, 48 frames'
} finally {
    $animation.Dispose()
}
