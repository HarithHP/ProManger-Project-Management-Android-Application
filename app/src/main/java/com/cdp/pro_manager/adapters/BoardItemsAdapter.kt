package com.cdp.pro_manager.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.models.Board
import de.hdodenhof.circleimageview.CircleImageView

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_board, parent, false))

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.requireViewById(R.id.iv_board_image))
            holder.itemView.requireViewById<TextView>(R.id.tv_name).text = model.name
            holder.itemView.requireViewById<TextView>(R.id.tv_created_by).text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {

                if(onClickListener !=null){
                    onClickListener!!.onClick(position,model)
                }
            }

        }

    }

    interface OnClickListener{
        fun onClick(position: Int,model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }


    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}