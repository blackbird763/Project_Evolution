package hardcode;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

import org.eclipse.swt.widgets.Display;

public class Hunter implements Runnable{			//Macht die Classe zu einen Threadbaren Objekt

    private  int DELAY = 200;
private int xposnow = 10;
private int yposnow = 20;
private int food = 400;				//Hungerwert


	@Override
	public void run() {
		
		while (true) 
		{				
				try
				{
				Thread.sleep(DELAY);						//bewegungsgeschwindigkeit (nicht zu schnell machen, sonst gibts probleme)
				} catch (InterruptedException e) {break;}
			
				MoveHunter();
			
				
				
			    if (Thread.interrupted()) {
			           break;
			    	}	
	
		}
	}


public void feedme()			//erh�t die Nahrung
{
	if (food <= 400)
	{
		food = food +100;
	}
}

public int gethunger()
{
	return food;
}

public void CreateHunter(int i, int j)		//setzt die Position des Hunters um
{
	xposnow=i;
	yposnow=j;
}

public void MoveHunter()			//die bewegungsmechanismen. Im moment noch sehr simpel und willk�rlich !! 
{

Random rand = new Random();	
int f = rand.nextInt(41);		// random int erzeugen zwischen 0 und 40 und dann je nachdem bewegen

	if(f <= 9)
	{
		if (xposnow < 99 && xposnow>1)	xposnow =xposnow+1;
	}
	if( f <=19 && f>=10)
	{
		if (yposnow < 99 && yposnow>1)	yposnow =yposnow+1;
	}
	if( f <=29 && f>=20)
	{
		if (xposnow < 99 && xposnow>1)	xposnow =xposnow-1;
	}
	if( f <=39 && f>=30)
	{
		if (yposnow < 99 && yposnow>1)   yposnow =yposnow-1;
	}

	if(food >0)
	food = food - 5;	//jede bewegung verbraucht momentan 5 nahrung
	
}



public int getx()
{
	return xposnow;
}

public int gety()
{
	return yposnow;
}



}