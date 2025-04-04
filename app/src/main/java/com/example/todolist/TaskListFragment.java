package com.example.todolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.todolist.databinding.FragmentTaskListBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

        // Configuration des filtres et des tris
        setupFilterAndSort();
    }

    private void setupRecyclerView() {
        // Initialiser l'adaptateur
        taskAdapter = new TaskAdapter(
                task -> navigateToTaskDetail(task),
                task -> toggleTaskCompletion(task),
                (task, view) -> showTaskOptionsMenu(task, view)
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

        // Observer les messages d'erreur
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });

        // Observer les actions sur les tâches
        viewModel.getTaskDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (deleted) {
                viewModel.resetTaskDeleted();
                Toast.makeText(requireContext(), R.string.task_deleted, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterAndSort() {
        // Configurer les chips de filtre
        binding.chipFilterAll.setOnClickListener(v -> {
            viewModel.getAllTasks().observe(getViewLifecycleOwner(), tasks -> {
                taskAdapter.submitList(tasks);
                updateEmptyState(tasks == null || tasks.isEmpty());
            });
        });

        binding.chipFilterPending.setOnClickListener(v -> {
            viewModel.getPendingTasks().observe(getViewLifecycleOwner(), tasks -> {
                taskAdapter.submitList(tasks);
                updateEmptyState(tasks == null || tasks.isEmpty());
            });
        });

        binding.chipFilterCompleted.setOnClickListener(v -> {
            viewModel.getCompletedTasks().observe(getViewLifecycleOwner(), tasks -> {
                taskAdapter.submitList(tasks);
                updateEmptyState(tasks == null || tasks.isEmpty());
            });
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.rvTasks.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.tvNoTasks.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void navigateToTaskDetail(Task task) {
        Bundle bundle = new Bundle();
        bundle.putLong("taskId", task.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_tasks_to_taskDetailFragment, bundle);
    }

    private void toggleTaskCompletion(Task task) {
        viewModel.toggleTaskCompletion(task);
    }

    private void showTaskOptionsMenu(Task task, View anchorView) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), anchorView);
        popupMenu.inflate(R.menu.task_options_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit_task) {
                Bundle bundle = new Bundle();
                bundle.putLong("taskId", task.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_tasks_to_addEditTaskFragment, bundle);
                return true;
            } else if (itemId == R.id.action_delete_task) {
                showDeleteTaskConfirmation(task);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showDeleteTaskConfirmation(Task task) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.delete_task_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    viewModel.deleteTask(task);
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}