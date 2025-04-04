package com.example.todolist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.todolist.databinding.FragmentCategoryTasksBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class CategoryTasksFragment extends Fragment {

    private FragmentCategoryTasksBinding binding;
    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private TaskAdapter taskAdapter;
    private long categoryId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer l'ID de la catégorie des arguments
        if (getArguments() != null) {
            categoryId = getArguments().getLong("categoryId", -1);
        }

        if (categoryId == -1) {
            // Si aucune catégorie n'est fournie, revenir en arrière
            Navigation.findNavController(view).popBackStack();
            return;
        }

        // Initialiser les ViewModels
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Configurer l'interface utilisateur
        setupUI();

        // Charger les données de la catégorie
        loadCategoryData();

        // Charger les tâches initiales (non complétées)
        loadTasks(false);
    }

    private void setupUI() {
        // Configurer le bouton de retour
        binding.btnBack.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            Navigation.findNavController(requireView()).popBackStack();
        });

        // Configurer le bouton d'édition
        binding.btnEditCategory.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            // TODO: Implémenter l'édition de catégorie
            Toast.makeText(requireContext(), "Fonctionnalité à venir", Toast.LENGTH_SHORT).show();
        });

        // Configurer le FAB
        binding.fabAddTask.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            navigateToAddTask();
        });

        // Configurer l'adaptateur pour la liste des tâches
        taskAdapter = new TaskAdapter(
                task -> navigateToTaskDetail(task.getId()),
                task -> toggleTaskCompletion(task),
                (task, view) -> showTaskOptionsMenu(task, view)
        );
        binding.rvCategoryTasks.setAdapter(taskAdapter);

        // Ajouter l'animation à la RecyclerView
        AnimationUtils.setRecyclerViewAnimation(binding.rvCategoryTasks);

        // Configurer les onglets
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadTasks(tab.getPosition() == 1); // Position 1 = Onglet "Terminées"
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Non utilisé
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Non utilisé
            }
        });
    }

    private void loadCategoryData() {
        categoryViewModel.getCategoryById(categoryId).observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                // Mettre à jour le titre
                binding.tvCategoryName.setText(category.getName());

                // Mettre à jour l'icône
                // TODO: Implémenter la gestion des icônes de catégorie
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_category);

                // Définir la couleur en fonction de la catégorie
                binding.appBarLayout.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), getCategoryColorRes(category.getColor()))
                );
            }
        });
    }

    private void loadTasks(boolean completed) {
        taskViewModel.getTasksByCategoryId(categoryId).observe(getViewLifecycleOwner(), tasks -> {
            if (tasks != null) {
                // Filtrer les tâches en fonction de l'état de complétion
                List<Task> filteredTasks = tasks.stream()
                        .filter(task -> task.isCompleted() == completed)
                        .toList();

                if (filteredTasks.isEmpty()) {
                    binding.rvCategoryTasks.setVisibility(View.GONE);
                    binding.tvNoTasks.setVisibility(View.VISIBLE);
                } else {
                    binding.rvCategoryTasks.setVisibility(View.VISIBLE);
                    binding.tvNoTasks.setVisibility(View.GONE);
                    taskAdapter.submitList(filteredTasks);
                }
            }
        });
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

    private void toggleTaskCompletion(Task task) {
        taskViewModel.toggleTaskCompletion(task);

        // Recharger la liste pour refléter le changement
        int selectedTabPosition = binding.tabLayout.getSelectedTabPosition();
        loadTasks(selectedTabPosition == 1);
    }

    private void showTaskOptionsMenu(Task task, View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.task_options_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit_task) {
                navigateToEditTask(task.getId());
                return true;
            } else if (item.getItemId() == R.id.action_delete_task) {
                showDeleteTaskDialog(task);
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showDeleteTaskDialog(Task task) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.delete_task_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    taskViewModel.deleteTask(task);
                    Toast.makeText(requireContext(), R.string.task_deleted, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void navigateToTaskDetail(long taskId) {
        Bundle bundle = new Bundle();
        bundle.putLong("taskId", taskId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_categoryTasksFragment_to_taskDetailFragment, bundle);
    }

    private void navigateToAddTask() {
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_categoryTasksFragment_to_addEditTaskFragment, bundle);
    }

    private void navigateToEditTask(long taskId) {
        Bundle bundle = new Bundle();
        bundle.putLong("taskId", taskId);
        bundle.putLong("categoryId", categoryId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_categoryTasksFragment_to_addEditTaskFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}