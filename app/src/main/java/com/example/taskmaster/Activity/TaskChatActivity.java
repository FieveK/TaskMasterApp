package com.example.taskmaster.Activity;

import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmaster.Adapter.ChatAdapter;
import com.example.taskmaster.Model.ChatMessage;
import com.example.taskmaster.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class TaskChatActivity extends AppCompatActivity {
    private String userId, taskId;
    private FirebaseFirestore db;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_chat);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        taskId = getIntent().getStringExtra("taskId");
        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = findViewById(R.id.chat_recycler_view);
        EditText messageInput = findViewById(R.id.edit_chat_message);
        Button sendButton = findViewById(R.id.button_send);

        chatAdapter = new ChatAdapter(chatMessages, userId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        messageInput.requestFocus();

        messageInput.postDelayed(() -> {
            messageInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(messageInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);


        loadMessages();

        sendButton.setOnClickListener(v -> {
            String msg = messageInput.getText().toString().trim();
            if (!msg.isEmpty()) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(doc -> {
                            String senderName = doc.getString("name"); // pastikan field 'name' ada di koleksi users
                            ChatMessage chat = new ChatMessage(userId, senderName, msg, System.currentTimeMillis());

                            db.collection("tasks")
                                    .document(taskId)
                                    .collection("chats")
                                    .add(chat);
                            messageInput.setText("");
                        });
            }
        });

    }

    private void loadMessages() {
        db.collection("tasks")
                .document(taskId)
                .collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (snapshots != null) {
                        chatMessages.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            chatMessages.add(msg);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }
                });

    }
}
