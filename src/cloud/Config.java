package cloud;
/*
 *	@author - Rahul Varanasi 
 */

import java.io.File;

public class Config {
	public static final String MAIN_DIRECTORY = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Cloud";
	public static final String DECODED_DIRECTORY = MAIN_DIRECTORY + File.separator + "Decoded";
	public static final String FILE_STORE_DIRECTORY = MAIN_DIRECTORY + File.separator + "Store";
	public static final String TEMP_DIRECTORY = MAIN_DIRECTORY + File.separator + "Temp";
	public static final String UPLOAD_DIRECTORY = MAIN_DIRECTORY + File.separator + "Upload";
}