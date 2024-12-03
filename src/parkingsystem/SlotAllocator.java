package parkingsystem;

import java.util.ArrayList;
import java.util.List;

public class SlotAllocator {
    private List<Integer> occupiedSlots;

    // Constructor initializes the list of occupied slots
    public SlotAllocator(List<Integer> occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    // Method to get the nearest available slot based on the selected gate
    public int getNearestAvailableSlot(int gateIndex) {
        int[] slotPriorities;

        // Define priority slot order for each gate
        if (gateIndex == 2) {  // Top center gate
            slotPriorities = new int[] {2, 8, 1, 3, 14, 7, 9, 15};
        } else if (gateIndex == 17) {  // Middle right gate
            slotPriorities = new int[] {17, 11, 23, 16, 18, 10, 12, 24};
        } else {  // Middle left gate
            slotPriorities = new int[] {14, 8, 20, 13, 15, 7, 21, 19};
        }

        // Find the first available slot in the priority list
        for (int slot : slotPriorities) {
            if (!occupiedSlots.contains(slot)) {
                occupiedSlots.add(slot);  // Mark slot as occupied
                return slot;
            }
        }
        return -1; // No available slots
    }
}
