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
	private int measured;
	private int[] measurements;
	private int[] differenceBetweenVals;
	private int i;

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
		measurements = new int[72];
		differenceBetweenVals = new int[71];	// 0 is between 0 and 1 of measurement-Array, 5 between 5 and 6...
		// measure, compare difference
		// if: corner(pos to neg): continue
		// else: retreive dose (pos to neg to pos)

		// put first measurement value, therfor the for-loop wont change
		measurements[0] = ultrasonicSensor.getDistance();
		// fill measurments and diff-array via loop
		int numValidMeasurements = 1;
		for (int i=0; i < 72; i++) {
			for(int j = 0; j<5; j++) {
				measured = ultrasonicSensor.getDistance();
				if (measured != 255) {
					measured += measured;
					numValidMeasurements++;
					Delay.msDelay(80);
				}
			}
			measurements[i] = (int) (measured/numValidMeasurements);
			pilot.rotate(5);				// 5 degrees left
		}

		// process diff-Array
		for (int i = 0; i<72;i++){
			differenceBetweenVals[i]= measurements[i] - measurements[i+1];
		}

		// print diff-array
		for (int i = 0; i<72;i++){
			if((i+1)%10 == 0){					// every tenth value, enter new line
				System.out.println();
			}
			System.out.print(differenceBetweenVals[i]+ ",");
		}

		// all measurements done, evaluate differences
		boolean distanceIncreasing = (differenceBetweenVals[0] >= 0) ? true : false ;
		if (distanceIncreasing){
			// search first increase in distance
			for(i = 0; i<differenceBetweenVals.length; i++){	// for each int in array, save value in diff
				if (differenceBetweenVals[i] < 0 && differenceBetweenVals[i] > 15){
					System.out.println("feature at: " +i);
				}
			}
		} else {
			// search first decrease in distance
			for(i = 0; i<differenceBetweenVals.length; i++){
				if (diff >= 0 && differenceBetweenVals[i] > 15){
					System.out.println("feature at: " + i);
				}
			}
		}	// if ende
		
		// rotiere zum peak
		if(i>36){
			pilot.rotate((72-i)*5);
		} else{
			pilot.rotate(i*5);
		}
		
		// gib distanz aus
		System.out.println("distance to feature: " + measurements[i]);
	}
}