package parkingsystem;

import java.time.LocalDateTime;

public class Vehicle {
    private int id; // Thêm ID duy nhất
    private String name;
    private String vehicleNumber;
    private String mobile;
    private int gateIndex;
    private int assignedSlotIndex = -1; // Default to -1 to indicate no slot assigned
    private LocalDateTime entryTime;
    private boolean takenVehicle; // Field to track if the vehicle is parked or not

    // Constructor đầy đủ
    public Vehicle(int id, String name, String vehicleNumber, String mobile, int gateIndex) {
        this.id = id;

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must be filled in");
        }

        // Validate vehicle number
        if (vehicleNumber == null || vehicleNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle number must be filled in");
        }

        // Validate mobile number
        if (mobile == null || mobile.trim().isEmpty() || !mobile.matches("\\d+")) {
            throw new IllegalArgumentException("Phone number must contain only digits");
        }

        // Assign values
        this.name = name.trim();
        this.vehicleNumber = vehicleNumber.trim();
        this.mobile = mobile.trim();
        this.gateIndex = gateIndex;
        this.entryTime = LocalDateTime.now();
        this.takenVehicle = true; // Vehicle is parked by default
    }

    // Getter và Setter cho ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter và Setter cho các thuộc tính khác
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

    public int getAssignedSlotIndex() {
        return assignedSlotIndex;
    }

    public void assignSlot(int slotIndex) {
        if (slotIndex < 0) {
            throw new IllegalArgumentException("Slot index cannot be negative");
        }
        this.assignedSlotIndex = slotIndex;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public boolean isTakenVehicle() {
        return takenVehicle;
    }

    public void setTakenVehicle(boolean takenVehicle) {
        this.takenVehicle = takenVehicle;
    }

    // Override phương thức toString để dễ dàng debug
    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", mobile='" + mobile + '\'' +
                ", gateIndex=" + gateIndex +
                ", assignedSlotIndex=" + assignedSlotIndex +
                ", entryTime=" + entryTime +
                ", takenVehicle=" + takenVehicle +
                '}';
    }
}
