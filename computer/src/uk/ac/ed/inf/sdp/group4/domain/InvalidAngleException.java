class InvalidAngleException extends Exception
{
	public InvalidAngleException(int angle)
	{
		super("Angle was not in range 0-359 (" + angle + ")");
	}
}
