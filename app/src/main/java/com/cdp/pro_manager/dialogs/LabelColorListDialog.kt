package com.cdp.pro_manager.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.LabelColorListItemsAdapter

abstract class LabelColorListDialog (
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private var mSelectedColor : String =""
        ): Dialog(context){

            private var adapter: LabelColorListItemsAdapter? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list,null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)


    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setUpRecyclerView(view: View){
        view.requireViewById<TextView>(R.id.tvTitle).text = title
        view.requireViewById<RecyclerView>(R.id.rvList).layoutManager = LinearLayoutManager(context)
        adapter = LabelColorListItemsAdapter(context,list,mSelectedColor)
        view.requireViewById<RecyclerView>(R.id.rvList).adapter = adapter

        adapter!!.onItemClickListner = object : LabelColorListItemsAdapter.OnItemClickListener{
            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }

    protected abstract fun onItemSelected(color:String)


}