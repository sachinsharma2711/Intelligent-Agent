package gameplay;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class IntelligentAgentNxN {

	private static int[][] cellValues;
	private static char player1;
	private static char player2;
	private static int n;

	final static int STAKE = 0;
	final static int RAID = 1;

	static PrintWriter debug;

	public static void main(String[] args) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("input.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String mode;
		int depth = 0;
		char[][] state;
		Location location = null;
		try {
			n = Integer.parseInt(br.readLine().trim());
			mode = br.readLine().trim();
			player1 = br.readLine().trim().charAt(0);
			player2 = 'O';
			depth = Integer.parseInt(br.readLine().trim());

			state = new char[n][n];
			cellValues = new int[n][n];

			for (int i = 0; i < n; i++) {
				String[] values = br.readLine().trim().split(" ");
				for (int j = 0; j < values.length; j++) {
					cellValues[i][j] = Integer.parseInt(values[j]);
				}
			}
			for (int i = 0; i < n; i++) {
				String values = br.readLine().trim();
				for (int j = 0; j < n; j++) {
					state[i][j] = values.charAt(j);
				}
			}
			br.close();
			if (player1 == 'O') {
				player2 = 'X';
			}
			debug = new PrintWriter("debug.txt", "UTF-8");
			if(mode.equals("MINIMAX")){
				location = minimax(state, depth);
			} else if(mode.equals("ALPHABETA")){
				location = alphabeta(state,depth);
			}
			debug.close();
			state[location.x][location.y] = player1;
			String moveType = "Stake";
			if (location.moveType == RAID) {
				moveType = "Raid";
				if (location.x > 0 && state[location.x - 1][location.y] == player2) {
					state[location.x - 1][location.y] = player1;
				}
				if (location.y > 0 && state[location.x][location.y - 1] == player2) {
					state[location.x][location.y - 1] = player1;
				}
				if (location.x < n - 1 && state[location.x + 1][location.y] == player2) {
					state[location.x + 1][location.y] = player1;
				}
				if (location.y < n - 1 && state[location.x][location.y + 1] == player2) {
					state[location.x][location.y + 1] = player1;
				}
			}
			PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
			char c = (char) (location.y + 'A');
			writer.println(c + "" + (location.x + 1) + " " + moveType);
			for (int i = 0; i < n; i++) {
				writer.println(state[i]);
			}
			writer.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	private static Location minimax(char[][] state, int depth) {
		int x = -1, y = -1;
		int utility = Integer.MIN_VALUE;
		Location location;
		int moveType = -1;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					state[i][j] = player1;
					int value = minValue(state, depth - 1);
					if (value > utility) {
						utility = value;
						x = i;
						y = j;
						moveType = STAKE;
					}
					state[i][j] = '.';
				}
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					if ((i > 0 && state[i - 1][j] == player1) || (j > 0 && state[i][j - 1] == player1)
							|| (i < n - 1 && state[i + 1][j] == player1) || (j < n - 1 && state[i][j + 1] == player1)) {
						state[i][j] = player1;
						boolean top = false, bottom = false, left = false, right = false;
						if (i > 0 && state[i - 1][j] == player2) {
							state[i - 1][j] = player1;
							top = true;
						}
						if (j > 0 && state[i][j - 1] == player2) {
							state[i][j - 1] = player1;
							left = true;
						}
						if (i < n - 1 && state[i + 1][j] == player2) {
							state[i + 1][j] = player1;
							bottom = true;
						}
						if (j < n - 1 && state[i][j + 1] == player2) {
							state[i][j + 1] = player1;
							right = true;
						}
						int value = minValue(state, depth - 1);
						if (value > utility) {
							utility = value;
							x = i;
							y = j;
							moveType = RAID;
						}
						if (top) {
							state[i - 1][j] = player2;
						}
						if (left) {
							state[i][j - 1] = player2;
						}
						if (bottom) {
							state[i + 1][j] = player2;
						}
						if (right) {
							state[i][j + 1] = player2;
						}
					}
					state[i][j] = '.';
				}
			}
		}
		location = new Location(x, y);
		location.setMoveType(moveType);
		return location;
	}

	private static int maxValue(char[][] state, int depth) {
		int value = Integer.MIN_VALUE;
		boolean emptyPositionAvailable = false;
		if (depth == 0) {
			return utility(state);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					emptyPositionAvailable = true;
					state[i][j] = player1;
					int v = minValue(state, depth - 1);
					if (v > value) {
						value = v;
					}
					state[i][j] = '.';
				}
			}
		}
		if(!emptyPositionAvailable){
			return utility(state);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					if ((i > 0 && state[i - 1][j] == player1) || (j > 0 && state[i][j - 1] == player1)
							|| (i < n - 1 && state[i + 1][j] == player1) || (j < n - 1 && state[i][j + 1] == player1)) {
						state[i][j] = player1;
						boolean top = false, bottom = false, left = false, right = false;
						if (i > 0 && state[i - 1][j] == player2) {
							state[i - 1][j] = player1;
							top = true;
						}
						if (j > 0 && state[i][j - 1] == player2) {
							state[i][j - 1] = player1;
							left = true;
						}
						if (i < n - 1 && state[i + 1][j] == player2) {
							state[i + 1][j] = player1;
							bottom = true;
						}
						if (j < n - 1 && state[i][j + 1] == player2) {
							state[i][j + 1] = player1;
							right = true;
						}
						int v = minValue(state, depth - 1);
						if (v > value) {
							value = v;
						}
						if (top) {
							state[i - 1][j] = player2;
						}
						if (left) {
							state[i][j - 1] = player2;
						}
						if (bottom) {
							state[i + 1][j] = player2;
						}
						if (right) {
							state[i][j + 1] = player2;
						}
					}
					state[i][j] = '.';
				}
			}
		}
		return value;
	}

	private static int minValue(char[][] state, int depth) {
		int value = Integer.MAX_VALUE;
		boolean emptyPositionAvailable = false;
		if (depth == 0) {
			return utility(state);
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					emptyPositionAvailable = true;
					state[i][j] = player2;
					int v = maxValue(state, depth - 1);
					if (v < value) {
						value = v;
					}
					state[i][j] = '.';
				}
			}
		}
		if(!emptyPositionAvailable){
			return utility(state);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					if ((i > 0 && state[i - 1][j] == player2) || (j > 0 && state[i][j - 1] == player2)
							|| (i < n - 1 && state[i + 1][j] == player2) || (j < n - 1 && state[i][j + 1] == player2)) {
						state[i][j] = player2;
						boolean top = false, bottom = false, left = false, right = false;
						if (i > 0 && state[i - 1][j] == player1) {
							state[i - 1][j] = player2;
							top = true;
						}
						if (j > 0 && state[i][j - 1] == player1) {
							state[i][j - 1] = player2;
							left = true;
						}
						if (i < n - 1 && state[i + 1][j] == player1) {
							state[i + 1][j] = player2;
							bottom = true;
						}
						if (j < n - 1 && state[i][j + 1] == player1) {
							state[i][j + 1] = player2;
							right = true;
						}
						int v = maxValue(state, depth - 1);
						if (v < value) {
							value = v;
						}
						if (top) {
							state[i - 1][j] = player1;
						}
						if (left) {
							state[i][j - 1] = player1;
						}
						if (bottom) {
							state[i + 1][j] = player1;
						}
						if (right) {
							state[i][j + 1] = player1;
						}
					}
					state[i][j] = '.';
				}
			}
		}
		return value;
	}
	
	private static int utility(char[][] state) {
		int score = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == player1) {
					score += cellValues[i][j];
				} else if (state[i][j] == player2) {
					score -= cellValues[i][j];
				}
			}
		}
		return score;
	}

	private static Location alphabeta(char[][] state, int depth) {
		int x = -1, y = -1;
		int utility = Integer.MIN_VALUE;
		Location location;
		int moveType = -1;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					state[i][j] = player1;
					int value = alphabetaMinValue(state, depth - 1, alpha, beta);
					if (value > utility) {
						utility = value;
						x = i;
						y = j;
						moveType = STAKE;
					}
					state[i][j] = '.';
					if(utility >= beta){
						location = new Location(x, y);
						location.setMoveType(moveType);
						return location;
					}
					if(utility > alpha){
						alpha = utility;
					}
				}
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					if ((i > 0 && state[i - 1][j] == player1) || (j > 0 && state[i][j - 1] == player1)
							|| (i < n - 1 && state[i + 1][j] == player1) || (j < n - 1 && state[i][j + 1] == player1)) {
						state[i][j] = player1;
						boolean top = false, bottom = false, left = false, right = false;
						if (i > 0 && state[i - 1][j] == player2) {
							state[i - 1][j] = player1;
							top = true;
						}
						if (j > 0 && state[i][j - 1] == player2) {
							state[i][j - 1] = player1;
							left = true;
						}
						if (i < n - 1 && state[i + 1][j] == player2) {
							state[i + 1][j] = player1;
							bottom = true;
						}
						if (j < n - 1 && state[i][j + 1] == player2) {
							state[i][j + 1] = player1;
							right = true;
						}
						int value = alphabetaMinValue(state, depth - 1, alpha, beta);
						if (value > utility) {
							utility = value;
							x = i;
							y = j;
							moveType = RAID;
						}
						if (top) {
							state[i - 1][j] = player2;
						}
						if (left) {
							state[i][j - 1] = player2;
						}
						if (bottom) {
							state[i + 1][j] = player2;
						}
						if (right) {
							state[i][j + 1] = player2;
						}
					}
					state[i][j] = '.';
					if(utility >= beta){
						location = new Location(x, y);
						location.setMoveType(moveType);
						return location;
					}
					if(utility > alpha){
						alpha = utility;
					}
				}
			}
		}
		location = new Location(x, y);
		location.setMoveType(moveType);
		return location;
	}

	private static int alphabetaMaxValue(char[][] state, int depth, int alpha, int beta) {
		int value = Integer.MIN_VALUE;
		boolean emptyPositionAvailable = false;
		if (depth == 0) {
			return utility(state);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					emptyPositionAvailable = true;
					state[i][j] = player1;
					int v = alphabetaMinValue(state, depth - 1, alpha, beta);
					if (v > value) {
						value = v;
					}
					state[i][j] = '.';
					if(value >= beta)
						return value;
					if(value > alpha){
						alpha = value;
					}
				}
			}
		}
		if(!emptyPositionAvailable){
			return utility(state);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					if ((i > 0 && state[i - 1][j] == player1) || (j > 0 && state[i][j - 1] == player1)
							|| (i < n - 1 && state[i + 1][j] == player1) || (j < n - 1 && state[i][j + 1] == player1)) {
						state[i][j] = player1;
						boolean top = false, bottom = false, left = false, right = false;
						if (i > 0 && state[i - 1][j] == player2) {
							state[i - 1][j] = player1;
							top = true;
						}
						if (j > 0 && state[i][j - 1] == player2) {
							state[i][j - 1] = player1;
							left = true;
						}
						if (i < n - 1 && state[i + 1][j] == player2) {
							state[i + 1][j] = player1;
							bottom = true;
						}
						if (j < n - 1 && state[i][j + 1] == player2) {
							state[i][j + 1] = player1;
							right = true;
						}
						int v = alphabetaMinValue(state, depth - 1, alpha, beta);
						if (v > value) {
							value = v;
						}
						if (top) {
							state[i - 1][j] = player2;
						}
						if (left) {
							state[i][j - 1] = player2;
						}
						if (bottom) {
							state[i + 1][j] = player2;
						}
						if (right) {
							state[i][j + 1] = player2;
						}
					}
					state[i][j] = '.';
					if(value >= beta)
						return value;
					if(value > alpha){
						alpha = value;
					}
				}
			}
		}
		return value;
	}
	
	private static int alphabetaMinValue(char[][] state, int depth, int alpha, int beta) {
		int value = Integer.MAX_VALUE;
		boolean emptyPositionAvailable = false;
		if (depth == 0) {
			return utility(state);
		}

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					emptyPositionAvailable = true;
					state[i][j] = player2;
					int v = alphabetaMaxValue(state, depth - 1, alpha, beta);
					if (v < value) {
						value = v;
					}
					state[i][j] = '.';
					if(value <= alpha){
						return value;
					}
					if(value < beta){
						beta = value;
					}
				}
			}
		}
		if(!emptyPositionAvailable){
			return utility(state);
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (state[i][j] == '.') {
					if ((i > 0 && state[i - 1][j] == player2) || (j > 0 && state[i][j - 1] == player2)
							|| (i < n - 1 && state[i + 1][j] == player2) || (j < n - 1 && state[i][j + 1] == player2)) {
						state[i][j] = player2;
						boolean top = false, bottom = false, left = false, right = false;
						if (i > 0 && state[i - 1][j] == player1) {
							state[i - 1][j] = player2;
							top = true;
						}
						if (j > 0 && state[i][j - 1] == player1) {
							state[i][j - 1] = player2;
							left = true;
						}
						if (i < n - 1 && state[i + 1][j] == player1) {
							state[i + 1][j] = player2;
							bottom = true;
						}
						if (j < n - 1 && state[i][j + 1] == player1) {
							state[i][j + 1] = player2;
							right = true;
						}
						int v = maxValue(state, depth - 1);
						if (v < value) {
							value = v;
						}
						if (top) {
							state[i - 1][j] = player1;
						}
						if (left) {
							state[i][j - 1] = player1;
						}
						if (bottom) {
							state[i + 1][j] = player1;
						}
						if (right) {
							state[i][j + 1] = player1;
						}
					}
					state[i][j] = '.';
				}
			}
		}
		return value;
	}
}
