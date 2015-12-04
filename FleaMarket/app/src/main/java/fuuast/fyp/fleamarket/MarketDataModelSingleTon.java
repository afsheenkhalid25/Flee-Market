package fuuast.fyp.fleamarket;

public class MarketDataModelSingleTon {

    private String market_id,admin_id,market_name,market_address,market_lat,market_lon;

    static MarketDataModelSingleTon obj = new MarketDataModelSingleTon();

    public String getMarket_lat() {
        return market_lat;
    }

    public void setMarket_lat(String market_lat) {
        this.market_lat = market_lat;
    }

    public String getMarket_lon() {
        return market_lon;
    }

    public void setMarket_lon(String market_lon) {
        this.market_lon = market_lon;
    }

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getMarket_address() {
        return market_address;
    }

    public void setMarket_address(String market_area) {
        this.market_address = market_area;
    }

    public static MarketDataModelSingleTon getInstance() {
        if (obj == null) {
            obj = new MarketDataModelSingleTon();
        }
        return obj;
    }
}
