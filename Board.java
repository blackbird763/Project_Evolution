package hardcode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


/*
 * 				HAUPTCODE
 * 			-------------------------
 * 
 * 			Die komplette Logik steckt hier. Die Board.class ist dabei eine erweiterung der SWT.Canvas.class und bietet somit zusatzfunktionen
 */

public class Board extends Canvas {

	private final int FIRSTHUNTERS = 10;										// Anzahl der ersten Hunter
	private int WIDTH = 200;													//Pixelbreite des Canvas
	 private int HEIGHT = 200;													//Pixelhöhe des Canvas
	private final int STARTAMOUNT =2000;										// Nahrungsmenge beim Programmstart
    private final int DOT_SIZE = 2;													// 2x2 sprites
    private final int ALL_DOTS = (WIDTH/DOT_SIZE)*(HEIGHT/DOT_SIZE);				//Anzahl der maximalen Dots/Bildpunkte. Momentan ist ein Feld 2x2 Pixel groß
    private  int DELAY = 500;													// Laufgeschwindigkeit
    private  int DELAYPICTURE = 10000;											// Updategeschwindigkeit des Bildes (das Canvas wird also nur nach X ms neu gezeichnet/dargestellt)
    private int activeThreadsCount = 0;											// Anzahl der Momentan lebenden Hunter / aktiven Threads
    private int activeFoodCount =0;  											//Anzahl der Momentanen Nahrung auf dem Feld					
    private int RESPAWN = 10;													// wie viel Nahrung pro Programmzyklus nachwächst
    
    private Display display;
    private Shell shell;
    private Runnable runnable1;													//Hauptthread
    private Runnable runnableREDRAWING;											//ZeichnenThread fürs Canvas
    private Runnable runnableDATA;												//Thread für das Schreiben der Datei.
    
    public final byte Raster[][] = new byte[HEIGHT/DOT_SIZE][WIDTH/DOT_SIZE];	
  /*
   * 	Das Raster ist das "Spielbrett" der Anwendung. Vorzustellen wie ein einfaches Schachbrett oder das Feld von Minesweper. Jedes Feld kann dabei 3 Werte annehmen
   * 	wenn 0 auf dem Feld: das Feld ist leer
   *  	..	 1 auf dem Feld: auf dem Feld befindet sich Nahrung
   *  	..   2 auf dem Feld: auf dem Feld BEFAND sich ein Hunter
   *  
   *  		Da die Hunter eigene Threads sind und ihre Bewegungen unabhängig berechnen kann das Canvas lediglich die aktuelle Position der Hunter abfragen und diese dann auf dem Feld Eintragen
   *  		Die Hunter selbst bewegen sich dabei nicht direkt auf dem Raster sondern stehen in stetiger communikativer Verbindung zur Canvas
   */
    
    private  String newline = System.getProperty("line.separator");				// <- holt sich das Zeichen für den Zeilenumbruch bei Strings ("/n") vom Betriebssystem, da das nicht bei jedem gleich ist.
    
    public Hunter[] hunters = new Hunter[1024]; 								// Array von 1024 Hunter objekten
   public Thread[] th = new Thread[1024];										//Array von 1024 einzelnen Threads
   public Textwork Txtfile = new Textwork();									// Textwork objekt für das Abspeichern von Informationen in eine txt Datei (siehe Textwork.java)
    public Image foodimg;
    public Image hunterimg;
              
public Board(Composite composite) {
		super(composite,SWT.NULL);
		shell = composite.getShell();
		
		
	
		Txtfile.writeHeaderData(STARTAMOUNT, RESPAWN, ALL_DOTS);					//löscht den Inhalt der .txt und schreibt den Headder mit Programminfos rein
		
		System.out.println("height: " + HEIGHT + "width: " + WIDTH );				
		initBoard();																//Boardfunktionen des Programms werden initialisiert
	
	}



private void initBoard()
{
	display = shell.getDisplay();

	foodimg = new Image(display,getClass().getResourceAsStream("food.jpg"));				//image für die Darstellung von Nahrung	
	hunterimg = new Image(display,getClass().getResourceAsStream("hunter.jpg"));			//images für die Hunter
	
    Color col = new Color(shell.getDisplay(), 0, 0, 0);										//setzt Canvashintergrund schwarz -> Leere Felder sind schwarz

    setBackground(col);																		//setzt Canvashintergrund schwarz -> Leere Felder sind schwarz
    col.dispose();																			
	
	createFirstField();																		//Funktion, um die erste Nahrungsverteilung zufällig zu erstellen (siehe Funktion weiter unten)
	
	
	
	addListener(SWT.Paint, event -> drawAll(event));										//immer, wenn jemand das Canvas neu bemalen will, wird die fkt. drawAll aufgerufen
																							//drawAll malt das momentane Raster auf das Canvas um den Fortschritt visuell darzustellen (siehe Funktion weiter unten)
//	addListener(SWT.KeyDown, event -> onKeyDown(event));		
	 
	
	addListener(SWT.Dispose, event -> {														//wenn die Shell geschlossen wird/das Canvas Disposed wird, dann wird das folgende event ausgeführt	  
        
		for (int i = 1;i<=activeThreadsCount; i++)
        {       	
		th[i].interrupt();																	//schließen aller Momentan offenen Threads
        };
		
       });
	
	
	
	   
    runnable1 = new Runnable() {							
  /*
   * HAUPTTHREAD 
   * -----------
   * übernimmt die Komplette Interaktion zwischen den HunterThreads und der Nahrung 
   * und verwaltet das komplette Geschehen auf dem Raster 
   *  												(non-Javadoc)
   * @see java.lang.Runnable#run()
   */
        @Override
        public void run() {	
        	
        	ResetRaster();																	//löscht positionen aller hunter vom Feld. (siehe Funktion unten) 
        	addFood();																		//Spawnt neue Nahrung (funktion unten)
        	   	
        	makeBabys();																	//erzeugt neue Hunter wenn die bedingungen Stimmen (Funktion unten)
        	killingHunters();																//löscht alle toten Hunter (funktion unten)
        	feeding();																		//füttert alle Hunter, wenn sie auf Nahrung stehen (funktion unten)
        
        	setAllHunters();																//setzt die Positionen der Hunter auf das Spielbrett (funktion unten)
        	
        	
            display.timerExec(DELAY, this);							
						
        } 
    };
display.timerExec(DELAY, runnable1);		//erlaubt es den Thread, die Darstellung zu updaten
   
    
    
    
    runnableREDRAWING = new Runnable() {	
   
    	/*
    	 * 
    	 * ZEICHENTHREAD
    	 * --------------
    	 * 
    	 * Ist alleine Zuständig für die Visuelle Darstellung im SWT, da er die redraw Funktion aufruft, welche den SWT.PAINT Listener von weiter oben aktiviert.
    	 * Zudem ist er im moment noch zuständig für das Schreiben der Textdatei zur auswertung in Excel (siehe Textwork.class)
    	 * 
    	 * (non-Javadoc)
    	 * @see java.lang.Runnable#run()
    	 * 
    	 */
    	
@Override
public void run() {	

display.timerExec(DELAYPICTURE, this);										
redraw();																			//schickt Redraw befehl, führt dadurch den SWT.Paint Listener aus

countFoodUnits();																	// zählt die Menge der Nahrung. Dient allein der Auswertung und wird in die Txt geschrieben

Txtfile.writeData(activeThreadsCount, activeFoodCount);								// schreibt neue Zeile mit Daten in die Txt (siehe classe)
	

System.out.println(activeThreadsCount + "," + activeFoodCount);

} 

};
display.timerExec(DELAYPICTURE, runnableREDRAWING);									//ermöglicht dem Thread, das Display/die Darstellung zu updaten
  
    
    
}

private void setAllHunters()		// Holt sich die Positionen aller Hunter und setzt sie auf Das Raster für die Grafische Darstellung
{
	for (int i = 1;i<=activeThreadsCount; i++)
		{		if(th[i].isInterrupted() == false)	{setMonsterUnit(hunters[i].getx(),hunters[i].gety());}		 }  //checkt zur sicherheit noch einmal, ob der Thread auch wirklich läuft, bevor er es setzt
}

private void killingHunters()
/*
 * 	Diese Funktion fragt alle Hunter nacheinander ab, ob diese keine Nahrung mehr haben (siehe Hunter.class für mehr info), und falls ja, 
 * dann wird der Momentane Hunter "gelöscht" indem alle Nachfolgenden Hunter/Threads im Array einen Platz runter im Array rutschen und den toten Hunter überschreiben
 * dadurch wird der vorher höchste Hunter/Thread frei und muss gestopt werden.   bsp.:  Momentan 10 active Hunter. Hunter 3 stirbt. Hunter 4 wird zu Hunter 3, Hunter 5 wird zu Hunter 4......
 * Hunter 10 wird zu Hunter 9. Dadurch sind jetzt Hunter 10 und Hunter 9 identisch und Hunter 10 kann Terminiert werden.
 * 
 * danach wird activeThreadsCount um 1 minimiert, da ja ein Thread weggefallen ist.
 * Das Funktioniert, da beim erstellen eines Neuen Jäger jedes mal das Komplette Objekt und der dazugehörige Thread neu initialisiert wird.
 */

{
    for (int i = 1;i<=activeThreadsCount; i++)
    {
    	
   
    	if (hunters[i].getfood() <= 0)
    	{
    				
    		for(int j=i;j<activeThreadsCount;j++)   		
    		{
    			hunters[j]=hunters[j+1];
    			//th[j]=th[j+1];
    		};  		   		
    		th[activeThreadsCount].interrupt();
    		activeThreadsCount--;
   		
    	}

    	
    };
}

private void setMonsterUnit(int x, int y)			//setzt Monsterpositionen auf das feld. 
{
	
	Raster[y][x]=2;
	
	
}

private void countFoodUnits()					//geht das Ganze Raster von oben nach unten ab und zählt dabei die Anzahl der Nahrung / welche Positionen im Raster eine 1 haben
{
	activeFoodCount = 0;
	for(int a=0;a<HEIGHT/DOT_SIZE;a++)
	{
		for(int b=0;b<HEIGHT/DOT_SIZE;b++)
    	{
    		if(Raster[a][b] == 1)
    		{
    			activeFoodCount++;
    		}
    	}       		
	} 
	
}

private void feeding()

/*  geht alle Hunter durch. Fragt dabei die momentane interne Position eines Hunters ab. Wenn auf dieser Position Nahrung (=1) ist, 
 * dann wird diese gelöscht (=0) und der Hunter wird gefüttert (feedme funktion im Hunter) bevor der nächste Hunter gecheckt wird.
 * 
 */

{
	
	 for (int i = 1;i<=activeThreadsCount; i++)
     {
	if(getInhaltUnit(hunters[i].getx(),hunters[i].gety()) == 1 && th[i].isInterrupted() == false)		//checkt ob der thread noch läuft (sicherheit)
		{
		Raster[hunters[i].getx()][hunters[i].gety()] = 0;
		hunters[i].feedme();															//	Hunter bekommt nahrung(siehe Hunter)
		}		
     }	

}

private void addFood()			//Spawnd neue Nahrung
{

	for(int i=0; i<(RESPAWN) ; i++)							//RESPAWN = MENGE
	{	
	
		if(setfoodUnit(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE),ThreadLocalRandom.current().nextInt(0, WIDTH/DOT_SIZE)) == 1	)	
		{																			//siehe setfoodUnit() für mehr info
			i--; 																	//nimmt ein Random RasterFeld, setzt dort Nahrung hin, falls dort vorher schon Nahrung war wird dies nicht als neue Nahrung mitgezählt
		}
	};
}

private void makeBabys()

/*
 *  Diese Funktion checkt den internen Wert jedes Hunters um zu sehen, ob dieser bereit ist sich zu Paaren (howdeepisyourlove).
 *  ist der Hunter bereit, sich zu Paaren, so wird dessen "liebe" (siehe Hunter.class) als erstes zurück gesetzt bevor ein neuer Hunter erstellt wird
 *  Dafür wird einfach der activeThreadCount um eins erhöht und dann an dieser Stelle ein neues Hunterobjekt und der dazugehörige Thread initialisiert und gestartet.
 *  mit dem Start des Threads ist der Hunter damit am leben und bekommt erste Standardwerte bei der Geburt zugewiesen bevor er die Koordinaten siner Mutter als 
 *  startpunkt erhält. 
 *  HINWEIS: es gibt keine begrenzung dafür, wie viele Hunter auf der gleichen Position stehen können, da diese Komplett unabhängig voneinander sind. es Kann aber nattürlich
 *  immer nur 1 Hunter auf dem Feld dargestellt werden. stört aber nicht
 */

{
	 for (int i = 1;i<=activeThreadsCount; i++)
     {
		if(hunters[i].howdeepisyourlove()==true)
		{
			hunters[i].setlove(0);														//setzt den liebeswert zurück
			activeThreadsCount++;
			hunters[activeThreadsCount] = new Hunter();							
			 th[activeThreadsCount] = new Thread(hunters[activeThreadsCount]);
			th[activeThreadsCount].start();												
			hunters[activeThreadsCount].resetParameters();
			hunters[activeThreadsCount].setx(hunters[i].getx());
			hunters[activeThreadsCount].sety(hunters[i].gety());
			
			
		}
		 
     }
	
}

private void createFirstField()		

/* 
 * erzeugt die Startnahrung (wie addFood() ) und die ersten Jäger
 */

{
	for(int i=0; i<STARTAMOUNT ; i++)
	{	
	
		if(setfoodUnit(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE),ThreadLocalRandom.current().nextInt(0, WIDTH/DOT_SIZE)) == 1	)
		{
			i--;
		}
	};
	
	
	
	
	
	for (int i = 1; i <= FIRSTHUNTERS; i++)												//erzeugt die erste Anzahl an Jägern
	{
		hunters[i] = new Hunter();							
		th[i] = new Thread(hunters[i]);
		hunters[i].resetParameters();
		hunters[i].setx(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE));
		hunters[i].sety(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE));
		th[i].start();																	//ersten Hunter initalisieren als eigenen Thread und starten
		activeThreadsCount++;
	}
	
	
	
}

private void ResetRaster() 			 //geht das Raster durch und löscht die Positionen der Hunter vom Feld, damit diese dann wieder neu gesetzt werden können
{
	
	for(int a=0;a<HEIGHT/DOT_SIZE;a++)
	{
		for(int b=0;b<HEIGHT/DOT_SIZE;b++)
    	{
    		if(Raster[a][b] == 2)
    		{
    			Raster[a][b] = 0;
    		}
    	}       		
	}
}


private byte setfoodUnit(int x,int y)		//setzt Nahrung auf ein leeres Feld und gibt je nachdem true oder false zurück, ob das Setzen erfolgreich war oder nicht
{
	if (Raster[y][x]==0)
	{
	Raster[y][x]=1;
	return 0;
	}
	else {return 1;}
}

private byte getInhaltUnit(int x, int y)
{
	byte wert = Raster[x][y];
	return wert;
}


private void drawAll(Event e)

/*
 * DrawAll Funktion.  ist zuständig für das Darstellung und das Zeichnen des Canvas. im Prinzip nimmt die Funktion einfach das Komplette Raster / das Spielbrett, 
 * welches ja die Werte 0 1 2 beinhaltet, und zeichnet für jede 1 das Bild für die Nahrung und für jede 2 das Bild für die Hunter an die jeweilige Stelle.
 * Das Programm läuft dadurch theoretisch auch komplett ohne die Visuelle Darstellung.
 */
{
	GC gc = e.gc;
    Color col = new Color(shell.getDisplay(), 0, 0, 0);
    gc.setBackground(col);
    col.dispose();

    gc.setAntialias(SWT.ON);
   
   for(int a=0;a<(HEIGHT/DOT_SIZE);a++)
   {
    	for(int i=0;i<(WIDTH/DOT_SIZE);i++)
    	{
    		if(getInhaltUnit(i,a)==1)
    		{
    			gc.drawImage(foodimg, i*2, a*2);
    		} 
    		
    		if(getInhaltUnit(i,a)==2)
    		 gc.drawImage(hunterimg, i*2, a*2);
    	
    	}	
    	
    	
    	
    	
    	
    }
 //   gc.drawImage(foodimg, x, y);
    
    
	
}


}
