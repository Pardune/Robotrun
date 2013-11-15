package test;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class LightMeter {

	static LightSensor light = new LightSensor(SensorPort.S1);
	static boolean signal = false;
	
	static boolean getSignal() {
		return signal;
	}
	
	static void lightSensor(final DifferentialPilot pilot) {
		while(!signal) {
			if (41 - light.getLightValue() > 3) {
				pilot.quickStop();
				signal = true;
			}
			Delay.msDelay(10);
		}		
	}

}
