package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;
import resources.payloads.PlacePayload;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MapTest {

    @Test
    public void testMapAPI(){
        //S1: add a place into the map and get place_id from the api response using JsonPath
        RestAssured.baseURI= "https://rahulshettyacademy.com";
        String response= given().log().all().queryParam("key", "qaclick123").body(PlacePayload.getAddPlaceJson())
                        .when().post("/maps/api/place/add/json")
                        .then().assertThat().statusCode(200).extract().response().body().asString();
        JsonPath jsonPath = new JsonPath(response);
        String place_id = jsonPath.get("place_id");

        //S2: update address of the place
        String expectedAddress= "123 somewhere, TX";
        given().log().all().queryParam("key", "qaclick123").queryParam("place_id", place_id)
                .body(PlacePayload.getUpdatePlaceJson(place_id, expectedAddress))
                .when().put("/maps/api/place/update/json")
                .then().assertThat().statusCode(200).body("msg", equalTo("Address successfully updated"));

        //S3: call getPlace API and verify whether the address is updated
        given().log().all().queryParam("key", "qaclick123").queryParam("place_id", place_id)
                .when().get("/maps/api/place/get/json")
                .then().assertThat().statusCode(200).body("address", equalTo(expectedAddress));

    }
}
