package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AuthViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void login(String email, String password) {
        isLoading.setValue(true);

        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Veuillez remplir tous les champs");
            isLoading.setValue(false);
            return;
        }

        LiveData<User> userLiveData = userRepository.login(email, password);
        userLiveData.observeForever(user -> {
            isLoading.setValue(false);
            if (user != null) {
                // Sauvegarder l'ID de l'utilisateur connecté
                SharedPreferencesManager.saveUserId(getApplication(), user.getId());
                isLoggedIn.setValue(true);
            } else {
                errorMessage.setValue("Email ou mot de passe incorrect");
            }
        });
    }

    public void register(String username, String email, String password, String confirmPassword) {
        isLoading.setValue(true);

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.setValue("Veuillez remplir tous les champs");
            isLoading.setValue(false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Les mots de passe ne correspondent pas");
            isLoading.setValue(false);
            return;
        }

        LiveData<User> existingUser = userRepository.getUserByEmail(email);
        existingUser.observeForever(user -> {
            if (user != null) {
                errorMessage.setValue("Cet email est déjà utilisé");
                isLoading.setValue(false);
            } else {
                User newUser = new User(username, email, password);
                long userId = userRepository.insert(newUser);

                if (userId > 0) {
                    // Sauvegarder l'ID de l'utilisateur connecté
                    SharedPreferencesManager.saveUserId(getApplication(), userId);
                    isLoggedIn.setValue(true);
                } else {
                    errorMessage.setValue("Erreur lors de l'inscription");
                }
                isLoading.setValue(false);
            }
        });
    }

    public void logout() {
        SharedPreferencesManager.clearUserId(getApplication());
        isLoggedIn.setValue(false);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}
