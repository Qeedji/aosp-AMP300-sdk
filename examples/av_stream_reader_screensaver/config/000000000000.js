/**
 * Function: configuration-by-script for screensaver allowing to activate the user preferences to activate the Screensaver for AV stream reader APK
 * Version: 001A
 * File name pattern: the file name must match one of these formats:
 * - common to several devices 
 *    - 000000000000.js
 * 	  - configuration.js 
 * - specific for one device 
 * 	  - <device_WLAN0_MAC_addr>.js (format ABCDEFABCDEF.js)
 * - when using a external Ethernet to USB-C hub 
 *    - <device_ETH0_MAC_addr>.js (format ABCDEFABCDEF.js)
 *    - in this case, the "device_ETH0_MAC_addr" value is getting the hub_ETH0_MAC_addr value
 */

/** --------------------------------------
 * ---------------------------------------
 * ---- BEGIN of the user configuration
 * ---------------------------------------
 * ---------------------------------------
 */

/*------  Activate or not the AQS screensaver. Uncomment one of the two lines ---------*/
// disableScreensaver(); // default factory value
// enableScreensaver(120); // inactivity timeout in second before going into screensaver.

/*------  Select the appropriate screensaver and configure it if any. Uncomment one of the two lines ------------*/
// setBasicColorsScreensaver(); // default factory value. Select the default Basics.Colors AQS screensaver (color gradient)
// setAvStreamReaderScreensaver("udp://239.1.2.3:1234"); // url examples: "udp://224.1.2.3:1234", "udp://239.1.2.3:1234", "udp://192.168.1.23:1234". Select the "Screensaver For AV stream reader" APK. To work properly the "Screensaver For AV stream reader" APK needs to be installed

/** --------------------------------------
 * ---------------------------------------
 * ---- END of the user configuration
 * ---------------------------------------
 * ---------------------------------------
 */

/** --------------------------------------
 * ---- Functions section
 * ---------------------------------------
 */

function disableScreensaver() {
	Android.Preferences("Settings", "System").setInt("screen_stay_on", 1);
}

function enableScreensaver(timeout) {
	Android.Preferences("Settings", "System").setInt("screen_stay_on", 0);
	Android.Preferences("Settings", "System").setInt("screen_off_timeout", timeout*1000);
	Android.Preferences("Settings", "Secure").setInt("screensaver_enabled", 1);
}

function setBasicColorsScreensaver() {
	Android.Preferences("Settings", "Secure").setString("screensaver_components", "com.android.dreams.basic/com.android.dreams.basic.Colors");
}

function setAvStreamReaderScreensaver(url) {
	Android.Preferences("Settings", "Secure").setString("screensaver_components", "tech.qeedji.av_stream_reader_screensaver/tech.qeedji.av_stream_reader_screensaver.Screensaver");
	Android.Preferences("SharedPreferences", "tech.qeedji.av_stream_reader_screensaver", "tech.qeedji.av_stream_reader_screensaver.prefs").setString("url", url);
}
