package com.example.taskmaster.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmaster.Adapter.ChatAdapter;
import com.example.taskmaster.Model.ChatMessage;
import com.example.taskmaster.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList = new ArrayList<>();
    private EditText inputMessage;
    private Button sendButton;

    private FirebaseFirestore db;
    private String currentUserId;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_chat);
        inputMessage = findViewById(R.id.edit_message);
        sendButton = findViewById(R.id.btn_send);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatAdapter = new ChatAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        taskId = getIntent().getStringExtra("taskId");
        if (taskId == null) {
            finish(); // keluar jika tidak ada ID tugas
            return;
        }

        loadMessages();

        sendButton.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(msg)) {
                sendMessage(msg);
                inputMessage.setText("");
            }
        });
    }

    private void loadMessages() {
        db.collection("chats")
                .document(taskId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    messageList.clear();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ChatMessage msg = doc.toObject(ChatMessage.class);
                        if (msg != null) {
                            messageList.add(msg);
                        }
                    }
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                });
    }


    private void sendMessage(String message) {
        // Ambil nama user dari Firestore
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String senderName = documentSnapshot.getString("name");
                    if (senderName == null) senderName = "Pengguna"; // fallback kalau kosong

                    ChatMessage msg = new ChatMessage(currentUserId, senderName, message, System.currentTimeMillis());

                    DocumentReference chatDocRef = db.collection("chats").document(taskId);

                    // Buat dokumen utama jika belum ada
                    chatDocRef.set(new HashMap<String, Object>() {{
                        put("createdAt", FieldValue.serverTimestamp());
                    }}, SetOptions.merge());

                    // Simpan pesan
                    chatDocRef.collection("messages").add(msg);
                });
    }


}
