package com.prince.QRandBarcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ScannedBarcodeActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    Button btnViewText;
    Button btnScanAgain;
    ImageView shareIcon;
    String intentData = "";
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        initViews();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (checkPermission()) {
                    //Toast.makeText(ScannedBarcodeActivity.this, "Checking permission", Toast.LENGTH_SHORT).show();
                    Log.w("Prince", "Checking permission");
                    //initViews();
                } else {
                    //Toast.makeText(ScannedBarcodeActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                    Log.w("Prince", "Camera Permission Denied");
                    showMessageOKCancel("You need to allow access permissions",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermission();
                                    }
                                }
                            });
                    //requestPermission();
                }
            }
        }, 15000);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.w("Prince", "checkPermission()");
            requestPermission();
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
        Log.w("Prince", "requestPermission()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    initViews();
                    // main logic
                } else {
                    //Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ScannedBarcodeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void initViews() {
        setContentView(R.layout.activity_scanned_barcode);
        surfaceView = findViewById(R.id.surfaceView);
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        btnAction = findViewById(R.id.btnAction);
        btnViewText = findViewById(R.id.viewText);
        btnScanAgain = findViewById(R.id.btnScanAgain);
        shareIcon = findViewById(R.id.shareIcon);
        btnViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannedBarcodeActivity.this,
                        TextViewScrollingActivity.class).putExtra("text_display", intentData));
            }
        });
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                    if (isEmail)
                        startActivity(new Intent(ScannedBarcodeActivity.this, EmailActivity.class).putExtra("email_address", intentData));
                    else if (IsValidUrl(intentData))
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                    else {
                        AlertDialog.Builder urlAlert = new AlertDialog.Builder(ScannedBarcodeActivity.this);
                        urlAlert.setTitle("Invalid URL");
                        urlAlert.setMessage("It is not a URL/Link");
                        urlAlert.setCancelable(true);

                        urlAlert.setPositiveButton("Share Text", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, intentData);

                                startActivity(Intent.createChooser(intent, "Share Text via"));
                            }
                        });

                        urlAlert.setNegativeButton("Scan Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(ScannedBarcodeActivity.this, ScannedBarcodeActivity.class));
                            }
                        });

                        AlertDialog urlAlertDialog = urlAlert.create();
                        urlAlertDialog.show();
                    }
                }
            }
        });
    }

    /**
     * This function is called when user accept or decline the permission.
     * Request Code is used to check which permission called this function.
     * This request code is provided when user is prompt for permission.
     */

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION ) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initViews();
                // Showing the toast message
                Toast.makeText(ScannedBarcodeActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ScannedBarcodeActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder camAlert = new AlertDialog.Builder(this);
                camAlert.setTitle("Give Camera Access");
                camAlert.setMessage("This application cannot run because it does not have the camera" +
                        " access permission required for scanning. Please enable the permission");
                camAlert.setCancelable(true);
                camAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //initViews();
                    }
                });

                camAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog camAlertDialog = camAlert.create();
                camAlertDialog.show();
            }
        }
    }*/

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScannedBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScannedBarcodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {

                            if (barcodes.valueAt(0).email != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                intentData = barcodes.valueAt(0).email.address;
                                txtBarcodeValue.setText(intentData);
                                isEmail = true;
                                btnScanAgain.setVisibility(View.VISIBLE);
                                btnAction.setText("ADD CONTENT TO THE MAIL");
                                txtBarcodeValue.setVisibility(View.VISIBLE);
                                btnAction.setVisibility(View.VISIBLE);
                                shareIcon.setVisibility(View.VISIBLE);
                                btnScanAgain.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ScannedBarcodeActivity.this, ScannedBarcodeActivity.class));
                                    }
                                });
                                shareIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_TEXT, intentData);

                                        startActivity(Intent.createChooser(intent, "Share via"));
                                    }
                                });
                            } else {
                                isEmail = false;
                                intentData = barcodes.valueAt(0).displayValue;
                                int length = intentData.length();
                                String len = "" + length;
                                if (IsValidUrl(intentData)) {
                                    btnAction.setText("OPEN LINK");
                                }
                                else {
                                    btnAction.setText("INVALID URL");
                                }
                                if (length <= 136) {
                                    txtBarcodeValue.setText(intentData);
                                    txtBarcodeValue.setVisibility(View.VISIBLE);
                                    btnViewText.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    txtBarcodeValue.setText("Large size text....");
                                    txtBarcodeValue.setVisibility(View.INVISIBLE);
                                    btnViewText.setVisibility(View.VISIBLE);
                                }
                                btnAction.setVisibility(View.VISIBLE);
                                shareIcon.setVisibility(View.VISIBLE);
                                btnScanAgain.setVisibility(View.VISIBLE);
                                btnScanAgain.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ScannedBarcodeActivity.this, ScannedBarcodeActivity.class));
                                    }
                                });
                                shareIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_TEXT, intentData);

                                        startActivity(Intent.createChooser(intent, "Share via"));
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    public static boolean IsValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (MalformedURLException ignored) {
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}