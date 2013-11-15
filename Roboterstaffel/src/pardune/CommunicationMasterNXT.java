package pardune;

/**
 * this programm sends data to a slave
 * it's used to tell the other robot waht to do
 * 
 */
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class CommunicationMasterNXT {
	// fields for constuctor CommunicationMasterNXT()
	DataOutputStream dos;
	BTConnection btc;
	RemoteDevice btrd;
	String remoteName = "NXT";
	
	// fields for sendData()
	int data;

	public CommunicationMasterNXT() throws Exception {
		// setup for pcToSource()
		btc = Bluetooth.waitForConnection();
		dos = btc.openDataOutputStream();
				
		// setup for nxtToNxt()
		btrd = Bluetooth.getKnownDevice(remoteName);
		// setup end
	}
	
	public int sendData(int data){
		try {
			dos.writeInt(data);
			dos.flush();
		} catch (IOException ioe){
			System.out.println("Write Exception");
			return 1;
		}
		
		return 0;
	}
	
	public void end() throws Exception{
		// obligatory
		dos.close();
		Thread.sleep(100);			// better do this
	}
	
	public static void main(String[] args) throws Exception{
		new CommunicationMasterNXT();
	}
}
