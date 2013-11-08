package test;

import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DifferentialPilot pilot = new DifferentialPilot(43.2f, 4.4f, Motor.A, Motor.C, false);
		pilot.travel(100);
	}

}
