/*
 * Maze.java 
 * Author: Irene Alvarado 
 * Maze object that creates a maze using a
 * disjoint set representing cells and running a modified version of Kruskal's
 * algorithm to remove walls. In the end, the maze walls are drawn as well as a
 * unique path in red dots.
 */
import java.awt.*;
import java.util.Random;

public class Maze
{
	public static final int CELL_WIDTH = 20; // maze square size
	public static final int MARGIN = 50; // buffer between window edge and maze
	public static final int DOT_SIZE = 10; // size of maze solution dot
	public static final int DOT_MARGIN = 5; // space between wall and dot
	private int N;
	private Cell[] cells; // array containing all the cells in the maze
	private boolean[] path; // array representing the unique path solution

	public Maze(int n)
	{
		N = n;
		cells = new Cell[N * N]; // creates array of Cells
		
		for (int i = 0; i < N * N; i++) // initializes array with Cell objects
		{
			cells[i] = new Cell();
		}
	
		if(N > 0)
		{
			makeWalls(); // updates wall information inside each Cell object
			clearWalls(); // destoys wall until a maze is formed
		
			path = new boolean[N * N];
			createPath();

		}
	}

	public class Cell // Class representing a cell in a maze.
	{
		int[] walls; // array representing north, south, east, west walls
		int visitedBy; // for running first breath search, saves the cell that
		// visited this cell
		
		public Cell()
		{
			walls = new int[4];
			visitedBy = -1;
		}
	}
	
	final int NORTH = 0 ;
	final int SOUTH = 1 ;
	final int EAST = 2 ;
	final int WEST = 3 ;

	public void makeWalls() // fills wall information in Cells, -1 represents a
	// border wall
	{
		for (int i = 0; i < N * N; i++) // set north,south,east,west walls
		{
			cells[i].walls[NORTH] = i - N;
			cells[i].walls[SOUTH] = i + N;
			cells[i].walls[EAST] = i + 1;
			cells[i].walls[WEST] = i - 1;
		}
		
		for (int i = 0; i < N; i++)
		{
			cells[i].walls[NORTH] = -1; // set in border north cells, north wall to -1
			cells[N * N - i - 1].walls[SOUTH] = -1; // set in border south cells, south
			// wall to -1
		}
		for (int i = 0; i < N * N; i += N)
		{
			cells[N * N - i - 1].walls[EAST] = -1; // set in border east cells, east
			// wall to -1
			cells[i].walls[WEST] = -1; // set in border west cells, west wall to -1
		}
	}

	public void clearWalls() // destroys walls with a modified version of
	// Kruskal's algorithm
	{
		int NumElements = N * N;
		
		DisjSets ds = new DisjSets(NumElements); // creates a disjoint set to
		// represent cells
		for (int k = 0; k < N * N; k++)
		{
			ds.find(k); // adds each cell to a single set
		}
		
		Random generator = new Random();
		while (ds.allConnected() == false) // while not all the elements in the
		// set are connected
		{
			int cell1 = generator.nextInt(N * N); // pick a random cell
			int wall = generator.nextInt(4);
			
			int cell2 = cells[cell1].walls[wall]; // pick a second random cell
			
			if (cell2 != -1 && cell2 != N * N) // if there exists a wall between
			// these two cells
			{
				if (ds.find(cell1) != ds.find(cell2)) // if cells do not belong to
				// the same set
				{
					cells[cell1].walls[wall] = N * N; // destroy the wall between
					// these two cells. N*N will
					// represent no wall
					
					if (wall == NORTH || wall == EAST)
					{
						cells[cell2].walls[wall + 1] = N * N;
					}
					if (wall == SOUTH || wall == WEST)
					{
						cells[cell2].walls[wall - 1] = N * N;
					}
					
					ds.union(ds.find(cell1), ds.find(cell2)); // make a union of the
					// set of these two cells, through which a path has just been
					// created
				}
			}
		}
	}

	public void createPath() // finds a path in the maze
	{
		if( N != 1) //if maze is not of size 1
		{
			depthSearch(0); // executes a first breath search starting on the top
			// left cell
			
			path[0] = true; // path starts on top left cell
			path[N * N - 1] = true; // path ends on bottom right cell
			
			int current = cells[N * N - 1].visitedBy; // start on the last, bottom
			// right cell
			while (current != 0) // follows the path back to the starting cell
			{
				path[current] = true;
				current = cells[current].visitedBy;
			}
		}
		else // if maze is of size 1
		{
			path[0] = true ;
		}

		cells[0].walls[WEST] = N * N; // destroys west wall on top left cell
		cells[N * N - 1].walls[EAST] = N * N; // destroys east wall on bottom right
		// cell
	}

	public void depthSearch(int cell) // executes a first breath search to find
	// a path in the maze
	{
		Cell startCell = cells[cell]; // current cell being checked
		
		for (int i = 0; i < 4; i++) // check if there is a path north, south,
		// east, or west
		{
			int adjacent = -1;
			
			if (startCell.walls[i] == N * N) // if there is no wall in north,
			// south, east or west direction
			{
				if (i == NORTH)
				{
					adjacent = cell - N;
				}
				if (i == SOUTH)
				{
					adjacent = cell + N;
				}
				if (i == EAST)
				{
					adjacent = cell + 1;
				}
				if (i == WEST)
				{
					adjacent = cell - 1;
				}
				
				if (cells[adjacent].visitedBy == -1)
				{
					cells[adjacent].visitedBy = cell; // update information to
					// store which cell has visited this one
					depthSearch(adjacent);
				}
			}
		}
	}

	public void draw(Graphics g) // draws a maze and its solution
	{
		g.setColor(Color.BLACK);
		
		for (int i = 0; i < N; i++)
		{
			int count = i;
			for (int j = 0; j < N; j++)
			{
				if (j != 0)
				{
					count += N;
				}
				
				if (cells[count].walls[NORTH] != N * N) // if there exists a wall to the
				// north
				{
					g.drawLine((i * CELL_WIDTH + MARGIN), (j * CELL_WIDTH + MARGIN),
						((i + 1) * CELL_WIDTH + MARGIN), (j * CELL_WIDTH + MARGIN));
				}
				
				if (cells[count].walls[SOUTH] != N * N) // if there exists a wall to the
				// south
				{
					g.drawLine(i * CELL_WIDTH + MARGIN, (j + 1) * CELL_WIDTH
						+ MARGIN, (i + 1) * CELL_WIDTH + MARGIN, (j + 1) * CELL_WIDTH
						+ MARGIN);
				}
				
				if (cells[count].walls[EAST] != N * N) // if there exists a wall to the
				// east
				{
					g.drawLine((i + 1) * CELL_WIDTH + MARGIN, j * CELL_WIDTH
						+ MARGIN, (i + 1) * CELL_WIDTH + MARGIN, (j + 1) * CELL_WIDTH
						+ MARGIN);
				}
				
				if (cells[count].walls[WEST] != N * N) // if there exists a wall to the
				// west
				{
					g.drawLine(i * CELL_WIDTH + MARGIN, j * CELL_WIDTH + MARGIN, i
						* CELL_WIDTH + MARGIN, (j + 1) * CELL_WIDTH + MARGIN);
				}
			}
		}
		
		g.setColor(Color.RED); // changes color to draw the dots
		for (int i = 0; i < N; i++)
		{
			int count = i;
			for (int j = 0; j < N; j++)
			{
				if (j != 0)
				{
					count += N;
				}
				
				if (path[count] == true) // if cell is part of the path
				{
					g.fillOval(i * CELL_WIDTH + MARGIN + DOT_MARGIN, j * CELL_WIDTH
						+ MARGIN + DOT_MARGIN, DOT_SIZE, DOT_SIZE); // paint a red
					// circle in the
					// cell

				}
			}
		}
	}

	public Dimension windowSize() // returns the ideal size of the window (for
	// JScrollPanes)
	{
		return new Dimension(N * CELL_WIDTH + MARGIN * 2, N * CELL_WIDTH + MARGIN
			* 2);
	}
}
