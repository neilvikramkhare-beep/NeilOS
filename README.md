Combined futuristic operating system.

## Features


- AI assistant
- Cyber security center
- Banking system
- Search engine
- Games
- Terminal
- API manager
- Kernel support
- Bootloader support
- SQLite database
- Multi-language backend

## Run Frontend

Open:

frontend/index.html

## Run Python Backend

uvicorn server:app --reload

## Run C++ Backend

g++ server.cpp -o server -pthread

## Run Java Backend

mvn spring-boot:run

## Build Kernel

gcc -m32 -ffreestanding -c kernel.c -o kernel.o

## Build ISO

grub-mkrescue -o NeilOS.iso iso/

