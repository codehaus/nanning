package org.codehaus.nanning.profiler;

public interface Profiled
{
	/**
	 * @profile
	 */
	public void someMethod();
	
	public void notProfiledMethod();
	
	/**
	 * @profile
	 */
	public  void delayTwoHundredMillis();


}
