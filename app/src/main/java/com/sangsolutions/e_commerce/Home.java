package com.sangsolutions.e_commerce;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.sangsolutions.e_commerce.Adapter.ViewPagerAdapter.ViewPagerAdapter;
import com.sangsolutions.e_commerce.Fragment.CartFragment;
import com.sangsolutions.e_commerce.Fragment.CategoryFragment;
import com.sangsolutions.e_commerce.Fragment.HomeFragment;
import com.sangsolutions.e_commerce.Fragment.LanguageSelectionFragment;
import com.sangsolutions.e_commerce.Fragment.NotificationFragment;
import com.sangsolutions.e_commerce.Fragment.OrderHistoryDetailsFragment;
import com.sangsolutions.e_commerce.Fragment.ProfileFragment;
import com.sangsolutions.e_commerce.Fragment.WishListFragment;
import com.sangsolutions.e_commerce.Services.ProductStatusNotificationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Home extends AppCompatActivity {
    private ViewPager viewPager;
    private ImageView img_cart, img_wishlist, img_language,img_notification;
    private MenuItem prevMenuItem;
    private TextView cart_count, wishlist_count,notification_text;
    private Handler handler;
    private NavigationView navigationView;
    private BottomNavigationView navigation;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private boolean toolBarNavigationListenerIsRegistered = false;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home:
                    RemoveFragment();
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.category:
                    RemoveFragment();
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.wishlist:
                    RemoveFragment();
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.cart:
                    RemoveFragment();
                    viewPager.setCurrentItem(3);
                    return true;
                case R.id.profile:
                    RemoveFragment();
                    viewPager.setCurrentItem(4);
                    return true;
            }

            return true;
        }
    };


    public void initializeViews() {
        setContentView(R.layout.activity_home);
        img_language = findViewById(R.id.language);
        navigationView = findViewById(R.id.nav_view);
        FrameLayout frameLayout = findViewById(R.id.fragment);
        drawerLayout = findViewById(R.id.drawer_layout);

        img_wishlist = findViewById(R.id.favorites);
        cart_count = findViewById(R.id.cart_count);
        wishlist_count = findViewById(R.id.wishlist_count);
        img_notification = findViewById(R.id.img_notification);
        notification_text = findViewById(R.id.notification_text);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigation = findViewById(R.id.botNav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = findViewById(R.id.viewPager);

        img_cart = findViewById(R.id.cart);


        setUpViewPager(viewPager);
    }

    public void LoadNotification(){
        if (!Tools.getUserId(this).equals("0"))
            AndroidNetworking.get(URLs.getOrderConfirmAlert)
                    .addQueryParameter("iUser", Tools.getUserId(this))
                    .addQueryParameter("iStatus", "0")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response.getString("getConfirmDetails"));

                                if(jsonArray.length()>0){
                                    notification_text.setVisibility(View.VISIBLE);
                                }else {
                                    notification_text.setVisibility(View.INVISIBLE);
                                }

                            } catch (JSONException e) {
                                notification_text.setVisibility(View.INVISIBLE);
                                e.printStackTrace();
                                FirebaseCrashlytics.getInstance().recordException(new Throwable("getOrderConfirmAlert\n"+e.getMessage()));
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            notification_text.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "onError: " + anError.getResponse());
                            FirebaseCrashlytics.getInstance().recordException(new Throwable("getOrderConfirmAlert\n"+anError.getErrorDetail()));
                        }
                    });
    }

    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        initializeViews();
        if (lang.equals("en")) {
            img_language.setImageResource(R.drawable.ic_uk);
            ViewCompat.setLayoutDirection(drawerLayout, ViewCompat.LAYOUT_DIRECTION_LTR);
        } else {
            img_language.setImageResource(R.drawable.ic_oman);
            ViewCompat.setLayoutDirection(drawerLayout, ViewCompat.LAYOUT_DIRECTION_RTL);
        }

    }

    private void GetCartAndWishlistCount() {
        LoadNotification();
        File cartFile = new File(getExternalFilesDir(null), "cart.json");
        File wishlistFile = new File(getExternalFilesDir(null), "wishlist.json");


        if (cartFile.exists()) {
            try {
                String sCartJson = Tools.readFile(this, "cart.json");
                JSONArray jsonArray = new JSONArray(sCartJson);

                if (jsonArray.length() > 0) {
                    cart_count.setVisibility(View.VISIBLE);
                    if (jsonArray.length() > 9) {
                        cart_count.setText("9+");
                    } else {
                        cart_count.setText(String.valueOf(jsonArray.length()));
                    }
                } else {
                    cart_count.setVisibility(View.INVISIBLE);
                }


            } catch (FileNotFoundException | JSONException e) {
                cart_count.setVisibility(View.INVISIBLE);
               // e.printStackTrace();
            }
        } else {
            cart_count.setVisibility(View.INVISIBLE);
        }


        if (wishlistFile.exists()) {

            try {
                String sWishlistJson = Tools.readFile(this, "wishlist.json");
                JSONArray jsonArray = new JSONArray(sWishlistJson);

                if (jsonArray.length() > 0) {
                    wishlist_count.setVisibility(View.VISIBLE);
                    if (jsonArray.length() > 9) {
                        wishlist_count.setText("9+");
                    } else {
                        wishlist_count.setText(String.valueOf(jsonArray.length()));

                    }
                } else {
                    wishlist_count.setVisibility(View.INVISIBLE);
                }


            } catch (FileNotFoundException | JSONException e) {
                wishlist_count.setVisibility(View.INVISIBLE);
                //e.printStackTrace();
            }
        } else {
            wishlist_count.setVisibility(View.INVISIBLE);
        }

    }

    public void SetAlarm(Context context) {

        ComponentName receiver = new ComponentName(getApplicationContext(), Home.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


       /* AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getBaseContext(), ProductStatusNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, alarmIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 10000, pendingIntent);*/
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),60*1000, pendingIntent);


        Intent intent = new Intent(Home.this, ProductStatusNotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(Home.this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+10000, 60*1000, pendingIntent);
    }

    private void enableViews(boolean enable) {

        if (enable) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            if (!toolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

                toolBarNavigationListenerIsRegistered = true;
            }

        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            toolBarNavigationListenerIsRegistered = false;
        }
    }

    public ViewPager getViewPager() {
        if (null == viewPager) {
            viewPager = findViewById(R.id.viewPager);
        }
        return viewPager;
    }

    public void GotoOrderHistoryDetails(int OrderId,String iId){
        Fragment fragment = new OrderHistoryDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("OrderId", String.valueOf(OrderId));
        bundle.putString("iId", iId);
        fragment.setArguments(bundle);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        tx.replace(R.id.fragment, fragment).addToBackStack("orderListDetails").commit();
    }

    public void RemoveFragment() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.add(new HomeFragment(), "Home");
        adapter.add(new CategoryFragment(), "Category");
        adapter.add(new WishListFragment(), "Wishlist");
        adapter.add(new CartFragment(), "Cart");
        adapter.add(new ProfileFragment(), "Profile");


        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        android.app.FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for disabling firebase when
       FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(Tools.FireBaseCrashlyticsStatus());
        //for language
        SharedPreferences preferences = getSharedPreferences("language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //for login
        SharedPreferences preferences2 = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = preferences2.edit();
        SetAlarm(Home.this);

        if (Objects.equals(preferences.getString("language", ""), "")) {
            editor.putString("language", "english").apply();
        }



        if (Objects.equals(preferences.getString("language", ""), "english")) {
            setLocale("en");
        } else {
            setLocale("ar");
        }

      //  startService(new Intent(getBaseContext(), ProductStatusNotificationService.class));


        Intent intent = getIntent();

        if(intent!=null){
         int iOrderId = intent.getIntExtra("iOrderId",0);
            String iId = intent.getStringExtra("iId");
            if(iOrderId!=0){
                GotoOrderHistoryDetails(iOrderId,iId);
            }
        }



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        RemoveFragment();
                        viewPager.setCurrentItem(0);
                        drawerLayout.close();
                        return true;
                    case R.id.category:
                        RemoveFragment();
                        viewPager.setCurrentItem(1);
                        drawerLayout.close();
                        return true;
                    case R.id.wishlist:
                        RemoveFragment();
                        viewPager.setCurrentItem(2);
                        drawerLayout.close();
                        return true;
                    case R.id.cart:
                        RemoveFragment();
                        viewPager.setCurrentItem(3);
                        drawerLayout.close();
                        return true;
                    case R.id.profile:
                        RemoveFragment();
                        viewPager.setCurrentItem(4);
                        drawerLayout.close();
                        return true;
                }
                return true;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }


                Log.d("page", "onPageSelected: " + position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        img_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveFragment();
                viewPager.setCurrentItem(3);
            }
        });
        img_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveFragment();
                viewPager.setCurrentItem(2);
            }
        });

        //Changing text in nav header
        View headerView = navigationView.getHeaderView(0);
        TextView name = headerView.findViewById(R.id.name);
        TextView signin = headerView.findViewById(R.id.signin);
        TextView description = headerView.findViewById(R.id.description);
        ImageView img_edit = headerView.findViewById(R.id.edit);
        if (!Objects.equals(preferences2.getString("sUserName", ""), "")) {
            name.setText(preferences2.getString("sUserName", ""));
            signin.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            img_edit.setVisibility(View.VISIBLE);
            img_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Home.this, Register.class);
                    intent.putExtra("edit", true);
                    startActivity(intent);
                }
            });
        } else {
            name.setText(getString(R.string.welcome_guest));
            signin.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            img_edit.setVisibility(View.GONE);
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, MainActivity.class);
                startActivity(intent);
            }
        });


        img_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new LanguageSelectionFragment();
                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("language").commit();
            }
        });


        img_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NotificationFragment();
                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                tx.replace(R.id.fragment, fragment).addToBackStack("notification").commit();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fr = getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (fr != null) {
                    enableViews(true);
                } else {
                    enableViews(false);
                }
            }
        });

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                GetCartAndWishlistCount();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);


    }
}