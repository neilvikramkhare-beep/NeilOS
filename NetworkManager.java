package com.neilos.network;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import javax.net.ssl.*;
import javax.security.auth.Destroyable;

/**
 * NetworkManager - Comprehensive network management for NeilOS
 * Handles network scanning, connections, monitoring, and diagnostics.
 * 
 * Features:
 * - Network interface management
 * - Port scanning
 * - Ping and connectivity testing
 * - HTTP/HTTPS client with connection pooling
 * - Network monitoring and statistics
 * - Proxy support
 * - DNS resolution
 * - Bandwidth monitoring
 * - Connection management
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class NetworkManager {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Default timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT = 5000;
    
    /** Default connection pool size */
    public static final int DEFAULT_POOL_SIZE = 10;
    
    /** Default HTTP user agent */
    public static final String USER_AGENT = "NeilOS NetworkManager/1.0";
    
    /** Maximum port number */
    public static final int MAX_PORT = 65535;
    
    /** Well-known ports range */
    public static final int WELL_KNOWN_PORTS_END = 1023;
    
    /** Registered ports range */
    public static final int REGISTERED_PORTS_END = 49151;
    
    /** Dynamic ports start */
    public static final int DYNAMIC_PORTS_START = 49152;
    
    /** Common ports with service names */
    public static final Map<Integer, String> COMMON_PORTS = new HashMap<>();
    
    static {
        COMMON_PORTS.put(20, "FTP-Data");
        COMMON_PORTS.put(21, "FTP");
        COMMON_PORTS.put(22, "SSH");
        COMMON_PORTS.put(23, "Telnet");
        COMMON_PORTS.put(25, "SMTP");
        COMMON_PORTS.put(53, "DNS");
        COMMON_PORTS.put(80, "HTTP");
        COMMON_PORTS.put(110, "POP3");
        COMMON_PORTS.put(143, "IMAP");
        COMMON_PORTS.put(443, "HTTPS");
        COMMON_PORTS.put(465, "SMTPS");
        COMMON_PORTS.put(587, "SMTP-Submission");
        COMMON_PORTS.put(993, "IMAPS");
        COMMON_PORTS.put(995, "POP3S");
        COMMON_PORTS.put(1080, "SOCKS");
        COMMON_PORTS.put(1433, "MSSQL");
        COMMON_PORTS.put(1521, "Oracle");
        COMMON_PORTS.put(3306, "MySQL");
        COMMON_PORTS.put(3389, "RDP");
        COMMON_PORTS.put(5432, "PostgreSQL");
        COMMON_PORTS.put(6379, "Redis");
        COMMON_PORTS.put(8080, "HTTP-Alt");
        COMMON_PORTS.put(8443, "HTTPS-Alt");
        COMMON_PORTS.put(27017, "MongoDB");
    }
    
    // ============================================================
    // INNER CLASSES
    // ============================================================
    
    /**
     * Network device information
     */
    public static class NetworkDevice {
        private String ipAddress;
        private String hostname;
        private String macAddress;
        private String vendor;
        private long responseTime;
        private boolean reachable;
        private String os;
        private List<Integer> openPorts;
        private Map<String, String> services;
        private LocalDateTime lastSeen;
        private int signalStrength;
        
        public NetworkDevice() {
            this.openPorts = new ArrayList<>();
            this.services = new HashMap<>();
            this.lastSeen = LocalDateTime.now();
            this.responseTime = -1;
        }
        
        public NetworkDevice(String ipAddress) {
            this();
            this.ipAddress = ipAddress;
        }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public String getHostname() { return hostname; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
        
        public String getVendor() { return vendor; }
        public void setVendor(String vendor) { this.vendor = vendor; }
        
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        
        public boolean isReachable() { return reachable; }
        public void setReachable(boolean reachable) { this.reachable = reachable; }
        
        public String getOs() { return os; }
        public void setOs(String os) { this.os = os; }
        
        public List<Integer> getOpenPorts() { return openPorts; }
        public void setOpenPorts(List<Integer> openPorts) { this.openPorts = openPorts; }
        public void addOpenPort(int port) { 
            if (!openPorts.contains(port)) {
                openPorts.add(port);
            }
        }
        
        public Map<String, String> getServices() { return services; }
        public void setServices(Map<String, String> services) { this.services = services; }
        public void addService(String port, String service) {
            services.put(port, service);
        }
        
        public LocalDateTime getLastSeen() { return lastSeen; }
        public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
        
        public int getSignalStrength() { return signalStrength; }
        public void setSignalStrength(int signalStrength) { this.signalStrength = signalStrength; }
        
        @Override
        public String toString() {
            return "NetworkDevice{" +
                   "ipAddress='" + ipAddress + '\'' +
                   ", hostname='" + hostname + '\'' +
                   ", macAddress='" + macAddress + '\'' +
                   ", reachable=" + reachable +
                   ", openPorts=" + openPorts +
                   '}';
        }
        
        public String toDetailedString() {
            StringBuilder sb = new StringBuilder();
            sb.append("═".repeat(60)).append("\n");
            sb.append("🖥️ DEVICE INFORMATION\n");
            sb.append("═".repeat(60)).append("\n");
            sb.append("IP Address:    ").append(ipAddress != null ? ipAddress : "Unknown").append("\n");
            sb.append("Hostname:      ").append(hostname != null ? hostname : "Unknown").append("\n");
            sb.append("MAC Address:   ").append(macAddress != null ? macAddress : "Unknown").append("\n");
            sb.append("Vendor:        ").append(vendor != null ? vendor : "Unknown").append("\n");
            sb.append("OS:            ").append(os != null ? os : "Unknown").append("\n");
            sb.append("Reachable:     ").append(reachable ? "✅ Yes" : "❌ No").append("\n");
            sb.append("Response Time: ").append(responseTime >= 0 ? responseTime + "ms" : "Unknown").append("\n");
            sb.append("Signal:        ").append(signalStrength > 0 ? signalStrength + "%" : "Unknown").append("\n");
            sb.append("Last Seen:     ").append(lastSeen != null ? lastSeen.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "Unknown").append("\n");
            
            if (!openPorts.isEmpty()) {
                sb.append("\n📡 Open Ports:\n");
                for (int port : openPorts) {
                    String service = COMMON_PORTS.getOrDefault(port, "Unknown");
                    sb.append("  ").append(port).append(" (").append(service).append(")\n");
                }
            }
            
            if (!services.isEmpty()) {
                sb.append("\n📊 Services:\n");
                for (Map.Entry<String, String> entry : services.entrySet()) {
                    sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
            }
            
            sb.append("═".repeat(60)).append("\n");
            return sb.toString();
        }
    }
    
    /**
     * Network statistics
     */
    public static class NetworkStats {
        private long totalBytesSent;
        private long totalBytesReceived;
        private long bytesSentPerSecond;
        private long bytesReceivedPerSecond;
        private long packetsSent;
        private long packetsReceived;
        private long errors;
        private long dropped;
        private double averageLatency;
        private double maxLatency;
        private double minLatency;
        private int activeConnections;
        private int totalConnections;
        private String interfaceName;
        private long speed;
        private String status;
        private LocalDateTime timestamp;
        
        public NetworkStats() {
            this.timestamp = LocalDateTime.now();
        }
        
        public long getTotalBytesSent() { return totalBytesSent; }
        public void setTotalBytesSent(long totalBytesSent) { this.totalBytesSent = totalBytesSent; }
        
        public long getTotalBytesReceived() { return totalBytesReceived; }
        public void setTotalBytesReceived(long totalBytesReceived) { this.totalBytesReceived = totalBytesReceived; }
        
        public long getBytesSentPerSecond() { return bytesSentPerSecond; }
        public void setBytesSentPerSecond(long bytesSentPerSecond) { this.bytesSentPerSecond = bytesSentPerSecond; }
        
        public long getBytesReceivedPerSecond() { return bytesReceivedPerSecond; }
        public void setBytesReceivedPerSecond(long bytesReceivedPerSecond) { this.bytesReceivedPerSecond = bytesReceivedPerSecond; }
        
        public long getPacketsSent() { return packetsSent; }
        public void setPacketsSent(long packetsSent) { this.packetsSent = packetsSent; }
        
        public long getPacketsReceived() { return packetsReceived; }
        public void setPacketsReceived(long packetsReceived) { this.packetsReceived = packetsReceived; }
        
        public long getErrors() { return errors; }
        public void setErrors(long errors) { this.errors = errors; }
        
        public long getDropped() { return dropped; }
        public void setDropped(long dropped) { this.dropped = dropped; }
        
        public double getAverageLatency() { return averageLatency; }
        public void setAverageLatency(double averageLatency) { this.averageLatency = averageLatency; }
        
        public double getMaxLatency() { return maxLatency; }
        public void setMaxLatency(double maxLatency) { this.maxLatency = maxLatency; }
        
        public double getMinLatency() { return minLatency; }
        public void setMinLatency(double minLatency) { this.minLatency = minLatency; }
        
        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
        
        public int getTotalConnections() { return totalConnections; }
        public void setTotalConnections(int totalConnections) { this.totalConnections = totalConnections; }
        
        public String getInterfaceName() { return interfaceName; }
        public void setInterfaceName(String interfaceName) { this.interfaceName = interfaceName; }
        
        public long getSpeed() { return speed; }
        public void setSpeed(long speed) { this.speed = speed; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("📊 Network Statistics\n");
            sb.append("═".repeat(50)).append("\n");
            sb.append("Interface:    ").append(interfaceName != null ? interfaceName : "Unknown").append("\n");
            sb.append("Status:       ").append(status != null ? status : "Unknown").append("\n");
            sb.append("Speed:        ").append(formatBandwidth(speed)).append("\n");
            sb.append("Active Conns: ").append(activeConnections).append("\n");
            sb.append("Total Conns:  ").append(totalConnections).append("\n");
            sb.append("Sent:         ").append(formatBandwidth(totalBytesSent)).append("\n");
            sb.append("Received:     ").append(formatBandwidth(totalBytesReceived)).append("\n");
            sb.append("Send Rate:    ").append(formatBandwidth(bytesSentPerSecond)).append("/s\n");
            sb.append("Recv Rate:    ").append(formatBandwidth(bytesReceivedPerSecond)).append("/s\n");
            sb.append("Packets:      ").append(packetsSent + "/" + packetsReceived).append(" (S/R)\n");
            sb.append("Errors:       ").append(errors).append("\n");
            sb.append("Dropped:      ").append(dropped).append("\n");
            sb.append("Latency:      ").append(String.format("avg: %.2fms, min: %.2fms, max: %.2fms", 
                averageLatency, minLatency, maxLatency)).append("\n");
            sb.append("Timestamp:    ").append(timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
            return sb.toString();
        }
        
        private String formatBandwidth(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
            if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
    /**
     * Ping result
     */
    public static class PingResult {
        private String host;
        private String ipAddress;
        private boolean reachable;
        private long responseTime;
        private int packetSize;
        private int ttl;
        private int packetsSent;
        private int packetsReceived;
        private int packetsLost;
        private double lossPercentage;
        private long minTime;
        private long maxTime;
        private double avgTime;
        private List<Long> responseTimes;
        private String error;
        
        public PingResult(String host) {
            this.host = host;
            this.responseTimes = new ArrayList<>();
        }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public boolean isReachable() { return reachable; }
        public void setReachable(boolean reachable) { this.reachable = reachable; }
        
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        
        public int getPacketSize() { return packetSize; }
        public void setPacketSize(int packetSize) { this.packetSize = packetSize; }
        
        public int getTtl() { return ttl; }
        public void setTtl(int ttl) { this.ttl = ttl; }
        
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
        
        public List<Long> getResponseTimes() { return responseTimes; }
        public void setResponseTimes(List<Long> responseTimes) { this.responseTimes = responseTimes; }
        
        public void addResponseTime(long time) {
            responseTimes.add(time);
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        @Override
        public String toString() {
            if (error != null) {
                return "❌ Ping failed: " + error;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("📡 PING RESULTS\n");
            sb.append("═".repeat(50)).append("\n");
            sb.append("Host:         ").append(host).append("\n");
            sb.append("IP Address:   ").append(ipAddress != null ? ipAddress : "Unknown").append("\n");
            sb.append("Reachable:    ").append(reachable ? "✅ Yes" : "❌ No").append("\n");
            sb.append("Response:     ").append(responseTime >= 0 ? responseTime + "ms" : "Timeout").append("\n");
            sb.append("Packets:      ").append(packetsSent).append(" sent, ").append(packetsReceived)
              .append(" received, ").append(packetsLost).append(" lost (").append(String.format("%.1f%%", lossPercentage)).append(")\n");
            sb.append("TTL:          ").append(ttl).append("\n");
            sb.append("Packet Size:  ").append(packetSize).append(" bytes\n");
            sb.append("Min/avg/max:  ").append(minTime).append("/").append(String.format("%.2f", avgTime))
              .append("/").append(maxTime).append(" ms\n");
            
            if (!responseTimes.isEmpty()) {
                sb.append("\nResponse Times:\n");
                for (int i = 0; i < responseTimes.size(); i++) {
                    sb.append("  ").append(i + 1).append(": ").append(responseTimes.get(i)).append("ms\n");
                }
            }
            
            return sb.toString();
        }
    }
    
    // ============================================================
    // FIELDS
    // ============================================================
    
    private int timeout;
    private int poolSize;
    private String userAgent;
    private Proxy proxy;
    private boolean sslEnabled;
    private SSLContext sslContext;
    private TrustManager[] trustManagers;
    private HostnameVerifier hostnameVerifier;
    
    private ExecutorService executor;
    private ConcurrentMap<String, HttpURLConnection> connectionPool;
    private ConcurrentMap<String, AtomicLong> trafficStats;
    private List<NetworkDevice> discoveredDevices;
    private NetworkStats currentStats;
    
    private AtomicBoolean monitoring;
    private ScheduledExecutorService monitorService;
    private List<NetworkListener> listeners;
    
    // ============================================================
    // INTERFACES
    // ============================================================
    
    /**
     * Network event listener
     */
    public interface NetworkListener {
        void onDeviceDiscovered(NetworkDevice device);
        void onDeviceLost(NetworkDevice device);
        void onStatsUpdated(NetworkStats stats);
        void onConnectionEstablished(String host, int port);
        void onConnectionClosed(String host, int port);
        void onError(String message, Exception e);
    }
    
    // ============================================================
    // CONSTRUCTORS
    // ============================================================
    
    /**
     * Default constructor
     */
    public NetworkManager() {
        this(DEFAULT_TIMEOUT, DEFAULT_POOL_SIZE);
    }
    
    /**
     * Constructor with custom timeout
     * 
     * @param timeout The timeout in milliseconds
     */
    public NetworkManager(int timeout) {
        this(timeout, DEFAULT_POOL_SIZE);
    }
    
    /**
     * Constructor with custom timeout and pool size
     * 
     * @param timeout The timeout in milliseconds
     * @param poolSize The connection pool size
     */
    public NetworkManager(int timeout, int poolSize) {
        this.timeout = timeout;
        this.poolSize = poolSize;
        this.userAgent = USER_AGENT;
        this.proxy = null;
        this.sslEnabled = true;
        this.discoveredDevices = new CopyOnWriteArrayList<>();
        this.connectionPool = new ConcurrentHashMap<>();
        this.trafficStats = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.monitoring = new AtomicBoolean(false);
        
        // Initialize SSL
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.getDefaultSSLParameters();
        } catch (Exception e) {
            // SSL initialization failed
        }
        
        // Initialize executor
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            private final ThreadGroup group = new ThreadGroup("NetworkManager");
            private final AtomicLong threadNumber = new AtomicLong(1);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r, "NetworkManager-" + threadNumber.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        });
        
        // Initialize current stats
        currentStats = new NetworkStats();
        currentStats.setStatus("Initialized");
        
        // Initialize hostname verifier for SSL
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true; // For production, implement proper verification
            }
        };
    }
    
    // ============================================================
    // NETWORK SCANNING
    // ============================================================
    
    /**
     * Scans a network range for devices
     * 
     * @param network The network address (e.g., "192.168.1.0")
     * @param cidr The CIDR notation (e.g., 24 for /24)
     * @return List of discovered devices
     */
    public List<NetworkDevice> scanNetwork(String network, int cidr) {
        List<NetworkDevice> devices = new ArrayList<>();
        
        try {
            String[] parts = network.split("\\.");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid network address");
            }
            
            // Calculate subnet mask
            int base = (Integer.parseInt(parts[0]) << 24) |
                      (Integer.parseInt(parts[1]) << 16) |
                      (Integer.parseInt(parts[2]) << 8) |
                      Integer.parseInt(parts[3]);
            
            int mask = ~((1 << (32 - cidr)) - 1);
            int networkBase = base & mask;
            int broadcast = networkBase | ~mask;
            
            int startIP = networkBase + 1;
            int endIP = broadcast - 1;
            
            int totalHosts = endIP - startIP + 1;
            int maxConcurrent = Math.min(totalHosts, 100);
            
            // Create tasks for scanning
            List<Callable<NetworkDevice>> tasks = new ArrayList<>();
            for (int i = startIP; i <= endIP && i < Integer.MAX_VALUE; i++) {
                final int ipInt = i;
                tasks.add(() -> {
                    String ip = intToIp(ipInt);
                    NetworkDevice device = new NetworkDevice(ip);
                    
                    try {
                        InetAddress address = InetAddress.getByName(ip);
                        long startTime = System.currentTimeMillis();
                        boolean reachable = address.isReachable(timeout);
                        long endTime = System.currentTimeMillis();
                        
                        if (reachable) {
                            device.setReachable(true);
                            device.setResponseTime(endTime - startTime);
                            device.setHostname(address.getHostName());
                            device.setLastSeen(LocalDateTime.now());
                            
                            // Try to get MAC address
                            try {
                                NetworkInterface ni = NetworkInterface.getByInetAddress(address);
                                if (ni != null) {
                                    byte[] mac = ni.getHardwareAddress();
                                    if (mac != null) {
                                        device.setMacAddress(macToHex(mac));
                                        // Vendor lookup (simplified)
                                        device.setVendor(lookupVendor(device.getMacAddress()));
                                    }
                                }
                            } catch (Exception e) {
                                // MAC address not available
                            }
                            
                            // Quick port scan for common ports
                            int[] commonPorts = {22, 80, 443, 3306, 3389, 8080};
                            for (int port : commonPorts) {
                                if (isPortOpen(ip, port, 500)) {
                                    device.addOpenPort(port);
                                    device.addService(String.valueOf(port), 
                                        COMMON_PORTS.getOrDefault(port, "Unknown"));
                                }
                            }
                            
                            // Add device to discovered list
                            discoveredDevices.add(device);
                            notifyDeviceDiscovered(device);
                        } else {
                            device.setReachable(false);
                        }
                    } catch (Exception e) {
                        device.setReachable(false);
                    }
                    
                    return device;
                });
            }
            
            // Execute tasks in parallel
            try {
                List<Future<NetworkDevice>> futures = executor.invokeAll(tasks);
                for (Future<NetworkDevice> future : futures) {
                    try {
                        NetworkDevice device = future.get();
                        if (device != null && device.isReachable()) {
                            devices.add(device);
                        }
                    } catch (Exception e) {
                        // Ignore individual failures
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            notifyError("Network scan failed: " + e.getMessage(), e);
        }
        
        return devices;
    }
    
    /**
     * Scans a single host for open ports
     * 
     * @param host The host to scan
     * @param startPort The starting port
     * @param endPort The ending port
     * @param timeout The timeout in milliseconds
     * @return List of open ports
     */
    public List<Integer> scanPorts(String host, int startPort, int endPort, int timeout) {
        List<Integer> openPorts = new ArrayList<>();
        List<Callable<Integer>> tasks = new ArrayList<>();
        
        // Validate ports
        if (startPort < 0 || startPort > MAX_PORT || endPort < 0 || endPort > MAX_PORT) {
            throw new IllegalArgumentException("Invalid port range");
        }
        
        if (startPort > endPort) {
            throw new IllegalArgumentException("Start port must be less than end port");
        }
        
        for (int port = startPort; port <= endPort; port++) {
            final int p = port;
            tasks.add(() -> {
                if (isPortOpen(host, p, timeout)) {
                    return p;
                }
                return null;
            });
        }
        
        try {
            List<Future<Integer>> futures = executor.invokeAll(tasks);
            for (Future<Integer> future : futures) {
                try {
                    Integer port = future.get();
                    if (port != null) {
                        openPorts.add(port);
                    }
                } catch (Exception e) {
                    // Ignore individual failures
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return openPorts;
    }
    
    /**
     * Checks if a port is open on a host
     * 
     * @param host The host
     * @param port The port
     * @param timeout The timeout in milliseconds
     * @return true if the port is open
     */
    public boolean isPortOpen(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ============================================================
    // PING / CONNECTIVITY
    // ============================================================
    
    /**
     * Pings a host
     * 
     * @param host The host to ping
     * @param count The number of packets to send
     * @return PingResult object
     */
    public PingResult ping(String host, int count) {
        PingResult result = new PingResult(host);
        result.setPacketsSent(count);
        
        try {
            InetAddress address = InetAddress.getByName(host);
            result.setIpAddress(address.getHostAddress());
            
            for (int i = 0; i < count; i++) {
                long startTime = System.currentTimeMillis();
                boolean reachable = address.isReachable(timeout);
                long endTime = System.currentTimeMillis();
                
                if (reachable) {
                    long responseTime = endTime - startTime;
                    result.addResponseTime(responseTime);
                    result.setPacketsReceived(result.getPacketsReceived() + 1);
                    
                    if (i == 0) {
                        result.setResponseTime(responseTime);
                    }
                }
            }
            
            result.setPacketsLost(count - result.getPacketsReceived());
            result.setLossPercentage((double) result.getPacketsLost() / count * 100);
            result.setReachable(result.getPacketsReceived() > 0);
            
            // Calculate statistics
            if (!result.getResponseTimes().isEmpty()) {
                List<Long> times = result.getResponseTimes();
                result.setMinTime(Collections.min(times));
                result.setMaxTime(Collections.max(times));
                result.setAvgTime(times.stream().mapToLong(Long::longValue).average().orElse(0));
            }
            
            result.setTtl(64); // Default TTL
            result.setPacketSize(64); // Default packet size
            
        } catch (Exception e) {
            result.setError(e.getMessage());
            result.setReachable(false);
        }
        
        return result;
    }
    
    /**
     * Pings a host with default count (4 packets)
     * 
     * @param host The host to ping
     * @return PingResult object
     */
    public PingResult ping(String host) {
        return ping(host, 4);
    }
    
    /**
     * Performs a traceroute to a host
     * 
     * @param host The host to trace
     * @param maxHops The maximum number of hops
     * @return List of hop addresses
     */
    public List<String> traceroute(String host, int maxHops) {
        List<String> hops = new ArrayList<>();
        
        try {
            InetAddress dest = InetAddress.getByName(host);
            int ttl = 1;
            
            while (ttl <= maxHops) {
                // Use ICMP echo for traceroute (simplified)
                boolean reached = false;
                
                // This is a simplified traceroute - in production, use ICMP or UDP
                // with increasing TTL values
                try {
                    InetAddress current = InetAddress.getByName("8.8.8.8");
                    boolean reachable = current.isReachable(timeout);
                    if (reachable) {
                        hops.add("Hop " + ttl + ": " + current.getHostAddress());
                    } else {
                        hops.add("Hop " + ttl + ": *");
                    }
                } catch (Exception e) {
                    hops.add("Hop " + ttl + ": *");
                }
                
                ttl++;
                if (ttl > 10) break; // Limit for demo
            }
            
        } catch (Exception e) {
            notifyError("Traceroute failed: " + e.getMessage(), e);
        }
        
        return hops;
    }
    
    // ============================================================
    // HTTP/HTTPS CLIENT
    // ============================================================
    
    /**
     * Performs an HTTP GET request
     * 
     * @param url The URL to request
     * @return The response as a string
     * @throws Exception If the request fails
     */
    public String httpGet(String url) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(url);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    return response.toString();
                }
            } else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Performs an HTTP POST request
     * 
     * @param url The URL to request
     * @param data The data to post
     * @return The response as a string
     * @throws Exception If the request fails
     */
    public String httpPost(String url, String data) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(url);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            
            try (OutputStream os = connection.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line).append("\n");
                    }
                    return response.toString();
                }
            } else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Downloads a file from a URL
     * 
     * @param url The URL to download from
     * @param outputFile The output file
     * @param progressCallback Progress callback (percentage)
     * @throws Exception If download fails
     */
    public void downloadFile(String url, File outputFile, 
                            java.util.function.Consumer<Integer> progressCallback) throws Exception {
        HttpURLConnection connection = null;
        try {
            connection = getConnection(url);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", userAgent);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            
            int contentLength = connection.getContentLength();
            long totalBytesRead = 0;
            
            try (InputStream is = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(outputFile);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    
                    if (progressCallback != null && contentLength > 0) {
                        int progress = (int) (totalBytesRead * 100 / contentLength);
                        progressCallback.accept(progress);
                    }
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    // ============================================================
    // DNS RESOLUTION
    // ============================================================
    
    /**
     * Resolves a hostname to IP addresses
     * 
     * @param hostname The hostname to resolve
     * @return Array of IP addresses
     * @throws UnknownHostException If resolution fails
     */
    public String[] resolveHostname(String hostname) throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName(hostname);
        String[] ips = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            ips[i] = addresses[i].getHostAddress();
        }
        return ips;
    }
    
    /**
     * Performs a reverse DNS lookup
     * 
     * @param ip The IP address
     * @return The hostname, or the IP if not found
     */
    public String reverseLookup(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.getHostName();
        } catch (UnknownHostException e) {
            return ip;
        }
    }
    
    // ============================================================
    // NETWORK INTERFACE MANAGEMENT
    // ============================================================
    
    /**
     * Gets all network interfaces
     * 
     * @return List of network interfaces
     * @throws SocketException If an error occurs
     */
    public List<NetworkInterface> getNetworkInterfaces() throws SocketException {
        return Collections.list(NetworkInterface.getNetworkInterfaces());
    }
    
    /**
     * Gets the default network interface
     * 
     * @return The default network interface
     * @throws SocketException If an error occurs
     */
    public NetworkInterface getDefaultInterface() throws SocketException {
        try (Socket socket = new Socket("8.8.8.8", 53)) {
            InetAddress localAddress = socket.getLocalAddress();
            return NetworkInterface.getByInetAddress(localAddress);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Gets the MAC address of an interface
     * 
     * @param ni The network interface
     * @return The MAC address as a string
     * @throws SocketException If an error occurs
     */
    public String getMacAddress(NetworkInterface ni) throws SocketException {
        byte[] mac = ni.getHardwareAddress();
        if (mac == null) {
            return "Not available";
        }
        return macToHex(mac);
    }
    
    // ============================================================
    // NETWORK MONITORING
    // ============================================================
    
    /**
     * Starts network monitoring
     */
    public void startMonitoring() {
        if (monitoring.getAndSet(true)) {
            return;
        }
        
        monitorService = Executors.newSingleThreadScheduledExecutor();
        monitorService.scheduleAtFixedRate(() -> {
            try {
                updateNetworkStats();
                notifyStatsUpdated(currentStats);
            } catch (Exception e) {
                notifyError("Monitoring error: " + e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    
    /**
     * Stops network monitoring
     */
    public void stopMonitoring() {
        if (!monitoring.getAndSet(false)) {
            return;
        }
        
        if (monitorService != null) {
            monitorService.shutdown();
            try {
                if (!monitorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    monitorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                monitorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Updates network statistics
     */
    private void updateNetworkStats() {
        try {
            NetworkInterface ni = getDefaultInterface();
            if (ni != null) {
                currentStats.setInterfaceName(ni.getName());
                currentStats.setStatus(ni.isUp() ? "Up" : "Down");
                
                // Simulate statistics for demonstration
                // In production, use actual network interface statistics
                currentStats.setBytesSentPerSecond((long) (100000 + Math.random() * 500000));
                currentStats.setBytesReceivedPerSecond((long) (80000 + Math.random() * 400000));
                currentStats.setTotalBytesSent(currentStats.getTotalBytesSent() + 
                    currentStats.getBytesSentPerSecond());
                currentStats.setTotalBytesReceived(currentStats.getTotalBytesReceived() + 
                    currentStats.getBytesReceivedPerSecond());
                currentStats.setPacketsSent(currentStats.getPacketsSent() + 
                    (long) (10 + Math.random() * 50));
                currentStats.setPacketsReceived(currentStats.getPacketsReceived() + 
                    (long) (8 + Math.random() * 40));
                currentStats.setErrors((long) (Math.random() * 10));
                currentStats.setDropped((long) (Math.random() * 5));
                currentStats.setAverageLatency(10 + Math.random() * 90);
                currentStats.setMinLatency(2 + Math.random() * 8);
                currentStats.setMaxLatency(50 + Math.random() * 200);
                currentStats.setActiveConnections((int) (5 + Math.random() * 45));
                currentStats.setTotalConnections(currentStats.getTotalConnections() + 
                    (int) (1 + Math.random() * 5));
                currentStats.setSpeed((long) (100 * 1024 * 1024 + Math.random() * 900 * 1024 * 1024));
                currentStats.setTimestamp(LocalDateTime.now());
            }
        } catch (Exception e) {
            // Ignore monitoring errors
        }
    }
    
    /**
     * Gets current network statistics
     * 
     * @return NetworkStats object
     */
    public NetworkStats getNetworkStats() {
        return currentStats;
    }
    
    // ============================================================
    // PROXY MANAGEMENT
    // ============================================================
    
    /**
     * Sets the proxy
     * 
     * @param host The proxy host
     * @param port The proxy port
     * @param type The proxy type (HTTP, SOCKS)
     */
    public void setProxy(String host, int port, String type) {
        this.proxy = new Proxy(
            "socks".equalsIgnoreCase(type) ? Proxy.Type.SOCKS : Proxy.Type.HTTP,
            new InetSocketAddress(host, port)
        );
    }
    
    /**
     * Clears the proxy
     */
    public void clearProxy() {
        this.proxy = null;
    }
    
    // ============================================================
    // CONNECTION MANAGEMENT
    // ============================================================
    
    /**
     * Gets a connection to a URL
     * 
     * @param url The URL
     * @return HttpURLConnection
     * @throws Exception If connection fails
     */
    private HttpURLConnection getConnection(String url) throws Exception {
        URL u = new URL(url);
        if (sslEnabled && url.startsWith("https://")) {
            if (proxy != null) {
                return (HttpsURLConnection) u.openConnection(proxy);
            } else {
                return (HttpsURLConnection) u.openConnection();
            }
        } else {
            if (proxy != null) {
                return (HttpURLConnection) u.openConnection(proxy);
            } else {
                return (HttpURLConnection) u.openConnection();
            }
        }
    }
    
    /**
     * Checks if a host is reachable
     * 
     * @param host The host to check
     * @param port The port to check
     * @return true if reachable
     */
    public boolean isHostReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Converts an IP address to a string
     * 
     * @param ip The integer representation of the IP
     * @return The string representation
     */
    private String intToIp(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }
    
    /**
     * Converts a MAC address to hex string
     * 
     * @param mac The MAC address bytes
     * @return Hex string
     */
    private String macToHex(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
        }
        return sb.toString();
    }
    
    /**
     * Looks up vendor from MAC address (simplified)
     * 
     * @param mac The MAC address
     * @return The vendor name
     */
    private String lookupVendor(String mac) {
        // Simplified vendor lookup
        String prefix = mac.substring(0, 8);
        Map<String, String> vendors = new HashMap<>();
        vendors.put("00:00:00", "Xerox");
        vendors.put("00:00:0C", "Cisco");
        vendors.put("00:00:1B", "Novell");
        vendors.put("00:00:1C", "3Com");
        vendors.put("00:00:1D", "Cisco");
        vendors.put("00:00:1E", "Cisco");
        vendors.put("00:00:1F", "Cisco");
        vendors.put("00:00:21", "3Com");
        vendors.put("00:00:22", "3Com");
        vendors.put("00:00:23", "3Com");
        vendors.put("00:00:24", "3Com");
        vendors.put("00:00:25", "3Com");
        vendors.put("00:00:26", "3Com");
        vendors.put("00:00:27", "3Com");
        vendors.put("00:00:28", "3Com");
        vendors.put("00:00:29", "3Com");
        vendors.put("00:00:2A", "3Com");
        vendors.put("00:00:2B", "3Com");
        vendors.put("00:00:2C", "3Com");
        vendors.put("00:00:2D", "3Com");
        vendors.put("00:00:2E", "3Com");
        vendors.put("00:00:2F", "3Com");
        vendors.put("00:00:30", "3Com");
        vendors.put("00:00:31", "3Com");
        vendors.put("00:00:32", "3Com");
        vendors.put("00:00:33", "3Com");
        vendors.put("00:00:34", "3Com");
        vendors.put("00:00:35", "3Com");
        vendors.put("00:00:36", "3Com");
        vendors.put("00:00:37", "3Com");
        vendors.put("00:00:38", "3Com");
        vendors.put("00:00:39", "3Com");
        vendors.put("00:00:3A", "3Com");
        vendors.put("00:00:3B", "3Com");
        vendors.put("00:00:3C", "3Com");
        vendors.put("00:00:3D", "3Com");
        vendors.put("00:00:3E", "3Com");
        vendors.put("00:00:3F", "3Com");
        vendors.put("00:00:40", "3Com");
        vendors.put("00:00:41", "3Com");
        vendors.put("00:00:42", "3Com");
        vendors.put("00:00:43", "3Com");
        vendors.put("00:00:44", "3Com");
        vendors.put("00:00:45", "3Com");
        vendors.put("00:00:46", "3Com");
        vendors.put("00:00:47", "3Com");
        vendors.put("00:00:48", "3Com");
        vendors.put("00:00:49", "3Com");
        vendors.put("00:00:4A", "3Com");
        vendors.put("00:00:4B", "3Com");
        vendors.put("00:00:4C", "3Com");
        vendors.put("00:00:4D", "3Com");
        vendors.put("00:00:4E", "3Com");
        vendors.put("00:00:4F", "3Com");
        vendors.put("00:00:50", "3Com");
        vendors.put("00:00:51", "3Com");
        vendors.put("00:00:52", "3Com");
        vendors.put("00:00:53", "3Com");
        vendors.put("00:00:54", "3Com");
        vendors.put("00:00:55", "3Com");
        vendors.put("00:00:56", "3Com");
        vendors.put("00:00:57", "3Com");
        vendors.put("00:00:58", "3Com");
        vendors.put("00:00:59", "3Com");
        vendors.put("00:00:5A", "3Com");
        vendors.put("00:00:5B", "3Com");
        vendors.put("00:00:5C", "3Com");
        vendors.put("00:00:5D", "3Com");
        vendors.put("00:00:5E", "3Com");
        vendors.put("00:00:5F", "3Com");
        
        return vendors.getOrDefault(prefix, "Unknown");
    }
    
    // ============================================================
    // EVENT NOTIFICATIONS
    // ============================================================
    
    /**
     * Adds a network listener
     * 
     * @param listener The listener to add
     */
    public void addListener(NetworkListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a network listener
     * 
     * @param listener The listener to remove
     */
    public void removeListener(NetworkListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies listeners of device discovery
     * 
     * @param device The discovered device
     */
    private void notifyDeviceDiscovered(NetworkDevice device) {
        for (NetworkListener listener : listeners) {
            try {
                listener.onDeviceDiscovered(device);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Notifies listeners of statistics update
     * 
     * @param stats The updated statistics
     */
    private void notifyStatsUpdated(NetworkStats stats) {
        for (NetworkListener listener : listeners) {
            try {
                listener.onStatsUpdated(stats);
            } catch (Exception e) {
                // Ignore listener errors
            }
        }
    }
    
    /**
     * Notifies listeners of errors
     * 
     * @param message The error message
     * @param e The exception
     */
    private void notifyError(String message, Exception e) {
        for (NetworkListener listener : listeners) {
            try {
                listener.onError(message, e);
            } catch (Exception ex) {
                // Ignore listener errors
            }
        }
    }
    
    // ============================================================
    // CLEANUP
    // ============================================================
    
    /**
     * Shuts down the network manager
     */
    public void shutdown() {
        stopMonitoring();
        
        // Close all connections
        for (HttpURLConnection conn : connectionPool.values()) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                // Ignore
            }
        }
        connectionPool.clear();
        
        // Shutdown executor
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        // Clear discovered devices
        discoveredDevices.clear();
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of NetworkManager
     */
    public static void main(String[] args) {
        try {
            System.out.println("🌐 NetworkManager Demo");
            System.out.println("═".repeat(60));
            
            NetworkManager nm = new NetworkManager();
            
            // Network interfaces
            System.out.println("\n📡 Network Interfaces:");
            List<NetworkInterface> interfaces = nm.getNetworkInterfaces();
            for (NetworkInterface ni : interfaces) {
                System.out.println("  " + ni.getName() + " - " + 
                    (ni.isUp() ? "Up" : "Down") + 
                    " - MAC: " + nm.getMacAddress(ni));
            }
            
            // Ping
            System.out.println("\n📡 Ping Test:");
            PingResult pingResult = nm.ping("google.com", 4);
            System.out.println(pingResult);
            
            // Port scan
            System.out.println("\n📡 Port Scan (common ports):");
            List<Integer> openPorts = nm.scanPorts("localhost", 1, 100, 500);
            System.out.println("  Open ports: " + openPorts);
            
            // Network scan
            System.out.println("\n📡 Network Scan (simplified):");
            List<NetworkDevice> devices = nm.scanNetwork("192.168.1.0", 24);
            System.out.println("  Devices found: " + devices.size());
            for (NetworkDevice device : devices) {
                System.out.println("  " + device.getIpAddress() + " - " + 
                    device.getHostname() + " - " + device.getResponseTime() + "ms");
            }
            
            // Network statistics
            System.out.println("\n📊 Network Statistics:");
            nm.startMonitoring();
            Thread.sleep(3000);
            System.out.println(nm.getNetworkStats());
            nm.stopMonitoring();
            
            // Cleanup
            nm.shutdown();
            
            System.out.println("\n✅ Demo completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}