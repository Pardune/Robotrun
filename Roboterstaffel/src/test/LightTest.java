package test;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.LightDetector;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class LightTest {
	static DifferentialPilot pilot;
	static LightSensor light;
	static UltrasonicSensor dist;
	static Thread algo;

	public static void main(String[] args) {
		pilot = new DifferentialPilot(43.2f, 161f, Motor.A, Motor.B, false);
		light = new LightSensor(SensorPort.S1);
		dist = new UltrasonicSensor(SensorPort.S4);
		boolean hasCan;
		//findWhitePaper();
		/*while (true) {
			checkLightValue();
		}*/
		
		//followLine();
		getOnLine();
		//driveOnLine();
		/*if (isLineLeft()== true ) {
			Delay.msDelay(2000);
			Sound.beep();
		}
		Delay.msDelay(5000);
		if (isLineRight() == true) {
			Delay.msDelay(2000);
			Sound.beep();
			Delay.msDelay(500);
			Sound.beep();
		}*/
		//Delay.msDelay(1000);
	}
	
	public static void getRawValue() {
		System.out.println("          " + light.readNormalizedValue());
		Delay.msDelay(1000);
	}
	
	public static void driveOnLine (){ //precondition: robot is in the line
		while (true) {
			boolean lineOnLeft = isLineLeft();
			boolean lineOnRight = isLineRight();
			if (!lineOnLeft && !lineOnRight) {
				pilot.travel(100);
			}
			else if (lineOnLeft) {
				pilot.rotate(20);
				pilot.travel(50);
			}
			else if (lineOnRight) {
				pilot.rotate(-20);
				pilot.travel(50);
			}
			if (dist.getDistance() < 50) {
				pilot.rotate(180);
			}
		}
	}
	
	public static boolean isLineRight() { //precondition: line is in front of robot
		int lightAtStart;
		int lightRight;
		
		lightAtStart = light.getLightValue();
		System.out.println("            " + lightAtStart);
		Delay.msDelay(500);
		pilot.rotate(-45);
		lightRight = light.getLightValue();
		System.out.println("            " + lightRight);
		Delay.msDelay(500);
		pilot.rotate(45);
		
		
		if (Math.abs(lightRight - lightAtStart) > 3) return false;
		else return true;
	}
	
	public static boolean isLineLeft() { //precondition: line is in front of roboter
		int lightAtStart;
		int lightLeft;
		
		lightAtStart = light.getLightValue();
		System.out.println("            " + lightAtStart);
		Delay.msDelay(500);
		pilot.rotate(45);
		lightLeft = light.getLightValue();
		System.out.println("            " + lightLeft);
		Delay.msDelay(500);
		pilot.rotate(-45);

		if (Math.abs(lightLeft - lightAtStart) < 3) return true;
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
		Delay.msDelay(1000);
	}
	
	public static void followLine() {
		int white = 44;	
		int angle = 3;	
		int dir = 1;	
		int rotDist = 1;
		int wert;
		pilot.setTravelSpeed(50);
		
		while (true) {
			wert = light.getLightValue(); 				
			if (white - wert > 3) {
				pilot.stop();	
				while(white - wert > 3) {	
					pilot.rotate(angle*dir*rotDist);	
					dir *= -1;			
					rotDist += 2;				
					wert = light.getLightValue();
				}
			}
			else {
				pilot.forward();				
				rotDist = 1;					
				
				if (dist.getDistance() < 30) {
					pilot.stop();
					pilot.rotate(180);
				}
			}
		}

	}

	public static void getOnLine() {
		pilot.setTravelSpeed(50);
		pilot.forward();
		Thread testLight = new Thread() {
			public void run() {
				while (true) {
					if (light.getLightValue() > 42) {
						algo.interrupt();
						handleLine();
						return;
					}
				}
			}
		};
		testLight.start();
		
		algo = new Thread() {
			public void run() {
				while (true) {
					
				}
			}
		};
		algo.start();
	}
	
	public static void handleLine() {
		boolean left = isLineLeft();
		boolean right = isLineRight();
		if (left && right) {
			pilot.travel(30);
			pilot.rotate(90);
			followLine();
		}
		else if (left) {
			pilot.travel(30);
			pilot.rotate(120);
			followLine();
		}
		else if (right) {
			pilot.travel(30);
			pilot.rotate(30);
			followLine();
		}
		else {
			followLine();
		}
	}
}
