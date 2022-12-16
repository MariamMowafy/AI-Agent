package code;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CoastGuard extends GenericSearch {
	
	// generate random grid
	public static String genGrid() { 
		String result = "";
		String ships = "";
		String stations = "";

		// m and n positions for grid
		int mGrid = (int) (Math.random() * 11 + 5);
		int nGrid = (int) (Math.random() * 11 + 5);

		String[][] grid = new String[mGrid][nGrid];
		result += mGrid + "," + nGrid + ";";

		// position of coast guard
		int coastGuardX = (int) (Math.random() * mGrid);
		int coastGuardY = (int) (Math.random() * nGrid);

		// number of passengers on coast guard
		int coastGuardPassengers = (int) (Math.random() * 71 + 30);

		grid[coastGuardX][coastGuardY] = "Coast Guard";
		result += coastGuardX + "," + coastGuardY + ";" + coastGuardPassengers + ";";

		// position of first ship
		int shipX = (int) (Math.random() * mGrid);
		int shipY = (int) (Math.random() * nGrid);
		while (grid[shipX][shipY] != null) {
			shipX = (int) (Math.random() * mGrid);
			shipY = (int) (Math.random() * nGrid);
		}
		int passangers = (int) (Math.random() * 101 + 1);
		grid[shipX][shipY] = "Ship(" + passangers + ")";
		ships += shipX + "," + shipY + "," + passangers;

		// position of first station
		int stationX = (int) (Math.random() * mGrid);
		int stationY = (int) (Math.random() * nGrid);
		while (grid[stationX][stationY] != null) {
			stationX = (int) (Math.random() * mGrid);
			stationY = (int) (Math.random() * nGrid);
		}
		grid[stationX][stationY] = "Station";
		stations += stationX + "," + stationY;

		// generate n number of ships
		int maxPositions = (mGrid * nGrid) - 3;
		int numberOfShips = (int) (Math.random() * maxPositions);
		ships += genShips(grid, mGrid, nGrid, numberOfShips);

		// generate n number of stations
		stations += genStations(grid, mGrid, nGrid, numberOfShips);
		result += stations + ";" + ships + ";";

		return result;
	}

	public static String encodeGrid(TreeNode node) {
		String result = "";
		String ships = "";
		String stations = "";

		// set size of grid
		result += node.nGrid + "," + node.mGrid + ";";

		// get position of coast guard
		int coastGuardX = node.cgX;
		int coastGuardY = node.cgY;

		// set number of passengers on coast guard
		int coastGuardPassengers = node.cgMaxCapacity;

		// set position of coast guard on grid
		result += coastGuardPassengers + ";" + coastGuardX + "," + coastGuardY + ";";

		ships += encodeShips(node);

		stations += encodeStations(node);
		result += stations + ";" + ships + ";";
		return result;
	}

	public static String encodeShips(TreeNode node) {
		int shipX;
		int shipY;
		int passengers;
		String ships = "";

		// place ships in position
		for (int i = 0; i < node.ships.size(); i++) {
			shipX = node.ships.get(i).shipX;
			shipY = node.ships.get(i).shipY;
			passengers = node.ships.get(i).passengers;

			// append to string
			ships += shipX + "," + shipY + "," + passengers + ",";
		}
		return ships.substring(0, ships.length() - 1);
	}

	public static String encodeStations(TreeNode node) {
		String stations = "";
		// place stations in position
		for (int i = 0; i < node.stations.size(); i++) {
			int stationX = node.stations.get(i).stationX;
			int stationY = node.stations.get(i).stationY;
			// append to string
			stations += stationX + "," + stationY + ",";
		}
		return stations.substring(0, stations.length() - 1);

	}
	// generate ships randomly on a given grid
	public static String genShips(String[][] grid, int mGrid, int nGrid, int numberOfShips) {
		int shipX;
		int shipY;
		int passangers;
		String ships = "";

		// place n number of ships at random positions
		for (int i = 0; i < numberOfShips; i++) {
			shipX = (int) (Math.random() * mGrid);
			shipY = (int) (Math.random() * nGrid);
			while (grid[shipX][shipY] != null) {
				shipX = (int) (Math.random() * mGrid);
				shipY = (int) (Math.random() * nGrid);
			}
			passangers = (int) (Math.random() * 101 + 1);
			grid[shipX][shipY] = "Ship(" + passangers + ")";

			// append to string
			ships += "," + shipX + "," + shipY + "," + passangers;
		}
		return ships;
	}

	// generate stations randomly on a given grid
	public static String genStations(String[][] grid, int mGrid, int nGrid, int numberOfShips) {
		int stationX;
		int stationY;
		String stations = "";
		int maxPositions = (mGrid * nGrid) - 3 - numberOfShips;
		int numberOfStations = (int) (Math.random() * maxPositions);
		// place n number of stations at random positions
		for (int i = 0; i < numberOfStations; i++) {
			stationX = (int) (Math.random() * mGrid);
			stationY = (int) (Math.random() * nGrid);
			while (grid[stationX][stationY] != null) {
				stationX = (int) (Math.random() * mGrid);
				stationY = (int) (Math.random() * nGrid);
			}
			grid[stationX][stationY] = "Station";
			// append to string
			stations += "," + stationX + "," + stationY;
		}
		return stations;

	}

	public static String solve(String grid, String strategy, boolean visualize) {
		TreeNode root = new TreeNode(null, new ArrayList<String>(), 0, 0, 0, 0, grid);
		CoastGuard.visitedStates = new HashSet<>();

		switch (strategy) {
			case "BF":
				return BFS(root, visualize);
			case "DF":
				return DFS(root, visualize);
			case "ID":
				return IDS(root, visualize);
			case "GR1":
				return GR1(root, visualize);
			case "GR2":
				return GR2(root, visualize);
			case "AS1":
				return AS1(root, visualize);
			case "AS2":
				return AS2(root, visualize);
			default:
				return "Strategy Not Found";
		}

	}


	public static void main(String[] args) {
		// String testGrid =
		// "10,6;59;1,7;0,0,2,2,3,0,5,3;1,3,69,3,4,80,4,7,94,4,9,14,5,2,39;";
//		System.err.println("inn");
//		 String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";
//		 String grid1 = "6,6;52;2,0;2,4,4,0,5,4;2,1,19,4,2,6,5,0,8;";
//		 String grid2 = "7,5;40;2,3;3,6;1,1,10,4,5,90;";
//		 String grid3 = "8,5;60;4,6;2,7;3,4,37,3,5,93,4,0,40;";
//		 String grid4 = "5,7;63;4,2;6,2,6,3;0,0,17,0,2,73,3,0,30;";
//		 String grid5 = "5,5;69;3,3;0,0,0,1,1,0;0,3,78,1,2,2,1,3,14,4,4,9;";
//		 String grid6 = "7,5;86;0,0;1,3,1,5,4,2;1,1,42,2,5,99,3,5,89;";
//		 String grid7 = "6,7;82;1,4;2,3;1,1,58,3,0,58,4,2,72;";
//		 String grid8 = "6,6;74;1,1;0,3,1,0,2,0,2,4,4,0,4,2,5,0;0,0,78,3,3,5,4,3,40;";
//		 String grid9 = "7,5;100;3,4;2,6,3,5;0,0,4,0,1,8,1,4,77,1,5,1,3,2,94,4,3,46;";
//		 String grid10 = "10,6;59;1,7;0,0,2,2,3,0,5,3;1,3,69,3,4,80,4,7,94,4,9,14,5,2,39;";

	

	
//		 solve(grid6, "AS2", false);
	}
		}
