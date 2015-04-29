package com.livescribe.base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;

import com.livescribe.base.constants.LSConstants;

public class IOUtils {
	public static String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, LSConstants.ENCODING_UTF8));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {        
			return "";
		}
	}

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns false.
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		} // The directory is now empty so delete it
		return dir.delete();
	}

	public static void deleteFilesModifiedBefore(String dir, long modifiedBefore) {
		File dirPath = new File(dir);
		if (dirPath.exists() ) {
			if ( dirPath.isDirectory() ) {
				deleteFilesModifiedBefore(dirPath, modifiedBefore);
			} else if ( dirPath.lastModified() < modifiedBefore ) {
				dirPath.delete();
			}
		}
	}

	public static void deleteFilesModifiedBefore(File file, final long modifiedBefore ) {
		if (file.exists()   ) {
			if ( file.isDirectory() ) {
				File[] filelist = file.listFiles(new FileFilter() {
					@Override
					public boolean accept(File in) {
						if ( in.lastModified() < modifiedBefore ) {
							return true;
						}
						return false;
					}

				});

				for ( File fl : filelist ) {
					deleteFilesModifiedBefore(fl, modifiedBefore);
				}
			} else {
				file.delete();
			}

		}
	}

	public static void deleteFilesModifiedBefore(String dir, Date date) {
		deleteFilesModifiedBefore(dir, date.getTime());
	}

	public static void deleteFilesOlderThanDays(String dir, int numberOfDays) {
		Date date = DateUtils.getFloorAfterDays(-1*numberOfDays);
		deleteFilesModifiedBefore(dir, date.getTime());
	}

	public static String getStringFromClob(Clob clob) throws SQLException, IOException {
		String returnValue = null;
		Reader reader = null;
		try {
			//	Convert CLOB description to String.
			if (clob != null && clob.length() != 0) {
				reader = clob.getCharacterStream();
				int clobSize = (int)clob.length();
				char[] buffer = new char[clobSize];
				reader.read(buffer);
				returnValue = new String(buffer);
				reader.close();
			}
			else {
			}
		}
		finally {
			if ( reader != null ) {
				reader.close();
			}
		}
		return returnValue;
	}
}
