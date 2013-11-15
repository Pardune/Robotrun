package pardune;

/**
 * this programm sends data to a slave
 * it's used to tell the other robot waht to do
 * 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class CommMaster {
	// fields for constuctor CommMaster()
	DataOutputStream dos;
	DataInputStream dis;
	BTConnection btc;
	RemoteDevice btrd;
	String remoteName = "NXT";
	
	// fields for sendData()
	int data;

	public CommMaster() throws Exception {
		// setup for pcToSource()
		//btc = Bluetooth.waitForConnection();
				
		// setup for nxtToNxt()
		btrd = Bluetooth.getKnownDevice(remoteName);
		System.out.println("      " + 123);
		btc = Bluetooth.connect(btrd);
		System.out.println("      " + 345);
		// setup end
		dos = btc.openDataOutputStream();
		dis = btc.openDataInputStream();
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
		dis.close();
		dos.close();
		Thread.sleep(100);			// better do this
	}
	
	public static void main(String[] args) throws Exception{
		CommMaster nxt = new CommMaster();
		for (int i = 0; i < 10; i++) {
			nxt.sendData(i);
		}
		nxt.end();
	}
}
