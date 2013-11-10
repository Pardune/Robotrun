package test;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class DistanceTest {

	public static void main(String[] args) {
		UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S4);
		DifferentialPilot pilot = new DifferentialPilot(43.2f, 180f, Motor.A, Motor.B, false);
		pilot.setAcceleration(30);
		while (ultra.getDistance() > 30) {
			pilot.travel(100);
		}
		pilot.rotate(180);
		pilot.travel(300);
	}

}
