// Min Zeng - mz299@njit.edu
// Lin Zhao - lz369@njit.edu

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SimpleCracker {
	public static ArrayList<SimpleUser> users = new ArrayList<SimpleUser>();
	public static ArrayList<String> commonPasswords;

	public static String toHex(byte[] bytes) {
		BigInteger bi = new BigInteger(1, bytes);
		return String.format("%0" + (bytes.length << 1) + "X", bi);
	}

	public static void readFile() {
		File fin = new File("shadow-simple.txt");
		try {
			FileInputStream fis = new FileInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			while ((line = br.readLine()) != null) {
				addUser(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addUser(String str) {
		List<String> items = Arrays.asList(str.split("\\s*:\\s*"));
		SimpleUser user = new SimpleUser();
		user.username = items.get(0);
		user.salt = items.get(1);
		user.shash = items.get(2);
		user.password = "";
		users.add(user);
	}

	public static void readCommonPasswords() {
		try {
			Scanner s = new Scanner(new File("common-passwords.txt"));
			ArrayList<String> list = new ArrayList<String>();
			while (s.hasNext()){
			    list.add(s.next());
			}
			commonPasswords = list;
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Boolean attack(SimpleUser user) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (String pw : commonPasswords) {
				String pws = user.salt + pw;
				md.update(pws.getBytes());
				byte[] bhash = md.digest();
				String hash = toHex(bhash);
				if (user.shash.equals(hash)) {
					user.password = pw;
					return true;
				}
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println("Reading shadow-simple.txt.");
		readFile();
		System.out.println("Reading common-passwords.txt.");
		readCommonPasswords();

		System.out.println("Attacking...");
		for (SimpleUser user: users) {
			if (attack(user)) {
				System.out.printf("%s:%s\n", user.username, user.password);
			}
		}

		System.out.println("Completed!");
	}
}


