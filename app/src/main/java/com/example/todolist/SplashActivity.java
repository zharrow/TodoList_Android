package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.databinding.ActivitySplashBinding;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_TIME = 2000; // 2 secondes
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Animation du logo
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        binding.ivLogo.startAnimation(logoAnimation);

        // Animation du nom de l'app
        Animation titleAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        titleAnimation.setStartOffset(500);
        binding.tvAppName.startAnimation(titleAnimation);

        // Animation du slogan
        Animation sloganAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        sloganAnimation.setStartOffset(700);
        binding.tvAppSlogan.startAnimation(sloganAnimation);

        // Délai avant de passer à l'écran suivant
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DISPLAY_TIME);
    }

    private void navigateToNextScreen() {
        // Vérifier si l'utilisateur est connecté
        if (SharedPreferencesManager.isLoggedIn(this)) {
            // Si connecté, aller à MainActivity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Sinon, aller à LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Appliquer une animation de transition
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // Fermer l'activité de splash
        finish();
    }
}