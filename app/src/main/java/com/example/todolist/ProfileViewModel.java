package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> profileUpdated = new MutableLiveData<>(false);
    private final long currentUserId;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        currentUserId = SharedPreferencesManager.getUserId(application);
    }

    public LiveData<User> getCurrentUser() {
        return userRepository.getUserById(currentUserId);
    }

    public void updateProfile(User user) {
        isLoading.setValue(true);

        if (user.getUsername().isEmpty() || user.getEmail().isEmpty()) {
            errorMessage.setValue("Le nom d'utilisateur et l'email sont obligatoires");
            isLoading.setValue(false);
            return;
        }

        userRepository.update(user);
        profileUpdated.setValue(true);
        isLoading.setValue(false);
    }

    public void updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        isLoading.setValue(true);

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.setValue("Veuillez remplir tous les champs");
            isLoading.setValue(false);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setValue("Les nouveaux mots de passe ne correspondent pas");
            isLoading.setValue(false);
            return;
        }

        LiveData<User> userLiveData = userRepository.getUserById(currentUserId);
        userLiveData.observeForever(user -> {
            if (user != null && user.getPassword().equals(currentPassword)) {
                user.setPassword(newPassword);
                userRepository.update(user);
                profileUpdated.setValue(true);
            } else {
                errorMessage.setValue("Mot de passe actuel incorrect");
            }
            isLoading.setValue(false);
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getProfileUpdated() {
        return profileUpdated;
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }

    public void resetProfileUpdated() {
        profileUpdated.setValue(false);
    }

    public void updateUser(User user) {
        UserRepository userRepository = new UserRepository(getApplication());
        userRepository.update(user);
    }

}
