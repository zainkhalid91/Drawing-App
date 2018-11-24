package smartboard.fyp.com.smartapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.raed.drawingview.BrushView;
import com.raed.drawingview.DrawingView;
import com.raed.drawingview.brushes.BrushSettings;
import com.raed.drawingview.brushes.Brushes;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static MainActivity activity;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    //TabItem tabProfile;

    /*FAB declaration*/
    private boolean isFabOpen = false;
    BrushSettings settings;
    private Animation fab_open, fab_close, rotate_forward, rotate_backwards;


    public static final String DRAWING_PATH = "DrawingPath";
    public static final int UTILITIY_PENCIL = 1;
    public static final int UTILITIY_ERASER = 2;
    public static final int UTILITIY_AIR_BRUSH = 3;
    public static final int UTILITIY_CALLIGRAPHY = 4;
    public static final int UTILITIY_PEN = 5;
    //Constants
    private final int IMAGE_COMPRESSION_QUALITY = 85;
    private final int REQ_WRITE_EXTERNAL_STORAGE = 1;
    MyView myView;
    boolean flag = false;
    private Menu menu;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private DrawingView drawing_view;
    private ImageView color_preview;
    private TextView current_utility;
    private FloatingActionButton fab, fab1, fab2, fab3, fab4;
    private ImageView arrow;
    private BrushView brush_view;
    //Variables as objects
    private ImageView calligraphy, pen, airbrush, eraser, pencil;
    private TextView current_size, resetZoom_tv;
    private SeekBar size;
    private ImageView overflow_menu, save_menu, ppt_menu, pdf_menu, resetZoom_menu, clearCanvas_menu, exit_menu;
    private ImageView choose_color;


    private RadioButton background_color;
    private RadioButton background_image;
    private ImageView background_color_preview;
    private Button choose_background_color;
    private ImageView background_image_preview;
    private Button choose_background_image;
    private ColorPickerDialog color_picker;
    private int current_color = Color.BLACK;
    private int current_background_color = Color.parseColor("#eeeeee");
    //Variables
    private boolean pressedOnce;
    private ImageView image_color_white, image_color_black,
            image_color_red, image_color_yellow, image_color_green,
            image_color_blue, image_color_pink, image_color_brown,
            image_color_light_brown, image_color_dark_brown, image_color_light_pink, image_color_dark_red,
            image_color_orange, image_color_light_orange, image_color_light_green, image_color_dark_green,
            image_color_light_blue, image_color_dark_blue, image_color_purle, image_color_dark_grey;
    private RelativeLayout menuLayout;
    private Drawer result = null;

    public static MainActivity getInstance() {
        if (activity == null) {
            activity = new MainActivity();
        }
        return activity;
    }

    /*Layout Handling*/
    /*navigation drawer*/
    private AccountHeader header = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_drawing, menu);
        menu.findItem(R.id.action_undo).getIcon().setAlpha(130);
        menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
        menu.findItem(R.id.action_done).getIcon().setAlpha(130);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(darkenColor(getResources().getColor(R.color.colorPrimary)));



         /*tabLayout = findViewById(R.id.tablayout);
        tabProfile = findViewById(R.id.tab_Profile);
        viewPager = (findViewById(R.id.viewpager));*/

        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab1.setSize(FloatingActionButton.SIZE_NORMAL);
        fab2 = findViewById(R.id.fab2);

        fab2.setSize(FloatingActionButton.SIZE_NORMAL);
        fab3 = findViewById(R.id.fab3);
        fab3.setSize(FloatingActionButton.SIZE_NORMAL);
        fab4 = findViewById(R.id.fab4);
        fab4.setSize(FloatingActionButton.SIZE_NORMAL);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_backwards = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backwards);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);

        menuLayout = findViewById(R.id.menu_layout);

        overflow_menu = findViewById(R.id.overflow_menu);
        save_menu = findViewById(R.id.save_menu);
        ppt_menu = findViewById(R.id.ppt_menu);
        pdf_menu = findViewById(R.id.pdf_menu);
        clearCanvas_menu = findViewById(R.id.clearCanvas_menu);
        resetZoom_menu = findViewById(R.id.resetZoom_menu);
        exit_menu = findViewById(R.id.exitApp_menu);
        overflow_menu.setOnClickListener(this);
        save_menu.setOnClickListener(this);
        ppt_menu.setOnClickListener(this);
        pdf_menu.setOnClickListener(this);
        resetZoom_menu.setOnClickListener(this);
        clearCanvas_menu.setOnClickListener(this);
        exit_menu.setOnClickListener(this);


        image_color_white = findViewById(R.id.image_color_white);
        image_color_light_brown = findViewById(R.id.image_color_light_brown);
        image_color_brown = findViewById(R.id.image_color_brown);
        image_color_dark_brown = findViewById(R.id.image_color_dark_brown);
        image_color_light_pink = findViewById(R.id.image_color_light_pink);
        image_color_pink = findViewById(R.id.image_color_pink);
        image_color_red = findViewById(R.id.image_color_red);
        image_color_dark_red = findViewById(R.id.image_color_dark_red);
        image_color_orange = findViewById(R.id.image_color_orange);
        image_color_light_orange = findViewById(R.id.image_color_light_orange);
        image_color_yellow = findViewById(R.id.image_color_yellow);
        image_color_light_green = findViewById(R.id.image_color_light_green);
        image_color_green = findViewById(R.id.image_color_green);
        image_color_dark_green = findViewById(R.id.image_color_dark_green);
        image_color_light_blue = findViewById(R.id.image_color_light_blue);
        image_color_blue = findViewById(R.id.image_color_blue);
        image_color_dark_blue = findViewById(R.id.image_color_dark_blue);
        image_color_purle = findViewById(R.id.image_color_purle);
        image_color_dark_grey = findViewById(R.id.image_color_dark_grey);
        image_color_black = findViewById(R.id.image_color_black);
        ColorSelector();




        slidingUpPanelLayout = findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                arrow.setRotation(slideOffset * 180.0f);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });

        //Initialize DrawingView
        drawing_view = findViewById(R.id.drawing_view);
        drawing_view.setUndoAndRedoEnable(true);
        drawing_view.getBrushSettings().setSelectedBrushSize(0.25f);
        drawing_view.setOnDrawListener(new DrawingView.OnDrawListener() {
            @Override
            public void onDraw() {
                menu.findItem(R.id.action_undo).setEnabled(true);
                menu.findItem(R.id.action_undo).getIcon().setAlpha(255);
                menu.findItem(R.id.action_redo).setEnabled(false);
                menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
                menu.findItem(R.id.action_done).setEnabled(true);
                menu.findItem(R.id.action_done).getIcon().setAlpha(255);
                menuLayout.setVisibility(View.INVISIBLE);

            }
        });

        drawing_view.clear();

        //Initialize Preview
        color_preview = findViewById(R.id.color_preview);
        current_utility = findViewById(R.id.current_utility);
        current_utility.setText(getString(R.string.current_utility_) + " " + getString(R.string.pen));
        current_size = findViewById(R.id.current_size);
        current_size.setText(getString(R.string.current_size_) + " 25%");
        arrow = findViewById(R.id.arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(slidingUpPanelLayout.getPanelState() ==
                        SlidingUpPanelLayout.PanelState.EXPANDED ? SlidingUpPanelLayout.PanelState.COLLAPSED : SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        brush_view = findViewById(R.id.brush_view);
        brush_view.setDrawingView(drawing_view);

        settings = drawing_view.getBrushSettings();

        //color_preview_slide = findViewById(R.id.color_preview_slide);
        choose_color = findViewById(R.id.choose_color);
        choose_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker = new ColorPickerDialog(MainActivity.this, current_color);
                color_picker.setAlphaSliderVisible(false);
                color_picker.setHexValueEnabled(true);
                color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_color = color;
                        color_preview.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                        //color_preview_slide.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                        settings.setColor(color);
                    }
                });
                color_picker.show();
            }
        });


        size = findViewById(R.id.size);
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                settings.setSelectedBrushSize(i / 100.0f);
                current_size.setText(String.format("%s %s%%", getResources().getString(R.string.current_size_), String.valueOf(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        pencil = findViewById(R.id.utility_pencil);

        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.setSelectedBrush(Brushes.PENCIL);
                settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                current_utility.setText(getString(R.string.pencil));
            }
        });


        eraser = findViewById(R.id.utility_eraser);
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.setSelectedBrush(Brushes.ERASER);
                settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                current_utility.setText(getString(R.string.eraser));
            }
        });

        pen = findViewById(R.id.utility_pen);
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.setSelectedBrush(Brushes.PEN);
                settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                current_utility.setText(getString(R.string.pen));
            }
        });


        calligraphy = findViewById(R.id.utility_calligraphy);
        calligraphy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.setSelectedBrush(Brushes.CALLIGRAPHY);
                settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                current_utility.setText(getString(R.string.calligraphy));
            }
        });

        airbrush = findViewById(R.id.utility_airbrush);
        airbrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.setSelectedBrush(Brushes.AIR_BRUSH);
                settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                current_utility.setText(getString(R.string.air_brush));
            }
        });

        if (getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, MainActivity.UTILITIY_PENCIL) == MainActivity.UTILITIY_ERASER)
            ;
        if (getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, MainActivity.UTILITIY_PENCIL) == MainActivity.UTILITIY_AIR_BRUSH)
            ;
        if (getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, MainActivity.UTILITIY_PENCIL) == MainActivity.UTILITIY_CALLIGRAPHY)
            ;
        if (getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, MainActivity.UTILITIY_PENCIL) == MainActivity.UTILITIY_PEN)
            ;

        //Background
        background_color = findViewById(R.id.background_color);
        background_color_preview = findViewById(R.id.background_color_preview);
        choose_background_color = findViewById(R.id.choose_background_color);
        background_image = findViewById(R.id.background_image);
        background_image_preview = findViewById(R.id.background_image_preview);
        choose_background_image = findViewById(R.id.choose_background_image);
        background_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                background_color_preview.setEnabled(b);

                choose_background_color.setEnabled(b);
            }
        });

        background_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                background_image_preview.setEnabled(b);
                choose_background_image.setEnabled(b);
            }
        });
        choose_background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker = new ColorPickerDialog(MainActivity.this, current_background_color);
                color_picker.setAlphaSliderVisible(false);
                color_picker.setHexValueEnabled(true);
                color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_background_color = color;
                        background_color_preview.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                        drawing_view.setDrawingBackground(color);
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                });
                color_picker.show();
            }
        });

        choose_background_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    chooseBackgroundImage();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        clearCanvas_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });

        pdf_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PdfViewer.class);
                startActivity(intent);
            }
        });

        ppt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PPTView.class);
                startActivity(intent);
            }
        });

        //Show toast
        if (i.hasExtra(DrawingActivityBuilder.TOAST_ENABLED)) {
            if (i.getBooleanExtra(DrawingActivityBuilder.TOAST_ENABLED, true)) {
                Toast toast = Toast.makeText(this, getString(R.string.drawing_instructions), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.drawing_instructions), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }


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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishWarning();
        } else if (id == R.id.action_done) {
            Bitmap b = drawing_view.exportDrawing();

            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            if (file.exists()) file.delete();
            try {
                file.createNewFile();

                FileOutputStream out = new FileOutputStream(file);
                b.compress(Bitmap.CompressFormat.PNG, IMAGE_COMPRESSION_QUALITY, out);
                out.flush();
                out.close();
            } catch (Exception e) {
            }

            Intent i = new Intent();
            i.putExtra(DRAWING_PATH, file.getAbsolutePath());

            setResult(RESULT_OK, i);
            Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_undo) {
            drawing_view.undo();
            menu.findItem(R.id.action_undo).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_undo).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_redo).setEnabled(!drawing_view.isRedoStackEmpty());
            menu.findItem(R.id.action_redo).getIcon().setAlpha(drawing_view.isRedoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_done).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_done).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
        } else if (id == R.id.action_redo) {
            drawing_view.redo();
            menu.findItem(R.id.action_undo).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_undo).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_redo).setEnabled(!drawing_view.isRedoStackEmpty());
            menu.findItem(R.id.action_redo).getIcon().setAlpha(drawing_view.isRedoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_done).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_done).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
        }
        return super.onOptionsItemSelected(item);
    }

    private void clear() {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(R.string.drawing)
                .setMessage(R.string.warning_clear)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        drawing_view.clear();
                        menu.findItem(R.id.action_undo).setEnabled(false);
                        menu.findItem(R.id.action_undo).getIcon().setAlpha(130);
                        menu.findItem(R.id.action_redo).setEnabled(false);
                        menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
                        menu.findItem(R.id.action_done).setEnabled(false);
                        menu.findItem(R.id.action_done).getIcon().setAlpha(130);
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                })
                .create();
        d.show();
    }


    private void chooseBackgroundImage() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .enableCameraSupport(true)
                .enableVideoPicker(false)
                .pickPhoto(MainActivity.this);
    }

    private void finishWarning() {
        if (!drawing_view.isUndoStackEmpty()) {
            if (!pressedOnce) {
                pressedOnce = true;
                Toast.makeText(MainActivity.this, R.string.press_again_to_discard_drawing, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pressedOnce = false;
                    }
                }, 2500);
            } else {
                pressedOnce = false;
                onBackPressed();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == RESULT_OK && data != null) {
            final ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            final Bitmap b = loadImageFromPath(paths.get(0));

            AlertDialog d = new AlertDialog.Builder(this)
                    .setTitle(R.string.drawing)
                    .setMessage(R.string.warning_background_image)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            drawing_view.setBackgroundImage(b);
                            background_image_preview.setImageBitmap(b);

                            menu.findItem(R.id.action_undo).setEnabled(false);
                            menu.findItem(R.id.action_undo).getIcon().setAlpha(130);
                            menu.findItem(R.id.action_redo).setEnabled(false);
                            menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
                            menu.findItem(R.id.action_done).setEnabled(false);
                            menu.findItem(R.id.action_done).getIcon().setAlpha(130);

                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    })
                    .create();
            d.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            chooseBackgroundImage();
    }

    private Bitmap loadImageFromPath(String path) {
        Bitmap b = null;
        try {
            InputStream in = new FileInputStream(path);
            b = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
        }
        return b;
    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

  /*  public static MainActivity getInstance() {
        return new MainActivity();
    }
*/

    @Override
    protected void onResume() {
        super.onResume();
        result.setSelection(-1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {
                finishWarning();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {


        int id = view.getId();
        switch (id) {
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                Log.d("Web", "Fab 1");

                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                startActivity(intent);

                break;

            case R.id.fab2:

                Log.d("pencil", "Fab 2");
                settings.setSelectedBrush(Brushes.PEN);
                settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                current_utility.setText(getString(R.string.pen));

                break;

            case R.id.fab3:

                Log.d("eraser", "Fab 3");
                settings.setSelectedBrush(Brushes.ERASER);
                settings.setSelectedBrushSize(size.getProgress() / 25.0f);
                current_utility.setText(getString(R.string.eraser));
                current_size.setText(getString(R.string.current_size_) + " 100%");


                break;

            case R.id.fab4:
                if (flag) {

                    drawing_view.isInZoomMode();
                    drawing_view.exitZoomMode();
                    fab4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_zoom_in));

                    flag = false;

                } else if (!flag) {
                    drawing_view.enterZoomMode();
                    fab4.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_zoom_out));
                    Toast toast = Toast.makeText(this, getString(R.string.enter_zoom_mode), Toast.LENGTH_LONG);
                    toast.show();

                    flag = true;
                }

                break;


        }


    }

    private void animateFAB() {
        if (isFabOpen) {

            fab.startAnimation(rotate_backwards);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);

            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
            Log.d("Menu", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);

            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
            Log.d("Menu", "open");

        }
    }

    private void ColorSelector() {
        image_color_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // int color = ResourcesCompat.getColor(getResources(), R.color.color_black,null);

                color_preview.setColorFilter(-1, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-1);
                scaleColorView(image_color_white);


            }
        });

        image_color_light_brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-3366510, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-3366510);
                scaleColorView(image_color_light_brown);


            }
        });


        image_color_brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-7508381, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-7508381);
                scaleColorView(image_color_brown);

            }
        });

        image_color_dark_brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-8695228, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-8695228);
                scaleColorView(image_color_dark_brown);

            }
        });

        image_color_light_pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-21290, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-21290);
                scaleColorView(image_color_light_pink);


            }
        });

        image_color_pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-383334, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-383334);
                scaleColorView(image_color_pink);


            }
        });

        image_color_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-1233362, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-1233362);
                scaleColorView(image_color_red);


            }
        });
        image_color_dark_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-5823966, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-5823966);
                scaleColorView(image_color_dark_red);


            }
        });

        image_color_light_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-151479, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-151479);
                scaleColorView(image_color_light_orange);


            }
        });

        image_color_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-759490, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-759490);
                scaleColorView(image_color_orange);


            }
        });

        image_color_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-4853, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-4853);
                scaleColorView(image_color_yellow);


            }
        });

        image_color_light_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-14427564, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-14427564);
                scaleColorView(image_color_light_green);


            }
        });

        image_color_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-14442665, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-14442665);
                scaleColorView(image_color_green);


            }
        });

        image_color_dark_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-16619989, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-16619989);
                scaleColorView(image_color_dark_green);


            }
        });

        image_color_light_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-16731649, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-16731649);
                scaleColorView(image_color_light_blue);


            }
        });

        image_color_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-16742718, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-16742718);
                scaleColorView(image_color_blue);


            }
        });

        image_color_dark_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-16754818, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-16754818);
                scaleColorView(image_color_dark_blue);


            }
        });

        image_color_purle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-9959524, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-9959524);
                scaleColorView(image_color_purle);


            }
        });
        image_color_dark_grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-9868951, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-9868951);
                scaleColorView(image_color_dark_grey);


            }
        });

        image_color_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                color_preview.setColorFilter(-16777216, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.setColor(-16777216);
                scaleColorView(image_color_black);


            }
        });

        overflow_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    menuLayout.setVisibility(RelativeLayout.VISIBLE);
                    // menuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    flag = false;
                } else if (!flag) {
                    menuLayout.setVisibility(RelativeLayout.INVISIBLE);
                    // menuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out ));
                    flag = true;

                }


            }
        });

        exit_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });

        resetZoom_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawing_view.resetZoom();
                drawing_view.exitZoomMode();
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void scaleColorView(View view) {
        //reset scale of all views


        image_color_white.setScaleX(1f);
        image_color_white.setScaleY(1f);

        image_color_light_brown.setScaleX(1f);
        image_color_light_brown.setScaleY(1f);

        image_color_brown.setScaleX(1f);
        image_color_brown.setScaleY(1f);

        image_color_dark_brown.setScaleX(1f);
        image_color_dark_brown.setScaleY(1f);

        image_color_light_pink.setScaleX(1f);
        image_color_light_pink.setScaleY(1f);

        image_color_pink.setScaleX(1f);
        image_color_pink.setScaleY(1f);

        image_color_red.setScaleX(1f);
        image_color_red.setScaleY(1f);

        image_color_dark_red.setScaleX(1f);
        image_color_dark_red.setScaleY(1f);

        image_color_light_orange.setScaleX(1f);
        image_color_light_orange.setScaleY(1f);

        image_color_orange.setScaleX(1f);
        image_color_orange.setScaleY(1f);

        image_color_yellow.setScaleX(1f);
        image_color_yellow.setScaleY(1f);

        image_color_light_green.setScaleX(1f);
        image_color_light_green.setScaleY(1f);

        image_color_green.setScaleX(1f);
        image_color_green.setScaleY(1f);

        image_color_dark_green.setScaleX(1f);
        image_color_dark_green.setScaleY(1f);

        image_color_light_blue.setScaleX(1f);
        image_color_light_blue.setScaleY(1f);

        image_color_blue.setScaleX(1f);
        image_color_blue.setScaleY(1f);

        image_color_dark_blue.setScaleX(1f);
        image_color_dark_blue.setScaleY(1f);

        image_color_purle.setScaleX(1f);
        image_color_purle.setScaleY(1f);

        image_color_dark_grey.setScaleX(1f);
        image_color_dark_grey.setScaleY(1f);

        image_color_black.setScaleX(1f);
        image_color_black.setScaleY(1f);

        //set scale of selected view
        view.setScaleX(1.5f);
        view.setScaleY(1.5f);
    }


}
