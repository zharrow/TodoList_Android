package com.example.todolist;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private FragmentCategoriesBinding binding;
    private CategoryViewModel viewModel;
    private CategoryAdapter adapter;

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
            String color = "default"; // Par défaut
            for (int i = 0; i < dialogBinding.colorChips.getChildCount(); i++) {
                View child = dialogBinding.colorChips.getChildAt(i);
                if (child instanceof Chip && ((Chip) child).isChecked()) {
                    int childId = child.getId();
                    // Extraire la couleur à partir de l'ID du chip
                    if (childId == R.id.chip_color_work) {
                        color = "work";
                    } else if (childId == R.id.chip_color_personal) {
                        color = "personal";
                    } else if (childId == R.id.chip_color_shopping) {
                        color = "shopping";
                    } else if (childId == R.id.chip_color_health) {
                        color = "health";
                    } else if (childId == R.id.chip_color_education) {
                        color = "education";
                    } else if (childId == R.id.chip_color_finance) {
                        color = "finance";
                    } else if (childId == R.id.chip_color_social) {
                        color = "social";
                    } else {
                        color = "default";
                    }
                    break;
                }
            }

            // TODO: Implémenter la sélection d'icône
            // Pour le moment, utilisons une icône par défaut
            String icon = "category";

            // Sauvegarder la catégorie
            viewModel.saveCategory(name, color, icon);
            dialog.dismiss();
        });

        // Afficher le dialogue
        dialog.show();
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
                // Pour le moment, utilisons une icône générique
                binding.ivCategoryIcon.setImageResource(R.drawable.ic_category);

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
        }
    }
}