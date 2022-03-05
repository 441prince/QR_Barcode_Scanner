package com.prince.QRandBarcodescanner;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class TextViewScrollingActivity extends AppCompatActivity {

    TextView viewLargeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initViews();

        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra("text_display"));

                startActivity(Intent.createChooser(intent, "Share Text via"));
            }
        });
    }
    private void initViews() {
        viewLargeText = findViewById(R.id.txtViewLargeText);
        if (getIntent().getStringExtra("text_display") != null) {
            viewLargeText.setText("Captured Text: \n\n\n" + getIntent().getStringExtra("text_display"));
        }
    }
}