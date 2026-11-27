param(
    [Parameter(Mandatory)][string] $Installer,
    [Parameter(Mandatory)][string] $OutputGif
)

$ErrorActionPreference = 'Stop'
Add-Type -TypeDefinition @'
using System;
using System.ComponentModel;
using System.Runtime.InteropServices;

public static class SquirrelPayload
{
    [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
    private static extern IntPtr LoadLibraryEx(string path, IntPtr file, uint flags);

    [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
    private static extern IntPtr FindResource(IntPtr module, IntPtr name, string type);

    [DllImport("kernel32.dll", SetLastError = true)]
    private static extern IntPtr LoadResource(IntPtr module, IntPtr resource);

    [DllImport("kernel32.dll", SetLastError = true)]
    private static extern uint SizeofResource(IntPtr module, IntPtr resource);

    [DllImport("kernel32.dll")]
    private static extern IntPtr LockResource(IntPtr resource);

    [DllImport("kernel32.dll")]
    private static extern bool FreeLibrary(IntPtr module);

    public static byte[] Read(string path)
    {
        const uint loadAsDataFile = 2;
        var module = LoadLibraryEx(path, IntPtr.Zero, loadAsDataFile);
        if (module == IntPtr.Zero) throw new Win32Exception(Marshal.GetLastWin32Error());
        try
        {
            // Squirrel's WriteZipToSetup stores the install ZIP as resource DATA/131.
            var entry = FindResource(module, new IntPtr(131), "DATA");
            if (entry == IntPtr.Zero) throw new Win32Exception(Marshal.GetLastWin32Error());
            var size = SizeofResource(module, entry);
            if (size == 0) throw new Win32Exception(Marshal.GetLastWin32Error());
            var resource = LoadResource(module, entry);
            if (resource == IntPtr.Zero) throw new Win32Exception(Marshal.GetLastWin32Error());
            var address = LockResource(resource);
            if (address == IntPtr.Zero) throw new InvalidOperationException("Cannot read Squirrel installer ZIP");
            var bytes = new byte[checked((int)size)];
            Marshal.Copy(address, bytes, 0, bytes.Length);
            return bytes;
        }
        finally
        {
            FreeLibrary(module);
        }
    }
}
'@

$stream = [IO.MemoryStream]::new([SquirrelPayload]::Read($Installer))
$zip = [IO.Compression.ZipArchive]::new($stream)
try {
    $gif = $zip.GetEntry('background.gif')
    if ($null -eq $gif) {
        throw 'Squirrel installer has no embedded background.gif'
    }
    [IO.Compression.ZipFileExtensions]::ExtractToFile($gif, $OutputGif)
} finally {
    $zip.Dispose()
}
