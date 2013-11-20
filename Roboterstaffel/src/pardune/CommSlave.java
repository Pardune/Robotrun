package pardune;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * this program receives data from a master
 * 
 *
 */
public class CommSlave {
	
	DataInputStream dis;
	DataOutputStream dos;
	BTConnection btc;
	//RemoteDevice btrd;
	String remoteName = "NXT";

	// fields for sendData()
	int data;

	/**
	 * Waits for an incoming connection from master robot.
	 */
	public CommSlave() {
		try {
			btc = Bluetooth.waitForConnection();
			dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();

			//btrd = Bluetooth.getKnownDevice(remoteName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Blocks till other robot sends a 1 (is ready).
	 */
	public void waitForAnswer() {
		try {
			while (dis.readInt() != 1) {
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a 1 to the other robot, to tell him to go on.
	 */
	public void sendReady() {
		try {
			dos.writeInt(1);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Closes input and output stream of the connection.
	 */
	public void end() {
		try {	
			// obligatory
			dis.close();
			dos.close();
			Thread.sleep(100);			//wait for streams being closed
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

