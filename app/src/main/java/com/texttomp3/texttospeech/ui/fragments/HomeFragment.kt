package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.ui.activities.SearchActivity
import com.texttomp3.texttospeech.adapters.ProjectAdapter
import com.texttomp3.texttospeech.adapters.SortAdapter
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.data.models.Sort
import com.texttomp3.texttospeech.databinding.FragmentHomeBinding
import com.texttomp3.texttospeech.ui.activities.TTSActivity
import com.texttomp3.texttospeech.utils.Constants.CONTENT
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.FILE_PATH
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.utils.Constants.ID
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.MORE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.NORMAL
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.viewmodels.HomeViewModel
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(), ProjectAdapter.OnClickListener,
    SortAdapter.OnClickListener {
    private lateinit var adapter: ProjectAdapter
    private lateinit var sortAdapter: SortAdapter

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().getViewModel<HomeViewModel>()
    }

    private val settingViewModel: SettingViewModel by lazy {
        requireActivity().getViewModel<SettingViewModel>()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getProjects()
    }


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            homeViewModel.getProjects()

            adapter = ProjectAdapter(this@HomeFragment)
            rvProject.adapter = adapter
            rvProject.layoutManager = LinearLayoutManager(requireContext())

            sortAdapter = SortAdapter(this@HomeFragment)
            rvSort.adapter = sortAdapter
            rvSort.layoutManager = LinearLayoutManager(requireContext())

            viewLifecycleOwner.lifecycleScope.launch {
                settingViewModel.proVersion.collect {
                    if (it) {
                        Utils.setGradientText(tvPro)
                        tvTrial.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                        tvPro.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_black)
                    } else {
                        Utils.setGradientText(tvTrial)
                        tvTrial.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_black)
                        tvPro.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.projects.collect {
                    adapter.submitData(it)
                    if (it.isNotEmpty()) {
                        ivProject.visibility = View.INVISIBLE
                        tvNoProject.visibility = View.INVISIBLE
                    } else {
                        ivProject.visibility = View.VISIBLE
                        tvNoProject.visibility = View.VISIBLE
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                homeViewModel.sorts.collect {
                    sortAdapter.submitData(it)
                }
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            ivSearch.setOnClickListener {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                startActivity(intent)
            }

            clUserMode.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fcv_main2, Upgrade2Fragment.newInstance(NORMAL))
                    .addToBackStack(null)
                    .commit()
            }

            ivFilter.setOnClickListener {
                clFilter.visibility =
                    if (clFilter.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
                vOverlay.visibility =
                    if (clFilter.visibility == View.VISIBLE) View.VISIBLE else View.GONE
            }

            vOverlay.setOnClickListener {
                clFilter.visibility = View.INVISIBLE
                vOverlay.visibility = View.GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onClick(item: Project) {
        val intent = Intent(requireContext(), TTSActivity::class.java)
        intent.putExtra(MODE, item.mode)
        intent.putExtra(LANGUAGE, item.language)
        intent.putExtra(VOICE, item.voice)
        intent.putExtra(GENDER, item.gender)
        intent.putExtra(DISPLAY_INDEX, item.displayIndex)
        intent.putExtra(PITCH, item.pitch)
        intent.putExtra(SPEED, item.speed)
        intent.putExtra(VOLUME, item.volume)
        intent.putExtra(ID, item.id)
        intent.putExtra(CONTENT, item.content)
        intent.putExtra(FILE_PATH, item.filePath)
        startActivity(intent)
    }

    override fun onClickMore(item: Project) {
        val moreBottomSheet = MoreBottomSheet.newInstance(item)
        moreBottomSheet.show(childFragmentManager, MORE_BOTTOM_SHEET)
    }

    override fun onClick(item: Sort) {
        homeViewModel.setSortType(item.subtitle)
        binding.clFilter.visibility = View.INVISIBLE
    }
}