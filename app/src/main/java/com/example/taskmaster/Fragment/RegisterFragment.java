package com.example.taskmaster.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.taskmaster.R;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.taskmaster.R.layout.fragment_register, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailInput = view.findViewById(R.id.emailInput);
        passwordInput = view.findViewById(R.id.passwordInput);
        Button registerBtn = view.findViewById(R.id.registerButton);
        TextView loginLink = view.findViewById(R.id.goToLogin);

        registerBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.login_fragment_container, new LoginFragment())
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Pendaftaran gagal", Toast.LENGTH_SHORT).show();
                }
            });
        });

        loginLink.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment_container, new RegisterFragment())
                .addToBackStack(null)
                .commit());

        return view;
    }
}
