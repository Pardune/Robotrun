package test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class ConnectionTest {

	public static void main(String[] args) {
		
	}
	
	public static void startServer() {
		RemoteDevice btrd = Bluetooth.getKnownDevice(name); //TODO
		
		BTConnection btc;
		while (btc == null) {
			btc = Bluetooth.connect(btrd);
		}
		
		DataInputStream dis = btc.openDataInputStream();
	    DataOutputStream dos = btc.openDataOutputStream();
	    try {
	    	for (int i = 10; i > 0; i--) {
	    		dos.writeInt(i);
	    		dos.flush();
	    	}
			dis.close();
		    dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    btc.close();
	}
	
	public static void startClient() {
		
		BTConnection btc;
		btc = Bluetooth.waitForConnection();
		
		DataInputStream dis = btc.openDataInputStream();
	    DataOutputStream dos = btc.openDataOutputStream();
	    try {
	    	for (int i = 0; i < 10; i++) {
	    		dos.writeInt(i);
	    		dos.flush();
	    	}
			dis.close();
		    dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    btc.close();
	}

}
