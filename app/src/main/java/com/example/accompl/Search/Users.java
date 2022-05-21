package com.example.accompl.Search;

public class Users {
    public String name;
    public String profilePic;
    public String uid;
    public String interests;

    public Users() {
    }

    public Users(String name, String profilePic, String uid, String interests) {
        this.name = name;
        this.profilePic = profilePic;
        this.uid = uid;
        this.interests = interests;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String UID) {
        this.uid = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
