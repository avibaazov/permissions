package com.example.permission;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.BatteryManager;
import android.provider.ContactsContract;
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
    private boolean isAirplaneModePermissionGranted = true;
    private boolean isTimeValid = false;

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    isContactsPermissionGranted = true;
                    Toast.makeText(this, "Contacts permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contacts permission is required", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

        setupPermissions();
        validateTime();

        loginButton.setOnClickListener(v -> {
            if (!isContactsPermissionGranted) {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            } else {
                validateConditions();
            }
        });
    }

    private void setupPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            isContactsPermissionGranted = true;
        }

        // Dynamically check Bluetooth state
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isBluetoothAvailable = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private void validateConditions() {
        String password = passwordField.getText().toString();

        StringBuilder errorMessage = new StringBuilder("Login Failed! Issues:\n");
        boolean failed = false;

        if (!validateBatteryPercentage(password)) {
            errorMessage.append("- Incorrect battery percentage password\n");
            failed = true;
        }
        if (!isBluetoothAvailable) {
            errorMessage.append("- Bluetooth is not enabled\n");
            failed = true;
        }
        if (!validateContacts()) {
            errorMessage.append("- Not enough contacts available\n");
            failed = true;
        }
        if (!isTimeValid) {
            errorMessage.append("- Time condition not met\n");
            failed = true;
        }
        if (!isAirplaneModePermissionGranted) {
            errorMessage.append("- Airplane mode permission not granted\n");
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

    private void validateTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Example: Login allowed between 9 AM and 5 PM
        isTimeValid = hour >= 9 && hour <= 17;
    }
}
