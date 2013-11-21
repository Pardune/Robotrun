package test;

import pardune.CommMaster;
import pardune.CommSlave;
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

import java.lang.Math;

public class Main {

	static DifferentialPilot pilot = new DifferentialPilot(43.2f, 160f, Motor.A, Motor.B, false);
	static boolean line = false;

	/**
	 * @param args
	 */

	static int findMaxDistance(int[] rotationArray) {	//calculate to biggest distance, makes collisions unlikely but still possible
		int maxDistance = 0;							//robot should drive into the center to ease can detection -> then handle line (avoid or drive onto it)
		int maxDistanceRot = 0;
		int start = (int) (Math.random()*72);
		for(int i = start; i < start+72; i++) {
			System.out.println("         "+i);
			int newDistance = rotationArray[(i+72)%72] + rotationArray[(i+73)%72] + rotationArray[(i+74)%72] + rotationArray[(i+75)%72]		//possibly to many values -> wrong directions & higher collision danger?
					+ rotationArray[(i+76)%72] + rotationArray[(i+77)%72] + rotationArray[(i+78)%72]+ rotationArray[(i+79)%72] + rotationArray[(i+80)%72];
			if(newDistance > maxDistance) {
				maxDistance = newDistance;
				maxDistanceRot = (i+76)%72;;
			}
		}
		return maxDistanceRot;


	}

	static int findPeak(int[] rotationArray) {

		int[] sortArray = arraySort(rotationArray);
		int peak = -1000; //returned if no peak found
		int i; //lowest unchecked distance
		for(int c = 0; c < 72; c++) {

			i = sortArray[c]; //get rotationArray index in order of c

			if(rotationArray[i]>170) continue; 			// don't choose peak that's further away than 170 cm

			//###



			if(rotationArray[i]>50) {
				if(jumpFinder(rotationArray, 5, i)) {
					peak = i;
					break;
				}
			}
			else if(rotationArray[i]>30) {	//search above distance of 30 for peak because smaller distances are not as precise
				if(jumpFinder(rotationArray, 7, i)) {
					peak = i;
					break;
				}
			} else {
				if(jumpFinder(rotationArray, 8, i)) {
					peak = i;
					break;
				}
			}
		}
		return peak;
	}

	static int[] arraySort(int[] rotationArray) {
		int temp1;		//rotationArray temp
		int temp2;		//sortArray temp
		int[] rotationArray2 = rotationArray.clone(); 	//duplicate to sort distances in increasing order
		int[] sortArray = new int[72];			//array index links to corresponding (increasing) distances in rotation Array
		//e.g. biggest distance -> rotationArray[sortArray[0]]

		for(int i=0; i<sortArray.length; i++) {
			sortArray[i]=i;						//create increasingly ordered array -> ["0,0";"1,1";"2,2";... ] 
		}

		for(int i=1; i<sortArray.length; i++) {			//bubble sort - sorted after distances contained in rotationArray2
			for(int j=0; j<sortArray.length-i; j++) {
				if(rotationArray2[j]>rotationArray2[j+1]) {
					temp1=rotationArray2[j];
					temp2=sortArray[j];
					rotationArray2[j]=rotationArray2[j+1];
					sortArray[j]=sortArray[j+1];
					rotationArray2[j+1]=temp1;
					sortArray[j+1]=temp2;
				}

			}
		}
		return sortArray;
	}

	static boolean jumpFinder(int[] rotationArray, int angleSegment, int i) {

		boolean foundLeftJump = false;
		boolean foundRightJump = false;
		for(int k = -angleSegment+i; k < i; k++) {
			if(rotationArray[(k+72)%72] ==255 && rotationArray[(k+73)%72] >50) continue;	//don't use values for peak search if they equal 255 (255 has high error probability)
			//if(rotationArray[(k+72)%72] ==255 && rotationArray[(k+71)%72] != 255) continue; //don't use single 255 peak
			if(rotationArray[(k+72)%72] - rotationArray[(k+73)%72] > 15) {
				foundLeftJump = true;
				break;
			}
		}
		for(int k = i; k < angleSegment+i; k++) {
			if(rotationArray[k+1] ==255 && rotationArray[k] >50 ) continue;
			//if(rotationArray[(k+73)%72] ==255 && rotationArray[(k+74)%72] != 255) continue; //don't use single 255 peak
			if(rotationArray[k+1] - rotationArray[k] > 15) {
				foundRightJump = true;
				break;
			}
		}
		if(foundLeftJump && foundRightJump) {
			return true;
		} else return false;
	}
	
	static int edgeScan() {
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		int measured = 0;
		int rightEdge=-1;
		int value;
		do {
			value = 0;
			int numValidMeasurements = 0;
			for(int j = 0; j<5; j++) {
				measured = us.getDistance();
				Delay.msDelay(80);
				if (measured != 255) {
					value += measured;
					numValidMeasurements++;
				}
			}
			if(numValidMeasurements == 0) {
				value = measured;
			} else {
				value = (int) (value/numValidMeasurements);
			}
			rightEdge++;
			pilot.rotate(1);
			while(pilot.isMoving()) Thread.yield();
		}
		while(value < 30);
		pilot.rotate(-rightEdge);
		while(pilot.isMoving()) Thread.yield();
		
		measured = 0;
		int leftEdge=1;
		do {
			value = 0;
			int numValidMeasurements = 0;
			for(int j = 0; j<5; j++) {
				measured = us.getDistance();
				Delay.msDelay(80);
				if (measured != 255) {
					value += measured;
					numValidMeasurements++;
				}
			}
			if(numValidMeasurements == 0) {
				value = measured;
			} else {
				value = (int) (value/numValidMeasurements);
			}
			leftEdge--;
			pilot.rotate(-1);
			while(pilot.isMoving()) Thread.yield();
		}
		while(value < 30);
		pilot.rotate(-leftEdge);
		while(pilot.isMoving()) Thread.yield();
		return(leftEdge+rightEdge);
		
	}

	static int preciseScan() {
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		int measured=0;
		int min=1000;
		int peak = 0;
		for (int i=0; i < 21; i++) {
			int value=0;
			int numValidMeasurements = 0;
			for(int j = 0; j<5; j++) {
				measured = us.getDistance();
				Delay.msDelay(80);
				if (measured != 255) {
					value += measured;
					numValidMeasurements++;
				}
			}
			if(numValidMeasurements == 0) {
				value = measured;
			} else {
				value = (int) (value/numValidMeasurements);
			}
			if(min > value) {
				min = value;
				peak = i;
			}
			pilot.rotate(1);				// 5 degrees left
			while(pilot.isMoving()) Thread.yield();
		}
		pilot.rotate(-21);				// 5 degrees left
		while(pilot.isMoving()) Thread.yield();
		for (int i=0; i > -21; i--) {
			int value=0;
			int numValidMeasurements = 0;
			for(int j = 0; j<5; j++) {
				measured = us.getDistance();
				Delay.msDelay(80);
				if (measured != 255) {
					value += measured;
					numValidMeasurements++;
				}
			}
			if(numValidMeasurements == 0) {
				value = measured;
			} else {
				value = (int) (value/numValidMeasurements);
			}
			if(min > value) {
				min = value;
				peak = i;
			}
			pilot.rotate(-1);				// 5 degrees left
			while(pilot.isMoving()) Thread.yield();
		}
		pilot.rotate(21);				// 5 degrees left
		while(pilot.isMoving()) Thread.yield();
		System.out.println("         X " + min);
		System.out.println("         X " + peak);
		return peak;
	}

	static boolean drive(int distance) {
		line = false;
		LightSensor light = new LightSensor(SensorPort.S1);
		pilot.travel(distance, true);
		while(pilot.isMoving()) {
			if (light.getLightValue() > LightTest.line) {
				pilot.setAcceleration(1000);
				pilot.stop();
				while(pilot.isMoving()) Thread.yield();
				pilot.setAcceleration(50);
				line = true;
			}
			Delay.msDelay(10);			
		}
		return line;
	}

	static boolean rotate(int angle) {
		line = false;
		LightSensor light = new LightSensor(SensorPort.S1);
		pilot.rotate(angle, true);
		while(pilot.isMoving()) {
			if (light.getLightValue() > LightTest.line) {
				pilot.setAcceleration(1000);
				pilot.stop();
				while(pilot.isMoving()) Thread.yield();
				pilot.setAcceleration(50);
				line = true;
			}
			Delay.msDelay(10);			
		}
		return line;
	}

	public static void liftClaw() {
		Motor.C.rotate(20);
		Motor.C.rotate(-1);
		Motor.C.setStallThreshold(3, 100);
		while(!Motor.C.isStalled()) {		//open Claw
			Motor.C.rotate(-1);
		}
		Motor.C.rotate(+3);
		Motor.C.stop();
	}
    public static void liftClaw(int angle) {
        Motor.C.setSpeed(8);
        Motor.C.rotateTo(angle);
        Motor.C.stop();
    }
    
    public static void openClaw() {
		Motor.C.setSpeed(100);
		Motor.C.rotate(1);
		Motor.C.setStallThreshold(3, 100);
		while(!Motor.C.isStalled()) {		//open Claw
			Motor.C.rotate(1);
		}
		Motor.C.rotate(-3);
		Motor.C.stop();
    }

	public static void main(String[] args) {
		Delay.msDelay(10000);
		pilot.setAcceleration(50); //30 if floor slippery
		pilot.setRotateSpeed(20);
		LightTest.setPilot();
		Button.waitForAnyPress();
		LightTest.setLineValue();
		CommMaster nxt = new CommMaster();
		while (true) {
			liftClaw();
			mainAlgorithm(pilot); //searching pedestal and grabbing the can, then drive to the line
			LightTest.handleLine(true);
			turnOfUltrasonic();
			nxt.sendReady();		//1, steht an linie, klaue gebhoben
			
			nxt.waitForAnswer();	//a.a
			liftClaw(25);			// klaue wird in waagerechte gebracht
			
			nxt.waitForAnswer();	// b.b
			releaseCan();
			
			nxt.sendReady();		// 3, dose freigelassen, warten
			nxt.waitForAnswer();	//c.c
			Delay.msDelay(10000);
			returnToField();
		}		
	}

	public static void turnOfUltrasonic() {
		UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S4);
		ultra.off();
	}

	public static void returnToField() {
		pilot.rotate(-90);
		pilot.travel(150);
	}

	private static void releaseCan() {
		Motor.C.setSpeed(100);
		Motor.C.rotate(1);
		Motor.C.setStallThreshold(3, 100);
		while(!Motor.C.isStalled()) {		//open Claw
			Motor.C.rotate(1);
		}
		Motor.C.rotate(-3);
		Motor.C.stop();
	}

	public static void mainAlgorithm(DifferentialPilot pilot) {

		int peakRot;  //rotation direction of supposed can position
		int peakDist; //distance to supposed can position
		line = false;

		while(true){
			if (line) avoidLine();

			int[] rotationArray = rotateNscan(); //get array with distance values

			peakRot = findPeak(rotationArray);	//find angle in which the pedestal possible is (distance peak)	

			if (peakRot == -1000) {					//no peak found, error handling -> avoid obstacle (try again later)

				int angle = findMaxDistance(rotationArray); 
				int optimAngle; 

				if(angle < 36) {
					optimAngle = angle;		//rotate right
				} else {
					optimAngle = angle-72;	//rotate left
				}

				if(rotate(optimAngle*5)) {						//robot ended on line, undo rotation
					pilot.rotate(-pilot.getAngleIncrement());
					continue;
				}

				Sound.setVolume(100);
				Sound.playNote(Sound.FLUTE, 1500, 1000);
				Sound.playNote(Sound.FLUTE, 1500, 1000);
				if(rotationArray[angle]>65) {			//TODO comment
					drive(450);
				} else if(rotationArray[angle]>50) {	//TODO comment
					drive(300);
				} else {
					drive((rotationArray[angle]-15)*10);	//TODO comment
				}
				continue;						//start turning and measuring again
			}
			peakDist = rotationArray[peakRot];
			if(driveNGrabCan(peakRot, peakDist)) { //true if can found and grabbed
				findLine();
				return; 							//continue 
			}
		}
	}

	private static int[] rotateNscan() {
		Thread thread1 = new Thread() {		//thread for complete turn
			public void run() {
				pilot.rotate(360);
			}
		};
		thread1.start();

		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		int[] rotationArray = new int[81];

		for(int rotation = 0; rotation < 72; rotation++ ) {
			while (rotation*5 > pilot.getAngleIncrement()) { //every 5°
				Delay.msDelay(1);
			}
			rotationArray[rotation]=us.getDistance();		 //save distance
			//				Sound.playNote(Sound.FLUTE, 3000 - (20*rotationArray[rotation]), 10);
			System.out.println("          " + rotationArray[rotation]);

		}

		rotationArray[72]=rotationArray[0];		//let measurements overlap
		rotationArray[73]=rotationArray[1];
		rotationArray[74]=rotationArray[2];
		rotationArray[75]=rotationArray[3];
		rotationArray[76]=rotationArray[4];
		rotationArray[77]=rotationArray[5];
		rotationArray[78]=rotationArray[6]; 
		rotationArray[79]=rotationArray[7];
		rotationArray[80]=rotationArray[8];
		return rotationArray;
	}

	public static void avoidLine() {
		pilot.rotate(180);
		pilot.travel(500);
	}

	public static void findLine() {
		while(true) {
			int[] rotationArray = rotateNscan();
			int angle = findMaxDistance(rotationArray);
			int optimAngle;

			if(angle < 36) {
				optimAngle = angle;
			} else {
				optimAngle = angle-72;
			}
			if(rotate(optimAngle * 5)) return;
			if(rotationArray[angle]>65) {
				if(drive(450)) return;
			} else if(rotationArray[angle]>50) {
				if(drive(300)) return;
			} else {
				if(drive((rotationArray[angle]-15)*10)) return;
			}
		}
	}

	static boolean driveNGrabCan(int peakRot, int peakDist) {

		boolean grabbedCan = false;

		Sound.setVolume(80);
		if(peakRot >= 36) peakRot = peakRot - 72;	//turn max 180° 
		if(rotate(peakRot*5)){ //convert to degree and rotate
			pilot.rotate(-pilot.getAngleIncrement());
			return false;
		};					

		if(peakDist > 82) {							

			Sound.playNote(Sound.FLUTE, 300, 1000);
			System.out.println("         A " + peakDist);
			if(drive(300))return false;
		}
		else if(peakDist > 22) {

			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         B " + peakDist);
			if(drive((peakDist-20)*10))return false;
			//int peak = preciseScan();
			int peak = edgeScan();
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         C " + peakDist);
			rotate(peak);

			openClaw();
			
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
			while(us.getDistance()>7) {		//drive to can stopping 7 cm in front of u.s.sensor
				//System.out.println("         X " + us.getDistance());
				if(drive(10)) return false;
			}
			liftClaw();
			
			//			Delay.msDelay(4000);		//wait to ensure claw closure
			grabbedCan = true;			//can now grabbed
			Sound.playNote(Sound.FLUTE, 1500, 1000);
			System.out.println("         C " + peakDist);
			drive(-100);
		} else {
			//int peak = preciseScan();
			int peak = edgeScan();
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         C " + peakDist);
			rotate(peak);					// -6 due to high tech sensors

			openClaw();
			
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
			while(us.getDistance()>7) {		//drive to can stopping 7 cm in front of u.s.sensor
				//System.out.println("         X " + us.getDistance());
				if(drive(10)) return false;
			}
			if(drive(30)) return false;
			liftClaw();
			//			Delay.msDelay(4000);		//wait to ensure claw closure
			grabbedCan = true;			//can now grabbed
			Sound.playNote(Sound.FLUTE, 1500, 1000);
			System.out.println("         C " + peakDist);
			drive(-100);
		}

		while(pilot.isMoving())Thread.yield();
		return grabbedCan;

	}
}
