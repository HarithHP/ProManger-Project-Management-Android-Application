package com.cdp.pro_manager.adapters

import android.content.Context
import android.opengl.Visibility
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.models.User
import com.cdp.pro_manager.utils.Constants


open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_member,
                parent,
                false
            )
        )
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.requireViewById(R.id.iv_member_image))

            holder.itemView.requireViewById<TextView>(R.id.tv_member_name).text = model.name
            holder.itemView.requireViewById<TextView>(R.id.tv_member_email).text = model.email

            if(model.selected){

                holder.itemView.requireViewById<ImageView>(R.id.iv_selected_member).visibility = View.VISIBLE
            }else{
                holder.itemView.requireViewById<ImageView>(R.id.iv_selected_member).visibility = View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    if(model.selected){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }

                }
            }
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(position: Int,user:User,action: String)
    }


}
