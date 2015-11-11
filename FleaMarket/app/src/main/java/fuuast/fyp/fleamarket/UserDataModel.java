package fuuast.fyp.fleamarket;

public class UserDataModel {

    private String name,email_id,phone,type,address,nic,org_name,org_cntct,org_typ;

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getOrg_cntct() {
        return org_cntct;
    }

    public void setOrg_cntct(String org_cntct) {
        this.org_cntct = org_cntct;
    }

    public String getOrg_typ() {
        return org_typ;
    }

    public void setOrg_typ(String org_typ) {
        this.org_typ = org_typ;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
