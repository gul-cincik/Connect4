package ai.connect4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameState;
import sac.game.GameStateImpl;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

public class Connect4 extends GameStateImpl{
	
	public final byte n1; //row
	public final byte n2;  //column
	
	public byte[][] board = null;
	public static char move = 'x';
	public static boolean check =false;
	
	public Connect4(byte n1, byte n2) {
		board=new byte[n1][n2];
		this.n1=n1;
		this.n2=n2;
	}
	
	public Connect4(Connect4 toCopy) {
		this.n1=toCopy.n1;
		this.n2=toCopy.n2;
		board=new byte[n1][n2];
		for (int i=0;i<n1;i++) {
			for (int j=0;j<n2;j++) {
				board[i][j]=toCopy.board[i][j];
			}
		}
		this.setMaximizingTurnNow(toCopy.isMaximizingTurnNow());
	}
	
	
	public void showGameBoard() {
		
		System.out.println();
		for(int row = 0; row < n1; row++) {
			System.out.print("|");
			for(int col = 0; col < n2; col++) {
				
				if (board[row][col]==0)
					System.out.print(" " + " " + " |");
				
				if (board[row][col]==1)
					System.out.print(" " + "X" + " |");
				
				if (board[row][col]==2)
					System.out.print(" " + "O" + " |");
			}
			System.out.println();
		}

	}
		
		public boolean checkGameBoard(){
			
			for(int j = 0; j < n2; j++) {
				if(board[0][j] != 0) {
					return true;
				}
			}
			//check horizontally
			//System.out.println("stepin");
		
			
			for(int row = 0; row < board.length; row++) {
				for(int col = 0; col < board[row].length-4; col++) {
					if(	board[row][col] != 0 &&
						board[row][col+1] == board[row][col] &&
						board[row][col+2] == board[row][col] &&
						board[row][col+3] == board[row][col]) {
							return true;
						}
				}
			}
			
			//System.out.println("horizontally");
			//check vertically
			for(int col = 0; col < n2; col++) {
				for(int row = 0; row <= n1-4; row++) {
					if(	board[row][col] != 0 &&
						board[row+1][col] == board[row][col] &&
						board[row+2][col] == board[row][col] &&
						board[row+3][col] == board[row][col]) {
						return true;
					}
				}
			}
			//System.out.println("vertically");
			//check diagnolly 1. direction
			
			for(int row =0; row <= n1-4; row++) {
				for(int col = 0; col <= n2-4; col++) {
					if( board[row][col] != 0 &&
						board[row+1][col+1] == board[row][col] &&
					   board[row+2][col+2] == board[row][col] &&
					   board[row+3][col+3] == board[row][col]) {
						return true;
					}
				}
			}
			//System.out.println("1");
			//check diagg 2. directon
			for(int row = 0; row <= n1-4; row++) {
				for(int col = n2-1; col >=3; col--) {
					if(board[row][col] != 0 &&
						board[row+1][col-1] == board[row][col] &&
					   board[row+2][col-2] == board[row][col] &&
					   board[row+3][col-3] == board[row][col]) {
						return true;
					}
				}
			}
			//System.out.println("2");
			return false;
		}
		
		public boolean makeMove(int col) {
			if(board[0][col] != 0) {
				return false;
			}
			for(int row = n1-1; row >= 0; row--) {
				if(board[row][col] == 0) {
					if (isMaximizingTurnNow()==true)
						board[row][col] = 1; //X
					else
						board[row][col] = 2; //Y
					setMaximizingTurnNow(!isMaximizingTurnNow());
					return true;
				}
			}
			return true;
		}
		
		public String toString() {
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < n1; i++) {
				for (int j = 0; j < n2; j++) {
					result.append(board[i][j] + ((j < n1 - 1) ? "," : ""));

				}
				result.append("\n");
			}
			return result.toString();
		}
		
		public static void main(String[] args) throws IOException {
			Connect4 c4=new Connect4((byte)9,(byte)9);
			c4.showGameBoard();
			//System.out.println(c4.toString());
			
			while(true) {
				while(true) {
					BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in),1);
					String line = inReader.readLine();
					int move=Integer.valueOf(line);
					
					if (!c4.makeMove(move)) {
						System.out.println("That column is already full.");
						c4.setMaximizingTurnNow(c4.isMaximizingTurnNow());
					}	
					else
						break;
				}

				c4.showGameBoard();
				
				if (c4.checkGameBoard()) {
					System.out.println("DONE!");
					break;
				}
				
				c4.showGameBoard();
				
				Connect4.setHFunction(new Heuristic());
				GameSearchAlgorithm algorithm=new AlphaBetaPruning(c4);
				algorithm.execute();
				System.out.println(algorithm.getMovesScores().toString());
				
				int bestMove=Integer.parseInt(algorithm.getFirstBestMove());
				//System.out.println("asdfgh"+bestMove);
				
				c4.makeMove(bestMove);
				if (c4.checkGameBoard()) {
					System.out.println("DONE!");
					break;
				}
				
				c4.showGameBoard();
			}
			c4.showGameBoard();
			
		}
		
		
		@Override
		public int hashCode() {
			byte[] toHash = new byte[n1*n2];
			int k=0;
			for (int i = 0; i < n1; i++) {
				for (int j=0;j<n1;j++) {
					toHash[k++]=board[i][j];
				}
			}
			return Arrays.hashCode(toHash);
		}

		@Override
		public List<GameState> generateChildren() {
			
			List<GameState> children = new LinkedList<GameState>();
			
			for (int j=0;j<n2;j++) {
				Connect4 child=new Connect4(this);
				child.makeMove(j);
				child.setMoveName(Integer.toString(j));
				children.add(child);
			}
			
			return children;
		}

		public double boardValue() {
			for (int j=0;j<n2;j++) {
				if (board[0][j]!=0) {
					if (isMaximizingTurnNow())
						return Double.POSITIVE_INFINITY;
					else
						return Double.NEGATIVE_INFINITY;
				}			
			}
			
			double value = 0;
			
			if(isMaximizingTurnNow()) {
				//thýs ýs score for player X
				for(int i = 0; i < n1;i++) {
					for(int j = 0; j<n2; j++) {
						if (board[i][j]==1) {
							if(i==1 || i==2 || i==6 || i==7) {
								value += 0.1;
							}
							if(i==3 || i==5) {
								value += 1.2;
							}
							if(i==4) {
								value += 4.5;
							}
						}
					}

				}
				for(int i=(n1-2); i>1; i--) {
					for(int j =1; j<n2-1; j++) {
						value=checkRight(i,j, value);
						value=checkLeft(i,j, value);
						value=checkDown(i,j, value);
						value=checkUp(i,j, value);
						value=checkRightUp(i,j, value);
						value=checkRightDown(i,j,value);
						value=checkLeftUp(i,j, value);
						value=checkLeftDown(i,j, value);
					}
				}
			}
			else {
				//thýs ýs score for player O
				for(int i = 0; i < n1; i++) {
					for(int j = 0; j<n2; j++) {
						if (board[i][j]==2) {
							if(i==1 || i==2 || i==6 || i==7) {
								value += 0.1;
							}
							if(i==3 || i==5) {
								value += 1.2;
							}
							if(i==4) {
								value += 4.5;
							}
						}
					}
				}
				for(int i=(n1-2); i>1; i--) {
					for(int j =1; j<n2-1; j++) {
						value=checkRight(i,j, value);
						value=checkLeft(i,j, value);
						value=checkDown(i,j, value);
						value=checkUp(i,j, value);
						value=checkRightUp(i,j, value);
						value=checkRightDown(i,j,value);
						value=checkLeftUp(i,j, value);
						value=checkLeftDown(i,j, value);
					}
				}
			}
			return value;
		}
		public double checkRight(int i, int j, double value) {
			while(board[i][j]!=0 && j<9) {
				if(board[i][j] == board[i][j+1]) {
					j++;
					return value=value+1;
				}
				else
					break;
		    }
			return value;
		}
		public double checkLeft(int i, int j, double value) {
			while(board[i][j]!=0 && j>0) {
				if(board[i][j] == board[i][j-1]) {
					j--;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
		public double checkDown(int i, int j, double value) {
			while(board[i][j]!=0 && i<9) {
				if(board[i][j] == board[i+1][j]) {
					i++;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
		public double checkUp(int i, int j, double value) {
			while(board[i][j]!=0 && i>=0) {
				if(board[i][j] == board[i-1][j]) {
					i--;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
		public double checkRightUp(int i, int j, double value) {
			while(board[i][j]!=0 && i >=0 && j<9) {
				if(board[i][j] == board[i-1][j+1]) {
					i--;
					j++;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
		public double checkRightDown(int i, int j, double value) {
			while(board[i][j]!=0 && i <9 && j<9) {
				if(board[i][j] == board[i+1][j+1]) {
					i++;
					j++;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
		public double checkLeftUp(int i, int j, double value) {
			while(board[i][j]!=0 && i>=0 && j>=0) {
				if(board[i][j] == board[i-1][j-1]) {
					i--;
					j--;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
		public double checkLeftDown(int i, int j, double value){
			while(board[i][j]!=0 && i<9 && j>=0) {
				if(board[i][j] == board[i+1][j-1]) {
					i++;
					j--;
					return value=value+1;
				}
				else
					break;
			}
			return value;
		}
}
