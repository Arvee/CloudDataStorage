package erasurecoding;
/*
 *	@author - Rahul Varanasi 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ErasureCodingDecoder {
	private byte[] fs1 = null; // file store1
	private byte[] fs2 = null; // file store2
	private byte[] fs3 = null; // file store3
	private byte[] fs4 = null; // file store4

	private byte[] r1 = null; // random1
	private byte[] r2 = null; // random2
	private byte[] r3 = null; // random3
	private byte[] r4 = null; // random4
	private byte[] r5 = null; // random5
	private byte[] r6 = null; // random6
	private byte[] r7 = null; // random7
	private byte[] r8 = null; // random8
	private byte[] r9 = null; // random9

	private byte[] f1 = null; // file chunk1
	private byte[] f2 = null; // file chunk2
	private byte[] f3 = null; // file chunk3
	
	private final static String DECODED_DIRECTORY = System.getProperty("user.home")
			+ File.separator + "Desktop" + File.separator + "Cloud"
			+ File.separator + "Decoded";
	
	public void decode(File store1, File store2, File store3, File store4,
			long length, long chunkSize, String fileName) throws IOException {
		
		if (length == -1 || chunkSize == -1 || fileName.equals("")) {
			System.err.println("Unhandled Error!");
			System.exit(-1);
		}
		
		r1 = new byte[(int) chunkSize];
		r2 = new byte[(int) chunkSize];
		r3 = new byte[(int) chunkSize];
		r4 = new byte[(int) chunkSize];
		r5 = new byte[(int) chunkSize];
		r6 = new byte[(int) chunkSize];
		r7 = new byte[(int) chunkSize];
		r8 = new byte[(int) chunkSize];
		r9 = new byte[(int) chunkSize];

		loadFileBytes(store1, store2, store3, store4, length, chunkSize);

		if (store1 == null) {
			chunk234Available(length, chunkSize);
		} else if (store2 == null) {
			chunk134Available(length, chunkSize);
		} else if (store3 == null) {
			chunk124Available(length, chunkSize);
		} else {
			chunk123Available(length, chunkSize);
		}

		writeBytesToFile(length, chunkSize, fileName);
	}

	private void loadFileBytes(File store1, File store2, File store3,
			File store4, long length, long chunkSize) throws IOException {
		FileInputStream fis = null;

		if (store1 != null) {
			fis = new FileInputStream(store1);
			fs1 = new byte[(int) length];
			fis.read(fs1);
			fis.close();
		}

		if (store2 != null) {
			fis = new FileInputStream(store2);
			fs2 = new byte[(int) length];
			fis.read(fs2);
			fis.close();
		}

		if (store3 != null) {
			fis = new FileInputStream(store3);
			fs3 = new byte[(int) length];
			fis.read(fs3);
			fis.close();
		}

		if (store4 != null) {
			fis = new FileInputStream(store4);
			fs4 = new byte[(int) length];
			fis.read(fs4);
			fis.close();
		}
	}

	private void chunk123Available(long length, long chunkSize) {
		byte[] r6r7 = new byte[(int) chunkSize];
		byte[] r1r4 = new byte[(int) chunkSize];
		byte[] r4r5 = new byte[(int) chunkSize];
		byte[] r3r8 = new byte[(int) chunkSize];
		byte[] r8r9 = new byte[(int) chunkSize];
		byte[] r2r6 = new byte[(int) chunkSize];

		for (int i = 0; i < length; i++) {
			if (i < chunkSize) {
				r1[i] = fs1[i];
				r2[i] = fs2[i];
				r3[i] = fs3[i];
			} else if (i < 2 * chunkSize) {
				r6r7[(int) (i - chunkSize)] = fs1[i];
				r1r4[(int) (i - chunkSize)] = fs2[i];
				r4r5[(int) (i - chunkSize)] = fs3[i];
			} else if (i < 3 * chunkSize) {
				r3r8[(int) (i - 2 * chunkSize)] = fs1[i];
				r8r9[(int) (i - 2 * chunkSize)] = fs2[i];
				r2r6[(int) (i - 2 * chunkSize)] = fs3[i];
			}
		}

		r4 = ErasureCodingUtils.xorRandomBytes(r1r4, r1, chunkSize);
		r5 = ErasureCodingUtils.xorRandomBytes(r4r5, r4, chunkSize);
		r6 = ErasureCodingUtils.xorRandomBytes(r2r6, r2, chunkSize);
		r7 = ErasureCodingUtils.xorRandomBytes(r6r7, r6, chunkSize);
		r8 = ErasureCodingUtils.xorRandomBytes(r3r8, r3, chunkSize);
		r9 = ErasureCodingUtils.xorRandomBytes(r8r9, r8, chunkSize);
	}

	private void chunk124Available(long length, long chunkSize) {
		byte[] r2r5 = new byte[(int) chunkSize];
		byte[] r6r7 = new byte[(int) chunkSize];
		byte[] r1r4 = new byte[(int) chunkSize];
		byte[] r3r7 = new byte[(int) chunkSize];
		byte[] r3r8 = new byte[(int) chunkSize];
		byte[] r8r9 = new byte[(int) chunkSize];
		byte[] r1r9 = new byte[(int) chunkSize];

		for (int i = 0; i < length; i++) {
			if (i < chunkSize) {
				r1[i] = fs1[i];
				r2[i] = fs2[i];
				r2r5[i] = fs4[i];
			} else if (i < 2 * chunkSize) {
				r6r7[(int) (i - chunkSize)] = fs1[i];
				r1r4[(int) (i - chunkSize)] = fs2[i];
				r3r7[(int) (i - chunkSize)] = fs4[i];
			} else if (i < 3 * chunkSize) {
				r3r8[(int) (i - 2 * chunkSize)] = fs1[i];
				r8r9[(int) (i - 2 * chunkSize)] = fs2[i];
				r1r9[(int) (i - 2 * chunkSize)] = fs4[i];
			}
		}

		r4 = ErasureCodingUtils.xorRandomBytes(r1r4, r1, chunkSize);
		r5 = ErasureCodingUtils.xorRandomBytes(r2r5, r2, chunkSize);
		r9 = ErasureCodingUtils.xorRandomBytes(r1r9, r1, chunkSize);
		r8 = ErasureCodingUtils.xorRandomBytes(r8r9, r9, chunkSize);
		r3 = ErasureCodingUtils.xorRandomBytes(r3r8, r8, chunkSize);
		r7 = ErasureCodingUtils.xorRandomBytes(r3r7, r3, chunkSize);
		r6 = ErasureCodingUtils.xorRandomBytes(r6r7, r7, chunkSize);
	}

	private void chunk134Available(long length, long chunkSize) {
		byte[] r2r5 = new byte[(int) chunkSize];
		byte[] r6r7 = new byte[(int) chunkSize];
		byte[] r4r5 = new byte[(int) chunkSize];
		byte[] r3r7 = new byte[(int) chunkSize];
		byte[] r3r8 = new byte[(int) chunkSize];
		byte[] r2r6 = new byte[(int) chunkSize];
		byte[] r1r9 = new byte[(int) chunkSize];

		for (int i = 0; i < length; i++) {
			if (i < chunkSize) {
				r1[i] = fs1[i];
				r3[i] = fs3[i];
				r2r5[i] = fs4[i];
			} else if (i < 2 * chunkSize) {
				r6r7[(int) (i - chunkSize)] = fs1[i];
				r4r5[(int) (i - chunkSize)] = fs3[i];
				r3r7[(int) (i - chunkSize)] = fs4[i];
			} else if (i < 3 * chunkSize) {
				r3r8[(int) (i - 2 * chunkSize)] = fs1[i];
				r2r6[(int) (i - 2 * chunkSize)] = fs3[i];
				r1r9[(int) (i - 2 * chunkSize)] = fs4[i];
			}
		}

		r9 = ErasureCodingUtils.xorRandomBytes(r1r9, r1, chunkSize);
		r8 = ErasureCodingUtils.xorRandomBytes(r3r8, r3, chunkSize);
		r7 = ErasureCodingUtils.xorRandomBytes(r3r7, r3, chunkSize);
		r6 = ErasureCodingUtils.xorRandomBytes(r6r7, r7, chunkSize);
		r2 = ErasureCodingUtils.xorRandomBytes(r2r6, r6, chunkSize);
		r5 = ErasureCodingUtils.xorRandomBytes(r2r5, r2, chunkSize);
		r4 = ErasureCodingUtils.xorRandomBytes(r4r5, r5, chunkSize);
	}

	private void chunk234Available(long length, long chunkSize) {
		byte[] r2r5 = new byte[(int) chunkSize];
		byte[] r1r4 = new byte[(int) chunkSize];
		byte[] r4r5 = new byte[(int) chunkSize];
		byte[] r3r7 = new byte[(int) chunkSize];
		byte[] r8r9 = new byte[(int) chunkSize];
		byte[] r2r6 = new byte[(int) chunkSize];
		byte[] r1r9 = new byte[(int) chunkSize];

		for (int i = 0; i < length; i++) {
			if (i < chunkSize) {
				r2[i] = fs2[i];
				r3[i] = fs3[i];
				r2r5[i] = fs4[i];
			} else if (i < 2 * chunkSize) {
				r1r4[(int) (i - chunkSize)] = fs2[i];
				r4r5[(int) (i - chunkSize)] = fs3[i];
				r3r7[(int) (i - chunkSize)] = fs4[i];
			} else if (i < 3 * chunkSize) {
				r8r9[(int) (i - 2 * chunkSize)] = fs2[i];
				r2r6[(int) (i - 2 * chunkSize)] = fs3[i];
				r1r9[(int) (i - 2 * chunkSize)] = fs4[i];
			}
		}

		r5 = ErasureCodingUtils.xorRandomBytes(r2r5, r2, chunkSize);
		r6 = ErasureCodingUtils.xorRandomBytes(r2r6, r2, chunkSize);
		r7 = ErasureCodingUtils.xorRandomBytes(r3r7, r3, chunkSize);
		r4 = ErasureCodingUtils.xorRandomBytes(r4r5, r5, chunkSize);
		r1 = ErasureCodingUtils.xorRandomBytes(r1r4, r4, chunkSize);
		r9 = ErasureCodingUtils.xorRandomBytes(r1r9, r1, chunkSize);
		r8 = ErasureCodingUtils.xorRandomBytes(r8r9, r9, chunkSize);
	}

	private void writeBytesToFile(long length, long chunkSize, String fileName)
			throws IOException {
		f1 = ErasureCodingUtils.xorRandomBytes(r8, r5, chunkSize);
		f2 = ErasureCodingUtils.xorRandomBytes(r7, r4, chunkSize);
		f3 = ErasureCodingUtils.xorRandomBytes(r9, r6, chunkSize);

		byte[] fileBytes = new byte[(int) length];
		for (int i = 0; i < length; i++) {
			if (i < chunkSize) {
				fileBytes[i] = f1[i];
			} else if (i < 2 * chunkSize) {
				fileBytes[i] = f2[(int) (i - chunkSize)];
			} else if (i < 3 * chunkSize) {
				fileBytes[i] = f3[(int) (i - 2 * chunkSize)];
			}
		}
		
		// create directories recursively if they don't exist
		new File(DECODED_DIRECTORY).mkdirs();
		
		File decodedFile = new File(DECODED_DIRECTORY + File.separator + fileName);
		if (decodedFile.exists()) {
			decodedFile.delete();			
		}
		decodedFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(decodedFile);
		fos.write(fileBytes);
		fos.close();
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("Please provide 4 command line arguments!");
			System.exit(-1);
		}
				
		File store1 = new File(args[0]);
		File store2 = new File(args[1]);
		File store3 = new File(args[2]);
		File store4 = new File(args[3]);
		
		long store1Length;
		long store2Length;
		long store3Length;
		long store4Length;
		int nullFileCount = 0;
		
		if (store1 != null && store1.exists() && store1.isFile()) {
			store1Length = store1.length();
		} else {
			store1 = null;
			store1Length = -1;
			nullFileCount++;
		}
		if (store2 != null && store2.exists() && store2.isFile()) {
			store2Length = store2.length();
		} else {
			store2 = null;
			store2Length = -1;
			nullFileCount++;
		}
		if (store3 != null && store3.exists() && store3.isFile()) {
			store3Length = store3.length();
		} else {
			store3 = null;
			store3Length = -1;
			nullFileCount++;
		}
		if (store4 != null && store4.exists() && store4.isFile()) {
			store4Length = store4.length();
		} else {
			store4 = null;
			store4Length = -1;
			nullFileCount++;
		}
		
		if (nullFileCount > 1) {
			System.err.println("Atleast 3 files should exist!");
			System.exit(-1);
		}
		
		boolean sameLength = false;		
		String fileName = "";
		long length = -1;
		long chunkSize = -1;		
		
		if (isSameLength(store1Length, store2Length, store3Length)) {
			sameLength = true;
			fileName = store1.getName().substring(6);
			length = store1Length;
			chunkSize = length / 3;			
		} else if (isSameLength(store1Length, store2Length, store4Length)) {
			sameLength = true;
			fileName = store1.getName().substring(6);
			length = store1Length;
			chunkSize = length / 3;			
		} else if (isSameLength(store1Length, store3Length, store4Length)) {
			sameLength = true;
			fileName = store1.getName().substring(6);
			length = store1Length;
			chunkSize = length / 3;			
		} else if (isSameLength(store2Length, store3Length, store4Length)) {
			sameLength = true;
			fileName = store2.getName().substring(6);
			length = store2Length;
			chunkSize = length / 3;			
		}
		
		if (sameLength) {
			new ErasureCodingDecoder().decode(store1,
					store2, store3, store4, length,
					chunkSize, fileName);
		} else {
			System.err.println("Atleast 3 files should be of same length!");
			System.exit(-1);
		}
	}
	
	private static boolean isSameLength(long a, long b, long c) {
		return (a == b && b == c) ? true : false;		
	}
}
