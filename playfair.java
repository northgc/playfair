import java.util.*;

/*
 * playfair.java a java application for encrypting and decrypting using the Playfair cipher
 * for more info on this cryptosystem see http://en.wikipedia.org/wiki/Playfair_cipher
 * 
 * @author Grayson North
 * @date April 2014
 */

public class playfair {
	static String ciphertext;
	static String keyword;
	static List<Digraph> cdigraphs = new LinkedList<Digraph>();
	static List<Digraph> ddigraphs = new LinkedList<Digraph>();
	static char[][] keytable = new char[5][5];
	static Hashtable<Character, Integer> rowval = new Hashtable<Character,Integer>();
	static Hashtable<Character, Integer> colval = new Hashtable<Character,Integer>();
	
	
	
	public static void main(String args[]) {
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Grayson's Playfair Cipher v1.0");
		System.out.println("Would you like to (encrypt) or (decrypt) a message? (type one)");
		
		String answer = scan.nextLine();
		if(answer.equals("encrypt") || answer.equals("e") || answer.equals("decrypt") || answer.equals("d")) {
		if(answer.equals("encrypt") || answer.equals("e")) {
			System.out.println("Please type a message for encryption");
			ciphertext = scan.nextLine(); 
			System.out.println("Please input keyword");
			//TODO multi thread while waiting for text
			keyword = scan.nextLine();
		
			buildKeyTable();
			buildDigraphList();
			encryptDigraphList();
		
			//print decrpyted digraph
			System.out.println("Encrypted message:");
			for(Digraph d : ddigraphs) {
				System.out.print(d.c1);
				System.out.print(d.c2);
			}
		}
		
		else if(answer.equals("decrypt") || answer.equals("d")) {
			System.out.println("Please type a message for decryption");
			ciphertext = scan.nextLine(); 
			System.out.println("Please input keyword");
			//TODO multi thread while waiting for text
			keyword = scan.nextLine();
		
			buildKeyTable();
			buildDigraphList();
			decryptDigraphList();
			
			System.out.println("Decrypted message (ignore extra Xs and Qs):");
			for(Digraph d : ddigraphs) {
				System.out.print(d.c1);
				System.out.print(d.c2);
			}
		}
		System.out.println("\nEncrypt/Decrypt another message? (yes/no)");
		answer = scan.nextLine();
		if(answer.equals("yes")) {
			main(null);
		}
		}
		
		else {
			System.out.println("Invalid input restarting");
			main(null);
		}
			
	}
	
	private static void buildKeyTable() {
		keyword = keyword.toUpperCase(); //make sure keyword is all upper case
		keyword = keyword.replaceAll("J","I"); //replace all js with is
		keyword = keyword.replaceAll("\\s+",""); //remove all whitespace 
		keyword = keyword.replaceAll("[^a-zA-Z]", ""); //remove all nonalphabet chars
		keyword = keyword.concat("ABCDEFGHIKLMNOPQRSTUVWXYZ");
		keyword = removeDuplicates(keyword); //remove all duplicate characters
		//System.out.println("Formatted keyword: "+keyword);
		
		int k = 0;
		for(int i=0; i<5; i++) {
			for(int j=0; j<5; j++) {
				keytable[i][j] = keyword.charAt(k);
				rowval.put(keyword.charAt(k), i); //while we're here let's store the row
				colval.put(keyword.charAt(k), j); //and col values of the chars in a fast hashtable
				k++;
			}
		}
		
		System.out.println("Key Table:"); 
		for(int i=0;i<5;i++) { //print out the key table
			System.out.println();
			for(int j=0;j<5;j++) {
				System.out.print(keytable[i][j]);
			}
		}
	}
	
	private static void buildDigraphList() {
		ciphertext = ciphertext.toUpperCase(); //change to all upper case
		ciphertext = ciphertext.replaceAll("\\s+",""); //remove all whitespace 
		ciphertext = ciphertext.replaceAll("J","I"); //replace all j with i
		ciphertext = ciphertext.replaceAll("[^a-zA-Z]", "");//remove all non alphabet characters
		
		char last = 7;
		char curr;
		//int j = 0;
		//char[] newcipher = new char[];
		StringBuilder sb = new StringBuilder();
		
		//split all repeated letters up with X
		int k = 0;
		for(int i=0; i<ciphertext.length(); i++) {
			curr = ciphertext.charAt(i);
			if(curr == last && k%2==1) { //if we have repeating characters
				sb.append('X'); //put a X in between them only if we need to
				k++;
				sb.append(curr);
				k++;
			}
			else {
				sb.append(curr);
				k++;
			}
			last = ciphertext.charAt(i); //store the last character
			//k++;
		}
		
		
		String newtext = sb.toString();
		if(newtext.length()%2 == 1) { //if there is an odd number of chars, pad with an Q
			newtext = newtext + "Q";
		}
		
		System.out.println("\nFormatted cipher: "+newtext);
		for(int i=0; i<newtext.length(); i=i+2) {
			Digraph toadd = new Digraph(newtext.charAt(i),newtext.charAt(i+1));
			cdigraphs.add(toadd);
		}
	}
	
	private static void encryptDigraphList() {
		char one;
		int rowone = -1;
		int colone =-1;
		char two;
		int rowtwo =-2;
		int coltwo =-2;
		
		for(Digraph d:cdigraphs) {
			one = d.c1;
			two = d.c2;
			System.out.println("Checking digraph: "+one+two);
			rowone = rowval.get(one);
			colone = colval.get(one);
			rowtwo = rowval.get(two);
			coltwo = colval.get(two);
			//System.out.println(one+" row "+rowone+" col " +colone+" "+two+" row "+rowtwo+" col "+coltwo);
		
			Digraph newd = new Digraph();
			if(rowone == rowtwo) {
				//System.out.println("rows match");
				newd.c1 = keytable[rowone][(colone+1)%5];
				newd.c2 = keytable[rowtwo][(coltwo+1)%5];
				System.out.println("Row replacing with: "+newd.c1+newd.c2);
				//System.out.println(newd.c1+" row "+rowone+" col "+((colone+1)%5));
			}
			
			else if(colone == coltwo) {
				//System.out.println("cols match");
				if(rowone == 4 && rowtwo != 4) {
					newd.c1 = keytable[0][colone];
					newd.c2 = keytable[(rowtwo+1)%5][coltwo];
				}
				else if(rowone != 4 && rowtwo == 4) {
					newd.c1 = keytable[(rowone+1)%5][colone];
					newd.c2 = keytable[0][coltwo];
				}
				else if(rowone == 4 && rowtwo == 4) {
					newd.c1 = keytable[0][colone];
					newd.c2 = keytable[0][coltwo];
				}
				else {
					newd.c1 = keytable[(rowone+1)%5][colone];
					newd.c2 = keytable[(rowtwo+1)%5][coltwo];
				}
				System.out.println("Column replacing with: "+newd.c1+newd.c2);
			}
			
			else {
				//System.out.println("rectangle");
				newd.c1 = keytable[rowone][coltwo];
				newd.c2 = keytable[rowtwo][colone];
				System.out.println("Rectangle replacing with: "+newd.c1+newd.c2);
			}
			
			ddigraphs.add(newd);
		}
		
	}
	
	private static void decryptDigraphList() {
		char one;
		int rowone = -1;
		int colone =-1;
		char two;
		int rowtwo =-2;
		int coltwo =-2;
		
		for(Digraph d:cdigraphs) {
			one = d.c1;
			two = d.c2;
			System.out.println("Checking digraph: "+one+two);
			rowone = rowval.get(one);
			colone = colval.get(one);
			rowtwo = rowval.get(two);
			coltwo = colval.get(two);
			//System.out.println(one+" row "+rowone+" col " +colone+" "+two+" row "+rowtwo+" col "+coltwo);
		
			Digraph newd = new Digraph();
			if(rowone == rowtwo) {
				//System.out.println("rows match");
				if(colone == 0 && coltwo == 0) {
					newd.c1 = keytable[rowone][4];
					newd.c2 = keytable[rowtwo][4];
				}
				else if (colone == 0 && coltwo != 0) {
					newd.c1 = keytable[rowone][4];
					newd.c2 = keytable[rowtwo][(coltwo-1)%5];
				}
				else if (colone != 0 && coltwo == 0) {
					newd.c1 = keytable[rowone][(colone-1)%5];
					newd.c2 = keytable[rowtwo][4];
				}
				else {
					newd.c1 = keytable[rowone][(colone-1)%5];
					newd.c2 = keytable[rowtwo][(coltwo-1)%5];
				}
				System.out.println("Row replacing with: "+newd.c1+newd.c2);
				//System.out.println(newd.c1+" row "+rowone+" col "+((colone+1)%5));
			}
			
			else if(colone == coltwo) {
				//System.out.println("cols match");
				if(rowone == 0 && rowtwo != 0) {
					newd.c1 = keytable[4][colone];
					newd.c2 = keytable[(rowtwo-1)%5][coltwo];
				}
				else if(rowone != 0 && rowtwo == 0) {
					newd.c1 = keytable[(rowone-1)%5][colone];
					newd.c2 = keytable[4][coltwo];
				}
				else if(rowone == 0 && rowtwo == 0) {
					newd.c1 = keytable[4][colone];
					newd.c2 = keytable[4][coltwo];
				}
				else {
					newd.c1 = keytable[(rowone-1)%5][colone];
					newd.c2 = keytable[(rowtwo-1)%5][coltwo];
				}
				System.out.println("Column replacing with: "+newd.c1+newd.c2);
			}
			
			else {
				//System.out.println("rectangle");
				newd.c1 = keytable[rowone][coltwo];
				newd.c2 = keytable[rowtwo][colone];
				System.out.println("Rectangle replacing with: "+newd.c1+newd.c2);
			}
			
			ddigraphs.add(newd);
		}
		
	}
	
	private static String removeDuplicates(String str) {
	     
	    Set<Character> set = new LinkedHashSet<Character>();
	     
	    for(int i=0; i< str.length(); i++) {
	        set.add(str.charAt(i));
	    }
	     
	    StringBuilder sb = new StringBuilder();
	    for(Character character: set) {
	        sb.append(character);
	    }
	     
	    return sb.toString();
	}
	
	

}

class Digraph {
	public char c1;
	public char c2;
	public int row;
	public int col;
	
	public Digraph(char c1, char c2) {
		this.c1 = c1;
		this.c2 = c2;
	}
	
	public Digraph() {
		//
	}
}
