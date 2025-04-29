package com.texttomp3.texttospeech.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerView<VB : ViewBinding, T> :
    RecyclerView.Adapter<BaseRecyclerView<VB, T>.ViewHolder>() {
    abstract fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): VB

    abstract fun setData(binding: VB, item: T, layoutPosition: Int)

    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    open fun onClickViews(binding: VB, obj: T, layoutPosition: Int) {
        binding.root.setOnClickListener {
        }
    }

    val items: MutableList<T> = mutableListOf()
    var context: Context? = null

    open fun submitData(newData: List<T>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = items.size
            override fun getNewListSize(): Int = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(items[oldItemPosition], newData[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areContentsTheSame(items[oldItemPosition], newData[newItemPosition])
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = getItemLayout(LayoutInflater.from(parent.context), parent)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getList(): List<T> = items

    inner class ViewHolder(private val binding: VB) : BaseViewHolder<T>(binding) {
        override fun bindData(obj: T) {
            setData(binding, obj, layoutPosition)
            onClickViews(binding, obj, layoutPosition)
        }
    }

    abstract class BaseViewHolder<T>(binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bindData(obj: T)
    }
}