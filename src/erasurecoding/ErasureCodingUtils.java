package erasurecoding;
/*
 *	@author - Rahul Varanasi 
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class which contains common Erasure Coding utility methods.
 * @author Anarchy
 *
 */
public class ErasureCodingUtils {
	/**
	 * This method can generate random bytes of given size using a SecureRandom object. 
	 * @param secureRandom The SecureRandom object with which random bytes should be generated.
	 * @param chunkSize The size of the bytes that have to be generated.
	 * @return The random bytes of size chunkSize generated using secureRandom.
	 */
	public static byte[] genRandomBytes(SecureRandom secureRandom, long chunkSize) {
		byte[] randomBytes = new byte[(int) chunkSize];
		secureRandom.nextBytes(randomBytes);
		return randomBytes;
	}
	
	/**
	 * This method is to XOR a file with random bytes. 
	 * The size of file and random bytes must be same for this method to work correctly.
	 * This method doesn't check for file lock before it starts reading.
	 * @param randomBytes The random bytes which have to be XORed with a file.
	 * @param file The file which has to be XORed with random bytes.
	 * @param chunkSize The chunk size of the randomBytes and file.
	 * @return The XOR of file and randomBytes.
	 * @throws IOException Can throw IO Exception for various IO related reasons. 
	 */
	public static byte[] xorRandomBytesAndFile(byte[] randomBytes, File file,
			long chunkSize) throws IOException {
		if (randomBytes.length != file.length() || randomBytes.length != chunkSize || file.length() != chunkSize) {
			throw new ArrayIndexOutOfBoundsException();
		}
		byte[] fileBytes = Files.readAllBytes(file.toPath());
		byte[] xorBytes = new byte[(int) chunkSize];		
		for (int i = 0; i < chunkSize; i++) {
			xorBytes[i] = (byte) (fileBytes[i] ^ randomBytes[i]);
		}
		return xorBytes;
	}
	
	/**
	 * This method is to XOR two random byte arrays of same size.
	 * The size of both arrays must be same for this method to work correctly. 
	 * @param random1 The first random byte array.
	 * @param random2 The second random byte array.
	 * @param chunkSize The size of random1 and random2.
	 * @return The XOR of random1 and random2.
	 * @throws ArrayIndexOutOfBoundsException Throws this exception when byte array length doesn't match the chunkSize.
	 */
	public static byte[] xorRandomBytes(byte[] random1, byte[] random2, long chunkSize) throws ArrayIndexOutOfBoundsException {
		if (random1.length != random2.length || random1.length != chunkSize || random2.length != chunkSize) {
			throw new ArrayIndexOutOfBoundsException();
		}
		byte[] xorBytes = new byte[(int) chunkSize];
		for (int i = 0; i < chunkSize; i++) {
			xorBytes[i] = (byte) (random1[i] ^ random2[i]);
		}
		return xorBytes;
	}
}

/**
 * Wrapper class to wrap the file stores.
 * @author Anarchy
 *
 */
class FileStores {
	private byte[] store1 = null;
	private byte[] store2 = null;
	private byte[] store3 = null;
	private byte[] store4 = null;
	private List<byte[]> fileStoreList = new ArrayList<byte[]>();

	public FileStores(byte[] store1, byte[] store2, byte[] store3, byte[] store4) {
		this.store1 = store1;
		this.store2 = store2;
		this.store3 = store3;
		this.store4 = store4;
	}

	public List<byte[]> getFileStoreList() {
		fileStoreList.add(store1);
		fileStoreList.add(store2);
		fileStoreList.add(store3);
		fileStoreList.add(store4);
		return fileStoreList;
	}	
}