import java.io.*;
import java.util.Random;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset; 
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.ChartUtilities;

public class Network {

	static int N=20;
	static int i, j, k;
	static int t =0;
	static int d =0;
	static int a[][] = new int[20][20];
	static int b[][] = new int[20][20];
	static int path[][] = new int [400][400];
	static int link_capacity[][] = new int [20][20];
	static int[] generate_bij = {2,0,2,1,3,6,3,5,9,8,2,0,2,1,3,6,3,5,9,8};
	static double total_cost[] =  new double[12];
	static double density_set[] = new double[12];
	
	
	//Function to generate the demand values
	public static int demand_generate(int i, int j) {
		int d = 0;
		if (generate_bij[i] >= generate_bij[j]) {
			d = generate_bij[i]-generate_bij[j];
		}
		else {
			d = generate_bij[j]- generate_bij[i];
		}
		return d;
	}
	
	//Function to generate random jk values
	public static void rand_generator(int i) {
		int p =0;
		int rand_index;
		Random r = new Random();
		for (p = 1; p <= k; p++) {
			rand_index = r.nextInt(N);
			while ((rand_index == i) || (a[i][rand_index] == 1)) {
				rand_index = r.nextInt(N);
			}
			a[i][rand_index] = 1;
		}
	}
	

	//Function to get the total cost of the network
	public static int cost_fetch() {
		int cost = 0;
		int m, n;
		for (m = 0; m < N; m++) {
			for (n = 0; n < N; n++) {
				if (m != n) {
					cost += a[m][n]*b[m][n];
				}
			}
		}
		return cost;
	}
	
	//main application function
	public static void main_app() {
		int p = 0;
		double density;

/*All pair shortest path calculation based on Floyd Warshall algorithm.
1) Add all vertices one by one to the set of intermediate vertices. Before start of a iteration, we have shortest distances between all pairs of vertices such that the shortest distances consider only the vertices in set {0, 1, 2, .. p-1} as intermediate vertices.
2)After the end of a iteration, vertex no. p is added to the set of intermediate vertices and the set becomes {0, 1, 2, .. p} 
3)Reference: https://www.geeksforgeeks.org/dynamic-programming-set-16-floyd-warshall-algorithm/
		 */
		for (p = 0; p < N; p++) {
			//source vertices
			for (i = 0; i < N; i++) {
/*loop through all destination vertices for each source*/
				for (j = 0; j < N; j++) {
					
					/*If vertex p is on the shortest path from i to
					 j then update a[i][j]*/
					if (a[i][p] + a[p][j] < a[i][j]) {
						a[i][j] = a[i][p] + a[p][j];
						link_capacity[i][j] = b[i][p] + b[p][j];
						path[i][j] = p;
					}
				}
			}
		}

		/* Test by displaying the link capacities
		for (i = 0; i < N; i++) {
			for (j = 0; j < N; j++) {
				System.out.print(link_capacity[i][j] + "  ");
			}
			System.out.println();
		}*/		

		/* Fetch the total cost and put it in the total_cost[] array
		 * so that it can be used for generating total cost vs k graph
		 */
		total_cost[t++] = cost_fetch();
		
		//Display the total cost.
		System.out.println("Total network cost - " + cost_fetch());
		
		//Finding the number of directed edges in the final graph
		int directed_edges = 0;
		for (i = 0; i < N; i++) {
			for (j = 0; j < N; j++) {
				if (path[i][j] == 0) {
					directed_edges++;
				}
			}
		}
		
		//density calculation and density display
		density = (double) directed_edges/(N*(N-1));
		density_set[d++] = density;
		System.out.println("Density - " + density);
		
	}
	
	public static void main(String[] args) throws Exception{
		// USE jgrapht for java graph generation  or networkx for python.
		/* The main idea of the project is to implement the basic network
		 * design model presented in “An Application to Network Design”. 
		 * The software will receive the number of nodes, traffic demand 
		 * between pair of nodes and the unit cost values for the potential
		 * links as input and it will generate a network topology, with 
		 * capacities assigned to the links using the shortest path based
		 * fast solution method. K test cases will be generated.
		 */
		for (k = 3;k <= 14; k++) {
			System.out.println("k = " + k + "\n==========\n");
			//Initialization of a[i][j] and b[i][j] values
			for (i = 0; i < N; i++) {
				for (j = 0; j < N; j++) {
					if (i != j) {
						a[i][j] = 100;
					}
					else {
						a[i][j] = 0;
					}
					b[i][j] = 0;
					path[i][j] = 0;
					link_capacity[i][j] = 0;
				}
			}
			
			//generate b[i][j] values from student id
			for (i = 0; i < N; i++) {
				for (j = 0; j < N; j++) {
					b[i][j] = demand_generate(i,j);
				}
			}
			
			//generate a[i][j] values
			for (i = 0; i < N; i++) {
				rand_generator(i);
			}
			
			/*Main method to find shortest path, total cost and density                   calculation*/
			main_app();
			
			System.out.println("\n================================================================================\n");
		}
		
		//Generate the graph of total cost vs k and store in cost_chart.jpeg file
		final String x_axis = "k";
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		for (i = 0; i < 12; i++) {
			String str1 = Integer.toString(i+3);
			dataset.addValue(total_cost[i],str1, x_axis);
		}
		JFreeChart barChart = ChartFactory.createBarChart(
		         "Total cost vs k", 
		         "k", "total cost", 
		         dataset,PlotOrientation.VERTICAL, 
		         true, true, false);
		         
		int width = 640;    /* Width of the image */
		int height = 480;   /* Height of the image */ 
		File BarChart = new File( "cost_chart.jpeg" ); 
		ChartUtilities.saveChartAsJPEG( BarChart,barChart,width,height );
		 
		//Generate the graph of density vs k and store it in Density_chart.jpeg file
		final DefaultCategoryDataset densityset = new DefaultCategoryDataset( );
		for (i = 0; i < 12; i++) {
			String str1 = Integer.toString(i+3);
			densityset.addValue(density_set[i],str1, x_axis);
		}
		JFreeChart barChart2 = ChartFactory.createBarChart(
		    "density vs k", 
		    "k", "density", 
		    densityset,PlotOrientation.VERTICAL, 
		    true, true, false);
			          
		File BarChart2 = new File( "Density_chart.jpeg" ); 
		ChartUtilities.saveChartAsJPEG( BarChart2 , barChart2 , width , height );
		      
	}
}
