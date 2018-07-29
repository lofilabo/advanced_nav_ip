package com.lfl.MyAnpp;

import jssc.*;
import javax.swing.SwingUtilities;
import com.advancednavigation.anPackets.*;
import java.io.*;  
import java.net.*; 

public class Main {

	private SerialPort serialPort;
	protected Boolean serialConnected = false;
	private Boolean serialPortsAvailable = false;
	public String comPort;
	public String ipAddress;
	public int ipPort;
	public int standAlone;
	public int countTracker;
	public int countTrackerLimit;
	public String httpAddressAndPort;
	
    public static void main( String [] arguments ){
    	
    	Main main = new Main();

    	main.countTracker = 0;
/*    	
    	if(arguments.length > 2) {
    		//should be: COM PORT, IP ADDRESS, IP PORT
    		main.comPort = arguments[0];
    		main.ipAddress = arguments[1];
    		main.ipPort = Integer.parseInt(arguments[2]);
    		main.standAlone = 0;    		
    	}else if(arguments.length > 1){
    		//no valid options
    		System.out.println("Incorrect args: COM Port, IP Address, IP Port");
    		System.exit(0);   		
    	}else if(arguments.length > 0) {
    		//COM PORT
			main.comPort = arguments[0];
			main.standAlone = 1;   		
    	}else {
    		//no args.  wrong
    		System.out.println("Incorrect args: COM Port, [ IP Address, IP Port ]");
    		System.exit(0);   		
    	}
*/
    	
    	if(arguments.length < 2) {
    		//no args.  wrong
    		System.out.println("Incorrect args: Count, COM Port, [ IP Address, IP Port ]");
    		System.exit(0);    		
    	}else if(arguments.length == 2) {
    		//count, com port
    		main.countTrackerLimit = Integer.parseInt(arguments[0]);
			main.comPort = arguments[1];
			main.standAlone = 1;// 1 indicates, print only, no ip activity
    	}else if(arguments.length == 3) {
    		//count, com port, http ip:port
    		main.countTrackerLimit = Integer.parseInt(arguments[0]);
    		main.comPort = arguments[1];
    		main.httpAddressAndPort = arguments[2];
    		main.standAlone = -1;  //-1 indicates: use HTTP Address:Port as 1 string  	
    	}else {
    		//count, com port, ip addr, port
    		main.countTrackerLimit = Integer.parseInt(arguments[0]);
    		main.comPort = arguments[1];
    		main.ipAddress = arguments[2];
    		main.ipPort = Integer.parseInt(arguments[3]);
    		main.standAlone = 0;  //0 indicates: use IP Address + Port  		
    	}
    	
    	//System.exit(0);
    	
    	 main.serialSetup( main );
    }
    
    public void serialSetup(Main main){
    	
		serialPort = new SerialPort((String) comPort);
		try {
			serialPort.openPort();
			serialPort.setParams(Integer.parseInt((String) "115200"), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
			serialPort.addEventListener(new SerialReader(main));
		}catch (Exception exception) {
			System.err.println(exception.toString());
			serialPortClose();
		}    	
    }

	private void serialPortClose() {
		try{
			serialPort.removeEventListener();
			serialPort.closePort();
		}catch (SerialPortException e){
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}		
	}

	public class SerialReader implements SerialPortEventListener {
		ANPacketDecoder packetDecoder;
		int i;
		DataOutputStream dout;
		Socket soc;
		public Main main;
		
		public SerialReader( Main main ){
			packetDecoder = new ANPacketDecoder();
			if (main.standAlone==0){
				startConnection(main);
			}else {
				this.main = main;
			}
		}
		
		public void startConnection(Main main) {
			this.main = main;
			while(true) {
				try {
			        soc = new Socket(main.ipAddress, main.ipPort);
			        dout = new DataOutputStream(soc.getOutputStream());
					break;
				}catch (SocketTimeoutException e){
					System.err.println("Trying to connect ");
				}catch (IOException e){
					System.err.println("IO Exception ");
			    }catch(Exception e) {
					System.err.println(e.toString());
				}
			}
			
		}
		
		public void finalize() {
			 try {
				dout.close();  
				soc.close();
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}
		
		public void serialEvent(SerialPortEvent event)
		{
			if (event.isRXCHAR()){
				try{
					byte[] buffer = serialPort.readBytes();
					if (buffer != null){
						for (int i = 0; i < buffer.length; i++)	{
							if (packetDecoder.bufferLength < packetDecoder.buffer.length){
								packetDecoder.buffer[packetDecoder.bufferLength++] = buffer[i];
							}
						}
						
						ANPacket packet = null;
						while ((packet = packetDecoder.packetDecode()) != null){
							switch (packet.id){
								case ANPacket.PACKET_ID_RAW_SENSORS:
									if (packet.length == 48){
										final ANPacket28 anPacket28 = new ANPacket28(packet);
										SwingUtilities.invokeLater(new Runnable(){
											public void run(){
											}
										});
									}
									break;
								case ANPacket.PACKET_ID_SYSTEM_STATE:
									if (packet.length == 100){
										final ANPacket20 anPacket20 = new ANPacket20(packet);
	
										SwingUtilities.invokeLater(new Runnable(){
											public void run(){
												
												main.countTracker++;
												
												if( main.countTracker > main.countTrackerLimit ) {
													main.countTracker = 0;
												
												
													System.out.print( anPacket20.position[0] * 180 / Math.PI );
													System.out.print( "\t");
													System.out.print( anPacket20.position[1] * 180 / Math.PI );
													System.out.print( "\t\t");
													System.out.print( anPacket20.position[2]);
													System.out.print( "\t");
													System.out.print( anPacket20.orientation[0] * 180 / Math.PI );
													System.out.print( "\t");
													System.out.print( anPacket20.orientation[1] * 180 / Math.PI );
													System.out.print( "\t");
													System.out.print( anPacket20.orientation[2] * 180 / Math.PI );
													System.out.println(  );
													
													if ( main.standAlone==0){
														try {
														    dout.writeUTF(
														    		"-->>" 
														    		+ anPacket20.position[0] * 180 / Math.PI 
														    		+ ":"
														    		+ anPacket20.position[1] * 180 / Math.PI 
														    		+ ":"
														    		+ anPacket20.position[2] / Math.PI 
														    		+ ":"
														    		+ anPacket20.orientation[0] * 180 / Math.PI 
														    		+ ":"
														    		+ anPacket20.orientation[1] * 180 / Math.PI 
														    		+ ":"
														    		+ anPacket20.orientation[2] * 180 / Math.PI
														    		 
														    );
														    dout.flush();
		
														}catch(SocketException VariableDeclaratorId) {
															System.err.println("No Socket. Starting connection again.");
															startConnection(main);
														}catch(Exception e) {
															System.err.println(e.toString());
														}
													}else if( main.standAlone==-1 ) {
														System.out.println("HTTP Things");
											    		//System.exit(0);
														AdvHttpURLConnection httpconn = new AdvHttpURLConnection();
														try {
															httpconn.sendGet(
																	main.httpAddressAndPort
																	,
																	"/" 
														    		//+ "-->>" 
														    		+ anPacket20.position[0] * 180 / Math.PI 
														    		+ "\t"
														    		+ anPacket20.position[1] * 180 / Math.PI 
														    		+ "\t"
														    		+ anPacket20.position[2] / Math.PI 
														    		+ "\t"
														    		+ anPacket20.orientation[0] * 180 / Math.PI 
														    		+ "\t"
														    		+ anPacket20.orientation[1] * 180 / Math.PI 
														    		+ "\t"
														    		+ anPacket20.orientation[2] * 180 / Math.PI																	
															);
														} catch (Exception e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														
													}
													i++;
												}
											}
										});
									}
									break;
								default:
									break;
							}
						}
					}
				}catch (SerialPortException exception){
					System.err.println(exception.toString());
				}
			}
		}
	}
}
