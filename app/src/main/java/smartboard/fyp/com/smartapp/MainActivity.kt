package smartboard.fyp.com.smartapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.model.interfaces.Nameable
import com.raed.drawingview.BrushView
import com.raed.drawingview.DrawingView
import com.raed.drawingview.brushes.BrushSettings
import com.raed.drawingview.brushes.Brushes
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import net.margaritov.preference.colorpicker.ColorPickerDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var toolbar: Toolbar

    /*FAB declaration*/
    private var isFabOpen = false
    lateinit var settings: BrushSettings
    private var fab_open: Animation? = null
    private var fab_close: Animation? = null
    private var rotate_forward: Animation? = null
    private var rotate_backwards: Animation? = null

    //Constants
    private val IMAGE_COMPRESSION_QUALITY = 85
    private val REQ_WRITE_EXTERNAL_STORAGE = 1
    var flag = false
    private var menu: Menu? = null
    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var drawing_view: DrawingView
    private lateinit var color_preview: ImageView
    private lateinit var current_utility: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var fab1: FloatingActionButton
    private lateinit var fab2: FloatingActionButton
    private lateinit var fab3: FloatingActionButton
    private lateinit var fab4: FloatingActionButton
    private lateinit var arrow: ImageView
    private lateinit var brush_view: BrushView

    //Variables as objects
    private lateinit var calligraphy: ImageView
    private lateinit var pen: ImageView
    private lateinit var airbrush: ImageView
    private lateinit var eraser: ImageView
    private lateinit var pencil: ImageView
    private lateinit var current_size: TextView
    private lateinit var size: SeekBar
    private lateinit var overflow_menu: ImageView
    private lateinit var save_menu: ImageView
    private lateinit var ppt_menu: ImageView
    private lateinit var pdf_menu: ImageView
    private lateinit var resetZoom_menu: ImageView
    private lateinit var clearCanvas_menu: ImageView
    private lateinit var exit_menu: ImageView
    private lateinit var choose_color: ImageView
    private lateinit var background_color: RadioButton
    private lateinit var background_image: RadioButton
    private lateinit var background_color_preview: ImageView
    private lateinit var choose_background_color: Button
    private lateinit var background_image_preview: ImageView
    private lateinit var choose_background_image: Button
    private lateinit var color_picker: ColorPickerDialog
    private var current_color = Color.BLACK
    private var current_background_color = Color.parseColor("#eeeeee")

    //Variables
    private var pressedOnce = false
    private var image_color_white: ImageView? = null
    private var image_color_black: ImageView? = null
    private var image_color_red: ImageView? = null
    private var image_color_yellow: ImageView? = null
    private var image_color_green: ImageView? = null
    private var image_color_blue: ImageView? = null
    private var image_color_pink: ImageView? = null
    private var image_color_brown: ImageView? = null
    private var image_color_light_brown: ImageView? = null
    private var image_color_dark_brown: ImageView? = null
    private var image_color_light_pink: ImageView? = null
    private var image_color_dark_red: ImageView? = null
    private var image_color_orange: ImageView? = null
    private var image_color_light_orange: ImageView? = null
    private var image_color_light_green: ImageView? = null
    private var image_color_dark_green: ImageView? = null
    private var image_color_light_blue: ImageView? = null
    private var image_color_dark_blue: ImageView? = null
    private var image_color_purle: ImageView? = null
    private var image_color_dark_grey: ImageView? = null
    private lateinit var menuLayout: RelativeLayout
    private var result: Drawer? = null
    private var auth: FirebaseAuth? = null

    /*Layout Handling*/ /*navigation drawer*/
    private lateinit var header: AccountHeader
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_drawing, menu)
        menu.findItem(R.id.action_undo).icon.alpha = 130
        menu.findItem(R.id.action_redo).icon.alpha = 130
        menu.findItem(R.id.action_done).icon.alpha = 130
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        val i = intent
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Interactive Smart Board App"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= 21) window.statusBarColor =
            darkenColor(resources.getColor(R.color.header_color))


        /*FireBase auth*/auth = FirebaseAuth.getInstance()

        /*tabLayout = findViewById(R.id.tablayout);
        tabProfile = findViewById(R.id.tab_Profile);
        viewPager = (findViewById(R.id.viewpager));*/fab = findViewById(R.id.fab)
        fab1 = findViewById(R.id.fab1)
        fab1.size = FloatingActionButton.SIZE_NORMAL
        fab2 = findViewById(R.id.fab2)
        fab2.size = FloatingActionButton.SIZE_NORMAL
        fab3 = findViewById(R.id.fab3)
        fab3.size = FloatingActionButton.SIZE_NORMAL
        fab4 = findViewById(R.id.fab4)
        fab4.size = FloatingActionButton.SIZE_NORMAL
        fab_open = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        rotate_backwards = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_backwards)
        rotate_forward = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_forward)
        fab.setOnClickListener(this)
        fab1.setOnClickListener(this)
        fab2.setOnClickListener(this)
        fab3.setOnClickListener(this)
        fab4.setOnClickListener(this)
        menuLayout = findViewById(R.id.menu_layout)
        overflow_menu = findViewById(R.id.overflow_menu)
        save_menu = findViewById(R.id.save_menu)
        ppt_menu = findViewById(R.id.ppt_menu)
        pdf_menu = findViewById(R.id.pdf_menu)
        clearCanvas_menu = findViewById(R.id.clearCanvas_menu)
        resetZoom_menu = findViewById(R.id.resetZoom_menu)
        exit_menu = findViewById(R.id.exitApp_menu)


        /*overflow_menu.setOnClickListener(this);
        save_menu.setOnClickListener(this);
        ppt_menu.setOnClickListener(this);
        pdf_menu.setOnClickListener(this);
        resetZoom_menu.setOnClickListener(this);
        clearCanvas_menu.setOnClickListener(this);
        exit_menu.setOnClickListener(this);
*/image_color_white = findViewById(R.id.image_color_white)
        image_color_light_brown = findViewById(R.id.image_color_light_brown)
        image_color_brown = findViewById(R.id.image_color_brown)
        image_color_dark_brown = findViewById(R.id.image_color_dark_brown)
        image_color_light_pink = findViewById(R.id.image_color_light_pink)
        image_color_pink = findViewById(R.id.image_color_pink)
        image_color_red = findViewById(R.id.image_color_red)
        image_color_dark_red = findViewById(R.id.image_color_dark_red)
        image_color_orange = findViewById(R.id.image_color_orange)
        image_color_light_orange = findViewById(R.id.image_color_light_orange)
        image_color_yellow = findViewById(R.id.image_color_yellow)
        image_color_light_green = findViewById(R.id.image_color_light_green)
        image_color_green = findViewById(R.id.image_color_green)
        image_color_dark_green = findViewById(R.id.image_color_dark_green)
        image_color_light_blue = findViewById(R.id.image_color_light_blue)
        image_color_blue = findViewById(R.id.image_color_blue)
        image_color_dark_blue = findViewById(R.id.image_color_dark_blue)
        image_color_purle = findViewById(R.id.image_color_purle)
        image_color_dark_grey = findViewById(R.id.image_color_dark_grey)
        image_color_black = findViewById(R.id.image_color_black)
        ColorSelector()
        slidingUpPanelLayout = findViewById(R.id.slidingLayout)
        slidingUpPanelLayout.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                arrow.rotation = slideOffset * 180.0f
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
            }
        })


        //Initialize DrawingView
        drawing_view = findViewById(R.id.drawing_view)
        drawing_view.setUndoAndRedoEnable(true)
        drawing_view.brushSettings.selectedBrushSize = 0.25f
        drawing_view.setOnDrawListener(DrawingView.OnDrawListener {
            menu!!.findItem(R.id.action_undo).isEnabled = true
            menu!!.findItem(R.id.action_undo).icon.alpha = 255
            menu!!.findItem(R.id.action_redo).isEnabled = false
            menu!!.findItem(R.id.action_redo).icon.alpha = 130
            menu!!.findItem(R.id.action_done).isEnabled = true
            menu!!.findItem(R.id.action_done).icon.alpha = 255
            menuLayout.visibility = View.INVISIBLE
        })
        drawing_view.clear()

        //Initialize Preview
        color_preview = findViewById(R.id.color_preview)
        current_utility = findViewById(R.id.current_utility)
        current_utility.text = getString(R.string.current_utility_) + " " + getString(R.string.pen)
        current_size = findViewById(R.id.current_size)
        current_size.text = getString(R.string.current_size_) + " 25%"
        arrow = findViewById(R.id.arrow)
        arrow.setOnClickListener(View.OnClickListener {
            slidingUpPanelLayout.panelState = if (slidingUpPanelLayout.panelState ==
                PanelState.EXPANDED
            ) PanelState.COLLAPSED else PanelState.EXPANDED
        })
        brush_view = findViewById(R.id.brush_view)
        brush_view.setDrawingView(drawing_view)
        settings = drawing_view.brushSettings

        //color_preview_slide = findViewById(R.id.color_preview_slide);
        choose_color = findViewById(R.id.choose_color)
        choose_color.setOnClickListener(View.OnClickListener {
            color_picker = ColorPickerDialog(this@MainActivity, current_color)
            color_picker.alphaSliderVisible = false
            color_picker.hexValueEnabled = true
            color_picker.setOnColorChangedListener { color ->
                current_color = color
                color_preview.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                //color_preview_slide.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                settings.color = color
            }
            color_picker.show()
        })
        size = findViewById(R.id.size)
        size.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                settings.selectedBrushSize = i / 100.0f
                current_size.text = String.format(
                    "%s %s%%",
                    resources.getString(R.string.current_size_),
                    i
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        pencil = findViewById(R.id.utility_pencil)
        pencil.setOnClickListener(View.OnClickListener {
            settings.selectedBrush = Brushes.PENCIL
            settings.selectedBrushSize = size.progress / 100.0f
            current_utility.text = getString(R.string.pencil)
        })
        eraser = findViewById(R.id.utility_eraser)
        eraser.setOnClickListener(View.OnClickListener {
            settings.selectedBrush = Brushes.ERASER
            settings.selectedBrushSize = size.progress / 100.0f
            current_utility.text = getString(R.string.eraser)
        })
        pen = findViewById(R.id.utility_pen)
        pen.setOnClickListener(View.OnClickListener {
            settings.selectedBrush = Brushes.PEN
            settings.selectedBrushSize = size.progress / 100.0f
            current_utility.text = getString(R.string.pen)
        })
        calligraphy = findViewById(R.id.utility_calligraphy)
        calligraphy.setOnClickListener(View.OnClickListener {
            settings.selectedBrush = Brushes.CALLIGRAPHY
            settings.selectedBrushSize = size.progress / 100.0f
            current_utility.text = getString(R.string.calligraphy)
        })
        airbrush = findViewById(R.id.utility_airbrush)
        airbrush.setOnClickListener(View.OnClickListener {
            settings.selectedBrush = Brushes.AIR_BRUSH
            settings.selectedBrushSize = size.progress / 100.0f
            current_utility.text = getString(R.string.air_brush)
        })
        if (intent.getIntExtra(
                DrawingActivityBuilder.Companion.DEFAULT_UTILITY,
                UTILITIY_PEN
            ) == UTILITIY_ERASER
        );
        if (intent.getIntExtra(
                DrawingActivityBuilder.Companion.DEFAULT_UTILITY,
                UTILITIY_PEN
            ) == UTILITIY_AIR_BRUSH
        );
        if (intent.getIntExtra(
                DrawingActivityBuilder.Companion.DEFAULT_UTILITY,
                UTILITIY_PEN
            ) == UTILITIY_CALLIGRAPHY
        );
        if (intent.getIntExtra(
                DrawingActivityBuilder.Companion.DEFAULT_UTILITY,
                UTILITIY_PEN
            ) == UTILITIY_PENCIL
        );

        //Background
        background_color = findViewById(R.id.background_color)
        background_color_preview = findViewById(R.id.background_color_preview)
        choose_background_color = findViewById(R.id.choose_background_color)
        background_image = findViewById(R.id.background_image)
        background_image_preview = findViewById(R.id.background_image_preview)
        choose_background_image = findViewById(R.id.choose_background_image)
        background_color.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            background_color_preview.isEnabled = b
            choose_background_color.isEnabled = b
        })
        background_image.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            background_image_preview.isEnabled = b
            choose_background_image.isEnabled = b
        })
        choose_background_color.setOnClickListener(View.OnClickListener { view: View? ->
            color_picker = ColorPickerDialog(this@MainActivity, current_background_color)
            color_picker.alphaSliderVisible = false
            color_picker.hexValueEnabled = true
            color_picker.setOnColorChangedListener(ColorPickerDialog.OnColorChangedListener { color ->
                current_background_color = color
                background_color_preview.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawing_view.drawingBackground = color
                slidingUpPanelLayout.panelState = PanelState.COLLAPSED
            })
            color_picker.show()
        })
        choose_background_image.setOnClickListener(View.OnClickListener { view: View? ->
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                chooseBackgroundImage()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQ_WRITE_EXTERNAL_STORAGE
                )
            }
        })
        clearCanvas_menu.setOnClickListener(View.OnClickListener { view: View? ->
            clear()
            menuLayout.visibility = View.INVISIBLE
        })
        pdf_menu.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(applicationContext, PdfViewer::class.java)
            startActivity(intent)
        })
        ppt_menu.setOnClickListener(View.OnClickListener { view: View? ->
            Toast.makeText(
                this@MainActivity,
                "PPT",
                Toast.LENGTH_SHORT
            ).show()
        })
        save_menu.setOnClickListener(View.OnClickListener { view: View? ->
            val b = drawing_view.exportDrawing()
            val file = File(
                (Environment.getExternalStorageDirectory()
                    .toString() + File.separator + ""
                        + System.currentTimeMillis() + ".jpg")
            )
            if (file.exists()) file.delete()
            try {
                file.createNewFile()
                val out = FileOutputStream(file)
                b.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
            }
            val i1 = Intent()
            i1.putExtra(DRAWING_PATH, file.absolutePath)
            setResult(RESULT_OK, i1)
            Toast.makeText(applicationContext, "Saved Successfully", Toast.LENGTH_SHORT).show()
            menuLayout.visibility = View.INVISIBLE
        })

        //Show toast
        if (i.hasExtra(DrawingActivityBuilder.Companion.TOAST_ENABLED)) {
            if (i.getBooleanExtra(DrawingActivityBuilder.Companion.TOAST_ENABLED, true)) {
                val toast = Toast.makeText(
                    this,
                    getString(R.string.drawing_instructions),
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        } else {
            val toast =
                Toast.makeText(this, getString(R.string.drawing_instructions), Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        }
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val profile: IProfile<*> =
            ProfileDrawerItem().withName("Zain Ali Khalid").withEmail("zain.khalid@gmail.com")
                .withIcon(R.drawable.user).withIdentifier(100)
        header = AccountHeaderBuilder()
            .withActivity(this)
            .withTranslucentStatusBar(true)
            .withHeaderBackground(R.color.colorPrimary)
            .addProfiles(profile)
            .withOnAccountHeaderListener { view: View?, profile1: IProfile<*>, current: Boolean ->
                if (profile1 is IDrawerItem<*, *> && profile1.getIdentifier() == 100000L) {
                    val count: Int = 100 + header.profiles.size + 1
                }
                false
            }
            .withSavedInstance(savedInstanceState)
            .build()
        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withAccountHeader(header)
            .withTranslucentStatusBar(false)
            .withActionBarDrawerToggle(false)
            .withSavedInstance(savedInstanceState)
            .addDrawerItems(
                PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_book)
                    .withName("Update Semesters").withIdentifier(201),
                PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_cloud_upload)
                    .withName("Upload Files").withIdentifier(202),
                PrimaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_exit_to_app)
                    .withName("Sign Out").withIdentifier(203)
            )
            .withOnDrawerItemClickListener { view: View?, position: Int, drawerItem: IDrawerItem<*, *> ->
                if (drawerItem is Nameable<*>) {
                    if (drawerItem.identifier == 202L) {
                        val intent: Intent =
                            Intent(applicationContext, MaterialActivity::class.java)
                        startActivity(intent)
                    }
                    if (drawerItem.identifier == 203L) {
                        /*  auth!!.signOut()
                          if (auth!!.getCurrentUser() == null) {
                              startActivity(Intent(this, LoginActivity::class.java))
                              finish()
                          }*/
                    }
                }
                false
            }
            .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finishWarning()
        } else if (id == R.id.action_done) {
            val b = drawing_view.exportDrawing()
            val file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + ""
                        + System.currentTimeMillis() + ".png"
            )
            if (file.exists()) file.delete()
            try {
                file.createNewFile()
                val out = FileOutputStream(file)
                b.compress(Bitmap.CompressFormat.PNG, IMAGE_COMPRESSION_QUALITY, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
            }
            val i = Intent()
            i.putExtra(DRAWING_PATH, file.absolutePath)
            setResult(RESULT_OK, i)
            Toast.makeText(applicationContext, "Saved Successfully", Toast.LENGTH_SHORT).show()
        } else if (id == R.id.action_undo) {
            drawing_view.undo()
            menu!!.findItem(R.id.action_undo).isEnabled = !drawing_view.isUndoStackEmpty
            menu!!.findItem(R.id.action_undo).icon.alpha =
                if (drawing_view.isUndoStackEmpty) 130 else 255
            menu!!.findItem(R.id.action_redo).isEnabled = !drawing_view.isRedoStackEmpty
            menu!!.findItem(R.id.action_redo).icon.alpha =
                if (drawing_view.isRedoStackEmpty) 130 else 255
            menu!!.findItem(R.id.action_done).isEnabled = !drawing_view.isUndoStackEmpty
            menu!!.findItem(R.id.action_done).icon.alpha =
                if (drawing_view.isUndoStackEmpty) 130 else 255
        } else if (id == R.id.action_redo) {
            drawing_view.redo()
            menu!!.findItem(R.id.action_undo).isEnabled = !drawing_view.isUndoStackEmpty
            menu!!.findItem(R.id.action_undo).icon.alpha =
                if (drawing_view.isUndoStackEmpty) 130 else 255
            menu!!.findItem(R.id.action_redo).isEnabled = !drawing_view.isRedoStackEmpty
            menu!!.findItem(R.id.action_redo).icon.alpha =
                if (drawing_view.isRedoStackEmpty) 130 else 255
            menu!!.findItem(R.id.action_done).isEnabled = !drawing_view.isUndoStackEmpty
            menu!!.findItem(R.id.action_done).icon.alpha =
                if (drawing_view.isUndoStackEmpty) 130 else 255
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clear() {
        val d = AlertDialog.Builder(this)
            .setTitle(R.string.drawing)
            .setMessage(R.string.warning_clear)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.yes) { dialogInterface, i ->
                drawing_view.clear()
                menu!!.findItem(R.id.action_undo).isEnabled = false
                menu!!.findItem(R.id.action_undo).icon.alpha = 130
                menu!!.findItem(R.id.action_redo).isEnabled = false
                menu!!.findItem(R.id.action_redo).icon.alpha = 130
                menu!!.findItem(R.id.action_done).isEnabled = false
                menu!!.findItem(R.id.action_done).icon.alpha = 130
                slidingUpPanelLayout.panelState = PanelState.COLLAPSED
            }
            .create()
        d.show()
    }

    private fun chooseBackgroundImage() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .enableCameraSupport(true)
            .enableVideoPicker(false)
            .pickPhoto(this@MainActivity)
    }

    private fun finishWarning() {
        if (!drawing_view.isUndoStackEmpty) {
            if (!pressedOnce) {
                pressedOnce = true
                Toast.makeText(
                    this@MainActivity,
                    R.string.press_again_to_discard_drawing,
                    Toast.LENGTH_SHORT
                ).show()
                Handler().postDelayed({ pressedOnce = false }, 2500)
            } else {
                pressedOnce = false
                onBackPressed()
            }
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == RESULT_OK && data != null) {
            val paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
            val b = loadImageFromPath(paths!![0])
            val d = AlertDialog.Builder(this)
                .setTitle(R.string.drawing)
                .setMessage(R.string.warning_background_image)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes) { dialogInterface, i ->
                    drawing_view.setBackgroundImage(b)
                    background_image_preview.setImageBitmap(b)
                    menu!!.findItem(R.id.action_undo).isEnabled = false
                    menu!!.findItem(R.id.action_undo).icon.alpha = 130
                    menu!!.findItem(R.id.action_redo).isEnabled = false
                    menu!!.findItem(R.id.action_redo).icon.alpha = 130
                    menu!!.findItem(R.id.action_done).isEnabled = false
                    menu!!.findItem(R.id.action_done).icon.alpha = 130
                    slidingUpPanelLayout.panelState = PanelState.COLLAPSED
                }
                .create()
            d.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) chooseBackgroundImage()
    }

    private fun loadImageFromPath(path: String): Bitmap? {
        var b: Bitmap? = null
        try {
            val `in`: InputStream = FileInputStream(path)
            b = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
        }
        return b
    }

    private fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }

    /*  public static MainActivity getInstance() {
        return new MainActivity();
    }
*/
    override fun onResume() {
        super.onResume()
        result!!.setSelection(-1)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingUpPanelLayout.panelState == PanelState.EXPANDED || slidingUpPanelLayout.panelState == PanelState.DRAGGING) {
                slidingUpPanelLayout.panelState = PanelState.COLLAPSED
            } else {
                finishWarning()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.fab -> animateFAB()
            R.id.fab1 -> {
                Log.d("Web", "Fab 1")
                val intent = Intent(this@MainActivity, WebViewActivity::class.java)
                startActivity(intent)
            }
            R.id.fab2 -> {
                Log.d("pencil", "Fab 2")
                settings.selectedBrush = Brushes.PEN
                settings.selectedBrushSize = size.progress / 100.0f
                current_utility.text = getString(R.string.pen)
                current_size.text = getString(R.string.current_size_) + " 25%"
            }
            R.id.fab3 -> {
                Log.d("eraser", "Fab 3")
                settings.selectedBrush = Brushes.ERASER
                settings.selectedBrushSize = size.progress / 25.0f
                current_utility.text = getString(R.string.eraser)
                current_size.text = getString(R.string.current_size_) + " 100%"
            }
            R.id.fab4 -> if (flag) {
                Log.d("zoom", "Fab 4")
                drawing_view.isInZoomMode
                drawing_view.exitZoomMode()
                fab4.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_zoom_in
                    )
                )
                flag = false
            } else if (!flag) {
                drawing_view.enterZoomMode()
                fab4.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_zoom_out
                    )
                )
                val toast =
                    Toast.makeText(this, getString(R.string.enter_zoom_mode), Toast.LENGTH_LONG)
                toast.show()
                flag = true
            }
        }
    }

    private fun animateFAB() {
        if (isFabOpen) {
            fab.startAnimation(rotate_backwards)
            fab1.startAnimation(fab_close)
            fab2.startAnimation(fab_close)
            fab3.startAnimation(fab_close)
            fab4.startAnimation(fab_close)
            fab1.isClickable = false
            fab2.isClickable = false
            fab3.isClickable = false
            fab4.isClickable = false
            isFabOpen = false
            Log.d("Menu", "close")
        } else {
            fab.startAnimation(rotate_forward)
            fab1.startAnimation(fab_open)
            fab2.startAnimation(fab_open)
            fab3.startAnimation(fab_open)
            fab4.startAnimation(fab_open)
            fab1.isClickable = true
            fab2.isClickable = true
            fab3.isClickable = true
            fab4.isClickable = true
            isFabOpen = true
            Log.d("Menu", "open")
        }
    }

    private fun ColorSelector() {
        image_color_white!!.setOnClickListener { view: View? ->
            // int color = ResourcesCompat.getColor(getResources(), R.color.color_black,null);
            color_preview.setColorFilter(-1, PorterDuff.Mode.SRC_IN)
            settings.color = -1
            scaleColorView(image_color_white)
        }
        image_color_light_brown!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-3366510, PorterDuff.Mode.SRC_IN)
            settings.color = -3366510
            scaleColorView(image_color_light_brown)
        }
        image_color_brown!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-7508381, PorterDuff.Mode.SRC_IN)
            settings.color = -7508381
            scaleColorView(image_color_brown)
        }
        image_color_dark_brown!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-8695228, PorterDuff.Mode.SRC_IN)
            settings.color = -8695228
            scaleColorView(image_color_dark_brown)
        }
        image_color_light_pink!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-21290, PorterDuff.Mode.SRC_IN)
            settings.color = -21290
            scaleColorView(image_color_light_pink)
        }
        image_color_pink!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-383334, PorterDuff.Mode.SRC_IN)
            settings.color = -383334
            scaleColorView(image_color_pink)
        }
        image_color_red!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-1233362, PorterDuff.Mode.SRC_IN)
            settings.color = -1233362
            scaleColorView(image_color_red)
        }
        image_color_dark_red!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-5823966, PorterDuff.Mode.SRC_IN)
            settings.color = -5823966
            scaleColorView(image_color_dark_red)
        }
        image_color_light_orange!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-151479, PorterDuff.Mode.SRC_IN)
            settings.color = -151479
            scaleColorView(image_color_light_orange)
        }
        image_color_orange!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-759490, PorterDuff.Mode.SRC_IN)
            settings.color = -759490
            scaleColorView(image_color_orange)
        }
        image_color_yellow!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-4853, PorterDuff.Mode.SRC_IN)
            settings.color = -4853
            scaleColorView(image_color_yellow)
        }
        image_color_light_green!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-14427564, PorterDuff.Mode.SRC_IN)
            settings.color = -14427564
            scaleColorView(image_color_light_green)
        }
        image_color_green!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-14442665, PorterDuff.Mode.SRC_IN)
            settings.color = -14442665
            scaleColorView(image_color_green)
        }
        image_color_dark_green!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-16619989, PorterDuff.Mode.SRC_IN)
            settings.color = -16619989
            scaleColorView(image_color_dark_green)
        }
        image_color_light_blue!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-16731649, PorterDuff.Mode.SRC_IN)
            settings.color = -16731649
            scaleColorView(image_color_light_blue)
        }
        image_color_blue!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-16742718, PorterDuff.Mode.SRC_IN)
            settings.color = -16742718
            scaleColorView(image_color_blue)
        }
        image_color_dark_blue!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-16754818, PorterDuff.Mode.SRC_IN)
            settings.color = -16754818
            scaleColorView(image_color_dark_blue)
        }
        image_color_purle!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-9959524, PorterDuff.Mode.SRC_IN)
            settings.color = -9959524
            scaleColorView(image_color_purle)
        }
        image_color_dark_grey!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-9868951, PorterDuff.Mode.SRC_IN)
            settings.color = -9868951
            scaleColorView(image_color_dark_grey)
        }
        image_color_black!!.setOnClickListener { view: View? ->
            color_preview.setColorFilter(-16777216, PorterDuff.Mode.SRC_IN)
            settings.color = -16777216
            scaleColorView(image_color_black)
        }
        overflow_menu.setOnClickListener { view: View? ->
            if (flag) {
                menuLayout.visibility = RelativeLayout.VISIBLE
                // menuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                flag = false
            } else if (!flag) {
                menuLayout.visibility = RelativeLayout.INVISIBLE
                // menuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out ));
                flag = true
            }
        }
        exit_menu.setOnClickListener { view: View? ->
            finish()
            System.exit(0)
        }
        resetZoom_menu.setOnClickListener { view: View? ->
            drawing_view.resetZoom()
            drawing_view.exitZoomMode()
            menuLayout.visibility = View.INVISIBLE
        }
    }

    private fun scaleColorView(view: View?) {
        //reset scale of all views
        image_color_white!!.scaleX = 1f
        image_color_white!!.scaleY = 1f
        image_color_light_brown!!.scaleX = 1f
        image_color_light_brown!!.scaleY = 1f
        image_color_brown!!.scaleX = 1f
        image_color_brown!!.scaleY = 1f
        image_color_dark_brown!!.scaleX = 1f
        image_color_dark_brown!!.scaleY = 1f
        image_color_light_pink!!.scaleX = 1f
        image_color_light_pink!!.scaleY = 1f
        image_color_pink!!.scaleX = 1f
        image_color_pink!!.scaleY = 1f
        image_color_red!!.scaleX = 1f
        image_color_red!!.scaleY = 1f
        image_color_dark_red!!.scaleX = 1f
        image_color_dark_red!!.scaleY = 1f
        image_color_light_orange!!.scaleX = 1f
        image_color_light_orange!!.scaleY = 1f
        image_color_orange!!.scaleX = 1f
        image_color_orange!!.scaleY = 1f
        image_color_yellow!!.scaleX = 1f
        image_color_yellow!!.scaleY = 1f
        image_color_light_green!!.scaleX = 1f
        image_color_light_green!!.scaleY = 1f
        image_color_green!!.scaleX = 1f
        image_color_green!!.scaleY = 1f
        image_color_dark_green!!.scaleX = 1f
        image_color_dark_green!!.scaleY = 1f
        image_color_light_blue!!.scaleX = 1f
        image_color_light_blue!!.scaleY = 1f
        image_color_blue!!.scaleX = 1f
        image_color_blue!!.scaleY = 1f
        image_color_dark_blue!!.scaleX = 1f
        image_color_dark_blue!!.scaleY = 1f
        image_color_purle!!.scaleX = 1f
        image_color_purle!!.scaleY = 1f
        image_color_dark_grey!!.scaleX = 1f
        image_color_dark_grey!!.scaleY = 1f
        image_color_black!!.scaleX = 1f
        image_color_black!!.scaleY = 1f

        //set scale of selected view
        view!!.scaleX = 1.5f
        view.scaleY = 1.5f
    }

    companion object {
        private var activity: MainActivity? = null
        const val DRAWING_PATH = "DrawingPath"
        const val UTILITIY_PENCIL = 1
        const val UTILITIY_ERASER = 2
        const val UTILITIY_AIR_BRUSH = 3
        const val UTILITIY_CALLIGRAPHY = 4
        const val UTILITIY_PEN = 5
        val instance: MainActivity?
            get() {
                if (activity == null) {
                    activity = MainActivity()
                }
                return activity
            }
    }
}