# Dockerfile for NeilOS
FROM python:3.11-slim

LABEL maintainer="NeilOS Team"
LABEL version="1.0.0"
LABEL description="NeilOS Operating System Simulator"

# Install system dependencies
RUN apt-get update && apt-get install -y \
    xorg \
    x11-xserver-utils \
    python3-tk \
    xvfb \
    && rm -rf /var/lib/apt/lists/*

# Create working directory
WORKDIR /neilos

# Copy application files
COPY neilos.py .
COPY assets/ ./assets/

# Install Python dependencies
RUN pip install --no-cache-dir \
    Pillow \
    pyttsx3 \
    moviepy \
    watchdog \
    numpy \
    pandas \
    sympy \
    matplotlib \
    requests

# Create user
RUN useradd -m -u 1000 neilos && \
    chown -R neilos:neilos /neilos

# Switch to user
USER neilos

# Environment variables
ENV DISPLAY=:99
ENV PYTHONPATH=/neilos

# Expose any ports if needed
EXPOSE 8080

# Entry point
CMD ["bash", "-c", "Xvfb :99 -screen 0 1024x768x24 & python /neilos/neilos.py"]