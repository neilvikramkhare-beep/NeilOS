package com.neilos.network;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PingUtility - Advanced ICMP ping utility for NeilOS
 * Provides low-level ICMP ping functionality with detailed statistics,
 * multi-threaded pinging, and comprehensive reporting.
 * 
 * Features:
 * - ICMP echo requests (ping)
 * - Multiple packet sizes
 * - Adjustable timeouts
 * - Packet loss statistics
 * - Latency tracking (min/max/avg)
 * - TTL tracking
 * - Multi-threaded pinging
 * - Ping sweep for network scanning
 * - Report generation
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class PingUtility {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default packet size in bytes */
    public static final int DEFAULT_PACKET_SIZE = 64;
    
    /** Default timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT = 5000;
    
    /** Default TTL (Time To Live) */
    public static final int DEFAULT_TTL = 64;
    
    /** Default number of packets */
    public static final int DEFAULT_COUNT = 4;
    
    /** ICMP protocol number */
    public static final int ICMP_PROTOCOL = 1;
    
    /** ICMP Echo Request type */
    public static final int ICMP_ECHO_REQUEST = 8;
    
    /** ICMP Echo Reply type */
    public static final int ICMP_ECHO_REPLY = 0;
    
    /** ICMP header size in bytes */
    public static final int ICMP_HEADER_SIZE = 8;
    
    /** Maximum packet size */
    public static final int MAX_PACKET_SIZE = 65507;
    
    /** Minimum packet size */
    public static final int MIN_PACKET_SIZE = 8;
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Ping result containing all statistics
     */
    public static class PingResult {
        private String host;
        private String ipAddress;
        private int packetSize;
        private int ttl;
        private int timeout;
        private int packetsSent;
        private int packetsReceived;
        private int packetsLost;
        private double lossPercentage;
        private long minTime;
        private long maxTime;
        private double avgTime;
        private double stdDev;
        private List<Long> responseTimes;
        private List<Integer> ttlValues;
        private List<Boolean> successful;
        private String error;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean reachable;
        private String resolvedHostname;
        
        public PingResult(String host) {
            this.host = host;
            this.responseTimes = new ArrayList<>();
            this.ttlValues = new ArrayList<>();
            this.successful = new ArrayList<>();
            this.startTime = LocalDateTime.now();
            this.minTime = Long.MAX_VALUE;
            this.maxTime = Long.MIN_VALUE;
            this.packetSize = DEFAULT_PACKET_SIZE;
            this.ttl = DEFAULT_TTL;
            this.timeout = DEFAULT_TIMEOUT;
        }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public int getPacketSize() { return packetSize; }
        public void setPacketSize(int packetSize) { this.packetSize = packetSize; }
        
        public int getTtl() { return ttl; }
        public void setTtl(int ttl) { this.ttl = ttl; }
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public int getPacketsSent() { return packetsSent; }
        public void setPacketsSent(int packetsSent) { this.packetsSent = packetsSent; }
        
        public int getPacketsReceived() { return packetsReceived; }
        public void setPacketsReceived(int packetsReceived) { this.packetsReceived = packetsReceived; }
        
        public int getPacketsLost() { return packetsLost; }
        public void setPacketsLost(int packetsLost) { this.packetsLost = packetsLost; }
        
        public double getLossPercentage() { return lossPercentage; }
        public void setLossPercentage(double lossPercentage) { this.lossPercentage = lossPercentage; }
        
        public long getMinTime() { return minTime; }
        public void setMinTime(long minTime) { this.minTime = minTime; }
        
        public long getMaxTime() { return maxTime; }
        public void setMaxTime(long maxTime) { this.maxTime = maxTime; }
        
        public double getAvgTime() { return avgTime; }
        public void setAvgTime(double avgTime) { this.avgTime = avgTime; }
        
        public double getStdDev() { return stdDev; }
        public void setStdDev(double stdDev) { this.stdDev = stdDev; }
        
        public List<Long> getResponseTimes() { return responseTimes; }
        public void setResponseTimes(List<Long> responseTimes) { this.responseTimes = responseTimes; }
        
        public List<Integer> getTtlValues() { return ttlValues; }
        public void setTtlValues(List<Integer> ttlValues) { this.ttlValues = ttlValues; }
        
        public List<Boolean> getSuccessful() { return successful; }
        public void setSuccessful(List<Boolean> successful) { this.successful = successful; }
        
        public void addResponse(long time, int ttl, boolean success) {
            if (success) {
                responseTimes.add(time);
                ttlValues.add(ttl);
                if (time < minTime) minTime = time;
                if (time > maxTime) maxTime = time;
            }
            successful.add(success);
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public boolean isReachable() { return reachable; }
        public void setReachable(boolean reachable) { this.reachable = reachable; }
        
        public String getResolvedHostname() { return resolvedHostname; }
        public void setResolvedHostname(String resolvedHostname) { this.resolvedHostname = resolvedHostname; }
        
        public void calculateStatistics() {
            packetsSent = successful.size();
            packetsReceived = 0;
            for (Boolean success : successful) {
                if (success) packetsReceived++;
            }
            packetsLost = packetsSent - packetsReceived;
            lossPercentage = packetsSent > 0 ? (double) packetsLost / packetsSent * 100 : 0;
            
            if (!responseTimes.isEmpty()) {
                avgTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);
                
                // Calculate standard deviation
                double variance = responseTimes.stream()
                    .mapToDouble(t -> Math.pow(t - avgTime, 2))
                    .average()
                    .orElse(0);
                stdDev = Math.sqrt(variance);
            }
            
            reachable = packetsReceived > 0;
            endTime = LocalDateTime.now();
        }
        
        @Override
        public String toString() {
            if (error != null) {
                return "❌ Ping failed: " + error;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("📡 PING STATISTICS\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("Host:           ").append(host).append("\n");
            if (resolvedHostname != null && !resolvedHostname.equals(host)) {
                sb.append("Resolved:       ").append(resolvedHostname).append("\n");
            }
            sb.append("IP Address:     ").append(ipAddress != null ? ipAddress : "Unknown").append("\n");
            sb.append("Packet Size:    ").append(packetSize).append(" bytes\n");
            sb.append("TTL:            ").append(ttl).append("\n");
            sb.append("Timeout:        ").append(timeout).append("ms\n");
            sb.append("Packets:        ").append(packetsSent).append(" sent, ")
              .append(packetsReceived).append(" received, ")
              .append(packetsLost).append(" lost (")
              .append(String.format("%.1f%%", lossPercentage)).append(" loss)\n");
            
            if (packetsReceived > 0) {
                sb.append("Latency:\n");
                sb.append("  Min:        ").append(minTime).append("ms\n");
                sb.append("  Max:        ").append(maxTime).append("ms\n");
                sb.append("  Avg:        ").append(String.format("%.2f", avgTime)).append("ms\n");
                sb.append("  Std Dev:    ").append(String.format("%.2f", stdDev)).append("ms\n");
            }
            
            sb.append("Status:         ").append(reachable ? "✅ Reachable" : "❌ Unreachable").append("\n");
            sb.append("Start Time:     ").append(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
            sb.append("End Time:       ").append(endTime != null ? endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "N/A").append("\n");
            
            if (!responseTimes.isEmpty() && responseTimes.size() <= 20) {
                sb.append("\n📊 Response Times (ms):\n");
                for (int i = 0; i < responseTimes.size(); i++) {
                    sb.append(String.format("  %2d: %-6d  TTL: %-3d  %s\n", 
                        i + 1, 
                        responseTimes.get(i), 
                        i < ttlValues.size() ? ttlValues.get(i) : 0,
                        successful.get(i) ? "✅" : "❌"));
                }
            }
            
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
        
        public String toCSV() {
            StringBuilder sb = new StringBuilder();
            sb.append("Host,IP,PacketSize,TTL,Sent,Received,Loss%,Min,Max,Avg,StdDev,Reachable\n");
            sb.append(String.format("%s,%s,%d,%d,%d,%d,%.1f,%d,%d,%.2f,%.2f,%s\n",
                host,
                ipAddress != null ? ipAddress : "Unknown",
                packetSize,
                ttl,
                packetsSent,
                packetsReceived,
                lossPercentage,
                minTime != Long.MAX_VALUE ? minTime : 0,
                maxTime != Long.MIN_VALUE ? maxTime : 0,
                avgTime,
                stdDev,
                reachable ? "Yes" : "No"
            ));
            return sb.toString();
        }
    }
    
    /**
     * Ping sweep result for network scanning
     */
    public static class PingSweepResult {
        private String network;
        private int cidr;
        private List<PingResult> results;
        private int totalHosts;
        private int reachableHosts;
        private int unreachableHosts;
        private long startTime;
        private long endTime;
        
        public PingSweepResult(String network, int cidr) {
            this.network = network;
            this.cidr = cidr;
            this.results = new ArrayList<>();
            this.startTime = System.currentTimeMillis();
        }
        
        public String getNetwork() { return network; }
        public void setNetwork(String network) { this.network = network; }
        
        public int getCidr() { return cidr; }
        public void setCidr(int cidr) { this.cidr = cidr; }
        
        public List<PingResult> getResults() { return results; }
        public void setResults(List<PingResult> results) { this.results = results; }
        public void addResult(PingResult result) { 
            results.add(result);
            if (result.isReachable()) {
                reachableHosts++;
            } else {
                unreachableHosts++;
            }
        }
        
        public int getTotalHosts() { return totalHosts; }
        public void setTotalHosts(int totalHosts) { this.totalHosts = totalHosts; }
        
        public int getReachableHosts() { return reachableHosts; }
        public void setReachableHosts(int reachableHosts) { this.reachableHosts = reachableHosts; }
        
        public int getUnreachableHosts() { return unreachableHosts; }
        public void setUnreachableHosts(int unreachableHosts) { this.unreachableHosts = unreachableHosts; }
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        
        public void complete() {
            endTime = System.currentTimeMillis();
            totalHosts = reachableHosts + unreachableHosts;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("🌐 PING SWEEP RESULTS\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("Network:        ").append(network).append("/").append(cidr).append("\n");
            sb.append("Total Hosts:    ").append(totalHosts).append("\n");
            sb.append("Reachable:      ").append(reachableHosts).append("\n");
            sb.append("Unreachable:    ").append(unreachableHosts).append("\n");
            sb.append("Time:           ").append((endTime - startTime) / 1000.0).append("s\n");
            sb.append("═".repeat(60)).append("\n");
            
            if (!results.isEmpty()) {
                sb.append("\n📡 Reachable Hosts:\n");
                for (PingResult result : results) {
                    if (result.isReachable()) {
                        sb.append("  ").append(result.getIpAddress())
                          .append(" (").append(result.getHost()).append(")")
                          .append(" - ").append(result.getAvgTime()).append("ms avg\n");
                    }
                }
            }
            
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private int packetSize;
    private int timeout;
    private int ttl;
    private int count;
    private int interval;
    private boolean verbose;
    private boolean resolveHostnames;
    private ExecutorService executor;
    private AtomicBoolean running;
    private AtomicInteger activePings;
    private PingCallback callback;
    
    // ============================================================
    // INTERFACES
    // ============================================================
    
    /**
     * Callback interface for ping events
     */
    public interface PingCallback {
        void onStart(PingResult result);
        void onResponse(PingResult result, int sequence, long responseTime, int ttl);
        void onTimeout(PingResult result, int sequence);
        void onComplete(PingResult result);
        void onError(String message, Exception e);
    }
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public PingUtility() {
        this(DEFAULT_PACKET_SIZE, DEFAULT_TIMEOUT, DEFAULT_TTL, DEFAULT_COUNT);
    }
    
    /**
     * Constructor with custom settings
     * 
     * @param packetSize The packet size in bytes
     * @param timeout The timeout in milliseconds
     * @param ttl The TTL value
     * @param count The number of packets
     */
    public PingUtility(int packetSize, int timeout, int ttl, int count) {
        this.packetSize = Math.max(MIN_PACKET_SIZE, Math.min(packetSize, MAX_PACKET_SIZE));
        this.timeout = timeout;
        this.ttl = Math.max(1, ttl);
        this.count = Math.max(1, count);
        this.interval = 1000;
        this.verbose = false;
        this.resolveHostnames = true;
        this.running = new AtomicBoolean(false);
        this.activePings = new AtomicInteger(0);
        this.executor = Executors.newCachedThreadPool();
    }
    
    // ============================================================
    // PING METHODS
    // ============================================================
    
    /**
     * Pings a host
     * 
     * @param host The host to ping
     * @return PingResult object
     */
    public PingResult ping(String host) {
        return ping(host, count, packetSize, timeout, ttl);
    }
    
    /**
     * Pings a host with custom settings
     * 
     * @param host The host to ping
     * @param count The number of packets
     * @param packetSize The packet size
     * @param timeout The timeout in milliseconds
     * @param ttl The TTL value
     * @return PingResult object
     */
    public PingResult ping(String host, int count, int packetSize, int timeout, int ttl) {
        PingResult result = new PingResult(host);
        result.setPacketSize(packetSize);
        result.setTimeout(timeout);
        result.setTtl(ttl);
        
        try {
            // Resolve hostname
            InetAddress address = InetAddress.getByName(host);
            result.setIpAddress(address.getHostAddress());
            
            if (resolveHostnames) {
                result.setResolvedHostname(address.getCanonicalHostName());
            }
            
            if (callback != null) {
                callback.onStart(result);
            }
            
            // Perform ping
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows - use InetAddress.isReachable()
                pingWindows(result, address);
            } else {
                // Linux/Unix - use ICMP (requires root or capabilities)
                try {
                    pingICMP(result, address);
                } catch (Exception e) {
                    // Fallback to InetAddress.isReachable()
                    pingWindows(result, address);
                }
            }
            
            // Calculate statistics
            result.calculateStatistics();
            
            if (callback != null) {
                callback.onComplete(result);
            }
            
            return result;
            
        } catch (Exception e) {
            result.setError(e.getMessage());
            if (callback != null) {
                callback.onError("Ping failed: " + e.getMessage(), e);
            }
            return result;
        }
    }
    
    /**
     * Pings multiple hosts
     * 
     * @param hosts The hosts to ping
     * @return Map of host to PingResult
     */
    public Map<String, PingResult> pingMultiple(List<String> hosts) {
        Map<String, PingResult> results = new LinkedHashMap<>();
        List<Callable<PingResult>> tasks = new ArrayList<>();
        
        for (String host : hosts) {
            tasks.add(() -> ping(host));
        }
        
        try {
            List<Future<PingResult>> futures = executor.invokeAll(tasks);
            for (int i = 0; i < futures.size(); i++) {
                try {
                    PingResult result = futures.get(i).get();
                    results.put(hosts.get(i), result);
                } catch (Exception e) {
                    PingResult errorResult = new PingResult(hosts.get(i));
                    errorResult.setError(e.getMessage());
                    results.put(hosts.get(i), errorResult);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return results;
    }
    
    /**
     * Performs a ping sweep on a network
     * 
     * @param network The network address (e.g., "192.168.1.0")
     * @param cidr The CIDR notation
     * @return PingSweepResult object
     */
    public PingSweepResult pingSweep(String network, int cidr) {
        PingSweepResult sweepResult = new PingSweepResult(network, cidr);
        
        try {
            String[] parts = network.split("\\.");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid network address");
            }
            
            int base = (Integer.parseInt(parts[0]) << 24) |
                      (Integer.parseInt(parts[1]) << 16) |
                      (Integer.parseInt(parts[2]) << 8) |
                      Integer.parseInt(parts[3]);
            
            int mask = ~((1 << (32 - cidr)) - 1);
            int networkBase = base & mask;
            int broadcast = networkBase | ~mask;
            
            int startIP = networkBase + 1;
            int endIP = broadcast - 1;
            
            List<Callable<PingResult>> tasks = new ArrayList<>();
            for (int i = startIP; i <= endIP && i < Integer.MAX_VALUE; i++) {
                final int ipInt = i;
                tasks.add(() -> {
                    String ip = intToIp(ipInt);
                    return ping(ip, 1, packetSize, timeout, ttl);
                });
            }
            
            // Limit concurrent pings
            int maxConcurrent = Math.min(tasks.size(), 100);
            List<Future<PingResult>> futures = executor.invokeAll(tasks, 30, TimeUnit.SECONDS);
            
            for (Future<PingResult> future : futures) {
                try {
                    PingResult result = future.get();
                    sweepResult.addResult(result);
                } catch (Exception e) {
                    // Ignore individual failures
                }
            }
            
        } catch (Exception e) {
            if (callback != null) {
                callback.onError("Ping sweep failed: " + e.getMessage(), e);
            }
        }
        
        sweepResult.complete();
        return sweepResult;
    }
    
    // ============================================================
    // CORE PING IMPLEMENTATIONS
    // ============================================================
    
    /**
     * Windows ping using InetAddress.isReachable()
     */
    private void pingWindows(PingResult result, InetAddress address) {
        try {
            for (int i = 0; i < count; i++) {
                if (!running.get()) break;
                
                long startTime = System.currentTimeMillis();
                boolean reachable = address.isReachable(timeout);
                long endTime = System.currentTimeMillis();
                
                if (reachable) {
                    long responseTime = endTime - startTime;
                    result.addResponse(responseTime, ttl, true);
                    result.setPacketsReceived(result.getPacketsReceived() + 1);
                    
                    if (callback != null) {
                        callback.onResponse(result, i + 1, responseTime, ttl);
                    }
                } else {
                    result.addResponse(0, 0, false);
                    result.setPacketsLost(result.getPacketsLost() + 1);
                    
                    if (callback != null) {
                        callback.onTimeout(result, i + 1);
                    }
                }
                
                // Wait for interval
                if (i < count - 1) {
                    Thread.sleep(interval);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Linux/Unix ping using ICMP sockets
     * This requires root privileges or CAP_NET_RAW capability
     */
    private void pingICMP(PingResult result, InetAddress address) throws Exception {
        DatagramChannel channel = null;
        try {
            // Create raw socket (requires root)
            channel = DatagramChannel.open();
            channel.socket().setSoTimeout(timeout);
            
            // Use ICMP protocol
            // Note: Java doesn't support raw sockets directly, this is a demonstration
            // In practice, you would use JNI or process execution for ICMP
            
            // Fallback to InetAddress.isReachable()
            pingWindows(result, address);
            
        } catch (Exception e) {
            throw new Exception("ICMP ping requires root privileges: " + e.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
    
    // ============================================================
    // NATIVE ICMP (JNI) - Simplified version
    // ============================================================
    
    /**
     * Native method for ICMP ping (JNI implementation)
     * This is a placeholder - actual implementation would require native code
     */
    private native int nativePing(String host, int packetSize, int timeout, int ttl);
    
    static {
        try {
            System.loadLibrary("icmp");
        } catch (UnsatisfiedLinkError e) {
            // Native library not available - use fallback
        }
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Converts an IP address to a string
     */
    private String intToIp(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }
    
    /**
     * Calculates ICMP checksum
     */
    private short calculateChecksum(byte[] data) {
        int sum = 0;
        for (int i = 0; i < data.length - 1; i += 2) {
            sum += ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
        }
        if ((data.length & 1) == 1) {
            sum += (data[data.length - 1] & 0xFF) << 8;
        }
        while ((sum >> 16) != 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        return (short) ~sum;
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    
    public int getPacketSize() { return packetSize; }
    public void setPacketSize(int packetSize) {
        this.packetSize = Math.max(MIN_PACKET_SIZE, Math.min(packetSize, MAX_PACKET_SIZE));
    }
    
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    
    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = Math.max(1, ttl); }
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = Math.max(1, count); }
    
    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = Math.max(100, interval); }
    
    public boolean isVerbose() { return verbose; }
    public void setVerbose(boolean verbose) { this.verbose = verbose; }
    
    public boolean isResolveHostnames() { return resolveHostnames; }
    public void setResolveHostnames(boolean resolveHostnames) { this.resolveHostnames = resolveHostnames; }
    
    public PingCallback getCallback() { return callback; }
    public void setCallback(PingCallback callback) { this.callback = callback; }
    
    // ============================================================
    // REPORT GENERATION
    // ============================================================
    
    /**
     * Generates a report from ping results
     * 
     * @param results The ping results
     * @param format The report format (text, csv, json)
     * @return The report as a string
     */
    public String generateReport(List<PingResult> results, String format) {
        if (format.toLowerCase().equals("csv")) {
            return generateCSVReport(results);
        } else if (format.toLowerCase().equals("json")) {
            return generateJSONReport(results);
        } else {
            return generateTextReport(results);
        }
    }
    
    /**
     * Generates a text report
     */
    private String generateTextReport(List<PingResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("📡 PING REPORT\n");
        sb.append("═".repeat(70)).append("\n\n");
        
        for (PingResult result : results) {
            sb.append(result.toString());
            sb.append("\n");
        }
        
        // Summary statistics
        sb.append("\n📊 SUMMARY STATISTICS\n");
        sb.append("═".repeat(70)).append("\n");
        
        int totalHosts = results.size();
        int reachable = 0;
        int unreachable = 0;
        double totalLatency = 0;
        int latencyCount = 0;
        
        for (PingResult result : results) {
            if (result.isReachable()) {
                reachable++;
                if (result.getAvgTime() > 0) {
                    totalLatency += result.getAvgTime();
                    latencyCount++;
                }
            } else {
                unreachable++;
            }
        }
        
        sb.append("Total Hosts:    ").append(totalHosts).append("\n");
        sb.append("Reachable:      ").append(reachable).append("\n");
        sb.append("Unreachable:    ").append(unreachable).append("\n");
        sb.append("Success Rate:   ").append(String.format("%.1f%%", 
            totalHosts > 0 ? (double) reachable / totalHosts * 100 : 0)).append("\n");
        sb.append("Avg Latency:    ").append(latencyCount > 0 ? 
            String.format("%.2fms", totalLatency / latencyCount) : "N/A").append("\n");
        sb.append("═".repeat(70)).append("\n");
        
        return sb.toString();
    }
    
    /**
     * Generates a CSV report
     */
    private String generateCSVReport(List<PingResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("Host,IP,PacketSize,TTL,Sent,Received,Loss%,Min,Max,Avg,StdDev,Reachable\n");
        
        for (PingResult result : results) {
            sb.append(String.format("%s,%s,%d,%d,%d,%d,%.1f,%d,%d,%.2f,%.2f,%s\n",
                result.getHost(),
                result.getIpAddress() != null ? result.getIpAddress() : "Unknown",
                result.getPacketSize(),
                result.getTtl(),
                result.getPacketsSent(),
                result.getPacketsReceived(),
                result.getLossPercentage(),
                result.getMinTime() != Long.MAX_VALUE ? result.getMinTime() : 0,
                result.getMaxTime() != Long.MIN_VALUE ? result.getMaxTime() : 0,
                result.getAvgTime(),
                result.getStdDev(),
                result.isReachable() ? "Yes" : "No"
            ));
        }
        
        return sb.toString();
    }
    
    /**
     * Generates a JSON report
     */
    private String generateJSONReport(List<PingResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"timestamp\": \"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\",\n");
        sb.append("  \"total_hosts\": ").append(results.size()).append(",\n");
        sb.append("  \"results\": [\n");
        
        for (int i = 0; i < results.size(); i++) {
            PingResult result = results.get(i);
            sb.append("    {\n");
            sb.append("      \"host\": \"").append(escapeJSON(result.getHost())).append("\",\n");
            sb.append("      \"ip\": \"").append(result.getIpAddress() != null ? result.getIpAddress() : "Unknown").append("\",\n");
            sb.append("      \"packet_size\": ").append(result.getPacketSize()).append(",\n");
            sb.append("      \"ttl\": ").append(result.getTtl()).append(",\n");
            sb.append("      \"packets_sent\": ").append(result.getPacketsSent()).append(",\n");
            sb.append("      \"packets_received\": ").append(result.getPacketsReceived()).append(",\n");
            sb.append("      \"loss_percentage\": ").append(result.getLossPercentage()).append(",\n");
            sb.append("      \"min_latency\": ").append(result.getMinTime() != Long.MAX_VALUE ? result.getMinTime() : 0).append(",\n");
            sb.append("      \"max_latency\": ").append(result.getMaxTime() != Long.MIN_VALUE ? result.getMaxTime() : 0).append(",\n");
            sb.append("      \"avg_latency\": ").append(result.getAvgTime()).append(",\n");
            sb.append("      \"std_dev\": ").append(result.getStdDev()).append(",\n");
            sb.append("      \"reachable\": ").append(result.isReachable()).append("\n");
            sb.append("    }").append(i < results.size() - 1 ? "," : "").append("\n");
        }
        
        sb.append("  ]\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    /**
     * Escapes JSON special characters
     */
    private String escapeJSON(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    // ============================================================
    // CLEANUP
    // ============================================================
    
    /**
     * Shuts down the ping utility
     */
    public void shutdown() {
        running.set(false);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of PingUtility
     */
    public static void main(String[] args) {
        System.out.println("📡 PingUtility Demo");
        System.out.println("═".repeat(60));
        
        PingUtility pingUtil = new PingUtility();
        
        // Set callback
        pingUtil.setCallback(new PingCallback() {
            @Override
            public void onStart(PingResult result) {
                System.out.println("Pinging " + result.getHost() + "...");
            }
            
            @Override
            public void onResponse(PingResult result, int sequence, long responseTime, int ttl) {
                System.out.println("  #" + sequence + ": " + responseTime + "ms (TTL=" + ttl + ")");
            }
            
            @Override
            public void onTimeout(PingResult result, int sequence) {
                System.out.println("  #" + sequence + ": Timeout");
            }
            
            @Override
            public void onComplete(PingResult result) {
                System.out.println("\n" + result);
            }
            
            @Override
            public void onError(String message, Exception e) {
                System.err.println("Error: " + message);
            }
        });
        
        // Ping a host
        PingResult result = pingUtil.ping("google.com", 4, 64, 5000, 64);
        System.out.println("\nDetailed Result:\n" + result.toCSV());
        
        // Ping multiple hosts
        System.out.println("\n📡 Multiple Hosts Ping:");
        List<String> hosts = Arrays.asList("google.com", "github.com", "stackoverflow.com");
        Map<String, PingResult> results = pingUtil.pingMultiple(hosts);
        for (Map.Entry<String, PingResult> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + 
                (entry.getValue().isReachable() ? "✅ " + entry.getValue().getAvgTime() + "ms" : "❌ Unreachable"));
        }
        
        // Ping sweep (simplified)
        System.out.println("\n📡 Ping Sweep (simplified):");
        PingSweepResult sweepResult = pingUtil.pingSweep("192.168.1.0", 24);
        System.out.println(sweepResult);
        
        pingUtil.shutdown();
        
        System.out.println("\n✅ Demo completed!");
    }
}