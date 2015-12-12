package fuuast.fyp.fleamarket;

public class UserDataModelSingleTon {

    private String id,name,email_id,phone,type,address,nic,org_name,org_contact,org_typ;

    static UserDataModelSingleTon obj = new UserDataModelSingleTon();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
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

    public String getOrg_contact() {
        return org_contact;
    }

    public void setOrg_contact(String org_contact) {
        this.org_contact = org_contact;
    }

    public String getOrg_typ() {
        return org_typ;
    }

    public void setOrg_typ(String org_typ) {
        this.org_typ = org_typ;
    }

    public static UserDataModelSingleTon getInstance() {
        if (obj == null) {
            obj = new UserDataModelSingleTon();
        }
        return obj;
    }
}
