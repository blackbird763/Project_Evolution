package hardcode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Textwork {
	
	/*
	 * 	Classe zum Schreiben einer Txt Datei im selben Verzeichniss des Programmes.
	 *  Die Txt dient mir dabei zur einfachen Auswertung von Daten der Simulation über eine Excel Tabel
	 * 
	 * 
	 */

	private  String newline = System.getProperty("line.separator");			// holt sich die Zeichen für /n vom Betriebssystem, da /n nicht überall erkannt wird
	private int count = 0;													// integer um die Zeilen im der Txt zu markieren
	
public void writeHeaderData(int STARTAMOUNT, int RESPAWN, int ALL_DOTS)	
/*
 *  Erzeugt eine neue Datei/überschreibt die alte Datei und Schreibt einen Headertext mit Parametern/Einstellungen des Programmes hinen.
 *  Die Parameter müssen dabei an die Funktion übergeben werden. 
 */
{
	try {
		Writer fileWriter = new FileWriter("testdaten.txt"); //<- Name der Datei
		fileWriter.write("TESTVERSUCH START" + newline + "DOTS: " + ALL_DOTS + newline + "STARTFOOD: " + STARTAMOUNT + newline + "FOODRESPAWN : " + RESPAWN + newline +"PARAMETER: food 200 maxfood 300 movement -5 love >5 >150" + newline );
		fileWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
}

public void writeData(int activeThreadsCount , int activeFoodCount )
/*
 *  Funktion zum schreiben einer Neuen Zeile in die .txt Datei
 *  Bekommt dabei in der Board.class die Momentane Anzahl an Nahrung und Huntern übergeben und schreibt 
 *  Diese in eine Zeile der Txt.
 *  Format:   [Zeilennummer],[Hunteranzahl],[Nahrungsanzahl]
 */

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
