/** 

Kevin Saldana
CSCI 316
Project 1 

This class is a lexical analyzer for the tokens defined by the grammar:

<letter> --> a | b | ... | z | A | B | ... | Z 
<digit> --> 0 | 1 | ... | 9
<int> --> {<digit>}+ 
<id> --> <letter> { <letter> | <digit> } 
<float> --> {<digit>}+ "." {<digit>}  |  "." {<digit>}+ 
<floatE> --> <float> (E|e) [+|-] {<digit>}+ 
<add> --> + 
<sub> --> -
<mul> --> * 
<div> --> / 
<incr> --> "++" 
<decr> --> "--" 
<or> --> "||" 
<and> --> "&&" 
<inv> --> ! 
<lt> --> "<" 
<le> --> "<=" 
<gt> --> ">" 
<ge> --> ">=" 
<eq> --> "==" 
<neq> --> "!=" 
<assign> --> = 
<LParen> --> ( 
<RParen> --> ) 
<LBrace> --> { 
<RBrace> --> } 
<semicolon> --> ; 
<comma> --> , 

This class implements a DFA that will accept the above tokens.
The DFA has The following 24 final states represented by enum-type literals:

state     token accepted

Id        identifiers
Int       integers
Float     floats without exponentiation part
FloatE    floats with exponentiation part
Add       +
Sub       -
Mul       *
Div       /
LParen    (
RParen    )
LBrace    {                
RBrace    }              
Semicolon ;                
Comma     ,                  
Or        ||            
And       &&            
Inv       !             
Neq       !=            
Assign    ==                
Eq        =            
Lt        <                               
Le        <=                               
Gt        >            
Ge        >=

For EXTRA-CREDIT 35 additional final states were 
added for keywords:
int, float, boolean, if, else, while, do, false, true


The DFA also uses 6 non-final states:

state      string recognized

Start      the empty string
Period     float parts ending with "."
E          float parts ending with E or e
EPlusMinus float parts ending with + or - in exponentiation part
Bar        "|" 
Ampersand  "&"





The states are represented by an Enum type called "State".
The function "driver" is the driver to operate the DFA. 
The function "nextState" returns the next state given
the current state and the input character.

To modify this lexical analyzer to recognize a different token set,
the functions "nextState", "isFinal" and the enum type "State" need to be modified;
the function "driver" and the other utility functions remain the same.

**/

import java.io.*;

public abstract class lexArith
{
	public enum State 
       	{ 
	  // non-final states     ordinal number

		Start,             // 0
		Period,            // 1
		E,                 // 2
		EPlusMinus,        // 3
		Bar,			   // 4
		Ampersand,         // 5

	  // final states

		Id,                // 6
		Int,               // 7
		Float,             // 8
		FloatE,            // 9
		Incr,              // 10
		Decr,              // 11 
		Add,               // 12
		Sub,               // 14 
		Mul,               // 15
		Div,               // 16     
		LParen,            // 17    
		RParen,            // 18        
		LBrace,            // 19       
		RBrace,            // 20       
		Semicolon,         // 21          
		Comma,             // 22      
		Or,                // 23   
		And,               // 24    
		Inv,               // 25    
		Neq,               // 26    
		Assign,            // 27       
		Eq,                // 28   
		Lt,                // 29                      
		Le,                // 30                      
		Gt,                // 31   
		Ge,                // 32 
		
		//Keyword final states
		
		Keyword_int,       // 33     
		Keyword_float,     // 34                   		
		Keyword_boolean,   // 35         
		Keyword_if,        // 36    		
		Keyword_else,      // 37      
		Keyword_while,     // 38       
		Keyword_do,        // 39                
		Keyword_false,     // 40       
		Keyword_true,      // 41
		
		//final states of each letter in keywords
		
		id_i,              // 42           
		int_n,             // 43           
		id_f,              // 44          
		float_l,           // 45             
		float_o,           // 46             
		float_a,           // 47             
		id_b,              // 48          
		boolean_o,         // 49               
		boolean_oo,        // 50                
		boolean_l,         // 51                           
		boolean_e,         // 52               
		boolean_a,         // 53               
		id_e,              // 54          
		else_l,            // 55            
		else_s,            // 56            
		id_w,              // 57          
		id_h,              // 58          
		while_i,           // 59                         
		while_l,           // 60             
		false_a,           // 61             
		false_l,           // 62             
		false_s,           // 63             
		id_d,              // 64          
		id_t,              // 65          
		id_r,              // 66           
		id_u,              // 67          
		
		UNDEF
	}

	// By enumerating the non-final states first and then the final states,
	// test for a final state can be done by testing if the state's ordinal number
	// is greater than or equal to that of Id.

	public static String t; // holds an extracted token
	public static State state; // the current state of the FA
	private static int a; // the current input character
	private static char c; // used to convert the variable "a" to 
	                       // the char type whenever necessary
	private static BufferedReader inStream;
	private static PrintWriter outStream;

	private static int getNextChar()

	// Returns the next character on the input stream.

	{
		try
		{
			return inStream.read();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return -1;
		}
	} //end getNextChar

	private static int getChar()

	// Returns the next non-whitespace character on the input stream.
	// Returns -1, end-of-stream, if the end of the input stream is reached.

	{
		int i = getNextChar();
		while ( Character.isWhitespace((char) i) )
			i = getNextChar();
		return i;
	} // end getChar

	private static int driver()

	// This is the driver of the FA. 
	// If a valid token is found, assigns it to "t" and returns 1.
	// If an invalid token is found, assigns it to "t" and returns 0.
	// If end-of-stream is reached without finding any non-whitespace character, returns -1.

	{
		State nextState; // the next state of the FA

		t = "";
		state = State.Start;

		if ( Character.isWhitespace((char) a) )
			a = getChar(); // get the next non-whitespace character
		if ( a == -1 ) // end-of-stream is reached
			return -1;

		while ( a != -1 ) // while "a" is not end-of-stream
		{
			c = (char) a;
			nextState = nextState( state, c );
			if ( nextState == State.UNDEF ) // The FA will halt.
			{
				if ( isFinal(state) )
					return 1; // valid token extracted
				else // "c" is an unexpected character
				{
					t = t+c;
					a = getNextChar();
					return 0; // invalid token found
				}
			}
			else // The FA will go on.
			{
				state = nextState;
				t = t+c;
				a = getNextChar();
			}
		}

		// end-of-stream is reached while a token is being extracted

		if ( isFinal(state) )
			return 1; // valid token extracted
		else
			return 0; // invalid token found
	} // end driver

	private static State nextState(State s, char c)

	// Returns the next state of the FA given the current state and input char;
	// if the next state is undefined, UNDEF is returned.

	{
		switch( state )
		{
		case Start:
			if(c == 'i')
				return State.id_i;
			else if ( c == 'f' )
				return State.id_f;
			else if ( c == 'b' )
				return State.id_b;
			else if ( c == 'e' )
				return State.id_e;
			else if ( c == 'w' )
				return State.id_w;
			else if ( c == 'd' )
				return State.id_d;
			else if ( c == 't' )
				return State.id_t;
			else if ( Character.isLetter(c) )
				return State.Id;
			else if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '+' )
				return State.Add;
			else if ( c == '-' )
				return State.Sub;
			else if ( c == '*' )
				return State.Mul;
			else if ( c == '/' )
				return State.Div;
			else if ( c == '(' )
				return State.LParen;
			else if ( c == ')' )
				return State.RParen;
			else if ( c == '{' )
				return State.LBrace;
			else if ( c == '}' )
				return State.RBrace;
			else if ( c == ';' )
				return State.Semicolon;
			else if ( c == ',' )
				return State.Comma;
			else if ( c == '!' )
				return State.Inv;
			else if ( c == '=' )
				return State.Assign;
			else if ( c == '<' )
				return State.Lt;
			else if ( c == '>' )
				return State.Gt;
			else if ( c == '.' )
				return State.Period;
			else if ( c == '|' )
				return State.Bar;
			else if ( c == '&' )
				return State.Ampersand;			
			else
				return State.UNDEF;
		case Id:
			if ( Character.isLetterOrDigit(c) )
				return State.Id;
			else
				return State.UNDEF;
		case Int:
			if ( Character.isDigit(c) )
				return State.Int;
			else if ( c == '.' )
				return State.Float; 
			else
				return State.UNDEF;
		case Period:
			if ( Character.isDigit(c) )
				return State.Float;
			else
				return State.UNDEF;
		case Float:
			if ( Character.isDigit(c) )
				return State.Float;
			else if ( c == 'e' || c == 'E' )
				return State.E;
			else
				return State.UNDEF;
		case E:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else if ( c == '+' || c == '-' )
				return State.EPlusMinus;
			else
				return State.UNDEF;
		case EPlusMinus:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else
				return State.UNDEF;
		case FloatE:
			if ( Character.isDigit(c) )
				return State.FloatE;
			else
				return State.UNDEF;
		case Add:
			if ( c == '+' )
				return State.Incr;
			else
				return State.UNDEF;
		case Sub:
			if ( c == '-' )
				return State.Decr;
			else
				return State.UNDEF;	
		case Bar:
			if ( c == '|')
				return State.Or;
			else
				return State.UNDEF;
		case Ampersand:
			if ( c == '&' )
				return State.And;
			else
				return State.UNDEF;			
		case Inv:
			if ( c == '=' )
				return State.Neq;
			else
				return State.UNDEF;	
		case Assign:
			if ( c == '=' )
				return State.Eq;
			else
				return State.UNDEF;
		case Lt:
			if ( c == '=' )
				return State.Le;
			else
				return State.UNDEF;
		case Gt:
			if ( c == '=' )
				return State.Ge;
			else
				return State.UNDEF;
		case id_i:
			if ( c == 'f' )
				return State.Keyword_if;
			else if (c == 'n')
				return State.int_n;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;	
		case Keyword_if:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case int_n:
			if (c == 't')
				return State.Keyword_int;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_int:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_f:
			if ( c == 'l' )
				return State.float_l;
			else if (c == 'a')
				return State.false_a;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case float_l:
			if (c == 'o')
				return State.float_o;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case float_o:
			if (c == 'a')
				return State.float_a;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case float_a:
			if (c == 't')
				return State.Keyword_float;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_float:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_b:
			if ( c == 'o' )
				return State.boolean_o;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case boolean_o:
			if ( c == 'o' )
				return State.boolean_oo;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case boolean_oo:
			if ( c == 'l' )
				return State.boolean_l;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case boolean_l:
			if ( c == 'e' )
				return State.boolean_e;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case boolean_e:
			if ( c == 'a' )
				return State.boolean_a;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case boolean_a:
			if ( c == 'n' )
				return State.Keyword_boolean;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_boolean:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_e:
			if ( c == 'l' )
				return State.else_l;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case else_l:
			if ( c == 's' )
				return State.else_s;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case else_s:
			if ( c == 'e' )
				return State.Keyword_else;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_else:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_w:
			if ( c == 'h' )
				return State.id_h;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_h:
			if ( c == 'i' )
				return State.while_i;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case while_i:
			if ( c == 'l' )
				return State.while_l;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case while_l:
			if ( c == 'e' )
				return State.Keyword_while;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_while:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_d:
			if ( c == 'o' )
				return State.Keyword_do;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_do:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case false_a:
			if (c == 'l')
				return State.false_l;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case false_l:
			if (c == 's')
				return State.false_s;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case false_s:
			if (c == 'e')
				return State.Keyword_false;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_false:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_t:
			if ( c == 'r' )
				return State.id_r;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_r:
			if ( c == 'u' )
				return State.id_u;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case id_u:
			if ( c == 'e' )
				return State.Keyword_true;
			else if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		case Keyword_true:
			if (Character.isLetterOrDigit(c))
				return State.Id;
			else
				return State.UNDEF;
		default:
			return State.UNDEF;
		}
	} // end nextState

	private static boolean isFinal(State state)
	{
		return ( state.compareTo(State.Id) >= 0 );  
	}
	
	public static void getToken()

	// Extract the next token using the driver of the FA.
	// If an invalid token is found, issue an error message.

	{
		int i = driver();
		if ( i == 0 )
			displayln(t + "  -- Invalid Token");
	} // end getToken

	public static void display(String s)
	{
		outStream.print(s);
	}

	public static void displayln(String s)
	{
		outStream.println(s);
	}

	public static void setLex(String inFile, String outFile)

	// Sets the input and output streams to "inFile" and "outFile", respectively.
	// Also sets the current input character "a" to the first character on
	// the input stream.

	{
		try
		{
			inStream = new BufferedReader( new FileReader(inFile) );
			outStream = new PrintWriter( new FileOutputStream(outFile) );
			a = inStream.read();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	} // end setIO

	public static void closeIO()
	{
		try
		{
			inStream.close();
			outStream.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	} // end closeIO

	public static void main(String argv[])

	// The input/output file names must be passed as argv[0] and argv[1].

	{
		int i;

		setLex( argv[0], argv[1] );

		while ( a != -1 ) // while "a" is not end-of-stream
		{
			i = driver(); // extract the next token
			if ( i == 1 )
				displayln( t+"   : "+state.toString() );
			else if ( i == 0 )
				displayln( t+"  -- Invalid Token");
		} 

		closeIO();
	} // end main
} 
