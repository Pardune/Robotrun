package Controller;

import java.util.*;
import robot.Robot;

public class RobotController implements EventListener {
	private Robot[] robots;
	/**
	 * @param args
	 */
	public RobotController(){
		robots[0] = new Robot();
		robots[1] = new Robot();
		robots[0].addEventListener(this);
		robots[1].addEventListener(this);
		
		init();
	}
	
	private void init() {
		// let robots search for podests
		
		// somehow (?) evaluate which robot gets the Can initially
		
		// assign roles to Robots (fetch the can / get in position to receive the can)
		// start communication between Robots
		
		
	}	

}
