package fuuast.fyp.fleamarket;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Shop {

    private LatLng location,NorthWest,NorthEast,SouthEast,SouthWest;
    private Double width,height;
    private Double Change_latitude,Change_longitude;

    public Shop(Double latitude, Double longitude, Double width, Double height) {
        location = new LatLng(latitude,longitude);
        this.width=width;
        this.height=height;
        setShopParam();
    }

    public LatLng getLocation() {
        return location;
    }

    public LatLng getNorthWest() {
        return NorthWest;
    }

    public LatLng getNorthEast() {
        return NorthEast;
    }

    public LatLng getSouthEast() {
        return SouthEast;
    }

    public LatLng getSouthWest() {
        return SouthWest;
    }

    public Double getWidth(){
        return width;
    }

    public Double getHeight(){
        return height;
    }

    public void setShopParam(){

        Change_latitude=0.00000625*(height/2);
        Change_longitude=0.00000666*(width/2);

        Double up,down,left,right;
        up=location.latitude+Change_latitude;
        down=location.latitude-Change_latitude;
        left=location.longitude-Change_longitude;
        right=location.longitude+Change_longitude;

        NorthWest=new LatLng(up, left);
        NorthEast=new LatLng(up, right);
        SouthEast=new LatLng(down, right);
        SouthWest=new LatLng(down, left);

        Log.d("NORTH WEST",NorthWest.latitude+"---"+NorthWest.longitude);
        Log.d("SOUTH EAST",SouthEast.latitude+"---"+SouthEast.longitude);
        Log.d("SOUTH WEST",SouthWest.latitude+"---"+SouthWest.longitude);
        Log.d("NORTH EAST",NorthEast.latitude+"---"+NorthEast.longitude);

    }
}
