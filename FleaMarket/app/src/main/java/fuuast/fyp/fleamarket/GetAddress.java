package fuuast.fyp.fleamarket;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetAddress {

    public volatile boolean parsingComplete = true;
    Double lat,lng;
    private String city="None";
    Geocoder geocoder;
    List<Address> addresses = null;

    public GetAddress(Context mContext,Double lat,Double lng) {
        this.lat=lat;
        this.lng=lng;
        geocoder = new Geocoder(mContext, Locale.getDefault());
    }

    public String getCity(){
        return city;
    }

    public void get(){
        Thread thread = new Thread( new Runnable(){
            @Override
            public void run() {
                try {
                    addresses = geocoder.getFromLocation(lat,lng, 1);
                } catch (IllegalArgumentException e2) {
                    parsingComplete = false;
                } catch (IOException e1) {
                    parsingComplete = false;
                } catch (Exception e) {
                    parsingComplete = false;
                }
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    city = String.format("%s %s", address.getAddressLine(0),address.getLocality());
                    parsingComplete = false;
                }
            }
        });
        thread.start();
    }
}
