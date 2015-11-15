package fuuast.fyp.fleamarket;

public class MarketDataModel {

    private String name, adminID, latitude, longitude, address, allow_checkbox;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAllow_checkbox() {
        return allow_checkbox;
    }

    public void setAllow_checkbox(String allow_checkbox) {
        this.allow_checkbox = allow_checkbox;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
