package test;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class main {
	
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
		if(maxDistanceRot < 36) {
			return maxDistanceRot;
		} else return maxDistanceRot - 72;
		
		
	}
	
	static int findPeak(int[] rotationArray) {
		int temp1;		//rotationArray temp
		int temp2;		//sortArray temp
		int[] rotationArray2 = rotationArray; 	//duplicate to sort distances in increasing order
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
			
			//if(rotationArray[i+2]<13) { //small distance -> pedestal at least 3 times in array
//				if (rotationArray[i]+15 < rotationArray[i+2] && rotationArray[i+1]+15 < rotationArray[i+2]
//						&& rotationArray[i+2] <= rotationArray[i+3] && rotationArray[i+3] >= rotationArray[i+4]
//							&& rotationArray[i+4] > rotationArray[i+5]+15 && rotationArray[i+4] > rotationArray[i+6]+15) {
//					peak = i+3;
//					break;
//				}
			//}
			if(rotationArray[i]>80) continue; 			// don't choose peak that's further away than 80 cm
			
			int extender = 0;			//Array ends at 0 -> for case 0 and 1:
			if(i==0) extender = 72;		//use these if "exceptions"				because [i-1] && [i-2] would cause errors
			if(i==1) extender = 70;
				
			
			int currentCenter = (int) ((rotationArray[i-1 + extender] + rotationArray[i] + rotationArray[i+1])/3);
			if (rotationArray[i-2 +extender]+15 < currentCenter && currentCenter > rotationArray[i+2]+15) {
				peak = i;
				break;
			}
		}
		return peak;
	}	
		
	
	public static void main(String[] args) {
		
		Button.waitForAnyPress();
		
		final DifferentialPilot pilot = new DifferentialPilot(43.2f, 160f, Motor.A, Motor.B, false); //161f if floor slippery
		pilot.setAcceleration(50); //30 if floor slippery
		int peakRot;  //rotation direction of supposed can position
		int peakDist; //distance to supposed can position
		while(true){
			Thread thread1 = new Thread() {		//thread for complete turn
				public void run() {
					pilot.rotate(360);
				}
			};
			thread1.start();
				UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
				int[] rotationArray = new int[80];

				for(int rotation = 0; rotation < 72; rotation++ ) {
					while (rotation*5 > pilot.getAngleIncrement()) { //every 5°
						Delay.msDelay(10);
					}
					rotationArray[rotation]=us.getDistance();		 //save distance
				}
			
			rotationArray[72]=rotationArray[0];		//let measurements overlap
			rotationArray[73]=rotationArray[1];
			rotationArray[74]=rotationArray[2];
			rotationArray[75]=rotationArray[3];
			rotationArray[76]=rotationArray[4];
			rotationArray[77]=rotationArray[5];
			
			peakRot = findPeak(rotationArray);
			
			if(peakRot == -1000) {					//no peak found
				rotationArray[78]=rotationArray[6]; //findMaxDistance needs more overlapping distances
				rotationArray[79]=rotationArray[7];
				
				int rotate = findMaxDistance(rotationArray)*5;
				pilot.rotate(rotate);
				while(pilot.isMoving())Thread.yield(); //wait till DifPilot has finished all tasks
				pilot.travel(300);
				while(pilot.isMoving())Thread.yield();
				continue;							//start turning and measuring again
			}
			peakDist = rotationArray[peakRot];
			if(driveNGrabCan(pilot, peakRot, peakDist)) { //true when can found and grabbed
				break; 									  //stop for now -> for complete program add function here
			}
		}
	}

	static boolean driveNGrabCan(DifferentialPilot pilot, int peakRot, int peakDist) {

		boolean grabbedCan = false;

		Sound.setVolume(100);
		if(peakRot >= 36) peakRot = peakRot - 72;	//turn max 180° 
		pilot.rotate(peakRot*5);					//convert to degree and rotate
		while(pilot.isMoving())Thread.yield();
		
		if(peakDist > 25) {
			pilot.travel(120);
			Sound.playNote(Sound.FLUTE, 500, 1000);
			Sound.playNote(Sound.FLUTE, 500, 1000);
		} else if(peakDist > 20){
			pilot.travel(30);
			Sound.playNote(Sound.FLUTE, 1000, 1000);
		} else {						//can near enough to be grabbed
			Motor.C.setSpeed(8);
			Motor.C.forward();			//open claw
			Delay.msDelay(4000);
			pilot.travel((peakDist - 7)*10);	//drive to can stopping 7 cm in front of u.s.sensor
			while(pilot.isMoving())Thread.yield();
			Motor.C.backward();			//close claw
			Delay.msDelay(4000);		//wait to ensure claw closure
			grabbedCan = true;			//can now grabbed
			Sound.playNote(Sound.FLUTE, 1500, 1000);
		}
		while(pilot.isMoving())Thread.yield();
		Delay.msDelay(2000);
		return grabbedCan;
		
	}
}
