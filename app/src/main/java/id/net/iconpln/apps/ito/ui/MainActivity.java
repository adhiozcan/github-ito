package id.net.iconpln.apps.ito.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import id.net.iconpln.apps.ito.R;
import id.net.iconpln.apps.ito.config.AppConfig;
import id.net.iconpln.apps.ito.model.UserProfile;
import id.net.iconpln.apps.ito.socket.SocketTransaction;
import id.net.iconpln.apps.ito.storage.StorageTransaction;
import id.net.iconpln.apps.ito.ui.fragment.HomeFragment;
import id.net.iconpln.apps.ito.ui.fragment.PelaksanaanFragment;
import id.net.iconpln.apps.ito.ui.fragment.PelaksanaanItemFragment;
import id.net.iconpln.apps.ito.ui.fragment.PerancanganFragment;
import id.net.iconpln.apps.ito.ui.fragment.SinkronisasiFragment;
import id.net.iconpln.apps.ito.utility.StringUtils;

public class MainActivity extends AppCompatActivity implements
        PelaksanaanFragment.OnFragmentInteractionListener,
        PelaksanaanItemFragment.OnFragmentInteractionListener {
    private NavigationView navigationView;
    private DrawerLayout   drawer;
    private View           navHeader;
    private Toolbar        toolbar;
    private TextView       txtName;
    private TextView       txtNamaUnitUp;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME         = "home";
    private static final String TAG_PERANCANGAN  = "perencanaan";
    private static final String TAG_PELAKSANAAN  = "pelaksanaan";
    private static final String TAG_SINKRONISASI = "sinkronisasi";
    private static final String TAG_MONITORING   = "monitoring";
    private static final String TAG_ABOUT        = "about";
    public static        String CURRENT_TAG      = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.namajob);
        txtNamaUnitUp = (TextView) navHeader.findViewById(R.id.unitup);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        StorageTransaction<UserProfile> storageTransaction = new StorageTransaction<>();
        UserProfile                     userProfile        = storageTransaction.find(UserProfile.class);
        txtName.setText(userProfile.getNama());
        txtNamaUnitUp.setText(StringUtils.normalize(userProfile.getNamaunitup()));
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment            fragment            = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.content_frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // Perencanaan
                PerancanganFragment perancanganFragment = new PerancanganFragment();
                return perancanganFragment;
            case 2:
                // PelaksanaanRealm
                PelaksanaanFragment pelaksanaanFragment = new PelaksanaanFragment();
                return pelaksanaanFragment;
            case 3:
                // Sinkronisasi
                SinkronisasiFragment sinkronisasiFragment = new SinkronisasiFragment();
                return sinkronisasiFragment;
            case 4:
                // Monitoring
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item original icon color
        navigationView.setItemIconTintList(null);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_perancangan:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PERANCANGAN;
                        break;
                    case R.id.nav_pelaksanaan:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_PELAKSANAAN;
                        break;
                    case R.id.nav_sync:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_SINKRONISASI;
                        break;
                    case R.id.nav_monitoring:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_MONITORING;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_help:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_logout:
                        logout();
                        SocketTransaction.getInstance().stop();

                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                drawer.closeDrawers();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                loadHomeFragment();

                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("FRAGMENT_POSITION", navItemIndex);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void logout() {
        System.out.println("User will be logout, entering mode cleaning.");
        AppConfig.cleanDataSafely();

        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "Anda telah logout dengan aman", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
