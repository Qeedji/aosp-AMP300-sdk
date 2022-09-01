/**
 * Function: configuration-by-script for URL Launcher 
 * Version: 1.10.10
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

/*------  Allows to handle start_after_boot_completed ---------*/
//enableStartAfterBootCompleted();
//disableStartAfterBootCompleted();

/*------  Allows to handle autorefresh_url_enabled ---------*/
//enableAutoRefreshUrl();
//disableAutoRefreshUrl();

/*------  Allows to set autorefresh_url_interval ---------*/
//setAutoRefreshUrlInterval(60);

/*------  Allows to set url ---------*/
//setURL("https://www.qeedji.tech/en/");

/*------  Allows to set credential ---------*/
//setCredential("credential1");

/*------  Allows to create a credential of type Native ---------*/
//createNativeCredential("credential1", "username", "password");

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

/* Functions to handle start_after_boot_completed */
function enableStartAfterBootCompleted() {
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setBoolean("start_after_boot_completed", true);
}
function disableStartAfterBootCompleted() {
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setBoolean("start_after_boot_completed", false);
}

/* Functions to handle autorefresh_url_enabled */
function enableAutoRefreshUrl() {
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setBoolean("autorefresh_url_enabled", true);
}
function disableAutoRefreshUrl() {
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setBoolean("autorefresh_url_enabled", false);
}

/* Function to set autorefresh_url_interval */
function setAutoRefreshUrlInterval(aSeconds) {
	if (aSeconds >= 1) {
		Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setLong("autorefresh_url_interval", aSeconds);
	}
}

/* Function to set url */
function setURL(url) {
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setString("url", url);
}

/* Function to set credential */
function setCredential(aCredential) {
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", "tech.qeedji.url_launcher.prefs").setString("credential", aCredential);
}

/* Function to create a credential of type native */
function createNativeCredential(aCredential, aUsername, aPassword) {
	var basename = "tech.qeedji.url_launcher.credential." + aCredential + ".prefs";
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", basename).setString("type", "native");
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", basename).setString("username", aUsername);
	Android.Preferences("SharedPreferences", "tech.qeedji.url_launcher", basename).setString("password", aPassword);
}
