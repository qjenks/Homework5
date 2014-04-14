package movingaverage;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MovingAverage {
	// this class will prompt the user to input temperatures (as doubles)
	// based on the temperature input history, it will calculate an expected next temperature
	// expected temperature based on the formula
	
	// T(n+1) = a*t(n) + (1-a)*T(n) 
	// where T(n) represents the previous average
	// a is the averaging coefficient (how heavily to weight newer data over older data)
	// and t is the actual temperature that was entered
	
	public static void main(String[] args)
	{
		MovingAverage mv = new MovingAverage();
	}
	
	public MovingAverage()
	{
		Scanner scan = new Scanner(System.in);
		
		double t = 0.0;
		double tao = 0.0;
		boolean done = false;
		String input = "";
		int count = 2; //start data at row 2, because row 1 is the headers
		Map<String, Object[]> data = new TreeMap<>();
		data.put("1", new Object[] {"TIME", "PREDICTED TEMP", "ACTUAL TEMP"});
		
		do{
			System.out.println("Enter the actual temperature, or 'exit' to stop: ");
			input = scan.next();
			if(input.equalsIgnoreCase("exit")){
				done = true;
			}
			else{
				t = Double.parseDouble(input);
			
				tao = calculateNextTemp(tao, t);
				System.out.println("Expected next temperature is: " +tao);
				data.put(String.valueOf(count), new Object[] {(count-2), tao, t}); 
				count++;
			}
		} while(!done);
		
		System.out.println("Exporting to excel file...");
		exportToExcel(data);
	}
	
	private double calculateNextTemp(double tao, double temp)
	{
		double alpha = .75;
		
		return alpha*temp + (1-alpha) * tao;
	}
	
	private void exportToExcel(Map<String, Object[]> data)
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Temperature Data");
		Set<String> keySet = data.keySet();
		int rownum = 0;
		
		for (String key : keySet)
		{
			Row row = sheet.createRow(rownum++);
			Object[] objArr = data.get(key);
			int cellnum = 0;
			for(Object obj : objArr)
			{
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof String)
				{
					cell.setCellValue((String) obj);
				}
				else if(obj instanceof Integer)
				{
					cell.setCellValue((Integer) obj);
				}
			}
		}
		
		try
		{
			FileOutputStream out = new FileOutputStream(new File("TemperatureData.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Successfully wrote temperature data to disk.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
