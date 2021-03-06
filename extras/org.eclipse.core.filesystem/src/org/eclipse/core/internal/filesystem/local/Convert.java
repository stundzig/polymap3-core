/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * 	Martin Oberhuber (Wind River) - [170317] add symbolic link support to API
 *******************************************************************************/
package org.eclipse.core.internal.filesystem.local;

import java.io.UnsupportedEncodingException;
import org.eclipse.osgi.service.environment.Constants;

public class Convert {

	/** Indicates the default string encoding on this platform */
	private static String defaultEncoding = new java.io.InputStreamReader(new java.io.ByteArrayInputStream(new byte[0])).getEncoding();

	/** Indicates if we are running on windows */
	private static final boolean isWindows = Constants.OS_WIN32.equals(LocalFileSystem.getOS());

	private static final String WIN32_FILE_PREFIX = "\\\\?\\"; //$NON-NLS-1$
	private static final String WIN32_UNC_FILE_PREFIX = "\\\\?\\UNC"; //$NON-NLS-1$

	/**
	 * Performs conversion of a long value to a byte array representation.
	 *
	 * @see #bytesToLong(byte[])
	 */
	public static byte[] longToBytes(long value) {

		// A long value is 8 bytes in length.
		byte[] bytes = new byte[8];

		// Convert and copy value to byte array:
		//   -- Cast long to a byte to retrieve least significant byte;
		//   -- Left shift long value by 8 bits to isolate next byte to be converted;
		//   -- Repeat until all 8 bytes are converted (long = 64 bits).
		// Note: In the byte array, the least significant byte of the long is held in
		// the highest indexed array bucket.

		for (int i = 0; i < bytes.length; i++) {
			bytes[(bytes.length - 1) - i] = (byte) value;
			value >>>= 8;
		}

		return bytes;
	}

	/**
	 * Performs conversion of a byte array to a long representation.
	 *
	 * @see #longToBytes(long)
	 */
	public static long bytesToLong(byte[] value) {

		long longValue = 0L;

		// See method convertLongToBytes(long) for algorithm details.	
		for (int i = 0; i < value.length; i++) {
			// Left shift has no effect through first iteration of loop.
			longValue <<= 8;
			longValue ^= value[i] & 0xFF;
		}

		return longValue;
	}

	/**
	 * Calling new String(byte[] s) creates a new encoding object and other garbage.
	 * This can be avoided by calling new String(byte[] s, String encoding) instead.
	 * @param source String in platform bytes
	 * @return converted Java String
	 * @since org.eclipse.core.filesystem 1.1
	 */
	public static String fromPlatformBytes(byte[] source) {
		if (defaultEncoding == null)
			return new String(source);
		// try to use the default encoding
		try {
			return new String(source, defaultEncoding);
		} catch (UnsupportedEncodingException e) {
			// null the default encoding so we don't try it again
			defaultEncoding = null;
			return new String(source);
		}
	}

	/**
	 * Calling String.getBytes() creates a new encoding object and other garbage.
	 * This can be avoided by calling String.getBytes(String encoding) instead.
	 */
	public static byte[] toPlatformBytes(String target) {
		if (defaultEncoding == null)
			return target.getBytes();
		// try to use the default encoding
		try {
			return target.getBytes(defaultEncoding);
		} catch (UnsupportedEncodingException e) {
			// null the default encoding so we don't try it again
			defaultEncoding = null;
			return target.getBytes();
		}
	}

	/**
	 * Converts a file name to a unicode char[] suitable for use by native methods.
	 * See http://msdn.microsoft.com/library/default.asp?url=/library/en-us/fileio/fs/naming_a_file.asp
	 */
	public static char[] toPlatformChars(String target) {
		//Windows use special prefix to handle long filenames
		if (!isWindows)
			return target.toCharArray();
		//convert UNC path of form \\server\path to unicode form \\?\UNC\server\path
		if (target.startsWith("\\\\")) { //$NON-NLS-1$
			int nameLength = target.length();
			int prefixLength = WIN32_UNC_FILE_PREFIX.length();
			char[] result = new char[prefixLength + nameLength - 1];
			WIN32_UNC_FILE_PREFIX.getChars(0, prefixLength, result, 0);
			target.getChars(1, nameLength, result, prefixLength);
			return result;
		}
		//convert simple path of form c:\path to unicode form \\?\c:\path
		int nameLength = target.length();
		int prefixLength = WIN32_FILE_PREFIX.length();
		char[] result = new char[prefixLength + nameLength];
		WIN32_UNC_FILE_PREFIX.getChars(0, prefixLength, result, 0);
		target.getChars(0, nameLength, result, prefixLength);
		return result;
	}
}
