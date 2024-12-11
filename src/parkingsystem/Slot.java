package parkingsystem;

public class Slot {
     private int id;
     private boolean available;
     private int distanceToGate1;
     private int distanceToGate2;
     private int distanceToGate3;

     public Slot(int id, boolean available, int distanceToGate1, int distanceToGate2, int distanceToGate3) {
          this.id = id;
          this.available = available;
          this.distanceToGate1 = distanceToGate1;
          this.distanceToGate2 = distanceToGate2;
          this.distanceToGate3 = distanceToGate3;
     }

     public int getId() {
          return id;
     }

     public void setId(int id) {
          this.id = id;
     }

     public boolean isAvailable() {
          return available;
     }

     public void setAvailable(boolean available) {
          this.available = available;
     }

     public int getDistanceToGate1() {
          return distanceToGate1;
     }

     public void setDistanceToGate1(int distanceToGate1) {
          this.distanceToGate1 = distanceToGate1;
     }

     public int getDistanceToGate2() {
          return distanceToGate2;
     }

     public void setDistanceToGate2(int distanceToGate2) {
          this.distanceToGate2 = distanceToGate2;
     }

     public int getDistanceToGate3() {
          return distanceToGate3;
     }

     public void setDistanceToGate3(int distanceToGate3) {
          this.distanceToGate3 = distanceToGate3;
     }

     @Override
     public String toString() {
          return "Slot{" +
                    "id=" + id +
                    ", available=" + available +
                    ", distanceToGate1=" + distanceToGate1 +
                    ", distanceToGate2=" + distanceToGate2 +
                    ", distanceToGate3=" + distanceToGate3 +
                    '}';
     }
}
