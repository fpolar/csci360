import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class project1cs360s2019 {
	public static void main(String[] args) {
		int i = 0;
		checkResult(simulateJungle("input"+i+".txt"), i);
	}
	
	public class Jungle{
		int size;
		//int[][] animals; Do I need this?
		ArrayList<Point> cameras;
		Set<Point> invalidLocations;
		
		public Jungle(int size) {
			this.size = size;
		}
		
		/*
		 * TODO: Make a seperate function to check if can be placed (or just make set public)
		 * Returns how many locations were invalidated by placement
		 * -1 if it wasn't placed
		 */
		public int addCamera(Point p) {
			//if(!canBePlaced(p)) return -1;
			cameras.add(p);
			
			int out = 0;

			//Adds cameras row to invalid Locs
			for(int i = 0; i<this.size; i++) {
				out += invalidLocations.add(new Point(p.x, i)) ? 1:0;
			}
			//Adds cameras col to invalid Locs
			for(int i = 0; i<this.size; i++) {
				out += invalidLocations.add(new Point(i, p.y)) ? 1:0;
			}
			
			/*
			 * Adds cameras diagonals to invalid Locs
			 * Starts top left of top bottom right
			 * then top right to bottom left
			 * (+x is right, +y is down)
			 */
			int cRow = Math.max(0, p.x-p.y);
			int cCol = Math.max(0, p.y-p.x);
			
			while(cRow < this.size && cCol < this.size) {
				out += invalidLocations.add(new Point(cRow++, cCol++)) ? 1:0;
			}

			cRow = p.x + Math.min(this.size-1-p.x, p.y);
			cCol = p.y - Math.min(this.size-1-p.x, p.y);
			
			while(cRow >=0 && cCol >= 0) {
				out += invalidLocations.add(new Point(cRow--, cCol++)) ? 1:0;
			}
			
			return out;
		}
		
		/*
		 * removes a camera at a given point
		 * There may be a more efficient way to backtrack
		 * this seems very innefficient
		 */
		public void removeCamera(Point p) {
			cameras.remove(p);
			ArrayList<Point> cTemp = cameras;
			invalidLocations.clear();
			cameras.clear();
			for(Point c:cTemp) {
				addCamera(c);
			}
		}
	}
	
	/*
	 * TODO: If runtime becomes an issue, convert 2D array to 1D
	 */
	
	public static int simulateJungle(String inputFileName) {
		File file = new File("C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P1_Camera_Traps\\src\\inputs\\"+inputFileName); 
		
		int jungleSize = -1;
		int numTraps = -1;
		int numAnimals = -1;
		boolean astar = true;
		int[][] animals;

        long start = System.nanoTime(); //TODO: comment out when submitting, just for my tests
		try {
			Scanner sc = new Scanner(file);
			jungleSize = sc.nextInt();
			sc.nextLine();
			numTraps = sc.nextInt();
			sc.nextLine();
			numAnimals = sc.nextInt();
			animals = new int[numAnimals][2];
			sc.nextLine();
			String alg = sc.nextLine();
			if(alg.equals("dfs")) {
				astar = false; 
			}
			System.out.println("size: "+jungleSize+" - traps: "+numTraps+" - animals:"+numAnimals+" - "+alg); 
			
			for(int i = 0;i<numAnimals;i++) {
				String[] coordinate = sc.nextLine().split(",");
				animals[i][0] = Integer.parseInt(coordinate[0]);
				animals[i][1] = Integer.parseInt(coordinate[1]);
				System.out.println(animals[i][0]+", "+animals[i][1]);
			} 
		} catch (FileNotFoundException e) {
			System.out.println("File Not found"); 
			e.printStackTrace();
			return -1;
		} 
		
		Stack<Jungle> states = new Stack<Jungle>();
		states.push(new Jungle());
	
		int numImages = 0;
		
		return numImages;
	}
	
	/*
	 * TODO: reads correct output file and compares it to the given result
	 */
	public static void checkResult(int result, int testID){

		File file = new File("C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P1_Camera_Traps\\src\\outputs\\output"+testID+".txt"); 
		
		try {
			Scanner sc = new Scanner(file);
			int correct = sc.nextInt();
			System.out.println("Current: "+result+" - Expected: "+correct);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
