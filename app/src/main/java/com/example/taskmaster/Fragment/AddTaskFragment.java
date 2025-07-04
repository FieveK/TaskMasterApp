package com.example.taskmaster.Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.taskmaster.Model.Task;
import com.example.taskmaster.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class AddTaskFragment extends Fragment {
    private EditText titleInput, descInput, deadlineInput, locationInput;
    private Button addTaskButton, photoButton;
    private ImageView previewImage;

    private String photoUrl = ""; // Placeholder, bisa diganti jika menyimpan ke storage
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_CODE = 101;

    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);

        // Inisialisasi UI
        titleInput = view.findViewById(R.id.input_title);
        descInput = view.findViewById(R.id.input_description);
        deadlineInput = view.findViewById(R.id.input_deadline);
        locationInput = view.findViewById(R.id.input_location);
        addTaskButton = view.findViewById(R.id.btn_add_task);
        photoButton = view.findViewById(R.id.btn_take_photo);
        previewImage = view.findViewById(R.id.image_preview);

        // Inisialisasi lokasi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Tanggal deadline
        deadlineInput.setOnClickListener(v -> showDatePicker());

        // Ambil lokasi saat klik
        locationInput.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else {
                getLastLocation();
            }
        });

        // Ambil foto
        photoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        // Tambah ke Firestore
        addTaskButton.setOnClickListener(v -> addTaskToFirestore());

        return view;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, y, m, d) ->
                deadlineInput.setText(d + "/" + (m + 1) + "/" + y), year, month, day);
        dialog.show();
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String fullAddress = addresses.get(0).getAddressLine(0);
                                locationInput.setText(fullAddress);
                            } else {
                                locationInput.setText(latitude + ", " + longitude);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            locationInput.setText(latitude + ", " + longitude);
                            Toast.makeText(getContext(), "Gagal membaca alamat", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show());
    }


    private void addTaskToFirestore() {
        String id = UUID.randomUUID().toString();
        String title = titleInput.getText().toString();
        String desc = descInput.getText().toString();
        String deadline = deadlineInput.getText().toString();
        String location = locationInput.getText().toString();

        if (title.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan Deadline wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("id", id);
        taskMap.put("title", title);
        taskMap.put("description", desc);
        taskMap.put("deadline", deadline);
        taskMap.put("location", location);
        taskMap.put("photoUrl", photoUrl);
        taskMap.put("participants", Arrays.asList(userId));

        FirebaseFirestore.getInstance()
                .collection("tasks")
                .document(id) // pakai ID yang Anda generate
                .set(taskMap) // <-- pakai .set() untuk menulis data baru
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menambah tugas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }


    private void clearForm() {
        titleInput.setText("");
        descInput.setText("");
        deadlineInput.setText("");
        locationInput.setText("");
        previewImage.setImageResource(0); // Reset gambar
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                previewImage.setImageBitmap(imageBitmap);
                previewImage.setVisibility(View.VISIBLE); // ← Tambahkan baris ini!
            }
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(getContext(), "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
