package pardune;
/**
 * this program receives data from a master
 * 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class CommSlave {
	// fields for constuctor CommMaster()
	DataInputStream dis;
	DataOutputStream dos;
	BTConnection btc;
	RemoteDevice btrd;
	String remoteName = "NXT";

	// fields for sendData()
	int data;

	public CommSlave() throws Exception {
		// setup for pcToSource()
		btc = Bluetooth.waitForConnection();
		dis = btc.openDataInputStream();
		dos = btc.openDataOutputStream();

		// setup for nxtToNxt()
		btrd = Bluetooth.getKnownDevice(remoteName);
		// setup end
	}

	public int recData(){
		try {
			data = dis.readInt();		// input stream data is stored in a variable
		} catch (IOException ioe){
			System.out.println("Read Exception");
			return 1;
		}
		System.out.println("         data: " + data);
		return data;
	}

	public void end() throws Exception{
		// obligatory
		dis.close();
		dos.close();
		Thread.sleep(100);			// better do this
	}
	
	public static void main(String[] args) throws Exception{
		CommSlave nxt = new CommSlave();
		for (int i = 0; i < 10; i++) {
			nxt.recData();
		}
		nxt.end();
	}
}

