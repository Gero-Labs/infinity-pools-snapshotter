package io.gerowallet.snapshotter.utils;

public class SlotConverter {

    /**
     * Convert Epoch Unix Time to Cardano Slot
     * @param timeMillis Epoch Unix Timestamp in Milliseconds
     * @return Cardano Slot
     */
    public static long timeToSlot(long timeMillis) {
        return (timeMillis / 1000) - 1591566291;
    }

    /**
     * Convert Cardano Slot to Epoch Unix Time
     * @param slot Cardano Slot
     * @return Epoch Unix Time in Milliseconds
     */
    public static long slotToTime(long slot) {
        return (slot + 1591566291) * 1000;
    }
}
