# NeilOS JVM

NeilOS JVM is a Java 17-compatible Swing desktop shell inspired by the original Python NeilOS project. It is a user-space workstation, not a replacement kernel or Linux distribution. The original ISO scripts remain separate because building a bootable Linux image requires a Linux live-build environment and root privileges.

## Run on Windows

Prerequisites: JDK 17 or newer and Maven.

```text
run_neilos.bat
```

Or:

```text
mvn compile exec:java
```

## Included now

- Arch-style dark workstation shell with a stable app registry.
- Animator Studio with unlimited text-area input, prompt-generated scenes, imported images, script-defined keyframes, smooth interpolation, live playback, and SVG frame export.
- Best-effort English, Hindi, and Marathi voice-over through Windows SAPI or `espeak`/`espeak-ng` on Linux. The selected OS voice must be installed for a language to sound native.
- Shared background runtime for file, process, network, image, voice, and export work, with Swing EDT synchronization and clean shutdown.
- `NeilOSFunctions.java` contains the audited Python callable surface: antivirus, notes, banking, files, terminal, search, AI, social, games, network, security, calculator, animator, code, clinic, deployment, API, kernel, monitor, accounts, and Earth operations.

## Animator script format

One keyframe per line:

```text
frame x y scale rotation
```

Example:

```text
0 100 200 0.6 0
80 420 180 1.0 15
180 650 300 1.4 360
```

The animator interpolates between surrounding keyframes using smoothstep easing. Image and text content is kept in memory for the current session; project persistence and video codecs are planned extension points.

## Relationship to the source project

The source at `C:\Users\Admin\Desktop\NeilOS` is a Python/Tkinter/Turtle monolith. Swing replaces turtle drawing and click registration with panels and listeners. Defensive boundaries remain explicit: destructive or abusive simulator functions do not launch attacks, and kernel/device operations report their JVM-userland boundary. Production database, Earth compiler, hardware, and external service adapters still need their real deployment contracts.