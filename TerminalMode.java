package com.neilos.terminal;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents different terminal modes (Ubuntu, PowerShell, CMD, Bash)
 */
public enum TerminalMode {
    UBUNTU("ubuntu@neilos:~$ ", new HashMap<String, String>() {{
        put("ls", "List directory contents");
        put("cd", "Change directory");
        put("pwd", "Print working directory");
        put("mkdir", "Make directory");
        put("rm", "Remove file");
        put("cp", "Copy file");
        put("mv", "Move file");
        put("cat", "Display file contents");
        put("grep", "Search text");
        put("sudo", "Run with superuser privileges");
        put("apt", "Package management");
        put("python", "Run Python interpreter");
        put("git", "Version control");
        put("echo", "Display message");
        put("date", "Display date and time");
        put("whoami", "Display current user");
    }}),
    
    POWERSHELL("PS C:\\Users\\neilos> ", new HashMap<String, String>() {{
        put("Get-ChildItem", "List directory contents");
        put("Set-Location", "Change directory");
        put("Get-Location", "Print working directory");
        put("New-Item", "Create new item");
        put("Remove-Item", "Delete item");
        put("Copy-Item", "Copy item");
        put("Move-Item", "Move item");
        put("Get-Content", "Display file contents");
        put("Select-String", "Search text");
        put("Get-Process", "List processes");
        put("Stop-Process", "Stop process");
        put("Get-Help", "Get help");
        put("Clear-Host", "Clear screen");
        put("Write-Host", "Display message");
        put("Get-Date", "Get current date and time");
    }}),
    
    CMD("C:\\Users\\neilos> ", new HashMap<String, String>() {{
        put("dir", "List directory contents");
        put("cd", "Change directory");
        put("mkdir", "Make directory");
        put("rmdir", "Remove directory");
        put("del", "Delete file");
        put("copy", "Copy file");
        put("move", "Move file");
        put("type", "Display file contents");
        put("find", "Search text");
        put("ping", "Test network connection");
        put("ipconfig", "Display IP configuration");
        put("systeminfo", "System information");
        put("tasklist", "List processes");
        put("taskkill", "Terminate process");
        put("echo", "Display message");
        put("date", "Display date and time");
        put("time", "Display time");
        put("cls", "Clear screen");
        put("help", "Display help");
    }}),
    
    BASH("bash-5.0$ ", new HashMap<String, String>() {{
        put("ls", "List directory contents");
        put("cd", "Change directory");
        put("pwd", "Print working directory");
        put("mkdir", "Make directory");
        put("rm", "Remove file");
        put("cp", "Copy file");
        put("mv", "Move file");
        put("cat", "Display file contents");
        put("grep", "Search text");
        put("echo", "Display message");
        put("date", "Display date and time");
        put("whoami", "Display current user");
        put("ps", "Process status");
        put("kill", "Terminate process");
        put("chmod", "Change file permissions");
        put("chown", "Change file owner");
        put("df", "Disk free space");
        put("du", "Disk usage");
        put("head", "Display first lines");
        put("tail", "Display last lines");
        put("wc", "Word count");
        put("sort", "Sort text");
        put("uniq", "Unique lines");
        put("tar", "Archive files");
        put("gzip", "Compress files");
    }});
    
    private String prompt;
    private Map<String, String> commands;
    
    TerminalMode(String prompt, Map<String, String> commands) {
        this.prompt = prompt;
        this.commands = commands;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public Map<String, String> getCommands() {
        return commands;
    }
    
    public boolean hasCommand(String cmd) {
        return commands.containsKey(cmd);
    }
    
    public String getCommandDescription(String cmd) {
        return commands.get(cmd);
    }
}