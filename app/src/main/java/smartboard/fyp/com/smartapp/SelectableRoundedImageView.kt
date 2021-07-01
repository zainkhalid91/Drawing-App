package smartboard.fyp.com.smartapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView

class SelectableRoundedImageView : AppCompatImageView {
    private var mResource: Int

    companion object {
        val TAG: String = "SelectableRoundedImageView"
        private val sScaleTypeArray: Array<ScaleType>
        private val DEFAULT_BORDER_COLOR: Int = -16777216

        init {
            sScaleTypeArray = arrayOf(
                ScaleType.MATRIX,
                ScaleType.FIT_XY,
                ScaleType.FIT_START,
                ScaleType.FIT_CENTER,
                ScaleType.FIT_END,
                ScaleType.CENTER,
                ScaleType.CENTER_CROP,
                ScaleType.CENTER_INSIDE
            )
        }
    }

    var cornerRadius: Float
        private set
    private var mRightTopCornerRadius: Float
    private var mLeftBottomCornerRadius: Float
    private var mRightBottomCornerRadius: Float
    var borderWidth: Float
        private set
    var borderColors: ColorStateList?
        private set
    private var isOval: Boolean
    private var mDrawable: Drawable? = null
    private var mRadii: FloatArray
    private var mScaleType: ScaleType

    constructor(context: Context?) : super((context)!!) {
        mResource = 0
        mScaleType = ScaleType.FIT_CENTER
        cornerRadius = 0.0f
        mRightTopCornerRadius = 0.0f
        mLeftBottomCornerRadius = 0.0f
        mRightBottomCornerRadius = 0.0f
        borderWidth = 0.0f
        borderColors = ColorStateList.valueOf(-16777216)
        isOval = false
        mRadii = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        this.invalidate()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int = 0) : super(
        context,
        attrs,
        defStyle
    ) {
        mResource = 0
        mScaleType = ScaleType.FIT_CENTER
        cornerRadius = 0.0f
        mRightTopCornerRadius = 0.0f
        mLeftBottomCornerRadius = 0.0f
        mRightBottomCornerRadius = 0.0f
        borderWidth = 0.0f
        borderColors = ColorStateList.valueOf(-16777216)
        isOval = false
        mRadii = floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        val a: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SelectableRoundedImageView,
            defStyle,
            0
        )
        val index: Int = a.getInt(R.styleable.SelectableRoundedImageView_android_scaleType, -1)
        if (index >= 0) {
            scaleType = sScaleTypeArray.get(index)
        }
        cornerRadius = a.getDimensionPixelSize(
            R.styleable.SelectableRoundedImageView_sriv_left_top_corner_radius,
            0
        )
            .toFloat()
        mRightTopCornerRadius = a.getDimensionPixelSize(
            R.styleable.SelectableRoundedImageView_sriv_right_top_corner_radius,
            0
        )
            .toFloat()
        mLeftBottomCornerRadius = a.getDimensionPixelSize(
            R.styleable.SelectableRoundedImageView_sriv_left_bottom_corner_radius,
            0
        ).toFloat()
        mRightBottomCornerRadius = a.getDimensionPixelSize(
            R.styleable.SelectableRoundedImageView_sriv_right_bottom_corner_radius,
            0
        ).toFloat()
        if ((cornerRadius >= 0.0f) && (mRightTopCornerRadius >= 0.0f) && (mLeftBottomCornerRadius >= 0.0f) && (mRightBottomCornerRadius >= 0.0f)) {
            mRadii = floatArrayOf(
                cornerRadius,
                cornerRadius,
                mRightTopCornerRadius,
                mRightTopCornerRadius,
                mRightBottomCornerRadius,
                mRightBottomCornerRadius,
                mLeftBottomCornerRadius,
                mLeftBottomCornerRadius
            )
            borderWidth =
                a.getDimensionPixelSize(R.styleable.SelectableRoundedImageView_sriv_border_width, 0)
                    .toFloat()
            if (borderWidth < 0.0f) {
                throw IllegalArgumentException("border width cannot be negative.")
            } else {
                borderColors =
                    a.getColorStateList(R.styleable.SelectableRoundedImageView_sriv_border_color)
                if (borderColors == null) {
                    borderColors = ColorStateList.valueOf(-16777216)
                }
                isOval = a.getBoolean(R.styleable.SelectableRoundedImageView_sriv_oval, false)
                a.recycle()
                updateDrawable()
            }
        } else {
            throw IllegalArgumentException("radius values cannot be negative.")
        }
    }

    override fun getScaleType(): ScaleType {
        return mScaleType
    }

    override fun setImageDrawable(drawable: Drawable?) {
        mResource = 0
        mDrawable = SelectableRoundedCornerDrawable.fromDrawable(drawable, resources)
        super.setImageDrawable(mDrawable)
        updateDrawable()
    }

    override fun setImageBitmap(bm: Bitmap) {
        mResource = 0
        mDrawable = SelectableRoundedCornerDrawable.fromBitmap(bm, resources)
        super.setImageDrawable(mDrawable)
        updateDrawable()
    }

    override fun setImageResource(resId: Int) {
        if (mResource != resId) {
            mResource = resId
            mDrawable = resolveResource()
            super.setImageDrawable(mDrawable)
            updateDrawable()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setImageDrawable(drawable)
    }

    private fun resolveResource(): Drawable? {
        val rsrc: Resources? = resources
        if (rsrc == null) {
            return null
        } else {
            var d: Drawable? = null
            if (mResource != 0) {
                try {
                    d = rsrc.getDrawable(mResource)
                } catch (var4: NotFoundException) {
                    Log.w("SRIV", "Unable to find resource: " + mResource, var4)
                    mResource = 0
                }
            }
            return SelectableRoundedCornerDrawable.fromDrawable(d, resources)
        }
    }

    private fun updateDrawable() {
        if (mDrawable != null) {
            (mDrawable as SelectableRoundedCornerDrawable?)!!.scaleType = mScaleType
            (mDrawable as SelectableRoundedCornerDrawable?)!!.setCornerRadii(mRadii)
            (mDrawable as SelectableRoundedCornerDrawable?)!!.borderWidth = borderWidth
            (mDrawable as SelectableRoundedCornerDrawable?)!!.setBorderColor(borderColors)
            (mDrawable as SelectableRoundedCornerDrawable?)!!.setOval(isOval)
        }
    }

    fun setCornerRadiiDP(leftTop: Float, rightTop: Float, leftBottom: Float, rightBottom: Float) {
        val density: Float = resources.displayMetrics.density
        val lt: Float = leftTop * density
        val rt: Float = rightTop * density
        val lb: Float = leftBottom * density
        val rb: Float = rightBottom * density
        mRadii = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)
        updateDrawable()
    }

    fun setBorderWidthDP(width: Float) {
        val scaledWidth: Float = resources.displayMetrics.density * width
        if (borderWidth != scaledWidth) {
            borderWidth = scaledWidth
            updateDrawable()
            this.invalidate()
        }
    }

    var borderColor: Int
        get() {
            return borderColors!!.defaultColor
        }
        set(color) {
            setBorderColor(ColorStateList.valueOf(color))
        }

    fun setBorderColor(colors: ColorStateList?) {
        if (!(borderColors == colors)) {
            borderColors = if (colors != null) colors else ColorStateList.valueOf(-16777216)
            updateDrawable()
            if (borderWidth > 0.0f) {
                this.invalidate()
            }
        }
    }

    fun isOval(): Boolean {
        return isOval
    }

    fun setOval(oval: Boolean) {
        isOval = oval
        updateDrawable()
        this.invalidate()
    }

    override fun setScaleType(scaleType: ScaleType) {
        super.setScaleType(scaleType)
        mScaleType = scaleType
        updateDrawable()
    }

    internal class SelectableRoundedCornerDrawable constructor(bitmap: Bitmap?, r: Resources) :
        Drawable() {
        private val mBounds: RectF = RectF()
        private val mBorderBounds: RectF = RectF()
        private val mBitmapRect: RectF = RectF()
        private var mBitmapWidth: Int = 0
        private var mBitmapHeight: Int = 0
        private val mBitmapPaint: Paint
        private val mBorderPaint: Paint
        private val mBitmapShader: BitmapShader
        private val mRadii: FloatArray =
            floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        private val mBorderRadii: FloatArray =
            floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        private var mOval: Boolean = false
        private var mBorderWidth: Float = 0.0f
        var borderColors: ColorStateList = ColorStateList.valueOf(-16777216)
            private set
        private var mScaleType: ScaleType
        private val mPath: Path
        private val mBitmap: Bitmap?
        private var mBoundsConfigured: Boolean
        override fun isStateful(): Boolean {
            return borderColors.isStateful
        }

        override fun onStateChange(state: IntArray): Boolean {
            val newColor: Int = borderColors.getColorForState(state, 0)
            if (mBorderPaint.color != newColor) {
                mBorderPaint.color = newColor
                return true
            } else {
                return super.onStateChange(state)
            }
        }

        private fun configureBounds(canvas: Canvas) {
            val clipBounds: Rect = canvas.clipBounds
            val canvasMatrix: Matrix = canvas.matrix
            if (ScaleType.CENTER == mScaleType) {
                mBounds.set(clipBounds)
            } else if (ScaleType.CENTER_CROP == mScaleType) {
                applyScaleToRadii(canvasMatrix)
                mBounds.set(clipBounds)
            } else if (ScaleType.FIT_XY == mScaleType) {
                val m: Matrix = Matrix()
                m.setRectToRect(mBitmapRect, RectF(clipBounds), ScaleToFit.FILL)
                mBitmapShader.setLocalMatrix(m)
                mBounds.set(clipBounds)
            } else if ((ScaleType.FIT_START != mScaleType) && (ScaleType.FIT_END != mScaleType) && (ScaleType.FIT_CENTER != mScaleType) && (ScaleType.CENTER_INSIDE != mScaleType)) {
                if (ScaleType.MATRIX == mScaleType) {
                    applyScaleToRadii(canvasMatrix)
                    mBounds.set(mBitmapRect)
                }
            } else {
                applyScaleToRadii(canvasMatrix)
                mBounds.set(mBitmapRect)
            }
        }

        private fun applyScaleToRadii(m: Matrix) {
            val values: FloatArray = FloatArray(9)
            m.getValues(values)
            for (i in mRadii.indices) {
                mRadii[i] /= values.get(0)
            }
        }

        private fun adjustCanvasForBorder(canvas: Canvas) {
            val canvasMatrix: Matrix = canvas.matrix
            val values: FloatArray = FloatArray(9)
            canvasMatrix.getValues(values)
            val scaleFactorX: Float = values.get(0)
            val scaleFactorY: Float = values.get(4)
            val translateX: Float = values.get(2)
            val translateY: Float = values.get(5)
            val newScaleX: Float = mBounds.width() / (mBounds.width() + mBorderWidth + mBorderWidth)
            val newScaleY: Float =
                mBounds.height() / (mBounds.height() + mBorderWidth + mBorderWidth)
            canvas.scale(newScaleX, newScaleY)
            if ((ScaleType.FIT_START != mScaleType) && (ScaleType.FIT_END != mScaleType) && (ScaleType.FIT_XY != mScaleType) && (ScaleType.FIT_CENTER != mScaleType) && (ScaleType.CENTER_INSIDE != mScaleType) && (ScaleType.MATRIX != mScaleType)) {
                if (ScaleType.CENTER == mScaleType || ScaleType.CENTER_CROP == mScaleType) {
                    canvas.translate(
                        -translateX / (newScaleX * scaleFactorX),
                        -translateY / (newScaleY * scaleFactorY)
                    )
                    canvas.translate(-(mBounds.left - mBorderWidth), -(mBounds.top - mBorderWidth))
                }
            } else {
                canvas.translate(mBorderWidth, mBorderWidth)
            }
        }

        private fun adjustBorderWidthAndBorderBounds(canvas: Canvas) {
            val canvasMatrix: Matrix = canvas.matrix
            val values: FloatArray = FloatArray(9)
            canvasMatrix.getValues(values)
            val scaleFactor: Float = values.get(0)
            val viewWidth: Float = mBounds.width() * scaleFactor
            mBorderWidth = mBorderWidth * mBounds.width() / (viewWidth - 2.0f * mBorderWidth)
            mBorderPaint.strokeWidth = mBorderWidth
            mBorderBounds.set(mBounds)
            mBorderBounds.inset(-mBorderWidth / 2.0f, -mBorderWidth / 2.0f)
        }

        private fun setBorderRadii() {
            for (i in mRadii.indices) {
                if (mRadii[i] > 0.0f) {
                    mBorderRadii[i] = mRadii.get(i)
                    mRadii[i] -= mBorderWidth
                }
            }
        }

        override fun draw(canvas: Canvas) {
            canvas.save()
            if (!mBoundsConfigured) {
                configureBounds(canvas)
                if (mBorderWidth > 0.0f) {
                    adjustBorderWidthAndBorderBounds(canvas)
                    setBorderRadii()
                }
                mBoundsConfigured = true
            }
            if (mOval) {
                if (mBorderWidth > 0.0f) {
                    adjustCanvasForBorder(canvas)
                    mPath.addOval(mBounds, Path.Direction.CW)
                    canvas.drawPath(mPath, mBitmapPaint)
                    mPath.reset()
                    mPath.addOval(mBorderBounds, Path.Direction.CW)
                    canvas.drawPath(mPath, mBorderPaint)
                } else {
                    mPath.addOval(mBounds, Path.Direction.CW)
                    canvas.drawPath(mPath, mBitmapPaint)
                }
            } else if (mBorderWidth > 0.0f) {
                adjustCanvasForBorder(canvas)
                mPath.addRoundRect(mBounds, mRadii, Path.Direction.CW)
                canvas.drawPath(mPath, mBitmapPaint)
                mPath.reset()
                mPath.addRoundRect(mBorderBounds, mBorderRadii, Path.Direction.CW)
                canvas.drawPath(mPath, mBorderPaint)
            } else {
                mPath.addRoundRect(mBounds, mRadii, Path.Direction.CW)
                canvas.drawPath(mPath, mBitmapPaint)
            }
            canvas.restore()
        }

        fun setCornerRadii(radii: FloatArray?) {
            if (radii != null) {
                if (radii.size != 8) {
                    throw ArrayIndexOutOfBoundsException("radii[] needs 8 values")
                } else {
                    for (i in radii.indices) {
                        mRadii[i] = radii.get(i)
                    }
                }
            }
        }

        @SuppressLint("WrongConstant")
        override fun getOpacity(): Int {
            return if ((mBitmap != null) && !mBitmap.hasAlpha() && (mBitmapPaint.alpha >= 255)) -1 else -3
        }

        override fun setAlpha(alpha: Int) {
            mBitmapPaint.alpha = alpha
            invalidateSelf()
        }

        override fun setColorFilter(cf: ColorFilter?) {
            mBitmapPaint.colorFilter = cf
            invalidateSelf()
        }

        override fun setDither(dither: Boolean) {
            mBitmapPaint.isDither = dither
            invalidateSelf()
        }

        override fun setFilterBitmap(filter: Boolean) {
            mBitmapPaint.isFilterBitmap = filter
            invalidateSelf()
        }

        override fun getIntrinsicWidth(): Int {
            return mBitmapWidth
        }

        override fun getIntrinsicHeight(): Int {
            return mBitmapHeight
        }

        var borderWidth: Float
            get() {
                return mBorderWidth
            }
            set(width) {
                mBorderWidth = width
                mBorderPaint.strokeWidth = width
            }
        var borderColor: Int
            get() {
                return borderColors.defaultColor
            }
            set(color) {
                setBorderColor(ColorStateList.valueOf(color))
            }

        fun setBorderColor(colors: ColorStateList?) {
            if (colors == null) {
                mBorderWidth = 0.0f
                borderColors = ColorStateList.valueOf(0)
                mBorderPaint.color = 0
            } else {
                borderColors = colors
                mBorderPaint.color = borderColors.getColorForState(state, -16777216)
            }
        }

        fun isOval(): Boolean {
            return mOval
        }

        fun setOval(oval: Boolean) {
            mOval = oval
        }

        var scaleType: ScaleType?
            get() {
                return mScaleType
            }
            set(scaleType) {
                if (scaleType != null) {
                    mScaleType = scaleType
                }
            }

        companion object {
            private val TAG: String = "SelectableRoundedCornerDrawable"
            private val DEFAULT_BORDER_COLOR: Int = -16777216
            fun fromBitmap(bitmap: Bitmap?, r: Resources): SelectableRoundedCornerDrawable? {
                return if (bitmap != null) SelectableRoundedCornerDrawable(bitmap, r) else null
            }

            fun fromDrawable(drawable: Drawable?, r: Resources): Drawable? {
                if (drawable != null) {
                    if (drawable is SelectableRoundedCornerDrawable) {
                        return drawable
                    }
                    if (drawable is LayerDrawable) {
                        val ld: LayerDrawable = drawable
                        val num: Int = ld.numberOfLayers
                        for (i in 0 until num) {
                            val d: Drawable = ld.getDrawable(i)
                            ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d, r))
                        }
                        return ld
                    }
                    val bm: Bitmap? = drawableToBitmap(drawable)
                    if (bm != null) {
                        return SelectableRoundedCornerDrawable(bm, r)
                    }
                    Log.w("SRIV", "Failed to create bitmap from drawable!")
                }
                return drawable
            }

            fun drawableToBitmap(drawable: Drawable?): Bitmap? {
                if (drawable == null) {
                    return null
                } else if (drawable is BitmapDrawable) {
                    return drawable.bitmap
                } else {
                    val width: Int = Math.max(drawable.intrinsicWidth, 2)
                    val height: Int = Math.max(drawable.intrinsicHeight, 2)
                    var bitmap: Bitmap?
                    try {
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        val canvas: Canvas = Canvas(bitmap)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                    } catch (var5: IllegalArgumentException) {
                        var5.printStackTrace()
                        bitmap = null
                    }
                    return bitmap
                }
            }
        }

        init {
            mScaleType = ScaleType.FIT_CENTER
            mPath = Path()
            mBoundsConfigured = false
            mBitmap = bitmap
            mBitmapShader = BitmapShader((bitmap)!!, TileMode.CLAMP, TileMode.CLAMP)
            if (bitmap != null) {
                mBitmapWidth = bitmap.getScaledWidth(r.displayMetrics)
                mBitmapHeight = bitmap.getScaledHeight(r.displayMetrics)
            } else {
                mBitmapHeight = -1
                mBitmapWidth = mBitmapHeight
            }
            mBitmapRect.set(0.0f, 0.0f, mBitmapWidth.toFloat(), mBitmapHeight.toFloat())
            mBitmapPaint = Paint(1)
            mBitmapPaint.style = Paint.Style.FILL
            mBitmapPaint.shader = mBitmapShader
            mBorderPaint = Paint(1)
            mBorderPaint.style = Paint.Style.STROKE
            mBorderPaint.color = borderColors.getColorForState(state, -16777216)
            mBorderPaint.strokeWidth = mBorderWidth
        }
    }
}