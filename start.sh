#!/bin/bash
# NeilOS Launcher

echo "🚀 NeilOS v1.0.0"
echo "Starting NeilOS..."

# Check for Python
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 not found. Please install Python 3.8+"
    exit 1
fi

# Check for dependencies
echo "📦 Checking dependencies..."
pip3 install -r requirements.txt || echo "⚠️ Some dependencies may be missing"

# Run the application
python3 neilos.py