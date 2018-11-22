package de.blocklink.pigrid.Adapter;

public class PiItem {
    public final String id;
    public final String serialNumber;
    public final String serviceType;
    public final String location;


    public PiItem(String id, String serialNumber, String serviceType, String location) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.serviceType = serviceType;
        this.location = location;
    }

    @Override
    public String toString() {
        return location;
    }
}
