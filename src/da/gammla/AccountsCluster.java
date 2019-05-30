package da.gammla;

import java.io.Serializable;
import java.util.ArrayList;

public class AccountsCluster implements Serializable {
    ArrayList<Account> contents = new ArrayList<Account>();

    public AccountsCluster(){

    }

    public void add(Account acc){
        contents.add(acc);
    }

    public void set(int i, Account acc){
        contents.set(i, acc);
    }

    public int size(){
        return contents.size();
    }

    public Account get(int i){
        return contents.get(i);
    }

    public void remove(int i){
        contents.remove(i);
    }
}
