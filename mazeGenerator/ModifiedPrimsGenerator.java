package mazeGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import maze.Cell;
import maze.Maze;


public class ModifiedPrimsGenerator implements MazeGenerator 
{
	 /** 
     * Generate a maze using the modified Prim's algorithm 
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM  normalGeneration (maze) or hexGeneration (maze)
     * Perform a maze generation for maze types normal and hex using modified Prim's algorithm.
     * Input: Maze maze.
     * OUTPUT : Maze with paths carved between cells in such a way that there is no loops and there is
     * 			a definite path between the entrance and the exit.
     * 
     * 1: Get a random cell from the maze
     * 2: Add the cell to setZ
     * 3: Get the neighbours of the current cell and add them to the frontier set
     * 4: Randomly select a cell A from frontier
     * 5: Randomly select a cell B from setZ that is adjacent to the cell chosen earlier
     * 6: Remove the cell A from the fronter and carve a path between A and B.
     * 7: Add cell A to the setZ
     * 8: Repeat step 4 until the frontier is empty
     * 
     * ******************************************************************************************
     * 
     * @param maze Input Maze.
     * @returns Maze with paths in between the cells without any loops present
     */
	public void generateMaze(Maze maze) 
	{
		if(maze.type == maze.NORMAL)
		{
			normalGeneration(maze);
		}
		else if(maze.type == maze.HEX)
		{
			hexGeneration(maze);
		}
		else if(maze.type == maze.TUNNEL)
		{
			System.out.println("Invalid maze type");
		}

	} // end of generateMaze()
	
	
	private void normalGeneration(Maze maze)
	{	
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		int currentR;
		int currentC;
		
		boolean cellIsAdjacent = false;
		int dir = 0;
		int random;
		
		Random rand = new Random();
		
		// Random row and column number is generated and assigned to their corresponding variables
		currentR = rand.nextInt(sizeR);
		currentC = rand.nextInt(sizeC);		
		
		// The frontier set that will store the valid neighbors of the current cell at each iteration
		ArrayList<Cell> frontier = new ArrayList<Cell>();
		
		// setZ will be storing the cells that we will move through at each iteration. It will contain all the 
		// cells by the end of the generation.
		ArrayList<Cell> setZ = new ArrayList<Cell>();
		
		// Starting cell created by passing in the starting row and column coordinates
		Cell cell = new Cell(currentR, currentC);
		
		// Cell is reassigned position so that details of each neighboring walls are present as well
		cell = maze.map[currentR][currentC]; 
		
		// Add the current cell to setZ
		setZ.add(cell);
		
		do
		{
			/* For each direction, check if the current cell neighbor in the direction is in the maze and 
			 * if it doesn't already exist in the setZ or in the frontier set.
			 *  Adds neighbor to the frontier set if the above conditions are fulfilled */		
			for (int i = 0; i < maze.NUM_DIR; i++)
			{
				if (i == 1 || i == 4)
				{
					continue;
				}
				if (isIn(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{
					if(setZ.contains(cell.neigh[i]) != true)
					{
						if(frontier.contains(cell.neigh[i]) != true)
						{
							frontier.add(cell.neigh[i]);
						}
					}
				}
			}
		
			cellIsAdjacent = false;
			dir = 0;
			
			// Random number generated based on the frontier size
			random = (int)(Math.random()*frontier.size());
			
			// Random cell chosen from the frontier set using the random number generated earlier
			cell = frontier.get(random);
			
			do
			{	
				// Generate a random direction
				dir = (int)  (Math.random()*maze.NUM_DIR);
				
				/* if the setZ contains the neighbor of the cell in the direction generated
				 * then remove the cell from the frontier, carve a path between the cell and the neighbor
				 * and finally add the cell to the setZ */
				if(setZ.contains(cell.neigh[dir]) == true)
				{
					frontier.remove(random);
					cell.wall[dir].present = false;
					setZ.add(cell);
					cellIsAdjacent = true;
				}
			// repeat until a cell adjacent to the current cell is found
			}while(cellIsAdjacent == false);
			
		}while (!frontier.isEmpty());		
	}
	

	
	private void hexGeneration(Maze maze)
	{
		// Number of rows and number of columns of the maze is assigned to their corresponding variables
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		int currentR;
		int currentC;
		
		boolean cellIsAdjacent = false;
		int dir = 0;
		int random;
		
		Random rand = new Random();
		
		// Random row and column number is generated and assigned to their corresponding variables
		do
		{
			currentR = rand.nextInt(sizeR);
			currentC = ((sizeR + 1) / 2) + (int)(Math.random() * ((sizeC + (sizeR + 1) / 2) - ((sizeR + 1) / 2)));
		}while(!isInHex(currentR, currentC, maze));
		
		// The frontier set that will store the valid neighbors of the current cell at each iteration
		ArrayList<Cell> frontier = new ArrayList<Cell>();
		
		// setZ will be storing the cells that we will move through at each iteration. It will contain 
		// all the cells by the end of the generation.
		ArrayList<Cell> setZ = new ArrayList<Cell>();
		
		// Starting cell created by passing in the starting row and column coordinates
		Cell cell = new Cell(currentR, currentC);
		
		// Cell is reassigned position so that details of each neighboring walls are present as well
		cell = maze.map[currentR][currentC]; 
		
		// Add the current cell to setZ
		setZ.add(cell);
		
		do
		{
			/* For each direction, check if the current cell neighbor in the direction is in the maze and 
			 * if it doesn't already exist in the setZ or in the frontier set.
			 *  Adds neighbor to the frontier set if the above conditions are fulfilled */
			for (int i = 0; i < maze.NUM_DIR; i++)
			{
				if (isInHex(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{
					if(setZ.contains(cell.neigh[i]) != true)
					{
						if(frontier.contains(cell.neigh[i]) != true)
						{
							frontier.add(cell.neigh[i]);
						}
					}
				}
			}
		
			cellIsAdjacent = false;
			dir = 0;
			
			// Random number generated based on the frontier size
			random = (int)(Math.random()*frontier.size());
			
			// Random cell chosen from the frontier set using the random number generated earlier
			cell = frontier.get(random);
			
			do
			{	
				// Generate a random direction
				dir = (int)  (Math.random()*maze.NUM_DIR);
				
				/* if the setZ contains the neighbor of the cell in the direction generated
				 * then remove the cell from the frontier, carve a path between the cell and the neighbor
				 * and finally add the cell to the setZ
				 */
				if(setZ.contains(cell.neigh[dir]) == true)
				{
					frontier.remove(random);
					cell.wall[dir].present = false;
					setZ.add(cell);
					cellIsAdjacent = true;
				}
			// repeat until a cell adjacent to the current cell is found
			}while(cellIsAdjacent == false);
			
		}while (!frontier.isEmpty());	
	}
	
	// Checks if the cell is inside the maze. 
	private boolean isIn(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= 0 && c < maze.sizeC;
	}
	
	// Checks if the cell is inside the hex maze
	private boolean isInHex(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= (r + 1) / 2 && c < maze.sizeC + (r + 1) / 2;
	}
	
} // end of class ModifiedPrimsGenerator
