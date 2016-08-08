package ly.generalassemb.prep;

/**
 * Created by aaronfields on 8/7/16.
 */
public class User {

    private String email;
    private String displayName;
    private String UID;

    public User(String email, String displayName, String UID){
        this.email = email;
        this.displayName = displayName;
        this.UID = UID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

}
