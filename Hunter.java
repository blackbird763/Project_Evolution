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


public void feedme()			//erhöt die Nahrung
{
	if (food <= 400)
	{
		food = food +100;
		love = love +1;
	}
	
}

public boolean howdeepisyourlove()
{
	if (love >= 5 && food >=200)
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
