package robot;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class Robot {

	DifferentialPilot pilot;
	UltrasonicSensor distanceSensor = new UltrasonicSensor(SensorPort.S1);

	/**
	 * @param args
	 */
	public Robot() {
		robot = new TravelTest();
		robot.pilot = new DifferentialPilot(2.25f, 5.5f, Motor.A, Motor.C);
		go();
	}
	
	private void go(){	
		pilot.travel(20, true);
		while (pilot.isMoving()) {
			if (bump.isPressed()) pilot.stop();
		}
	}
}
