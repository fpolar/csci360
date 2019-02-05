import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

public class Project1cs360s2019 {
	public static void main(String[] args) {
		int i = 0;
		checkResult(simulateJungle("input" + i + ".txt"), i);
	}

	public class Jungle implements Comparable{
		int size;
		int imagesSecured;
		HashMap<Point, Integer> animals;
		Set<Point> cameras;
		Set<Point> invalidLocations;
//		Set<Point> validLocations; //If deriving this everytime from above field takes to long
		//I'll look into implementing thi data structure back

		public Jungle(int size, HashMap<Point, Integer> animals ) {
			this.size = size;
			this.animals = animals;
			cameras = new HashSet<Point>();
			invalidLocations = new HashSet<Point>();
			//validLocations = size * size;
		}

		/*
		 * TODO: Make a seperate function to check if can be placed (or just make set
		 * public) Returns how many locations were invalidated by placement -1 if it
		 * wasn't placed
		 */
		public int addCamera(Point p) {
			// if(!canBePlaced(p)) return -1;
			cameras.add(p);

			int out = 0;

			// Adds cameras row to invalid Locs
			for (int i = 0; i < this.size; i++) {
				out += invalidLocations.add(new Point(p.x, i)) ? 1 : 0;
//				System.out.println(p.x+", "+i+" Invalidated");
			}
			// Adds cameras col to invalid Locs
			for (int i = 0; i < this.size; i++) {
				out += invalidLocations.add(new Point(i, p.y)) ? 1 : 0;
//				System.out.println(i+", "+p.y+" Invalidated");
			}

			/*
			 * Adds cameras diagonals to invalid Locs Starts top left of top bottom right
			 * then top right to bottom left (+x is right, +y is down)
			 */
			int cRow = Math.max(0, p.x - p.y);
			int cCol = Math.max(0, p.y - p.x);

			while (cRow < this.size && cCol < this.size) {
//				System.out.println(cRow+", "+cCol+" Invalidated");
				out += invalidLocations.add(new Point(cRow++, cCol++)) ? 1 : 0;
			}

			cRow = p.x + Math.min(this.size - 1 - p.x, p.y);
			cCol = p.y - Math.min(this.size - 1 - p.x, p.y);

			while (cRow >= 0 && cCol < size) {
//				System.out.println(cRow+", "+cCol+" Invalidated");
				out += invalidLocations.add(new Point(cRow--, cCol++)) ? 1 : 0;
			}

			return out;
		}

		/*
		 * removes a camera at a given point There may be a more efficient way to
		 * backtrack this seems very inefficient
		 */
		public void removeCamera(Point p) {
			cameras.remove(p);
			Set<Point> cTemp = cameras;
			invalidLocations.clear();
			cameras.clear();
			for (Point c : cTemp) {
				addCamera(c);
			}
		}

		
		/*
		 * Returns the amount of animals in the same spot as a camera in the jungle
		 */
		public int animalScore() {
			int out = 0;
		    for(Entry<Point, Integer> e : animals.entrySet()) {
		    	if(cameras.contains(e.getKey())) {
		    		out += e.getValue();
		    	}
		    }
		    return out;
		}

		/*
		 * Returns the amount of valid locations for a camera in the jungle
		 */
		public int numValidLocations() {
			return size*size - invalidLocations.size();
		}
		

		/*
		 * Returns a String representing the jungle's state
		 * P is picture, C is camera, A is animal(s), O is valid location, X is invalid location
		 * a is animal(s) in invalid location
		 * find better way to print that can handle mutliple animals in one loc
		 */
		public String toString() {
			String out = "\nWelcome to the Jungle Baby\n";
			
			for(int i = 0;i<size;i++) {
				for(int a =0;a<size;a++) {
					Point p = new Point(i, a);
					if(cameras.contains(p)) {
						if(animals.containsKey(p)) {
							out+=" P ";
						}else {
							out+=" C ";
						}
					}else if(animals.containsKey(p)) {
						if(invalidLocations.contains(p)) {
							out+=" a ";
						}else {
							out+=" A ";
						}
					}else {
						if(invalidLocations.contains(p)) {
							out+=" X ";
						}else {
							out+=" O ";
						}
					}
				}
				out+="\n";
			}

//			for(Point p:invalidLocations) {
//				out+="\n"+p.x+", "+p.y;
//			}
			return out+"\nAvailableSpaces: "+numValidLocations();
		}

		@Override
		public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (getClass() != o.getClass())
			return false;
		Jungle other = (Jungle) o;
		if(other.size != this.size) {
			return false;
		}
		if(!other.animals.equals(this.animals)) {
			return false;
		}
		if(!other.invalidLocations.equals(this.invalidLocations)) {
			return false;
		}
		if(!other.cameras.equals(this.cameras)) {
			return false;
		}
		return true;
	}


		@Override
		public int compareTo(Object o) {
			if(equals(o)) {
				return 0;
			}
			if(this.animalScore() <= ((Jungle)o).animalScore()) {
				return -1;
			}
			return 1;
		}
	}

	/*
	 * TODO: If runtime becomes an issue, convert 2D array to 1D
	 */

	public static int simulateJungle(String inputFileName) {
		File file = new File(
				"C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P1_Camera_Traps\\src\\inputs\\" + inputFileName);

		int jungleSize = -1;
		int numTraps = -1;
		int numAnimals = -1;
		boolean astar = true;
		HashMap<Point, Integer> animals = new HashMap<Point, Integer>();

		long start = System.nanoTime(); // TODO: comment out when submitting, just for my tests
		try {
			Scanner sc = new Scanner(file);
			jungleSize = sc.nextInt();
			sc.nextLine();
			numTraps = sc.nextInt();
			sc.nextLine();
			numAnimals = sc.nextInt();
			sc.nextLine();
			String alg = sc.nextLine();
			if (alg.equals("dfs")) {
				astar = false;
			}
			System.out.println(
					"size: " + jungleSize + " - traps: " + numTraps + " - animals:" + numAnimals + " - " + alg);

			for (int i = 0; i < numAnimals; i++) {
				String[] coordinate = sc.nextLine().split(",");
				Point point = new Point(Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]));
				if (animals.containsKey(point)) {
					animals.put(point, animals.get(point) + 1);
				} else {
					animals.put(point, 1);
				}
				System.out.println(point.x +", "+point.y+" ("+animals.get(point)+")");
			}
		} catch (FileNotFoundException e) {
			System.out.println("File Not found");
			e.printStackTrace();
			return -1;
		}

		Stack<Jungle> states = new Stack<Jungle>();
		Project1cs360s2019 pp = new Project1cs360s2019();
		Jungle j = pp.new Jungle(jungleSize, animals);
		
		System.out.println(j.toString());
		j.addCamera(new Point(3,3));
		System.out.println(j.toString());
		
		states.push(j);

		int numImages = 0;
		
		if(astar) {
			numImages = aStar(states, numTraps);
		}else {
			numImages = dfs(states, numTraps);
		}

		return numImages;
	}

	public static int aStar(Stack<Jungle> states, int numTraps) {
		return 0;
	}
	
	/*
	 * Runs dfs with backtracking on the stack on states given for the given number of cameras/traps
	 */
	public static int dfs(Stack<Jungle> states, int numTraps) {
		
		Set<Jungle> attempts = new HashSet<Jungle>();
		
		while(!states.isEmpty()) {
			
			Jungle jungle = states.pop();
			
			System.out.println(jungle);
			
			if(!attempts.add(jungle)) {
				continue; //if I've already tried this jungle state, don't try again
			}
			
			if(jungle.numValidLocations() == 0) {
				if(jungle.cameras.size() == numTraps) {
					return jungle.animalScore();
				}else {
					continue; //if theres no more valid locations in this jungle, don't/can't make children out of it
				}
			}
			
			// if there are more valid locations
		}
		
		
		return 0;
	}

	/*
	 * TODO: reads correct output file and compares it to the given result
	 */
	public static void checkResult(int result, int testID) {

		File file = new File(
				"C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P1_Camera_Traps\\src\\outputs\\output" + testID
						+ ".txt");

		try {
			Scanner sc = new Scanner(file);
			int correct = sc.nextInt();
			System.out.println("Current: " + result + " - Expected: " + correct);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}