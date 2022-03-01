package model;
public class Stock {
    /**
	 * Here: all the needed class members and their getters and setters
	 */
	
	public String name;
	public String ticker;
	public String startDate;
	public int stockBrokers;
	public String description;
	public String exchangeCode;
	
    public Stock() {
    	
    }

    @Override
	public String toString() {
		String result = new String("\nStock information: \n- Stock Name: " + name + "\n- Ticker: " + ticker);
		result += "\n- Start Date: " + startDate + "\n";
		result += "- StockBroker: " + stockBrokers + "\n- description: " + description + "\n- exchangeCode: " + exchangeCode + "\n";
		return result;
	}
}

