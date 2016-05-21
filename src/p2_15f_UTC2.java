import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Stack;
/* Author: 		Ulenn Terry Chern
 * UCID/Email:	utc2@njit.edu
 * SID Number:	31345003
 * Class/Sect:	CS341-001
 * Assignment:	Project 2 - Pushdown Automata Equation Parser
 */

public class p2_15f_UTC2 {
	
	private static boolean process(Scanner s, String choice){
	// Helper function for dealing with user choice of yes/no
		System.out.print("Would you like to parse an equation (y/n)?");
		choice = s.next();
		while((choice.toLowerCase()).charAt(0) != 'y' && (choice.toLowerCase()).charAt(0) != 'n'){
			System.out.println("Invalid choice, please type y or n.");
			choice = s.next();
		}
		return (choice.toLowerCase()).charAt(0) == 'y'? true : false;
	}
	
	private static void printStack(Stack stack){
		String contents = "";
		for(Object element: stack){
			contents = element.toString() + " " + contents;
		}
		contents = "Current Stack: " + contents + "\n";
		System.out.println(contents);
	}
	
	private static boolean parse(String s){
		// Switch containing all the different states and redirections through the DFA
		int size = s.length();
		Stack stack = new Stack();
		boolean accept = false;
		int state = 0;
		String os = s;			// original string for output
		s = s.toLowerCase();	// In case someone tries to mess with the program
		System.out.printf("Initial State:q0 | ");

		pda: {
			for (int i = 0; i <= size; i++){
				printStack(stack);
				accept = false;		// As long as we are reading in letters, reset accept state to false
				switch(state){
				case 0: // Starting state - Read '$', Pop Nothing, Push 'S$'
					if(s.charAt(i) == '$'){
						state = 1;
						stack.push("$");
						stack.push("S");
					} else {	// Reading anything else kicks you to trap state
						state = 7;
					}
					break;
				case 1:	// Can either read a letter or (
					if(String.valueOf(s.charAt(i)).matches("[a-zA-Z]")){	// If a letter is read
						if(stack.peek() == "S"){							// and the stack contains an 'S', pop the 'S' and replace with a 'T'
							stack.pop();
							stack.push("T");
							state = 2;
						} else {
							state = 7;
						}
					} else if(s.charAt(i) == '('){							// If a '(' is read
						if(stack.peek() == "S"){							// and the stack contains an 'S', pop the 'S' and replace with "T)"
							stack.pop();									// which is what is needed to close the '('
							stack.push(")");
							stack.push("T");
							state = 3;
						} else {
							state = 7;
						}
					} else {	// Anything else being read results in going to trap state
						state = 7;
					}
					break;
				case 2: // Processing variable names and T?T where ? = +-*/
					if(String.valueOf(s.charAt(i)).matches("[a-zA-Z_0-9]")){	// If a letter, number, or underscore is read we are still building a variable name
						if(stack.peek() == "T" || stack.peek() == "X"){			// Variable names are of the form T->CX, where X->C|N|_|empty
							stack.pop();										// If the top of the stack is 'T' or 'X' we are in the process of building a variable name 
							stack.push("X");									// so we pop and then push the continued part of the variable name onto the stack as 'X'
							state = 2;
						} else {	// Expecting a 'T' or 'X' on stack, if neither should crash
							state = 7;
						}
					} else if(String.valueOf(s.charAt(i)).matches("[+-/*]")){	// If a math operator is found
						if(stack.peek() == "T" || stack.peek() == "X"){			// and the preceding value is of the form 'T' or 'X' we are building an expression 
							stack.pop();										// in the form T->T?T where ? is an operator. From here we pop the start of the expression
							stack.push("T");									// and push the second half of the expression 'T' to the stack.
							state = 4;
						} else {	// Expecting a 'T' or 'X' on stack, if neither should crash
							state = 7;
						}
					} else if(s.charAt(i)==')'){								// If we've found a closing parenthesis
						if(stack.peek() == "T" || stack.peek() == "X"){			// check and make sure we are coming from a valid precursor ('T' or 'X')
							stack.pop();										// Then finish the precursor (pop from the stack) and check to see if we've
							if(stack.peek() == ")"){							// found a closing parenthesis on the stack.
								stack.pop();
								stack.push("T");
								state = 5;
							} else {
								state = 7;										// If not we've gotten a mismatched parenthesis
							}

						} else {	// mismatched parentheses
							state = 7;
						}
					} else if(s.charAt(i) == '$') {
						if(stack.peek() == "T" || stack.peek() == "X"){			// check and make sure we are coming from a valid precursor ('T' or 'X')
							stack.pop();										// Then finish the precursor (pop from the stack) and check to see if we've
							if(stack.peek() == "$"){							// found a closing $ on the stack.
								stack.pop();
								state = 6;
							} else {
								state = 7;										// If not we've not matched up the sequence properly
							}

						} else {	// mismatched parentheses
							state = 7;
						}
					} else {	// Anything else being read results in going to trap state
						state = 7;
					}
					break;
				case 3:	// Dealing with ( and (T
					if(String.valueOf(s.charAt(i)).matches("[a-zA-Z]")){	// If a letter is read
						if(stack.peek() == "T"){							// and the stack contains a 'T' we will begin building a new variable name
							stack.pop();									// which is of the form T->CX, we pop the 'T' and replace it with the 'X'
							stack.push("X");								// having read in the 'C' previously.
							state = 2;
						} else {
							state = 7;
						}
					} else if(s.charAt(i) == '('){							// If a '(' is read
						if(stack.peek() == "T"){							// and the stack contains a 'T', we will be building a new enclosed expression
							stack.pop();									// which is of the form T->(T), we pop the 'T' and replace it with the 'T) 
							stack.push(")");								// which is what is needed to close the '('
							stack.push("T");
							state = 3;
						} else {
							state = 7;
						}
					} else {	// Anything else being read results in going to trap state
						state = 7;
					}
					break;
				case 4:	// Dealing with T->T?(T) and T->T?T
					if(String.valueOf(s.charAt(i)).matches("[a-zA-Z]")){	// If a letter is read
						if(stack.peek() == "T"){							// and the stack contains a 'T' we will begin building a new variable name
							stack.pop();									// which is of the form T->CX, we pop the 'T' and replace it with the 'X'
							stack.push("X");								// having read in the 'C' previously.
							state = 2;
						} else {
							state = 7;
						}
					} else if(s.charAt(i) == '('){							// If a '(' is read
						if(stack.peek() == "T"){							// and the stack contains a 'T', we will be building a new enclosed expression
							stack.pop();									// which is of the form T->(T), we pop the 'T' and replace it with the 'T) 
							stack.push(")");								// which is what is needed to close the '('
							stack.push("T");
							state = 3;
						} else {
							state = 7;
						}
					} else {
						state = 7;
					}
					break;
				case 5: // Dealing with ) and )?T where ? = +-*/ and $
					if(String.valueOf(s.charAt(i)).matches("[+-/*]")){			// If a math operator is found
						if(stack.peek() == "T" || stack.peek() == "X" || stack.peek() == ")"){			// and the preceding value is of the form 'T' or 'X' we are building an expression 
							stack.pop();										// in the form T->T?T where ? is an operator. From here we pop the start of the expression
							stack.push("T");									// and push the second half of the expression 'T' to the stack.
							state = 4;
						} else {	// Expecting a 'T' or 'X' on stack, if neither should crash
							state = 7;
						}
					} else if(s.charAt(i)==')'){								// If we've found a closing parenthesis
						if(stack.peek() == "T" || stack.peek() == "X"){			// check and make sure we are coming from a valid precursor ('T' or 'X')
							stack.pop();										// Then finish the precursor (pop from the stack) and check to see if we've
							if(stack.peek() == ")"){							// found a closing parenthesis on the stack.
								stack.pop();
								stack.push("T");
								state = 5;
							} else {
								state = 7;										
							}
//						} else if (stack.peek() == ")"){						// Dealing with consecutive nested parenthesis
//							stack.pop();
//							state = 5;
						} else {	// mismatched parentheses
							state = 7;
						}
					} else if(s.charAt(i) == '$') {
						if(stack.peek() == "T" || stack.peek() == "X"){			// check and make sure we are coming from a valid precursor ('T' or 'X')
							stack.pop();										// Then finish the precursor (pop from the stack) and check to see if we've
							if(stack.peek() == "$"){							// found a closing $ on the stack.
								stack.pop();
								state = 6;
							} else {
								state = 7;										// If not we've not matched up the sequence properly
							}

						} else {	// mismatched parentheses
							state = 7;
						}
					}
					break;
				case 6:
					if(stack.isEmpty()){
						accept = true;
					} else {
						accept = false;
						state = 7;
					}
					break pda;
				default:
					accept = false;
					System.out.println("Crashed: Incorrect input.");
					break pda;
					// Trap state; do nothing, leave in state 20
				}
				if(i < size){ System.out.printf("Read: %c | State:q%d | ", os.charAt(i), state); }
			}
			printStack(stack);
		}
		return accept;
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String input = "";
		
		if (args.length == 0){ // No arguments, user input
			System.out.print("Welcome to the equation checker!\n");
			while(process(scanner, input)){
				System.out.print("Please enter an equation:");
				input = scanner.next();
				System.out.println(parse(input) == true? "Accepted\n\n":"Rejected\n\n");
			}
			
		} else if (args.length == 1){ // Accept input file
			try{
				FileInputStream fstream = new FileInputStream(args[0]);
				DataInputStream istream = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(istream));
				int counter = 1;
				while((br.readLine()).charAt(0) == 'y'){
					input = br.readLine();
					System.out.print(counter + ".");
					System.out.println(parse(input) == true? "Accepted\n\n":"Rejected\n\n");
					counter++;
				}
				istream.close();
				fstream.close();
			} catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}
		} else { // Process parameters
			System.out.println("Invalid number of arguments. usage p1_15f_UTC2.java [input.txt]");
		}
		System.out.println("Thanks for using the equation checker!");
	}

}
