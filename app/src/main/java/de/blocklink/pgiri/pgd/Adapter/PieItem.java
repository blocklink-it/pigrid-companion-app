package de.blocklink.pgiri.pgd.Adapter;

public class PieItem {
    public final String id;
    public final String serialNumber;
    public final String serviceType;
    public final String location;


    public PieItem(String id, String serialNumber, String serviceType, String location) {
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
