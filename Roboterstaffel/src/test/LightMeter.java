package test;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class LightMeter {

	static LightSensor light = new LightSensor(SensorPort.S1);
	static boolean signal;
	static boolean loop;
	
	public static boolean getSignal() {
		return signal;
	}
	
	public static void stopLoop() {
		loop = false;
	}
	
	public static void lightSensor(final DifferentialPilot pilot) {
		signal = false;
		loop = true;
		while(loop) {
			if (40 - light.getLightValue() < 0) {
				pilot.quickStop();
				signal = true;
				System.out.println("         Found Line!!");
				break;
			}
			Delay.msDelay(10);
		}
	}

}
