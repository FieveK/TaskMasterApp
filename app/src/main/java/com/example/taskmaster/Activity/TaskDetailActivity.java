package com.example.taskmaster.Activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmaster.R;

public class TaskDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        TextView title = findViewById(R.id.detail_title);
        TextView description = findViewById(R.id.detail_description);
        TextView deadline = findViewById(R.id.detail_deadline);
        TextView location = findViewById(R.id.detail_location);

        // Ambil data dari Intent
        String taskTitle = getIntent().getStringExtra("title");
        String taskDescription = getIntent().getStringExtra("description");
        String taskDeadline = getIntent().getStringExtra("deadline");
        String taskLocation = getIntent().getStringExtra("location");

        // Set ke tampilan
        title.setText(taskTitle);
        description.setText(taskDescription);
        deadline.setText(taskDeadline);
        location.setText(taskLocation);
    }
}
