package com.example.mockup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private NodeDBDAO dao;
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_activity";
    final Handler handler = new Handler();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        //getSupportFragmentManager().putFragment(outState, "fragmentToday", fragmentToday);
        //getSupportFragmentManager().putFragment(outState, "fragmentBookmark", fragmentBookmark);
        //getSupportFragmentManager().putFragment(outState, "fragmentAll", fragmentAll);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(NOTIFY_ACTIVITY_ACTION));

        dao = new NodeDBDAO(this);

        /*
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            fragmentToday = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentToday");
            fragmentBookmark = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentBookmark");
            fragmentAll = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentAll");
        }else {
            fragmentToday = new MainActivityFragmentToday();
            fragmentBookmark = new MainActivityFragmentBookmark();
            fragmentAll = new MainActivityFragmentAll();
        }
         */

        display();
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String xmlData=intent.getStringExtra("XML Data");
            if(!xmlData.isEmpty()){
                NodeDBDAO dao = new NodeDBDAO(getApplicationContext());
                dao.insertXMLData(xmlData);
                Log.d("ApiLog","Data Inserted");
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home_settings:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void display() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new MainActivityFragment(), "Today");
        viewPagerAdapter.addFragment(new MainActivityFragment(), "Favourites");
        viewPagerAdapter.addFragment(new MainActivityFragment(), "All");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void addItemHandler(View view) {
        Intent intent = new Intent(this, InsertActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                Node node = (Node) data.getSerializableExtra("node");
                if (node != null) {
                    node.setDAO(dao);
                    Log.d("onActivityResult: ", node.getTitle());
                }else{
                    Log.d("onActivityResult: ", "NULL");
                }

                String action = data.getSerializableExtra("action").toString();

                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String dayToday = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

                switch (action) {
                    case "save":

                        Integer old_bookmark = Integer.parseInt(data.getSerializableExtra("old_bookmark").toString());
                        String old_day = data.getSerializableExtra("old_day").toString();

                        if (old_day.equals(dayToday)) {
                            if (node.getTimeDay().equals(dayToday)) {
                                ((MainActivityFragment) viewPagerAdapter.getItem(0)).editNode(node);
                            } else {
                                ((MainActivityFragment) viewPagerAdapter.getItem(0)).deleteNode(node);
                            }
                        } else if (node.getTimeDay().equals(dayToday)) {
                            ((MainActivityFragment) viewPagerAdapter.getItem(0)).insertNode(node);
                        }

                        if (old_bookmark.equals(node.getBookmarked())) {
                            ((MainActivityFragment) viewPagerAdapter.getItem(1)).editNode(node);
                        } else {
                            if (old_bookmark == 1) {
                                ((MainActivityFragment) viewPagerAdapter.getItem(1)).deleteNode(node);
                            } else {
                                ((MainActivityFragment) viewPagerAdapter.getItem(1)).insertNode(node);
                            }
                        }

                        ((MainActivityFragment) viewPagerAdapter.getItem(2)).editNode(node);

                        break;
                    case "delete":

                        if (node.getTimeDay().equals(dayToday)) {
                            ((MainActivityFragment) viewPagerAdapter.getItem(0)).deleteNode(node);
                        }
                        ((MainActivityFragment) viewPagerAdapter.getItem(1)).deleteNode(node);
                        ((MainActivityFragment) viewPagerAdapter.getItem(2)).deleteNode(node);

                        break;
                    case "insert":

                        if (node.getTimeDay().equals(dayToday)) {
                            ((MainActivityFragment) viewPagerAdapter.getItem(0)).insertNode(node);
                        }
                        if (node.getBookmarked() == 1) {
                            ((MainActivityFragment) viewPagerAdapter.getItem(1)).insertNode(node);
                        }
                        ((MainActivityFragment) viewPagerAdapter.getItem(2)).insertNode(node);
                        break;
                    default:
                        return;
                }
                Log.d("MainActivity", "Activity Result Got + " + data.getSerializableExtra("action").toString());
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private Fragment fragmentToday, fragmentBookmark, fragmentAll;

        //private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle args = new Bundle();
            args.putString("fragmentType", getPageTitle(i).toString());
            switch (getPageTitle(i).toString()) {
                case "Today":
                    if(fragmentToday == null) {
                        fragmentToday = new MainActivityFragment();
                    }
                    fragmentToday.setArguments(args);
                    return fragmentToday;
                case "Favourites":
                    if(fragmentBookmark == null)
                        fragmentBookmark = new MainActivityFragment();
                    fragmentBookmark.setArguments(args);
                    return fragmentBookmark;
                case "All":
                    if(fragmentAll == null)
                        fragmentAll = new MainActivityFragment();
                    fragmentAll.setArguments(args);
                    return fragmentAll;
            }
            return null;
            //return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 3;
            //return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            //mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            if(title.equals("Today") && fragmentToday == null){
                fragmentToday = fragment;
                Log.d("mainLog","fragmentToday saved");
            }else if(title.equals("Favourites") && fragmentBookmark == null){
                fragmentBookmark = fragment;
                Log.d("mainLog","fragmentBookmark saved");
            }else if(title.equals("All") && fragmentAll == null){
                fragmentAll = fragment;
                Log.d("mainLog","fragmentAll saved");
            }else{
                Log.d("mainLog",title + " fragment restored");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onDestroy();
    }
}