package com.honeywell.relay;

import jssc.*;

/**
* The RelayBoard application allows control of the KMTronic relay boards for zone/fault 
* creation on UUT.
*
* @author  Tony Crichton
* @version 1.2.0
* @since   07-11-2016
*/

public class RelayBoard
{
	SerialPort serialPort;
	String com;
	String receivedData;
	int rioNo;
	int zoneNo;
	int relay;
	int Offset;
		
	/**
	   * This constructor is used to create serial connection to USB controller. 
	   * 
	   * @param comPort This is the serial COM port to connect.
	   */
	
	public RelayBoard(String comPort) throws SerialPortException 
	{
		com = comPort;	
		serialPort = new SerialPort(com);
	}
	
	 /**
	   * This method is used to create panel system faults. 
	   * 
	   * @param failure This is the type of failure to be created
	   * @param state  This is the zone state to be created
	   */
	
	public String setFault(String failure, int state) throws InterruptedException 
	{
		int data[] = {255, ConvertZone(failure, 0), state, 255, (160+rioNo) - Offset, 0};
		SerialTx(data, 0);		
		return receivedData;
		
	}
	
	/**
	   * This method is used to create panel system faults with debounce time. 
	   * 
	   * @param failure This is the type of failure to be created
	   * @param state  This is the zone state to be created
	   * @param debounce This is the programmed debounce time(ms)
	 * @throws InterruptedException 
	   */
	
	public String setFault(String failure, int state, int debounce) throws InterruptedException 
	{
		int data[] = {255, ConvertZone(failure, 0), state, 255, (160+rioNo) - Offset, 0};
		SerialTx(data, debounce);
		return receivedData;
	}	
	
	/**
	   * This method is used to create panel zone activation with debounce time and addr offset. 
	   * 
	   * @param zone This is the zone to be activated
	   * @param state  This is the zone state to be created
	   * @param debounce This is the programmed debounce time(ms)
	   * @param offset This is the RelayBoard addr offset (0 or 1)
	   */
	
	public String setZone(String zone, int state, int debounce, int offset) throws InterruptedException 
	{
		int data[] = {255, ConvertZone(zone, offset), state, 255, (160+rioNo) - Offset, 0};
		SerialTx(data, debounce);		
		return receivedData;
	}	
	
	/**
	   * This method is used to create panel zone activation with debounce time and addr offset. 
	   * 
	   * @param zone This is the zone to be activated
	   * @param state  This is the zone state to be created
	   * @param offset This is the RelayBoard addr offset (0 or 1)
	   */

	public String setZone(String zone, int state, int offset) throws InterruptedException  
	{
		int data[] = {255, ConvertZone(zone, offset), state, 255, (160+rioNo) - Offset, 0};
		SerialTx(data, 0);
		return receivedData;
	}	
	
		
	
	private void SerialTx(int[] data, int debounce) throws InterruptedException 
	{
		int dataClose[] = {255, data[1], 0};	
					
			try
			{
				serialPort.openPort();
								
				serialPort.setParams(SerialPort.BAUDRATE_9600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
   		   	   
				serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
		   
		   serialPort.writeIntArray(data);
		    		    		    
		    if (debounce > 0)
		    {
		    	try
		    	{
		    	Thread.sleep(debounce);		    	
		    	serialPort.writeIntArray(dataClose);
		    	}
		    	
		    	catch (InterruptedException ex)
		    	{
		    		System.out.println("Error:  " + ex);
		    	}		    	
		    }
		    
		    Thread.sleep(50);
		    serialPort.closePort();
			}
			
			catch (SerialPortException ex)
			{
				System.out.println("Error with port communication:  " + ex);
			}
		    		    
	}
	
	private int ConvertZone(String zone, int offset)
	{
		Offset = offset;
		rioNo = Integer.parseInt(zone.substring(0, 2));
		zoneNo = Integer.parseInt(zone.substring(2, 3));
		relay = (rioNo-(1 + offset)) * 8 + zoneNo;
		return relay;
		
	}
	
	
	private class PortReader implements SerialPortEventListener 
	{

	    @Override
	    public void serialEvent(SerialPortEvent event) 
	    {
	    		    	
	        if(event.isRXCHAR() && event.getEventValue() > 9 && event.getEventValue() <= 10) 
	        {
	            try
	            {
	            	receivedData = serialPort.readHexString(event.getEventValue());
	                
	            }
	            catch (SerialPortException ex) 
	            {
	                receivedData = "Error in receiving string from COM-port: " + ex;
	            }
	        }
	        
	       
	   }
	            
	  }
}	
	






