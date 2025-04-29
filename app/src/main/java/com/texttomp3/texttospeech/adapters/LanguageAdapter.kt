package com.texttomp3.texttospeech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseRecyclerView
import com.texttomp3.texttospeech.data.models.Language
import com.texttomp3.texttospeech.databinding.ItemLanguageBinding
import com.texttomp3.texttospeech.helpers.LanguageHelper

class LanguageAdapter(private val listener: OnClickListener): BaseRecyclerView<ItemLanguageBinding, Language>() {

    override fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): ItemLanguageBinding {
        return ItemLanguageBinding.inflate(inflater, parent, false)
    }

    override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean {
       return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Language, newItem: Language): Boolean {
        return oldItem.name == newItem.name
    }

    override fun setData(binding: ItemLanguageBinding, item: Language, layoutPosition: Int) {
        val languages = LanguageHelper(binding.root.context).getLanguageList()
        with(binding) {
            tvName.text = languages.find { it.code == item.name }?.name
            icFlag.setCountryCode(item.name)

            if (item.isSelected) {
                clItem.setBackgroundResource(R.drawable.bg_blue_rounded)
                val typeface = ResourcesCompat.getFont(binding.root.context, R.font.roboto_semibold)
                tvName.typeface = typeface
                ivTick.visibility = View.VISIBLE
            } else {
                clItem.setBackgroundResource(R.drawable.bg_white_rounded)
                val typeface = ResourcesCompat.getFont(binding.root.context, R.font.roboto_regular)
                tvName.typeface = typeface
                ivTick.visibility = View.GONE
            }

            clItem.setOnClickListener {
                listener.onClick(item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(language: Language)
    }
}