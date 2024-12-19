package parkingsystem;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Vehicle {
    // Static set to track unique vehicle numbers
    private String name;
    private String vehicleNumber;
    private String mobile;
    private int gateIndex;
    private int assignedSlotIndex = -1; // Default to -1 to indicate no slot assigned
    private LocalDateTime entryTime;

    public Vehicle(String name, String vehicleNumber, String mobile, int gateIndex) {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be fill in");
        }

        // Validate vehicle number - check for uniqueness
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number must be fill in");
        }

        // Mobile number validation
        if (mobile == null || mobile.trim().isEmpty() || !mobile.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain only digits");
        }

        // Assign values
        this.name = name.trim();
        this.vehicleNumber = vehicleNumber;
        this.mobile = mobile != null ? mobile.trim() : "";
        this.gateIndex = gateIndex;
        this.entryTime = LocalDateTime.now();

    }

    // Method to assign a slot
    public void assignSlot(int slotIndex) {
        if (slotIndex < 0) {
            throw new IllegalArgumentException("Slot index cannot be negative");
        }
        this.assignedSlotIndex = slotIndex;
    }

    // Existing getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getGateIndex() {
        return gateIndex;
    }

    public void setGateIndex(int gateIndex) {
        this.gateIndex = gateIndex;
    }

    // New getter for assigned slot index
    public int getAssignedSlotIndex() {
        return assignedSlotIndex;
    }

    // Getter for entry time
    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "name='" + name + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", mobile='" + mobile + '\'' +
                ", gateIndex=" + gateIndex +
                ", assignedSlotIndex=" + assignedSlotIndex +
                ", entryTime=" + entryTime +
                '}';
    }
}