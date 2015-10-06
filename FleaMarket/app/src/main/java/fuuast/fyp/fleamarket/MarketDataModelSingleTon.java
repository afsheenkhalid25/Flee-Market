package fuuast.fyp.fleamarket;

public class MarketDataModelSingleTon {

    private String market_id,admin_id,market_name,market_address;

    static MarketDataModelSingleTon obj = new MarketDataModelSingleTon();

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
