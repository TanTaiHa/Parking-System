package parkingsystem;

public class Vehicle {
    private String name;
    private String vehicleNumber;
    private String mobile;
    private int gateIndex;

    // Constructor to initialize the Vehicle object
    public Vehicle(String name, String vehicleNumber, String mobile, int gateIndex) {
        this.name = name;
        this.vehicleNumber = vehicleNumber;
        this.mobile = mobile;
        this.gateIndex = gateIndex;
    }

    // Getters and setters
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "name='" + name + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", mobile='" + mobile + '\'' +
                ", gateIndex=" + gateIndex +
                '}';
    }
}
