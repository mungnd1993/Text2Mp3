package com.texttomp3.texttospeech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseRecyclerView
import com.texttomp3.texttospeech.data.models.Sort
import com.texttomp3.texttospeech.databinding.ItemSortBinding
import com.texttomp3.texttospeech.utils.Constants.A_Z
import com.texttomp3.texttospeech.utils.Constants.LAST_MODIFIED
import com.texttomp3.texttospeech.utils.Constants.NAME
import com.texttomp3.texttospeech.utils.Constants.NEW_TO_OLD
import com.texttomp3.texttospeech.utils.Constants.OLD_TO_NEW
import com.texttomp3.texttospeech.utils.Constants.Z_A

class SortAdapter(private val listener: OnClickListener) :
    BaseRecyclerView<ItemSortBinding, Sort>() {
    override fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): ItemSortBinding {
        return ItemSortBinding.inflate(inflater, parent, false)
    }

    override fun areContentsTheSame(oldItem: Sort, newItem: Sort): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Sort, newItem: Sort): Boolean {
        return oldItem.subtitle == newItem.subtitle
    }

    override fun setData(binding: ItemSortBinding, item: Sort, layoutPosition: Int) {
        with(binding) {
            when (item.title) {
                LAST_MODIFIED -> {
                    tvTitle.text = context?.let { getString(it, R.string.last_modified) }
                }

                NAME -> {
                    tvTitle.text = context?.let { getString(it, R.string.name) }
                }
            }
            when (item.subtitle) {
                NEW_TO_OLD -> {
                    tvSubtitle.text = context?.let { getString(it, R.string.new_to_old) }
                    ivImage.setImageResource(R.drawable.ic_new_to_old)
                }
                OLD_TO_NEW -> {
                    tvSubtitle.text = context?.let { getString(it, R.string.old_to_new) }
                    ivImage.setImageResource(R.drawable.ic_old_to_new)
                }
                A_Z -> {
                    tvSubtitle.text = context?.let { getString(it, R.string.a_z) }
                    ivImage.setImageResource(R.drawable.ic_az)
                }
                Z_A -> {
                    tvSubtitle.text = context?.let { getString(it, R.string.z_a) }
                    ivImage.setImageResource(R.drawable.ic_za)
                }
            }
            if (item.isSelected) {
                ivTick.visibility = View.VISIBLE
                clItem.setBackgroundResource(R.drawable.bg_grey_rounded_16)
            } else {
                ivTick.visibility = View.GONE
                clItem.setBackgroundResource(0)
            }

            clItem.setOnClickListener {
                listener.onClick(item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(item: Sort)
    }
}