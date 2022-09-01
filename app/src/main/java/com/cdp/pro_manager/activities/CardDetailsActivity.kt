package com.cdp.pro_manager.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cdp.pro_manager.R
import com.cdp.pro_manager.adapters.CardMemberListItemsAdapter
import com.cdp.pro_manager.dialogs.LabelColorListDialog
import com.cdp.pro_manager.dialogs.MembersListDialog
import com.cdp.pro_manager.firebase.FirestoreClass
import com.cdp.pro_manager.models.*
import com.cdp.pro_manager.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private lateinit var mBoardDetails : Board
    private var mTaskListPosition = -1
    private var mCardPosition = -1
    private var mSelectedColor = ""

    private lateinit var mMemberDetailList: ArrayList<User>
    private var mSelectedDueDateMilliSeconds:Long =0

    var toolbarCardDetailsActivity : Toolbar?=null
    var editTextCard: AppCompatEditText?=null
    var btnUpdate :Button?=null
    var colorText:TextView?=null
    var selectMembers :TextView? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        toolbarCardDetailsActivity = findViewById(R.id.toolbar_card_details_activity)
        editTextCard = findViewById(R.id.et_name_card_details)
        btnUpdate = findViewById(R.id.btn_update_card_details)
        colorText = findViewById(R.id.tv_select_label_color)
        selectMembers = findViewById(R.id.tv_select_members)


        getIntentData()
        setupActionBar()
        editTextCard?.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        editTextCard?.setSelection(editTextCard?.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor

        if (mSelectedColor.isNotEmpty()){
            setColor()
        }

        btnUpdate?.setOnClickListener {
            if(editTextCard?.text.toString().isNotEmpty()){
                updateCardDetails()
            }else{
                Toast.makeText(this@CardDetailsActivity,"Enter a card name.",Toast.LENGTH_LONG).show()
            }
        }

        colorText?.setOnClickListener {
            labelColorsListDialog()
        }

        selectMembers?.setOnClickListener {
            membersListDialog()
        }


        setSelectedMembersList()

        mSelectedDueDateMilliSeconds = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate

        if(mSelectedDueDateMilliSeconds > 0){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            requireViewById<TextView>(R.id.tv_select_due_date).text = selectedDate
        }

        requireViewById<TextView>(R.id.tv_select_due_date).setOnClickListener {
            showDatePicker()
        }


    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()

    }

    private fun setupActionBar(){
        setSupportActionBar(toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_white_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition]
                .cards[mCardPosition].name

        }
        toolbarCardDetailsActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun colorsList():ArrayList<String>{
        val colorsList : ArrayList<String> = ArrayList()
        colorsList.add("#FFAFCC")
        colorsList.add("#A2D2FF")
        colorsList.add("#A8DADC")
        colorsList.add("#B8C0FF")
        colorsList.add("#FFDDD2")
        colorsList.add("#9F86C0")
        colorsList.add("#2EC4B6")
        colorsList.add("#48CAE4")
        colorsList.add("#C9ADA7")
        colorsList.add("#FF758F")
        colorsList.add("#DEE2E6")
        colorsList.add("#A4C3B2")
        colorsList.add("#FFD7BA")
        colorsList.add("#E8C2CA")
        colorsList.add("#FCF6BD")
        return colorsList
    }

    private fun setColor(){
        colorText?.text=""
        colorText?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){R.id.action_delete_member->{
            alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
            return true
        }


        }

        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData(){
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if(intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMemberDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        if(cardAssignedMembersList.size >0){
            for(i in mMemberDetailList.indices){
                for(j in cardAssignedMembersList){
                    if(mMemberDetailList[i].id == j){
                        mMemberDetailList[i].selected = true
                    }
                }
            }
        }else{
            for(i in mMemberDetailList.indices){
                mMemberDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
            this,
            mMemberDetailList,
            resources.getString((R.string.str_select_members))

        ){

            override fun onItemSelected(user: User, action: String) {
                if(action == Constants.SELECT){
                    if(!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)){
                        mBoardDetails
                            .taskList[mTaskListPosition].cards[mCardPosition]
                            .assignedTo.add(user.id)
                    }
                }
                else{
                    mBoardDetails
                        .taskList[mTaskListPosition].cards[mCardPosition]
                        .assignedTo.remove(user.id)

                    for(i in mMemberDetailList.indices){
                        if(mMemberDetailList[i].id == user.id){
                            mMemberDetailList[i].selected = false
                        }


                    }
                }

                setSelectedMembersList()


            }

        }
        listDialog.show()
    }

    private fun updateCardDetails(){
        val card = Card(
            editTextCard?.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )

        val taskList : ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun deleteCard(){
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)
        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity,mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card,cardName
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)){ dialogInterface, which ->
            dialogInterface.dismiss()
            deleteCard()
        }

        builder.setNegativeButton(resources.getString(R.string.no)){ dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),mSelectedColor
        ){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }

        listDialog.show()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setSelectedMembersList(){
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        for(i in mMemberDetailList.indices){
            for(j in cardAssignedMemberList){
                if(mMemberDetailList[i].id == j){
                    val selectedMember = SelectedMembers(
                        mMemberDetailList[i].id,
                        mMemberDetailList[i].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }
        if(selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("",""))
            requireViewById<TextView>(R.id.tv_select_members).visibility = View.GONE
            requireViewById<RecyclerView>(R.id.rv_selected_members_list).visibility =View.VISIBLE

            requireViewById<RecyclerView>(R.id.rv_selected_members_list).layoutManager = GridLayoutManager(
                this,6
            )
            val adapter = CardMemberListItemsAdapter(this,selectedMembersList,true)

            requireViewById<RecyclerView>(R.id.rv_selected_members_list).adapter = adapter
            adapter.setOnClickListener(
                object : CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }
                }
            )
        }else{
            requireViewById<TextView>(R.id.tv_select_members).visibility = View.VISIBLE
            requireViewById<RecyclerView>(R.id.rv_selected_members_list).visibility = View.GONE
        }
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun showDatePicker(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this,
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val sDayOfMonth = if(dayOfMonth<10) "0$dayOfMonth" else "$dayOfMonth"
            val sMonthOfYear =
                if((monthOfYear + 1)<10) "0${monthOfYear+1}" else "${monthOfYear+1}"

            val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
            requireViewById<TextView>(R.id.tv_select_due_date).text = selectedDate

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val theDate = sdf.parse(selectedDate)
            mSelectedDueDateMilliSeconds = theDate!!.time
        },
            year,
            month,
            day
        )
        dpd.show()
    }

}