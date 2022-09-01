package com.cdp.pro_manager.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R

class LabelColorListItemsAdapter (private val context: Context,
                                  private var list:ArrayList<String>,
                                  private val mSelectedColor:String)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClickListner :OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_label_color,parent,false))


    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if(holder is MyViewHolder){
            holder.itemView.requireViewById<View>(R.id.view_main).setBackgroundColor(Color.parseColor(item))

            if(item == mSelectedColor){
                holder.itemView.requireViewById<ImageView>(R.id.iv_selected_color).visibility = View.VISIBLE
            }else{
                holder.itemView.requireViewById<ImageView>(R.id.iv_selected_color).visibility = View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onItemClickListner != null){
                    onItemClickListner!!.onClick(position,item)

                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnItemClickListener{
        fun onClick(position:Int, color:String)
    }

}