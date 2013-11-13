package test;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class FirstRoboter {
	
	static DifferentialPilot pilot;
	static LightSensor light;
	static UltrasonicSensor dist;
	static boolean hasCan;
	static boolean onLine;
	static Thread search;
	
	public static void main(String[] args) {
		pilot = new DifferentialPilot(43.2f, 161f, Motor.A, Motor.B, false);
		pilot.setTravelSpeed(40);
		light = new LightSensor(SensorPort.S1);
		dist = new UltrasonicSensor(SensorPort.S4);
		hasCan = false;
		
		SensorPort.S1.addSensorPortListener(new SensorPortListener() {

			@Override
			public void stateChanged(SensorPort aSource, int aOldValue, int aNewValue) {
				if (onLine == false) {
					Sound.beep();
					pilot.stop();
					pilot.rotate(180);
				}
				onLine = !onLine;
			}
		});
		SensorPort.S1.setListenerTolerance(40);
		
		search = new Thread() {
			public void run() {
				searchCan();
			}
		};
		search.run();
		
	}
	
	public static void searchCan() {
		
		while (!hasCan) {
			pilot.stop();
			pilot.travel(300);
		}
	}
}
