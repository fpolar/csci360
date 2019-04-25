import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import javafx.util.Pair;

public class project3cs360s2019 {
	public static boolean testing = true;
	public static boolean debug_in = true;
	public static boolean debug_sim = true;
	
	
	public static int grid_size;
	public static int num_obstacles;
	public static ArrayList< Pair<Integer, Integer> > obstacles;
	public static Pair<Integer, Integer> destination;
	
	public static double[][] values;
	public static String[][] policies;
	
	public static void main(String[] args) {
		readInput("input-0.txt");
		simulateMars();
	}
	
	public static void simulateMars() {
		values = new double[grid_size][grid_size];
		policies = new String[grid_size][grid_size];
		
		values[destination.getKey()][destination.getValue()] = 99;
		policies[destination.getKey()][destination.getValue()] = ".";
		
		evaluateNeighbors(destination);
		
		if(debug_sim) {
			printPolicies();
		writePolicies();
		}
	}

	public static void evaluateNeighbors(Pair<Integer, Integer> loc) {
		//out of each direction, which is best to take, store that as util val
		
		//the value is the sum for all directions of the chance u go a direction times it's value

		//left
		if(loc.getKey()-1>=0) {
			
		}
		//up
		if(loc.getValue()-1>=0) {
			
		}
		//right
		if(loc.getKey()+1<grid_size) {
			
		}
		//down
		if(loc.getValue()+1<grid_size) {
			
		}
	}
	
	public static void printPolicies() {
		for(String[] pl:policies) {
			for(String p:pl) {
				System.out.print(p);
			}
			System.out.println();
		}
	}
	
	public static void writePolicies() {
		FileWriter fw;
		try {
			fw = new FileWriter("output.txt");
			PrintWriter pw = new PrintWriter(fw);
			for(String[] pl:policies) {
				for(String p:pl) {
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
}
