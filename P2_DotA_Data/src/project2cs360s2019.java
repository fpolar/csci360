import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class project2cs360s2019 {

	static boolean ab_pruning;
	static int numHeroes;
	static SortedMap<Integer, Hero> heroes;

	static boolean testing = true;
	static boolean debug_in = false;
	static boolean debug_drafting = false;
	static boolean debug_minimax = false;
	static boolean debug_ab = false;
	static boolean debug_time = true;
	static boolean debug_solution = true;
	static int max_depth = 200;

	public static void main(String[] args) {
		if (testing) {
			long s = System.currentTimeMillis();
			for (int test_num = 0; test_num < 10; test_num++) {
				long start = System.currentTimeMillis();
				readInput("input" + test_num + ".txt");
				int result = simulateDraft();
				if(debug_solution) checkResult(result, test_num);
				if (debug_time)
					System.out.println(
							test_num + " - Runtime: " + (System.currentTimeMillis() - start) / 1000 + " Seconds");
			}
			if (debug_time)
				System.out.println("Total Runtime: " + (System.currentTimeMillis() - s) / 1000 + " Seconds");
		} else {
			readInput("input.txt");
			writeOutputFile(simulateDraft());
		}
	}

	public static int simulateDraft() {
		DraftState root = new DraftState(heroes);
		if (debug_in)
			printHeroes(heroes);
		
		if(!ab_pruning) {
			minimax(root, 1);
		}else {
			alphabeta(root, Double.MIN_VALUE, Double.MAX_VALUE);
		}
		
		return root.getNext_draft();
	}

	public static void writeOutputFile(int result) {
		FileWriter fw;
		try {
			fw = new FileWriter("output.txt");
			PrintWriter pw = new PrintWriter(fw);
			pw.print(result);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TODO remove int parameter that im using for testing
	public static double minimax(DraftState root, int depth) {
		if (depth > max_depth) {
			return -1;
		}

		if (root.draftOver()) {
			return root.getAdvantage();
		}
		
		//maximizing
		if(root.isPlayer_turn()) {
			Queue<DraftState> drafts = new PriorityQueue<DraftState>(new ReverseAdvantageComparator());
			drafts.addAll(root.createSuccessors(heroes));
			root.setMinimax_val(Double.MIN_VALUE);
			while (!drafts.isEmpty()) {
				DraftState curr_draft_state = drafts.poll();
				double curr_minimax_val = minimax(curr_draft_state, depth+1);
				if(root.getMinimax_val() <= curr_minimax_val) {
					root.setMinimax_val(curr_minimax_val);
					root.setNext_draft(curr_draft_state.getLast_draft());
				}
			}
		}
		//minimizing
		else {
			Queue<DraftState> drafts = new PriorityQueue<DraftState>(new AdvantageComparator());
			drafts.addAll(root.createSuccessors(heroes));
			root.setMinimax_val(Double.MAX_VALUE);
			while (!drafts.isEmpty()) {
				DraftState curr_draft_state = drafts.poll();
				double curr_minimax_val = minimax(curr_draft_state, depth+1);
				if(root.getMinimax_val() >= curr_minimax_val) {
					root.setMinimax_val(curr_minimax_val);
					root.setNext_draft(curr_draft_state.getLast_draft());
				}
			}
		}

		return root.getMinimax_val();
	}
	
	public static double alphabeta(DraftState root, double alpha, double beta) {
		if (root.draftOver()) {
			return root.getAdvantage();
		}			   
		//maximizing
		if(root.isPlayer_turn()) {
			Queue<DraftState> drafts = new PriorityQueue<DraftState>(new ReverseAdvantageComparator());
			drafts.addAll(root.createSuccessors(heroes));
			root.setMinimax_val(Double.MIN_VALUE);
			while (!drafts.isEmpty()) {
				DraftState curr_draft_state = drafts.poll();
				if(debug_ab) System.out.println(curr_draft_state);
				double curr_minimax_val = alphabeta(curr_draft_state, alpha, beta);
				if(curr_minimax_val > alpha) {
					root.setMinimax_val(curr_minimax_val);
					root.setNext_draft(curr_draft_state.getLast_draft());
					alpha = curr_minimax_val;
				}
				if(alpha >= beta) {
					break;
				}
			}
			return alpha;
		}
		//minimizing
		else {
			Queue<DraftState> drafts = new PriorityQueue<DraftState>(new AdvantageComparator());
			drafts.addAll(root.createSuccessors(heroes));
			root.setMinimax_val(Double.MAX_VALUE);
			while (!drafts.isEmpty()) {
				DraftState curr_draft_state = drafts.poll();
				if(debug_ab) System.out.println(curr_draft_state);
				double curr_minimax_val = alphabeta(curr_draft_state, alpha, beta);
				if(curr_minimax_val < beta) {
					root.setMinimax_val(curr_minimax_val);
					root.setNext_draft(curr_draft_state.getLast_draft());
					beta = curr_minimax_val;
				}
				if(alpha >= beta) {
					break;
				}
			}
			return beta;
		}
	}

	public static void readInput(String inputFileName) {
		heroes = new TreeMap<Integer, Hero>();

		File file = new File("input.txt");
		if (testing) {
			file = new File("C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P2_DotA_Data\\src\\test_case\\"
					+ inputFileName);
		}

		try {
			Scanner sc = new Scanner(file);
			numHeroes = sc.nextInt();
			sc.nextLine();
			ab_pruning = sc.nextLine().equals("ab");

			for (int i = 0; i < numHeroes; i++) {
				String[] attrs = sc.nextLine().split(",");
				Hero tempHero = new Hero(Double.parseDouble(attrs[1]), Double.parseDouble(attrs[2]),
						Double.parseDouble(attrs[3]), Integer.parseInt(attrs[4]));
				heroes.put(Integer.parseInt(attrs[0]), tempHero);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			if (debug_in)
				System.out.println("Input File Not found");
			e.printStackTrace();
		}

		if (debug_in)
			printHeroes(heroes);

	}


	/*
	 * reads correct output file and compares it to the given result
	 */
	public static void checkResult(int result, int testID) {

		File file = new File(
				"C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P2_DotA_Data\\src\\test_case\\output" + testID
						+ ".txt");

		try {
			Scanner sc = new Scanner(file);
			int correct = sc.nextInt();
			System.out.println("\nResult: " + result + " - Expected: " + correct);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void printHeroes(Map<Integer, Hero> mp) {
		Iterator<Entry<Integer, Hero>> it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>) it.next();
			System.out.println(pair.getKey() + "" + pair.getValue());
		}
	}

	public static void printStates(Map<Double, DraftState> mp) {
		Iterator<Entry<Double, DraftState>> it = mp.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, DraftState> pair = (Map.Entry<Double, DraftState>) it.next();
			System.out.println(pair.getValue());
		}
	}
}

class AdvantageComparator implements Comparator<DraftState> {
	public int compare(DraftState d1, DraftState d2) {
		if (d1.getAdvantage() < d2.getAdvantage()) {
			return 1;
		} else if (d1.getAdvantage() > d2.getAdvantage()) {
			return -1;
		}else if(d1.getLast_draft() > d2.getLast_draft()) {
			return 1;
		}else if(d1.getLast_draft() < d2.getLast_draft()) {
			return -1;
		}
		return 0;
	}
}

class ReverseAdvantageComparator implements Comparator<DraftState> {
	public int compare(DraftState d1, DraftState d2) {
		if (d1.getAdvantage() < d2.getAdvantage()) {
			return -1;
		} else if (d1.getAdvantage() > d2.getAdvantage()) {
			return 1;
		}else if(d1.getLast_draft() < d2.getLast_draft()) {
			return 1;
		}else if(d1.getLast_draft() > d2.getLast_draft()) {
			return -1;
		}
		return 0;
	}
}

class DraftState {
	double advantage;
	double minimax_val;
	boolean player_turn;
	//HashMap<Integer, Integer> hero_states;
//	HashMap<Integer, Hero> player_heroes;
//	HashMap<Integer, Hero> opponent_heroes;
	ArrayList<Integer> player_heroes;
	ArrayList<Integer> opponent_heroes;
	//SortedMap<Double, DraftState> successors;

	//The id of the most recent hero drafted
	int last_draft = -1;
	//The id of the hero to draft next for best result
	int next_draft = -1;
	
	public DraftState(Map<Integer, Hero> heroes) {

		player_turn = true;
		player_heroes = new ArrayList<Integer>();
		opponent_heroes = new ArrayList<Integer>();

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        if(pair.getValue().membership == 1) {
	        	player_heroes.add(pair.getKey());
	        }
	        if(pair.getValue().membership == 2) {
	        	opponent_heroes.add(pair.getKey());
	        }
	    }
	}

	// constructor for cloning
	public DraftState() {
		player_heroes = new ArrayList<Integer>();
		opponent_heroes = new ArrayList<Integer>();
		//successors = new TreeMap<Double, DraftState>();
	}

	public String toString() {
		String out = "Draft State: advantage = "+advantage+", my turn = "+player_turn;
		out += "\n             last draft: "+last_draft+", next best draft: "+next_draft;
		out+= "\n\tPlayer Heroes:";
		for(int h:player_heroes) {
			out+=" "+h;
		}
		out+= "\n\tOpponent Heroes: ";
		for(int h:opponent_heroes) {
			out+=" "+h;
		}
	    return out;
	}

	public DraftState cloneState() {
		DraftState tempDraftState = new DraftState();
		tempDraftState.advantage = advantage;
		tempDraftState.player_turn = player_turn;
		tempDraftState.player_heroes.addAll(player_heroes);
		tempDraftState.opponent_heroes.addAll(opponent_heroes);
		return tempDraftState;
	}
	
	//may be necessary later
	public boolean equals(DraftState d) {
		return false;
	}
	
	public void calcAdvantage(Map<Integer, Hero> heroes) {
		double player_advantage = 0;
		double opponent_advantage = 0;

		boolean[] synergy = new boolean[5];

		for(int h:player_heroes) {
	        Hero hero = heroes.get(h);
	        player_advantage += hero.power*hero.mastery_player;
	        synergy[h%10-1] = true;
	    }
	    if(synergy[0]&&synergy[1]&&synergy[2]&&synergy[3]&&synergy[4]) {
	    	player_advantage+=120;
	    }

		synergy = new boolean[5];
		for(int h:opponent_heroes) {
	        Hero hero = heroes.get(h);
	        opponent_advantage += hero.power*hero.mastery_opponent;
	        synergy[h%10-1] = true;
	    }
	    if(synergy[0]&&synergy[1]&&synergy[2]&&synergy[3]&&synergy[4]) {
	    	opponent_advantage+=120;
	    }
	    
	    advantage = player_advantage - opponent_advantage;
	}
	
	public boolean draftHero(int h, Hero hero) {
		if(player_turn) {
			if(player_heroes.size()>=5 || player_heroes.contains(h) || opponent_heroes.contains(h) ) {
				return false;
			}else {
				player_heroes.add(h);
			}
		}else {
			if(opponent_heroes.size()>=5 || opponent_heroes.contains(h) || player_heroes.contains(h)) {
				return false;
			}else {
				opponent_heroes.add(h);
			}
		}
		player_turn = !player_turn;
		last_draft = h;
		return true;
	}
	
	public ArrayList<DraftState> createSuccessors(Map<Integer, Hero> heroes) {
		
		ArrayList<DraftState> successors;
		if(player_turn) {
			successors= new ArrayList<DraftState>();
		}else {
			successors = new ArrayList<DraftState>();
		}

	    Iterator<Entry<Integer, Hero>> it = heroes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        int id = pair.getKey();
	        Hero hero = pair.getValue();
	        
        	DraftState tempDraftState = cloneState();
        	if(tempDraftState.draftHero(id, hero)) {
        		tempDraftState.setLast_draft(id);
        		tempDraftState.calcAdvantage(heroes);
        		successors.add(tempDraftState);
        	}
	    }
	    return successors;
	}


    public boolean draftOver() {
    	if (player_heroes.size() == 5 && opponent_heroes.size() == 5) {
    		return true;
    	}
    	return false;
    }
       
	public double getAdvantage() {
		return advantage;
	}


	public void setAdvantage(double advantage) {
		this.advantage = advantage;
	}


	public boolean isPlayer_turn() {
		return player_turn;
	}


	public void setPlayer_turn(boolean player_turn) {
		this.player_turn = player_turn;
	}


	public ArrayList<Integer> getPlayer_heroes() {
		return player_heroes;
	}

	public void setPlayer_heroes(ArrayList<Integer> player_heroes) {
		this.player_heroes = player_heroes;
	}

	public ArrayList<Integer> getOpponent_heroes() {
		return opponent_heroes;
	}

	public void setOpponent_heroes(ArrayList<Integer> opponent_heroes) {
		this.opponent_heroes = opponent_heroes;
	}

	public int getLast_draft() {
		return last_draft;
	}

	public void setLast_draft(int last_draft) {
		this.last_draft = last_draft;
	}

	public int getNext_draft() {
		return next_draft;
	}

	public void setNext_draft(int next_draft) {
		this.next_draft = next_draft;
	}

	public double getMinimax_val() {
		return minimax_val;
	}

	public void setMinimax_val(double minimax_val) {
		this.minimax_val = minimax_val;
	}
	
}

class Hero {
	double power;
	double mastery_player;
	double mastery_opponent;
	int membership;
	
	public Hero(double power, double mastery_player, double mastery_opponent, int membership) {
		this.power = power;
		this.mastery_player = mastery_player;
		this.mastery_opponent = mastery_opponent;
		this.membership = membership;
	}
	
	public String toString() {
		return "\n\tpower = "+power+
			   "\n\tmaster_player = "+mastery_player+
			   "\n\tmastery_opponent = "+mastery_opponent+
			   "\n\tmemership = "+membership;
	}
}

