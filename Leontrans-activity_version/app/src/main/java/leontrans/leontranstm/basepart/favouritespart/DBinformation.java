package leontrans.leontranstm.basepart.favouritespart;

public class DBinformation {
    private int id ;
    private int id_selected_item;

    public DBinformation(int id, int id_selected_item) {
        this.id = id;
        this.id_selected_item = id_selected_item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_selected_item() {
        return id_selected_item;
    }

    public void setId_selected_item(int id_selected_item) {
        this.id_selected_item = id_selected_item;
    }
}
