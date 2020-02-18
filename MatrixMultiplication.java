import java.util.Random;

public class MatrixMultiplication 
{
	volatile static float[][] a = new float[4][4];
	volatile static float[][] b = new float[4][4];
	
	public static void main(String args[]) throws InterruptedException
	{
		int bound = 100;
		int m = 4;
		int n = 4;
		int p = 4;
		float[][] A = matrixGen(m, n, bound);
		float[][] B = matrixGen(n, p, bound);
		matmult(A, B, m, n, p);
		
		
		
		
		Runnable left = () -> 
		{
			a = matmult(A, B, m, n, p, 0, 2);
		};
		Runnable right = () -> 
		{
			b = matmult(A, B, m, n, p, 2, 4);
		};
		
		
		Thread lThread = new Thread(left);
		Thread rThread = new Thread(right);
		long time = System.nanoTime();
		lThread.start();
		rThread.start();
		lThread.join();
		rThread.join();
		float[][] c = sum(a,b, 0, 2, p);
		time = System.nanoTime() - time;
		System.out.println("2 threads: Time Elapsed(ns): " + time);
	}
	
	//performs matrix multiplication
	public static float[][] matmult(float[][] A, float[][] B, int m, int n, int p)
	{
		long time = System.nanoTime();
		float[][] C = new float[m][p];
		for(int i = 0; i < m; i++) 
		{
			
			for(int j = 0; j < p; j++)
			{	
				float result = 0;
				for(int k = 0; k < n; k++)
				{
					 result += A[i][k] * B[k][j];
				}
				C[i][j] = result;
			}
		}
		time = System.nanoTime() - time;
		System.out.println("Single ThreadTime Elapsed(ns): " + time);
		return C;
	}
	
	public static float[][] matmult(float[][] A, float[][] B, 
			int m, int n, int p,
			int low, int high)
	{
		float[][] C = new float[m][p];
		for(int i = low; i < high; i++) 
		{
			for(int j = 0; j < p; j++)
			{	
				float result = 0;
				for(int k = 0; k < n; k++)
				{
					 result += A[i][k] * B[k][j];
				}
				C[i][j] = result;
			}
		}
		
		return C;
	}

	public static float[][] sum(float[][] A, float[][] B, int low, int mid,int numOfColumns)
	{
		float[][] C = B.clone();
		for(int i = low; i < mid; i++)
		{
			for(int j = 0; j < numOfColumns; j++)
			{
				C[i][j] = A[i][j];
			}
		}
		return C;
	}
	public static float[][] matrixGen(int m, int n, int bound)
	{
		float[][] matrix = new float[m][n];
		Random rand = new Random();
		for(int i = 0; i < m; i++) 
		{
			for(int j = 0; j < n; j++)
			{	
				matrix[i][j] = rand.nextInt(bound);
			}
		}
		return matrix;
	}
	
	public static void printMatrix(float[][] matrix, int m, int n)
	{
		for(int i = 0; i < m; i++) 
		{
			for(int j = 0; j < n; j++)
			{	
				System.out.print(matrix[i][j] + "\t");
			}
			System.out.println();
		}
	}
}
