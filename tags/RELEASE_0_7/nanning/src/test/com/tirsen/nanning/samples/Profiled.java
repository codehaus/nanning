package com.tirsen.nanning.samples;

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
