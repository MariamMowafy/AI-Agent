package code;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class GenericSearch {
	
	//track visited states to prevent repeated states
	public static HashSet<String> visitedStates = new HashSet<String>();

	public static String BFS(TreeNode node, boolean visualize) {
		int nodesSearched = 0;
		ArrayList<TreeNode> queue = new ArrayList<>();
		queue.add(node);
		
		//loop until the queue is empty (no solution found) or goal test is passed
		while (!queue.isEmpty()) {
			node = queue.remove(0); //remove the front node
			nodesSearched++; 		//increase the number of nodes searched
			//check if goal test is passed
			if (goalTest(node)) {
				System.out.println(node.actions);
				String res = ""; 
				//append to the output string the (plan, deaths, black boxes retrieved, and number of nodes searched)
				for (String action : node.actions)
					res += action + ",";
				res = res.substring(0, res.length() - 1);
				res += ";";
				res += node.deaths + ";";
				res += node.blackBoxRetrived + ";";
				res += nodesSearched + "";

				while (node != null) {
					System.out.println("Node: " + node);
					System.out.println("Parent: " + node.parent);
					if (node.actions.size() > 0)
						System.out.println("Actions: " + node.actions.get(node.actions.size() - 1));
					if (visualize)
						node.printGrid(); //print the grid to visualize procedure
					node = node.parent;
					System.err.println();
				}
				return res;
			}
			// if goal state is not passed, add the generated nodes to the end of the queue
			queue.addAll(generateStates(node));

		}
		return "No Solution found";

	}

	public static String DFS(TreeNode node, boolean visualize) {
		int nodesSearched = 0; 
		ArrayList<TreeNode> queue = new ArrayList<>();
		queue.add(node); //add root node

		//loop until the queue is not empty (no solution found) or goal test is passed
		while (!queue.isEmpty()) {
			node = queue.remove(0); //remove the front node 
			nodesSearched++; 		//increase the number of nodes searched
			
			//check if goal test is passed
			if (goalTest(node)) {
				System.out.println(node.actions);

				String res = "";
				
				//append to the output string the (plan, deaths, black boxes retrieved, and number of nodes searched)
				for (String action : node.actions)
					res += action + ",";
				res = res.substring(0, res.length() - 1);
				res += ";";
				res += node.deaths + ";";
				res += node.blackBoxRetrived + ";";
				res += nodesSearched + "";

				while (node != null) {
					System.out.println("Node: " + node);
					System.out.println("Parent: " + node.parent);
					if (node.actions.size() > 0)
						System.out.println("ACtions: " + node.actions.get(node.actions.size() - 1));
					if (visualize)
						node.printGrid();
					node = node.parent;
					System.err.println();
				}
				return res;
			}
		
			// if goal state is not passed, add the generated nodes to the beginning of the queue
			queue.addAll(0, generateStates(node));

		}
		return "No Solution found";
	}

	public static String IDS(TreeNode node, boolean visualize) {

		int level = 0;
		ArrayList<TreeNode> q;

		ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		int nodesSearched = 0;

		String solution = "No solution";

		while (solution.equals("No solution")) {

			//at each level create a new queue and add the root 
			q = new ArrayList<TreeNode>();
			visitedStates = new HashSet<String>();
			q.add(node); 

			while (!q.isEmpty()) {
				node = q.remove(0); //remove the node at the front of the queue
				nodesSearched++; // increase number of nodes searched by 1

				if (node.actions.size() < level) {
					//generate new nodes if the level is not passed
					children = generateStates(node); 

				} else {
					//clear the list of children to start over with the level incremented
					children.clear(); 
				}

				//check if goal test is passed
				if (goalTest(node)) {
					System.out.println(node.actions);

					String res = "";
					
					//append to the output string the (plan, deaths, black boxes retrieved, and number of nodes searched)
					for (String action : node.actions)
						res += action + ",";
					res = res.substring(0, res.length() - 1);
					res += ";";
					res += node.deaths + ";";
					res += node.blackBoxRetrived + ";";
					res += nodesSearched + "";

					while (node != null) {
						System.out.println("Node: " + node);
						System.out.println("Parent: " + node.parent);
						if (node.actions.size() > 0)
							System.out.println("ACtions: " + node.actions.get(node.actions.size() - 1));
						if (visualize)
							node.printGrid();
						node = node.parent;
						System.err.println();
					}
					solution = res;
					return res;
				}
				//add the generated nodes to the queue
				q.addAll(children);
			}
			level++; //increment level after each iteration
			solution = "No solution";
		}
		System.out.println("No solution");
		// return failure
		return "No Solution"; //if all levels are searched and the goal test is still not found
	}

	public static ArrayList<TreeNode> calcHeuristic1OnQueue(ArrayList<TreeNode> queue, boolean uniformCost) {
		for (TreeNode node : queue) {
			int alive = 0;
			int pickups = 0;
			int retrieves = 0;
			for (Ship ship : node.ships) {
				// Calculate Retrieves Neeeded
				if (ship.passengers != -20)
					retrieves++;
				if (ship.passengers > 0) {
					alive += ship.passengers;
					// Calculate Pickups Needed
					pickups++;
				}
			}
			// Calculate Drops Needed
			int drops = alive / (node.cgMaxCapacity + node.passengersCarried);
			node.h = pickups + retrieves + drops;

			if (uniformCost) {
				node.h += node.pathCost;
			}
		}
		return queue;
	}

	public static String GR1(TreeNode node, boolean visualize) {
		ArrayList<TreeNode> q = new ArrayList<TreeNode>();
		q.add(node); //add root node
		q = calcHeuristic1OnQueue(q, false);
		int nodesSearched = 0;
		while (!q.isEmpty()) {
			int minSoFar = 2147483647;
			int minIndex = 0;

			//loop over all nodes in the queue and save the index of the node with the lowest cost
			for (int i = 0; i < q.size(); i++) {
				node = q.get(i);
				//compare the cost of the node with the minSoFar
				if (node.h < minSoFar) { 
					//if the cost of the current node is less than the minSoFar, set the minSoFar and the min index to this node
					minSoFar = node.h; 
					minIndex = i;
				}
			}

			node = q.remove(minIndex); //remove the node that has the minimum cost
			nodesSearched++; //increase number of nodes searched by 1

			//check if goal test is passed
			if (goalTest(node)) {
				System.out.println(node.actions);

				String res = "";
				
				//append to the output string the (plan, deaths, black boxes retrieved, and number of nodes searched)
				for (String action : node.actions)
					res += action + ",";
				res = res.substring(0, res.length() - 1);
				res += ";";
				res += node.deaths + ";";
				res += node.blackBoxRetrived + ";";
				res += nodesSearched + "";

				while (node != null) {
					System.out.println("Node: " + node);
					System.out.println("Parent: " + node.parent);
					if (node.actions.size() > 0)
						System.out.println("ACtions: " + node.actions.get(node.actions.size() - 1));
					if (visualize)
						node.printGrid();
					node = node.parent;
					System.err.println();
				}
				return res;
			}

			ArrayList<TreeNode> children = generateStates(node);
			children = calcHeuristic1OnQueue(children, false);
			q.addAll(children);
		}
		return "No Solution";

	}

	public static ArrayList<TreeNode> calcHeuristic2OnQueue(ArrayList<TreeNode> queue, boolean uniformCost) {
		// Same as First heuristic but giving weights to actions

		// Another Heurstics is getting calculating the minimum distance to ship+this
		// ship to nearest station (To be implemented)
		for (TreeNode node : queue) {
			int alive = 0;
			int pickups = 0;
			int retrieves = 0;
			for (Ship ship : node.ships) {
				// Calculate Retrieves Neeeded
				if (ship.passengers != -20)
					retrieves++;
				if (ship.passengers > 0) {
					alive += ship.passengers;
					// Calculate Pickups Needed
					pickups++;
				}
			}
			// Calculate Drops Needed
			int drops = alive / (node.cgMaxCapacity + node.passengersCarried);

			// Weighing every action as states with less pickups is closer to goal
			pickups = pickups /5;
			retrieves = retrieves* 10;
			drops = drops * 2;
			
			node.h = pickups + retrieves + drops;
			
			if (uniformCost) {
				node.h += node.pathCost;
			}
		}
		return queue;
	}

	public static String GR2(TreeNode node, boolean visualize) {
		ArrayList<TreeNode> q = new ArrayList<TreeNode>();
		q.add(node);
		q = calcHeuristic2OnQueue(q, false);
		int nodesSearched = 0;
		while (!q.isEmpty()) {
			int minSoFar = 2147483647;
			int minIndex = 0;

			for (int i = 0; i < q.size(); i++) {
				node = q.get(i);
				if (node.h < minSoFar) {
					minSoFar = node.h;
					minIndex = i;
				}
			}

			node = q.remove(minIndex);
			nodesSearched++;

			if (goalTest(node)) {
				System.out.println(node.actions);

				String res = "";
				for (String action : node.actions)
					res += action + ",";
				res = res.substring(0, res.length() - 1);
				res += ";";
				res += node.deaths + ";";
				res += node.blackBoxRetrived + ";";
				res += nodesSearched + "";

				while (node != null) {
					System.out.println("Node: " + node);
					System.out.println("Parent: " + node.parent);
					if (node.actions.size() > 0)
						System.out.println("ACtions: " + node.actions.get(node.actions.size() - 1));
					if (visualize)
						node.printGrid();
					node = node.parent;
					System.err.println();
				}
				return res;
			}

			ArrayList<TreeNode> children = generateStates(node);
			children = calcHeuristic2OnQueue(children, false);
			q.addAll(children);
		}
		return "No Solution";

	}

	public static String AS1(TreeNode node, boolean visualize) {
		ArrayList<TreeNode> q = new ArrayList<TreeNode>();
		q.add(node);
		q = calcHeuristic1OnQueue(q, true);
		int nodesSearched = 0;
		while (!q.isEmpty()) {
			int minSoFar = 2147483647;
			int minIndex = 0;

			for (int i = 0; i < q.size(); i++) {
				node = q.get(i);
				if (node.h < minSoFar) {
					minSoFar = node.h;
					minIndex = i;
				}
			}

			node = q.remove(minIndex);
			nodesSearched++;

			if (goalTest(node)) {
				System.out.println(node.actions);

				String res = "";
				for (String action : node.actions)
					res += action + ",";
				res = res.substring(0, res.length() - 1);
				res += ";";
				res += node.deaths + ";";
				res += node.blackBoxRetrived + ";";
				res += nodesSearched + "";

				while (node != null) {
					System.out.println("Node: " + node);
					System.out.println("Parent: " + node.parent);
					if (node.actions.size() > 0)
						System.out.println("ACtions: " + node.actions.get(node.actions.size() - 1));
					if (visualize)
						node.printGrid();
					node = node.parent;
					System.err.println();
				}
				return res;
			}

			ArrayList<TreeNode> children = generateStates(node);
			children = calcHeuristic1OnQueue(children, true);
			q.addAll(children);
		}
		return "No Solution";

	}

	public static String AS2(TreeNode node, boolean visualize) {
		ArrayList<TreeNode> q = new ArrayList<TreeNode>();
		q.add(node);
		q = calcHeuristic2OnQueue(q, true);
		int nodesSearched = 0;
		while (!q.isEmpty()) {
			int minSoFar = 2147483647;
			int minIndex = 0;

			for (int i = 0; i < q.size(); i++) {
				node = q.get(i);
				if (node.h < minSoFar) {
					minSoFar = node.h;
					minIndex = i;
				}
			}

			node = q.remove(minIndex);
			nodesSearched++;

			if (goalTest(node)) {
				System.out.println(node.actions);

				String res = "";
				for (String action : node.actions)
					res += action + ",";
				res = res.substring(0, res.length() - 1);
				res += ";";
				res += node.deaths + ";";
				res += node.blackBoxRetrived + ";";
				res += nodesSearched + "";

				while (node != null) {
					System.out.println("Node: " + node);
					System.out.println("Parent: " + node.parent);
					if (node.actions.size() > 0)
						System.out.println("ACtions: " + node.actions.get(node.actions.size() - 1));
					if (visualize)
						node.printGrid();
					node = node.parent;
					System.err.println();
				}
				return res;
			}

			ArrayList<TreeNode> children = generateStates(node);
			children = calcHeuristic2OnQueue(children, true);

			q.addAll(children);
		}
		return "No Solution";

	}

	//generate the child nodes given the parent node
	private static ArrayList<TreeNode> generateStates(TreeNode node) {
		//returns an array of all the possible actions in that position
		ArrayList<String> possibleActions = node.getAllPossibleActions();
		
		ArrayList<TreeNode> children = new ArrayList<>();
		for (String action : possibleActions) {
			TreeNode child = node.makeChild();
			//pickup
			if (action == "pickup") {
				//perform the action
				child.pickUp();
				//add the action being performed to the actions list
				child.actions.add("pickup");
				//increase the path cost of the node
				child.pathCost += 1;
				//calls the damage method
				child.damage();
				//get the new grid string at the current node after performing the action
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}

			// drop
			if (action == "drop") {
				child.drop();
				child.actions.add("drop");
				child.pathCost += 1;
				child.damage();
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}

			// retrieve
			if (action == "retrieve") {
				child.retrieve();
				child.actions.add("retrieve");
				child.pathCost += 1;
				child.damage();
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}

			// Move Left
			if (action == "left") {
				child.moveLeft();
				child.actions.add("left");
				child.pathCost += 5;
				child.damage();
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}

			// Move Right
			if (action == "right") {
				child.moveRight();
				child.actions.add("right");
				child.pathCost += 5;
				child.damage();
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}

			// Move Up
			if (action == "up") {
				child.moveUp();
				child.actions.add("up");
				child.pathCost += 5;
				child.damage();
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}

			// Move Down
			if (action == "down") {
				child.moveDown();
				child.actions.add("down");
				child.pathCost += 5;
				child.damage();
				child.gridString = CoastGuard.encodeGrid(child);
				child.updateGrid();
			}
			
			//to prevent repeated states
			if (!visitedStates.contains(child.gridString)) {
				children.add(child);
				visitedStates.add(child.gridString);
			}
		}
		return children;
	}

	private static boolean goalTest(TreeNode node) {
		// No living passengers who are not rescued
		for (Ship s : node.ships) {
			if (s.passengers > 0)
				return false;
		}

		// There are no undamaged boxes which have not been retrieved
		for (Ship s : node.ships) {
			if (s.passengers < 0 && s.passengers > -20)
				return false;
		}

		// the coast guard is not carrying any passengers
		if (node.passengersCarried > 0)
			return false;
		return true;
	}
}
