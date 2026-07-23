#!/usr/bin/env python3
"""
NeilOS Setup Script
Creates a standalone executable and installer
"""

import os
import sys
import shutil
import subprocess
import platform
from pathlib import Path

class NeilOSInstaller:
    def __init__(self):
        self.project_name = "NeilOS"
        self.version = "1.0.0"
        self.author = "NeilOS Team"
        self.main_script = "neilos.py"
        self.output_dir = "dist"
        
        # System detection
        self.system = platform.system()
        self.is_windows = self.system == "Windows"
        self.is_mac = self.system == "Darwin"
        self.is_linux = self.system == "Linux"
        
        # Determine architecture
        self.architecture = platform.machine()
        
    def check_dependencies(self):
        """Check if all required dependencies are installed"""
        print("🔍 Checking dependencies...")
        
        required_packages = [
            "Pillow", "pyttsx3", "moviepy", "watchdog", 
            "numpy", "pandas", "sympy", "matplotlib", "requests"
        ]
        
        missing = []
        for package in required_packages:
            try:
                __import__(package)
                print(f"✅ {package} found")
            except ImportError:
                print(f"❌ {package} missing")
                missing.append(package)
        
        if missing:
            print(f"\n⚠️ Missing packages: {', '.join(missing)}")
            choice = input("Install missing packages? (y/n): ")
            if choice.lower() == 'y':
                for package in missing:
                    subprocess.run([sys.executable, "-m", "pip", "install", package])
                print("✅ All packages installed!")
            else:
                print("⚠️ Some features may not work without dependencies")
        
        return len(missing) == 0
    
    def create_executable(self):
        """Create a standalone executable using PyInstaller"""
        print("📦 Creating executable...")
        
        # Ensure PyInstaller is installed
        try:
            import PyInstaller
        except ImportError:
            print("Installing PyInstaller...")
            subprocess.run([sys.executable, "-m", "pip", "install", "pyinstaller"])
        
        # Build command
        cmd = [
            "pyinstaller",
            "--onefile",
            "--windowed",
            "--name", "NeilOS",
            "--icon", "assets/icon.ico" if self.is_windows else "",
            "--add-data", f"assets{os.pathsep}assets",
            "--hidden-import", "PIL.Image",
            "--hidden-import", "PIL.ImageDraw",
            "--hidden-import", "PIL.ImageFont",
            "--hidden-import", "moviepy",
            "--hidden-import", "watchdog",
            "--hidden-import", "numpy",
            "--hidden-import", "pandas",
            "--hidden-import", "sympy",
            "--hidden-import", "matplotlib",
            self.main_script
        ]
        
        # Remove empty args
        cmd = [arg for arg in cmd if arg]
        
        print(f"Running: {' '.join(cmd)}")
        subprocess.run(cmd)
        
        print("✅ Executable created in dist/ directory")
        return os.path.join("dist", "NeilOS" + (".exe" if self.is_windows else ""))
    
    def create_windows_installer(self, executable_path):
        """Create Windows installer using Inno Setup or NSIS"""
        print("🪟 Creating Windows installer...")
        
        # Check for Inno Setup
        inno_path = r"C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
        if not os.path.exists(inno_path):
            print("⚠️ Inno Setup not found at default location")
            print("Please install Inno Setup from: https://jrsoftware.org/isdl.php")
            return False
        
        # Create Inno Setup script
        with open("installer/inno/neilos_installer.iss", "w") as f:
            f.write(f"""
[Setup]
AppName=NeilOS
AppVersion={self.version}
DefaultDirName={{pf}}\\NeilOS
DefaultGroupName=NeilOS
UninstallDisplayIcon={{app}}\\NeilOS.exe
Compression=lzma2
SolidCompression=yes
OutputDir=dist
OutputBaseFilename=NeilOS_Setup

[Files]
Source: "{executable_path}"; DestDir: "{{app}}"
Source: "assets\\*"; DestDir: "{{app}}\\assets"

[Icons]
Name: "{{group}}\\NeilOS"; Filename: "{{app}}\\NeilOS.exe"
Name: "{{group}}\\Uninstall NeilOS"; Filename: "{{uninstallexe}"
Name: "{{commondesktop}}\\NeilOS"; Filename: "{{app}}\\NeilOS.exe"

[Run]
Filename: "{{app}}\\NeilOS.exe"; Description: "Launch NeilOS"; Flags: postinstall nowait skipifsilent
""")
        
        # Build installer
        subprocess.run([inno_path, "installer/inno/neilos_installer.iss"])
        print("✅ Windows installer created!")
        return True
    
    def create_mac_app(self):
        """Create macOS application bundle"""
        print("🍎 Creating macOS application...")
        
        # Create app bundle structure
        app_name = "NeilOS.app"
        app_path = os.path.join("dist", app_name)
        contents_path = os.path.join(app_path, "Contents")
        macos_path = os.path.join(contents_path, "MacOS")
        resources_path = os.path.join(contents_path, "Resources")
        
        os.makedirs(macos_path, exist_ok=True)
        os.makedirs(resources_path, exist_ok=True)
        
        # Copy executable
        shutil.copy("dist/NeilOS", os.path.join(macos_path, "NeilOS"))
        os.chmod(os.path.join(macos_path, "NeilOS"), 0o755)
        
        # Create Info.plist
        with open(os.path.join(contents_path, "Info.plist"), "w") as f:
            f.write(f"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleName</key>
    <string>NeilOS</string>
    <key>CFBundleDisplayName</key>
    <string>NeilOS</string>
    <key>CFBundleIdentifier</key>
    <string>com.neilos.os</string>
    <key>CFBundleVersion</key>
    <string>{self.version}</string>
    <key>CFBundleShortVersionString</key>
    <string>{self.version}</string>
    <key>CFBundleIconFile</key>
    <string>AppIcon</string>
    <key>CFBundleExecutable</key>
    <string>NeilOS</string>
    <key>NSHighResolutionCapable</key>
    <true/>
</dict>
</plist>""")
        
        # Create icon (placeholder)
        if not os.path.exists(os.path.join(resources_path, "AppIcon.icns")):
            print("⚠️ No icon found, using default")
        
        print("✅ macOS application created!")
        return app_path
    
    def create_linux_deb(self):
        """Create Linux .deb package"""
        print("🐧 Creating Linux .deb package...")
        
        deb_name = f"neilos_{self.version}_all"
        deb_dir = os.path.join("dist", deb_name)
        
        # Create DEB structure
        os.makedirs(os.path.join(deb_dir, "DEBIAN"), exist_ok=True)
        os.makedirs(os.path.join(deb_dir, "usr", "local", "bin"), exist_ok=True)
        os.makedirs(os.path.join(deb_dir, "usr", "share", "applications"), exist_ok=True)
        os.makedirs(os.path.join(deb_dir, "usr", "share", "neilos"), exist_ok=True)
        
        # Copy files
        shutil.copy("dist/NeilOS", os.path.join(deb_dir, "usr", "local", "bin", "neilos"))
        os.chmod(os.path.join(deb_dir, "usr", "local", "bin", "neilos"), 0o755)
        
        shutil.copytree("assets", os.path.join(deb_dir, "usr", "share", "neilos", "assets"))
        
        # Create control file
        with open(os.path.join(deb_dir, "DEBIAN", "control"), "w") as f:
            f.write(f"""Package: neilos
Version: {self.version}
Section: utils
Priority: optional
Architecture: all
Maintainer: {self.author}
Description: NeilOS Operating System Simulator
 A feature-rich simulated operating system with AI, security, and multimedia features.
""")
        
        # Build .deb
        subprocess.run(["dpkg", "-b", deb_dir])
        print("✅ .deb package created!")
        return True
    
    def create_portable(self):
        """Create portable version for Windows"""
        print("📱 Creating portable version...")
        
        portable_dir = os.path.join("dist", "NeilOS_Portable")
        os.makedirs(portable_dir, exist_ok=True)
        
        # Copy executable
        shutil.copy("dist/NeilOS.exe", os.path.join(portable_dir, "NeilOS.exe"))
        
        # Copy assets
        shutil.copytree("assets", os.path.join(portable_dir, "assets"))
        
        # Create launcher script
        with open(os.path.join(portable_dir, "run.bat"), "w") as f:
            f.write("""@echo off
echo Starting NeilOS...
start /B NeilOS.exe
""")
        
        print(f"✅ Portable version created in: {portable_dir}")
        return portable_dir
    
    def run(self):
        """Main installation process"""
        print("=" * 60)
        print(f"🚀 NeilOS Installer v{self.version}")
        print(f"📱 System: {self.system} ({self.architecture})")
        print("=" * 60)
        
        # Check dependencies
        self.check_dependencies()
        
        # Create executable
        executable = self.create_executable()
        
        # Create platform-specific installer
        if self.is_windows:
            self.create_windows_installer(executable)
            self.create_portable()
        elif self.is_mac:
            self.create_mac_app()
        elif self.is_linux:
            self.create_linux_deb()
        
        print("\n" + "=" * 60)
        print("✅ Installation complete!")
        print("📂 Files available in: dist/")
        print("=" * 60)

if __name__ == "__main__":
    installer = NeilOSInstaller()
    installer.run()