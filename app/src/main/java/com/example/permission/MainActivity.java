package com.example.permission;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.BatteryManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText passwordField;
    private Button loginButton;

    private boolean isContactsPermissionGranted = false;
    private boolean isBluetoothAvailable = false;
    private boolean isStoragePermissionGranted = false;
    private boolean isCalendarPermissionGranted = false;
    private boolean isCameraPermissionGranted = false;
    private boolean isAirplaneModeOff = true;
    private boolean isTimeValid = false;

    private ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean contactsPermission = result.getOrDefault(Manifest.permission.READ_CONTACTS, false);
                boolean storagePermission = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);
                boolean calendarPermission = result.getOrDefault(Manifest.permission.READ_CALENDAR, false);
                boolean cameraPermission = result.getOrDefault(Manifest.permission.CAMERA, false);

                if (!contactsPermission) {
                    Toast.makeText(this, "Contacts permission is required", Toast.LENGTH_SHORT).show();
                } else {
                    isContactsPermissionGranted = true;
                }

                if (!storagePermission) {
                    Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show();
                } else {
                    isStoragePermissionGranted = true;
                }

                if (!calendarPermission) {
                    Toast.makeText(this, "Calendar permission is required", Toast.LENGTH_SHORT).show();
                } else {
                    isCalendarPermissionGranted = true;
                }

                if (!cameraPermission) {
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                } else {
                    isCameraPermissionGranted = true;
                }

                updateBluetoothState();
                updateAirplaneModeState();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

        requestAllPermissions();

        loginButton.setOnClickListener(v -> {
            validateTime();
            updateBluetoothState();
            updateAirplaneModeState();
            validateConditions();
        });
    }

    private void requestAllPermissions() {
        requestPermissionsLauncher.launch(new String[]{
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.CAMERA
        });
    }

    private void updateBluetoothState() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isBluetoothAvailable = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private void updateAirplaneModeState() {
        isAirplaneModeOff = Settings.System.getInt(
                getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) == 0;
    }

    private void validateConditions() {
        String password = passwordField.getText().toString();

        StringBuilder errorMessage = new StringBuilder("Login Failed! Issues:\n");
        boolean failed = false;

        if (!validateBatteryPercentage(password)) {
            errorMessage.append("- Incorrect battery percentage password\n");
            failed = true;
        }
        if (!isBluetoothEnabled()) {
            errorMessage.append("- Bluetooth is not enabled\n");
            failed = true;
        }
        if (!validateContacts()) {
            errorMessage.append("- Not enough contacts available\n");
            failed = true;
        }
        if (!validateImages()) {
            errorMessage.append("- Not enough images in storage\n");
            failed = true;
        }
        if (!isStoragePermissionGranted) {
            errorMessage.append("- Storage permission not granted\n");
            failed = true;
        }
        if (!isCalendarPermissionGranted) {
            errorMessage.append("- Calendar permission not granted\n");
            failed = true;
        }
        if (!isCameraPermissionGranted) {
            errorMessage.append("- Camera permission not granted\n");
            failed = true;
        }
        if (!isAirplaneModeOff) {
            errorMessage.append("- Airplane mode is enabled\n");
            failed = true;
        }
        if (!isTimeValid) {
            errorMessage.append("- Time condition not met\n");
            failed = true;
        }

        if (failed) {
            Toast.makeText(MainActivity.this, errorMessage.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateBatteryPercentage(String password) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        return password.equals(String.valueOf(level));
    }

    private boolean validateContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            int contactCount = cursor.getCount();
            cursor.close();

            // Validate if the number of contacts is greater than 5
            return contactCount > 5;
        }
        return false;
    }

    private boolean validateImages() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            int imageCount = cursor.getCount();
            cursor.close();

            // Validate if the number of images is greater than 5
            return imageCount > 5;
        }
        return false;
    }

    private void validateTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Example: Login allowed between 9 AM and 5 PM
        isTimeValid = hour >= 9 && hour <= 21;
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }
}
