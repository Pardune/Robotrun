package robot;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

import java.lang.Math;

import communication.CommMaster;
import communication.CommSlave;

/**
 * Contains the main method for the can transportation of robot1
 * and all search and move methods except for line handling
 * All methods except for main, mainAlgorithm and driveNGrabCan are interchangeable between Main and Main2.
 * @author Birger Lüers, Tim Kohlmeier, Anton Komarov, Stefan Schuler
 * @version 2.5
 */
public class Main {

	static DifferentialPilot pilot = new DifferentialPilot(43.2f, 160f, Motor.A, Motor.B, false);
	static boolean line = false;

	/**
	 * Calculate the rotation of one biggest distance to ease can detection or get on line.
	 * 
	 * @param rotationArray	  72 sized array containing circular measured distances
	 * @return maxDistanceRot rotation to a maximal distance
	 */
	static int findMaxDistance(int[] rotationArray) {	
		int maxDistance = 0; 				  //one of the biggest sums of 9 neighboring distances
		int maxDistanceRot = 0; 			  //the corresponding rotation of the middle distance in maxDistance
		int start = (int) (Math.random()*72); //a random integer between 0 and 71 to avoid a robot movement loop
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

	/**
	 * Find a through in the rotationArray, that is not a wall.
	 * 
	 * @param rotationArray 72 sized array containing circular measured distances
	 * @return trough 		the rotation trough, -1000 if no trough found
	 **/
	static int findTrough(int[] rotationArray) {

		int[] sortArray = arraySort(rotationArray); //the indices of the rotationArray ordered by ascending distance
		int trough = -1000;
		int i;										//lowest unchecked distance
		for(int c = 0; c < 72; c++) {

			i = sortArray[c]; //get rotationArray index in order of c

			if(rotationArray[i]>170) continue; 	//don't choose trough further away than 170 cm

			if(rotationArray[i]>50) {
				if(jumpFinder(rotationArray, 5, i)) {
					trough = i;
					break;
				}
			}
			else if(rotationArray[i]>30) {
				if(jumpFinder(rotationArray, 7, i)) {
					trough = i;
					break;
				}
			} else {
				if(jumpFinder(rotationArray, 8, i)) {
					trough = i;
					break;
				}
			}
		}
		return trough;
	}
	/**
	 * Bubble sort the distances of the rotationArray to order the indices by ascending distance.
	 * 
	 * @param rotationArray 72 sized array containing circular measured distances
	 * @return arraySort	the indices of the rotationArray ordered by ascending distance
	 */
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

	/**
	 * Search for a descending jump of at least 16 cm in direction of the angleSegment rotation on both sides.
	 * 
	 * @param rotationArray 72 sized array containing circular measured distances
	 * @param angleSegment  the rotation segment of the roationArray, that is searched for jumps
	 * @param i				the width of search in each direction
	 * @return 				true if jumps on both side of the rotation segment
	 */
	static boolean jumpFinder(int[] rotationArray, int angleSegment, int i) {

		boolean foundLeftJump = false;
		boolean foundRightJump = false;
		for(int k = -angleSegment+i; k < i; k++) {
			if(rotationArray[(k+72)%72] ==255 && rotationArray[(k+73)%72] >50) continue;	//don't use values for trough search if they equal 255 (255 has high error probability)
			//if(rotationArray[(k+72)%72] ==255 && rotationArray[(k+71)%72] != 255) continue; //don't use single 255 trough
			if(rotationArray[(k+72)%72] - rotationArray[(k+73)%72] > 15) {
				foundLeftJump = true;
				break;
			}
		}
		for(int k = i; k < angleSegment+i; k++) {
			if(rotationArray[k+1] ==255 && rotationArray[k] >50 ) continue;
			//if(rotationArray[(k+73)%72] ==255 && rotationArray[(k+74)%72] != 255) continue; //don't use single 255 trough
			if(rotationArray[k+1] - rotationArray[k] > 15) {
				foundRightJump = true;
				break;
			}
		}
		if(foundLeftJump && foundRightJump) {
			return true;
		} else return false;
	}
	
	/**
	 * Scan for a distance of at least 30cm from the central rotation to the sides in an angle of 36 degrees on each side.
	 * 
	 * @return central angle in degrees in relation to both edges or -1000 if not both edges found
	 */
	static int edgeScan() {
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		int measured = 0;
		int rightEdge=-1;
		int value;
		do {
			if(rightEdge > 36) return -1000;
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
			if(rightEdge < -36) return -1000;
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

//	static int preciseScan() {
//		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
//		int measured=0;
//		int min=1000;
//		int trough = 0;
//		for (int i=0; i < 21; i++) {
//			int value=0;
//			int numValidMeasurements = 0;
//			for(int j = 0; j<5; j++) {
//				measured = us.getDistance();
//				Delay.msDelay(80);
//				if (measured != 255) {
//					value += measured;
//					numValidMeasurements++;
//				}
//			}
//			if(numValidMeasurements == 0) {
//				value = measured;
//			} else {
//				value = (int) (value/numValidMeasurements);
//			}
//			if(min > value) {
//				min = value;
//				trough = i;
//			}
//			pilot.rotate(1);				// 5 degrees left
//			while(pilot.isMoving()) Thread.yield();
//		}
//		pilot.rotate(-21);				// 5 degrees left
//		while(pilot.isMoving()) Thread.yield();
//		for (int i=0; i > -21; i--) {
//			int value=0;
//			int numValidMeasurements = 0;
//			for(int j = 0; j<5; j++) {
//				measured = us.getDistance();
//				Delay.msDelay(80);
//				if (measured != 255) {
//					value += measured;
//					numValidMeasurements++;
//				}
//			}
//			if(numValidMeasurements == 0) {
//				value = measured;
//			} else {
//				value = (int) (value/numValidMeasurements);
//			}
//			if(min > value) {
//				min = value;
//				trough = i;
//			}
//			pilot.rotate(-1);				// 5 degrees left
//			while(pilot.isMoving()) Thread.yield();
//		}
//		pilot.rotate(21);				// 5 degrees left
//		while(pilot.isMoving()) Thread.yield();
//		System.out.println("         X " + min);
//		System.out.println("         X " + trough);
//		return trough;
//	}

	/**
	 * Drive to distance in mm and stop fast if line detected.
	 * 
	 * @param distance  drive distance in mm
	 * @return line		true if line found
	 */
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

	/**
	 * Rotate to angle in degree and stop fast if line detected.
	 * 
	 * @param angle	rotation angle in degree
	 * @return line	true if line found
	 */
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

	/**
	 * Lift the claw till the motor stalls and reverse 3 degrees.
	 */
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
	
	/**
	 * Move the claw at a specified angle.
	 * 
	 * @param angle the angle of rotation
	 */
    public static void moveClaw(int angle) {
        Motor.C.setSpeed(8);
        Motor.C.rotate(angle);
        Motor.C.stop();
    }
    
    /**
     * Lower the claw till the motor stalls and reverse 3 degrees
     */
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
    
    /**
     * Set values and create CommMaster. Program cycle for robot1.
     * @param args
     */
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
			moveClaw(45);			// klaue wird in waagerechte gebracht
			
			nxt.waitForAnswer();	// b.b
			openClaw();
			
			nxt.sendReady();		// 3, dose freigelassen, warten
			
			Delay.msDelay(10000);
			returnToField();
			nxt.waitForAnswer();	//c.c
		}		
	}

	/**
	 * Turn the ultrasonic sensor off.
	 */
	public static void turnOfUltrasonic() {
		UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S4);
		ultra.off();
	}

	/**
	 * Robot is on Line and rotates 90 degrees to the right and travels 15cm.
	 */
	public static void returnToField() {
		pilot.rotate(-90);
		pilot.travel(150);
	}

	/**
	 * While can not found search for can. If no can found in measurement drive in direction of findMaxDistance().
	 * If can found execute driveNGrabCan() and if this grabs the can return to main method.
	 * 
	 * @param pilot the differential pilot for driving
	 */
	public static void mainAlgorithm(DifferentialPilot pilot) {

		int troughRot;  //rotation direction of supposed can position
		int troughDist; //distance to supposed can position
		line = false;

		while(true){
			if (line) avoidLine();

			int[] rotationArray = rotateNscan(); //get array with distance values

			troughRot = findTrough(rotationArray);	//find angle in which the pedestal possible is (distance trough)	

			if (troughRot == -1000) {					//no trough found, error handling -> avoid obstacle (try again later)

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
				if(rotationArray[angle]>65) {				//don't drive further than 45 cm to avoid collision
					drive(450);
				} else if(rotationArray[angle]>50) {		//if distance between 66 and 50 cm drive 30 cm
					drive(300);
				} else {
					drive((rotationArray[angle]-15)*10);	//if distance under 51 drive distance-15 cm
				}
				continue;						//start turning and measuring again
			}
			troughDist = rotationArray[troughRot];
			if(driveNGrabCan(troughRot, troughDist)) { //true if can found and grabbed
				findLine();
				return; 							//continue 
			}
		}
	}

	/**
	 * Rotate 360 degrees and measure the distance every 5 degrees.
	 * 
	 * @return roationArray	72 sized array containing circular measured distances
	 */
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
			while (rotation*5 > pilot.getAngleIncrement()) { //every 5 degrees
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

	/**
	 * Travel back 50 cm.
	 */
	public static void avoidLine() {
		pilot.rotate(180);
		pilot.travel(500);
	}

	/**
	 * Drive to one of the biggest distance till the line is found.
	 */
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

	/**
	 * Drive in the direction of the can if distance to can bigger than 22cm. If below 83cm execute
	 * edgeScan() and if this returns an angle different to -1000 approach can and grab it.
	 * 
	 * @param troughRot		the rotation through (that should be in the direction of the can)
	 * @param troughDist	the distance of the rotation through
	 * @return				true if can grabbed
	 */
	static boolean driveNGrabCan(int troughRot, int troughDist) {

		boolean grabbedCan = false;

		Sound.setVolume(80);
		if(troughRot >= 36) troughRot = troughRot - 72;	//turn max 180 degrees
		if(rotate(troughRot*5)){ //convert to degree and rotate
			pilot.rotate(-pilot.getAngleIncrement());
			return false;
		};					

		if(troughDist > 82) {							

			Sound.playNote(Sound.FLUTE, 300, 1000);
			System.out.println("         A " + troughDist);
			if(drive(300))return false;
		}
		else if(troughDist > 22) {

			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         B " + troughDist);
			if(drive((troughDist-20)*10))return false;
			//int trough = preciseScan();
			int trough = edgeScan();
			if(trough == -1000) return false;
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         C " + troughDist);
			rotate(trough);

			openClaw();
			
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
			while(us.getDistance()>7) {		//drive to can stopping 7 cm in front of u.s.sensor
				//System.out.println("         X " + us.getDistance());
				if(drive(10)) return false;
			}
			if(drive(50)) return false;
			liftClaw();
			
			//			Delay.msDelay(4000);		//wait to ensure claw closure
			grabbedCan = true;			//can now grabbed
			Sound.playNote(Sound.FLUTE, 1500, 1000);
			System.out.println("         C " + troughDist);
			drive(-100);
		} else {
			//int trough = preciseScan();
			int trough = edgeScan();
			if(trough == -1000) return false;
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         C " + troughDist);
			rotate(trough);					// -6 due to high tech sensors

			openClaw();
			
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
			while(us.getDistance()>7) {		//drive to can stopping 7 cm in front of u.s.sensor
				//System.out.println("         X " + us.getDistance());
				if(drive(10)) return false;
			}
			if(drive(50)) return false;
			liftClaw();
			//			Delay.msDelay(4000);		//wait to ensure claw closure
			grabbedCan = true;			//can now grabbed
			Sound.playNote(Sound.FLUTE, 1500, 1000);
			System.out.println("         C " + troughDist);
			drive(-100);
		}

		while(pilot.isMoving())Thread.yield();
		return grabbedCan;

	}
}
