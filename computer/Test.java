public class Test
{
	public static void main(String[] args) throws InterruptedException
	{
		FatController fatty = new FatController();
		fatty.drivef(40);
		Thread.sleep(5000);
		fatty.driveb(20);
		Thread.sleep(5000);
		fatty.drivef(20);
		Thread.sleep(5000);
		fatty.shoot();
		Thread.sleep(5000);
		fatty.finish();
	}
}
