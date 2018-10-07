package com.volmit.fulcrum.resourcepack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.volmit.volume.lang.io.VIO;

public class PackUtils
{
	public static String hexOf(byte[] hash)
	{
		StringBuilder sb = new StringBuilder();
		for(byte b : hash)
		{
			sb.append(String.format("%02X", b));
		}

		return sb.toString();
	}

	public static byte[] hashMD5(File pack) throws IOException
	{
		try
		{
			return hash("MD5", pack);
		}

		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] hashSHA256(File pack) throws IOException
	{
		try
		{
			return hash("SHA-256", pack);
		}

		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] hash(String method, File pack) throws IOException, NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance(method);
		DigestInputStream din = new DigestInputStream(new FileInputStream(pack), md);
		VIO.fullTransfer(din, new OutputStream()
		{
			@Override
			public void write(int b) throws IOException
			{
				// ¯\_(ツ)_/¯
			}
		}, 8192);

		din.close();
		return md.digest();
	}
}
