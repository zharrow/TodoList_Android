package com.example.todolist;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.todolist.databinding.FragmentTaskDetailBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding binding;
    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private long taskId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer l'ID de la tâche des arguments
        if (getArguments() != null) {
            taskId = getArguments().getLong("taskId", -1);
        }

        if (taskId == -1) {
            // Si aucune tâche n'est fournie, revenir en arrière
            Navigation.findNavController(view).popBackStack();
            return;
        }

        // Initialiser les ViewModels
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Configurer l'interface utilisateur
        setupUI();

        // Charger les données de la tâche
        loadTaskData();
    }

    private void setupUI() {
        // Configurer le bouton de retour
        binding.btnBack.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            Navigation.findNavController(requireView()).popBackStack();
        });

        // Configurer le bouton d'édition
        binding.btnEdit.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            navigateToEditTask();
        });

        // Configurer le bouton de complétion
        binding.btnCompleteTask.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            completeTask();
        });

        // Configurer le bouton de suppression
        binding.btnDeleteTask.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showDeleteTaskDialog();
        });

        // Observer les changements d'état
        taskViewModel.getTaskDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (deleted) {
                taskViewModel.resetTaskDeleted();
                Toast.makeText(requireContext(), R.string.task_deleted, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            }
        });
    }

    private void loadTaskData() {
        taskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                // Mettre à jour les détails de la tâche
                binding.tvTaskTitle.setText(task.getTitle());

                if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                    binding.tvDescription.setText(task.getDescription());
                } else {
                    binding.tvDescriptionLabel.setVisibility(View.GONE);
                    binding.tvDescription.setVisibility(View.GONE);
                }

                // Date d'échéance
                Date dueDate = DateUtils.parseDateTime(task.getDueDate());
                if (dueDate != null) {
                    binding.tvDueDate.setText(DateUtils.formatDisplayDateTime(dueDate));
                }

                // Date de création
                binding.tvCreatedAt.setText(String.format("Créée le %s", DateUtils.formatDisplayDate(new Date(Long.parseLong(task.getCreatedAt())))));

                // Priorité
                setPriorityChip(task.getPriority());

                // État du bouton de complétion
                updateCompleteButton(task.isCompleted());

                // Charger les détails de la catégorie
                loadCategoryData(task.getCategoryId());
            }
        });
    }

    private void loadCategoryData(long categoryId) {
        categoryViewModel.getCategoryById(categoryId).observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                // Mettre à jour le chip de catégorie
                Chip categoryChip = binding.chipCategory;
                categoryChip.setText(category.getName());

                // Définir la couleur du chip en fonction de la catégorie
                int colorRes = getCategoryColorRes(category.getColor());
                categoryChip.setChipBackgroundColor(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), colorRes)));
                categoryChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            }
        });
    }

    private void setPriorityChip(Priority priority) {
        Chip priorityChip = binding.chipPriority;
        priorityChip.setText(ViewExtensions.getPriorityName(priority, binding.getRoot()));

        int colorRes;
        if (priority == Priority.LOW) {
            colorRes = R.color.priority_low;
        } else if (priority == Priority.HIGH) {
            colorRes = R.color.priority_high;
        } else if (priority == Priority.URGENT) {
            colorRes = R.color.priority_urgent;
        } else { // MEDIUM
            colorRes = R.color.priority_medium;
        }

        priorityChip.setChipBackgroundColor(ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), colorRes)));
        priorityChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
    }

    private int getCategoryColorRes(String color) {
        if (color == null || color.isEmpty()) {
            return R.color.category_default;
        }

        if ("work".equals(color)) {
            return R.color.category_work;
        } else if ("personal".equals(color)) {
            return R.color.category_personal;
        } else if ("shopping".equals(color)) {
            return R.color.category_shopping;
        } else if ("health".equals(color)) {
            return R.color.category_health;
        } else if ("education".equals(color)) {
            return R.color.category_education;
        } else if ("finance".equals(color)) {
            return R.color.category_finance;
        } else if ("social".equals(color)) {
            return R.color.category_social;
        } else {
            return R.color.category_default;
        }
    }

    private void updateCompleteButton(boolean isCompleted) {
        if (isCompleted) {
            binding.btnCompleteTask.setText("Marquer comme non terminée");
            binding.btnCompleteTask.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.priority_low)));
        } else {
            binding.btnCompleteTask.setText(R.string.mark_as_complete);
            binding.btnCompleteTask.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.primary)));
        }
    }

    private void completeTask() {
        // Créer une variable pour stocker l'observateur afin de pouvoir le supprimer
        final Observer<Task> taskObserver = new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (task != null) {
                    // Supprimer l'observateur après la première notification
                    taskViewModel.getTaskById(taskId).removeObserver(this);

                    // Inverser l'état de complétion
                    taskViewModel.toggleTaskCompletion(task);
                    updateCompleteButton(!task.isCompleted());

                    // Afficher le bon message
                    if (task.isCompleted()) {
                        Toast.makeText(requireContext(), "Tâche marquée comme non terminée", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), R.string.task_completed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        // Observer une seule fois
        taskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), taskObserver);
    }

    private void showDeleteTaskDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.delete_task_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    taskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), task -> {
                        if (task != null) {
                            taskViewModel.deleteTask(task);
                        }
                    });
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void navigateToEditTask() {
        Bundle bundle = new Bundle();
        bundle.putLong("taskId", taskId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_taskDetailFragment_to_addEditTaskFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}