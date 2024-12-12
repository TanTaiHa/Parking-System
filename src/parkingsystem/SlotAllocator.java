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
                return slot.getId() + 1; // Return slot ID in base-1
            }
        }
        
        // No available slot found
        return -1;
    }

    public boolean returnSlot(int id) {
        // Adjust input to base-0
        Slot slot = slotMap.get(id - 1);
        if (slot != null) {
            slot.setAvailable(true);
            return true;
        }
        return false;
    }

    // Read information from file
    private void loadSlotsFromFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                boolean available = Boolean.parseBoolean(parts[1].trim());
                int distanceToGate1 = Integer.parseInt(parts[2].trim());
                int distanceToGate2 = Integer.parseInt(parts[3].trim());
                int distanceToGate3 = Integer.parseInt(parts[4].trim());
                
                // Only add Slot if id doesn't exist
                if (!slotMap.containsKey(id)) {
                    Slot slot = new Slot(id, available, distanceToGate1, distanceToGate2, distanceToGate3);
                    slotsByGate1.add(slot);
                    slotsByGate2.add(slot);
                    slotsByGate3.add(slot);
                    slotMap.put(id, slot);
                }
            }
        }
    }

    // Sort arrays in ascending order by distance
    private void sortSlots() {
        slotsByGate1.sort(Comparator.comparingInt(Slot::getDistanceToGate1));
        slotsByGate2.sort(Comparator.comparingInt(Slot::getDistanceToGate2));
        slotsByGate3.sort(Comparator.comparingInt(Slot::getDistanceToGate3));
    }

    // Getter methods remain the same
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