public class WaitingGroup {

	private String name;
	private int number_of_people;
	private int priority;
	static List<WaitingGroup> waitingList;
	
	public WaitingGroup(String n, int number_of_people, int priority)
	{
		this.name = n;
		this.number_of_people = number_of_people;
		this.priority = priority;

	}


	static void addToList(WaitingGroup wg)
	{
		waitingList.add(wg);
	}
	

	public void notifyWhenAvailable()
	{
		//estw kainourgio thread
		Table t = findFreeTable(wg);
		while (t == null)
		{
			t = findFreeTable();
		}
		
		TableFreeNotification tn = new TableFreeNotification(t,  ,wg); 
		tn.show();
	}

	public void remove()
	{
		waitingList.remove(this);
		
	}
}
