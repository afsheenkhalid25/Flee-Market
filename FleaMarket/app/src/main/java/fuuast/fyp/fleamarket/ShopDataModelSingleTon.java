package fuuast.fyp.fleamarket;

public class ShopDataModelSingleTon {

    String name,market_id,shop_id,user_id,category1,category2,category3,category1_url,category2_url,category3_url,width,length;

    double lat,lon;

    boolean Edit_Check=false;

    static ShopDataModelSingleTon obj = new ShopDataModelSingleTon();

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCategory3() {
        return category3;
    }

    public void setCategory3(String category3) {
        this.category3 = category3;
    }

    public String getCategory1_url() {
        return category1_url;
    }

    public void setCategory1_url(String category1_url) {
        this.category1_url = category1_url;
    }

    public String getCategory2_url() {
        return category2_url;
    }

    public void setCategory2_url(String category2_url) {
        this.category2_url = category2_url;
    }

    public String getCategory3_url() {
        return category3_url;
    }

    public void setCategory3_url(String category3_url) {
        this.category3_url = category3_url;
    }

    public boolean isEdit_Check() {
        return Edit_Check;
    }

    public void setEdit_Check(boolean edit_Check) {
        Edit_Check = edit_Check;
    }

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

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
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

    public static ShopDataModelSingleTon getInstance() {
        if (obj == null) {
            obj = new ShopDataModelSingleTon();
        }
        return obj;
    }

}
