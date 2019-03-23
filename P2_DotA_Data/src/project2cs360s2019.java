import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;

import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import models.DraftState;
import models.Hero;

public class project2cs360s2019 {

	static boolean ab_pruning;
	static int numHeroes;
	static SortedMap<Integer, Hero> heroes;

	static boolean testing = true;
	static boolean debug_in = false;
	static boolean debug_drafting = true;
	static boolean debug_minimax = false;
	static boolean debug_time = true;
	static boolean debug_solution = true;
	static int max_depth = 200;

	public static void main(String[] args) {
		if (testing) {
			long s = System.currentTimeMillis();
			for (int test_num = 3; test_num < 4; test_num++) {
				long start = System.currentTimeMillis();
				readInput("input" + test_num + ".txt");
				simulateDraft();
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
		minimax(root, 1);
		if (debug_drafting)
			System.out.println("Done Drafting");
		if (debug_solution)
			System.out.println(root.getNext_draft());
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

		/*
		 * if it's currently opponents turn, he's going to try to find the successor
		 * with minimum advant
		 */
		root.setMinimax_val(Double.MAX_VALUE);
		if (root.isPlayer_turn()) {
			root.setMinimax_val(Double.MIN_VALUE);
		}

		Queue<DraftState> drafts = new PriorityQueue<DraftState>(new AdvantageComparator());
//		Queue<DraftState> drafts = new LinkedList<DraftState>();
		if (root.isPlayer_turn())
			drafts = new PriorityQueue<DraftState>(new ReverseAdvantageComparator());
		drafts.addAll(root.createSuccessors(heroes));
		if (debug_minimax)
			System.out.println("Drafts Starting!\nqueue size: " + drafts.size());
		while (!drafts.isEmpty()) {
			DraftState currDraftRound = drafts.poll();

			if (debug_drafting) {// && (currDraftRound.getPlayer_heroes().get(2)==26604)) {
				System.out.println(currDraftRound);
			}
			double curr_advantage = minimax(currDraftRound, depth + 1);
			if (root.isPlayer_turn() && root.getMinimax_val() <= curr_advantage) {
				root.setMinimax_val(curr_advantage);
				root.setNext_draft(currDraftRound.getLast_draft());
			}
			if (!root.isPlayer_turn() && root.getMinimax_val() >= curr_advantage) {
				root.setMinimax_val(curr_advantage);
				root.setNext_draft(currDraftRound.getLast_draft());
			}
		}

		return root.getMinimax_val();
	}

	public static void readInput(String inputFileName) {
		heroes = new TreeMap<Integer, Hero>();

		File file = new File("input.txt");
		if (!inputFileName.equals("submission")) {
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
		}
		return 0;
	}
}
