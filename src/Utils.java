/*
 *	@author - Rahul Varanasi 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

	public static String readFromConsole(String message) throws IOException {
		if (message != null) {
			printMsg(message, false);
		}
		BufferedReader br = null;
		String input = "";
		while (true) {
			System.out.print(">>> ");
			br = new BufferedReader(new InputStreamReader(System.in));
			input = br.readLine().toLowerCase().trim();
			if (input.equals("") || input.equals(null)) {
				printMsg("Input cannot be empty", true);
				continue;
			} else {
				break;
			}
		}
		return input;
	}

	public static void printMsg(String message, boolean error) {
		if (error) {
			System.err.println("[!] " + message + "!!!");
		} else {
			System.out.println("[.] " + message + "...");
		}
	}

	public static void printMsgln(String message, boolean error) {
		printMsg(message, error);
		System.out.println();
	}

}
