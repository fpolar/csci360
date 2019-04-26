import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javafx.util.Pair;

public class project3cs360s2019 {
	public static boolean testing = true;
	public static boolean debug_in = false;
	public static boolean debug_sim = true;
	public static boolean debug_setup = false;
	public static boolean debug_setUtility = false;
	
	public static int grid_size;
	public static int num_obstacles;
	public static ArrayList< Pair<Integer, Integer> > obstacles;
	public static Pair<Integer, Integer> destination;
	
	public static double[][] values;
	public static String[][] policies;
	public static ArrayList< Pair<Integer, Integer> > visited_locations;
	
	public static double prob_correct_move = .7;
	public static double gamma = .9;
	public static double epsilon = .1;
	
	public static void main(String[] args) {
		readInput("input-1.txt");
		simulateMars();
	}
	
	public static void simulateMars() {
		setDebugFlags();
		
		values = new double[grid_size][grid_size];
		policies = new String[grid_size][grid_size];
		visited_locations = new ArrayList< Pair<Integer, Integer> >();
		
		//filling values with V0 of -1, since movement costs 1
		for(double[] vals: values) Arrays.fill(vals, -1);
		for(String[] policy_row: policies) Arrays.fill(policy_row, "x");
		
		//goal state is 100 - 1 for movement cost, 99
		values[destination.getKey()][destination.getValue()] = 99;
		policies[destination.getKey()][destination.getValue()] = ".";
		
		setObstacleValues();
		
		if(debug_setup) {
			printValues();
			printPolicies();
		}
		
		evaluateNeighbors(destination);
		setObstacleValues();
		
		if(debug_sim){
			printValues();
			printPolicies();
		}
		writePolicies();
	}

	public static void evaluateNeighbors(Pair<Integer, Integer> loc) {
		visited_locations.add(loc);
		ArrayList<Pair<Integer, Integer>> children = new ArrayList<Pair<Integer, Integer>>();
		
		//left
		if(loc.getKey()-1>=0) {
			Pair<Integer, Integer> childPair = new Pair<Integer, Integer>(loc.getKey()-1, loc.getValue());
			if(!hasBeenVisited(childPair)) {
				children.add(childPair);
				setUtility(childPair);
			}
		}
		//up
		if(loc.getValue()-1>=0) {
			Pair<Integer, Integer> childPair = new Pair<Integer, Integer>(loc.getKey(), loc.getValue()-1);
			if(!hasBeenVisited(childPair)) {
				children.add(childPair);
				setUtility(childPair);
			}
		}
		//right
		if(loc.getKey()+1<grid_size) {
			Pair<Integer, Integer> childPair = new Pair<Integer, Integer>(loc.getKey()+1, loc.getValue());
			if(!hasBeenVisited(childPair)) {
				children.add(childPair);
				setUtility(childPair);
			}
		}
		//down
		if(loc.getValue()+1<grid_size) {
			Pair<Integer, Integer> childPair = new Pair<Integer, Integer>(loc.getKey(), loc.getValue()+1);
			if(!hasBeenVisited(childPair)) {
				children.add(childPair);
				setUtility(childPair);
			}

		}
		
		for(Pair<Integer, Integer> p:children) {
			evaluateNeighbors(p);
		}
	}

	public static void setUtility(Pair<Integer, Integer> loc) {
		
		//left, up, right, down
		double[] utils = new double[4];

		double max_util = Double.NEGATIVE_INFINITY;
		int max_util_index = 0;
//		String[] index_to_dirStrings = { "^","<", "v", ">"};
		String[] index_to_dirStrings = {"<", "^", ">", "v"};
		
		//left
		if(loc.getKey()-1>=0) {
			utils[0] = values[loc.getKey()-1][loc.getValue()];
		}else {
			utils[0] = values[loc.getKey()][loc.getValue()];
		}
		//up
		if(loc.getValue()-1>=0) {
			utils[1] = values[loc.getKey()][loc.getValue()-1];
		}else {
			utils[1] = values[loc.getKey()][loc.getValue()];
		}
		//right
		if(loc.getKey()+1<grid_size) {
			utils[2] = values[loc.getKey()+1][loc.getValue()];
		}else {
			utils[2] = values[loc.getKey()][loc.getValue()];
		}
		//down
		if(loc.getValue()+1<grid_size) {
			utils[3] = values[loc.getKey()][loc.getValue()+1];
		}else {
			utils[3] = values[loc.getKey()][loc.getValue()];
		}

		if(debug_setUtility) {
			System.out.println("loc: "+loc);
		}
		for(int i = 0;i<4;i++) {
			double curr_util = utils[i] * prob_correct_move;
			for(int a=0;a<4;a++) {
				if(a!=i) {
					curr_util+=utils[a]*(1-prob_correct_move);
				}
			}
			curr_util = curr_util*gamma;
			//TODO Add R(s) here

			if(debug_setUtility) {
				System.out.println("\tindex: "+i+"\n\tcurr_util: "+curr_util);
			}
			
			if(curr_util>max_util) {
				max_util = curr_util;
				max_util_index = i;
			}
		}
		
		values[loc.getKey()][loc.getValue()] = max_util;
		policies[loc.getKey()][loc.getValue()] = index_to_dirStrings[max_util_index];
	}
	
	public static void setObstacleValues() {
		for(Pair<Integer, Integer> p:obstacles) {
			values[p.getKey()][p.getValue()] = -101;
			policies[p.getKey()][p.getValue()] = "o";
		}
	}
	
	public static void printPolicies() {
		for(int c = 0;c<grid_size;c++) {
			for(int r = 0;r<grid_size;r++) {
				String p = policies[r][c];
				System.out.print(p+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printValues() {
		for(int c = 0;c<grid_size;c++) {
			for(int r = 0;r<grid_size;r++) {
				double d = values[r][c];
				if(d == (long) d)
					System.out.print(String.format("%d",(long)d)+"\t");
			    else
			        System.out.print(round(d, 2)+"\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void writePolicies() {
		FileWriter fw;
		try {
			fw = new FileWriter("output.txt");
			PrintWriter pw = new PrintWriter(fw);
			for(int c = 0;c<grid_size;c++) {
				for(int r = 0;r<grid_size;r++) {
					String p = policies[r][c];
					pw.print(p);
				}
				pw.println();
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readInput(String inputFileName) {
		obstacles = new ArrayList<Pair<Integer, Integer>>();

		File file = new File("input.txt");
		if (testing) {
			file = new File("C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P3_Rover_Route\\src\\dev_cases\\"
					+ inputFileName);
		}

		try {
			Scanner sc = new Scanner(file);
			grid_size = sc.nextInt();
			sc.nextLine();
			num_obstacles = sc.nextInt();
			sc.nextLine();

			for (int i = 0; i < num_obstacles; i++) {
				String[] obstacle_string = sc.nextLine().split(",");
				obstacles.add(new Pair<Integer, Integer>(Integer.parseInt(obstacle_string[0]), Integer.parseInt(obstacle_string[1])));
			}
			
			String[] dest_string = sc.nextLine().split(",");
			destination = new Pair<Integer, Integer>(Integer.parseInt(dest_string[0]), Integer.parseInt(dest_string[1]));
			
			sc.close();
		} catch (FileNotFoundException e) {
			if (debug_in)
				System.out.println("Input File Not found");
			e.printStackTrace();
		}
		
		if(debug_in) {
			System.out.println(grid_size);
			System.out.println(num_obstacles);
			for (int i = 0; i < num_obstacles; i++) {
				System.out.println(obstacles.get(i).getKey()  + "," +obstacles.get(i).getValue());
			}
			System.out.println(destination.getKey()  + "," +destination.getValue());
		}

	}
	
	public static boolean hasBeenVisited(Pair<Integer, Integer> p) {
		return visited_locations.contains(p);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static void setDebugFlags() {
		debug_in &= testing;
		debug_sim &= testing;
		debug_setup &= testing;
		debug_setUtility &= testing;
	}
}
