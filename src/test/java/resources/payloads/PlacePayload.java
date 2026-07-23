package resources.payloads;

import resources.pojo.map.Location;
import resources.pojo.map.CreatePlaceRequest;
import resources.pojo.map.UpdatePlaceRequest;

import java.util.ArrayList;

public class PlacePayload {
    public static String getAddPlaceJson(){
        return "{\n" +
                "  \"location\": {\n" +
                "    \"lat\": -38.383494,\n" +
                "    \"lng\": 33.427362\n" +
                "  },\n" +
                "  \"accuracy\": 50,\n" +
                "  \"name\": \"Frontline house\",\n" +
                "  \"phone_number\": \"(+91) 983 893 3937\",\n" +
                "  \"address\": \"29, side layout, cohen 09\",\n" +
                "  \"types\": [\n" +
                "    \"shoe park\",\n" +
                "    \"shop\"\n" +
                "  ],\n" +
                "  \"website\": \"http://google.com\",\n" +
                "  \"language\": \"French-IN\"\n" +
                "}\n";
    }

    public static CreatePlaceRequest getAddPlaceObject(){
        CreatePlaceRequest place = new CreatePlaceRequest();
        place.setAccuracy(50);
        place.setAddress("29, side layout, cohen 09");
        place.setLanguage("Vietnamese");
        place.setLocation(new Location(-38.383494, 33.427362));
        place.setName("Hanh");
        ArrayList<String> types= new ArrayList<>();
        types.add("shoe park");
        types.add("restaurant");
        place.setTypes(types);
        place.setWebsite("https://taolaomialao.com");
        place.setPhone_number("(+91) 983 893 3937");
        return place;
    }

    public static String getUpdatePlaceJson(String place_id, String address){
        return "{\n" +
                "\"place_id\":\""+ place_id +"\",\n" +
                "\"address\":\""+ address +"\",\n" +
                "\"key\":\"qaclick123\"\n" +
                "}\n";
    }

    public static UpdatePlaceRequest getUpdatePlaceObject(String place_id, String address){
        return new UpdatePlaceRequest(place_id, address, "qaclick123");
    }

}
