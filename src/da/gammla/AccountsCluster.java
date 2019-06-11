package da.gammla;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class AccountsCluster implements Serializable {
    public ArrayList<Account> contents = new ArrayList<Account>();

    public AccountsCluster(){

    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);
        out.flush();
        return bos.toByteArray();
    }

    public boolean contains(Account acc){
        for (Account test_acc:contents) {
            if (test_acc.equals(acc))
                return true;
        }
        return false;
    }
}
