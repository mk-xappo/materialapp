package de.xappo.materialapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import de.xappo.materialapp.extras.SortListener;
import de.xappo.materialapp.fragments.FragmentBoxOffice;
import de.xappo.materialapp.fragments.FragmentSearch;
import de.xappo.materialapp.fragments.FragmentUpcomming;
import de.xappo.materialapp.fragments.NavigationDrawerFragment;
import de.xappo.materialapp.R;
import de.xappo.materialapp.logging.L;
import de.xappo.materialapp.views.SlidingTabLayout;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class MainActivity extends ActionBarActivity implements MaterialTabListener, View.OnClickListener {

    private Toolbar toolbar;
    private SlidingTabLayout mTabs;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    public static final int MOVIES_SEARCH_RESULTS = 0;
    public static final int MOVIES_HITS = 1;
    public static final int MOVIES_UPCOMING = 2;
    public static final int TAB_COUNT = 3;
    public static final String TAG_SORT_NAME = "sortName";
    public static final String TAG_SORT_DATE = "sortDate";
    public static final String TAG_SORT_RATINGS = "sortRatings";


    private MaterialTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        tabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);

            }
        });

        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(tabHost.newTab().setIcon(adapter.getIcon(i)).setTabListener(this));
        }
        buildFAB();
    }

    private void buildFAB() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_action_new);

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(imageView)
                .setBackgroundDrawable(R.drawable.selector_button_red)
                .build();

        ImageView iconSortName = new ImageView(this);
        iconSortName.setImageResource(R.drawable.ic_action_alphabets);
        ImageView iconSortDate = new ImageView(this);
        iconSortDate.setImageResource(R.drawable.ic_action_calendar);
        ImageView iconSortRatings = new ImageView(this);
        iconSortRatings.setImageResource(R.drawable.ic_action_important);
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_sub_button_gray));

        SubActionButton buttonSortName = itemBuilder.setContentView(iconSortName).build();
        SubActionButton buttonSortDate = itemBuilder.setContentView(iconSortDate).build();
        SubActionButton buttonSortRatings = itemBuilder.setContentView(iconSortRatings).build();

        buttonSortName.setTag(TAG_SORT_NAME);
        buttonSortDate.setTag(TAG_SORT_DATE);
        buttonSortRatings.setTag(TAG_SORT_RATINGS);

        buttonSortName.setOnClickListener(this);
        buttonSortDate.setOnClickListener(this);
        buttonSortRatings.setOnClickListener(this);

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonSortName)
                .addSubActionView(buttonSortDate)
                .addSubActionView(buttonSortRatings)
                .attachTo(actionButton)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.navigate) {
            startActivity(new Intent(this, SubActivity.class));
        }

        if (R.id.action_tabs_using_library == id) {
            startActivity(new Intent(this, ActivitySlidingTabLayout.class));
        }

        if (R.id.action_vector_test == id) {
            startActivity(new Intent(this, VectorTestActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        viewPager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }

    @Override
    public void onClick(View v) {
        Fragment fragment = (Fragment) adapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        if (fragment instanceof SortListener) {
            if (v.getTag().equals(TAG_SORT_NAME)) {
                ((SortListener) fragment).onSortByName();
            }
            if (v.getTag().equals(TAG_SORT_DATE)) {
                ((SortListener) fragment).onSortByDate();
            }
            if (v.getTag().equals(TAG_SORT_RATINGS)) {
                ((SortListener) fragment).onSortByRatings();
            }
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int icons[] = {R.drawable.ic_action_home, R.drawable.ic_action_articles, R.drawable.ic_action_personal};
        String[] tabText = getResources().getStringArray(R.array.tabs);

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            tabText = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int num) {
            Fragment fragment = null;
            switch (num) {
                case MOVIES_SEARCH_RESULTS:
                    fragment = FragmentSearch.newInstance("", "");
                    break;
                case MOVIES_HITS:
                    fragment = FragmentBoxOffice.newInstance("", "");
                    break;
                case MOVIES_UPCOMING:
                    fragment = FragmentUpcomming.newInstance("", "");
                    break;

            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(icons[position]);
            drawable.setBounds(0, 0, 36, 36);
            ImageSpan imageSpan = new ImageSpan(drawable);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        private Drawable getIcon(int position) {
            return getResources().getDrawable(icons[position]);
        }
    }



}

