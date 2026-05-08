package com.ahmadabuhasan.pointofsales.settings.categories

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
import com.ahmadabuhasan.pointofsales.databinding.CategoryItemBinding
import es.dmoral.toasty.Toasty

/*
 * Created by Ahmad Abu Hasan (C) 2022
 */

class CategoryAdapter(
    private val context: Context,
    private val categoryData: MutableList<HashMap<String, String>>
) : RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val categoryId = categoryData[position][Constant.CATEGORY_ID]

        holder.binding.tvCategoryName.text = categoryData[position][Constant.CATEGORY_NAME]
        holder.binding.ivDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setMessage(R.string.want_to_delete_category)
                .setCancelable(false)
                .setPositiveButton(R.string.yes) { dialogInterface, _ ->
                    val databaseAccess = DatabaseAccess.getInstance(context)
                    databaseAccess.open()
                    if (databaseAccess.deleteCategory(categoryId)) {
                        Toasty.success(context, R.string.category_deleted, Toasty.LENGTH_SHORT).show()
                        categoryData.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                    } else {
                        Toasty.error(context, R.string.failed, Toasty.LENGTH_SHORT).show()
                    }
                    dialogInterface.cancel()
                }.setNegativeButton(R.string.no) { dialogInterface, _ -> dialogInterface.cancel() }.show()
        }
    }

    override fun getItemCount(): Int = categoryData.size

    inner class MyViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init { itemView.setOnClickListener(this) }

        override fun onClick(view: View) {
            val i = Intent(context, EditCategoryActivity::class.java)
            i.putExtra(Constant.CATEGORY_ID, categoryData[adapterPosition][Constant.CATEGORY_ID].orEmpty())
            i.putExtra(Constant.CATEGORY_NAME, categoryData[adapterPosition][Constant.CATEGORY_NAME].orEmpty())
            context.startActivity(i)
        }
    }
}
