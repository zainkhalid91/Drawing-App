package smartboard.fyp.com.smartapp

import android.graphics.Bitmap
import android.view.View

object ScreenShot {
    fun takescreenshot(v: View): Bitmap {
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache(true)
        val b = Bitmap.createBitmap(v.drawingCache)
        v.isDrawingCacheEnabled = false
        return b
    }

    fun takescreenshotOfRootView(v: View?): Bitmap {
        return takescreenshot(v!!.rootView)
    }
}