package mazeGenerator;

import java.util.Random;
import java.util.Stack;
import maze.Cell;
import maze.Maze;

public class KruskalGenerator implements MazeGenerator 
{
	 /** 
     * Generate a maze using Kruskal's algorithm.
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM normalAndTunnelGeneration (maze) or hexGeneration (maze)
     * Performs the generation of the maze using the algorithm.
     * Input: Maze maze.
     * OUTPUT : Maze with paths carved between cells in such a way that there is no loops and there is
     * 			a definite path between the entrance and the exit.
     * 
     * 1: Create edges between cells using the "walls" to represent them and put them in a stack.
     * 2: Randomize the edges in the stack
     * 3: Select an edge
     * 4: If the edge selected joins two separated sets of cells(trees), 
     *    join them and pop the edge from the stack
     * 5: Otherwise, pop the edge without connecting the trees
     * 6: Repeat from step 3 until the stack is empty, 
     *    which means there are no more edges to be connected
     * 
     * ******************************************************************************************
     * 
     * @param Maze maze: takes the maze to manipulate it and create paths.
     */
	@Override
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
	
	/*************************************************
	 * Implement Kruskal's algorithm.
	 * 
	 * (1) Select an edge
	 * (2) If the sets of cells aren't already connected, 
	 *     connect the sets 
	 * (4) Carve the path between the sets of cells
	 * (5) Keep repeating until all the edges are popped from the stack
	 *************************************************/
	private void normalAndTunnelGeneration(Maze maze)
	{
		Stack<Edge> edges = new Stack<Edge>();
		
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		int row, column, direction;
		
		Random rand = new Random();
		
		// Create stack of edges from each wall connecting two cells
		for(int i = 0; i < sizeR; i++)
		{
			for(int j = 0; j < sizeC; j++)
			{
				// Only add the north and east edges between each cell, if they exist
				// This gets the edges for each wall in the maze without repeating the edges
				if (isIn(i + maze.deltaR[maze.NORTH], j + maze.deltaC[maze.NORTH], maze))
				{ 
					edges.add(new Edge(i, j, maze.NORTH)); 
				}
				if (isIn(i + maze.deltaR[maze.EAST], j + maze.deltaC[maze.EAST], maze))
				{
					edges.add(new Edge(i, j, maze.EAST));
				}
			}
		}
		
		// Randomize the edges in the stack
		for (int i = 0; i < edges.size(); i++) 
		{
			int edgeIndex = rand.nextInt(edges.size());
			// Get a random edge from the stack
			Edge tmp1 = edges.get(i);
			Edge tmp2 = edges.get(edgeIndex);
			
			// Then swap them with the current edge in the stack
			edges.set(i, tmp2);
			edges.set(edgeIndex, tmp1);
		}
		
		
		do
		{
			// Select and pop an edge from the stack
			Edge edge = edges.pop();
			
			row = edge.getRow();
			column = edge.getColumn();
			direction = edge.getDirection();
			
			Cell current = new Cell();
			// Get one of the two cells connected by the edge
			current = maze.map[row][column];
			
			Cell next = new Cell();
			// Get the other cell connected by the same edge
			next = maze.map[row + maze.deltaR[direction]][column + maze.deltaC[direction]];
			
			Cell previous = new Cell();
			
			// Checks if the edge doesn't already connect to the same set of cells,
			// so that a loop may not occur
			if(checkEdgeExistance(maze, next, current, previous) != false)
			{
				// Carve a path
				current.wall[direction].present = false;
			}
		}
		// Repeat until the stack is emptied
		while(!edges.isEmpty());
	}
	
	// Same as normalAndTunnelGeneration() with slight changes to accommodate the hex structure
	private void hexGeneration(Maze maze)
	{
		Stack<Edge> edges = new Stack<Edge>();
		
		int sizeR = maze.sizeR;
		int sizeC = maze.sizeC;
		
		int row, column, direction;
		
		Random rand = new Random();
		
		// Create stack of edges from each wall connecting two cells
		for(int i = 0; i < sizeR; i++)
		{
			for(int j = (i + 1) / 2; j < sizeC + (i + 1) / 2; j++)
			{
				// Only add the northeast, northwest and east edges between each cell, if they exist
				// This gets the edges for each wall in the maze without repeating the edges
				if (isInHex(i + maze.deltaR[maze.NORTHEAST], j + maze.deltaC[maze.NORTHEAST], maze))
				{ 
					edges.add(new Edge(i, j, maze.NORTHEAST)); 
				}
				if (isInHex(i + maze.deltaR[maze.NORTHWEST], j + maze.deltaC[maze.NORTHWEST], maze))
				{ 
					edges.add(new Edge(i, j, maze.NORTHWEST)); 
				}
				if (isInHex(i + maze.deltaR[maze.EAST], j + maze.deltaC[maze.EAST], maze))
				{
					edges.add(new Edge(i, j, maze.EAST));
				}				
			}
		}
		
		for (int i = 0; i < edges.size(); i++) 
		{
			int edgeIndex = rand.nextInt(edges.size());
			Edge tmp1 = edges.get(i);
			Edge tmp2 = edges.get(edgeIndex);
			edges.set(i, tmp2);
			edges.set(edgeIndex, tmp1);
		}
		
		do
		{
			Edge edge = edges.pop();
			
			row = edge.getRow();
			column = edge.getColumn();
			direction = edge.getDirection();
			
			Cell current = new Cell();
			current = maze.map[row][column];
			
			Cell next = new Cell();
			next = maze.map[row + maze.deltaR[direction]][column + maze.deltaC[direction]];
			
			Cell previous = new Cell();

			if(checkEdgeExistance(maze, next, current, previous) != false)
			{
				current.wall[direction].present = false;
			}
		}
		while(!edges.isEmpty());
	}
	
	/* 
	 * Checks if the edge doesn't already connect to the same set of cells
	 * By taking the cell one of the cells connected by the edge 
	 * to check if the there is another way through the maze to get that cell
	 * 
	 * 
	 * @param Maze maze: to check its type.
	 * @param Cell cell: The cell on the end of the edge
	 * @param Cell check: The first cell of edge
	 * @param Cell previous: THe previous cell visited
	 * 
	 * @return: True if there is no other way to the check cell
	 * @return: false if there is another way the cell being connected to another set
	 *          which means that the cells being connected by the edge are already in 
	 *          the same set
	 */
	public boolean checkEdgeExistance(Maze maze, Cell cell, Cell check, Cell previous)
	{
		// Checks if the traversing reached the same cell in the maze
		if(check.r == cell.r && check.c == cell.c)
		{
			return false;
		}
		
		if (maze.type == maze.NORMAL)
		{
			for(int i = 0; i < maze.NUM_DIR; i++)
			{
				if(i == 1 || i == 4)
				{
					continue;	
				}
				
				if (isIn(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{
					// Checks if the next cell is not the same as the previous cell so the traversing 
					// doesn't go back to a cell that has been already checked
					if(cell.neigh[i] != previous)
					{
						// Checks if there is no existing path
						if(cell.wall[i].present != true)
						{
							Cell next = new Cell();
							next = maze.map[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]];
							
							// Repeat the process until we are sure that the cells being connected aren't in the same set
							if(checkEdgeExistance(maze, next, check, cell) == false)
							{
								return false;
							}
						}
					}
				}
			}
		}
		else if (maze.type == maze.HEX)
		{
			for(int i = 0; i < maze.NUM_DIR; i++)
			{
				if (isInHex(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{
					// Checks if the next cell is not the same as the previous cell so the traversing 
					// doesn't go back to a cell that has been already checked
					if(cell.neigh[i] != previous)
					{
						// Checks if there is no existing path
						if(cell.wall[i].present != true)
						{
							Cell next = new Cell();
							next = maze.map[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]];
							
							// Repeat the process until we are sure that the cells being connected aren't in the same set
							if(checkEdgeExistance(maze, next, check, cell) == false)
							{
								return false;
							}
						}
					}
				}
			}
		}
		else if (maze.type == maze.TUNNEL)
		{
			// Gets the cell at end of the tunnel
			if (maze.map[cell.r][cell.c].tunnelTo != null)
			{
				// Checks if the next cell is not the same as the previous cell so the traversing 
				// doesn't go back to a cell that has been already checked
				if(cell.tunnelTo != previous)
				{
					Cell next = new Cell();
					next = maze.map[cell.r][cell.c].tunnelTo;
					
					// Repeat the process until we are sure that the cells being connected aren't in the same set
					if(checkEdgeExistance(maze, next, check, cell) == false)
					{
						return false;
					}				
				}
			}
			 
			for(int i = 0; i < maze.NUM_DIR; i++)
			{
				if(i == 1 || i == 4)
				{
					continue;	
				}
				
				if (isIn(cell.r + maze.deltaR[i], cell.c + maze.deltaC[i], maze)) 
				{
					// Checks if the next cell is not the same as the previous cell so the traversing 
					// doesn't go back to a cell that has been already checked
					if(cell.neigh[i] != previous)
					{
						// Checks if there is no existing path
						if(cell.wall[i].present != true)
						{
							Cell next = new Cell();
							next = maze.map[cell.r + maze.deltaR[i]][cell.c + maze.deltaC[i]];
							
							// Repeat the process until we are sure that the cells being connected aren't in the same set
							if(checkEdgeExistance(maze, next, check, cell) == false)
							{
								return false;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private boolean isIn(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= 0 && c < maze.sizeC;
	}
	
	private boolean isInHex(int r, int c, Maze maze) 
	{
		return r >= 0 && r < maze.sizeR && c >= (r + 1) / 2 && c < maze.sizeC + (r + 1) / 2;
	} 
	
	
	/* 
	 * This class creates an object of an edge
	 * that contains the row and column of a cell and
	 * the direction its heading to create an edge between two cells
	 */
	public class Edge
	{
		private int row;
		private int column;
		private int direction;
		
		public Edge(int row, int column, int direction) 
		{
			this.row = row; 
			this.column = column;
			this.direction = direction;
		}
		
		public int getColumn() 
		{
			return column;
		}
		
		public int getDirection() 
		{
			return direction;
		}
		
		public int getRow() 
		{
			return row;
		}
	}

} // end of class KruskalGenerator
