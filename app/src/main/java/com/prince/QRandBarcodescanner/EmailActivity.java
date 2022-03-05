package com.prince.QRandBarcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EmailActivity extends AppCompatActivity {

    EditText inSubject, inBody;
    TextView txtEmailAddress;
    String toAddress;
    Button btnSendEmail;
    Button btnScanBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        initViews();
    }

    private void initViews() {
        inSubject = findViewById(R.id.inSubject);
        inBody = findViewById(R.id.inBody);
        txtEmailAddress = findViewById(R.id.txtEmailAddress);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);

        if (getIntent().getStringExtra("email_address") != null) {
            txtEmailAddress.setText("Recipient : " + getIntent().getStringExtra("email_address"));
            toAddress = getIntent().getStringExtra("email_address");
        }

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");  //need this to prompts email client only
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toAddress});
                intent.putExtra(Intent.EXTRA_SUBJECT, inSubject.getText().toString().trim());
                intent.putExtra(Intent.EXTRA_TEXT, inBody.getText().toString().trim());
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        btnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailActivity.this, ScannedBarcodeActivity.class));
            }
        });
    }
}