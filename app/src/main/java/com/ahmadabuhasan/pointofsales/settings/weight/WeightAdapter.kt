package com.ahmadabuhasan.pointofsales.settings.weight

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadabuhasan.pointofsales.Constant
import com.ahmadabuhasan.pointofsales.R
import com.ahmadabuhasan.pointofsales.database.DatabaseAccess
import com.ahmadabuhasan.pointofsales.databinding.WeightItemBinding
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class WeightAdapter(
    private val context: Context,
    private val weightData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<WeightAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = WeightItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val weight_id = weightData[position][Constant.WEIGHT_ID]

        holder.binding.tvWeightName.text = weightData[position][Constant.WEIGHT_UNIT]
        holder.binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialogInterface, _ ->
                    val databaseAccess = DatabaseAccess.getInstance(context)
                    databaseAccess.open()
                    if (databaseAccess.deleteWeight(weight_id)) {
                        Toasty.success(context, R.string.weight_unit_deleted, Toasty.LENGTH_SHORT).show()
                        weightData.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    } else {
                        Toasty.error(context, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                    dialogInterface.cancel()
                }.setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }.show()
        }
    }

    override fun getItemCount(): Int = weightData.size

    inner class MyViewHolder(val binding: WeightItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init { itemView.setOnClickListener(this) }

        override fun onClick(view: View) {
            val i = Intent(context, EditWeightActivity::class.java)
            i.putExtra(Constant.WEIGHT_ID, weightData[adapterPosition][Constant.WEIGHT_ID] as String)
            i.putExtra(Constant.WEIGHT_UNIT, weightData[adapterPosition][Constant.WEIGHT_UNIT] as String)
            context.startActivity(i)
        }
    }
}
