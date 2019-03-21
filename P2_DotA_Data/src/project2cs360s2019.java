import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import models.DraftState;
import models.Hero;

public class project2cs360s2019 {
	
	static boolean ab_pruning;
	static int numHeroes;
	static HashMap<Integer, Hero> heroes;
	
	static boolean debug_in = false;
	static boolean debug_drafting = true;
	
	public static void main(String[] args) {
		readInput("input0.txt");
		simulateDraft();
	}
	
	public static void simulateDraft() {
		DraftState root = new DraftState(heroes);
		root.createSuccessors(heroes);
		printHeroes(heroes);
		if(debug_drafting) printStates(root.getSuccessors());
	}
	
	public static void readInput(String inputFileName) {
		heroes = new HashMap<Integer, Hero>();
		
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
				Hero tempHero = new Hero(
						Double.parseDouble(attrs[1]), 
						Double.parseDouble(attrs[2]),
						Double.parseDouble(attrs[3]),
						Integer.parseInt(attrs[4]));
				heroes.put(Integer.parseInt(attrs[0]), tempHero);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			if (debug_in)
				System.out.println("Input File Not found");
			e.printStackTrace();
		}
		
		if(debug_in)
			printHeroes(heroes);

	}

	public static void printHeroes(HashMap<Integer, Hero> mp) {
	    Iterator<Entry<Integer, Hero>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Hero> pair = (Map.Entry<Integer, Hero>)it.next();
	        System.out.println(pair.getKey() +""+ pair.getValue());
	        //it.remove();
	    }
	}
	public static void printStates(Map<Double, DraftState> mp) {
	    Iterator<Entry<Double, DraftState>> it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Double, DraftState> pair = (Map.Entry<Double, DraftState>)it.next();
	        System.out.println(pair.getValue());
	        //it.remove();
	    }
	}
}
