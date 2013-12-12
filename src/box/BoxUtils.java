package box;
/*
 *	@author - Rahul Varanasi 
 */

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import cloud.Utils;

import com.box.boxjavalibv2.BoxClient;
import com.box.boxjavalibv2.dao.BoxFolder;
import com.box.boxjavalibv2.dao.BoxItem;
import com.box.boxjavalibv2.dao.BoxTypedObject;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.boxjavalibv2.requests.requestobjects.BoxDefaultRequestObject;
import com.box.boxjavalibv2.requests.requestobjects.BoxFileRequestObject;
import com.box.boxjavalibv2.requests.requestobjects.BoxFileUploadRequestObject;
import com.box.boxjavalibv2.requests.requestobjects.BoxFolderRequestObject;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxUtils {	
	private static BoxUtils boxUtils = null;
	private BoxClient boxClient = null;
	
	private BoxUtils() {
		super();
	}
	
	public static synchronized BoxUtils getBoxUtilsInstance() {		
		if (boxUtils == null) {
			boxUtils = new BoxUtils();
			boxUtils.getBoxClient();			
		}
		if (boxUtils.boxClient == null) {
			boxUtils = null;
		}
		return boxUtils;
	}
	
	private void getBoxClient() {
		boxClient = BoxObject.getAuthenticatedBoxClientInstance();
	}
	
	public void listDir() {
		Utils.printMsg("Directory", false);
		try {
			BoxFolder boxFolder = boxClient.getFoldersManager().getFolder(
					"0", null);
			ArrayList<BoxTypedObject> folderEntries = boxFolder
					.getItemCollection().getEntries();
			int folderSize = folderEntries.size();
			BoxTypedObject folderEntry;
			String name = "";
			for (int i = 0; i <= folderSize - 1; i++) {
				folderEntry = folderEntries.get(i);
				name = (folderEntry instanceof BoxItem) ? ((BoxItem) folderEntry)
						.getName() : "(unknown)";
				System.out.println("[.] Name: " + name + ", Type: "
						+ folderEntry.getType() + ", Id: "
						+ folderEntry.getId());
			}
		} catch (Exception ex) {
			System.err.println("[!] Error!" + ex.getMessage());
		}
	}

	public void uploadFile(Path path) {
		Utils.printMsg("Upload file", false);
		try {
			String filePath;
			File srcFile;
			if (path == null) {
				filePath = Utils
						.readFromConsole("Please enter the path of the file to Upload");
				srcFile = new File(filePath);
			} else {
				srcFile = path.toFile();
			}

			if (srcFile.isFile()) {
				Utils.printMsg("Uploading " + srcFile.getName() + ". Please wait", false);

				FileExistsResult result = fileExists(srcFile.getName());

				BoxFileUploadRequestObject requestObj = BoxFileUploadRequestObject
						.uploadFileRequestObject("0", srcFile.getName(),
								srcFile);

				if (result != null && result.isFileExists()
						&& result.getType().equals("file")) {
					Utils.printMsg("File exists in Box. Overwriting", false);
					boxClient.getFilesManager().uploadNewVersion(
							result.getFileId(), requestObj);
				} else {
					boxClient.getFilesManager().uploadFile(requestObj);
				}
				Utils.printMsg("Uploaded", false);
			} else {
				Utils.printMsg("It is not a file", true);
			}
		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}
	}

	public void downloadFile() {
		Utils.printMsg("Download File", false);
		try {
			String srcFile = Utils
					.readFromConsole("Please enter the name of file to download");

			FileExistsResult result = fileExists(srcFile);

			if (result != null && result.isFileExists()
					&& result.getType().equals("file")) {
				Utils.printMsg("File exists in Box", false);
				String destFilePath = Utils
						.readFromConsole("Enter the path to save the downloaded file");

				File destFile = new File(destFilePath + "/" + srcFile);

				Utils.printMsg("Downloading file. Please wait", false);

				BoxDefaultRequestObject requestObj = new BoxDefaultRequestObject();
				InputStream ins = boxClient.getFilesManager().downloadFile(
						result.getFileId(), requestObj);
				Files.copy(ins, destFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING);

				Utils.printMsg("Downloaded", false);
			} else {
				Utils.printMsg("File doesn't exist", true);
			}
		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}
	}

	public void deleteFile() {
		Utils.printMsg("Delete File", false);
		try {
			String srcFile = Utils
					.readFromConsole("Please enter the name of file to delete");

			FileExistsResult result = fileExists(srcFile);

			if (result != null && result.isFileExists()
					&& result.getType().equals("file")) {
				BoxFileRequestObject requestObj = BoxFileRequestObject
						.deleteFileRequestObject();
				boxClient.getFilesManager().deleteFile(result.getFileId(),
						requestObj);
				Utils.printMsg("File deleted", false);
			} else {
				Utils.printMsg("File doesn't exist", true);
			}

		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}
	}

	public void createFolder() {
		Utils.printMsg("Create Folder", false);
		try {
			String folderName = Utils
					.readFromConsole("Enter the name of the folder to be created");

			FileExistsResult result = fileExists(folderName);

			if (result != null && result.isFileExists()
					&& result.getType().equals("folder")) {
				Utils.printMsg("Folder already exits", true);
			} else {
				BoxFolderRequestObject requestObj = BoxFolderRequestObject
						.createFolderRequestObject(folderName, "0");
				boxClient.getFoldersManager().createFolder(requestObj);
				Utils.printMsg("Folder created", false);
			}
		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}
	}

	public void deleteFolder() {
		Utils.printMsg("Delete Folder", false);
		try {
			String srcFolderDelete = Utils
					.readFromConsole("Please enter the name of folder to delete");
			FileExistsResult result = fileExists(srcFolderDelete);

			if (result != null && result.isFileExists()
					&& result.getType().equals("folder")) {
				BoxFolderRequestObject requestObj = BoxFolderRequestObject
						.deleteFolderRequestObject(true);
				boxClient.getFoldersManager().deleteFolder(
						result.getFileId(), requestObj);
				Utils.printMsg("Folder deleted", false);
			} else {
				Utils.printMsg("Folder doesn't exist", true);
			}

		} catch (Exception ex) {
			Utils.printMsg("Error! " + ex.getMessage(), true);
		}
	}

	private FileExistsResult fileExists(String srcFileName)
			throws BoxRestException, BoxServerException,
			AuthFatalFailureException {
		BoxFolder boxFolder = boxClient.getFoldersManager().getFolder("0",
				null);
		ArrayList<BoxTypedObject> folderEntries = boxFolder.getItemCollection()
				.getEntries();
		BoxTypedObject folderEntry;
		boolean fileExists = false;
		String fileId = "";
		String name = "";
		String type = "";

		for (int i = 0; i <= folderEntries.size() - 1; i++) {
			folderEntry = folderEntries.get(i);
			name = (folderEntry instanceof BoxItem) ? ((BoxItem) folderEntry)
					.getName() : "(unknown)";
			if (name.equals(srcFileName)) {
				fileExists = true;
				fileId = folderEntry.getId();
				type = folderEntry.getType();
				return new FileExistsResult(fileExists, fileId, name, type);
			}
		}

		return null;
	}
}

class FileExistsResult {
	private final boolean fileExists;
	private final String fileId;
	private final String fileName;
	private final String type;

	public FileExistsResult(boolean fileExists, String fileId, String fileName,
			String type) {
		this.fileExists = fileExists;
		this.fileId = fileId;
		this.fileName = fileName;
		this.type = type;
	}

	public boolean isFileExists() {
		return fileExists;
	}

	public String getFileId() {
		return fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public String getType() {
		return type;
	}

}
