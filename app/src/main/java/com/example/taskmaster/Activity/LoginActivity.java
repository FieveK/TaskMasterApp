package com.example.taskmaster.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmaster.R;
import com.example.taskmaster.Fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_fragment_container, new LoginFragment())
                    .commit();
        }
    }
}
