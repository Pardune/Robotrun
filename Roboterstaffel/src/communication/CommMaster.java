package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.bluetooth.RemoteDevice;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * this programm sends data to a slave
 * it's used to tell the other robot what to do
 *
 */
public class CommMaster {
	DataOutputStream dos;
	DataInputStream dis;
	BTConnection btc;
	RemoteDevice btrd;
	String remoteName = "NXT";
	
	//int data;

	/**
	 * Establishes a connection to slave robot named by class field "remoteName". Then opens input and output
	 * streams.
	 */
	public CommMaster() {
		try {
			btrd = Bluetooth.getKnownDevice(remoteName);
			//System.out.println("      " + 123);
			btc = Bluetooth.connect(btrd);
			//System.out.println("      " + 345);
			dos = btc.openDataOutputStream();
			dis = btc.openDataInputStream();
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
/*	
	public int sendData(int data){
		try {
			dos.writeInt(data);
			dos.flush();
		} catch (IOException ioe){
			System.out.println("Write Exception");
			return 1;
		}
		
		return 0;
	}*/
	
	/**
	 * Closes input and output stream of the connection.
	 */
	public void end() {
		try {
			// obligatory
			dis.close();
			dos.close();
			Thread.sleep(100);			// better do this
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
