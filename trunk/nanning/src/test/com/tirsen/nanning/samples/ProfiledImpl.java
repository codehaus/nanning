package com.tirsen.nanning.samples;

public class ProfiledImpl implements Profiled
{

	public void someMethod()
	{
		notProfiledMethod();
		
	}
	
	public void notProfiledMethod()
	{
	}
	
	public void delayTwoHundredMillis()
	{
		try
		{
			Thread.sleep(200);
		}
		catch(InterruptedException e)
		{
		}
	}

}
