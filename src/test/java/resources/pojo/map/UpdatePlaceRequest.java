package resources.pojo.map;

public class UpdatePlaceRequest {
    private String place_id;
    private String address;
    private String key;

    public UpdatePlaceRequest(String place_id, String address, String key) {
        this.place_id = place_id;
        this.address = address;
        this.key = key;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
