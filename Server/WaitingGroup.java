import java.util.ArrayList;

public class WaitingGroup {
	static final int TABLE_SLEEP_TIME_MS = 1000;
	private String name;
	private int number_of_people;
	private int priority;
	static ArrayList<WaitingGroup> waitingList = new ArrayList<>();
	
	public WaitingGroup(String n, int number_of_people, int priority){
		this.name = n;
		this.number_of_people = number_of_people;
		this.priority = priority;

	}

	public String toString(){
		return "(WaitingGroup: " + name + ", people=" + number_of_people + ", priority=" + priority + ")";
	}

	public int getNumOfPeople(){ return number_of_people; }
	public void remove(){
		waitingList.remove(this);
	}
	public void notifyWhenAvailable(){

		// Create new thread to wait for new Table
		new Thread(()->{
            Table t = Table.findFreeTable(WaitingGroup.this);
            while (t == null){
                // Sleep for TABLE_SLEEP_TIME_MS milliseconds
                try{
                    Thread.sleep(TABLE_SLEEP_TIME_MS);
                }catch(InterruptedException ignored){}

                // Check for free table
                t = Table.findFreeTable(WaitingGroup.this);
            }

            // Notify all PRs
            for(PR pr : PR.allPRs)
                new TableFreeNotification(t, pr, WaitingGroup.this).show();
        }).start();

	}

	static void addToList(WaitingGroup wg){
		waitingList.add(wg);
	}
}
