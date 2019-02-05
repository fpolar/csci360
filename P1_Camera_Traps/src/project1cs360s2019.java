import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
public class Project1cs360s2019 {

	static Project1cs360s2019 world = new Project1cs360s2019();
	static boolean debugAStar = true;
	static boolean debugAddCam = false;
	static boolean debugRemoveCam = true;

	public static void main(String[] args) {
		int i = 0;
		for (; i < 1; i++)
			checkResult(simulateJungle("input" + i + ".txt"), i);
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
			// if(!canBePlaced(p)) return -1;
			cameras.add(p);

			int out = 0;

			// Adds cameras row to invalid Locs
			for (int i = 0; i < this.size; i++) {
				out += invalidLocations.add(new Point(p.x, i)) ? 1 : 0;
				if (debugAddCam) System.out.println(p.x+", "+i+" Invalidated");
			}
			// Adds cameras col to invalid Locs
			for (int i = 0; i < this.size; i++) {
				out += invalidLocations.add(new Point(i, p.y)) ? 1 : 0;
				if (debugAddCam) System.out.println(i+", "+p.y+" Invalidated");
			}

			/*
			 * Adds cameras diagonals to invalid Locs Starts top left of top bottom right
			 * then top right to bottom left (+x is right, +y is down)
			 */
			int cRow = Math.max(0, p.x - p.y);
			int cCol = Math.max(0, p.y - p.x);

			while (cRow < this.size && cCol < this.size) {
				if (debugAddCam) System.out.println(cRow+", "+cCol+" Invalidated");
				out += invalidLocations.add(new Point(cRow++, cCol++)) ? 1 : 0;
			}

			cRow = p.x + Math.min(this.size - 1 - p.x, p.y);
			cCol = p.y - Math.min(this.size - 1 - p.x, p.y);

			while (cRow >= 0 && cCol < size) {
				if (debugAddCam) System.out.println(cRow+", "+cCol+" Invalidated");
				out += invalidLocations.add(new Point(cRow--, cCol++)) ? 1 : 0;
			}
			if(debugAddCam) System.out.println(this);
			invalidLocations.remove(p);
			return out;
		}

		/*
		 * removes a camera at a given point There may be a more efficient way to
		 * backtrack this seems very inefficient
		 */
		public void removeCamera(Point p) {
			if(debugRemoveCam) System.out.println("removing: "+p+"\n"+this);
			cameras.remove(p);
			Set<Point> cTemp = new HashSet<Point>(cameras);
			invalidLocations.clear();
			cameras.clear();
			for (Point c : cTemp) {
				addCamera(c);
			}
			if(debugRemoveCam) System.out.println("removed: "+p+"\n"+this);
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
		 * Puts c cameras on purely random spots TODO: make it so first on random spots
		 * with animals, then on random spots throughout not concerned with validity
		 * when placing
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
			return out + "\nAvailableSpaces: " + numValidLocations();
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
			if (other.size != this.size) {
				return false;
			}
			if (!other.animals.equals(this.animals)) {
				return false;
			}
			if (!other.invalidLocations.equals(this.invalidLocations)) {
				return false;
			}
			if (!other.cameras.equals(this.cameras)) {
				return false;
			}
			return true;
		}

		@Override
		public int compareTo(Object o) {
			if (equals(o)) {
				return 0;
			}
			if (this.animalScore() <= ((Jungle) o).animalScore()) {
				return -1;
			}
			return 1;
		}

		public boolean allCamerasValid() {
			for (Point p : cameras) {
				System.out.println(p);
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
			System.out.println("File Not found");
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
		System.out.println("Simulated jungle in: " + runtime + " milliseconds");
		return numImages;
	}

	/*
	 * Runs A* on the given initial jungle state, given for the given number of
	 * cameras/traps
	 */
	public static int aStar(Jungle initialJungle, int numTraps) {
		int out = -1;

		Queue<Jungle> states = new PriorityQueue<>(); // may need to be reverse order since I'm maximizing score
//		Queue<Jungle> states = new PriorityQueue<>(Collections.reverseOrder());
		Set<Jungle> attempts = new HashSet<Jungle>();
		initialJungle.randomlySetUpCameras(numTraps);
		System.out.println(initialJungle);
		System.out.println(initialJungle.allCamerasValid());

		states.add(initialJungle);

		while (true && !states.isEmpty()) {

			Jungle jungle = states.remove();

			if (!attempts.add(jungle)) {
				continue; // if I've already tried this jungle state, don't try again
			}

			if (debugAStar) System.out.println(jungle);
			if (debugAStar) System.out.println(jungle.allCamerasValid() + " " + jungle.cameras.size() + " == " + numTraps);

			// goal state check
			if (jungle.allCamerasValid() && jungle.cameras.size() == numTraps) {
				System.out.println("All cameras placed");
				System.out.println("Score: " + jungle.animalScore());
				out = Math.max(out, jungle.animalScore());
				continue;
			}

			System.out.println("Finding Children");
			// attempts to add children states to queue equal to the number of cameras
			// checks and tries to fix a camera in each state, if it is invalid
			for (int i = 0; i < 3; i++) {
				Random r = new Random();
				Jungle childJungle = world.new Jungle(jungle.size, jungle.animals);
				childJungle.cameras.addAll(jungle.cameras);
				childJungle.invalidLocations.addAll(jungle.invalidLocations);

				for (Point p : jungle.cameras) {
					System.out.println("checking camera: "+p);
					if (childJungle.invalidLocations.contains(p)) {
						if (debugAStar) System.out.println("It's invalid");
						childJungle.removeCamera(p);
						int a = 0;
						for (; a < childJungle.size; a++) {
							Point newPoint = new Point(p.x, a);
							if (!childJungle.invalidLocations.contains(newPoint)) {
								if (debugAStar) System.out.println("adding safe camera: "+newPoint);
								childJungle.addCamera(newPoint);
								break;
							}
						}
						if (a >= childJungle.size) {
							if (debugAStar) System.out.println("Adding random camera");
							childJungle.addCamera(new Point(r.nextInt(childJungle.size), r.nextInt(childJungle.size)));
						}
					}
				}

				if (debugAStar) System.out.println(childJungle + "\n^ Child");
				states.add(childJungle);
			}

		}

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
}
