package test;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
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
		checkLightValue();
		
	}
	
	public static void findWhitePaper() {
		pilot.travel(300);
		int first = light.getNormalizedLightValue();
		pilot.travel(300);
		int two = light.getNormalizedLightValue();
		if (two > first) pilot.travel(-300);
	}
	
	public static void checkLightValue() {
		System.out.println(light.getLightValue());
		Delay.msDelay(10000);
	}

}
