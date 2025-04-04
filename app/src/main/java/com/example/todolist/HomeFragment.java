package com.example.todolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Configurer l'interface utilisateur et les observateurs
        setupUI();
    }

    private void setupUI() {
        // Observez les données du ViewModel et mettez à jour l'interface utilisateur
        viewModel.getTotalTasksCount().observe(getViewLifecycleOwner(), totalTasks -> {
            binding.tvTotalTasks.setText(String.valueOf(totalTasks));
        });

        viewModel.getCompletedTasksCount().observe(getViewLifecycleOwner(), completedTasks -> {
            binding.tvCompletedTasks.setText(String.valueOf(completedTasks));
        });

        viewModel.getTodayTasks().observe(getViewLifecycleOwner(), todayTasks -> {
            // Gérer l'affichage des tâches du jour
            if (todayTasks != null && !todayTasks.isEmpty()) {
                binding.tvNoTodayTasks.setVisibility(View.GONE);
                // Configurer l'adaptateur pour la RecyclerView des tâches du jour
                // TODO: Implémenter l'adaptateur pour les tâches du jour
            } else {
                binding.tvNoTodayTasks.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getUpcomingTasks().observe(getViewLifecycleOwner(), upcomingTasks -> {
            // Gérer l'affichage des tâches à venir
            if (upcomingTasks != null && !upcomingTasks.isEmpty()) {
                binding.tvNoUpcomingTasks.setVisibility(View.GONE);
                // Configurer l'adaptateur pour la RecyclerView des tâches à venir
                // TODO: Implémenter l'adaptateur pour les tâches à venir
            } else {
                binding.tvNoUpcomingTasks.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getOverdueTasks().observe(getViewLifecycleOwner(), overdueTasks -> {
            // Gérer l'affichage des tâches en retard
            if (overdueTasks != null && !overdueTasks.isEmpty()) {
                binding.tvNoOverdueTasks.setVisibility(View.GONE);
                // Configurer l'adaptateur pour la RecyclerView des tâches en retard
                // TODO: Implémenter l'adaptateur pour les tâches en retard
            } else {
                binding.tvNoOverdueTasks.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}