# Start app at boot

To start your app after the boot of the device, you can receive the [Intent.ACTION_BOOT_COMPLETED](https://developer.android.com/reference/android/content/Intent#ACTION_BOOT_COMPLETED) that is broadcast after the system finishes booting.
When you receive this broadcast action, you can launch your activity or your service.

Your manifest must authorize the [RECEIVE_BOOT_COMPLETED](https://developer.android.com/reference/android/Manifest.permission#RECEIVE_BOOT_COMPLETED) and declare the receiver `android.intent.action.BOOT_COMPLETED`.
