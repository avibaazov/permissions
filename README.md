# Android Security App

This Android application demonstrates various security checks during the login process. The app requires specific conditions to be met for successful login, such as:

- The password matching the current battery percentage.
- Bluetooth being enabled.
- The number of contacts in the phone exceeding 5.
- The current time being within a valid range (e.g., 9 AM to 5 PM).
- Airplane mode being disabled.

## Features

1. **Battery Percentage Validation**: Users must enter the current battery percentage as the password.
2. **Bluetooth Check**: Ensures that Bluetooth is enabled on the device.
3. **Contact Validation**: Verifies that the device has more than 5 contacts.
4. **Time Validation**: Restricts login to specific hours (9 AM to 5 PM).
5. **Airplane Mode Check**: Ensures that airplane mode is turned off.

## Permissions Required

The app requires the following permissions:

- `android.permission.READ_CONTACTS`: To validate the number of contacts on the device.
- `android.permission.BLUETOOTH`: To check the state of Bluetooth.
- `android.permission.BLUETOOTH_ADMIN`: To manage Bluetooth settings.
- `android.permission.ACCESS_FINE_LOCATION`: Required for certain Bluetooth operations.
- `android.permission.BATTERY_STATS`: Used to fetch battery level (note: simulated behavior for non-system apps).

## Installation

Clone the repository:
   ```bash
   git clone https://github.com/avibaazov/permissions.git
   ```
2. Open the project in Android Studio.
3. Build and run the app on an Android device or emulator.

## Usage

1. Grant the required permissions when prompted.
2. Enter the current battery percentage in the password field.
3. Ensure Bluetooth is enabled, you have more than 5 contacts, the time is within the allowed range, and airplane mode is disabled.
4. Press the "Login" button.

## Troubleshooting

- If login fails, the app will display a detailed error message listing unmet conditions.
- Ensure all required permissions are granted for the app to function properly.







