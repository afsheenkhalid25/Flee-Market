package fuuast.fyp.fleamarket;

public class ShopDataModelSingleTon
{
    String name,market_id,user_id,ctgry_one,ctgry_two,ctgry_three,width,length;

    double lat,lon,NW_lat,NW_lon,NE_lat,NE_lon,SW_lat,SW_lon,SE_lat,SE_lon;

    static ShopDataModelSingleTon obj = new ShopDataModelSingleTon();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCtgry_one() {
        return ctgry_one;
    }

    public void setCtgry_one(String ctgry_one) {
        this.ctgry_one = ctgry_one;
    }

    public String getCtgry_two() {
        return ctgry_two;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setCtgry_two(String ctgry_two) {
        this.ctgry_two = ctgry_two;
    }

    public String getCtgry_three() {
        return ctgry_three;
    }

    public void setCtgry_three(String ctgry_three) {
        this.ctgry_three = ctgry_three;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getNW_lat() {
        return NW_lat;
    }

    public void setNW_lat(double NW_lat) {
        this.NW_lat = NW_lat;
    }

    public double getNW_lon() {
        return NW_lon;
    }

    public void setNW_lon(double NW_lon) {
        this.NW_lon = NW_lon;
    }

    public double getNE_lat() {
        return NE_lat;
    }

    public void setNE_lat(double NE_lat) {
        this.NE_lat = NE_lat;
    }

    public double getNE_lon() {
        return NE_lon;
    }

    public void setNE_lon(double NE_lon) {
        this.NE_lon = NE_lon;
    }

    public double getSW_lat() {
        return SW_lat;
    }

    public void setSW_lat(double SW_lat) {
        this.SW_lat = SW_lat;
    }

    public double getSW_lon() {
        return SW_lon;
    }

    public void setSW_lon(double SW_lon) {
        this.SW_lon = SW_lon;
    }

    public double getSE_lat() {
        return SE_lat;
    }

    public void setSE_lat(double SE_lat) {
        this.SE_lat = SE_lat;
    }

    public double getSE_lon() {
        return SE_lon;
    }

    public void setSE_lon(double SE_lon) {
        this.SE_lon = SE_lon;
    }

    public static ShopDataModelSingleTon getInstance() {
        if (obj == null) {
            obj = new ShopDataModelSingleTon();
        }
        return obj;
    }

}
