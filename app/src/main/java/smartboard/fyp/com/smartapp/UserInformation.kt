package smartboard.fyp.com.smartapp

class UserInformation {
    var name: String? = null
    var email: String? = null
    var passwaord: String? = null
    var profilepic: String? = null
    var systemid: String? = null
    var userid: String? = null

    constructor()
    constructor(
        name: String?,
        email: String?,
        passwaord: String?,
        profilepic: String?,
        systemid: String?,
        userid: String?
    ) {
        this.name = name
        this.email = email
        this.passwaord = passwaord
        this.profilepic = profilepic
        this.systemid = systemid
        this.userid = userid
    }
}