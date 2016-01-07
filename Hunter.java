package hardcode;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

import org.eclipse.swt.widgets.Display;

public class Hunter implements Runnable{											//Macht die Classe zu einen Threadbaren Objekt

    private  int DELAY = 500;														// Geschwindigkeit des Threads
private int xposnow = 10;															//aktuelle X Position 
private int yposnow = 20;															//aktuelle Y Position
private int food = 200;																//Momentane Nahrungsmenge / Hunger 
private int love = 0;																// gespeicherte Liebe. erhöt sich durch essen und wird zum Fortpflanzen benötigt
private int maxfood = 300;															// Maximaler Nahrungswert / food <= maxfood


	@Override
	public void run() {																// Thread run
		
		while (true) 
		{				
				try
				{
				Thread.sleep(DELAY);												//bewegungsgeschwindigkeit (nicht zu schnell machen, sonst gibts probleme)
				} catch (InterruptedException e) {break;}
			
				MoveHunter();														//Bewegungsalgorithmus des Hunters (siehe Funktion)
				
				
			    if (Thread.interrupted()) {											//wenn .interrupt aufgerufen wird, dann:
			           break;
			    	}	
			    
			    try {
			    	if (food <= 0)													// wenn der Nahrungswert/Hunger 0 erreicht, dann wird der Thread automatisch interrupted	 
					throw new InterruptedException();
				} catch (InterruptedException e) {
					break;
				} 
			    
	
		}
	}


public void feedme()				//beim aufrufen dieser Funktion bekommt der Hunter +100 food und +1 love
{
	if (food <= maxfood)
	{
		food = food +100;
		love = love +1;
	}
	
}

public boolean howdeepisyourlove()		//checkt, ob der Food-wert über 150 und die love >=5. in diesem Falle ist der Hunter bereit, sich fortzupflanzen (return true)
{
	if (love >= 5 && food >=150)
	{
	return true;
	}else return false;
}

public void setlove(int a)			// setzt den love-Wert auf den übergebenen wert (wird zum zurücksetzen auf 0 beim paaren verwendet)
{
	love = a;
}

public int getfood()			//zum abfragen des food-Wertes / Hungers des Hunters
{
	return food;
}

	
public void MoveHunter()	

/*	Der Algorithmus für die Bewegung des Hunters. Im moment noch ohne wirkliches Muster/AI verhalten und komplett willkürlich
 * Der Algorithmus erzeugt dabei eine Zahl zwischen 0 und 40 und bewegt den Hunter je nach Zahl in eine bestimmte Richtung.
 * Der Algorithmus ist dabei fest auf eine Feldgröße von 100x100 Programmiert. bei änderung der Feldgröße müssen also die if Anweisungen 
 * angepasst werden.
 * 
 */
{

Random rand = new Random();	
int f = rand.nextInt(41);						// random int erzeugen zwischen 0 und 40 und dann je nachdem bewegen

	if(f <= 9)
	{
		if (xposnow < 99 && xposnow>1)	{xposnow =xposnow+1;} 
		if (xposnow == 99) {xposnow--;} 
	}
	if( f <=19 && f>=10)
	{
		if (yposnow < 99 && yposnow>1)	{yposnow =yposnow+1;} 
		if (yposnow == 99) {yposnow--;}
	}
	if( f <=29 && f>=20)
	{
		if (xposnow < 99 && xposnow>1)	{xposnow =xposnow-1;}
		if (xposnow == 1) {xposnow++;}
	}
	if( f <=39 && f>=30)
	{
		if (yposnow < 99 && yposnow>1)   {yposnow =yposnow-1;} 
		if (yposnow == 1) {yposnow++;}
	}

												// nach der bewegung wird noch Nahrung "verbrannt"
	if(food >0)
	food = food - 5;							//jede bewegung verbraucht momentan 5 nahrung
	if(food <= 5)
	{
	food = 0;
	}
	
}

public void resetParameters()	// Funktion, um alle Parameter wieder auf den Standardwert zu setzen
{
	love = 0;
	food = 200;
	xposnow = 10;
	yposnow = 20;
}

public int getx()
{
	return xposnow;
}

public int gety()
{
	return yposnow;
}

public void setx(int a)
{
	xposnow = a;
}

public void sety(int a)
{
	yposnow = a;
}

}
