package com.cdp.pro_manager.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.activities.TaskListActivity
import com.cdp.pro_manager.models.Task

open class TaskListItemsAdapter (private val context: Context, private var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_task,parent,false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp()).toPx(),0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            if(position == list.size-1){
                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.requireViewById<LinearLayout>(R.id.ll_task_item).visibility =View.GONE
            }else{
                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.requireViewById<LinearLayout>(R.id.ll_task_item).visibility =View.VISIBLE
            }
            holder.itemView.requireViewById<TextView>(R.id.tv_task_list_title).text = model.title
            holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {
                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.requireViewById<CardView>(R.id.cv_add_task_list_name).visibility =View.VISIBLE
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {

                holder.itemView.requireViewById<TextView>(R.id.tv_add_task_list).visibility = View.VISIBLE
                holder.itemView.requireViewById<CardView>(R.id.cv_add_task_list_name).visibility =View.GONE
            }
            holder.itemView.requireViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {

                val listName = holder.itemView.requireViewById<EditText>(R.id.et_task_list_name).text.toString()

                if(listName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.createTaskList(listName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter List Name.",Toast.LENGTH_LONG).show()
                }
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener {
                holder.itemView.requireViewById<EditText>(R.id.et_edit_task_list_name).setText(model.title)
                holder.itemView.requireViewById<LinearLayout>(R.id.ll_title_view).visibility = View.GONE
                holder.itemView.requireViewById<CardView>(R.id.cv_edit_task_list_name).visibility =View.VISIBLE
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_close_editable_view).setOnClickListener {

                holder.itemView.requireViewById<LinearLayout>(R.id.ll_title_view).visibility = View.VISIBLE
                holder.itemView.requireViewById<CardView>(R.id.cv_edit_task_list_name).visibility =View.GONE
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_done_edit_list_name).setOnClickListener {

                val listName = holder.itemView.requireViewById<EditText>(R.id.et_edit_task_list_name).text.toString()


                if (listName.isNotEmpty()){
                    if (context is TaskListActivity){
                        context.updateTaskList(position,listName,model)
                    }
                }else{
                    Toast.makeText(context,"Please Enter List Name",Toast.LENGTH_LONG).show()
                }


            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {
                alertDialogForDeleteList(position,model.title)
            }

            holder.itemView.requireViewById<TextView>(R.id.tv_add_card).setOnClickListener {
                holder.itemView.requireViewById<TextView>(R.id.tv_add_card).visibility =View.GONE
                holder.itemView.requireViewById<CardView>(R.id.cv_add_card).visibility = View.VISIBLE
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_close_card_name).setOnClickListener {
                holder.itemView.requireViewById<TextView>(R.id.tv_add_card).visibility =View.VISIBLE
                holder.itemView.requireViewById<CardView>(R.id.cv_add_card).visibility = View.GONE
            }

            holder.itemView.requireViewById<ImageButton>(R.id.ib_done_card_name).setOnClickListener {

                val cardName = holder.itemView.requireViewById<EditText>(R.id.et_card_name).text.toString()

                if(cardName.isNotEmpty()){
                    if(context is TaskListActivity){
                        context.addCardToTaskList(position,cardName)
                    }
                }else{
                    Toast.makeText(context,"Please Enter a Card Name.",Toast.LENGTH_LONG).show()
                }
            }
            holder.itemView.requireViewById<RecyclerView>(R.id.rv_card_list).layoutManager = LinearLayoutManager(context)
            holder.itemView.requireViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context,model.cards)
            holder.itemView.requireViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter


            adapter.setOnClickListener(
                object : CardListItemsAdapter.OnClickListener{
                    override fun onClick(cardPosition: Int) {

                        if(context is TaskListActivity){
                            context.cardDetails(position, cardPosition )
                        }

                    }
                }

            )

        }

    }

    private fun alertDialogForDeleteList(position:Int, title : String){
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title .")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){
            dialogInterface,which ->
            dialogInterface.dismiss()

            if(context is TaskListActivity){
                context.deleteTaskList(position)
            }
        }
        builder.setNegativeButton("No"){
            dialogInterface,which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int=
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int=
        (this * Resources.getSystem().displayMetrics.density).toInt()


    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)


}