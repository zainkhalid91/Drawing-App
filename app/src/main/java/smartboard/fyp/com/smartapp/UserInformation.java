package smartboard.fyp.com.smartapp;

public class UserInformation {
    String name, email, passwaord, profilepic, systemid, userid;


    public UserInformation() {


    }

    public UserInformation(String name, String email, String passwaord, String profilepic, String systemid, String userid) {
        this.name = name;
        this.email = email;
        this.passwaord = passwaord;
        this.profilepic = profilepic;
        this.systemid = systemid;
        this.userid = userid;
    }


    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswaord() {
        return passwaord;
    }

    public void setPasswaord(String passwaord) {
        this.passwaord = passwaord;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

}
