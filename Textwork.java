package hardcode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Textwork {

	private  String newline = System.getProperty("line.separator");
	private int count = 0;
	
public void writeHeaderData(int STARTAMOUNT, int RESPAWN, int ALL_DOTS)
{
	try {
		Writer fileWriter = new FileWriter("testdaten.txt");
		fileWriter.write("TESTVERSUCH START" + newline + "DOTS: " + ALL_DOTS + newline + "STARTFOOD: " + STARTAMOUNT + newline + "FOODRESPAWN foodcount/4 : " + RESPAWN + newline +"PARAMETER: food 200 maxfood 300 movement -2 love >5 >150" + newline );
		fileWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

public void writeData(int activeThreadsCount , int activeFoodCount )
{
	try {
		
		Writer fileWriter = new FileWriter("testdaten.txt",true);
		fileWriter.write( count  + "," + activeThreadsCount + "," + activeFoodCount  + newline );
		count++;
		fileWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
