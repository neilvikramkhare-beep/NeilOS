package com.NEILOSULTIMATE.NEILOSULTIMATE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NeilOSServer {

    public static void main(String[] args) {
        SpringApplication.run(NeilOSServer.class, args);
    }

    // =========================
    // IN-MEMORY DATABASES
    // =========================

    private final Map<String, Integer> users =
            new ConcurrentHashMap<>();

    private final List<String> files =
            Collections.synchronizedList(new ArrayList<>());

    private final List<Map<String, Object>> patients =
            Collections.synchronizedList(new ArrayList<>());

    // =========================
    // ROOT
    // =========================

    @GetMapping("/")
    public Map<String, Object> root() {

        Map<String, Object> res = new HashMap<>();

        res.put("system", "NeilOS");
        res.put("status", "running");

        return res;
    }

    // =========================
    // BANKING
    // =========================

    @GetMapping("/bank/{user}")
    public Map<String, Object> balance(
            @PathVariable String user) {

        users.putIfAbsent(user, 0);

        Map<String, Object> res = new HashMap<>();

        res.put("user", user);
        res.put("balance", users.get(user));

        return res;
    }

    @PostMapping("/deposit")
    public Map<String, Object> deposit(
            @RequestBody Map<String, Object> req) {

        String user =
                (String) req.get("user");

        int amount =
                ((Number) req.get("amount"))
                        .intValue();

        users.putIfAbsent(user, 0);

        users.put(
                user,
                users.get(user) + amount
        );

        return Map.of(
                "status", "deposited",
                "balance", users.get(user)
        );
    }

    @PostMapping("/withdraw")
    public Map<String, Object> withdraw(
            @RequestBody Map<String, Object> req) {

        String user =
                (String) req.get("user");

        int amount =
                ((Number) req.get("amount"))
                        .intValue();

        users.putIfAbsent(user, 0);

        int balance = users.get(user);

        if (balance < amount) {

            return Map.of(
                    "error",
                    "insufficient balance"
            );
        }

        users.put(
                user,
                balance - amount
        );

        return Map.of(
                "status", "withdrawn",
                "balance", users.get(user)
        );
    }

    // =========================
    // FILE MANAGER
    // =========================

    @GetMapping("/files")
    public Map<String, Object> getFiles() {

        return Map.of(
                "files",
                files
        );
    }

    @PostMapping("/files/create")
    public Map<String, Object> createFile(
            @RequestBody Map<String, Object> req) {

        String filename =
                (String) req.get("filename");

        files.add(filename);

        return Map.of(
                "status",
                "created"
        );
    }

    // =========================
    // TERMINAL
    // =========================

    @PostMapping("/terminal")
    public Map<String, Object> terminal(
            @RequestBody Map<String, Object> req) {

        String cmd =
                ((String) req.get("command"))
                        .toLowerCase();

        String output;

        switch (cmd) {

            case "help":
                output =
                        "help dir files version clear";
                break;

            case "dir":
            case "files":
                output =
                        String.join("\n", files);
                break;

            case "version":
                output =
                        "NeilOS Java Backend v1.0";
                break;

            case "clear":
                output = "";
                break;

            default:
                output =
                        "Unknown command";
        }

        return Map.of(
                "output",
                output
        );
    }

    // =========================
    // AI ASSISTANT
    // =========================

    @PostMapping("/ai")
    public Map<String, Object> ai(
            @RequestBody Map<String, Object> req) {

        String message =
                ((String) req.get("message"))
                        .toLowerCase();

        String response = "AI Ready";

        if (message.contains("hello"))
            response = "Hello User";

        if (message.contains("scan"))
            response =
                    "Opening Cyber Security Center";

        if (message.contains("bank"))
            response =
                    "Opening Banking System";

        return Map.of(
                "response",
                response
        );
    }

    // =========================
    // CYBER SECURITY
    // =========================

    @PostMapping("/cyber/scan")
    public Map<String, Object> scan(
            @RequestBody Map<String, Object> req) {

        String ip =
                (String) req.get("ip");

        return Map.of(
                "target", ip,
                "firewall", "active",
                "ports",
                Arrays.asList(22, 80, 443),
                "threatLevel", "low",
                "status", "secure"
        );
    }

    @GetMapping("/cyber/threats")
    public Map<String, Object> threats() {

        return Map.of(
                "level", "low",
                "attacks", 0,
                "firewall", "enabled"
        );
    }

    // =========================
    // CLINIC DATABASE
    // =========================

    @PostMapping("/clinic/add")
    public Map<String, Object> addPatient(
            @RequestBody Map<String, Object> req) {

        patients.add(req);

        return Map.of(
                "status",
                "patient added"
        );
    }

    @GetMapping("/clinic/list")
    public List<Map<String, Object>> clinicList() {

        return patients;
    }

    // =========================
    // SOCIALNET
    // =========================

    @GetMapping("/social/groups")
    public Map<String, Object> groups() {

        return Map.of(
                "groups",
                Arrays.asList(
                        Arrays.asList(1),
                        Arrays.asList(0, 2),
                        Arrays.asList(1)
                )
        );
    }

    @GetMapping("/social/recommend")
    public Map<String, Object> recommend() {

        return Map.of(
                "user",
                "cyber_user"
        );
    }

    // =========================
    // NETWORK
    // =========================

    @GetMapping("/network")
    public Map<String, Object> network()
            throws Exception {

        String hostname =
                InetAddress.getLocalHost()
                        .getHostName();

        String ip =
                InetAddress.getLocalHost()
                        .getHostAddress();

        return Map.of(
                "hostname", hostname,
                "ip", ip,
                "gateway", "active",
                "dns", "connected"
        );
    }

    // =========================
    // DEPLOYMENT
    // =========================

    @PostMapping("/deploy")
    public Map<String, Object> deploy() {

        return Map.of(
                "status",
                "ISO generation started",
                "file",
                "NeilOS.iso"
        );
    }

    // =========================
    // KERNEL
    // =========================

    @GetMapping("/kernel")
    public Map<String, Object> kernel() {

        return Map.of(
                "kernel",
                "NeilOS Kernel v1.0",
                "arch",
                System.getProperty("os.arch"),
                "os",
                System.getProperty("os.name")
        );
    }

    // =========================
    // SYSTEM MONITOR
    // =========================

    @GetMapping("/monitor")
    public Map<String, Object> monitor() {

        Runtime runtime =
                Runtime.getRuntime();

        long free =
                runtime.freeMemory();

        long total =
                runtime.totalMemory();

        long uptime =
                ManagementFactory
                        .getRuntimeMXBean()
                        .getUptime();

        return Map.of(
                "uptimeMs", uptime,
                "freeMemory", free,
                "totalMemory", total,
                "processors",
                runtime.availableProcessors()
        );
    }

    // =========================
    // API STATUS
    // =========================

    @GetMapping("/status")
    public Map<String, Object> status() {

        return Map.of(
                "status", "online",
                "message",
                "NeilOS API Running"
        );
    }
}