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

import lejos.nxt.Motor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.navigation.DifferentialPilot;

public class CommMaster {
	// fields for constuctor CommMaster()
	DataOutputStream dos;
	DataInputStream dis;
	BTConnection btc;
	RemoteDevice btrd;
	String remoteName = "NXT";
	
	// fields for sendData()
	int data;

	public CommMaster() {
		try {
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
