package fuuast.fyp.fleamarket;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MarketView_PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public MarketView_PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentMarketDetails tab1 = new FragmentMarketDetails();
                return tab1;
            case 1:
                FragmentShopList tab2 = new FragmentShopList();
                return tab2;
            case 2:
                FragmentMapView tab3 = new FragmentMapView();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
