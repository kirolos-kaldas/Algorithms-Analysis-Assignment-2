package mazeSolver;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import maze.Cell;
import maze.Maze;

/**
 * Implements the recursive backtracking maze solving algorithm.
 */
public class RecursiveBacktrackerSolver implements MazeSolver 
{
	boolean visited[][];
	
	Cell entrance;
	Cell exit;
	
	int count = 0;
	
	/** 
     * Solve a maze using the recursive backtracker algorithm
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM  normalAndTunnelSolver (maze) or hexSolver (maze)
     * Solve normal, tunnel or hex maze using the recursive backtracker solver algorithm.
     * Input: Maze maze.
     * OUTPUT : Solved maze with a marked path from the entrance to the exit.
     * 
     * 1: Get the cell of the entrance 
     * 2: Add the cell to the stack
     * 3: Mark it as visited
     * 4 (Only if maze type is tunnel): Check if the current cell is a tunnel, if so 
     *    then current cell will become the cell at the tunnel exit
     * 5: Get a random neighbour and assign it to be the current cell
     * 6: Repeat from step 2 until the current cell has no unvisited neighbours 
     * 7: If so, then backtrack (pop cell from stack) until you find a cell 
     *    with an unvisited neighbour
     * 8: Repeat from step 5 until the exit is found
     * 
     * ******************************************************************************************
     * 
     * @param maze Input Maze.
     * @returns Solved maze with a drawn path from the entrance to the exit 
     */
	public void solveMaze(Maze maze) 
	{
		if(maze.type == maze.NORMAL || maze.type == maze.TUNNEL)
		{
			normalAndTunnelSolver(maze);
		}		
		else if(maze.type == maze.HEX)
		{
			hexSolver(maze);
		}
		
	} // end of solveMaze()
	
	
	
	public void normalAndTunnelSolver(Maze maze)
	{
		int startingR;
		int startingC;
		boolean alreadyVisited = false;
		
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		// Maze entrance and exit is assigned to the corresponding cells
		entrance = maze.entrance;
		exit = maze.exit;
		
		Random rand = new Random();
		
		// The row and column number of the starting cell is assigned to their corresponding variables
		startingR = entrance.r;
		startingC = entrance.c;
		
		// A boolean array is created to check visited status for each cell
		visited = new boolean[sizeR][sizeC];
		
		// Starting cell created by passing in the starting row and column coordinates
		Cell cell = new Cell(startingR, startingC);
		
		// Cell is reassigned position so that details of each neighboring walls are present as well
		cell = maze.map[startingR][startingC]; 
		
		// Stack that will keep count of cells as we move along the maze
		LinkedList<Cell> stack = new LinkedList<Cell>();
		
		// Add the starting cell to the stack
		stack.addFirst(cell);
		
		// A cell in a normal maze would have four neighbors. Hence the array size was set to 4.
		int[] neighbours = new int[4];
		
		do
		{	 
			if(visited[cell.r][cell.c] != true)
			{
				count++;
			}
			
			 // Set the visited status of the current cell to true 
			 visited[cell.r][cell.c] = true;
			 
			 // Mark the visited cell on the maze
			 maze.drawFtPrt(cell);
			 
			// Checks if the cell has a tunnel..
			 if (maze.map[cell.r][cell.c].tunnelTo != null)
			 {
				 Cell temp = maze.map[cell.r][cell.c].tunnelTo;
				 if (!visited[temp.r][temp.c])
				 {
					// If not then the current cell would be the cell at the other end of the tunnel
					 cell = maze.map[cell.r][cell.c].tunnelTo;
					 // Set the visited status of the cell at the end of the tunnel to true
					 visited[cell.r][cell.c] = true;
					 // Mark the visited cell on the maze
					 maze.drawFtPrt(cell);
					 
					 count++;
				 }
			}
			 
			 int freeNeighbourCount = 0;
			 
			 /* For each valid direction, check if the neighbor in that direction of the current cell is inside 
			 the maze, has not been visited and does not have a wall between itself and the current cell. 
			 The conditions stated are in order of the if statements written below. If the above conditions 
			 are met, add the direction index to the neighbours array. A value of 1 was added to each valid 
			 neighbor cell entry as it was not possible to add the EAST direction to the array as it had a value of 0 */			 
			 for (int i = 0; i < maze.NUM_DIR; i++) 
			 {	
				if (i== 1 || i == 4)
				{
					continue;
				}
				if (isIn(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{		
					 if (!visited[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]])
					 { 
						 if (cell.wall[i].present == false)
						{
							 neighbours[freeNeighbourCount++] = i+1;		
						}
					 }
				}					 
			 }
			 
			 // If valid neighbors were present then..
			 if (freeNeighbourCount > 0) 
			 {	 
				 // Create and add the cell to the stack. This cell's coordinates will be the changed to the
				 // coordinates of the next cell to which we will move to.
				 cell = new Cell(cell.r, cell.c);
				 stack.addFirst(cell);
				 Cell neigh = new Cell();
				 
				 // Randomly choose a cell from the neighbours array and change the current 
				 // cell coordinates to the coordinates of the chosen neighbour cell
				 switch (neighbours[rand.nextInt(freeNeighbourCount)]-1) 
				 {			
					 case Maze.NORTH:				 
						 neigh = maze.map[cell.r + maze.deltaR[maze.NORTH]][cell.c + maze.deltaC[maze.NORTH]];		
						 cell = maze.map[neigh.r][cell.c];
					 break;
					
					 case Maze.EAST:				
						 neigh = maze.map[cell.r + maze.deltaR[maze.EAST]][cell.c + maze.deltaC[maze.EAST]];						 
						 cell = maze.map[cell.r][neigh.c];
					 break;
					
					 case Maze.SOUTH:					
						 neigh = maze.map[cell.r + maze.deltaR[maze.SOUTH]][cell.c + maze.deltaC[maze.SOUTH]];
						 cell = maze.map[neigh.r][cell.c];					 
					 break;
					 
					 case Maze.WEST:					 
						 neigh = maze.map[cell.r + maze.deltaR[maze.WEST]][cell.c + maze.deltaC[maze.WEST]];					 
						 cell = maze.map[cell.r][neigh.c];					
					 break;			
				 }
			 }
			// If no valid neighbors were present, then remove the current cell from the stack. In other words, if 
		    // the current cell had no unvisited neighbors then remove it from the stack.
			 else 
			 {				 
				 if (maze.map[cell.r][cell.c].tunnelTo != null && alreadyVisited != true)
				 {
					 cell = maze.map[cell.r][cell.c].tunnelTo;
					 alreadyVisited = true;
				 }
				 else
				 {
					 cell = stack.removeFirst();
					 cell = maze.map[cell.r][cell.c];
				 }
			 }
			 
			 // Check if the current cell coordinates is equal to the coordinates of the exit cell
			 // If so then break the loop
			 if (cell.r == exit.r && cell.c == exit.c)
			 {
				 visited[cell.r][cell.c] = true;
				 maze.drawFtPrt(cell);
				 count++;
				 break;
			 }
			
		} while (!stack.isEmpty());
	}
	
	
	public void hexSolver(Maze maze)
	{	
		int startingR;
		int startingC;
		
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		// Maze entrance and exit is assigned to the corresponding cells
		entrance = maze.entrance;
		exit = maze.exit;
		
		// A boolean array is created to check visited status for each cell
		visited = new boolean[sizeR][sizeC + (sizeR + 1) / 2];
		
		// The row and column number of the starting cell is assigned to their corresponding variables
		startingR = entrance.r;
		startingC = entrance.c;
		
		Random rand = new Random();
		
		// Starting cell created by passing in the starting row and column coordinates
		Cell cell = new Cell(startingR, startingC);
		
		// Cell is reassigned position so that details of each neighboring walls are present as well
		cell = maze.map[startingR][startingC]; 
		
		// Stack that will keep count of cells as we move along the maze
		LinkedList<Cell> stack = new LinkedList<Cell>();
		
		// Add the starting cell to the stack
		stack.addFirst(cell);
		
		// A cell in a hex maze would have six neighbors. Hence the array size was set to 6.
		int[] neighbours = new int[6];
		
		do
		{	 
			if(visited[cell.r][cell.c] != true)
			{
				count++;
			}
			
			 // Set the visited status of the current cell to true
			 visited[cell.r][cell.c] = true;
			 
			 // Mark the visited cell on the maze
			 maze.drawFtPrt(cell);
			 
			 int freeNeighbourCount = 0;
			 
			 /* For each direction, check if the neighbor in that direction of the current cell is inside 
			 the maze, has not been visited and does not have a wall between itself and the current cell. 
			 The conditions stated are in order of the if statements written below. If the above conditions 
			 are met, add the direction index to the neighbours array. A value of 1 was added to each valid 
			 neighbor cell entry as it was not possible to add the EAST direction to the array as it had a value of 0 */
			 for (int i = 0; i < maze.NUM_DIR; i++) 
			 {			 
				if (isInHex(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{		
					 if (!visited[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]])
					 {
						 if (cell.wall[i].present == false)
						 {
							 neighbours[freeNeighbourCount++] = i+1;
						 }
					 }
				}					 
			 }
			 
			// If valid neighbors were present then..
			 if (freeNeighbourCount > 0) 
			 {	 
				 // Create and add the cell to the stack. This cell's coordinates will be the changed to the
				 // coordinates of the next cell to which we will move to.
				 cell = new Cell(cell.r, cell.c);
				 stack.addFirst(cell);
				 Cell neigh = new Cell();
				 
				 // Randomly choose a cell from the neighbours array and change the current 
				 // cell coordinates to the coordinates of the chosen neighbour cell
				 switch (neighbours[rand.nextInt(freeNeighbourCount)]-1) 
				 {			
					 case Maze.NORTHEAST:				 
						 cell = maze.map[cell.r + maze.deltaR[maze.NORTHEAST]][cell.c + maze.deltaC[maze.NORTHEAST]];		
					 break;
					 
					 case Maze.NORTHWEST:				 
						 cell = maze.map[cell.r + maze.deltaR[maze.NORTHWEST]][cell.c + maze.deltaC[maze.NORTHWEST]];			
					 break;
					
					 case Maze.EAST:				
						 cell = maze.map[cell.r + maze.deltaR[maze.EAST]][cell.c + maze.deltaC[maze.EAST]];						 
					 break;
					
					 case Maze.SOUTHEAST:					
						 cell = maze.map[cell.r + maze.deltaR[maze.SOUTHEAST]][cell.c + maze.deltaC[maze.SOUTHEAST]];				 
					 break;
					 
					 case Maze.SOUTHWEST:					
						 cell = maze.map[cell.r + maze.deltaR[maze.SOUTHWEST]][cell.c + maze.deltaC[maze.SOUTHWEST]];				 
					 break;
					 
					 case Maze.WEST:					 
						 cell = maze.map[cell.r + maze.deltaR[maze.WEST]][cell.c + maze.deltaC[maze.WEST]];				 
					 break;			
				 }
			 } 
			// If no valid neighbors were present, then remove the current cell from the stack. In other words, if 
			// the current cell had no unvisited neighbors then remove it from the stack.
			 else 
			 {				 
				 cell = stack.removeFirst();
				 cell = maze.map[cell.r][cell.c];
			 }
			// Check if the current cell coordinates is equal to the coordinates of the exit cell
			 // If so then break the loop
			 if (cell.r == exit.r && cell.c == exit.c)
			 {
				 visited[cell.r][cell.c] = true;
				 maze.drawFtPrt(cell);
				 count++;
				 break;
			 }
			
		} while (!stack.isEmpty());
	}
	
	@Override
	public boolean isSolved() 
	{
		if (visited[entrance.r][entrance.c] == true)
		{
			if (visited[exit.r][exit.c] == true)
			{
				return true;
			}
		}
		
		return false;
	} // end if isSolved()


	@Override
	public int cellsExplored() 
	{
		return count;
	} // end of cellsExplored()

	
	// Checks if the cell is inside the maze and false if not
	private boolean isIn(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= 0 && c < maze.sizeC;
	}
	
	// Checks if the cell is null, if not then it checks if its inside the maze by calling the fucntion
	protected boolean isIn(Cell cell, Maze maze) 
	{
		if (cell == null)
			return false;
		return isIn(cell.r, cell.c, maze);
	}
	
	// Checks if the cell is inside the hex maze
	private boolean isInHex(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= (r + 1) / 2 && c < maze.sizeC + (r + 1) / 2;
	} 
} // end of class RecursiveBackTrackerSolver
