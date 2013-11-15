package test;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class DistanceTest {

	public static void main(String[] args) {
		UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S4);
		DifferentialPilot pilot = new DifferentialPilot(43.2f, 180f, Motor.A, Motor.B, false);
		while (true) {
			System.out.println("          " + ultra.getDistance());
			Delay.msDelay(100);
		}
	}

}
