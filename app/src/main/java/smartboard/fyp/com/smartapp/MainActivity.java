package smartboard.fyp.com.smartapp;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

public class MainActivity extends AppCompatActivity {
    private static MainActivity activity;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tabProfile;
    private AccountHeader header = null;
    private Drawer result = null;

    public static MainActivity getInstance() {
        if (activity == null) {
            activity = new MainActivity();
        }
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tablayout);
        tabProfile = findViewById(R.id.tab_Profile);
        viewPager = (findViewById(R.id.viewpager));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.material_drawer_background));
        }

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final IProfile profile = new ProfileDrawerItem().withName("Zain Ali Khalid").withEmail("zain.khalid@gmail.com").withIcon(R.drawable.user).withIdentifier(100);

        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.material_drawer_user_background)
                .addProfiles(profile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == 100000) {
                            int count = 100 + header.getProfiles().size() + 1;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(false)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("Profile").withIdentifier(201),
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("Assignments").withIdentifier(202),
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("Quizes").withIdentifier(203),
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("Internal Evaluation").withIdentifier(204),
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("Attendence").withIdentifier(205),
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("abc").withIdentifier(206),
                        new PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_settings).withName("xyz").withIdentifier(207)
                ).withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem instanceof Nameable) {
                        if (drawerItem.getIdentifier() == 207) {
                            /*Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);*/
                        }
                    }
                    return false;
                })
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        result.setSelection(-1);
    }
}
