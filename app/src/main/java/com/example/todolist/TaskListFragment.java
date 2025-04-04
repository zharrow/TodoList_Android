package com.example.todolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.databinding.FragmentTaskListBinding;

public class TaskListFragment extends Fragment {
    private FragmentTaskListBinding binding;
    private TaskViewModel viewModel;
    private TaskAdapter taskAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Configuration de la RecyclerView
        setupRecyclerView();

        // Configuration des observateurs
        setupObservers();
    }

    private void setupRecyclerView() {
        // Initialiser l'adaptateur
        taskAdapter = new TaskAdapter(
                task -> {
                    // Navigation vers les détails de la tâche
                    Bundle bundle = new Bundle();
                    bundle.putLong("taskId", task.getId());
                    Navigation.findNavController(requireView()).navigate(R.id.action_navigation_tasks_to_taskDetailFragment, bundle);
                },
                task -> {
                    // Basculer l'état de complétion de la tâche
                    viewModel.toggleTaskCompletion(task);
                }
        );

        // Configurer la RecyclerView
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTasks.setAdapter(taskAdapter);

        // Appliquer l'animation de la RecyclerView
        AnimationUtils.setRecyclerViewAnimation(binding.rvTasks);
    }

    private void setupObservers() {
        // Observer les tâches
        viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null && !tasks.isEmpty()) {
                taskAdapter.submitList(tasks);
                binding.rvTasks.setVisibility(View.VISIBLE);
                binding.tvNoTasks.setVisibility(View.GONE);
            } else {
                binding.rvTasks.setVisibility(View.GONE);
                binding.tvNoTasks.setVisibility(View.VISIBLE);
            }
        });

        // Configurer le filtrage et le tri
        setupFilterAndSort();
    }

    private void setupFilterAndSort() {
        // TODO: Implémenter le filtrage et le tri des tâches
        // Exemple de mise en place de filtres
        binding.chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_filter_pending) {
                viewModel.getPendingTasks().observe(getViewLifecycleOwner(), tasks -> {
                    taskAdapter.submitList(tasks);
                });
            } else if (checkedId == R.id.chip_filter_completed) {
                viewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
                    taskAdapter.submitList(tasks);
                });
            } else {
                // Toutes les tâches
                viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
                    taskAdapter.submitList(tasks);
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}