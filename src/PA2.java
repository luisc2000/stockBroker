import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import model.*;

public class PA2 {
	static List<Stock> stocksList; // has all of the stocks from json
	static ArrayList<Schedule.Task> taskList;
	static Schedule myCSV;
	static Map<String, Semaphore> sems; // map to hold all of the semaphore
	static String fileName;
	public static long startTime; // gives time in milliseconds 
	
	public PA2() 
	{
		stocksList = new ArrayList<>();
		taskList = new ArrayList<Schedule.Task>();
		myCSV = new Schedule(taskList);
		sems = new HashMap<String, Semaphore>();
		fileName = "";
	}
	
    //Read Stock Json File inputed by user using GSON
    private static void readStockFile() {
    	
    	Gson gson = new Gson();
	    Reader reader = null;
	    StockData stockData = null;
	    while(true) // keep looping until you find a file that works
	    {
	    	System.out.println("What is the name of the file containing the company information?");
	    	BufferedReader input = new BufferedReader(new InputStreamReader(System.in)); // like cin
	    	try {fileName = input.readLine();} 
	    	catch (IOException e1) {}
	    	try(BufferedReader file = new BufferedReader(new FileReader(fileName));)
	    	{
	    	} 
	    	catch (NullPointerException e) { System.out.println("file is empty");continue;}
	    	catch (FileNotFoundException e) {
	    	System.out.print("The file " + fileName +  " could not be found.\n\n");continue;}
	    	catch (IOException e) {System.out.print("e");continue;}
	    	
	    	// here the file has been found, so can now begin to populate
		    try 
		    {
		    	reader = Files.newBufferedReader(Paths.get(fileName));
		    	stockData = gson.fromJson(reader, StockData.class); // ?
		    	stocksList = new ArrayList<Stock>(Arrays.asList(stockData.getData())); //converting from array to arrayList
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    	continue;
		    }
		    catch (NullPointerException e) {
		    	System.out.println("\nFile not formatted correctly ");
		    	continue;
		    }
		    catch (JsonSyntaxException e) {
		    	System.out.println("\nFile not formatted correctly ");
		    	continue;
		    }
		    Boolean br = false;
		    if(stocksList.size() == 0)
    		{
		    	System.out.println("\nYour data was not formatted correctly");
    			continue;
    		}
		    for(int i = 0; i < stocksList.size(); i++)
		    {	
		    	try 
		    	{
		    		stocksList.get(i).ticker = stocksList.get(i).ticker.replaceAll(" ", "");
		    		if(stocksList.get(i).name.isEmpty() || stocksList.get(i).ticker.isEmpty() || stocksList.get(i).startDate.isEmpty() || stocksList.get(i).description.isEmpty() || stocksList.get(i).exchangeCode.isEmpty() || stocksList.get(i).stockBrokers <= 0)
		    		{
		    			System.out.println("\nYour data was not formatted correctly");
		    			br = true;
		    			break;
		    		}
		    		if(!validatetDate(stocksList.get(i).startDate))
		    		{
		    			System.out.print("A year number was not formatted properly. Enter a properly formatted data source: ");
		    			br = true;
		    			break;
		    		}
		    	} 
		    	catch (NullPointerException e) 
		    	{
		    		System.out.println("\nFile not formatted correctly ");
		    		br = true;
		    		break;
		    	}
		    }
			if(br) continue;
			else break;
	    }
	    
    }
	
	private static boolean validatetDate(String text)
	{
		String re = "^\\s*\\d{1,4}\\s*\\-\\s*\\d{0,2}\\s*\\-\\s*\\d{0,2}\\s*$";
		Pattern pt = Pattern.compile(re);
		Matcher mt = pt.matcher(text);
		return mt.matches();
	}
    // Read Stock Trades CSV File inputed by user
    private static void readScheduleFile() throws IOException {
    	
    	Boolean broke = false;
    	while(true)
    	{
    		broke = false;
    		System.out.println("What is the name of the file containing the schedule information?");
        	BufferedReader input = new BufferedReader(new InputStreamReader(System.in)); // like cin
        	String file = "";
        	file = input.readLine();
    		try(BufferedReader br = new BufferedReader(new FileReader(file))) 
    		{
        		String line = "";
        		while ((line = br.readLine()) != null) 
        		{
        			line = line.replaceAll("\\s", "");
        			String[] tokens = line.split(",");
        			tokens[0] = tokens[0].replaceAll("\\uFEFF", "");
        			
        			Integer time = null;
        			String ticker = null;
        			Integer tradeQuantity = null;
        			try
        			{
        				if(tokens.length != 3) 
        				{
        					System.out.println("CSV file does not have the appropriate tokens");
        					broke = true;
        					break;
        				}
        				 time = Integer.parseInt(tokens[0]);
        				 if(tokens[1] == null)
        				 {
        					 System.out.print("YES");
        				 }
        				 ticker = tokens[1];
        				 ticker.replaceAll(" ", ""); // edge case
        				 if(ticker.isEmpty() || tokens.length <= 2 || time < 0)
        				 {
        					 System.out.println("\nYour data was not formatted correctly");
        					 broke = true;
        					 break;
        				 }
        				 tradeQuantity = Integer.parseInt(tokens[2]);
        				 myCSV.addTask(time, ticker, tradeQuantity);
        			}
        			catch(NumberFormatException e)
        			{
        				System.out.println("Your data was not formatted correctly");
        				broke = true;
        				break;
        			}
        			catch(NullPointerException e)
        			{
        				broke = true;
        				System.out.println("Missing a parameter in the CSV file");
        			}
        		}
        		if(broke) continue;
        		else break;
        		
        	}
    		catch (FileNotFoundException e) 
    		{
    			System.out.print("The file " + file +  " could not be found.");
	    	}
    	}
    }

    /**
     *Set up Semaphore for Stock Brokers
     */
    //create a semaphore for each company with the amount of stockBrokers
    private static void initializeSemaphor() {
    	
    	for(int i = 0; i < stocksList.size(); i++)
    	{
    		Semaphore semaphore= new Semaphore(stocksList.get(i).stockBrokers, true); // get the num of stockbrokers
    		sems.put(stocksList.get(i).ticker, semaphore);
    	}
    }

    private static void executeTrades() throws InterruptedException {
    	ExecutorService es = Executors.newCachedThreadPool();
    	//startTime = System.currentTimeMillis();
    	if(myCSV.getTaskList().size() > 0)
    	{
    		System.out.println("\nStarting execution of program...");
    	}
    	else if(myCSV.getTaskList().size() == 0)
    	{
    		System.out.println("\nYou have no trades to complete.");
    	}
    	
    	for( int i = 0; i < myCSV.getTaskList().size(); i++)
    	{	
    		es.execute(new Trade(myCSV.getTaskList(), i, sems.get(myCSV.getTaskList().get(i).getTicker())));
    		if(i == myCSV.getTaskList().size() - 1)  break;
    	}
    	es.shutdown();
    	while (!es.isTerminated()) {}
    	if(myCSV.getTaskList().size() > 0)
    	{
    		System.out.println("All trades completed!");
    	}
    }

    public static void main(String[] args) throws InterruptedException {
    	//System.out.println(System.getProperty("java.runtime.version")); // running in java version 14
    	PA2 a = new PA2(); // invokes the constructor
    	PA2.readStockFile();
    	try {readScheduleFile();} 
    	catch (IOException e) {e.printStackTrace();}
    	initializeSemaphor();
    	PA2.executeTrades();
    }
}