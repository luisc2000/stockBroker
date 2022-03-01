import java.util.ArrayList;

public class Schedule {

    /** 
     * Stock Trades Schedule 
     * Keep the track of tasks
    */
	// task is row, schedule is whole file
	
	private ArrayList<Task> taskList;
	
    public Schedule(ArrayList<Schedule.Task> taskList) {
		this.taskList = taskList;
	}

	public ArrayList<Task> getTaskList() { // getter that returns the arraylist in the csv file
		return taskList;
	}

	// add method to 
	public void addTask(int time, String ticker, int tradeQuantity)
	{
		Task task = new Task(time, ticker, tradeQuantity);
		taskList.add(task);
	}

	/**
     * Inner class to store task object
     */

    public static class Task {
    	private int time;
    	private String ticker;
    	private int tradeQuantity;
    	
    	@Override
    	public String toString() {
    		String result = new String(time + "," + ticker + "," + tradeQuantity + "\n");
    		return result;
    	}
    	
		public Task(int time, String ticker, int tradeQuantity) {
			this.time = time;
			this.ticker = ticker;
			this.tradeQuantity = tradeQuantity;
		}

		public int getTime() {
			return time;
		}

		public String getTicker() {
			return ticker;
		}

		public int getTradeQuantity() {
			return tradeQuantity;
		}
    }
    
}
