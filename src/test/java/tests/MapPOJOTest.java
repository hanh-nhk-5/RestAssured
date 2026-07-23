package tests;

import io.restassured.http.ContentType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.Test;
import resources.payloads.PlacePayload;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static resources.utils.AssertHelpers.assertNonBlank;

public class MapPOJOTest {
    private static final String BASE_URI = "https://rahulshettyacademy.com";
    private static final String GET_PLACE_ENDPOINT = "/maps/api/place/get/json";
    private static final String ADD_PLACE_ENDPOINT = "/maps/api/place/add/json";
    private static final String UPDATE_PLACE_ENDPOINT = "/maps/api/place/update/json";

    private static final String KEY = "qaclick123";
    private static final String EXPECTED_ADDRESS = "123 somewhere, TX";

    private static final RequestSpecification BASE_REQUEST_SPEC =
            new RequestSpecBuilder().setBaseUri(BASE_URI).addQueryParam("key", KEY).build();
    private static final ResponseSpecification STATUS_OK = new ResponseSpecBuilder().expectStatusCode(200).build();

    @Test
    public void testMapAPI_POJO() {
        String placeId = addPlace();
        assertNonBlank(placeId, "Place ID must be returned from add-place API");

        updatePlace(placeId, EXPECTED_ADDRESS);

        String actualAddress = getPlaceAddress(placeId);
        assertEquals(actualAddress, EXPECTED_ADDRESS, "Address should match the updated value");
    }

    private String addPlace() {
        String response = given()
                .spec(BASE_REQUEST_SPEC)
                .contentType(ContentType.JSON)
                .body(PlacePayload.getAddPlaceObject())
                .when()
                .post(ADD_PLACE_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .extract()
                .asString();

        return JsonPath.from(response).getString("place_id");
    }

    private void updatePlace(String placeId, String expectedAddress) {
        given()
                .spec(BASE_REQUEST_SPEC)
                .queryParam("place_id", placeId)
                .contentType(ContentType.JSON)
                .body(PlacePayload.getUpdatePlaceObject(placeId, expectedAddress))
                .when()
                .put(UPDATE_PLACE_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .body("msg", equalTo("Address successfully updated"));
    }

    private String getPlaceAddress(String placeId) {
        return given()
                .spec(BASE_REQUEST_SPEC)
                .queryParam("place_id", placeId)
                .when()
                .get(GET_PLACE_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .extract()
                .jsonPath()
                .getString("address");
    }
}
