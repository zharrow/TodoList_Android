package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Configuration des boutons
        setupButtons();

        // Configuration des observateurs LiveData
        setupObservers();
    }

    private void setupButtons() {
        // Bouton de connexion
        binding.btnLogin.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            attemptLogin();
        });

        // Bouton d'inscription
        binding.btnRegister.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            navigateToRegister();
        });
    }

    private void setupObservers() {
        // Observer pour l'état de chargement
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setText(isLoading ? "" : getString(R.string.login));
            binding.btnLogin.setEnabled(!isLoading);
            binding.btnRegister.setEnabled(!isLoading);
        });

        // Observer pour les messages d'erreur
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.tvError.setText(errorMessage);
                binding.tvError.setVisibility(View.VISIBLE);
                AnimationUtils.fadeIn(binding.tvError, 300);
                viewModel.clearErrorMessage();
            } else {
                binding.tvError.setVisibility(View.GONE);
            }
        });

        // Observer pour l'état de connexion
        viewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                navigateToMain();
            }
        });
    }

    private void attemptLogin() {
        // Récupération des données du formulaire
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validation des champs
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_empty_email));
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_empty_password));
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        // Tentative de connexion
        viewModel.login(email, password);
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}