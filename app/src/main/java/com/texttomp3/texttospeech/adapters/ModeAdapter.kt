package com.texttomp3.texttospeech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseRecyclerView
import com.texttomp3.texttospeech.data.models.Mode
import com.texttomp3.texttospeech.databinding.ItemModeBinding
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.EDGE

class ModeAdapter(private val listener: OnClickListener): BaseRecyclerView<ItemModeBinding, Mode>() {
    override fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): ItemModeBinding {
         return ItemModeBinding.inflate(inflater, parent, false)
    }

    override fun areContentsTheSame(oldItem: Mode, newItem: Mode): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Mode, newItem: Mode): Boolean {
        return oldItem.name == newItem.name
    }

    override fun setData(binding: ItemModeBinding, item: Mode, layoutPosition: Int) {
        with(binding) {
            when (item.name) {
                ANDROID -> {
                    tvName.text = "Default Voice Engine"
                    ivIcon.setImageResource(R.drawable.ic_android)
                }
                EDGE -> {
                    tvName.text = "Edge Text To Speech"
                    ivIcon.setImageResource(R.drawable.ic_edge)
                }
            }
            if (item.isSelected) {
                clItem.setBackgroundResource(R.drawable.bg_blue_rounded)
                ivTick.visibility = View.VISIBLE
            } else {
                clItem.setBackgroundResource(R.drawable.bg_white_rounded)
                ivTick.visibility = View.GONE
            }

            clItem.setOnClickListener {
                listener.onClick(item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(mode: Mode)
    }
}