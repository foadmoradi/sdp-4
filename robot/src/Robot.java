import java.lang.Math;

import lejos.nxt.*;
import lejos.robotics.navigation.Pilot;
import lejos.robotics.navigation.TachoPilot;

/**
 * This class
 *
 * @author Chris Brown <cb@tardis.ed.ac.uk>
 */
public class Robot
{
	private Motor LEFT_MOTOR = Motor.A;
	private Motor RIGHT_MOTOR = Motor.B;

	private Motor SHOOT_MOTOR = Motor.C;
	private int SHOOT_ANGLE = 90;

	private float WHEEL_DIAMETER = 7.5f;
	private float TRACK_WIDTH = 13.0f;

	private Pilot pilot;
	private Thread kicker;

	public Robot()
	{
		pilot = new TachoPilot(WHEEL_DIAMETER, TRACK_WIDTH, LEFT_MOTOR, RIGHT_MOTOR, true);

		// Set faster motor speeds.
		Motor.A.setSpeed(900);
		Motor.B.setSpeed(900);
		Motor.C.setSpeed(900);

		kicker = new Thread(new ShootThread());
	}

	public boolean isMoving()
	{
		return pilot.isMoving();
	}

	public void drive(float distance)
	{
		this.drive(distance, true);
	}

	public void drive(float distance, boolean instant)
	{
		pilot.travel((float)(distance), instant);
	}

	public void stop()
	{
		pilot.stop();
	}

	public void hold()
	{
		this.hold(100);
	}

	public void hold(int power)
	{
		LEFT_MOTOR.lock(power);
		RIGHT_MOTOR.lock(power);
	}

	public void shoot()
	{
		kicker.start();
		try
		{
			kicker.join();
		}
		catch (InterruptedException e)
		{
		}
		kicker = new Thread(new ShootThread());
	}

	public void left(int degrees)
	{
		pilot.rotate((-1 * degrees), false);
	}

	public void right(int degrees)
	{
		pilot.rotate(degrees, false);
	}

	// The code following this line is a complete travesty. I'm not even
	// going to give it the honor of some documentation.
	public void singHappyBirthday()
	{
		try
		{
			playNote(392, 500);
			playNote(392, 200);
			playNote(440, 500);
			playNote(392, 500);
			playNote(523, 500);
			playNote(493, 500);
			Thread.sleep(500);
			playNote(392, 500);
			playNote(392, 200);
			playNote(440, 500);
			playNote(392, 200);
			playNote(587, 500);
			playNote(523, 500);
			Thread.sleep(500);
			playNote(392, 500);
			playNote(392, 200);
			playNote(784, 500);
			playNote(659, 500);
			playNote(523, 200);
			playNote(523, 200);
			playNote(493, 500);
			playNote(440, 500);
			Thread.sleep(500);
			playNote(698, 500);
			playNote(698, 500);
			playNote(659, 500);
			playNote(523, 500);
			playNote(587, 500);
			playNote(523, 500);
		}
		catch (InterruptedException e) {}
	}

	private void playNote(int frequency, int length) throws InterruptedException
	{
		Sound.playTone(frequency, length);
		Thread.sleep(length);
	}

	private class ShootThread implements Runnable
	{
		public void run()
		{
			try
			{
				SHOOT_MOTOR.forward();
				Thread.sleep(1);
				SHOOT_MOTOR.backward();
				Thread.sleep(80);
				SHOOT_MOTOR.forward();
				Thread.sleep(100);
				SHOOT_MOTOR.stop();
			}
			catch (Exception e)
			{
				Log.e("Interrupted Thread!");
			}
		}
	}
}
