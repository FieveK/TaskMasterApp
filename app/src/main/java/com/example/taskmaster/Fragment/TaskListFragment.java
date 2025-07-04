package com.example.taskmaster.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Activity.TaskChatActivity;
import com.example.taskmaster.Activity.TaskDetailActivity;
import com.example.taskmaster.Adapter.TaskAdapter;
import com.example.taskmaster.Model.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.Utils.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(taskList, task -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Pilih Aksi")
                    .setItems(new CharSequence[]{"Lihat Detail Tugas", "Buka Chat Tugas"}, (dialog, which) -> {
                        if (which == 0) {
                            // Lihat Detail
                            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                            intent.putExtra("title", task.getTitle());
                            intent.putExtra("description", task.getDescription());
                            intent.putExtra("deadline", task.getDeadline());
                            intent.putExtra("location", task.getLocation());
                            startActivity(intent);

                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseFirestore.getInstance()
                                    .collection("tasks")
                                    .document(task.getId())
                                    .update("participants", FieldValue.arrayUnion(userId));

                        } else if (which == 1) {
                            // Buka Chat
                            Intent chatIntent = new Intent(getContext(), TaskChatActivity.class);
                            chatIntent.putExtra("taskId", task.getId());
                            startActivity(chatIntent);
                        }
                    });
            builder.show();
        });



        recyclerView.setAdapter(taskAdapter);
        loadTasksFromFirestore();
        return view;
    }

    private void loadTasksFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    taskList.clear();
                    int notifyId = 100;
                    String today = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        today = LocalDate.now().toString();
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Task task = new Task();
                        task.setId(doc.getId());
                        task.setTitle(doc.getString("title") != null ? doc.getString("title") : "");
                        task.setDescription(doc.getString("description") != null ? doc.getString("description") : "");
                        task.setDeadline(doc.getString("deadline") != null ? doc.getString("deadline") : "");
                        task.setLocation(doc.getString("location") != null ? doc.getString("location") : "");
                        task.setPhotoUrl(doc.getString("photoUrl") != null ? doc.getString("photoUrl") : "");
                        task.setParticipants((List<String>) doc.get("participants"));

                        taskList.add(task);

                        if (today != null && today.equals(task.getDeadline())) {
                            NotificationHelper.showNotification(
                                    getContext(),
                                    "Deadline Tugas",
                                    "Hari ini adalah tenggat: " + task.getTitle(),
                                    notifyId++);
                        }
                    }

                    taskAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal memuat tugas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
