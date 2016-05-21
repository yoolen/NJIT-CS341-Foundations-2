import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
/* Author: 		Ulenn Terry Chern
 * UCID/Email:	utc2@njit.edu
 * SID Number:	31345003
 * Class/Sect:	CS341-001
 * Assignment:	Project 1 - URL Parser
 */

public class p1_15f_UTC2 {
	
	private static boolean process(Scanner s, String choice){
	// Helper function for dealing with user choice of yes/no
		System.out.print("Would you like to check a URL (y/n)?");
		choice = s.next();
		while((choice.toLowerCase()).charAt(0) != 'y' && (choice.toLowerCase()).charAt(0) != 'n'){
			System.out.println("Invalid choice, please type y or n.");
			choice = s.next();
		}
		return (choice.toLowerCase()).charAt(0) == 'y'? true : false;
	}
	
	private static boolean parse(String s){
	// Switch containing all the different states and redirections through the DFA
		int size = s.length();
		boolean accept = false;
		int state = 0;
		s = s.toLowerCase();	// In case someone tries to mess with the program
		System.out.printf("q0 ");
		
		for (int i = 0; i < size; i++){
			accept = false;		// As long as we are reading in letters, reset accept state to false
			switch(state){
				case 0: // Starting state
					if(s.charAt(i) == 'w'){
						state = 1;
					} else if(Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 1:
					if(s.charAt(i) == '.'){
						state = 3;
					} else if(s.charAt(i) == 'w'){
						state = 4;
					} else if(Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 2:
					if(s.charAt(i)=='.'){
						state = 3;
					} else if (Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 3:
					if(s.charAt(i)=='c'){
						state = 5;
					} else {
						state = 20;
					}
					break;
				case 4:
					if(s.charAt(i)=='w'){
						state = 6;
					} else if (s.charAt(i)=='.'){
						state = 3;
					} else if (Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 5:
					if(s.charAt(i)=='o'){
						state = 7;
					} else {
						state = 20;
					}
					break;
				case 6:
					if(s.charAt(i)=='.'){
						state = 8;
					} else if (Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 7:
					if(s.charAt(i)=='m'){
						state = 9;
						accept = true;
					} else if (s.charAt(i)=='.'){
						state = 10;
					} else {
						state = 20;
					}
					break;
				case 8:
					if(s.charAt(i)=='c'){
						state = 11;
					} else if (Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 9:
					state = 20;
					break;
				case 10:
					if(s.charAt(i)=='c'){
						state = 12;
					} else if (s.charAt(i)=='u'){
						state = 13;
					} else {
						state = 20;
					}
					break;
				case 11:
					if(s.charAt(i)=='o'){
						state = 14;
					} else if (Character.isLetter(s.charAt(i))){
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 12:
					if(s.charAt(i)=='o'){
						state = 15;
					} else {
						state = 20;
					}
					break;
				case 13:
					if(s.charAt(i)=='k'){
						accept = true;
						state = 15;
					} else {
						state = 20;
					}
					break;
				case 14:
					if(s.charAt(i)=='.'){
						state = 10;
					} else if (s.charAt(i)=='m'){
						accept = true;
						state = 17;
					} else {
						state = 20;
					}
					break;
				case 15:
					if(s.charAt(i)=='m'){
						accept = true;
						state = 9;
					} else if(s.charAt(i)=='.'){
						state = 18;
					}
					break;
				case 16:
					state = 20;
					break;
				case 17:
					if(s.charAt(i)=='.'){
						state = 3;
					} else if(Character.isLetter(s.charAt(i))) {
						state = 2;
					} else {
						state = 20;
					}
					break;
				case 18:
					if(s.charAt(i)=='u'){
						state = 19;
					} else {
						state = 20;
					}
					break;
				case 19:
					if(s.charAt(i)=='k'){
						accept = true;
						state = 16;
					} else {
						state = 20;
					}
					break;
				default:
					// Trap state; do nothing, leave in state 20
			}
			System.out.printf("%c:q%d ", s.charAt(i), state);
		}
		return accept;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String input = "";
		
		if (args.length == 0){ // No arguments, user input
			System.out.print("Welcome to the URL checker!\n");
			while(process(scanner, input)){
				System.out.print("Please enter a URL:");
				input = scanner.next();
				System.out.println(parse(input) == true? "Accepted":"Rejected");
			}
			
		} else if (args.length == 1){ // Accept input file
			try{
				FileInputStream fstream = new FileInputStream(args[0]);
				DataInputStream istream = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(istream));
				while((br.readLine()).charAt(0) == 'y'){
					input = br.readLine();
					System.out.println(parse(input) == true? "Accepted":"Rejected");
				}
				istream.close();
				fstream.close();
			} catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}
		} else { // Process parameters
			System.out.println("Invalid number of arguments. usage p1_15f_UTC2.java [input.txt]");
		}
		System.out.println("Thanks for using the URL checker!");
	}

}
