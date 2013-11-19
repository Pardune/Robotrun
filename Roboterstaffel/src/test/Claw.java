package test;

import lejos.nxt.Motor;
import lejos.util.Delay;

public class Claw {

	public static void main(String[] args) {
		Motor.C.setSpeed(50);
		Motor.C.rotateTo(90);
		Delay.msDelay(2000);
		Motor.C.rotateTo(0);
	}

}
