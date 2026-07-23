package com.neilos;

/**
 * Represents a desktop application icon
 */
public class DesktopApp {
    private String name;
    private String icon;
    private String appId;
    
    public DesktopApp(String name, String icon, String appId) {
        this.name = name;
        this.icon = icon;
        this.appId = appId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public String getAppId() {
        return appId;
    }
    
    @Override
    public String toString() {
        return icon + " " + name;
    }
}