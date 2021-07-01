package smartboard.fyp.com.smartapp

import android.app.Activity
import android.content.Intent

class DrawingActivityBuilder private constructor(  //Variables as objects
    private val context: Activity
) {
    private val intent: Intent
    fun enableToast(enabled: Boolean): DrawingActivityBuilder {
        intent.putExtra(TOAST_ENABLED, enabled)
        return this
    }

    fun setTitle(title: String?): DrawingActivityBuilder {
        intent.putExtra(TITLE, title)
        return this
    }

    fun setDefaultUtility(defaultUtility: Int): DrawingActivityBuilder {
        intent.putExtra(DEFAULT_UTILITY, defaultUtility)
        return this
    }

    fun draw(requestCode: Int) {
        context.startActivityForResult(intent, requestCode)
    }

    companion object {
        //Constants
        val TOAST_ENABLED: String = "ToastEnabled"
        val TITLE: String = "Title"
        val DEFAULT_UTILITY: String = "DefaultUtility"
        fun getInstance(context: Activity): DrawingActivityBuilder {
            return DrawingActivityBuilder(context)
        }
    }

    //Variables
    init {
        intent = Intent(context, MainActivity::class.java)
    }
}