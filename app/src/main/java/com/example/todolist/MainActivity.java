package com.example.todolist;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.todolist.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // Configuration de la navigation
        setupNavigation();

        // Configuration du FAB
        setupFab();

        // Assurez-vous que le FAB est visible par défaut
        binding.fab.setVisibility(View.VISIBLE);
    }

    private void setupNavigation() {
        // Obtenir le NavHostFragment et le NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Configure les destinations de niveau supérieur
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home,
                    R.id.navigation_tasks,
                    R.id.navigation_categories,
                    R.id.navigation_profile)
                    .build();

            // Configure la toolbar avec le navController
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            // Configure la bottom navigation avec le navController
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            // Gestion de la visibilité du FAB et de la bottom navigation
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Afficher la bottom navigation uniquement pour les destinations de premier niveau
                if (destination.getId() == R.id.navigation_home ||
                        destination.getId() == R.id.navigation_tasks ||
                        destination.getId() == R.id.navigation_categories ||
                        destination.getId() == R.id.navigation_profile) {
                    showBottomNavigation();
                } else {
                    hideBottomNavigation();
                }

                // Afficher le FAB uniquement sur les écrans appropriés
                if (destination.getId() == R.id.navigation_tasks ||
                        destination.getId() == R.id.navigation_home) {
                    binding.fab.setImageResource(R.drawable.ic_add);
                    binding.fab.setContentDescription(getString(R.string.add_task));
                    showFab();
                } else if (destination.getId() == R.id.navigation_categories) {
                    // Changer l'icône du FAB pour l'écran des catégories
                    binding.fab.setImageResource(R.drawable.ic_add);
                    binding.fab.setContentDescription(getString(R.string.add_category));
                    showFab();
                } else {
                    hideFab();
                }

                // Configurer le titre de la toolbar en fonction de la destination
                if (destination.getId() == R.id.navigation_home) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);
                }
            });
        } else {
            // Gérer le cas où le NavHostFragment n'a pas été trouvé
            throw new IllegalStateException("NavHostFragment not found");
        }
    }

    private void setupFab() {
        binding.fab.setOnClickListener(view -> {
            // Animer le FAB quand il est cliqué
            AnimationUtils.applyClickAnimation(view);

            // Déterminer l'action en fonction de l'écran courant
            int currentDestination = navController.getCurrentDestination().getId();
            if (currentDestination == R.id.navigation_tasks) {
                // Naviguer vers l'écran d'ajout de tâche
                navController.navigate(R.id.action_navigation_tasks_to_addEditTaskFragment);
            } else if (currentDestination == R.id.navigation_home) {
                // Naviguer vers l'écran d'ajout de tâche depuis l'accueil
                navController.navigate(R.id.navigation_tasks);
                navController.navigate(R.id.action_navigation_tasks_to_addEditTaskFragment);
            } else if (currentDestination == R.id.navigation_categories) {
                // Afficher la boîte de dialogue d'ajout de catégorie
                // Sera implémenté dans le fragment de catégories
                // On envoie un événement à travers le NavController pour notifier le fragment
                Bundle bundle = new Bundle();
                bundle.putBoolean("showAddCategoryDialog", true);
                navController.navigate(R.id.navigation_categories, bundle);
            }
        });
    }

    private void showBottomNavigation() {
        if (binding.bottomNavigation.getVisibility() != View.VISIBLE) {
            binding.bottomNavigation.setVisibility(View.VISIBLE);
            AnimationUtils.slideInFromBottom(binding.bottomNavigation, 300);
        }
    }

    private void hideBottomNavigation() {
        if (binding.bottomNavigation.getVisibility() == View.VISIBLE) {
            AnimationUtils.slideOutToBottom(binding.bottomNavigation, 300);
        }
    }

    private void showFab() {
        if (binding.fab.getVisibility() != View.VISIBLE) {
            binding.fab.setVisibility(View.VISIBLE);
            AnimationUtils.showFab(binding.fab);
        }
    }

    private void hideFab() {
        if (binding.fab.getVisibility() == View.VISIBLE) {
            AnimationUtils.hideFab(binding.fab);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}