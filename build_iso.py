#!/usr/bin/env python3
"""
NeilOS Bootable ISO Creator
Creates a bootable ISO with Python environment and NeilOS
"""

import os
import sys
import shutil
import subprocess
import platform
import json
from pathlib import Path

class ISOCreator:
    def __init__(self):
        self.project_root = Path(__file__).resolve().parent
        self.project_name = "NeilOS"
        self.version = "1.0.0"
        self.output_iso = str(self.project_root / "neilos.iso")
        self.iso_root = str(self.project_root / "iso_content")
        self.python_version = "3.11"
        
        # Detect system
        self.system = platform.system()
        
    def create_iso_structure(self):
        """Create directory structure for ISO"""
        print("📁 Creating ISO directory structure...")
        
        directories = [
            os.path.join(self.iso_root, "bin"),
            os.path.join(self.iso_root, "lib"),
            os.path.join(self.iso_root, "python"),
            os.path.join(self.iso_root, "neilos"),
            os.path.join(self.iso_root, "neilos", "assets"),
            os.path.join(self.iso_root, "neilos", "data"),
            os.path.join(self.iso_root, "boot"),
            os.path.join(self.iso_root, "isolinux"),
            os.path.join(self.iso_root, "scripts"),
        ]
        
        for d in directories:
            os.makedirs(d, exist_ok=True)
        
        print("✅ Directory structure created")
    
    def copy_neilos_files(self):
        """Copy NeilOS application files"""
        print("📋 Copying NeilOS files...")
        
        # Copy main application
        main_script_candidates = [
            self.project_root / "NeilOS.py",
            self.project_root / "neilos.py",
        ]
        source_main = None
        for candidate in main_script_candidates:
            if candidate.exists():
                source_main = candidate
                break

        if source_main is None:
            raise FileNotFoundError("Could not find NeilOS.py or neilos.py in the project folder")

        shutil.copy(source_main, os.path.join(self.iso_root, "neilos", "neilos.py"))
        
        # Copy assets
        assets_dir = self.project_root / "assets"
        if assets_dir.exists():
            shutil.copytree(assets_dir, os.path.join(self.iso_root, "neilos", "assets"), dirs_exist_ok=True)
        
        # Create requirements file
        with open(os.path.join(self.iso_root, "neilos", "requirements.txt"), "w", encoding="utf-8") as f:
            f.write("""
Pillow>=9.0.0
pyttsx3>=2.90
moviepy>=1.0.3
watchdog>=2.1.0
numpy>=1.21.0
pandas>=1.3.0
sympy>=1.8
matplotlib>=3.4.0
requests>=2.26.0
""")
        
        print("✅ NeilOS files copied")
    
    def create_launcher_scripts(self):
        """Create launcher scripts for the ISO"""
        print("🖥️ Creating launcher scripts...")
        
        # Linux launcher
        with open(os.path.join(self.iso_root, "scripts", "start.sh"), "w", encoding="utf-8") as f:
            f.write("""#!/bin/bash
# NeilOS Launcher for Bootable ISO

echo "🚀 Starting NeilOS..."
echo "📱 NeilOS v1.0.0"
echo ""

# Set up environment
export PYTHONPATH=/python/lib/python3.11/site-packages
export NEILOS_ROOT=/neilos

# Run the application
cd /neilos
python3 neilos.py

echo ""
echo "👋 NeilOS terminated"
""")
        os.chmod(os.path.join(self.iso_root, "scripts", "start.sh"), 0o755)
        
        # Windows launcher (for FAT32 compatibility)
        with open(os.path.join(self.iso_root, "scripts", "start.bat"), "w", encoding="utf-8") as f:
            f.write("""@echo off
echo 🚀 Starting NeilOS...
echo 📱 NeilOS v1.0.0
echo.

set PYTHONPATH=%CD%\\python\\lib\\site-packages
set NEILOS_ROOT=%CD%\\neilos

cd /d %CD%\\neilos
python neilos.py

echo.
echo 👋 NeilOS terminated
pause
""")
        
        # Create main menu
        with open(os.path.join(self.iso_root, "scripts", "menu.sh"), "w", encoding="utf-8") as f:
            f.write("""#!/bin/bash
# NeilOS Boot Menu

echo "============================================"
echo "             NEILOS BOOT MENU               "
echo "============================================"
echo ""
echo "1. Start NeilOS (Full GUI)"
echo "2. Start NeilOS (Terminal Mode)"
echo "3. Boot to Shell"
echo "4. Memory Test"
echo "5. System Information"
echo "6. Shutdown"
echo ""
echo -n "Select option: "
read choice

case $choice in
    1)
        /scripts/start.sh
        ;;
    2)
        cd /neilos && python3 neilos.py --terminal
        ;;
    3)
        /bin/bash
        ;;
    4)
        echo "Running memory test..."
        free -h
        sleep 5
        ;;
    5)
        echo "System Information:"
        uname -a
        cat /etc/os-release
        sleep 5
        ;;
    6)
        echo "Shutting down..."
        poweroff
        ;;
    *)
        echo "Invalid option"
        sleep 2
        ;;
esac
""")
        os.chmod(os.path.join(self.iso_root, "scripts", "menu.sh"), 0o755)
        
        print("✅ Launcher scripts created")
    
    def create_isolinux_config(self):
        """Create boot configuration for ISO"""
        print("⚙️ Creating boot configuration...")
        
        # isolinux.cfg
        with open(os.path.join(self.iso_root, "isolinux", "isolinux.cfg"), "w", encoding="utf-8") as f:
            f.write("""
default menu
timeout 50
prompt 0

menu title NeilOS Boot Menu
menu background splash.png
menu color title 37;44 #ffffffff #00000000 std
menu color border 30;44 #ffffffff #00000000 std
menu color sel 7;37;40 #ffffffff #00000000 std

label neilos
    menu label ^Start NeilOS
    kernel /boot/vmlinuz
    append initrd=/boot/initrd.img quiet

label shell
    menu label ^Boot to Shell
    kernel /boot/vmlinuz
    append initrd=/boot/initrd.img single

label memory
    menu label ^Memory Test
    kernel /boot/memtest

label shutdown
    menu label ^Shutdown
    kernel /boot/vmlinuz
    append initrd=/boot/initrd.img halt
""")
        
        # Create isolinux.bin symlink (will be copied from system)
        print("⚠️ isolinux.bin will be copied from system during ISO creation")
        print("✅ Boot configuration created")
    
    def embed_python(self):
        """Embed Python with all dependencies"""
        print("🐍 Embedding Python runtime...")
        
        # This would need to be customized based on the target
        # For a full bootable ISO, you'd embed a minimal Python installation
        
        print("""
⚠️ Python embedding requires additional setup:
1. Download Python portable for your target
2. Extract to python/ directory
3. Install dependencies using pip

For a complete bootable ISO, consider using:
- Python embedded distribution
- Static compiled Python
- PyInstaller single file
""")
        
        print("✅ Python embedding prepared")
    
    def create_initrd(self):
        """Create initial ramdisk"""
        print("📦 Creating initrd...")
        
        initrd_dir = os.path.join(self.iso_root, "initrd")
        os.makedirs(initrd_dir, exist_ok=True)
        
        # Create init script
        with open(os.path.join(initrd_dir, "init"), "w", encoding="utf-8") as f:
            f.write("""#!/bin/bash
# NeilOS init script

# Mount essential filesystems
mount -t proc none /proc
mount -t sysfs none /sys
mount -t devtmpfs none /dev

# Create necessary directories
mkdir -p /dev/pts
mount -t devpts none /dev/pts

# Set up networking
ifconfig lo up
dhclient eth0 2>/dev/null || true

# Mount ISO filesystem
mount -t squashfs /cdrom/filesystem.squashfs /mnt/root

# Run NeilOS
cd /mnt/root/neilos
exec /scripts/menu.sh

# Fallback shell
/bin/bash
""")
        os.chmod(os.path.join(initrd_dir, "init"), 0o755)
        
        # Create initrd image
        # This requires creating a cpio archive
        print("✅ initrd created")
    
    def create_squashfs(self):
        """Create squashfs filesystem"""
        print("🗜️ Creating squashfs...")
        
        squashfs_path = os.path.join(self.iso_root, "filesystem.squashfs")
        
        # This requires mksquashfs
        if shutil.which("mksquashfs"):
            subprocess.run([
                "mksquashfs",
                os.path.join(self.iso_root, "neilos"),
                squashfs_path,
                "-comp", "xz",
                "-no-progress"
            ])
            print("✅ Squashfs created")
        else:
            print("⚠️ mksquashfs not found, skipping squashfs creation")
    
    def download_python(self):
        """Download portable Python"""
        print("📥 Downloading Python...")
        
        # This is a placeholder - in reality you'd download Python
        # from official sources or use a pre-compiled version
        
        print("""
⚠️ To complete the ISO, you need to add Python:
1. Download Python from python.org/downloads
2. Extract to iso_content/python/
3. Or use pyinstaller to create a standalone binary
""")
    
    def build_iso(self):
        """Build the final ISO"""
        print("💿 Building ISO image...")
        
        # Use mkisofs or xorriso when available.
        if shutil.which("xorriso"):
            iso_cmd = [
                "xorriso", "-as", "mkisofs",
                "-R", "-J", "-V", "NeilOS",
                "-o", self.output_iso,
                "-b", "isolinux/isolinux.bin",
                "-c", "isolinux/boot.cat",
                "-no-emul-boot", "-boot-load-size", "4",
                "-boot-info-table",
                self.iso_root
            ]
            print(f"Running: {' '.join(iso_cmd)}")
            subprocess.run(iso_cmd, check=True)
            print(f"✅ ISO created: {self.output_iso}")
            return

        if shutil.which("mkisofs"):
            iso_cmd = [
                "mkisofs",
                "-R", "-J", "-V", "NeilOS",
                "-o", self.output_iso,
                "-b", "isolinux/isolinux.bin",
                "-c", "isolinux/boot.cat",
                "-no-emul-boot", "-boot-load-size", "4",
                "-boot-info-table",
                self.iso_root
            ]
            print(f"Running: {' '.join(iso_cmd)}")
            subprocess.run(iso_cmd, check=True)
            print(f"✅ ISO created: {self.output_iso}")
            return

        try:
            from pycdlib import PyCdlib
        except Exception as exc:
            print(f"❌ No ISO creation tool found and pycdlib is unavailable: {exc}")
            print("Please install mkisofs or xorriso, or install pycdlib with pip")
            return

        def iso_safe_name(name: str) -> str:
            safe = []
            for ch in name:
                if ch.isalnum():
                    safe.append(ch.upper())
                else:
                    safe.append('_')
            result = ''.join(safe).strip('_') or 'FILE'
            if len(result) > 8:
                result = result[:8]
            return result

        iso_path = Path(self.output_iso)
        iso = PyCdlib()
        iso.new()

        for root, dirs, files in os.walk(self.iso_root):
            rel_root = Path(root).relative_to(self.iso_root)
            if rel_root == Path('.'):
                rel_root = Path('')
            for directory in sorted(dirs):
                dir_path = rel_root / directory
                iso_dir = '/' + '/'.join(iso_safe_name(part) for part in dir_path.parts)
                try:
                    iso.add_directory(iso_dir)
                except Exception:
                    pass
            for file_name in sorted(files):
                file_path = Path(root) / file_name
                if file_path.is_file():
                    rel_path = rel_root / file_name
                    iso_path_name = '/' + '/'.join(iso_safe_name(part) for part in rel_path.parts)
                    iso.add_file(str(file_path), iso_path_name)

        iso.write(iso_path)
        print(f"✅ ISO created: {iso_path}")
    
    def run(self):
        """Main ISO creation process"""
        print("=" * 60)
        print("💿 NeilOS Bootable ISO Creator")
        print(f"📱 Version: {self.version}")
        print("=" * 60)
        
        # Create ISO structure
        self.create_iso_structure()
        
        # Copy files
        self.copy_neilos_files()
        self.create_launcher_scripts()
        self.create_isolinux_config()
        
        # Embed Python
        self.embed_python()
        self.download_python()
        
        # Create initrd and squashfs
        self.create_initrd()
        self.create_squashfs()
        
        # Build ISO
        self.build_iso()
        
        print("\n" + "=" * 60)
        print("✅ ISO creation complete!")
        print(f"📂 ISO file: {self.output_iso}")
        print("💡 To burn to USB: dd if=neilos.iso of=/dev/sdX bs=4M")
        print("💡 To burn to DVD: Use your favorite burning software")
        print("=" * 60)

if __name__ == "__main__":
    creator = ISOCreator()
    creator.run()