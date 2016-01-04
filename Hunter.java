package hardcode;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

import org.eclipse.swt.widgets.Display;

public class Hunter implements Runnable{			//Macht die Classe zu einen Threadbaren Objekt

    private  int DELAY = 500;
private int xposnow = 10;
private int yposnow = 20;
private int food = 200;				//Hungerwert
private int love = 0;
private int maxfood = 300;


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
			    
			    try {
			    	if (food <= 0)
					throw new InterruptedException();
				} catch (InterruptedException e) {
					break;
				} 
			    
	
		}
	}


public void feedme()			//erhöt die Nahrung
{
	if (food <= maxfood)
	{
		food = food +100;
		love = love +1;
	}
	
}

public boolean howdeepisyourlove()
{
	if (love >= 5 && food >=150)
	{
	return true;
	}else return false;
}

public void setlove(int a)
{
	love = a;
}

public int getfood()
{
	return food;
}


public void MoveHunter()			//die bewegungsmechanismen. Im moment noch sehr simpel und willkürlich !! 
{

Random rand = new Random();	
int f = rand.nextInt(41);		// random int erzeugen zwischen 0 und 40 und dann je nachdem bewegen

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

	if(food >0)
	food = food - 2;	//jede bewegung verbraucht momentan 5 nahrung
	if(food <= 2)
	{
	food = 0;
	}
	
}

public void resetParameters()
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
