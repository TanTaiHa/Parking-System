package parkingsystem;

import java.util.List;

public class SlotAllocator {
    private final List<Integer> occupiedSlots;

    public SlotAllocator(List<Integer> occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    public int getNearestAvailableSlot(int gateIndex) {
        int[] slotPriorities;
        
        switch(gateIndex) {
            case 2:  // Gate 1 (Top center)
                // Slots closest to top center gate
                slotPriorities = new int[] {2, 3, 1, 8, 9, 7, 14, 15};
                break;
            case 17:  // Gate 2 (Middle right)
                slotPriorities = new int[] {17, 11, 23, 16, 18, 10, 12, 24};
                break;
            case 14:  // Gate 3 (Middle left)
                slotPriorities = new int[] {14, 8, 20, 13, 15, 7, 21, 19};
                break;
            default:
                return -1;
        }
        
        // Find the first available slot in the priority list
        for (int slot : slotPriorities) {
            if (!occupiedSlots.contains(slot)) {
                occupiedSlots.add(slot);  // Mark slot as occupied
                return slot;  // Return 0-based index
            }
        }
        
        return -1; // No available slots
    }
}