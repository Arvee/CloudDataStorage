package erasurecoding;
/*
 *	@author - Rahul Varanasi 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import cloud.Utils;
import cloud.WatchDir;

public class ErasureCodingEncoder implements Runnable {
	private File file = null;	
	private File[] fileChunksArr = null;	
	private final int numSplits = 3;	
	
	private IUploadFile uploadFile = null;
	
	private static final String TEMP_DIRECTORY = System.getProperty("user.home")
			+ File.separator + "Desktop" + File.separator
			+ "Cloud" + File.separator + "Temp";
	
	private static final String STORE_DIRECTORY = System.getProperty("user.home")
			+ File.separator + "Desktop" + File.separator
			+ "Cloud" + File.separator + "Store";
	
	public ErasureCodingEncoder(File file) {
		this.file = file;
	}

	public ErasureCodingEncoder(File file, WatchDir watch) {
		this.file = file;
		uploadFile = watch;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(5000);
			encode();
		} catch (Exception e) {
			Utils.printMsg("Exception: " + e.getMessage(), true);			
		}
	}

	private void encode() throws IOException, InterruptedException {		
		Utils.printMsg("Encoding", false);
		
		fileChunksArr = new File[numSplits];
		
		new File(TEMP_DIRECTORY).mkdirs();
		new File(STORE_DIRECTORY).mkdirs();

		long chunkSize = splitFile();		
		
		List<byte[]> fileStoreList = bpxorCoding(chunkSize);
		
		generateFileStores(fileStoreList);
			
		generateDecoderBatchFile();
			
		deleteOriginalFile();
		
		uploadStoreFile();		
	}	
	
	private long splitFile() throws IOException, InterruptedException {		
		String msg = "Splitting " + file.getName() + " into chunks";
		Utils.printMsg(msg, false);
		
		long fileSize = file.length();
		byte[] fileBytes = new byte[(int) (fileSize)];		
		
		FileChannel channel = null;
		while (true) {
			try {
				try {
					// check for open file handles
					channel = new RandomAccessFile(file, "r").getChannel();									
					break;
				} finally {					
					channel.close();					
				}				
			} catch (Exception e) {
				Utils.printMsg("Exception: " + e.getMessage(), true);				
				Thread.sleep(2000);
				continue;
			}
		}
		
		FileInputStream fis = new FileInputStream(file);
		fis.read(fileBytes); // read file bytes
		fis.close();
		
		int remainder = (int) (fileSize % numSplits);
		int bytesToAdd = (remainder == 0) ? 0 : (numSplits - remainder);
		if (bytesToAdd != 0) { // add some empty bytes
			fileBytes = Arrays.copyOf(fileBytes, fileBytes.length + bytesToAdd);
		}

		long chunkSize = fileBytes.length / 3;
		byte[] buffer;
		FileOutputStream fos = null;
		String fileChunkPath;
		File fileChunk;		

		for (int i = 0; i < numSplits; i++) { // generate file chunks
			int from = (int) (i * chunkSize);
			int to = (int) (from + chunkSize);
			buffer = Arrays.copyOfRange(fileBytes, from, to);

			fileChunkPath = TEMP_DIRECTORY + File.separator + file.getName()
					+ "." + String.format("%03d", i + 1);

			fileChunk = new File(fileChunkPath);
			if (fileChunk.exists()) {
				fileChunk.delete();
			}
			fileChunk.createNewFile();

			fos = new FileOutputStream(fileChunk);
			fos.write(buffer);
			fos.close();

			fileChunksArr[i] = fileChunk;
		}
		return chunkSize;
	}

	private List<byte[]> bpxorCoding(long chunkSize) throws IOException {
		String msg = "Performing BP-XOR coding on the file chunks";
		Utils.printMsg(msg, false);
		
		SecureRandom secureRandom = new SecureRandom();
		
		byte[] r1 = ErasureCodingUtils.genRandomBytes(secureRandom, chunkSize);
		byte[] r2 = ErasureCodingUtils.genRandomBytes(secureRandom, chunkSize);
		byte[] r3 = ErasureCodingUtils.genRandomBytes(secureRandom, chunkSize);
		byte[] r4 = ErasureCodingUtils.genRandomBytes(secureRandom, chunkSize);
		byte[] r5 = ErasureCodingUtils.genRandomBytes(secureRandom, chunkSize);
		byte[] r6 = ErasureCodingUtils.genRandomBytes(secureRandom, chunkSize);

		byte[] r7 = ErasureCodingUtils.xorRandomBytesAndFile(r4,
				fileChunksArr[1], chunkSize);
		byte[] r8 = ErasureCodingUtils.xorRandomBytesAndFile(r5,
				fileChunksArr[0], chunkSize);
		byte[] r9 = ErasureCodingUtils.xorRandomBytesAndFile(r6,
				fileChunksArr[2], chunkSize);

		byte[] r6r7 = ErasureCodingUtils.xorRandomBytes(r6, r7, chunkSize); // store1
		byte[] r3r8 = ErasureCodingUtils.xorRandomBytes(r3, r8, chunkSize);

		byte[] r1r4 = ErasureCodingUtils.xorRandomBytes(r1, r4, chunkSize); // store2
		byte[] r8r9 = ErasureCodingUtils.xorRandomBytes(r8, r9, chunkSize);

		byte[] r4r5 = ErasureCodingUtils.xorRandomBytes(r4, r5, chunkSize); // store3
		byte[] r2r6 = ErasureCodingUtils.xorRandomBytes(r2, r6, chunkSize);

		byte[] r2r5 = ErasureCodingUtils.xorRandomBytes(r2, r5, chunkSize); // store4
		byte[] r3r7 = ErasureCodingUtils.xorRandomBytes(r3, r7, chunkSize);
		byte[] r1r9 = ErasureCodingUtils.xorRandomBytes(r1, r9, chunkSize);

		byte[] store1 = new byte[(int) (3 * chunkSize)];
		byte[] store2 = new byte[(int) (3 * chunkSize)];
		byte[] store3 = new byte[(int) (3 * chunkSize)];
		byte[] store4 = new byte[(int) (3 * chunkSize)];

		for (int i = 0; i < 3 * chunkSize; i++) {
			if (i < chunkSize) {
				store1[i] = r1[i];
				store2[i] = r2[i];
				store3[i] = r3[i];
				store4[i] = r2r5[i];
			} else if (i < 2 * chunkSize) {
				store1[i] = r6r7[(int) (i - chunkSize)];
				store2[i] = r1r4[(int) (i - chunkSize)];
				store3[i] = r4r5[(int) (i - chunkSize)];
				store4[i] = r3r7[(int) (i - chunkSize)];
			} else if (i < 3 * chunkSize) {
				store1[i] = r3r8[(int) (i - 2 * chunkSize)];
				store2[i] = r8r9[(int) (i - 2 * chunkSize)];
				store3[i] = r2r6[(int) (i - 2 * chunkSize)];
				store4[i] = r1r9[(int) (i - 2 * chunkSize)];
			}
		}		
		return new FileStores(store1, store2, store3, store4).getFileStoreList();				 
	}

	private void generateFileStores(List<byte[]> fileStoreList)
			throws IOException {
		String msg = "Generating file stores";
		Utils.printMsg(msg, false);		
		
		FileOutputStream fos = null;
		int count = 1;
		String fileStorePath;
		File fileStore;
		for (byte[] store : fileStoreList) { // write file stores to files
			fileStorePath = STORE_DIRECTORY + File.separator
					+ "store" + count++ + file.getName();

			fileStore = new File(fileStorePath);
			if (fileStore.exists()) {
				fileStore.delete();
			}
			fileStore.createNewFile();
			fos = new FileOutputStream(fileStore);
			fos.write(store);
			fos.close();
		}		
	}
	
	private void generateDecoderBatchFile() throws IOException {
		String msg = "Generating the decoder batch file";
		Utils.printMsg(msg, false);			
		
		File batchFile = new File(STORE_DIRECTORY + File.separator
				+ "decode" + file.getName() + ".bat");
		if (batchFile.exists()) {
			batchFile.delete();
		}
		batchFile.createNewFile();
		PrintWriter writer = new PrintWriter(batchFile, "UTF-8");
		String execCmd = "java -jar ErasureCodingDecoder.jar";
		writer.print(execCmd);
		for (int i = 0; i < 4; i++) {
			writer.print(" store" + (i+1) + file.getName());
		}
		writer.println();
		writer.print("pause");
		writer.close();
	}
	
	private void deleteOriginalFile() {
		String msg = "Deleting the file chunks and the original file";
		Utils.printMsg(msg, false);
		
		for (File fileChunk : fileChunksArr) {
			fileChunk.delete(); // delete file chunks after stores are generated
		}
		file.delete();
	}
	
	private void uploadStoreFile() {
		String msg = "Uploading store1 file to Box";
		Utils.printMsg(msg, false);
		
		if (uploadFile == null) {
			return;			
		}
		String store1File = STORE_DIRECTORY + File.separator + "store1" + file.getName();
		File f = new File(store1File);		
		uploadFile.uploadFile(f);
	}	
	
	public static void main(String[] args) {
		new Thread(new ErasureCodingEncoder(new File(
				"C:\\Users\\Anarchy\\Desktop\\Cloud\\Upload\\video.wmv"))).start();
	}
	
	public static interface IUploadFile {
		void uploadFile(File file);
	}
}