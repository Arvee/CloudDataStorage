/*
 *	@author - Rahul Varanasi 
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main implements Runnable {
	private static BoxUtils globalUtils;
	private static Thread mainThread;
	private static Thread watchThread;

	public static void main(String[] args) {
		mainThread = new Thread(new Main());
		mainThread.start();
	}
	
	@Override
	public void run() {	
		Utils.printMsg("Welcome", false);
		globalUtils = BoxUtils.getBoxUtilsInstance();

		if (globalUtils != null) {
			runProgram();
		} else {
			Utils.printMsg("Critical Error", true);
		}
	}
	
	public static void runProgram() {
		try {
			boolean watching = startWatchDir(globalUtils);
			if (watching) {
				Utils.printMsgln("Watching", false);				
				String input = "";
				while (true) {
					displayCommands();
					input = Utils.readFromConsole(null);
					parseUserInput(input, globalUtils);
				}
			} else {
				Utils.printMsg("No folder to watch", true);
			}
		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);				
		}
	}	

	private static boolean startWatchDir(BoxUtils utils) throws IOException {
		boolean watching = true;
		String dirPath = Utils.readFromConsole("Enter the folder to watch");
		boolean recursive = false;
		Path dir = Paths.get(dirPath);

		watchThread = new Thread(new WatchDir(dir, recursive, utils));
		watchThread.start();

		return watching;
	}

	private static void displayCommands() {
		System.out.println("[.] Enter any of the following commands:");
		System.out
				.println("[.] 'dir' - Display contents of the root directory");
		System.out.println("[.] 'upload' - Upload a file to root directory");
		System.out
				.println("[.] 'download' - Download a file from root directory");
		System.out.println("[.] 'delete' - Delete a file from root directory");
		System.out
				.println("[.] 'mkdir' - Create a new folder in root directory");
		System.out.println("[.] 'rmdir' - Delete a folder in root directory");
		System.out.println("[.] 'quit' - Quit the application");
		System.out.println();
	}

	private static void parseUserInput(String input, BoxUtils utils) {
		switch (input) {
		case "dir":
			utils.listDir();
			break;
		case "upload":
			utils.uploadFile(null);
			break;
		case "download":
			utils.downloadFile();
			break;
		case "delete":
			utils.deleteFile();
			break;
		case "mkdir":
			utils.createFolder();
			break;
		case "rmdir":
			utils.deleteFolder();
			break;
		case "quit":
			Utils.printMsg("Quit", false);
			mainThread.interrupt();
			watchThread.interrupt();
			System.exit(0);
		default:
			Utils.printMsg("Invalid input. Try again.", true);
		}
		Utils.printMsg("", false);
	}
	
}