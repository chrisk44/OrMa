import java.util.ArrayList;

public class BillingAccount {
    private static ArrayList<BillingAccount> allAccounts = new ArrayList<>();
    private long id;
    private String name;
    private double balance;
    
    public BillingAccount(long id, String name, double balance){
        this.id = id;
        this.name = name;
        this.balance = balance;

        allAccounts.add(this);
    }

    void charge(double amount){
        balance += amount;

        // Save to database
    }
    String getName(){ return this.name; }               // TODO: Add to CD
    double getBalance(){ return this.balance; }         // TODO: Add to CD

    static BillingAccount getAccountById(long id){
        // THIS IS A BAD IMPLEMENTATION but I don't care

        for(BillingAccount ba : allAccounts){
            if(ba.id == id){
                return ba;
            }
        }

        return null;
    }
}
