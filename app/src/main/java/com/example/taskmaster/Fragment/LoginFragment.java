package com.example.taskmaster.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.taskmaster.R;
import com.example.taskmaster.Activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        Button loginBtn = view.findViewById(R.id.loginButton);
        TextView registerLink = view.findViewById(R.id.goToRegister);
        progressBar = view.findViewById(R.id.progressBar);  // Gunakan dari layout
        progressBar.setVisibility(View.GONE);

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Validasi email
            if (email.isEmpty()) {
                emailInput.setError("Email tidak boleh kosong");
                emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Format email tidak valid");
                emailInput.requestFocus();
                return;
            }

            // Validasi password
            if (password.isEmpty()) {
                passwordInput.setError("Password tidak boleh kosong");
                passwordInput.requestFocus();
                return;
            }

            // Tampilkan loading dan nonaktifkan tombol
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // Sembunyikan loading dan aktifkan tombol
                        progressBar.setVisibility(View.GONE);
                        loginBtn.setEnabled(true);

                        if (task.isSuccessful()) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            requireActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        registerLink.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.login_fragment_container, new RegisterFragment())
                        .commit()
        );

        return view;
    }
}
