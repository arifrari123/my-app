package com.example.model;

public enum DeviceProfile {
    MOBILE_ZTE("ZTE Blade A35 (Clean Mobile)", "Official hardware telemetry with clean signature"),
    EMULATOR_PC("Google Pixel 4 / PC Emulator", "High performance emulator signature");

    private final String displayName;
    private final String description;

    DeviceProfile(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
