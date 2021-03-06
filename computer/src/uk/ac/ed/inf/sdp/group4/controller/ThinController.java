package uk.ac.ed.inf.sdp.group4.controller;

import uk.ac.ed.inf.sdp.group4.domain.Position;
import uk.ac.ed.inf.sdp.group4.sim.Action;
import uk.ac.ed.inf.sdp.group4.sim.SimBot;

public class ThinController extends Controller
{
	private SimBot bot;

	public void setBot(SimBot bot)
	{
		this.bot = bot;
	}

	public void driveForward(int val)
	{
		System.out.println("Driving forward:" + val);
		bot.newAction(new Action(Action.Type.FORWARD, val));
	}

	public void driveBackward(int val)
	{
		System.out.println("Driving backward:" + val);
		bot.newAction(new Action(Action.Type.REVERSE, val));
	}

	public void shoot()
	{
		System.out.println("Shooting!");
		bot.shoot(0);
	}

	public void beserk(boolean val)
	{
		System.out.println("BESERK:" + val);
	}

	public void turnRight(int angle)
	{
		System.out.println("Turning right");
		bot.newAction(new Action(Action.Type.RIGHT, angle));
	}

	public void turnLeft(int angle)
	{
		System.out.println("Turning left");
		bot.newAction(new Action(Action.Type.LEFT, angle));
	}

	public void setSpeed(int val)
	{

	}

	public void steer(int val)
	{

	}

	public void stop()
	{

	}

	public void finish()
	{
		//tell sim robot to turn off
	}

	public void sendCommand(int command, int argument)
	{
		if (command == 0)
		{
			driveForward(argument);
		}
		else if (command == 1)
		{
			driveBackward(argument);
		}
		else if (command == 2)
		{
			shoot();
		}
		else if (command == 4)
		{
			turnLeft(argument);
		}
		else if (command == 5)
		{
			turnRight(argument);
		}
	}

	@Override
	public void driveForward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void driveBackward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLeftMotorSpeed(int speed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRightMotorSpeed(int speed) {
		// TODO Auto-generated method stub
		
	}
}
