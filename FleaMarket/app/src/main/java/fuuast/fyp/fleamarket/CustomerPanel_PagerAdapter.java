package fuuast.fyp.fleamarket;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CustomerPanel_PagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public CustomerPanel_PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentNearestMarkets tab1 = new FragmentNearestMarkets();
                return tab1;
            case 1:
                FragmentAllMarkets tab2 = new FragmentAllMarkets();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
