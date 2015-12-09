package hardcode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;



public class Board extends Canvas {

	private final int WIDTH = 200;				//Pixelbreite des Canvas
    private final int HEIGHT = 200;				//Pixelhöhe des Canvas
    private final int DOT_SIZE = 2;				// 2x2 sprites
    private final int ALL_DOTS = 10000;			//alle maximalen Dots
    private  int DELAY = 200;					// Laufgeschwindigkeit
    private Display display;
    private Shell shell;
    private Runnable runnable1;					
    private Runnable runnable2;
    public final byte Raster[][] = new byte[HEIGHT/DOT_SIZE][WIDTH/DOT_SIZE];	
    //DAS komplette Spielbrett. Wie ein großes Schachbrett. der Inhalt gibt an, wer sich auf dem Feld befindet: 0=leer,1=nahrung,2=jäger
   
    public Hunter[] hunters = new Hunter[200]; 
   public Thread[] t = new Thread[200];
    public Image foodimg;
    public Image hunterimg;
              
public Board(Composite composite) {
		super(composite,SWT.NULL);
		shell = composite.getShell();
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
	
	
	hunters[1] = new Hunter();							
	 t[1] = new Thread(hunters[1]);
	t[1].start();							//ersten Hunter initalisieren als eigenen Thread und starten

	
	
	addListener(SWT.Paint, event -> drawAll(event));			//immer, wenn jemand das Canvas neu bemalen will, wird die fkt. drawAll aufgerufen
																//drawAll malt das momentane Raster auf das Canvas um den Fortschritt visuell darzustellen
	//	addListener(SWT.KeyDown, event -> onKeyDown(event));
	   addListener(SWT.Dispose, event -> {			//wenn die Shell geschlossen wird dann wird das folgende event ausgeführt
		   t[1].interrupt();							
       });
	
	   
    runnable1 = new Runnable() {							//HAUPTTHREAD, kümmert sich um die komplette interaktion zwischen den HunterThreads und dem Spielfeld
    														//	also Nahrungsvergabe, Fortpflanzung etc.
        @Override
        public void run() {	
        	
        	ResetRaster();		//löscht positionen aller hunter vom Feld. Da diese ihre Positionen intern speichern -> löscht die Vorherige Position aller Hunter
        	addFood();
        	
        	
        	if(getInhaltUnit(hunters[1].getx(),hunters[1].gety()) == 1)		
        		hunters[1].feedme();										// wenn ein Hunter auf einem FoodFeld steht bekommt er Nahrung dazu
        			
        	if (hunters[1].gethunger() == 0)
        	{
        		t[1].interrupt();										//wenn die Nahrung eines Hunters auf 0 fällt wird er Terminiert >:)
        	} else
        	{
        		setMonsterUnit(hunters[1].getx(),hunters[1].gety());  		//Die aktuelle Position der Hunter abfragen und auf das Spielfeld setzen für die Visuelle Darstellung
        	}
        	

        	
        
        	
        	
            display.timerExec(DELAY, this);			//stell sicher, das der Thread läuft, solange wie das Programm geöffnet ist
            redraw();  								//schickt Redraw befehl, führt dadurch den SWT.Paint Listener aus (~68)
        } 
    };
    display.timerExec(DELAY, runnable1);		
   
    
}
private void setMonsterUnit(int x, int y)			//setzt Monsterpositionen auf das feld
{
	
	Raster[y][x]=2;
	
	
}



private void addFood()							//Spawnd neue Nahrung
{

	for(int i=0; i<2 ; i++)							//i = MENGE
	{	
	
		if(setfoodUnit(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE),ThreadLocalRandom.current().nextInt(0, WIDTH/DOT_SIZE)) == 1	)	
		{
			i--; //nimmt ein Random RasterFeld, setzt dort Nahrung hin, falls dort vorher schon Nahrung war wird dies nicht als neue Nahrung mitgezählt
		}
	};
}


private void createFirstField()		//erstes Spielfeld mit Nahrung füllen  MENGE
{
	for(int i=0; i<500 ; i++)
	{	
	
		if(setfoodUnit(ThreadLocalRandom.current().nextInt(0, HEIGHT/DOT_SIZE),ThreadLocalRandom.current().nextInt(0, WIDTH/DOT_SIZE)) == 1	)
		{
			i--;
		}
	};
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
   
   for(int a=0;a<=(HEIGHT/DOT_SIZE)-1;a++)
   {
    	for(int i=0;i<=(WIDTH/DOT_SIZE)-1;i++)
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
