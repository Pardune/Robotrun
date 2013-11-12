package test;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.LightDetector;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class LightTest {
	static DifferentialPilot pilot;
	static LightSensor light;

	public static void main(String[] args) {
		pilot = new DifferentialPilot(43.2f, 180f, Motor.A, Motor.B, false);
		light = new LightSensor(SensorPort.S1);
		
		//findWhitePaper();
		//checkLightValue();
		if (isLineLeft()) Sound.beep();
		if (isLineRight()) {
			Sound.beep();
			Sound.beep();
		}
		
	}
	
	public static boolean isLineLeft() {
		int lightAtStart;
		int lightLeft;
		
		lightAtStart = light.getNormalizedLightValue();
		pilot.rotate(90);
		lightLeft = light.getNormalizedLightValue();
		pilot.rotate(-90);
		
		
		if (lightLeft >= lightAtStart) return true;
		else return false;
	}
	
	public static boolean isLineRight() {
		int lightAtStart;
		int lightRight;
		
		lightAtStart = light.getNormalizedLightValue();
		pilot.rotate(-90);
		lightRight = light.getNormalizedLightValue();

		if (lightRight >= lightAtStart) return true;
		else return false;
	}
	
	public static void findWhitePaper() {
		pilot.travel(300);
		int first = light.getNormalizedLightValue();
		pilot.travel(300);
		int two = light.getNormalizedLightValue();
		if (two > first) pilot.travel(-300);
	}
	
	public static void checkLightValue() {
		System.out.println("          " + light.getLightValue());
		Delay.msDelay(10000);
	}

}
