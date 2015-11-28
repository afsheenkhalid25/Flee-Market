package fuuast.fyp.fleamarket;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Checker {

    public boolean shopOverlapping(Shop newShop,Shop oldShop){

        if (newShop.getLocation().latitude<oldShop.getNorthWest().latitude && newShop.getLocation().latitude>oldShop.getSouthWest().latitude){
            if (newShop.getLocation().longitude>oldShop.getNorthWest().longitude&&newShop.getLocation().longitude<oldShop.getNorthEast().longitude){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public boolean NWOverlapping(Shop newShop,Shop oldShop){

        if (newShop.getNorthWest().latitude<oldShop.getNorthWest().latitude && newShop.getNorthWest().latitude>oldShop.getSouthWest().latitude){
            if (newShop.getNorthWest().longitude>oldShop.getNorthWest().longitude&&newShop.getNorthWest().longitude<oldShop.getNorthEast().longitude){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public boolean NEOverlapping(Shop newShop,Shop oldShop){

        if (newShop.getNorthEast().latitude<oldShop.getNorthWest().latitude && newShop.getNorthEast().latitude>oldShop.getSouthWest().latitude){
            if (newShop.getNorthEast().longitude>oldShop.getNorthWest().longitude&&newShop.getNorthEast().longitude<oldShop.getNorthEast().longitude){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public boolean SEOverlapping(Shop newShop,Shop oldShop){

        if (newShop.getSouthEast().latitude<oldShop.getNorthWest().latitude && newShop.getSouthEast().latitude>oldShop.getSouthWest().latitude){
            if (newShop.getSouthEast().longitude>oldShop.getNorthWest().longitude&&newShop.getSouthEast().longitude<oldShop.getNorthEast().longitude){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public boolean SWOverlapping(Shop newShop,Shop oldShop){

        if (newShop.getSouthWest().latitude<oldShop.getNorthWest().latitude && newShop.getSouthWest().latitude>oldShop.getSouthWest().latitude){
            if (newShop.getSouthWest().longitude>oldShop.getNorthWest().longitude&&newShop.getSouthWest().longitude<oldShop.getNorthEast().longitude){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    public Shop NWResizing(Shop newShop,Shop oldShop){

        Log.d("cheking","NW RESIZING");
        Shop resizedShop=null;
        Double lat=null,lon=null,height=null,width=null;
        LatLng latLng=null;
        Double check1=(newShop.getNorthWest().latitude-oldShop.getSouthEast().latitude);
        Double check2= (oldShop.getSouthEast().longitude-newShop.getNorthWest().longitude);

        if((check1-check2)>-1 && (check1-check2)<1){
            Log.d("CHECKER-------------->","EQUAL");
            lat=(oldShop.getSouthEast().latitude+newShop.getSouthWest().latitude)/2;
            lon=(oldShop.getSouthEast().longitude+newShop.getNorthEast().longitude)/2;
            Log.d("LATITUDE",""+lat);
            Log.d("LONGITUDE",""+lon);
            height=(newShop.getSouthWest().latitude-oldShop.getSouthEast().latitude)/0.00000625;
            width=(newShop.getNorthEast().longitude-oldShop.getSouthEast().longitude)/0.00000666;
            resizedShop =new Shop(lat,lon,width,height);
        }
        else if (check1<check2){
            Log.d("CHECKER-------------->","CHECK1");
            Log.d("checked1",""+newShop.getNorthWest().latitude);
            Log.d("checked2",""+oldShop.getSouthEast().latitude);
            lat=(newShop.getSouthWest().latitude+oldShop.getSouthEast().latitude)/2;
            lon=newShop.getLocation().longitude;
            Log.d("checked3",""+lat);

            height=(oldShop.getSouthEast().latitude-newShop.getSouthWest().latitude)/0.00000625;
            width=newShop.getWidth();

            resizedShop =new Shop(lat,lon,width,height);
        }
        else if(check2<check1) {
            Log.d("CHECKER-------------->","CHECK2");
            lat = newShop.getLocation().latitude;
            lon = (newShop.getNorthEast().longitude + oldShop.getSouthEast().longitude) / 2;

            height = newShop.getHeight();
            width = (newShop.getNorthEast().longitude - oldShop.getSouthEast().longitude) / 0.00000666;

            resizedShop=new Shop(lat,lon,width,height);
        }
        Log.d("CHECKER-------------->","CHECKs COMPLETED");
        return resizedShop;
    }

    public Shop SEResizing(Shop newShop,Shop oldShop){
        Log.d("cheking","SE RESIZING");
        Shop resizedShop=null;
        Double lat=null,lon=null,height=null,width=null;
        LatLng latLng=null;

        Double check1=(oldShop.getNorthWest().latitude-newShop.getSouthEast().latitude);
        Double check2= (newShop.getSouthEast().longitude-oldShop.getNorthWest().longitude);

        if ((check1-check2)>-1 && (check1-check2)<1){
            lat=(newShop.getNorthWest().latitude+oldShop.getNorthWest().latitude)/2;
            lon=(newShop.getNorthWest().longitude+oldShop.getNorthWest().longitude)/2;
            width=(newShop.getNorthWest().longitude-oldShop.getNorthWest().longitude)/0.00000666;
            height=(newShop.getNorthWest().latitude-oldShop.getNorthWest().latitude)/0.00000625;

            resizedShop =new Shop(lat,lon,width,height);
        }
        else if (check1<check2){
            lat=(newShop.getNorthEast().latitude+oldShop.getNorthWest().latitude)/2;
            lon=newShop.getLocation().longitude;

            height=(newShop.getNorthEast().latitude-oldShop.getNorthWest().latitude)/0.00000625;
            width=newShop.getWidth();

            resizedShop =new Shop(lat,lon,width,height);
        }
        else if(check2<check1){
            lat=newShop.getLocation().latitude;
            lon=(oldShop.getNorthWest().longitude+newShop.getSouthWest().longitude)/2;
            width=(oldShop.getNorthWest().longitude-newShop.getSouthWest().longitude)/0.00000666;
            height=newShop.getHeight();

            resizedShop =new Shop(lat,lon,width,height);
        }
        return resizedShop;
    }

    public Shop SWResizing(Shop newShop,Shop oldShop){
        Log.d("cheking","SW RESIZING");
        Shop resizedShop=null;
        Double lat=null,lon=null,height=null,width=null;
        LatLng latLng=null;

        Double check1=(oldShop.getNorthEast().latitude-newShop.getSouthWest().latitude);
        Double check2= (oldShop.getNorthEast().longitude-newShop.getSouthWest().longitude);

        if ((check1-check2)>-1 && (check1-check2)<1){
            lat=(newShop.getNorthEast().latitude+oldShop.getNorthEast().latitude)/2;
            lon=(newShop.getNorthEast().longitude+oldShop.getNorthEast().longitude)/2;
            width=(newShop.getNorthEast().longitude-oldShop.getNorthEast().longitude)/0.00000666;
            height=(newShop.getNorthEast().latitude-oldShop.getNorthEast().latitude)/0.00000625;

            resizedShop =new Shop(lat,lon,width,height);
        }
        else if (check1<check2){
            lat=(newShop.getNorthEast().latitude+oldShop.getNorthEast().latitude)/2;
            lon=newShop.getLocation().longitude;

            height=(newShop.getNorthEast().latitude-oldShop.getNorthEast().latitude)/0.00000625;
            width=newShop.getWidth();

            resizedShop =new Shop(lat,lon,width,height);
        }
        else if(check2<check1){
            lat=newShop.getLocation().latitude;
            lon=(oldShop.getNorthEast().longitude+newShop.getNorthEast().longitude)/2;
            width=(newShop.getNorthEast().longitude-oldShop.getNorthEast().longitude)/0.00000666;
            height=newShop.getHeight();

            resizedShop =new Shop(lat,lon,width,height);
        }
        return resizedShop;
    }

    public Shop NEResizing(Shop newShop,Shop oldShop){
        Log.d("cheking","SW RESIZING");
        Shop resizedShop=null;
        Double lat=null,lon=null,height=null,width=null;
        LatLng latLng=null;

        Double check1=(newShop.getNorthEast().latitude-oldShop.getSouthWest().latitude);
        Double check2= (newShop.getNorthEast().longitude-oldShop.getSouthWest().longitude);

        if ((check1-check2)>-1 && (check1-check2)<1) {

            lat = (newShop.getSouthWest().latitude + oldShop.getSouthWest().latitude) / 2;
            lon = (newShop.getSouthWest().longitude + oldShop.getSouthWest().longitude) / 2;
            width = (oldShop.getSouthWest().longitude - newShop.getSouthWest().longitude) / 0.00000666;
            height = (oldShop.getSouthWest().latitude - newShop.getSouthWest().latitude) / 0.00000625;

            resizedShop = new Shop(lat, lon, width, height);
        }

        else if (check1<check2) {
            lat = (newShop.getSouthWest().latitude + oldShop.getSouthWest().latitude) / 2;
            lon = newShop.getLocation().longitude;
            height = (oldShop.getSouthWest().latitude - newShop.getSouthWest().latitude) / 0.00000625;
            width = newShop.getWidth();

            resizedShop = new Shop(lat, lon, width, height);
        }
        else if(check2<check1) {
            lat = newShop.getLocation().latitude;
            lon = (newShop.getNorthWest().longitude + oldShop.getSouthWest().longitude) / 2;
            width = (oldShop.getNorthWest().longitude - newShop.getNorthWest().longitude) / 0.00000666;
            height = newShop.getHeight();

            resizedShop = new Shop(lat, lon, width, height);
        }


        return resizedShop;
    }

}
