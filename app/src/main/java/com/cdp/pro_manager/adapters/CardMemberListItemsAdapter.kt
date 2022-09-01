package com.cdp.pro_manager.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cdp.pro_manager.R
import com.cdp.pro_manager.models.SelectedMembers
import de.hdodenhof.circleimageview.CircleImageView

open class CardMemberListItemsAdapter (
    private val context: Context,
    private val list:ArrayList<SelectedMembers>,
    private val assignMembers:Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var onClickListener : OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder( LayoutInflater.from(context).inflate(
            R.layout.item_card_selected_member,
            parent,
            false
        ))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size - 1 && assignMembers){
                holder.itemView.requireViewById<CircleImageView>(R.id.iv_add_member).visibility = View.VISIBLE
                holder.itemView.requireViewById<CircleImageView>(R.id.iv_selected_member_image).visibility = View.GONE
            }else{
                holder.itemView.requireViewById<CircleImageView>(R.id.iv_add_member).visibility = View.GONE
                holder.itemView.requireViewById<CircleImageView>(R.id.iv_selected_member_image).visibility = View.VISIBLE

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.requireViewById(R.id.iv_selected_member_image))

            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick()
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

    interface OnClickListener{
        fun onClick()
    }

    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)
}