package parkingsystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SlotAllocator {
    private List<Slot> slotsByGate1;
    private List<Slot> slotsByGate2;
    private List<Slot> slotsByGate3;
    private HashMap<Integer, Slot> slotMap;

    // Constructor
    public SlotAllocator(String fileName) throws IOException {
        slotsByGate1 = new ArrayList<>();
        slotsByGate2 = new ArrayList<>();
        slotsByGate3 = new ArrayList<>();
        slotMap = new HashMap<>();

        loadSlotsFromFile(fileName);
        sortSlots();
    }

    public void UpdateSlotValue(String fileName) throws IOException {
        slotsByGate1.clear();
        slotsByGate2.clear();
        slotsByGate3.clear();
        slotMap.clear();

        loadSlotsFromFile(fileName);
        sortSlots();
    }

    public int getNearestAvailableSlot(int gateIndex) {
        List<Slot> slotsByGate;

        switch (gateIndex) {
            case 1:
                slotsByGate = getSlotsByGate1();
                break;
            case 2:
                slotsByGate = getSlotsByGate2();
                break;
            case 3:
                slotsByGate = getSlotsByGate3();
                break;
            default:
                throw new IllegalArgumentException("Invalid gate index. Must be 1, 2, or 3.");
        }

        // Find available slot
        for (Slot slot : slotsByGate) {
            if (slot.isAvailable()) {
                slot.setAvailable(false); // Mark as unavailable
                slot.setTime(); // Set begin time
                return slot.getId(); // Return slot ID
            }
        }

        // No available slot found
        return -1;
    }

    public long returnSlot(int id) {
        Slot slot = slotMap.get(id);
        if (slot != null) {
            slot.setAvailable(true);

            if (!slot.isAvailable()) {
                return slot.getTime();
            } else {
                System.out.println("Slot " + id + " is already available.");
                return -1;
            }
        }

        System.out.println("Slot " + id + " not found.");
        return -1;
    }

    // Đọc thông tin từ file
    private boolean loadSlotsFromFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // Giả sử mỗi dòng có dạng:
                                                  // id,available,distanceToGate1,distanceToGate2,distanceToGate3
                int id = Integer.parseInt(parts[0].trim());
                boolean available = Boolean.parseBoolean(parts[1].trim());
                int distanceToGate1 = Integer.parseInt(parts[2].trim());
                int distanceToGate2 = Integer.parseInt(parts[3].trim());
                int distanceToGate3 = Integer.parseInt(parts[4].trim());

                // Chỉ thêm Slot nếu id chưa tồn tại
                if (!slotMap.containsKey(id)) {
                    Slot slot = new Slot(id, available, distanceToGate1, distanceToGate2, distanceToGate3);
                    slotsByGate1.add(slot);
                    slotsByGate2.add(slot);
                    slotsByGate3.add(slot);
                    slotMap.put(id, slot);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parsing file: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
        return true;
    }

    // Sắp xếp các mảng tăng dần theo khoảng cách
    private void sortSlots() {
        slotsByGate1.sort(Comparator.comparingInt(Slot::getDistanceToGate1));
        slotsByGate2.sort(Comparator.comparingInt(Slot::getDistanceToGate2));
        slotsByGate3.sort(Comparator.comparingInt(Slot::getDistanceToGate3));
    }

    // Lấy danh sách slot theo cổng
    public List<Slot> getSlotsByGate1() {
        return slotsByGate1;
    }

    public List<Slot> getSlotsByGate2() {
        return slotsByGate2;
    }

    public List<Slot> getSlotsByGate3() {
        return slotsByGate3;
    }

    public HashMap<Integer, Slot> getSlotMap() {
        return slotMap;
    }
}