package mazeGenerator;

import java.util.LinkedList;
import java.util.Random;

import maze.Cell;
import maze.Maze;
import maze.Wall;

public class RecursiveBacktrackerGenerator implements MazeGenerator 
{
	/** 
     * Generate a maze using the recursive backtracker algorithm.
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM normalAndTunnelGeneration (maze) or hexGeneration (maze)
     * Performs the generation of the maze using the algorithm.
     * Input: Maze maze.
     * OUTPUT : Maze with paths carved between cells in such a way that there is no loops and there is
     * 			a definite path between the entrance and the exit.
     * 
     * 1: Get a random cell from the maze
     * 2: Add the cell to the stack
     * 3: Mark it as visited
     * 4 (Only if maze type is tunnel): Check if the current cell is a tunnel, if so 
     *    then current cell will become the cell at the tunnel exit
     * 5: Get a random neighbour, carve a path between current cell and the neighbour 
     *    and assign the neighbour to be the current cell
     * 6: Repeat from step 2 until the current cell has no unvisited neighbours 
     * 7: If so, then backtrack (pop cell from stack) until you find a cell with an unvisited neighbour
     * 8: Repeat from step 5 until the stack is empty
     * ******************************************************************************************
     * 
     * @param maze Input Maze.
     * @returns Maze with paths in between the cells without any loops present
     */
	public void generateMaze(Maze maze) 
	{
		if(maze.type == maze.NORMAL || maze.type == maze.TUNNEL)
		{
			normalAndTunnelGeneration(maze);
		}
		else if(maze.type == maze.HEX)
		{
			hexGeneration(maze);
		}
	} // end of generateMaze()
	
	
	private void normalAndTunnelGeneration(Maze maze)
	{
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		int currentR;
		int currentC;
		
		// A boolean array is created to check visited status for each cell
		boolean visited[][] = new boolean[sizeR][sizeC];
		
		Random rand = new Random();
		
		// Random row and column number is generated and assigned to their corresponding variables
		currentR = rand.nextInt(sizeR);
		currentC = rand.nextInt(sizeC);
		
		// Starting cell created by passing in the randomly generated row and column coordinates
		Cell cell = new Cell(currentR, currentC);
		
		// Stack that will keep count of cells as we move along the maze
		LinkedList<Cell> stack = new LinkedList<Cell>();
		
		// A cell in a normal or tunnel maze would have four neighbors. Hence the array size was set to 4.
		int[] neighbours = new int[4];
		
		do
		{		
			 // Set the visited status of the current cell to true
			 visited[cell.r][cell.c] = true;			 
			 int freeNeighbourCount = 0;
			 
			 // Checks if the maze is a tunnel..
			 if (maze.type == maze.TUNNEL)
			 {
				 // Checks if the cell has a tunnel..
				 if (maze.map[cell.r][cell.c].tunnelTo != null)
			     {
					 // Create a temporary cell with the coordinates of the exit of the tunnel
					 Cell temp = maze.map[cell.r][cell.c].tunnelTo;
					 
					 // If the exit of the tunnel is not visited
					 if (!visited[temp.r][temp.c])
					 {
						 // If not then the current cell would be the cell at the other end of the tunnel
						 cell = maze.map[cell.r][cell.c].tunnelTo;
						 // Set the visited status of the cell at the end of the tunnel to true
						 visited[cell.r][cell.c] = true;
					 }
			     }
			 }
			 
			 /* For each valid direction, check if the neighbor in that direction of the current cell is inside 
			 the maze and has not been visited. The conditions stated are in order of the if statements written below. 
			 If the above conditions are met, add the direction index to the neighbours array. A value of 1 was added 
			 to each valid neighbor cell entry as it was not possible to add the EAST direction to the array as it had a value of 0 */
			 for (int i = 0; i < maze.NUM_DIR; i++) 
			 {		
				 	// 1 and 4 would correspond to NORTHEAST and SOUTHWEST which do not exist in a normal/tunnel maze
				 	if (i == 1 || i == 4)
					{
						continue;
					}			 
					if (isIn(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
					{		
						 if (!visited[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]])
						 { 
						 	neighbours[freeNeighbourCount++] = i+1;					
						 }
					}					 	
			 }
			 
			// If valid neighbors are present then..
			 if (freeNeighbourCount > 0) 
			 {	 
				// Create and add the cell to the stack. This cell's coordinates will be the changed to the
				 // coordinates of the next cell to which we will move to.
				 cell = new Cell(cell.r, cell.c);
				 stack.addFirst(cell);
				 Cell neigh = new Cell();
				 
				 // Function that decides which cell to carve a path to and assigns that cell to the current
				 // cell. A more detailed explanation is given in the function declaration.
				 nextCell (maze, cell, stack, neigh, neighbours, freeNeighbourCount);
			 }
			// If no valid neighbors were present, then remove the current cell from the stack. In other words, if 
			 // the current cell had no unvisited neighbors then remove it from the stack.
			 else 
			 {				 
				 cell = stack.removeFirst();			
			 }
			
		} while (!stack.isEmpty());
	}
	

	private void hexGeneration(Maze maze)
	{
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		int currentR;
		int currentC;
		
		// A boolean array is created to check visited status for each cell
		boolean visited[][] = new boolean[sizeR][sizeC + (sizeR + 1) / 2];
		
		Random rand = new Random();
		
		// Random row and column number is generated and assigned to their corresponding variables
		// The do-while loop ensures the cell generated exists only inside the maze
		do
		{
			currentR = rand.nextInt(sizeR);
			currentC = ((sizeR + 1) / 2) + (int)(Math.random() * ((sizeC + (sizeR + 1) / 2) - ((sizeR + 1) / 2)));
		} while(!isInHex(currentR, currentC, maze));
		
		// Starting cell created by passing in the starting row and column coordinates
		Cell cell = new Cell(currentR, currentC);
		
		// Stack that will keep count of cells as we move along the maze
		LinkedList<Cell> stack = new LinkedList<Cell>();
		
		// A cell in a hex maze would have six neighbors. Hence the array size was set to 6.
		int[] neighbours = new int[6];
		
		int freeNeighbourCount = 0;
		
		do
		{
			 // Set the visited status of the current cell to true
			 visited[cell.r][cell.c] = true;			 
			 freeNeighbourCount = 0;
			 
			 /* For each direction, check if the neighbor in that direction of the current cell is inside 
			 the maze and has not been visited. The conditions stated are in order of the if statements written below. 
			 If the above conditions are met, add the direction index to the neighbours array. A value of 1 was added 
			 to each valid neighbor cell entry as it was not possible to add the EAST direction to the array as it had a value of 0 */
			 for (int i = 0; i < maze.NUM_DIR; i++) 
			 {		 
				if (isInHex(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{		
					 if (!visited[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]])
					 { 
					 	neighbours[freeNeighbourCount++] = i+1;					
					 }
				}
			 }
			 
			// If valid neighbors are present then..
			 if (freeNeighbourCount > 0) 
			 {	
				// Create and add the cell to the stack. This cell's coordinates will be the changed to the
				 // coordinates of the next cell to which we will move to.
				 cell = new Cell(cell.r, cell.c);
				 stack.addFirst(cell);
				 Cell neigh = new Cell();
				 
				// Function that decides which cell to carve a path to and assigns that cell to the current
				 // cell. A more detailed explanation is given in the function declaration.
				 nextCell (maze, cell, stack, neigh, neighbours, freeNeighbourCount);
			 }
			// If no valid neighbors were present, then remove the current cell from the stack. In other words, if 
			 // the current cell had no unvisited neighbors then remove it from the stack.
			 else 
			 {				 
				 cell = stack.removeFirst();
			 }
			
		} while (!stack.isEmpty());
	}
	

	
	// Checks if the cell is inside the hex maze. 
	private boolean isInHex(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= (r + 1) / 2 && c < maze.sizeC + (r + 1) / 2;
	} 
	
	// Checks if the cell is inside the maze.
	private boolean isIn(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= 0 && c < maze.sizeC;
	}
	
	 /** 
     * Path carving and assignment of random neighbour cell to current cell
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM  nextCell (maze, cell, stack, neigh, neighbours, freeNeighbourCount)
     * Randomly generate a direction for a path to be carved from current cell to the neighbour cell
     * Input: Maze maze, Cell cell, LinkedList<Cell> stack, Cell neigh, int[] neighbours, int freeNeighbourCount
     * OUTPUT : Current cell with coordinates of the neighbouring cell to which a path was carved to.
     * 
     * 1: Randomly select a direction from the neighbours array
     * 2: Carve a path between the neighbour in the direction and the current cell
     * 3: The current cell becomes the neighbour cell		
	 *
     * ******************************************************************************************
     * 
     * @param maze 					Input Maze.
     * @param cell 					current cell.
     * @param stack 				Linked list that keeps track of the cells.
     * @param neigh 				an empty cell.
     * @param neighbours 			the number of neighbours for the current cell.
     * @param freeNeighboursCount	the number of unvisited neighbours for the current cell
     * 
     * */
	private void nextCell (Maze maze, Cell cell, LinkedList<Cell> stack, Cell neigh, int[] neighbours, int freeNeighbourCount)
	{
		Random rand = new Random();
		
		if (maze.type == maze.NORMAL || maze.type == maze.TUNNEL)
		{
			// Randomly choose a cell from the neighbours array and carve a path between the current cell
			// and the neighboring cell by breaking down the wall in between them. Then change the 
			// cell coordinates of the previously created cell to the coordinates of the chosen neighbour cell
			 switch (neighbours[rand.nextInt(freeNeighbourCount)]-1) 
			 {			
				 case Maze.NORTH:				 
					 neigh = maze.map[cell.r + maze.deltaR[maze.NORTH]][cell.c + maze.deltaC[maze.NORTH]];
					 cell.wall[maze.NORTH] = neigh.wall[maze.oppoDir[maze.NORTH]];
					 cell.wall[maze.NORTH].present = false;			
					 cell.r = neigh.r;				 
				 break;
				
				 case Maze.EAST:				
					 neigh = maze.map[cell.r + maze.deltaR[maze.EAST]][cell.c + maze.deltaC[maze.EAST]];
					 cell.wall[maze.EAST] = neigh.wall[maze.oppoDir[maze.EAST]];
					 cell.wall[maze.EAST].present = false;						 
					 cell.c = neigh.c;					
				 break;
				
				 case Maze.SOUTH:					
					 neigh = maze.map[cell.r + maze.deltaR[maze.SOUTH]][cell.c + maze.deltaC[maze.SOUTH]];
					 cell.wall[maze.SOUTH] = neigh.wall[maze.oppoDir[maze.SOUTH]];
					 cell.wall[maze.SOUTH].present = false;					 
					 cell.r = neigh.r;					 
				 break;
				 
				 case Maze.WEST:					 
					 neigh = maze.map[cell.r + maze.deltaR[maze.WEST]][cell.c + maze.deltaC[maze.WEST]];
					 cell.wall[maze.WEST] = neigh.wall[maze.oppoDir[maze.WEST]];
					 cell.wall[maze.WEST].present = false;					 
					 cell.c = neigh.c;					
				 break;			
			 }
		}
		else
		{
			// Randomly choose a cell from the neighbours array and carve a path between the current cell
			// and the neighboring cell by breaking down the wall in between them. Then change the 
			// cell coordinates of the previously created cell to the coordinates of the chosen neighbour cell
			switch (neighbours[rand.nextInt(freeNeighbourCount)]-1) 
			 {			
				 case Maze.NORTHEAST:				 
					 neigh = maze.map[cell.r + maze.deltaR[maze.NORTHEAST]][cell.c + maze.deltaC[maze.NORTHEAST]];
					 cell.wall[maze.NORTHEAST] = neigh.wall[maze.oppoDir[maze.NORTHEAST]];
					 cell.wall[maze.NORTHEAST].present = false;			
					 cell.r++;
					 cell.c++;
				 break;
				 
				 case Maze.NORTHWEST:				 
					 neigh = maze.map[cell.r + maze.deltaR[maze.NORTHWEST]][cell.c + maze.deltaC[maze.NORTHWEST]];
					 cell.wall[maze.NORTHWEST] = neigh.wall[maze.oppoDir[maze.NORTHWEST]];
					 cell.wall[maze.NORTHWEST].present = false;			
					 cell.r++;
				 break;
				
				 case Maze.EAST:				
					 neigh = maze.map[cell.r + maze.deltaR[maze.EAST]][cell.c + maze.deltaC[maze.EAST]];
					 cell.wall[maze.EAST] = neigh.wall[maze.oppoDir[maze.EAST]];
					 cell.wall[maze.EAST].present = false;						 
					 cell.c++;					
				 break;
				
				 case Maze.SOUTHEAST:					
					 neigh = maze.map[cell.r + maze.deltaR[maze.SOUTHEAST]][cell.c + maze.deltaC[maze.SOUTHEAST]];
					 cell.wall[maze.SOUTHEAST] = neigh.wall[maze.oppoDir[maze.SOUTHEAST]];
					 cell.wall[maze.SOUTHEAST].present = false;					 
					 cell.r--;
				 break;
				 
				 case Maze.SOUTHWEST:					
					 neigh = maze.map[cell.r + maze.deltaR[maze.SOUTHWEST]][cell.c + maze.deltaC[maze.SOUTHWEST]];
					 cell.wall[maze.SOUTHWEST] = neigh.wall[maze.oppoDir[maze.SOUTHWEST]];
					 cell.wall[maze.SOUTHWEST].present = false;					 
					 cell.r--;
					 cell.c--;
				 break;
				 
				 case Maze.WEST:					 
					 neigh = maze.map[cell.r + maze.deltaR[maze.WEST]][cell.c + maze.deltaC[maze.WEST]];
					 cell.wall[maze.WEST] = neigh.wall[maze.oppoDir[maze.WEST]];
					 cell.wall[maze.WEST].present = false;					 
					 cell.c--;
				 break;			
			 }
		}
	}
		
	
} // end of class RecursiveBacktrackerGenerator
