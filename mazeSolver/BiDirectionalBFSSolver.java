package mazeSolver;

import java.util.LinkedList;
import java.util.Random;

import maze.Cell;
import maze.Maze;

/** 
 * Implements Bi-directional BFS maze solving algorithm.
 */
public class BiDirectionalBFSSolver implements MazeSolver 
{	
	// Visited cells from each direction (entrance and exit)
	boolean visitedFromEntrance[][];
	boolean visitedFromExit[][];
	
	int sizeR;
	int sizeC;
	
	// Counts the cells visited
	int count = 0;
	
	/** 
     * Solve a maze using Bidirectional BFS algorithm.
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM normalAndTunnelSolver (maze) or hexSolver (maze)
     * Performs the solution of the maze using the algorithm.
     * Input: Maze maze.
     * OUTPUT : Solution of the maze and showing the visited cells on the it.
     * 
     * 1: Get the cell of the entrance and exit, add them to different linkedlists
     * 2: Get the current two cells' neighbours, add them to different linkedlists
     * 3: From the neighbours, get two current cells, one from the entrance linkedlist
     *    and one from the exit linkedlist
     * 4: Check if the current two cells from the entrance and exit directions
     *    have been visited by the opposites direction, for e.g, a current cell from 
     *    the direction of the entrance is checked if visited by the exit direction
     * 5: IF it has been visited, then that means there's a solution between the exit
     *    and the entrance.
     * 6: Otherwise, repeat from step 2 until a solution has been generated.
     * 
     * ******************************************************************************************
     * 
     * @param Maze maze: takes the maze to draw the solution on it.
     */
	@Override
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
	
	
	/*************************************************
	 * Implement Bidirectional BFS algorithm.
	 * 
	 * (1) Get the cell of the entrance and exit, 
	 *     add them to different linkedlists
	 * (2) Draw the two cells on the graph to 
	 *     indicate that they have been visited 
	 * (3) Get the current two cells' neighbours, 
	 *     add them to the different directions linkedlists    
	 * (4) From the neighbours, get two current cells, 
	 *     one from the entrance linkedlist
	 *     and one from the exit linkedlist 
	 * (5) Draw the two cells on the graph to 
	 *     indicate that they have been visited
	 * (6) Keep repeating from step 3, until one of the cells
	 *     from any direction has been found in the visited 
	 *     cells from the opposite direction.
	 *************************************************/
	public void normalAndTunnelSolver(Maze maze)
	{
		int startingREnt, startingRExi;
		int startingCEnt, startingCExi;
		
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		sizeR = maze.sizeR;
		sizeC = maze.sizeC;
		
		// Maze entrance and exit is assigned to the corresponding cells
		Cell entrance = maze.entrance;
		Cell exit = maze.exit;
				
		// The row and column number of the entrance cell is assigned to their corresponding variables
		startingREnt = entrance.r;
		startingCEnt = entrance.c;
		
		// The row and column number of the exit cell is assigned to their corresponding variables
		startingRExi = exit.r;
		startingCExi = exit.c;
		
		// A boolean array is created to check visited status for each cell from entrance and from exit
		visitedFromEntrance = new boolean[sizeR][sizeC];
		visitedFromExit = new boolean[sizeR][sizeC];
		
		// Entrance cell created by passing in the starting row and column coordinates
		Cell cellFromEnt = new Cell(startingREnt, startingCEnt);
		
		// Exit cell created by passing in the starting row and column coordinates
		Cell cellFromExit = new Cell(startingRExi, startingCExi);
		
		// Cells are reassigned position so that details of each neighboring walls are present as well
		cellFromEnt = maze.map[startingREnt][startingCEnt];
		cellFromExit = maze.map[startingRExi][startingCExi];
		
		// Stack that will keep count of cells as we move along the maze from entrance
		LinkedList<Cell> stackFromEntrance = new LinkedList<Cell>();
		
		// Stack that will keep count of cells as we move along the maze from exit
		LinkedList<Cell> stackFromExit = new LinkedList<Cell>();
		
		// Add the starting cell to the stack
		stackFromEntrance.addFirst(cellFromEnt);
		stackFromExit.addFirst(cellFromExit);
		
		while(stackFromEntrance.isEmpty() != true || stackFromExit.isEmpty() != true)
		{	
			// Set the visited status of the current cell to true 
			visitedFromEntrance[cellFromEnt.r][cellFromEnt.c] = true;
			visitedFromExit[cellFromExit.r][cellFromExit.c] = true;
			 
			// Mark the visited cells on the maze
			maze.drawFtPrt(cellFromEnt);
			maze.drawFtPrt(cellFromExit);
			
			count++;
			
			 if (maze.map[cellFromEnt.r][cellFromEnt.c].tunnelTo != null)
			 {
				 // If it does then the current cell would be the cell at the other end of the tunnel
				cellFromEnt = maze.map[cellFromEnt.r][cellFromEnt.c].tunnelTo;
				 // Set the visited status of the cell at the end of the tunnel to true
				visitedFromEntrance[cellFromEnt.r][cellFromEnt.c] = true;
				 // Mark the visited cell on the maze
				 maze.drawFtPrt(cellFromEnt);
				 
				 count++;
			 }
			
			 if (maze.map[cellFromExit.r][cellFromExit.c].tunnelTo != null)
			 {
				 // If it does then the current cell would be the cell at the other end of the tunnel
				cellFromExit = maze.map[cellFromExit.r][cellFromExit.c].tunnelTo;
				 // Set the visited status of the cell at the end of the tunnel to true
				visitedFromExit[cellFromExit.r][cellFromExit.c] = true;
				 // Mark the visited cell on the maze
				 maze.drawFtPrt(cellFromExit);
				 
				 count++;
			 }
			 
			 // Get the neighbours of the entrance and exit directions
			 // Then add them to the linked lists
			 for (int i = 0; i < maze.NUM_DIR; i++) 
			 {
				 if(i == 1 || i == 4)
				 {
					 continue;
				 }
				 
				 if (isIn(cellFromEnt.r + maze.deltaR[i], cellFromEnt.c + maze.deltaC[i], maze)) 
					{		
					 	// Checks if the next cell has not been visited yet
						if (!visitedFromEntrance[cellFromEnt.r + maze.deltaR[i]][cellFromEnt.c + maze.deltaC[i]])
						{
							// Checks if there's a path between the current cell and the next
							if (cellFromEnt.wall[i].present == false)
							{
								// Checks if the next cell doesn't already exist in the linkedlist
								if(stackFromEntrance.equals(cellFromEnt.neigh[i]) != true)
								 {
									 stackFromEntrance.addLast(cellFromEnt.neigh[i]);
								 }
							}
						}
					}
					
					if (isIn(cellFromExit.r + maze.deltaR[i], cellFromExit.c + maze.deltaC[i], maze)) 
					{		
						// Checks if the next cell has not been visited yet
						if (!visitedFromExit[cellFromExit.r + maze.deltaR[i]][cellFromExit.c + maze.deltaC[i]])
						{
							// Checks if there's a path between the current cell and the next
							if (cellFromExit.wall[i].present == false)
							{
								// Checks if the next cell doesn't already exist in the linkedlist
								if(stackFromExit.equals(cellFromExit.neigh[i]) != true)
								 {
									 stackFromExit.addLast(cellFromExit.neigh[i]);
								 }
							}
						}
					}
			 }
			 
			 
			 // Get the next cell from entrance direction from the stack
			 cellFromEnt = stackFromEntrance.removeFirst();
			 // Checks if the entrance cell has been visited in by the cells from the exit direction
			 if (visitedFromExit[cellFromEnt.r][cellFromEnt.c] == true)
			 {
				 visitedFromEntrance[cellFromEnt.r][cellFromEnt.c] = true;
				 count++;
				 break;
			 }
			 
			// Get the next cell from the exit direction from the stack
			 cellFromExit = stackFromExit.removeFirst();
			// Checks if the exit cell has been visited in by the cells from the entrance direction
			 if (visitedFromEntrance[cellFromExit.r][cellFromExit.c] == true)
			 {
				 visitedFromExit[cellFromExit.r][cellFromExit.c] = true;
				 count++;
				 break;
			 }
		}
	}
	
	/* Same implementation as normalAndTunnelGeneration() with slight changes to accommodate the hex structure*/
	public void hexSolver(Maze maze)
	{
		int startingREnt, startingRExi;
		int startingCEnt, startingCExi;
		
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		sizeR = maze.sizeR;
		sizeC = maze.sizeC;
		
		// Maze entrance and exit is assigned to the corresponding cells
		Cell entrance = maze.entrance;
		Cell exit = maze.exit;
				
		// The row and column number of the entrance cell is assigned to their corresponding variables
		startingREnt = entrance.r;
		startingCEnt = entrance.c;
		
		// The row and column number of the exit cell is assigned to their corresponding variables
		startingRExi = exit.r;
		startingCExi = exit.c;
		
		// A boolean array is created to check visited status for each cell from entrance and from exit
		visitedFromEntrance = new boolean[sizeR][sizeC + (sizeR + 1) / 2];
		visitedFromExit = new boolean[sizeR][sizeC + (sizeR + 1) / 2];
		
		// Entrance cell created by passing in the starting row and column coordinates
		Cell cellFromEnt = new Cell(startingREnt, startingCEnt);
		
		// Exit cell created by passing in the starting row and column coordinates
		Cell cellFromExit = new Cell(startingRExi, startingCExi);
		
		// Cells are reassigned position so that details of each neighboring walls are present as well
		cellFromEnt = maze.map[startingREnt][startingCEnt];
		cellFromExit = maze.map[startingRExi][startingCExi];
		
		// Stack that will keep count of cells as we move along the maze from entrance
		LinkedList<Cell> stackFromEntrance = new LinkedList<Cell>();
		
		// Stack that will keep count of cells as we move along the maze from exit
		LinkedList<Cell> stackFromExit = new LinkedList<Cell>();
		
		// Add the starting cell to the stack
		stackFromEntrance.addFirst(cellFromEnt);
		stackFromExit.addFirst(cellFromExit);
		
		
		while(stackFromEntrance.isEmpty() != true || stackFromExit.isEmpty() != true)
		{	
			// Set the visited status of the current cell to true 
			visitedFromEntrance[cellFromEnt.r][cellFromEnt.c] = true;
			visitedFromExit[cellFromExit.r][cellFromExit.c] = true;
			 
			// Mark the visited cells on the maze
			maze.drawFtPrt(cellFromEnt);
			maze.drawFtPrt(cellFromExit);
			 
			 count++;
			 
			 for (int i = 0; i < maze.NUM_DIR; i++) 
			 {					 
					if (isInHex(cellFromEnt.r + maze.deltaR[i], cellFromEnt.c + maze.deltaC[i], maze)) 
					{		
						if (!visitedFromEntrance[cellFromEnt.r + maze.deltaR[i]][cellFromEnt.c + maze.deltaC[i]])
						{
							if (cellFromEnt.wall[i].present == false)
							{
								if(stackFromEntrance.equals(cellFromEnt.neigh[i]) != true)
								 {
									 stackFromEntrance.addLast(cellFromEnt.neigh[i]);
								 }
							}
						}
					}
					
					if (isInHex(cellFromExit.r + maze.deltaR[i], cellFromExit.c + maze.deltaC[i], maze)) 
					{		
						if (!visitedFromExit[cellFromExit.r + maze.deltaR[i]][cellFromExit.c + maze.deltaC[i]])
						{
							if (cellFromExit.wall[i].present == false)
							{
								if(stackFromExit.equals(cellFromExit.neigh[i]) != true)
								 {
									 stackFromExit.addLast(cellFromExit.neigh[i]);
								 }
							}
						}
					}
			 }
			 
			 
			 cellFromEnt = stackFromEntrance.removeFirst();
			 if (visitedFromExit[cellFromEnt.r][cellFromEnt.c] == true)
			 {
				 visitedFromEntrance[cellFromEnt.r][cellFromEnt.c] = true;
				 count++;
				 break;
			 }
			 
			 cellFromExit = stackFromExit.removeFirst();
			 if (visitedFromEntrance[cellFromExit.r][cellFromExit.c] == true)
			 {
				 visitedFromExit[cellFromExit.r][cellFromExit.c] = true;
				 count++;
				 break;
			 }
		}
	}
	
	// checks if the maze has been solved
	@Override
	public boolean isSolved() 
	{
		for(int i = 0; i < sizeR; i++)
		{
			for(int j = 0; j < sizeC; j++)
			{
				// Checks if there's a link between the two searches which if there is then there's a solution
				if (visitedFromEntrance[i][j] == visitedFromExit[i][j])
				{
					return true;
				}
			}
		}
		return false;
	} // end of isSolved()


	// Returns the number of cells visited
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
	
	// Checks if the cell is inside the hex maze
	private boolean isInHex(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= (r + 1) / 2 && c < maze.sizeC + (r + 1) / 2;
	}

} // end of class BiDirectionalBFSSolver

