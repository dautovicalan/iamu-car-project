package com.alan.alancars.ui.home

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alan.alancars.CHANNEL_ID
import com.alan.alancars.R
import com.alan.alancars.adapter.CarAdapter
import com.alan.alancars.databinding.FragmentHomeBinding
import com.alan.alancars.framework.fetchItems
import com.alan.alancars.model.Car
import com.alan.alancars.notificationId
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var cars: MutableList<Car>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cars = requireContext().fetchItems()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rlCars.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CarAdapter(requireContext(), cars)
        }
        binding.fabAddCar.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_add_new_car)
        }

        binding.btnRandomNotification.setOnClickListener {
            sendNotification()
        }
    }

    override fun onResume() {
        super.onResume()
        cars = requireContext().fetchItems()
        binding.rlCars.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CarAdapter(requireContext(), cars)
        }
    }

    private fun sendNotification(){
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.mercedes)
            .setContentTitle("Your daily random Notification")
            .setContentText("Always be good")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(requireContext())){
            notify(notificationId, builder.build())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}