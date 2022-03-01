import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Trade extends Thread {
	ArrayList<Schedule.Task> myTask;
	private Semaphore semaphore;
	int id; // this tells me what stock I am dealing with 
	public Trade(ArrayList<Schedule.Task> a, int id, Semaphore semaphor) { // need to know the info of stock that will have this thread 
		myTask = a;
		this.id = id;
		this.semaphore = semaphor;
    }

	//Trading function using locks
	public void run() { 
		try {
			
			Thread.sleep(myTask.get(id).getTime() * 1000 );
			semaphore.acquire();
			if(myTask.get(id).getTradeQuantity() > 0) // trade
			{
				//Utility.millisecondsToTimestamp(long);
				System.out.println("[" + Utility.getZeroTimestamp() + "]" + " Starting purchase of " + myTask.get(id).getTradeQuantity() + " stocks of " + myTask.get(id).getTicker());
				Thread.sleep(1000);
				System.out.println("[" + Utility.getZeroTimestamp() + "]" + " Finished purchase of " + myTask.get(id).getTradeQuantity() + " stocks of " + myTask.get(id).getTicker());
			}
			else if(myTask.get(id).getTradeQuantity() < 0)
			{
				System.out.println("[" + Utility.getZeroTimestamp()+ "]" + " Starting sale of " + -1 * myTask.get(id).getTradeQuantity() + " stocks of " + myTask.get(id).getTicker());
				Thread.sleep(1000);
				System.out.println("[" + Utility.getZeroTimestamp() + "]" + " Finished sale of " +  -1 * myTask.get(id).getTradeQuantity() + " stocks of " + myTask.get(id).getTicker());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally
		{
			semaphore.release();
		}
	}
}
