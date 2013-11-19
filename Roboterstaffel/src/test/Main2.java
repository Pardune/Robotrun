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

public class Main2 {
	
	static DifferentialPilot pilot = new DifferentialPilot(43.2f, 160f, Motor.A, Motor.B, false);
	static boolean line = false;
	
	//Ich hoffe man kann meine Kommentare verstehen, leider funktioniert der Müll noch nicht so recht...
	/**
	 * @param args
	 */
	static int findMaxDistance(int[] rotationArray) {	//drive to biggest distance, makes collisions unlikely but still possible
		int maxDistance = 0;							//robot should drive into the center to ease can detection -> line detection has to be implemented
		int maxDistanceRot = 0;
		for(int i = 0; i < 72; i++) {
			int newDistance = rotationArray[i] + rotationArray[i+1] + rotationArray[i+2] + rotationArray[i+3]		//possibly to many values -> wrong directions & higher collision danger?
					+ rotationArray[i+4] + rotationArray[i+5] + rotationArray[i+6]+ rotationArray[i+7] + rotationArray[i+8];
			if(newDistance > maxDistance) {
				maxDistance = newDistance;
				maxDistanceRot = i+4;
			}
		}
		System.out.println("          " + rotationArray[maxDistanceRot]);
		
		return maxDistanceRot;
		
	}
	
	static int findPeak(int[] rotationArray) {
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
			
		int peak = -1000; //returned if no peak found
		int i; //lowest unchecked distance
for(int c = 0; c < 72; c++) {
			
			i = sortArray[c]; //get rotationArray index in order of c
			
			if(rotationArray[i]>70) continue; 			// don't choose peak that's further away than 80 cm
			
			//###
			
			boolean foundLeftJump = false;
			boolean foundRightJump = false;
			
			if(rotationArray[i]>50) {
				for(int k = -5+i; k < i; k++) {
					if(rotationArray[(k+72)%72] >230 || rotationArray[(k+73)%72] >230 ) continue;
					if(rotationArray[(k+72)%72] - rotationArray[(k+73)%72] > 20) {
						foundLeftJump = true;
						break;
					}
				}
				for(int k = i; k < 5+i; k++) {
					if(rotationArray[(k+72)%72] >230 || rotationArray[(k+73)%72] >230 ) continue;
					if(rotationArray[k+1] - rotationArray[k] > 20) {
						foundRightJump = true;
						break;
					}
				}
			}
			else if(rotationArray[i]>30) {
				for(int k = -7+i; k < i; k++) {
					if(rotationArray[(k+72)%72] >230 || rotationArray[(k+73)%72] >230 ) continue;
					if(rotationArray[(k+72)%72] - rotationArray[(k+73)%72] > 20) {
						foundLeftJump = true;
						break;
					}
				}
				for(int k = i; k < 7+i; k++) {
					if(rotationArray[(k+72)%72] >230 || rotationArray[(k+73)%72] >230 ) continue;
					if(rotationArray[k+1] - rotationArray[k] > 20) {
						foundRightJump = true;
						break;
					}
				}
			} else {
				for(int k = -8+i; k < i; k++) {
					if(rotationArray[(k+72)%72] >230 || rotationArray[(k+73)%72] >230 ) continue;
					if(rotationArray[(k+72)%72] - rotationArray[(k+73)%72] > 20) {
						foundLeftJump = true;
						break;
					}
				}
				for(int k = i; k < 8+i; k++) {
					if(rotationArray[(k+72)%72] >230 || rotationArray[(k+73)%72] >230 ) continue;
					if(rotationArray[k+1] - rotationArray[k] > 20) {
						foundRightJump = true;
						break;
					}
				}
			}
			
			if(foundLeftJump && foundRightJump) {
				peak = i;
				break;
			}
			
			
			
			
			//if(rotationArray[i+2]<13) { //small distance -> pedestal at least 3 times in array
//				if (rotationArray[i]+15 < rotationArray[i+2] && rotationArray[i+1]+15 < rotationArray[i+2]
//						&& rotationArray[i+2] <= rotationArray[i+3] && rotationArray[i+3] >= rotationArray[i+4]
//							&& rotationArray[i+4] > rotationArray[i+5]+15 && rotationArray[i+4] > rotationArray[i+6]+15) {
//					peak = i+3;
//					break;
//				}
			//}

			
		
//			int currentCenter = (int) ((rotationArray[i-1 + extender] + rotationArray[i] + rotationArray[i+1])/3);
//			if (rotationArray[i-2 +extender]+15 > currentCenter && currentCenter < rotationArray[i+2]+15) {
//				peak = i;
//				break;
//			}
			
//			if (rotationArray[i-2 %72]+20 > rotationArray[i] && rotationArray[i-1 %72] > rotationArray[i]
//					&& rotationArray[i] < rotationArray[i+1] && rotationArray[i] < rotationArray[i+2]+20) {
//				peak = i;
//				break;
//			}
		}
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
		Motor.C.setSpeed(20);
		Motor.C.setStallThreshold(2, 100);
		Motor.C.rotateTo(-90);
		while(!Motor.C.isStalled()){
			Delay.msDelay(10);
		}
		Motor.C.stop();
	}
	
	public static void main(String[] args) {
		pilot.setAcceleration(50); //30 if floor slippery
		pilot.setRotateSpeed(60);
		LightTest.setPilot();
		Button.waitForAnyPress();
		LightTest.setLineValue();
		CommSlave nxt = new CommSlave();

		while (true) {
			turnOfUltrasonic();
			liftClaw();
			nxt.waitForAnswer();
			
			findLine();
			LightTest.handleLine(true);
			grabCan();
			nxt.sendReady();
			returnToField();
			mainAlgorithm(pilot);
			nxt.sendReady();
		}		
	}
	public static void grabCan() {
		Motor.C.setSpeed(8);
		Motor.C.rotateTo(50);			//open claw
		Delay.msDelay(4000);
		LightTest.followLine(false);
		pilot.travel(150);
		Motor.C.rotateTo(-25);			//close claw
		Motor.C.setSpeed(20);
		Motor.C.setStallThreshold(2, 100);
		Motor.C.rotateTo(-90);
		while(!Motor.C.isStalled()){
			Delay.msDelay(10);
		}
		Motor.C.stop();
	}
	
	public static void turnOfUltrasonic() {
		UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S4);
		ultra.off();
	}
	
	public static void returnToField() {
		pilot.travel(-100);
		pilot.rotate(-90);
		pilot.travel(100);
	}
		
	
	private static void releaseCan() {
		Motor.C.setSpeed(20);
		Motor.C.setStallThreshold(2, 100);
		Motor.C.rotateTo(90);
		while(!Motor.C.isStalled()){
			Delay.msDelay(10);
		}
		Motor.C.stop();
	}

	public static void mainAlgorithm(DifferentialPilot pilot) {
		
		int peakRot;  //rotation direction of supposed can position
		int peakDist; //distance to supposed can position
		
		while(true){
			if(line) {
				foundLine();
			}
			int[] rotationArray = rotateNscan();
			
			peakRot = findPeak(rotationArray);
			
			if(peakRot == -1000) {					//no peak found

				int angle = findMaxDistance(rotationArray);
				int optimAngle;

				if(angle < 36) {
					optimAngle = angle;
				} else {
					optimAngle = angle-72;
				}
				if(rotate(optimAngle*5)) {
					pilot.rotate(-pilot.getAngleIncrement());
					continue;
				}
				Sound.setVolume(100);
				Sound.playNote(Sound.FLUTE, 1500, 1000);
				Sound.playNote(Sound.FLUTE, 1500, 1000);
				if(rotationArray[angle]>65) {			
					drive(450);
				} else if(rotationArray[angle]>50) {
					drive(300);
				} else {
					drive((rotationArray[angle]-15)*10);
				}
				continue;							//start turning and measuring again
			}
			peakDist = rotationArray[peakRot];
			if(driveNGrabCan(peakRot, peakDist)) { //true when can found and grabbed
				return; 									  //stop for now -> for complete program add function here
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


	public static void foundLine() {
		pilot.rotate(180);
		pilot.travel(500);
	}
	
	public static void findLine() {
		
		while(true) {
			int [] rotationArray = rotateNscan();
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
		else if(peakDist > 45) {

			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			System.out.println("         A " + peakDist);
			if(drive((peakDist-20)*10))return false;
		} else if(peakDist > 20){
			Sound.playNote(Sound.FLUTE, 1000, 1000);
			System.out.println("         B " + peakDist);
			if(drive((peakDist - 15)*10))return false;
		} else {						//can near enough to be grabbed
			
			Motor.C.setSpeed(8);
			Delay.msDelay(4000);
			drive((peakDist - 6)*10);	//drive to can stopping 10 cm in front of u.s.sensor
			Motor.C.rotateTo(50);			//open claw
			drive(-100);
			Motor.C.rotateTo(-25);			//close claw
			Motor.C.setSpeed(20);
			Motor.C.setStallThreshold(2, 100);
			Motor.C.rotateTo(-90);
			while(!Motor.C.isStalled()){
				Delay.msDelay(10);
			}
			Motor.C.stop();
//			Delay.msDelay(4000);		//wait to ensure claw closure
			grabbedCan = true;			//can now grabbed
			Sound.playNote(Sound.FLUTE, 1500, 1000);
			System.out.println("         C " + peakDist);
		}
		while(pilot.isMoving())Thread.yield();
		return grabbedCan;
		
	}
}
