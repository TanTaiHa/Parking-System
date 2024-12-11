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

        // // Lựa chọn danh sách dựa trên chỉ số cổng
        // System.out.println("Slots for gate after sort 1:" + gateIndex);
        // for (Slot slot : slotsByGate1) {
        // System.out.println(slot);
        // }
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

        // System.out.println("Slots for gate after sort copy:");
        // for (Slot slot : slotsByGate) {
        // System.out.println(slot);
        // }

        // Duyệt qua danh sách và tìm slot khả dụng
        for (Slot slot : slotsByGate) {
            if (slot.isAvailable()) {
                slot.setAvailable(false); // Gán trạng thái thành không khả dụng
                return slot.getId(); // Trả về ID của slot
            }
        }

        // Không tìm thấy slot khả dụng
        return -1;
    }

    public boolean returnSlot(int id) {
        Slot slot = slotMap.get(id);
        if (slot != null) {
            slot.setAvailable(true); // Đặt lại trạng thái thành khả dụng
            return true;
        }

        return false; // Không tìm thấy slot với id tương ứng
    }

    // Đọc thông tin từ file
    private void loadSlotsFromFile(String fileName) throws IOException {
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
                    getSlotsByGate1().add(slot);
                    getSlotsByGate2().add(slot);
                    getSlotsByGate3().add(slot);
                    getSlotMap().put(id, slot);
                }
            }
        }
    }

    // Sắp xếp các mảng tăng dần theo khoảng cách
    private void sortSlots() {
        getSlotsByGate1().sort(Comparator.comparingInt(Slot::getDistanceToGate1));
        getSlotsByGate2().sort(Comparator.comparingInt(Slot::getDistanceToGate2));
        getSlotsByGate3().sort(Comparator.comparingInt(Slot::getDistanceToGate3));
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