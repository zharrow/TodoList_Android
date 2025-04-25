package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;


import com.example.todolist.databinding.FragmentProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialiser les ViewModels
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Charger les données utilisateur
        loadUserData();

        // Charger les statistiques
        loadStatistics();

        // Configurer les actions de l'interface
        setupClickListeners();
    }

    private void loadUserData() {
        profileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvEmail.setText(user.getEmail());

                // TODO: Charger l'image de profil si disponible
                // Pour le moment, utiliser une icône par défaut
                binding.ivProfile.setImageResource(R.drawable.ic_person);
            }
        });
    }

    private void loadStatistics() {
        // Tâches totales
        homeViewModel.getTotalTasksCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvTotalTasksCount.setText(String.valueOf(count));
        });

        // Tâches terminées
        homeViewModel.getCompletedTasksCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvCompletedTasksCount.setText(String.valueOf(count));
        });

        // Tâches en attente
        homeViewModel.getPendingTasksCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvPendingTasksCount.setText(String.valueOf(count));
        });
    }

    private void setupClickListeners() {
        // Bouton de déconnexion
        binding.btnLogout.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showLogoutConfirmationDialog();
        });

        // Éditer le profil
        binding.btnEditProfile.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showEditProfileDialog();
        });

        // Changer le mot de passe
        binding.btnChangePassword.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showChangePasswordDialog();
        });

        // Notifications
        binding.btnNotifications.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            toggleNotifications();
        });

        // Thème
        binding.btnTheme.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            showThemeSelectionDialog();
        });

        // Charger l'état des notifications
        boolean notificationsEnabled = SharedPreferencesManager.areNotificationsEnabled(requireContext());
        binding.switchNotifications.setChecked(notificationsEnabled);

        // Charger les valeurs actuelles des préférences
        updatePreferencesDisplay();
    }

    private void updatePreferencesDisplay() {
        // Thème
        String currentTheme = SharedPreferencesManager.getTheme(requireContext());
        String themeText;
        switch (currentTheme) {
            case "light":
                themeText = getString(R.string.theme_light);
                break;
            case "dark":
                themeText = getString(R.string.theme_dark);
                break;
            default:
                themeText = getString(R.string.theme_system);
                break;
        }
        binding.tvThemeValue.setText(themeText);

        // Langue
        String currentLanguage = SharedPreferencesManager.getLanguage(requireContext());
        String languageText;
        switch (currentLanguage) {
            case "en":
                languageText = getString(R.string.language_english);
                break;
            case "fr":
                languageText = getString(R.string.language_french);
                break;
            default:
                languageText = getString(R.string.language_system);
                break;
        }
        binding.tvLanguageValue.setText(languageText);
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout)
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    // Se déconnecter
                    // Comme profileViewModel n'a pas de méthode logout(), utilisons le SharedPreferencesManager directement
                    SharedPreferencesManager.clearUserId(requireContext());

                    // Naviguer vers l'écran de connexion
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showEditProfileDialog() {
        profileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null);
            TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
            TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);

            etUsername.setText(user.getUsername());
            etEmail.setText(user.getEmail());

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Modifier le profil")
                    .setView(dialogView)
                    .setPositiveButton("Enregistrer", (dialog, which) -> {
                        String newUsername = etUsername.getText().toString().trim();
                        String newEmail = etEmail.getText().toString().trim();

                        if (!newUsername.isEmpty() && !newEmail.isEmpty()) {
                            user.setUsername(newUsername);
                            user.setEmail(newEmail);
                            profileViewModel.updateUser(user);
                            Toast.makeText(requireContext(), "Profil mis à jour", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Champs requis vides", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Changer le mot de passe")
                .setView(dialogView)
                .setPositiveButton("Changer", (dialog, which) -> {
                    String oldPassword = etOldPassword.getText().toString();
                    String newPassword = etNewPassword.getText().toString();

                    profileViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                        if (user != null && user.getPassword().equals(oldPassword)) {
                            user.setPassword(newPassword);
                            profileViewModel.updateUser(user);
                            Toast.makeText(requireContext(), "Mot de passe mis à jour", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Annuler", null)
                .show();
    }


    private void toggleNotifications() {
        boolean currentState = binding.switchNotifications.isChecked();
        binding.switchNotifications.setChecked(!currentState);
        SharedPreferencesManager.saveNotificationsEnabled(requireContext(), !currentState);
    }

    private void showThemeSelectionDialog() {
        final String[] themes = {
                getString(R.string.theme_system),
                getString(R.string.theme_light),
                getString(R.string.theme_dark)
        };

        final String[] themeValues = {"system", "light", "dark"};

        String currentTheme = SharedPreferencesManager.getTheme(requireContext());
        int selectedIndex = 0;
        for (int i = 0; i < themeValues.length; i++) {
            if (themeValues[i].equals(currentTheme)) {
                selectedIndex = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.theme)
                .setSingleChoiceItems(themes, selectedIndex, (dialog, which) -> {
                    SharedPreferencesManager.saveTheme(requireContext(), themeValues[which]);
                    binding.tvThemeValue.setText(themes[which]);
                    dialog.dismiss();

                    // TODO: Appliquer le nouveau thème
                    Toast.makeText(requireContext(), "Thème mis à jour", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showLanguageSelectionDialog() {
        final String[] languages = {
                getString(R.string.language_system),
                getString(R.string.language_french),
                getString(R.string.language_english)
        };

        final String[] languageValues = {"system", "fr", "en"};

        String currentLanguage = SharedPreferencesManager.getLanguage(requireContext());
        int selectedIndex = 0;
        for (int i = 0; i < languageValues.length; i++) {
            if (languageValues[i].equals(currentLanguage)) {
                selectedIndex = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.language)
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    SharedPreferencesManager.saveLanguage(requireContext(), languageValues[which]);
                    binding.tvLanguageValue.setText(languages[which]);
                    dialog.dismiss();

                    // TODO: Appliquer la nouvelle langue
                    Toast.makeText(requireContext(), "Langue mise à jour", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}