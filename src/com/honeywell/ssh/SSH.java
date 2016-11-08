package com.honeywell.ssh;

import java.io.IOException;
import java.io.InputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


/**
* Application to run shell commands on SSH enabled panel.
*
* @author  Tony Crichton
* @version 1.1.0
* @since   07-11-2016
*/

public class SSH 
{
	
	Session session;
	int i;
	
	/**
	   * This Constructor is used to establish SSH connection to remote panel. 
	   * 
	   * @param host   Remote panel IP
	   * @param user   user login credential
	   * @param password password login credential
	   */
		
	public SSH(String host, String user, String password)
	{
	try
		{	    	
	    	java.util.Properties config = new java.util.Properties(); 
	    	config.put("StrictHostKeyChecking", "no");
	    	JSch jsch = new JSch();
	    	session=jsch.getSession(user, host, 22);
	    	session.setPassword(password);
	    	session.setConfig(config);
	    	session.connect();
	    	System.out.println("\r SSH Session Connected");
		}
	catch(Exception e)
		{
		    e.printStackTrace();
		}
	}
	
	/**
	   * This method is used to send required shell command to panel over established SSH connection. 
	   * 
	   * @param command   Shell command to be sent to remote panel
	   */
	
	public String sendCommand(String command) throws JSchException, IOException
	{
		Channel channel=session.openChannel("exec");
        ((ChannelExec)channel).setCommand(command);
        System.out.print("\rCommand Sent: " + command + "\r");
        channel.setInputStream(null);
        ((ChannelExec)channel).setErrStream(System.err);
        InputStream in=channel.getInputStream();
        channel.connect();
        byte[] tmp=new byte[1024];
        while(true)
        {
          while(in.available()>0)
          {
            i=in.read(tmp, 0, 1024);
            if(i<0)break;
            System.out.print("Command Reply: " + new String(tmp, 0, i) + "\r");
          }
          if(channel.isClosed())
          {
        	  
            System.out.println("exit-status: " + channel.getExitStatus());
            break;
          }
          try{Thread.sleep(1000);}catch(Exception ee){}
        }
        channel.disconnect();
        //session.disconnect();
        return "Exit Status: " + Integer.toString(channel.getExitStatus());
		
	}
	
	/*public String sendFile(String sourceFile, String destination)
	{
		try
		{           
            
            session.connect(); 
            ((ChannelExec)channel).setErrStream(System.err);
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel; 
            sftpChannel.get(sourceFile, destination);
            sftpChannel.exit();
            session.disconnect();
        } 
		catch (JSchException e)
		{
            e.printStackTrace();  
        } 
		catch (SftpException e) 
		{
            e.printStackTrace();
        }
		return "Exit Status: " + Integer.toString(channel.getExitStatus());
 

 
    }*/
	

}
