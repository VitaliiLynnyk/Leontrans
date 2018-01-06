package leontrans.leontranstm.basepart.cardpart;

public class AdvertisementOwnerInfo {
    private String telephone;
    private String person_type;
    private String full_name;

    public AdvertisementOwnerInfo(String telephone, String person_type, String full_name) {
        this.telephone = telephone;
        this.person_type = person_type;
        this.full_name = full_name;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getPerson_type() {
        return person_type;
    }

    public String getFull_name() {
        return full_name;
    }
}
