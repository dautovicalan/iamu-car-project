package com.alan.alancars.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alan.alancars.R
import com.alan.alancars.databinding.FragmentAboutBinding
import com.alan.alancars.framework.applyAnimation

class AboutFragment : Fragment() {
    private var _binding: FragmentAboutBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivAbout.applyAnimation(R.anim.rotate_long)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}