import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

public class project1cs360s2019 {

	static project1cs360s2019 world = new project1cs360s2019();
	static boolean debugSim = false;
	static boolean debugAStar = true;
	static boolean debugAttempts = false;
	static boolean debugQueue = true;
	static boolean debugAddCam = false;
	static boolean debugRemoveCam = false;
	static boolean debugCompareTo = false;

	public static void main(String[] args) {
		for (int i = 0; i < 2; i++)
			checkResult(simulateJungle("input" + i + ".txt"), i);
//		simulateJungle("submission");
	}

	public class Jungle implements Comparable {
		int size;
		int imagesSecured;
		HashMap<Point, Integer> animals;
		Set<Point> cameras;
		Set<Point> invalidLocations;

		public Jungle(int size, HashMap<Point, Integer> animals) {
			this.size = size;
			this.animals = animals;
			cameras = new HashSet<Point>();
			invalidLocations = new HashSet<Point>();
		}

		/*
		 * TODO: Make a seperate function to check if can be placed (or just make set
		 * public) Returns how many locations were invalidated by placement -1 if it
		 * wasn't placed
		 */
		public int addCamera(Point p) {
			cameras.add(p);

			int out = 0;
			Set<Point> newInvalidLocs = new HashSet<Point>();

			// Adds cameras row to invalid Locs
			for (int i = 0; i < this.size; i++) {
				out += newInvalidLocs.add(new Point(p.x, i)) ? 1 : 0;
				if (debugAddCam)
					System.out.println(p.x + ", " + i + " Invalidated");
			}
			// Adds cameras col to invalid Locs
			for (int i = 0; i < this.size; i++) {
				out += newInvalidLocs.add(new Point(i, p.y)) ? 1 : 0;
				if (debugAddCam)
					System.out.println(i + ", " + p.y + " Invalidated");
			}

			/*
			 * Adds cameras diagonals to invalid Locs Starts top left of top bottom right
			 * then top right to bottom left (+x is right, +y is down)
			 */
			int cRow = Math.max(0, p.x - p.y);
			int cCol = Math.max(0, p.y - p.x);

			while (cRow < this.size && cCol < this.size) {
				if (debugAddCam)
					System.out.println(cRow + ", " + cCol + " Invalidated");
				out += newInvalidLocs.add(new Point(cRow++, cCol++)) ? 1 : 0;
			}

			cRow = p.x + Math.min(this.size - 1 - p.x, p.y);
			cCol = p.y - Math.min(this.size - 1 - p.x, p.y);

			while (cRow >= 0 && cCol < size) {
				if (debugAddCam)
					System.out.println(cRow + ", " + cCol + " Invalidated");
				out += newInvalidLocs.add(new Point(cRow--, cCol++)) ? 1 : 0;
			}
			if (debugAddCam)
				System.out.println(this);
			newInvalidLocs.remove(p);
			invalidLocations.addAll(newInvalidLocs);
			return out;
		}

		/*
		 * removes a camera at a given point There may be a more efficient way to
		 * backtrack this seems very inefficient
		 */
		public void removeCamera(Point p) {
			if (debugRemoveCam)
				System.out.println("removing: " + p + "\n" + this);
			cameras.remove(p);
			Set<Point> cTemp = new HashSet<Point>(cameras);
			invalidLocations.clear();
			cameras.clear();
			for (Point c : cTemp) {
				addCamera(c);
			}
			if (debugRemoveCam)
				System.out.println("removed: " + p + "\n" + this);
		}

		/*
		 * Returns the amount of animals in the same spot as a camera in the jungle
		 */
		public int animalScore() {
			int out = 0;
			for (Entry<Point, Integer> e : animals.entrySet()) {
				if (cameras.contains(e.getKey())) {
					out += e.getValue();
				}
			}
			return out;
		}

		/*
		 * Returns the amount of animals in the invalid locations minus the amount valid
		 * pictures being taken
		 */
		public int animalCost() {
			int out = 0;
			for (Entry<Point, Integer> e : animals.entrySet()) {
				if (invalidLocations.contains(e.getKey())) {
					out += e.getValue();
				}
			}
			return out;
		}

		/*
		 * Returns the amount of cameras in invalid locations the heuristic cost of the
		 * current jungle state
		 */
		public int cameraCost() {
			int out = 0;
			// System.out.println("CAMERASIZE: " + cameras.size());
			for (Point c : cameras) {
				if (invalidLocations.contains(c)) {
					out++;
				}
			}
			return out;
		}

		/*
		 * Returns the amount of valid locations for a camera in the jungle
		 */
		public int numValidLocations() {
			return size * size - invalidLocations.size();
		}

		/*
		 * Returns a set with all valid locations Should I do this or just keep another
		 * set of the valid locations? The question is, do i call this method more, or
		 * addCamera more
		 */
		public HashSet<Point> validLocations() {
			HashSet<Point> out = new HashSet<Point>();
			for (int i = 0; i < size; i++) {
				for (int a = 0; a < size; a++) {
					Point p = new Point(i, a);
					if (!invalidLocations.contains(p)) {
						out.add(p);
					}
				}
			}
			out.removeAll(cameras);
			return out;
		}

		/*
		 * Puts c cameras on random spots, first trying random animal locations then
		 * going purely randomly, no repeating
		 */
		public void randomlySetUpCameras(int c) {
			Random r = new Random();
			cameras.clear();
			invalidLocations.clear();
			while (cameras.size() < c) {
				Point p = new Point(r.nextInt(size), r.nextInt(size));
				while (cameras.contains(p)) {
					p = new Point(r.nextInt(size), r.nextInt(size));
				}
				addCamera(p);
			}

		}

		/*
		 * Puts c cameras on random spots, first trying random animal locations then
		 * going purely randomly, no repeating
		 */
		public void SetUpCamerasOnAnimals(int c) {
			Random r = new Random();
			cameras.clear();
			invalidLocations.clear();
			ArrayList<Point> newCams = new ArrayList<Point>();
			newCams.addAll(animals.keySet());

			while (newCams.size() > 0 && cameras.size() < c) {
				int i = r.nextInt(newCams.size());
				Point newCam = newCams.get(i);
				if (!invalidLocations.contains(newCam)) {
					addCamera(newCam);
				}
				newCams.remove(i);
			}

			while (cameras.size() < c) {
				Point p = new Point(r.nextInt(size), r.nextInt(size));
				while (cameras.contains(p)) {
					p = new Point(r.nextInt(size), r.nextInt(size));
				}
				addCamera(p);
			}

		}
		/*
		 * Puts c cameras on random spots, first trying random animal locations then
		 * going purely randomly, no repeating
		 */
		public void SetUpCamerasOnDiagonals(int c) {
			int cams = c;
			while(cams>0) {
				addCamera(new Point(cams, cams--));
			}
		}

		/*
		 * Returns a String representing the jungle's state P is picture, C is camera, A
		 * is animal(s), O is valid location, X is invalid location a is animal(s) in
		 * invalid location find better way to print that can handle mutliple animals in
		 * one loc
		 */
		public String toString() {
			String out = "\nWelcome to the Jungle Baby\n";

			for (int i = 0; i < size; i++) {
				for (int a = 0; a < size; a++) {
					Point p = new Point(i, a);
					if (cameras.contains(p)) {
						if (animals.containsKey(p)) {
							out += " P ";
						} else {
							out += " C ";
						}
					} else if (animals.containsKey(p)) {
						if (invalidLocations.contains(p)) {
							out += " a ";
						} else {
							out += " A ";
						}
					} else {
						if (invalidLocations.contains(p)) {
							out += " X ";
						} else {
							out += " O ";
						}
					}
				}
				out += "\n";
			}

//			for(Point p:invalidLocations) {
//				out+="\n"+p.x+", "+p.y;
//			}
			return out;// + "\nAvailableSpaces: " + numValidLocations();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null)
				return false;
			if (getClass() != o.getClass())
				return false;
			return this.toString().equals(o.toString());
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		/*
		 * Compares the costs and scores (f) of two jungles
		 */
		@Override
		public int compareTo(Object o) {
			if (debugCompareTo)
				System.out.println("this f: " + (this.animalScore() - this.cameraCost()) + "other f: "
						+ (((Jungle) o).animalScore() - ((Jungle) o).cameraCost()));
			if (equals(o)) {
				return 0;
			}
			if (this.animalScore() - this.cameraCost() <= ((Jungle) o).animalScore() - ((Jungle) o).cameraCost()) {
				return -1;
			}
			return 1;
		}

		public boolean allCamerasValid() {
			for (Point p : cameras) {
				if (invalidLocations.contains(p)) {
					return false;
				}
			}
			return true;
		}
	}

	/*
	 * TODO: If runtime becomes an issue, convert 2D array to 1D
	 */

	public static int simulateJungle(String inputFileName) {

		File file = new File("input.txt");
		if (!inputFileName.equals("submission")) {
			file = new File("C:\\Users\\theon\\OneDrive\\Documents\\Git\\csci360\\P1_Camera_Traps\\src\\inputs\\"
					+ inputFileName);
		}

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
			if (debugSim)
				System.out.println(
						"\nsize: " + jungleSize + " - traps: " + numTraps + " - animals: " + numAnimals + " - " + alg);

			for (int i = 0; i < numAnimals; i++) {
				String[] coordinate = sc.nextLine().split(",");
				Point point = new Point(Integer.parseInt(coordinate[0]), Integer.parseInt(coordinate[1]));
				if (animals.containsKey(point)) {
					animals.put(point, animals.get(point) + 1);
				} else {
					animals.put(point, 1);
				}
			}
		} catch (FileNotFoundException e) {
			if (debugSim)
				System.out.println("Input File Not found");
			e.printStackTrace();
			return -1;
		}

		Jungle j = world.new Jungle(jungleSize, animals);

		int numImages = 0;

		if (astar) {
			numImages = aStar(j, numTraps);
		} else {
			numImages = aStar(j, numTraps);
		}

		int runtime = (int) ((System.nanoTime() - start) / 1_000_000L);
		if (debugSim)
			System.out.println("Simulated jungle in: " + runtime + " milliseconds");

		FileWriter fw;
		try {
			fw = new FileWriter("output.txt");
			PrintWriter pw = new PrintWriter(fw);
			pw.print(numImages);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return numImages;
	}

	/*
	 * Runs A* on the given initial jungle state, given for the given number of
	 * cameras/traps
	 */
	public static int aStar(Jungle initialJungle, int numTraps) {
		int out = -1;

//		Queue<Jungle> states = new PriorityQueue<>(); // may need to be reverse order since I'm maximizing score
		Queue<Jungle> states = new PriorityQueue<>(Collections.reverseOrder()); // may need to be reverse order since
																				// I'm maximizing score
		Set<Jungle> attempts = new HashSet<Jungle>();
//		initialJungle.randomlySetUpCameras(numTraps);
		initialJungle.SetUpCamerasOnAnimals(numTraps);
//		initialJungle.SetUpCamerasOnDiagonals(numTraps);
		System.out.println(initialJungle);
		System.out.println(initialJungle.allCamerasValid());

		states.add(initialJungle);

		while (true && !states.isEmpty()) {

			Jungle jungle = states.remove();

			if (debugAStar)
				System.out.println(jungle);
			if (debugAttempts)
				System.out.println("attempts: " + attempts);
			if (debugQueue) {
				testPrioQueue(states);
			}
			if (jungle.cameras.size() > numTraps) {
				continue; // an error occurs where it adds one too many cameras, will fix later
			}
			if (!attempts.add(jungle)) {
				continue; // if I've already tried this jungle state, don't try again
			}

			if (debugAStar)
				System.out.println(jungle.allCamerasValid() + " " + jungle.cameras.size() + " == " + numTraps);

			// goal state check
			if (jungle.allCamerasValid() && jungle.cameras.size() == numTraps) {
				if (debugAStar) {
					System.out.println("All cameras placed");
					System.out.println(jungle);
					System.out.println("Score: " + jungle.animalScore());
				}
				out = Math.max(out, jungle.animalScore());
				continue;
			}

			if (debugAStar)
				System.out.println("Finding Children");
			// attempts to add children states to queue equal to the number of cameras
			// checks and tries to fix a camera in each state, if it is invalid
			Random r = new Random();

			/*
			 * Check every camera If it's invalid, remove it and add another - 1 child for
			 * each time this happens If theres no invalid cameras, move a random one - 1
			 * child for each as well
			 */
//			for (int x = 0; x < 5; x++) {
			for (Point p : jungle.cameras) {
				Jungle childJungle = world.new Jungle(jungle.size, jungle.animals);
				childJungle.cameras.addAll(jungle.cameras);
				childJungle.invalidLocations.addAll(jungle.invalidLocations);

				if (debugAStar)
					System.out.println(childJungle + "\nchecking camera: " + p);
				if (childJungle.invalidLocations.contains(p)) {
					if (debugAStar)
						System.out.println("It's invalid");
					childJungle.removeCamera(p);

					for (Point animalPoint : childJungle.animals.keySet()) {
						if (!childJungle.invalidLocations.contains(animalPoint)
								&& !childJungle.cameras.contains(animalPoint)) {
							if (r.nextInt(10) < 9) {
								childJungle.addCamera(animalPoint);
								if (debugAStar)
									System.out.println("added safe picture: " + animalPoint + "\n" + childJungle);
								break;
							}
						}
					}

					if (debugAStar)
						System.out.println("2: child size: " + childJungle.cameras.size() + " < parent size: "
								+ jungle.cameras.size());

					if (childJungle.cameras.size() < jungle.cameras.size()) {
						for (int i = 0; i < childJungle.size; i++) {
							for (int a = 0; a < childJungle.size; a++) {
								Point newPoint = new Point(i, a);
								if (!childJungle.invalidLocations.contains(newPoint)
										&& !childJungle.cameras.contains(newPoint)) {
									childJungle.addCamera(newPoint);
									if (debugAStar)
										System.out.println("adding safe camera: " + newPoint + "\n" + childJungle);
									i = childJungle.size;
									a = childJungle.size;
								}
							}
						}
					}

					if (debugAStar)
						System.out.println("3: child size: " + childJungle.cameras.size() + " < parent size: "
								+ jungle.cameras.size());
					if (childJungle.cameras.size() < jungle.cameras.size()) {
						if (debugAStar)
							System.out.println("Valid Locations: " + childJungle.numValidLocations());
						Point point = new Point(r.nextInt(childJungle.size), r.nextInt(childJungle.size));
						if (childJungle.numValidLocations() > 0) {
							int validPlacementAttempts = childJungle.size;
							while (validPlacementAttempts >= 0 && (childJungle.invalidLocations.contains(point)
									|| childJungle.cameras.contains(point))) {
								point = new Point(r.nextInt(childJungle.size), r.nextInt(childJungle.size));
								validPlacementAttempts--;
							}
						} else {
							while (childJungle.cameras.contains(point)) {
								point = new Point(r.nextInt(childJungle.size), r.nextInt(childJungle.size));
							}
						}
						childJungle.addCamera(point);
					}
				}

				states.add(childJungle);

			}

		}
//		}

		if (debugAStar)
			System.out.println("ASTAR COMPLETE: " + states.size() + "\n" + states.isEmpty());
		return out;
	}

	/*
	 * Runs dfs on the given initial jungle state, given for the given number of
	 * cameras/traps
	 */
	public static int dfs(Jungle initialJungle, int numTraps) {
		int out = -1;

		Stack<Jungle> states = new Stack<>();
		states.push(initialJungle);
		Set<Jungle> attempts = new HashSet<Jungle>();

		while (!states.isEmpty()) {

			Jungle jungle = states.pop();

			// System.out.println(jungle);

			if (!attempts.add(jungle)) {
				continue; // if I've already tried this jungle state, don't try again
			}

			if (jungle.cameras.size() == numTraps) {
				// System.out.println("All cameras placed");
				// System.out.println("Score: "+jungle.animalScore());
				out = Math.max(out, jungle.animalScore());
				continue;
			}

			// if there are more valid locations, explore each one
			for (Point p : jungle.validLocations()) {
				Jungle childJungle = world.new Jungle(jungle.size, jungle.animals);
				childJungle.cameras.addAll(jungle.cameras);
				childJungle.invalidLocations.addAll(jungle.invalidLocations);
				childJungle.addCamera(p);
				states.push(childJungle);
			}
		}

		return out;
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
			System.out.println("\nCurrent: " + result + " - Expected: " + correct);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * TODO: reads correct output file and compares it to the given result
	 */
	public static void testPrioQueue(Queue<Jungle> states) {
		Queue<Jungle> statesCopy = new PriorityQueue<>(states);
		System.out.println("Printing Queue");
		while (!statesCopy.isEmpty()) {
			Jungle jungle = statesCopy.poll();
			System.out.println(jungle + "\nscore: " + jungle.animalScore() + "\ncost: " + jungle.cameraCost());
		}
		System.out.println("Done printing Queue");
	}
}
