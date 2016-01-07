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



public class Board extends Canvas {

	private final int FIRSTHUNTERS = 10;
	private int WIDTH = 200;				//Pixelbreite des Canvas
	 private int HEIGHT = 200;	
	private final int STARTAMOUNT =2000;
    private final int DOT_SIZE = 2;				// 2x2 sprites
    private final int ALL_DOTS = (WIDTH/DOT_SIZE)*(HEIGHT/DOT_SIZE);			//alle maximalen Dots
    private  int DELAY = 500;				// Laufgeschwindigkeit
    private  int DELAYPICTURE = 10000;				// Laufgeschwindigkeit
    private int activeThreadsCount = 0;
    private int activeFoodCount =0;
    private int RESPAWN = 10;
    private int foodcount = STARTAMOUNT;
    
    private Display display;
    private Shell shell;
    private Runnable runnable1;					
    private Runnable runnableREDRAWING;
    private Runnable runnableDATA;
    public final byte Raster[][] = new byte[HEIGHT/DOT_SIZE][WIDTH/DOT_SIZE];	
    //DAS komplette Spielbrett. Wie ein großes Schachbrett. der Inhalt gibt an, wer sich auf dem Feld befindet: 0=leer,1=nahrung,2=jäger
    
    private  String newline = System.getProperty("line.separator");
    
    public Hunter[] hunters = new Hunter[1024]; 
   public Thread[] th = new Thread[1024];
   public Textwork Txtfile = new Textwork();
    public Image foodimg;
    public Image hunterimg;
              
public Board(Composite composite) {
		super(composite,SWT.NULL);
		shell = composite.getShell();
		
		
	
		Txtfile.writeHeaderData(STARTAMOUNT, RESPAWN, ALL_DOTS);
		
		System.out.println("height: " + HEIGHT + "width: " + WIDTH );
		initBoard();								//Boardfunktionen des Programms werden initialisiert
	
	}



private void initBoard()
{
	display = shell.getDisplay();

	foodimg = new Image(display,getClass().getResourceAsStream("food.jpg"));				
	hunterimg = new Image(display,getClass().getResourceAsStream("hunter.jpg"));			//images für die Teilnehmer
	
    Color col = new Color(shell.getDisplay(), 0, 0, 0);								

    setBackground(col);
    col.dispose();														//Hintergrund auf schwarz setzen
	
	createFirstField();											//Funktion, um die erste Nahrungsverteilung zufällig zu erstellen
	
	
	
	addListener(SWT.Paint, event -> drawAll(event));			//immer, wenn jemand das Canvas neu bemalen will, wird die fkt. drawAll aufgerufen
																//drawAll malt das momentane Raster auf das Canvas um den Fortschritt visuell darzustellen
	//	addListener(SWT.KeyDown, event -> onKeyDown(event));
	 
	
	addListener(SWT.Dispose, event -> {	//wenn die Shell geschlossen wird dann wird das folgende event ausgeführt	  
        for (int i = 1;i<=activeThreadsCount; i++)
        {       	
		th[i].interrupt();	
        };
		
       });
	
	
	
	   
    runnable1 = new Runnable() {							//HAUPTTHREAD, kümmert sich um die komplette interaktion zwischen den HunterThreads und dem Spielfeld
    														//	also Nahrungsvergabe, Fortpflanzung etc.
        @Override
        public void run() {	
        	
        	ResetRaster();		//löscht positionen aller hunter vom Feld. Da diese ihre Positionen intern speichern -> löscht die Vorherige Position aller Hunter
        	addFood();
        	
        	
        	
        	
        	makeBabys();
        	killingHunters();
        	feeding();
        
        	setAllHunters();
        	
        	
            display.timerExec(DELAY, this);			//stell sicher, das der Thread läuft, solange wie das Programm geöffnet ist
						
        } 
    };
display.timerExec(DELAY, runnable1);		
   
    
    
    
    runnableREDRAWING = new Runnable() {							
@Override
public void run() {	

display.timerExec(DELAYPICTURE, this);			//stell sicher, das der Thread läuft, solange wie das Programm geöffnet ist
redraw();//schickt Redraw befehl, führt dadurch den SWT.Paint Listener aus (~68)

countFoodUnits();

Txtfile.writeData(activeThreadsCount, activeFoodCount);




System.out.println(activeThreadsCount + "," + activeFoodCount);
} 
};
display.timerExec(DELAYPICTURE, runnableREDRAWING);		
  
    
    
}

private void setAllHunters()
{
	for (int i = 1;i<=activeThreadsCount; i++)
		{		if(th[i].isInterrupted() == false)	{setMonsterUnit(hunters[i].getx(),hunters[i].gety());}		 } 		//Die aktuelle Position der Hunter abfragen und auf das Spielfeld setzen für die Visuelle Darstellung
}

private void killingHunters()
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

private void setMonsterUnit(int x, int y)			//setzt Monsterpositionen auf das feld
{
	
	Raster[y][x]=2;
	
	
}

private void countFoodUnits()
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
	foodcount = activeFoodCount;
}

private void feeding()
{
	
	 for (int i = 1;i<=activeThreadsCount; i++)
     {
	if(getInhaltUnit(hunters[i].getx(),hunters[i].gety()) == 1 && th[i].isInterrupted() == false)		
		{
		Raster[hunters[i].getx()][hunters[i].gety()] = 0;
		hunters[i].feedme();
		}			// wenn ein Hunter auf einem FoodFeld steht bekommt er Nahrung dazu
     }	

}

private void addFood()							//Spawnd neue Nahrung
{

	for(int i=0; i<(foodcount/4) ; i++)							//i = MENGE
	{	
	
		if(setfoodUnit(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE),ThreadLocalRandom.current().nextInt(0, WIDTH/DOT_SIZE)) == 1	)	
		{
			i--; //nimmt ein Random RasterFeld, setzt dort Nahrung hin, falls dort vorher schon Nahrung war wird dies nicht als neue Nahrung mitgezählt
		}
	};
}

private void makeBabys()
{
	 for (int i = 1;i<=activeThreadsCount; i++)
     {
		if(hunters[i].howdeepisyourlove()==true)
		{
			hunters[i].setlove(0);
			activeThreadsCount++;
			hunters[activeThreadsCount] = new Hunter();							
			 th[activeThreadsCount] = new Thread(hunters[activeThreadsCount]);
			th[activeThreadsCount].start();//ersten Hunter initalisieren als eigenen Thread und starten
			hunters[activeThreadsCount].resetParameters();
			hunters[activeThreadsCount].setx(hunters[i].getx());
			hunters[activeThreadsCount].sety(hunters[i].gety());
			
			
		}
		 
     }
	
}

private void createFirstField()		//erstes Spielfeld mit Nahrung füllen  MENGE
{
	for(int i=0; i<STARTAMOUNT ; i++)
	{	
	
		if(setfoodUnit(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE),ThreadLocalRandom.current().nextInt(0, WIDTH/DOT_SIZE)) == 1	)
		{
			i--;
		}
	};
	
	for (int i = 1; i <= FIRSTHUNTERS; i++)
	{
		hunters[i] = new Hunter();							
		th[i] = new Thread(hunters[i]);
		hunters[i].resetParameters();
		hunters[i].setx(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE));
		hunters[i].sety(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE));
		th[i].start();							//ersten Hunter initalisieren als eigenen Thread und starten
		activeThreadsCount++;
	}
	
	
	
}

private void ResetRaster()
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


private byte setfoodUnit(int x,int y)
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

public int getactivehunters() {
	return 0;
}

public int getactivefood()
{
return activeFoodCount;	
}
}
