package smartboard.fyp.com.smartapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import java.util.*

class Multitouch : View {
    var current_path_count = -1
    var m_Path_list = ArrayList<Path>()
    var mX_list = ArrayList<Float>()
    var mY_list = ArrayList<Float>()
    var mActivePointerId_list = ArrayList<Int>()
    var arrayListPaths = ArrayList<Pair<Path, Paint>>()

    //ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<Pair<Path, Paint>>();
    private var m_Canvas: Canvas? = null
    private var m_Paint: Paint? = null
    private val mX = 0f
    private val mY = 0f
    private lateinit var bitmapToCanvas: Bitmap

    // The ‘active pointer’ is the one currently moving our object.
    private var mActivePointerId = INVALID_POINTER_ID

    constructor(context: Context?) : super(context) {
        isFocusable = true
        isFocusableInTouchMode = true
        onCanvasInitialization()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        isFocusable = true
        isFocusableInTouchMode = true
        onCanvasInitialization()
    }

    fun onCanvasInitialization() {
        m_Paint = Paint()
        m_Paint!!.isAntiAlias = true
        m_Paint!!.isDither = true
        m_Paint!!.color = Color.parseColor("#37A1D1")
        m_Paint!!.style = Paint.Style.STROKE
        m_Paint!!.strokeJoin = Paint.Join.ROUND
        m_Paint!!.strokeCap = Paint.Cap.ROUND
        m_Paint!!.strokeWidth = 2f

        //   m_Path = new Path();
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmapToCanvas = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        m_Canvas = Canvas(bitmapToCanvas)
    }

    override fun onDraw(canvas: Canvas) {
        try {
            canvas.drawBitmap(bitmapToCanvas, 0f, 0f, null)
            for (i in 0..current_path_count) {
                canvas.drawPath(m_Path_list[i], m_Paint!!)
            }
        } catch (e: Exception) {
        }
    }

    fun onDrawCanvas() {
        for (p in arrayListPaths) {
            m_Canvas!!.drawPath(p.first, p.second)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val action = event.action
        try {
            when (action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val x = event.x
                    val y = event.y
                    current_path_count = 0
                    mActivePointerId_list.add(event.getPointerId(0), current_path_count)
                    touch_start(x, y, current_path_count)
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if (event.pointerCount > current_path_count) {
                        current_path_count++
                        val x = event.getX(current_path_count)
                        val y = event.getY(current_path_count)
                        mActivePointerId_list.add(
                            event.getPointerId(current_path_count),
                            current_path_count
                        )
                        touch_start(x, y, current_path_count)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    var i = 0
                    while (i <= current_path_count) {
                        try {
                            val pointerIndex = event
                                .findPointerIndex(mActivePointerId_list[i])
                            val x = event.getX(pointerIndex)
                            val y = event.getY(pointerIndex)
                            touch_move(x, y, i)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        i++
                    }
                }
                MotionEvent.ACTION_UP -> {

                    //current_path_count = -1;
                    var i = 0
                    while (i <= current_path_count) {
                        touch_up(i)
                        i++
                    }
                    mActivePointerId_list = ArrayList()
                }
                MotionEvent.ACTION_CANCEL -> {
                    mActivePointerId = INVALID_POINTER_ID
                    current_path_count = -1
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    val pointerIndex =
                        event.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                    val pointerId = event.getPointerId(pointerIndex)
                    var i = 0
                    while (i <= current_path_count) {
                        if (pointerId == mActivePointerId_list[i]) {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.
                            mActivePointerId_list.removeAt(i)
                            touch_up(i)
                            break
                        }
                        i++
                    }
                }
                MotionEvent.ACTION_OUTSIDE -> {
                }
            }
        } catch (e: Exception) {
        }
        invalidate()
        return true
    }

    private fun touch_start(x: Float, y: Float, count: Int) {
        val m_Path = Path()
        m_Path_list.add(count, m_Path)
        m_Path_list[count].reset()
        m_Path_list[count].moveTo(x, y)
        mX_list.add(count, x)
        mY_list.add(count, y)
    }

    private fun touch_move(x: Float, y: Float, count: Int) {
        val dx = Math.abs(x - mX_list[count])
        val dy = Math.abs(y - mY_list[count])
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            m_Path_list[count].quadTo(
                mX_list[count],
                mY_list[count],
                (x + mX_list[count]) / 2,
                (y + mY_list[count]) / 2
            )
            try {
                mX_list.removeAt(count)
                mY_list.removeAt(count)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mX_list.add(count, x)
            mY_list.add(count, y)
        }
    }

    private fun touch_up(count: Int) {
        m_Path_list[count].lineTo(mX_list[count], mY_list[count])

        // commit the path to our offscreen
        m_Canvas!!.drawPath(m_Path_list[count], m_Paint!!)

        // kill this so we don't double draw
        val newPaint = Paint(m_Paint) // Clones the mPaint object
        arrayListPaths.add(
            Pair(
                m_Path_list[count], newPaint
            )
        )
        m_Path_list.removeAt(count)
        mX_list.removeAt(count)
        mY_list.removeAt(count)
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f

        //   private Path            m_Path;
        private const val INVALID_POINTER_ID = -1
    }
}