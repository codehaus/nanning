package org.codehaus.nanning.profiler;

import org.codehaus.nanning.profiler.Profiled;

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
