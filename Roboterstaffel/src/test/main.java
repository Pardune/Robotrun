package test;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class main {

	/**
	 * @param args
	 */
	static int findMaxDistance(int[] rotationArray) {
		int maxDistance = 0;
		int maxDistanceRot = 0;
		for(int i = 0; i < 72; i++) {
			int newDistance = rotationArray[i] + rotationArray[i+1] + rotationArray[i+2] + rotationArray[i+3]
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
		int peak = -1000;
		for(int i = 0; i < 72; i++) {
			if(rotationArray[i+2]<26) { //small distance -> pedestal at least 3 times in array
				if (rotationArray[i]+20 < rotationArray[i+2] && rotationArray[i+1]+20 < rotationArray[i+2]
						&& rotationArray[i+2] <= rotationArray[i+3] && rotationArray[i+3] >= rotationArray[i+4]
							&& rotationArray[i+4] > rotationArray[i+5]+20 && rotationArray[i+4] > rotationArray[i+6]+20) {
					peak = i+3;
					break;
				}
			}
			if (rotationArray[i]+15 < rotationArray[i+2] && rotationArray[i+1]+15 < rotationArray[i+2]
					&& rotationArray[i+2] > rotationArray[i+3]+15 && rotationArray[i+2] > rotationArray[i+4]+15) {
				peak = i+2;
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
				rotationArray[78]=rotationArray[6];
				rotationArray[79]=rotationArray[7];
				
				int rotate = findMaxDistance(rotationArray)*5;
				pilot.rotate(rotate);
				while(pilot.isMoving())Thread.yield();
				pilot.travel(300);
				while(pilot.isMoving())Thread.yield();
				continue;							//start turning and measuring again
			}
			peakDist = rotationArray[peakRot];
			if(driveNGrabCan(pilot, peakRot, peakDist)) {
				break; //can found and grabbed
			}
		}
	}

	static boolean driveNGrabCan(DifferentialPilot pilot, int peakRot, int peakDist) {

		boolean grabbedCan = false;

		Sound.setVolume(100);
		if(peakRot >= 36) peakRot = peakRot - 72;		
		pilot.rotate(peakRot*5);
		while(pilot.isMoving())Thread.yield();
		if(peakDist > 31) {
			pilot.travel(80);
			Sound.playNote(Sound.FLUTE, 500, 1000);
		} else if(peakDist > 26){
			pilot.travel(30);
			Sound.playNote(Sound.FLUTE, 1000, 1000);
		} else {						//can near enough to be grabbed
			Motor.C.setSpeed(8);
			Motor.C.forward();
			pilot.travel((peakDist - 10)*10);
			while(pilot.isMoving())Thread.yield();
			Motor.C.backward();
			Delay.msDelay(2000);
			grabbedCan = true;
			Sound.playNote(Sound.FLUTE, 1500, 1000);
		}
		while(pilot.isMoving())Thread.yield();
		return grabbedCan;
		
	}
}
