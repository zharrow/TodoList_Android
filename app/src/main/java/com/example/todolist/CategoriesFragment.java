package com.example.todolist;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.databinding.DialogAddEditCategoryBinding;
import com.example.todolist.databinding.FragmentCategoriesBinding;
import com.example.todolist.databinding.ItemCategoryBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;

    // Mapping des couleurs et icônes pour simplifier la récupération
    private final Map<Integer, String> colorMapping = new HashMap<>();
    private final Map<Integer, String> iconMapping = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser le ViewModel
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Initialiser les mappings
        initMappings();

        // Configurer la RecyclerView
        setupRecyclerView();

        // Configurer le FAB
        binding.fabAddCategory.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showAddCategoryDialog();
        });

        // Observer les changements de données
        observeViewModel();

        // Vérifier s'il faut afficher le dialogue d'ajout de catégorie (via navigation)
        if (getArguments() != null && getArguments().getBoolean("showAddCategoryDialog", false)) {
            showAddCategoryDialog();
        }
    }

    private void initMappings() {
        // Couleurs
        colorMapping.put(R.id.chip_color_default, "default");
        colorMapping.put(R.id.chip_color_work, "work");
        colorMapping.put(R.id.chip_color_personal, "personal");
        colorMapping.put(R.id.chip_color_shopping, "shopping");
        colorMapping.put(R.id.chip_color_health, "health");

        // Icônes
        iconMapping.put(R.id.icon_category, "category");
        iconMapping.put(R.id.icon_work, "work");
        iconMapping.put(R.id.icon_home, "home");
        iconMapping.put(R.id.icon_shopping, "shopping");
        iconMapping.put(R.id.icon_health, "health");
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(
                category -> navigateToCategoryTasks(category.getId()),
                this::showDeleteCategoryDialog
        );
        binding.rvCategories.setAdapter(adapter);
        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Ajouter animation de layout
        AnimationUtils.setRecyclerViewAnimation(binding.rvCategories);
    }

    private void observeViewModel() {
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                adapter.submitList(categories);
                binding.rvCategories.setVisibility(View.VISIBLE);
                binding.tvNoCategories.setVisibility(View.GONE);
            } else {
                binding.rvCategories.setVisibility(View.GONE);
                binding.tvNoCategories.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCategorySaved().observe(getViewLifecycleOwner(), saved -> {
            if (saved) {
                viewModel.resetCategorySaved();
                Toast.makeText(requireContext(), R.string.category_added, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getCategoryDeleted().observe(getViewLifecycleOwner(), deleted -> {
            if (deleted) {
                viewModel.resetCategoryDeleted();
                Toast.makeText(requireContext(), R.string.category_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                viewModel.clearErrorMessage();
            }
        });
    }

    private void showAddCategoryDialog() {
        // Initialisation de la vue du dialogue
        DialogAddEditCategoryBinding dialogBinding = DialogAddEditCategoryBinding.inflate(LayoutInflater.from(requireContext()));

        // Personnalisation des couleurs des boutons radio
        setupColorSelectors(dialogBinding.colorChips);

        // Personnalisation des icônes des boutons radio
        setupIconSelectors(dialogBinding.iconGroup);

        // Création du dialogue
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .create();

        // Configuration des boutons du dialogue
        dialogBinding.btnCancel.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            dialog.dismiss();
        });

        dialogBinding.btnSaveCategory.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);

            // Récupérer les valeurs
            String name = dialogBinding.etCategoryName.getText().toString().trim();

            // Vérification du nom
            if (name.isEmpty()) {
                dialogBinding.tilCategoryName.setError(getString(R.string.error_empty_name));
                return;
            }

            // Récupérer la couleur sélectionnée
            String color = "default";
            int selectedColorId = dialogBinding.colorChips.getCheckedRadioButtonId();
            if (colorMapping.containsKey(selectedColorId)) {
                color = colorMapping.get(selectedColorId);
            }

            // Récupérer l'icône sélectionnée
            String icon = "category";
            int selectedIconId = dialogBinding.iconGroup.getCheckedRadioButtonId();
            if (iconMapping.containsKey(selectedIconId)) {
                icon = iconMapping.get(selectedIconId);
            }

            // Sauvegarder la catégorie
            viewModel.saveCategory(name, color, icon);
            dialog.dismiss();
        });

        // Afficher le dialogue
        dialog.show();
    }

    private void setupColorSelectors(RadioGroup colorGroup) {
        // Appliquer les couleurs spécifiques à chaque bouton radio
        applyColorToRadioButton((RadioButton) colorGroup.findViewById(R.id.chip_color_default),
                R.color.category_default);
        applyColorToRadioButton((RadioButton) colorGroup.findViewById(R.id.chip_color_work),
                R.color.category_work);
        applyColorToRadioButton((RadioButton) colorGroup.findViewById(R.id.chip_color_personal),
                R.color.category_personal);
        applyColorToRadioButton((RadioButton) colorGroup.findViewById(R.id.chip_color_shopping),
                R.color.category_shopping);
        applyColorToRadioButton((RadioButton) colorGroup.findViewById(R.id.chip_color_health),
                R.color.category_health);
    }

    private void applyColorToRadioButton(RadioButton button, int colorResId) {
        if (button != null) {
            button.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), colorResId)));
        }
    }

    private void setupIconSelectors(RadioGroup iconGroup) {
        // Configurer les icônes pour chaque bouton
        // Dans une implémentation réelle, vous devriez définir des drawables différents par icône
    }

    private void showDeleteCategoryDialog(Category category) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirmation)
                .setMessage(R.string.delete_category_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> viewModel.deleteCategory(category))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void navigateToCategoryTasks(long categoryId) {
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryId);
        Navigation.findNavController(requireView())
                .navigate(R.id.action_navigation_categories_to_categoryTasksFragment, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Adapter pour les catégories
    private static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        private List<Category> categories = new ArrayList<>();
        private final OnCategoryClickListener clickListener;
        private final OnCategoryLongClickListener longClickListener;

        public interface OnCategoryClickListener {
            void onCategoryClick(Category category);
        }

        public interface OnCategoryLongClickListener {
            void onCategoryLongClick(Category category);
        }

        CategoryAdapter(OnCategoryClickListener clickListener, OnCategoryLongClickListener longClickListener) {
            this.clickListener = clickListener;
            this.longClickListener = longClickListener;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new CategoryViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.bind(category, clickListener, longClickListener);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public void submitList(List<Category> newCategories) {
            this.categories = newCategories;
            notifyDataSetChanged();
        }

        static class CategoryViewHolder extends RecyclerView.ViewHolder {
            private final ItemCategoryBinding binding;

            public CategoryViewHolder(ItemCategoryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(Category category, OnCategoryClickListener clickListener, OnCategoryLongClickListener longClickListener) {
                binding.tvCategoryName.setText(category.getName());

                // Définir la couleur
                int colorRes = getCategoryColorRes(category.getColor());
                binding.categoryColorBg.setBackgroundColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), colorRes));

                // Définir l'icône
                int iconRes = getIconResourceForCategory(category.getIcon());
                binding.ivCategoryIcon.setImageResource(iconRes);

                // Définir le nombre de tâches (à implémenter)
                // TODO: Implémenter le comptage des tâches par catégorie
                binding.tvTaskCount.setText(R.string.no_tasks_in_category);

                // Définir les actions
                binding.getRoot().setOnClickListener(v -> clickListener.onCategoryClick(category));
                binding.getRoot().setOnLongClickListener(v -> {
                    longClickListener.onCategoryLongClick(category);
                    return true;
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

            private int getIconResourceForCategory(String icon) {
                if (icon == null || icon.isEmpty()) {
                    return R.drawable.ic_category;
                }

                if ("work".equals(icon)) {
                    return R.drawable.ic_work;
                } else if ("home".equals(icon)) {
                    return R.drawable.ic_home;
                } else if ("shopping".equals(icon)) {
                    return R.drawable.ic_category; // Remplacer par une icône de shopping
                } else if ("health".equals(icon)) {
                    return R.drawable.ic_category; // Remplacer par une icône de santé
                } else {
                    return R.drawable.ic_category;
                }
            }
        }
    }
}