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

	public CommSlave() {
		try {
			// setup for pcToSource()
			btc = Bluetooth.waitForConnection();
			dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();

			// setup for nxtToNxt()
			btrd = Bluetooth.getKnownDevice(remoteName);
			// setup end
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void waitForAnswer() {
		try {
			while (dis.readInt() != 1) {
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendReady() {
		try {
			dos.writeInt(1);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void end() throws Exception{
		// obligatory
		dis.close();
		dos.close();
		Thread.sleep(100);			// better do this
	}
}

