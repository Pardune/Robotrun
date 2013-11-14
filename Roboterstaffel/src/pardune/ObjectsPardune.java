package pardune;

import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class ObjectsPardune {

	int prevVal, curVal;
	boolean foundPodest;
	DifferentialPilot pilot;
	UltrasonicSensor ultrasonicSensor;

	public ObjectsPardune(){
		// diff Pilot
		pilot = new DifferentialPilot(43.2f, 161f, Motor.A, Motor.B, false);
		pilot.setAcceleration(40);
		pilot.setTravelSpeed(40);
		pilot.setRotateSpeed(80);

		// bools
		foundPodest = false;

		// run the actual program
		findPodest();
	}

	private void findPodest() {
		int[] measurements = new int[72];
		int[] differenceBetweenVals = new int[71];	// 0 is between 0 and 1 of measurement-Array, 5 between 5 and 6...
		// measure, compare difference
			// if: corner(pos to neg): continue
			// else: retreive dose (pos to neg to pos)

		// put first measurement value, therfor the for-loop wont change
		measurements[0] = ultrasonicSensor.getDistance();
		// fill measurments via loop
		for (int i=1; i < 72; i++){
			measurements[i] = ultrasonicSensor.getDistance();
			differenceBetweenVals[i] = measurements[i-1] - measurements[i];
			pilot.rotate(5);				// 5 degrees left
			Delay.msDelay(80);				// since ultrasonic sensor needs 75ms between readings
		}

		// all measurements done, evaluate differences
		boolean distanceIncreasing = (differenceBetweenVals[0] >= 0) ? true : false ;
		if (distanceIncreasing){
			// search first negative increase in distance
			for(int diff : differenceBetweenVals){				// for each int in array, save value in diff
				if (diff < 0){
					System.out.println("feature at: ");
				}
			}
		} else {
			// search first positive increase in distance
			for(int diff : differenceBetweenVals){
				if (diff >= 0){
					System.out.println("feature at: ");
				}
			}
		}	// if ende
		
	}
}