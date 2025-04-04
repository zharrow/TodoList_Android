package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.todolist.databinding.ActivityRegisterBinding;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialisation du ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Configuration des boutons
        setupButtons();

        // Configuration des observateurs LiveData
        setupObservers();
    }

    private void setupButtons() {
        // Bouton de retour
        binding.btnBack.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            onBackPressed();
        });

        // Bouton d'inscription
        binding.btnRegister.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            attemptRegister();
        });

        // Bouton de connexion
        binding.btnLogin.setOnClickListener(v -> {
            AnimationUtils.applyClickAnimation(v);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void setupObservers() {
        // Observer pour l'état de chargement
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnRegister.setText(isLoading ? "" : getString(R.string.create_account));
            binding.btnRegister.setEnabled(!isLoading);
            binding.btnLogin.setEnabled(!isLoading);
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

    private void attemptRegister() {
        // Récupération des données du formulaire
        String username = binding.etUsername.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        // Validation des champs
        if (TextUtils.isEmpty(username)) {
            binding.tilUsername.setError(getString(R.string.error_empty_name));
            return;
        } else {
            binding.tilUsername.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.setError(getString(R.string.error_empty_email));
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_invalid_email));
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilPassword.setError(getString(R.string.error_empty_password));
            return;
        } else if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.error_short_password));
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_empty_password));
            return;
        } else if (!password.equals(confirmPassword)) {
            binding.tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            return;
        } else {
            binding.tilConfirmPassword.setError(null);
        }

        // Tentative d'inscription
        viewModel.register(username, email, password, confirmPassword);
    }

    private void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
