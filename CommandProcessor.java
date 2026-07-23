package com.neilos.terminal;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CommandProcessor - Advanced command processing engine for NeilOS Terminal
 * Handles command parsing, execution, piping, redirection, and scripting.
 * 
 * Features:
 * - Command parsing and tokenization
 * - Built-in commands (cd, ls, pwd, etc.)
 * - External command execution
 * - Command piping (|)
 * - Input/Output redirection (<, >, >>)
 * - Command chaining (&&, ||, ;)
 * - Background execution (&)
 * - Command aliases
 * - Environment variables
 * - Command history integration
 * - Script execution
 * - Auto-completion suggestions
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class CommandProcessor {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default prompt */
    public static final String DEFAULT_PROMPT = "neilos> ";
    
    /** Environment variable prefix */
    public static final String ENV_PREFIX = "$";
    
    /** Command separator for chaining */
    public static final String CHAIN_AND = "&&";
    public static final String CHAIN_OR = "||";
    public static final String CHAIN_SEMICOLON = ";";
    
    /** Redirection operators */
    public static final String REDIRECT_IN = "<";
    public static final String REDIRECT_OUT = ">";
    public static final String REDIRECT_APPEND = ">>";
    
    /** Pipe operator */
    public static final String PIPE = "|";
    
    /** Background execution */
    public static final String BACKGROUND = "&";
    
    /** Comment character */
    public static final String COMMENT = "#";
    
    /** Escape character */
    public static final char ESCAPE = '\\';
    
    /** Quote characters */
    public static final char QUOTE_SINGLE = '\'';
    public static final char QUOTE_DOUBLE = '"';
    
    /** Maximum command length */
    public static final int MAX_COMMAND_LENGTH = 32768;
    
    /** Maximum history size */
    public static final int MAX_HISTORY_SIZE = 1000;
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Command execution result
     */
    public static class CommandResult {
        private String command;
        private int exitCode;
        private String output;
        private String error;
        private long executionTime;
        private boolean success;
        private List<String> outputLines;
        private Map<String, String> environment;
        private String workingDirectory;
        
        public CommandResult(String command) {
            this.command = command;
            this.outputLines = new ArrayList<>();
            this.environment = new HashMap<>();
            this.workingDirectory = System.getProperty("user.dir");
            this.exitCode = 0;
            this.success = true;
        }
        
        public String getCommand() { return command; }
        public void setCommand(String command) { this.command = command; }
        
        public int getExitCode() { return exitCode; }
        public void setExitCode(int exitCode) { 
            this.exitCode = exitCode;
            this.success = exitCode == 0;
        }
        
        public String getOutput() { return output; }
        public void setOutput(String output) { 
            this.output = output;
            if (output != null) {
                this.outputLines = Arrays.asList(output.split("\n"));
            }
        }
        
        public void appendOutput(String line) {
            if (line != null) {
                outputLines.add(line);
                if (output == null) {
                    output = line;
                } else {
                    output += "\n" + line;
                }
            }
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public List<String> getOutputLines() { return outputLines; }
        
        public Map<String, String> getEnvironment() { return environment; }
        public void setEnvironment(Map<String, String> environment) { 
            this.environment = environment != null ? environment : new HashMap<>();
        }
        
        public String getWorkingDirectory() { return workingDirectory; }
        public void setWorkingDirectory(String workingDirectory) { 
            this.workingDirectory = workingDirectory; 
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CommandResult{\n");
            sb.append("  command: ").append(command).append("\n");
            sb.append("  exitCode: ").append(exitCode).append("\n");
            sb.append("  success: ").append(success).append("\n");
            sb.append("  executionTime: ").append(executionTime).append("ms\n");
            if (output != null && !output.isEmpty()) {
                sb.append("  output: ").append(output).append("\n");
            }
            if (error != null && !error.isEmpty()) {
                sb.append("  error: ").append(error).append("\n");
            }
            sb.append("}");
            return sb.toString();
        }
    }
    
    /**
     * Command alias
     */
    public static class Alias {
        private String name;
        private String command;
        private String description;
        private LocalDateTime createdAt;
        
        public Alias(String name, String command) {
            this.name = name;
            this.command = command;
            this.createdAt = LocalDateTime.now();
        }
        
        public Alias(String name, String command, String description) {
            this(name, command);
            this.description = description;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getCommand() { return command; }
        public void setCommand(String command) { this.command = command; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        @Override
        public String toString() {
            return name + " -> " + command + (description != null ? " (" + description + ")" : "");
        }
    }
    
    /**
     * Command completion suggestion
     */
    public static class Suggestion {
        private String text;
        private String description;
        private String type;
        private double score;
        
        public Suggestion(String text) {
            this.text = text;
            this.score = 1.0;
        }
        
        public Suggestion(String text, String description) {
            this(text);
            this.description = description;
        }
        
        public Suggestion(String text, String description, String type) {
            this(text, description);
            this.type = type;
        }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        
        @Override
        public String toString() {
            return text + (description != null ? " - " + description : "");
        }
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private String prompt;
    private String workingDirectory;
    private Map<String, String> environment;
    private Map<String, Alias> aliases;
    private List<String> history;
    private int historyIndex;
    private TerminalHistory terminalHistory;
    private boolean verbose;
    private boolean strictMode;
    private int maxHistorySize;
    private CommandCallback callback;
    private Map<String, BuiltinCommand> builtins;
    
    // ============================================================
    // INTERFACES
    // ============================================================
    
    /**
     * Callback interface for command events
     */
    public interface CommandCallback {
        void onCommandStart(String command);
        void onCommandComplete(CommandResult result);
        void onCommandError(String command, String error);
        void onOutput(String line);
        void onPromptChanged(String prompt);
    }
    
    /**
     * Built-in command interface
     */
    public interface BuiltinCommand {
        CommandResult execute(String[] args, CommandProcessor processor);
        String getDescription();
        String getUsage();
        List<String> getCompletions(String partial);
    }
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public CommandProcessor() {
        this(DEFAULT_PROMPT);
    }
    
    /**
     * Constructor with custom prompt
     * 
     * @param prompt The command prompt
     */
    public CommandProcessor(String prompt) {
        this.prompt = prompt;
        this.workingDirectory = System.getProperty("user.dir");
        this.environment = new HashMap<>(System.getenv());
        this.aliases = new ConcurrentHashMap<>();
        this.history = new ArrayList<>();
        this.maxHistorySize = MAX_HISTORY_SIZE;
        this.historyIndex = 0;
        this.verbose = false;
        this.strictMode = false;
        this.builtins = new ConcurrentHashMap<>();
        this.terminalHistory = new TerminalHistory();
        
        // Register built-in commands
        registerBuiltins();
        
        // Load aliases
        loadAliases();
    }
    
    // ============================================================
    // COMMAND EXECUTION
    // ============================================================
    
    /**
     * Processes and executes a command
     * 
     * @param input The command input
     * @return CommandResult object
     */
    public CommandResult execute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new CommandResult("");
        }
        
        // Add to history
        addToHistory(input);
        
        // Parse command
        ParsedCommand parsed = parseCommand(input);
        if (parsed == null) {
            return errorResult("Failed to parse command");
        }
        
        // Expand aliases
        String expanded = expandAliases(parsed.original);
        if (!expanded.equals(parsed.original)) {
            parsed = parseCommand(expanded);
        }
        
        // Notify start
        if (callback != null) {
            callback.onCommandStart(parsed.original);
        }
        
        long startTime = System.currentTimeMillis();
        CommandResult result = null;
        
        try {
            // Handle command chains
            if (parsed.chains != null && !parsed.chains.isEmpty()) {
                result = executeChain(parsed);
            } else {
                // Handle pipes
                if (parsed.pipes != null && !parsed.pipes.isEmpty()) {
                    result = executePipe(parsed);
                } else {
                    // Execute single command
                    result = executeSingleCommand(parsed);
                }
            }
            
        } catch (Exception e) {
            result = errorResult("Error executing command: " + e.getMessage());
            if (callback != null) {
                callback.onCommandError(parsed.original, e.getMessage());
            }
        }
        
        if (result != null) {
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            
            // Notify completion
            if (callback != null) {
                callback.onCommandComplete(result);
            }
            
            // Add to terminal history with details
            terminalHistory.addCommand(
                parsed.original,
                result.getExitCode(),
                result.getExecutionTime(),
                result.getOutput()
            );
        }
        
        return result;
    }
    
    /**
     * Executes a single command
     */
    private CommandResult executeSingleCommand(ParsedCommand parsed) {
        if (parsed.commands == null || parsed.commands.isEmpty()) {
            return new CommandResult(parsed.original);
        }
        
        String[] args = parsed.commands.get(0);
        if (args == null || args.length == 0) {
            return new CommandResult(parsed.original);
        }
        
        String command = args[0];
        String[] cmdArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
        
        // Check if it's a built-in command
        BuiltinCommand builtin = builtins.get(command.toLowerCase());
        if (builtin != null) {
            CommandResult result = builtin.execute(cmdArgs, this);
            result.setCommand(parsed.original);
            return result;
        }
        
        // Check if it's an alias
        Alias alias = aliases.get(command.toLowerCase());
        if (alias != null) {
            ParsedCommand aliasParsed = parseCommand(alias.getCommand() + " " + String.join(" ", cmdArgs));
            return executeSingleCommand(aliasParsed);
        }
        
        // Execute external command
        return executeExternal(parsed.original, command, cmdArgs, parsed);
    }
    
    /**
     * Executes a command chain
     */
    private CommandResult executeChain(ParsedCommand parsed) {
        CommandResult lastResult = null;
        boolean shouldContinue = true;
        
        for (ChainCommand chain : parsed.chains) {
            if (!shouldContinue) {
                break;
            }
            
            ParsedCommand cmd = parseCommand(chain.command);
            if (cmd == null) {
                continue;
            }
            
            if (cmd.pipes != null && !cmd.pipes.isEmpty()) {
                lastResult = executePipe(cmd);
            } else {
                lastResult = executeSingleCommand(cmd);
            }
            
            // Handle chain operators
            if (chain.operator.equals(CHAIN_AND)) {
                shouldContinue = lastResult != null && lastResult.isSuccess();
            } else if (chain.operator.equals(CHAIN_OR)) {
                shouldContinue = lastResult == null || !lastResult.isSuccess();
            }
            // For semicolon, always continue
        }
        
        return lastResult != null ? lastResult : new CommandResult(parsed.original);
    }
    
    /**
     * Executes a pipe chain
     */
    private CommandResult executePipe(ParsedCommand parsed) {
        List<Process> processes = new ArrayList<>();
        List<CommandResult> results = new ArrayList<>();
        CommandResult finalResult = new CommandResult(parsed.original);
        
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(new File(workingDirectory));
            
            // Build the pipe chain
            Process prevProcess = null;
            
            for (int i = 0; i < parsed.pipes.size(); i++) {
                String[] cmdArgs = parsed.pipes.get(i);
                String command = cmdArgs[0];
                String[] args = cmdArgs.length > 1 ? Arrays.copyOfRange(cmdArgs, 1, cmdArgs.length) : new String[0];
                
                // Check for built-in commands in pipe
                BuiltinCommand builtin = builtins.get(command.toLowerCase());
                if (builtin != null) {
                    // Execute built-in separately (simplified)
                    CommandResult result = builtin.execute(args, this);
                    finalResult.appendOutput(result.getOutput());
                    finalResult.setExitCode(result.getExitCode());
                    results.add(result);
                    continue;
                }
                
                // Build external command
                List<String> cmdList = new ArrayList<>();
                cmdList.add(command);
                cmdList.addAll(Arrays.asList(args));
                
                pb.command(cmdList);
                pb.redirectErrorStream(true);
                
                if (prevProcess != null) {
                    pb.redirectInput(ProcessBuilder.Redirect.PIPE);
                }
                
                Process process = pb.start();
                processes.add(process);
                
                // Connect previous process output to this process input
                if (prevProcess != null) {
                    try (OutputStream os = process.getOutputStream()) {
                        try (InputStream is = prevProcess.getInputStream()) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
                
                prevProcess = process;
            }
            
            // Collect output from last process
            if (prevProcess != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(prevProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        finalResult.appendOutput(line);
                        if (callback != null) {
                            callback.onOutput(line);
                        }
                    }
                }
                
                int exitCode = prevProcess.waitFor();
                finalResult.setExitCode(exitCode);
            }
            
        } catch (Exception e) {
            finalResult.setError(e.getMessage());
            finalResult.setSuccess(false);
            finalResult.setExitCode(1);
        }
        
        return finalResult;
    }
    
    /**
     * Executes an external command
     */
    private CommandResult executeExternal(String fullCommand, String command, String[] args, ParsedCommand parsed) {
        CommandResult result = new CommandResult(fullCommand);
        
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.directory(new File(workingDirectory));
            
            // Add environment variables
            Map<String, String> env = pb.environment();
            env.putAll(environment);
            
            // Build command list
            List<String> cmdList = new ArrayList<>();
            cmdList.add(command);
            cmdList.addAll(Arrays.asList(args));
            
            pb.command(cmdList);
            pb.redirectErrorStream(true);
            
            // Handle redirections
            if (parsed.inputFile != null) {
                pb.redirectInput(new File(resolvePath(parsed.inputFile)));
            }
            if (parsed.outputFile != null) {
                if (parsed.appendOutput) {
                    pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(resolvePath(parsed.outputFile))));
                } else {
                    pb.redirectOutput(new File(resolvePath(parsed.outputFile)));
                }
            }
            
            // Start process
            Process process = pb.start();
            
            // Read output
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    if (callback != null) {
                        callback.onOutput(line);
                    }
                }
            }
            
            int exitCode = process.waitFor();
            result.setOutput(output.toString());
            result.setExitCode(exitCode);
            result.setSuccess(exitCode == 0);
            result.setWorkingDirectory(workingDirectory);
            result.setEnvironment(new HashMap<>(environment));
            
        } catch (Exception e) {
            result.setError(e.getMessage());
            result.setSuccess(false);
            result.setExitCode(1);
        }
        
        return result;
    }
    
    // ============================================================
    // COMMAND PARSING
    // ============================================================
    
    /**
     * Parsed command structure
     */
    private static class ParsedCommand {
        String original;
        List<String[]> commands;
        List<String[]> pipes;
        List<ChainCommand> chains;
        String inputFile;
        String outputFile;
        boolean appendOutput;
        boolean background;
        
        ParsedCommand(String original) {
            this.original = original;
            this.commands = new ArrayList<>();
            this.pipes = new ArrayList<>();
            this.chains = new ArrayList<>();
        }
    }
    
    /**
     * Chain command structure
     */
    private static class ChainCommand {
        String operator;
        String command;
        
        ChainCommand(String operator, String command) {
            this.operator = operator;
            this.command = command;
        }
    }
    
    /**
     * Parses a command string
     */
    private ParsedCommand parseCommand(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = input.trim();
        ParsedCommand parsed = new ParsedCommand(trimmed);
        
        // Check for background execution
        if (trimmed.endsWith(BACKGROUND)) {
            parsed.background = true;
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }
        
        // Check for comments
        int commentIndex = trimmed.indexOf(COMMENT);
        if (commentIndex >= 0) {
            trimmed = trimmed.substring(0, commentIndex).trim();
            if (trimmed.isEmpty()) {
                return null;
            }
        }
        
        // Parse command chains
        List<ChainCommand> chains = parseChains(trimmed);
        if (!chains.isEmpty()) {
            parsed.chains = chains;
            return parsed;
        }
        
        // Parse pipes
        List<String> pipeSegments = splitWithQuotes(trimmed, PIPE);
        if (pipeSegments.size() > 1) {
            for (String segment : pipeSegments) {
                String[] args = tokenize(segment.trim());
                if (args != null && args.length > 0) {
                    parsed.pipes.add(args);
                }
            }
            return parsed;
        }
        
        // Parse redirections
        parsed = parseRedirections(parsed, trimmed);
        
        // Parse command and arguments
        String[] args = tokenize(parsed.original);
        if (args != null && args.length > 0) {
            parsed.commands.add(args);
        }
        
        return parsed;
    }
    
    /**
     * Parses command chains
     */
    private List<ChainCommand> parseChains(String input) {
        List<ChainCommand> chains = new ArrayList<>();
        String[] operators = {CHAIN_AND, CHAIN_OR, CHAIN_SEMICOLON};
        
        // Find the first operator
        int minIndex = -1;
        String minOperator = null;
        
        for (String op : operators) {
            int index = findOperator(input, op);
            if (index >= 0 && (minIndex < 0 || index < minIndex)) {
                minIndex = index;
                minOperator = op;
            }
        }
        
        if (minIndex >= 0) {
            String left = input.substring(0, minIndex).trim();
            String right = input.substring(minIndex + minOperator.length()).trim();
            
            if (!left.isEmpty()) {
                chains.add(new ChainCommand(minOperator, left));
            }
            if (!right.isEmpty()) {
                chains.addAll(parseChains(right));
            }
        }
        
        return chains;
    }
    
    /**
     * Finds an operator in the input, respecting quotes
     */
    private int findOperator(String input, String operator) {
        boolean inSingle = false;
        boolean inDouble = false;
        boolean escaped = false;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (c == ESCAPE) {
                escaped = true;
                continue;
            }
            
            if (c == QUOTE_SINGLE && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            
            if (c == QUOTE_DOUBLE && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            
            if (!inSingle && !inDouble) {
                // Check if operator matches
                if (i + operator.length() <= input.length()) {
                    String substring = input.substring(i, i + operator.length());
                    if (substring.equals(operator)) {
                        return i;
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Parses redirections
     */
    private ParsedCommand parseRedirections(ParsedCommand parsed, String input) {
        String remaining = input;
        
        // Parse input redirection (<)
        int inIndex = findRedirection(remaining, REDIRECT_IN);
        if (inIndex >= 0) {
            String before = remaining.substring(0, inIndex).trim();
            String after = remaining.substring(inIndex + REDIRECT_IN.length()).trim();
            
            // Get the filename
            String[] tokens = tokenize(after);
            if (tokens != null && tokens.length > 0) {
                parsed.inputFile = tokens[0];
                // The rest becomes the command
                if (tokens.length > 1) {
                    remaining = before + " " + String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
                } else {
                    remaining = before;
                }
            }
        }
        
        // Parse output redirection (> and >>)
        int outIndex = findRedirection(remaining, REDIRECT_APPEND);
        if (outIndex >= 0) {
            parsed.appendOutput = true;
            String before = remaining.substring(0, outIndex).trim();
            String after = remaining.substring(outIndex + REDIRECT_APPEND.length()).trim();
            
            String[] tokens = tokenize(after);
            if (tokens != null && tokens.length > 0) {
                parsed.outputFile = tokens[0];
                if (tokens.length > 1) {
                    remaining = before + " " + String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
                } else {
                    remaining = before;
                }
            }
        } else {
            outIndex = findRedirection(remaining, REDIRECT_OUT);
            if (outIndex >= 0) {
                parsed.appendOutput = false;
                String before = remaining.substring(0, outIndex).trim();
                String after = remaining.substring(outIndex + REDIRECT_OUT.length()).trim();
                
                String[] tokens = tokenize(after);
                if (tokens != null && tokens.length > 0) {
                    parsed.outputFile = tokens[0];
                    if (tokens.length > 1) {
                        remaining = before + " " + String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
                    } else {
                        remaining = before;
                    }
                }
            }
        }
        
        parsed.original = remaining;
        return parsed;
    }
    
    /**
     * Finds a redirection operator
     */
    private int findRedirection(String input, String operator) {
        boolean inSingle = false;
        boolean inDouble = false;
        boolean escaped = false;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (c == ESCAPE) {
                escaped = true;
                continue;
            }
            
            if (c == QUOTE_SINGLE && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            
            if (c == QUOTE_DOUBLE && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            
            if (!inSingle && !inDouble) {
                if (i + operator.length() <= input.length()) {
                    String substring = input.substring(i, i + operator.length());
                    if (substring.equals(operator)) {
                        return i;
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Tokenizes a command string respecting quotes and escapes
     */
    private String[] tokenize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean escaped = false;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (escaped) {
                current.append(c);
                escaped = false;
                continue;
            }
            
            if (c == ESCAPE) {
                escaped = true;
                continue;
            }
            
            if (c == QUOTE_SINGLE && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            
            if (c == QUOTE_DOUBLE && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            
            if (!inSingle && !inDouble && Character.isWhitespace(c)) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }
            
            current.append(c);
        }
        
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        
        return tokens.isEmpty() ? null : tokens.toArray(new String[0]);
    }
    
    /**
     * Splits a string by a delimiter respecting quotes
     */
    private List<String> splitWithQuotes(String input, String delimiter) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean escaped = false;
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if (escaped) {
                current.append(c);
                escaped = false;
                continue;
            }
            
            if (c == ESCAPE) {
                escaped = true;
                continue;
            }
            
            if (c == QUOTE_SINGLE && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            
            if (c == QUOTE_DOUBLE && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            
            if (!inSingle && !inDouble) {
                if (i + delimiter.length() <= input.length()) {
                    String substring = input.substring(i, i + delimiter.length());
                    if (substring.equals(delimiter)) {
                        result.add(current.toString().trim());
                        current.setLength(0);
                        i += delimiter.length() - 1;
                        continue;
                    }
                }
            }
            
            current.append(c);
        }
        
        if (current.length() > 0 || result.isEmpty()) {
            result.add(current.toString().trim());
        }
        
        return result;
    }
    
    // ============================================================
    // BUILT-IN COMMANDS
    // ============================================================
    
    /**
     * Registers all built-in commands
     */
    private void registerBuiltins() {
        registerBuiltin("cd", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("cd");
                try {
                    String target = args.length > 0 ? args[0] : System.getProperty("user.home");
                    Path newPath = Paths.get(resolvePath(target));
                    if (Files.exists(newPath) && Files.isDirectory(newPath)) {
                        workingDirectory = newPath.toAbsolutePath().toString();
                        System.setProperty("user.dir", workingDirectory);
                        result.setOutput("Changed directory to: " + workingDirectory);
                        if (callback != null) {
                            callback.onOutput(result.getOutput());
                        }
                    } else {
                        result.setError("Directory not found: " + target);
                        result.setSuccess(false);
                        result.setExitCode(1);
                    }
                } catch (Exception e) {
                    result.setError(e.getMessage());
                    result.setSuccess(false);
                    result.setExitCode(1);
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Change the current working directory";
            }
            
            @Override
            public String getUsage() {
                return "cd [directory]";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return getDirectoryCompletions(partial);
            }
        });
        
        registerBuiltin("pwd", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("pwd");
                result.setOutput(workingDirectory);
                if (callback != null) {
                    callback.onOutput(result.getOutput());
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Print the current working directory";
            }
            
            @Override
            public String getUsage() {
                return "pwd";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return Collections.emptyList();
            }
        });
        
        registerBuiltin("ls", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("ls");
                try {
                    boolean longFormat = false;
                    boolean showAll = false;
                    String target = ".";
                    
                    for (String arg : args) {
                        if (arg.equals("-l")) {
                            longFormat = true;
                        } else if (arg.equals("-a")) {
                            showAll = true;
                        } else if (!arg.startsWith("-")) {
                            target = arg;
                        }
                    }
                    
                    Path path = Paths.get(resolvePath(target));
                    if (!Files.exists(path)) {
                        result.setError("File or directory not found: " + target);
                        result.setSuccess(false);
                        result.setExitCode(1);
                        return result;
                    }
                    
                    if (Files.isDirectory(path)) {
                        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                            for (Path entry : stream) {
                                String name = entry.getFileName().toString();
                                if (!showAll && name.startsWith(".")) {
                                    continue;
                                }
                                if (longFormat) {
                                    String line = formatLongListing(entry);
                                    result.appendOutput(line);
                                    if (callback != null) {
                                        callback.onOutput(line);
                                    }
                                } else {
                                    result.appendOutput(name);
                                    if (callback != null) {
                                        callback.onOutput(name);
                                    }
                                }
                            }
                        }
                    } else {
                        result.appendOutput(path.getFileName().toString());
                        if (callback != null) {
                            callback.onOutput(path.getFileName().toString());
                        }
                    }
                } catch (Exception e) {
                    result.setError(e.getMessage());
                    result.setSuccess(false);
                    result.setExitCode(1);
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "List directory contents";
            }
            
            @Override
            public String getUsage() {
                return "ls [-l] [-a] [path]";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return getFileCompletions(partial);
            }
        });
        
        registerBuiltin("echo", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("echo");
                String text = String.join(" ", args);
                // Expand environment variables
                text = expandEnvironmentVariables(text);
                result.setOutput(text);
                if (callback != null) {
                    callback.onOutput(text);
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Display a line of text";
            }
            
            @Override
            public String getUsage() {
                return "echo [text...]";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return Collections.emptyList();
            }
        });
        
        registerBuiltin("alias", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("alias");
                if (args.length == 0) {
                    // List all aliases
                    for (Alias alias : aliases.values()) {
                        result.appendOutput(alias.toString());
                        if (callback != null) {
                            callback.onOutput(alias.toString());
                        }
                    }
                } else if (args.length == 1 && args[0].contains("=")) {
                    // Set alias: name=command
                    String[] parts = args[0].split("=", 2);
                    if (parts.length == 2) {
                        setAlias(parts[0].trim(), parts[1].trim());
                        result.setOutput("Alias set: " + parts[0] + " -> " + parts[1]);
                        if (callback != null) {
                            callback.onOutput(result.getOutput());
                        }
                    }
                } else if (args.length >= 2 && !args[0].contains("=")) {
                    // Remove alias: alias -r name
                    if (args[0].equals("-r") || args[0].equals("--remove")) {
                        for (int i = 1; i < args.length; i++) {
                            if (removeAlias(args[i])) {
                                result.appendOutput("Removed alias: " + args[i]);
                                if (callback != null) {
                                    callback.onOutput("Removed alias: " + args[i]);
                                }
                            }
                        }
                    } else {
                        result.setError("Usage: alias [name=command] or alias -r name");
                        result.setSuccess(false);
                        result.setExitCode(1);
                    }
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Manage command aliases";
            }
            
            @Override
            public String getUsage() {
                return "alias [name=command] or alias -r name";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                List<String> completions = new ArrayList<>();
                for (Alias alias : aliases.values()) {
                    if (alias.getName().startsWith(partial)) {
                        completions.add(alias.getName());
                    }
                }
                return completions;
            }
        });
        
        registerBuiltin("history", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("history");
                int limit = args.length > 0 ? Integer.parseInt(args[0]) : history.size();
                int start = Math.max(0, history.size() - limit);
                
                for (int i = start; i < history.size(); i++) {
                    String line = String.format("%6d  %s", i + 1, history.get(i));
                    result.appendOutput(line);
                    if (callback != null) {
                        callback.onOutput(line);
                    }
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Display command history";
            }
            
            @Override
            public String getUsage() {
                return "history [n]";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return Collections.emptyList();
            }
        });
        
        registerBuiltin("clear", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("clear");
                // Clear screen (send ANSI escape code)
                result.setOutput("\033[H\033[2J");
                if (callback != null) {
                    callback.onOutput("\033[H\033[2J");
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Clear the terminal screen";
            }
            
            @Override
            public String getUsage() {
                return "clear";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return Collections.emptyList();
            }
        });
        
        registerBuiltin("help", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("help");
                String target = args.length > 0 ? args[0] : null;
                
                if (target != null && builtins.containsKey(target.toLowerCase())) {
                    BuiltinCommand cmd = builtins.get(target.toLowerCase());
                    result.appendOutput("Command: " + target);
                    result.appendOutput("Description: " + cmd.getDescription());
                    result.appendOutput("Usage: " + cmd.getUsage());
                    if (callback != null) {
                        callback.onOutput("Command: " + target);
                        callback.onOutput("Description: " + cmd.getDescription());
                        callback.onOutput("Usage: " + cmd.getUsage());
                    }
                } else {
                    result.appendOutput("Available built-in commands:");
                    for (Map.Entry<String, BuiltinCommand> entry : builtins.entrySet()) {
                        result.appendOutput(String.format("  %-10s - %s", 
                            entry.getKey(), entry.getValue().getDescription()));
                        if (callback != null) {
                            callback.onOutput(String.format("  %-10s - %s", 
                                entry.getKey(), entry.getValue().getDescription()));
                        }
                    }
                    result.appendOutput("\nFor help on a specific command: help <command>");
                    if (callback != null) {
                        callback.onOutput("\nFor help on a specific command: help <command>");
                    }
                }
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Display help for commands";
            }
            
            @Override
            public String getUsage() {
                return "help [command]";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                List<String> completions = new ArrayList<>();
                for (String cmd : builtins.keySet()) {
                    if (cmd.startsWith(partial)) {
                        completions.add(cmd);
                    }
                }
                return completions;
            }
        });
        
        registerBuiltin("exit", new BuiltinCommand() {
            @Override
            public CommandResult execute(String[] args, CommandProcessor processor) {
                CommandResult result = new CommandResult("exit");
                int code = args.length > 0 ? Integer.parseInt(args[0]) : 0;
                result.setExitCode(code);
                if (callback != null) {
                    callback.onCommandComplete(result);
                }
                System.exit(code);
                return result;
            }
            
            @Override
            public String getDescription() {
                return "Exit the terminal";
            }
            
            @Override
            public String getUsage() {
                return "exit [code]";
            }
            
            @Override
            public List<String> getCompletions(String partial) {
                return Collections.emptyList();
            }
        });
    }
    
    /**
     * Registers a built-in command
     */
    public void registerBuiltin(String name, BuiltinCommand command) {
        builtins.put(name.toLowerCase(), command);
    }
    
    /**
     * Unregisters a built-in command
     */
    public void unregisterBuiltin(String name) {
        builtins.remove(name.toLowerCase());
    }
    
    // ============================================================
    // ALIAS MANAGEMENT
    // ============================================================
    
    /**
     * Sets a command alias
     */
    public void setAlias(String name, String command) {
        aliases.put(name.toLowerCase(), new Alias(name, command));
        saveAliases();
    }
    
    /**
     * Sets an alias with description
     */
    public void setAlias(String name, String command, String description) {
        aliases.put(name.toLowerCase(), new Alias(name, command, description));
        saveAliases();
    }
    
    /**
     * Gets an alias
     */
    public Alias getAlias(String name) {
        return aliases.get(name.toLowerCase());
    }
    
    /**
     * Removes an alias
     */
    public boolean removeAlias(String name) {
        boolean removed = aliases.remove(name.toLowerCase()) != null;
        if (removed) {
            saveAliases();
        }
        return removed;
    }
    
    /**
     * Gets all aliases
     */
    public Map<String, Alias> getAliases() {
        return new HashMap<>(aliases);
    }
    
    /**
     * Expands aliases in a command
     */
    private String expandAliases(String command) {
        if (command == null || command.isEmpty()) {
            return command;
        }
        
        String[] parts = command.split("\\s+");
        if (parts.length == 0) {
            return command;
        }
        
        Alias alias = aliases.get(parts[0].toLowerCase());
        if (alias != null) {
            String expanded = alias.getCommand();
            if (parts.length > 1) {
                expanded += " " + String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
            }
            return expanded;
        }
        
        return command;
    }
    
    // ============================================================
    // HISTORY MANAGEMENT
    // ============================================================
    
    /**
     * Adds a command to history
     */
    private void addToHistory(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        history.add(command);
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
        historyIndex = history.size();
    }
    
    /**
     * Gets the previous command from history
     */
    public String getPreviousCommand() {
        if (historyIndex > 0) {
            historyIndex--;
            return history.get(historyIndex);
        }
        return null;
    }
    
    /**
     * Gets the next command from history
     */
    public String getNextCommand() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            return history.get(historyIndex);
        }
        return null;
    }
    
    /**
     * Resets history navigation
     */
    public void resetHistoryNavigation() {
        historyIndex = history.size();
    }
    
    /**
     * Searches history for commands containing text
     */
    public List<String> searchHistory(String searchText) {
        List<String> results = new ArrayList<>();
        if (searchText == null || searchText.isEmpty()) {
            return new ArrayList<>(history);
        }
        for (String cmd : history) {
            if (cmd.toLowerCase().contains(searchText.toLowerCase())) {
                results.add(cmd);
            }
        }
        return results;
    }
    
    // ============================================================
    // ENVIRONMENT MANAGEMENT
    // ============================================================
    
    /**
     * Sets an environment variable
     */
    public void setEnvironmentVariable(String key, String value) {
        environment.put(key, value);
    }
    
    /**
     * Gets an environment variable
     */
    public String getEnvironmentVariable(String key) {
        return environment.get(key);
    }
    
    /**
     * Gets all environment variables
     */
    public Map<String, String> getEnvironment() {
        return new HashMap<>(environment);
    }
    
    /**
     * Expands environment variables in text
     */
    private String expandEnvironmentVariables(String text) {
        if (text == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\$\\{?([a-zA-Z_][a-zA-Z0-9_]*)\\}?");
        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String varName = matcher.group(1);
            String value = environment.get(varName);
            if (value == null) {
                value = System.getenv(varName);
            }
            if (value != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
    
    // ============================================================
    // PATH RESOLUTION
    // ============================================================
    
    /**
     * Resolves a path relative to the current working directory
     */
    public String resolvePath(String path) {
        if (path == null || path.isEmpty()) {
            return workingDirectory;
        }
        
        // Expand environment variables
        path = expandEnvironmentVariables(path);
        
        // Resolve home directory
        if (path.startsWith("~")) {
            String home = System.getProperty("user.home");
            if (path.length() == 1) {
                return home;
            }
            return home + path.substring(1);
        }
        
        Path p = Paths.get(path);
        if (p.isAbsolute()) {
            return p.toString();
        }
        
        return Paths.get(workingDirectory, path).toString();
    }
    
    // ============================================================
    // AUTO-COMPLETION
    // ============================================================
    
    /**
     * Gets completion suggestions for a partial command
     */
    public List<Suggestion> getCompletions(String partial) {
        List<Suggestion> suggestions = new ArrayList<>();
        
        if (partial == null || partial.isEmpty()) {
            return suggestions;
        }
        
        String[] parts = partial.split("\\s+");
        if (parts.length == 1) {
            // Command completion
            String cmd = parts[0];
            
            // Built-in commands
            for (String builtin : builtins.keySet()) {
                if (builtin.startsWith(cmd) && !builtin.equals(cmd)) {
                    suggestions.add(new Suggestion(
                        builtin,
                        builtins.get(builtin).getDescription(),
                        "builtin"
                    ));
                }
            }
            
            // Aliases
            for (Alias alias : aliases.values()) {
                if (alias.getName().startsWith(cmd)) {
                    suggestions.add(new Suggestion(
                        alias.getName(),
                        alias.getDescription() != null ? alias.getDescription() : "Alias for: " + alias.getCommand(),
                        "alias"
                    ));
                }
            }
            
            // External commands (from PATH)
            for (String path : environment.getOrDefault("PATH", "").split(File.pathSeparator)) {
                File dir = new File(path);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles((d, name) -> name.startsWith(cmd) && d.canExecute());
                    if (files != null) {
                        for (File file : files) {
                            suggestions.add(new Suggestion(
                                file.getName(),
                                "External command",
                                "external"
                            ));
                        }
                    }
                }
            }
            
        } else {
            // Argument completion
            String cmd = parts[0];
            String arg = parts[parts.length - 1];
            
            BuiltinCommand builtin = builtins.get(cmd.toLowerCase());
            if (builtin != null) {
                List<String> completions = builtin.getCompletions(arg);
                for (String completion : completions) {
                    suggestions.add(new Suggestion(
                        completion,
                        "Argument completion",
                        "argument"
                    ));
                }
            }
            
            // File completion
            if (arg != null && !arg.isEmpty()) {
                String dir = arg.contains("/") ? arg.substring(0, arg.lastIndexOf('/') + 1) : "";
                String prefix = arg.contains("/") ? arg.substring(arg.lastIndexOf('/') + 1) : arg;
                
                String searchDir = resolvePath(dir);
                File directory = new File(searchDir);
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles((d, name) -> name.startsWith(prefix));
                    if (files != null) {
                        for (File file : files) {
                            String displayName = file.isDirectory() ? file.getName() + "/" : file.getName();
                            String fullName = dir + file.getName() + (file.isDirectory() ? "/" : "");
                            suggestions.add(new Suggestion(
                                fullName,
                                file.isDirectory() ? "Directory" : "File",
                                file.isDirectory() ? "directory" : "file"
                            ));
                        }
                    }
                }
            }
        }
        
        // Sort by score
        suggestions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return suggestions;
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Creates an error result
     */
    private CommandResult errorResult(String message) {
        CommandResult result = new CommandResult("");
        result.setError(message);
        result.setSuccess(false);
        result.setExitCode(1);
        return result;
    }
    
    /**
     * Formats a long listing entry
     */
    private String formatLongListing(Path path) throws IOException {
        File file = path.toFile();
        String permissions = "";
        permissions += file.isDirectory() ? "d" : "-";
        permissions += file.canRead() ? "r" : "-";
        permissions += file.canWrite() ? "w" : "-";
        permissions += file.canExecute() ? "x" : "-";
        permissions += "r-xr-x"; // Simplified
        permissions += "    ";
        
        long size = file.length();
        String sizeStr = String.format("%8d", size);
        String modified = new java.text.SimpleDateFormat("MMM dd HH:mm").format(
            new Date(file.lastModified())
        );
        
        return String.format("%s 1 %-8s %-8s %s %s %s",
            permissions,
            System.getProperty("user.name"),
            System.getProperty("user.name"),
            sizeStr,
            modified,
            file.getName()
        );
    }
    
    /**
     * Gets directory completions
     */
    private List<String> getDirectoryCompletions(String partial) {
        List<String> completions = new ArrayList<>();
        String dir = partial.contains("/") ? partial.substring(0, partial.lastIndexOf('/') + 1) : "";
        String prefix = partial.contains("/") ? partial.substring(partial.lastIndexOf('/') + 1) : partial;
        
        File directory = new File(resolvePath(dir));
        if (directory.exists() && directory.isDirectory()) {
            File[] dirs = directory.listFiles((d, name) -> 
                name.startsWith(prefix) && d.isDirectory());
            if (dirs != null) {
                for (File dirFile : dirs) {
                    completions.add(dir + dirFile.getName());
                }
            }
        }
        return completions;
    }
    
    /**
     * Gets file completions
     */
    private List<String> getFileCompletions(String partial) {
        List<String> completions = new ArrayList<>();
        String dir = partial.contains("/") ? partial.substring(0, partial.lastIndexOf('/') + 1) : "";
        String prefix = partial.contains("/") ? partial.substring(partial.lastIndexOf('/') + 1) : partial;
        
        File directory = new File(resolvePath(dir));
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((d, name) -> name.startsWith(prefix));
            if (files != null) {
                for (File file : files) {
                    completions.add(dir + file.getName());
                }
            }
        }
        return completions;
    }
    
    // ============================================================
    // PERSISTENCE
    // ============================================================
    
    /**
     * Saves aliases to file
     */
    private void saveAliases() {
        try {
            File aliasFile = new File(System.getProperty("user.home"), ".neilos_aliases");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(aliasFile))) {
                for (Alias alias : aliases.values()) {
                    writer.write(alias.getName() + "=" + alias.getCommand());
                    if (alias.getDescription() != null) {
                        writer.write(" # " + alias.getDescription());
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    /**
     * Loads aliases from file
     */
    private void loadAliases() {
        try {
            File aliasFile = new File(System.getProperty("user.home"), ".neilos_aliases");
            if (!aliasFile.exists()) {
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(aliasFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String name = parts[0].trim();
                        String command = parts[1].trim();
                        String description = null;
                        
                        int commentIndex = command.indexOf("#");
                        if (commentIndex >= 0) {
                            description = command.substring(commentIndex + 1).trim();
                            command = command.substring(0, commentIndex).trim();
                        }
                        
                        setAlias(name, command, description);
                    }
                }
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { 
        this.prompt = prompt;
        if (callback != null) {
            callback.onPromptChanged(prompt);
        }
    }
    
    public String getWorkingDirectory() { return workingDirectory; }
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        System.setProperty("user.dir", workingDirectory);
    }
    
    public boolean isVerbose() { return verbose; }
    public void setVerbose(boolean verbose) { this.verbose = verbose; }
    
    public boolean isStrictMode() { return strictMode; }
    public void setStrictMode(boolean strictMode) { this.strictMode = strictMode; }
    
    public CommandCallback getCallback() { return callback; }
    public void setCallback(CommandCallback callback) { this.callback = callback; }
    
    public TerminalHistory getTerminalHistory() { return terminalHistory; }
    public void setTerminalHistory(TerminalHistory terminalHistory) { 
        this.terminalHistory = terminalHistory; 
    }
    
    public List<String> getHistory() { return new ArrayList<>(history); }
    public int getHistorySize() { return history.size(); }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of CommandProcessor
     */
    public static void main(String[] args) {
        System.out.println("⌨️ CommandProcessor Demo");
        System.out.println("═".repeat(60));
        
        CommandProcessor processor = new CommandProcessor();
        
        // Set callback
        processor.setCallback(new CommandCallback() {
            @Override
            public void onCommandStart(String command) {
                System.out.println("▶️ Executing: " + command);
            }
            
            @Override
            public void onCommandComplete(CommandResult result) {
                System.out.println("✅ Completed in " + result.getExecutionTime() + "ms");
            }
            
            @Override
            public void onCommandError(String command, String error) {
                System.err.println("❌ Error: " + error);
            }
            
            @Override
            public void onOutput(String line) {
                // Output is already shown
            }
            
            @Override
            public void onPromptChanged(String prompt) {
                System.out.println("📌 Prompt changed to: " + prompt);
            }
        });
        
        // Test built-in commands
        System.out.println("\n📝 Testing built-in commands:");
        processor.execute("pwd");
        processor.execute("ls -la");
        processor.execute("echo Hello, World!");
        
        // Test aliases
        System.out.println("\n📝 Testing aliases:");
        processor.setAlias("ll", "ls -l", "Long listing");
        processor.execute("ll");
        processor.execute("alias");
        
        // Test environment variables
        System.out.println("\n📝 Testing environment variables:");
        processor.setEnvironmentVariable("TEST", "Hello");
        processor.execute("echo $TEST World!");
        
        // Test history
        System.out.println("\n📝 Testing history:");
        processor.execute("history");
        
        // Test completion
        System.out.println("\n📝 Testing auto-completion:");
        List<Suggestion> completions = processor.getCompletions("e");
        for (Suggestion suggestion : completions) {
            System.out.println("  " + suggestion);
        }
        
        // Test pipe (simplified)
        System.out.println("\n📝 Testing pipe:");
        processor.execute("echo 'Hello World' | echo 'Pipe test'");
        
        System.out.println("\n✅ Demo completed!");
    }
}