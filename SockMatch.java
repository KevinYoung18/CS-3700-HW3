import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;


public class SockMatch 
{
	volatile static int numOfRedSocks = 0;
	volatile static int numOfGreenSocks = 0;
	volatile static int numOfBlueSocks = 0;
	volatile static int numOfOrangeSocks = 0;
	volatile static LinkedBlockingDeque<String> queue = new LinkedBlockingDeque<String>();
	static Object washBlock = new Object();
	
	public static void main(String[] args) 
	{
		
		Runnable redSockGen = () -> {
			sockGen("Red");
		};
		Runnable greenSockGen = () -> {
			sockGen("Green");
		};
		Runnable blueSockGen = () -> {
			sockGen("Blue");
		};
		Runnable orangeSockGen = () -> 
		{
			sockGen("Orange");
		};
		
		Thread redSock = new Thread(redSockGen);
		Thread greenSock = new Thread(greenSockGen);
		Thread blueSock = new Thread(blueSockGen);
		Thread orangeSock = new Thread(orangeSockGen);
		
		
		//this thread matches pairs of socks and sends them to the washer thread
		Runnable matcher = () -> 
		{
			// while any of the sock threads are still running 
			while(redSock.isAlive() || greenSock.isAlive() || blueSock.isAlive() || orangeSock.isAlive()) {
				match();
			}
			
			//finish sending out socks
			while(numOfRedSocks > 1 || numOfBlueSocks > 1 || numOfOrangeSocks > 1 || numOfGreenSocks > 1) {
				match();
			}
			
			//send flag to queue that to finish threads
			try {
				queue.put("flag");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		Thread matchThread = new Thread(matcher);
		
		// this thread destroys pairs of socks
		Runnable washer = () -> 
		{
			boolean notDone = true;
			while(notDone)
			{
				String sock = null;
				try 
				{
					sock = queue.take();
					
					if(sock.equals("flag")) //flag that thread is finished
					{
						notDone = false;
						System.out.println("All threads finished");
					}
					else if(sock.equalsIgnoreCase("Red")) 
					{
						System.out.println("Washer Thread: Destroyed " + sock + " socks");
					}
					else if(sock.equalsIgnoreCase("Green")) 
					{
						System.out.println("Washer Thread: Destroyed " + sock + " socks");
					}
					else if(sock.equalsIgnoreCase("Blue")) 
					{
						System.out.println("Washer Thread: Destroyed " + sock + " socks");
					}
					else if(sock.equalsIgnoreCase("Orange")) 
					{
						System.out.println("Washer Thread: Destroyed " + sock + " socks");
					}
					else
						System.out.println("invalid color in washer");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread wash = new Thread(washer);
				
		redSock.start();
		greenSock.start();
		blueSock.start();
		orangeSock.start();
		matchThread.start();
		wash.start();
	}
	
	//produces a random amount of socks of the passed color
	public synchronized static void sockGen(String color)
	{
		Random rand = new Random();
		int randNum = rand.nextInt(100) + 1;
		int count = 0;
		while(count < randNum)
		{
			count++;
			System.out.println(color + " Sock: produced " + count + " of " + randNum + " " + color + " Socks");
			
			if(color.equals("Red"))
				numOfRedSocks++;
			else if(color.equals("Green"))
				numOfGreenSocks++;
			else if(color.equals("Blue"))
				numOfBlueSocks++;
			else if(color.equals("Orange"))
				numOfOrangeSocks++;
			else
				System.out.println("invalid color in sock generator");
		}
	}
	
	//matches socks by color then adds them to a blocking queue
	public synchronized static void match()
	{
		if(numOfRedSocks > 1) 
		{
			numOfRedSocks -= 2;
			queue.add("Red");
			System.out.println("Matcher: Send Red Socks to Washer.");
		}
		else if(numOfGreenSocks > 1) 
		{
			numOfGreenSocks -= 2;
			queue.add("Green");
			System.out.println("Matcher: Send Green Socks to Washer.");
		}
		else if(numOfBlueSocks > 1) 
		{
			numOfBlueSocks -= 2;
			queue.add("Blue");
			System.out.println("Matcher: Send Blue Socks to Washer.");
		}
		else if(numOfOrangeSocks > 1) 
		{
			numOfOrangeSocks -= 2;
			queue.add("Orange");
			System.out.println("Matcher: Send Orange Socks to Washer.");
		}
		else
		{
			try {
				Thread.sleep(1); //force thread to sleep so not to waste CPU time
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Matcher: Total Socks: " 
				+ (numOfRedSocks + numOfGreenSocks + numOfBlueSocks + numOfOrangeSocks) 
				+ ". Pairs of socks in queue: " 
				+ queue.size());
	}

}
