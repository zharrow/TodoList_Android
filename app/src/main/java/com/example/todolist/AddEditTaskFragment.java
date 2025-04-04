package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.todolist.databinding.FragmentAddEditTaskBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditTaskFragment extends Fragment {

    private FragmentAddEditTaskBinding binding;
    private TaskViewModel taskViewModel;
    private CategoryViewModel categoryViewModel;
    private long taskId = -1;
    private long selectedCategoryId = -1;
    private Date selectedDueDate;
    private List<Category> categories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupérer les arguments
        if (getArguments() != null) {
            taskId = getArguments().getLong("taskId", -1);
            selectedCategoryId = getArguments().getLong("categoryId", -1);
        }

        // Initialiser les ViewModels
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Configurer l'interface utilisateur
        setupUI();

        // Charger les données si modification
        if (taskId != -1) {
            loadTaskData();
            binding.tvAddEditTitle.setText(R.string.edit_task);
        } else {
            // Initialiser avec la date d'aujourd'hui
            selectedDueDate = new Date();
            updateDateTimeDisplay();

            // Si une catégorie est sélectionnée, la définir
            if (selectedCategoryId != -1) {
                setCategoryFromId(selectedCategoryId);
            }
        }

        // Charger les catégories
        loadCategories();
    }

    private void setupUI() {
        // Configurer les boutons
        binding.btnClose.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            navigateBack();
        });

        binding.btnSave.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            saveTask();
        });

        // Configurer le sélecteur de catégorie
        binding.cardCategory.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showCategoryDialog();
        });

        // Configurer le sélecteur de date
        binding.cardDueDate.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showDateTimePicker();
        });

        // Configurer les observateurs
        taskViewModel.getTaskSaved().observe(getViewLifecycleOwner(), saved -> {
            if (saved) {
                taskViewModel.resetTaskSaved();
                Toast.makeText(requireContext(),
                        taskId == -1 ? R.string.task_added : R.string.task_updated,
                        Toast.LENGTH_SHORT).show();
                navigateBack();
            }
        });

        taskViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTaskData() {
        taskViewModel.getTaskById(taskId).observe(getViewLifecycleOwner(), task -> {
            if (task != null) {
                binding.etTaskTitle.setText(task.getTitle());
                binding.etTaskDescription.setText(task.getDescription());

                // Priorité
                switch (task.getPriority()) {
                    case LOW:
                        binding.rbLow.setChecked(true);
                        break;
                    case HIGH:
                        binding.rbHigh.setChecked(true);
                        break;
                    case URGENT:
                        binding.rbUrgent.setChecked(true);
                        break;
                    default:
                        binding.rbMedium.setChecked(true);
                        break;
                }

                // Date d'échéance
                selectedDueDate = DateUtils.parseDateTime(task.getDueDate());
                updateDateTimeDisplay();

                // Catégorie
                selectedCategoryId = task.getCategoryId();
                setCategoryFromId(selectedCategoryId);
            }
        });
    }

    private void loadCategories() {
        categoryViewModel.getAllCategories().observe(getViewLifecycleOwner(), categoryList -> {
            if (categoryList != null) {
                categories = categoryList;

                // Si aucune catégorie n'est sélectionnée et que la liste n'est pas vide, sélectionner la première
                if (selectedCategoryId == -1 && !categories.isEmpty()) {
                    selectedCategoryId = categories.get(0).getId();
                    setCategoryFromId(selectedCategoryId);
                }
            }
        });
    }

    private void showCategoryDialog() {
        if (categories.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_categories, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] categoryNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryNames[i] = categories.get(i).getName();
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.select_category)
                .setItems(categoryNames, (dialog, which) -> {
                    selectedCategoryId = categories.get(which).getId();
                    setCategoryFromId(selectedCategoryId);
                })
                .show();
    }

    private void setCategoryFromId(long categoryId) {
        categoryViewModel.getCategoryById(categoryId).observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                binding.tvSelectedCategory.setText(category.getName());

                // TODO: Définir l'icône de la catégorie
                // binding.ivCategoryIcon.setImageResource(getIconResourceId(category.getIcon()));

                // Pour le moment, utilisons une icône générique
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_category);

                // Définir la couleur de l'icône
                ViewExtensions.setImageViewTint(binding.ivCategoryIcon, getCategoryColorId(category.getColor()));
            }
        });
    }

    private int getCategoryColorId(String colorName) {
        // Par défaut
        if (colorName == null || colorName.isEmpty()) {
            return R.color.category_default;
        }

        if ("work".equals(colorName)) {
            return R.color.category_work;
        } else if ("personal".equals(colorName)) {
            return R.color.category_personal;
        } else if ("shopping".equals(colorName)) {
            return R.color.category_shopping;
        } else if ("health".equals(colorName)) {
            return R.color.category_health;
        } else if ("education".equals(colorName)) {
            return R.color.category_education;
        } else if ("finance".equals(colorName)) {
            return R.color.category_finance;
        } else if ("social".equals(colorName)) {
            return R.color.category_social;
        } else {
            return R.color.category_default;
        }
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDueDate != null) {
            calendar.setTime(selectedDueDate);
        }

        // Date Picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Time Picker après avoir sélectionné la date
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                selectedDueDate = calendar.getTime();
                                updateDateTimeDisplay();
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        if (selectedDueDate != null) {
            binding.tvSelectedDate.setText(DateUtils.formatDisplayDate(selectedDueDate));
            binding.tvSelectedTime.setText(DateUtils.formatDisplayTime(selectedDueDate));
        }
    }

    private void saveTask() {
        String title = binding.etTaskTitle.getText().toString().trim();
        String description = binding.etTaskDescription.getText().toString().trim();

        // Vérifier les données obligatoires
        if (title.isEmpty()) {
            binding.tilTaskTitle.setError(getString(R.string.error_empty_title));
            return;
        } else {
            binding.tilTaskTitle.setError(null);
        }

        if (selectedCategoryId == -1) {
            Toast.makeText(requireContext(), R.string.select_category, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDueDate == null) {
            Toast.makeText(requireContext(), R.string.due_date, Toast.LENGTH_SHORT).show();
            return;
        }

        // Déterminer la priorité
        Priority priority = Priority.MEDIUM;
        if (binding.rbLow.isChecked()) {
            priority = Priority.LOW;
        } else if (binding.rbHigh.isChecked()) {
            priority = Priority.HIGH;
        } else if (binding.rbUrgent.isChecked()) {
            priority = Priority.URGENT;
        }

        String dueDateTime = DateUtils.formatDateTime(selectedDueDate);

        if (taskId == -1) {
            // Créer une nouvelle tâche
            taskViewModel.saveTask(title, description, dueDateTime, priority, selectedCategoryId);
        } else {
            // Mettre à jour une tâche existante
            Task task = new Task(title, description, dueDateTime, priority, selectedCategoryId,
                    SharedPreferencesManager.getUserId(requireContext()));
            task.setId(taskId);
            task.setUpdatedAt(System.currentTimeMillis() + "");
            taskViewModel.updateTask(task);
        }
    }

    private void navigateBack() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}