package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * pcToSource()
 * PC can receive data from another BT-Source
 * and sends the data back
 * 
 * nxtToNxt()
 * name of remote BT-Device has to be "NXT", set via field "remoteName"
 * master has to establish connection
 * 
 * @author birger
 *
 */

public class Communications  {
	// fields for pcToSource()
	DataInputStream dis;
	DataOutputStream dos;
	BTConnection btc;
	
	// fields for nxtToNxt()
	RemoteDevice btrd;
	String remoteName = "NXT";
	
	public Communications() throws Exception {
		// setup for pcToSource()
		btc = Bluetooth.waitForConnection();
		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();
		
		// setup for nxtToNxt()
		btrd = Bluetooth.getKnownDevice(remoteName);
		// setup end
		
		// call functions that do candy
		//pcToSource();
		nxtToNxt();
		end();			// obligatory!
	}
	
	private void pcToSource() throws Exception {
		// the data stuff
		int n = dis.readInt();		// can be loop-ed
		dos.writeInt(-n);
		dos.flush();

	}
	
	private void nxtToNxt() throws Exception {
		int intToSend = 5;
		
		try {
			dos.writeInt(intToSend);
			dos.flush();
			System.out.println("Int was sent");
		} catch (IOException ioe){
			System.out.println("Write Exception");
		}
		
		try {
			System.out.println("read: " + dis.readInt());
		} catch (IOException ioe) {
			System.out.println("Close Exception");
		}
	}
	
	private void end() throws Exception{
		// obligatory
		dis.close();
		dos.close();
		Thread.sleep(100);			// better do this
	}
}