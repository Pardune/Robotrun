package test;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DifferentialPilot pilot = new DifferentialPilot(43.2f, 161f, Motor.A, Motor.B, false);
		//pilot.travel(1000);
		
		pilot.setAcceleration(30);
		//for(int x = 0; x <10; x++) {
		pilot.rotate(360);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		int[] rotationArray = new int[36];
		for(int rotation = 0; rotation < 36; rotation = rotation++ ) {
			while (rotation*10 > pilot.getAngleIncrement()) {
				Delay.msDelay(10);
			}
			rotationArray[rotation]=us.getDistance();
		}
		
		
	}

}
