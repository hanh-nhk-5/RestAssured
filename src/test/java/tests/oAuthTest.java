package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class oAuthTest {
    @Test
    public void testGetCourseDetailsWithOAuth(){
        //S1: get access token from oAuth API
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        String response = given().formParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                            .formParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
                            .formParam("grant_type", "client_credentials")
                            .formParam("scope", "scope")
                            .when().post("/oauthapi/oauth2/resourceOwner/token")
                            .then().assertThat().statusCode(200).extract().response().asString();
        System.out.println(response);
        JsonPath js= new  JsonPath(response);

        //S2: use the access token to execute the getCourseDetails API
        response = given().queryParam("access_token", js.getString("access_token"))
                .when().get("/oauthapi/getCourseDetails").asString();
        System.out.println("getCourseDetails: " + response);


    }
}
