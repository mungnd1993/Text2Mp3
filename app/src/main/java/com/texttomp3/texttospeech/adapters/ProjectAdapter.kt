package com.texttomp3.texttospeech.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.base.BaseRecyclerView
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.databinding.ItemProjectBinding
import com.texttomp3.texttospeech.utils.Utils

class ProjectAdapter(private val listener: OnClickListener) : BaseRecyclerView<ItemProjectBinding, Project>() {
    override fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): ItemProjectBinding {
        return ItemProjectBinding.inflate(inflater, parent, false)
    }

    override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem.id == newItem.id
    }

    override fun setData(binding: ItemProjectBinding, item: Project, layoutPosition: Int) {
        with(binding) {
            tvFilename.text = item.name
            tvDate.text = Utils.formatToDate(item.time)
            tvTime.text = Utils.formatToTime(item.time)

            ivImage.setCountryCode(item.language)

            clProject.setOnClickListener {
                listener.onClick(item)
            }

            ivMore.setOnClickListener {
                listener.onClickMore(item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(item: Project)
        fun onClickMore(item: Project)
    }
}