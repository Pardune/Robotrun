package robot;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

/**
 * Provides miscellaneous methods for line detecting and handling.
 * @author Anton Komarov, Stefan Schuler
 * @version 2.0
 */
public class LightTest {
	static DifferentialPilot pilot = new DifferentialPilot(43.2f, 161f, Motor.A, Motor.B, false);
	static LightSensor light = new LightSensor(SensorPort.S1);
	static UltrasonicSensor dist = new UltrasonicSensor(SensorPort.S4);
	
	static int floor;
	static int line;
	
	/**
	 * Has not further role, only for testing. Other methods are static and executed via main/main2 classes
	 * @param args void
	 */
	public static void main(String[] args) {
		
		while (true) {
			checkLightValue();
		}
		//followLine();
	}
	
	/**
	 * Sets the pilot to "regular" acceleration
	 */
	public static void setPilot() {
		pilot.setAcceleration(60);
	}
	
	/**
	 * Evaluates the light value of the line depending on the current light value (floor at this time)
	 */
	public static void setLineValue() {
		
		floor = light.getLightValue();
		floor = light.getLightValue();
		floor = light.getLightValue();
		line = floor + 5;
	}
	
	/**
	 * Measures the current raw light value and prints it on the NXTs screen
	 */
	public static void getRawValue() {
		System.out.println("          " + light.readNormalizedValue());
		Delay.msDelay(1000);
	}
	
	/**
	 * Checks if the line is on the right side (45 degree angle) of the robot
	 * Precondition: line is in front of robot
	 * @return true, if line is right. false, if not.
	 */
	public static boolean isLineRight() { 
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
	
	/**
	 * Checks if the line is on the left side (45 degree angle) of the robot
	 * Precondition: line is in front of robot
	 * @return true, if line is left. false, if not.
	 */
	public static boolean isLineLeft() { 
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
	
	/**
	 * Measures the current light value (0-100) and prints it on the NXTs screen
	 */
	public static void checkLightValue() {
		System.out.println("          " + light.getLightValue());
		Delay.msDelay(1000);
	}
	
	/**
	 * Follows the line till there is an obstacle(wall), then stops. Then rotates if input value is true.
	 * Precondition: Robot is able to spot the line with the light sensor.
	 * @param turnAtEnd true, if robot has to rotate (180 degree) at the end. false, if no rotation.
	 */
	public static void followLine(boolean turnAtEnd) {
		
		pilot.setAcceleration(60);
		int angle = 3;	
		int dir = 1;	
		int rotDist = 1;
		int wert;
		
		while (true) {
			wert = light.getLightValue(); 				
			if (line - wert > 1) {					//line lost
				pilot.setAcceleration(1000);
				pilot.stop();	
				pilot.setAcceleration(60);
				while(line - wert > 1) {			//trying to find line with increasing angles
					pilot.rotate(angle*dir*rotDist);	
					dir *= -1;			
					rotDist += 2;				
					wert = light.getLightValue();
				}
			}
			else {							//line still found
				pilot.forward();				
				rotDist = 1;					
				if (turnAtEnd) {
					if (dist.getDistance() < 20) { //wall found, going into position and return
						pilot.setAcceleration(1000);
						pilot.stop();
						pilot.rotate(175);
						pilot.setAcceleration(60);
						return;
					}
				} else {
					if (dist.getDistance() < 21) { //wall found, going into position and return
						pilot.setAcceleration(1000);
						pilot.stop();
						pilot.setAcceleration(60);
						return;
					}
				}	
			}
		}

	}
	
	/**
	 * Checks if the line is on the left or the right of the robot. Then turns in appropriate angle and starts followLine().
	 * 
	 * @param turn Handed to followLine(), specifies if robot turns at the wall.
	 */
	public static void handleLine(boolean turn) {
		boolean left = isLineLeft();
		boolean right = isLineRight();
		if (left && right) {
			pilot.travel(30);
			pilot.rotate(90);
			followLine(turn);
		}
		else if (left) {
			pilot.travel(30);
			pilot.rotate(120);
			followLine(turn);
		}
		else if (right) {
			pilot.travel(30);
			pilot.rotate(30);
			followLine(turn);
		}
		else {
			followLine(turn);
		}
	}
}
