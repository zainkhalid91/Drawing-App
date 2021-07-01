package smartboard.fyp.com.smartapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.io.File
import java.util.*

class PDFAdapter(context: Context, var al_pdf: ArrayList<File>) : ArrayAdapter<File?>(
    context, R.layout.adapter_pdf, al_pdf as List<File?>
) {
    var viewHolder: ViewHolder? = null
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getViewTypeCount(): Int {
        return if (al_pdf.size > 0) {
            al_pdf.size
        } else {
            1
        }
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_pdf, parent, false)
            viewHolder = ViewHolder()
            viewHolder!!.tv_filename = view.findViewById(R.id.tv_name)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        viewHolder!!.tv_filename!!.text = al_pdf[position].name
        return view!!
    }

    inner class ViewHolder {
        var tv_filename: TextView? = null
    }
}