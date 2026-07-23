package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import resources.pojo.course.GetCourseDetailsResponse;

import static io.restassured.RestAssured.given;

public class CourseOAuthTest {
    private static final String BASE_URI = "https://rahulshettyacademy.com";
    private static final String AUTHORIZATION_ENDPOINT = "/oauthapi/oauth2/resourceOwner/token";
    private static final String GET_COURSE_DETAILS_ENDPOINT = "/oauthapi/getCourseDetails";

    private static final RequestSpecification BASE_REQUEST_SPEC = new RequestSpecBuilder().setBaseUri(BASE_URI).build();
    private static final ResponseSpecification STATUS_OK = new ResponseSpecBuilder().expectStatusCode(200).build();

    private static final String CLIENT_ID = "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "erZOWM9g3UtwNRj340YYaK_W";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String SCOPE = "scope";

    @Test
    public void testGetCourseDetailsWithOAuth(){
        //S1: get access token from oAuth API
        String token = login();
        assertNotNull(token, "Token is null");
        assertFalse(token.isEmpty(), "Token is empty");

        //S2: execute the getCourseDetails API
        getcourseDetails(token);
    }

    private String login(){
        String responseStr = given()
                .spec(BASE_REQUEST_SPEC)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("grant_type", GRANT_TYPE)
                .formParam("scope", SCOPE)
                .when().post(AUTHORIZATION_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .extract().response().asString();
        return JsonPath.from(responseStr).getString("access_token");
    }

    private void getcourseDetails(String token){
        //deserialize POJO the JSON response
        GetCourseDetailsResponse detailsObj = given()
                .spec(BASE_REQUEST_SPEC)
                .queryParam("access_token", token)
                .when().get(GET_COURSE_DETAILS_ENDPOINT)
                .then().log().all().extract().response()
                .as(GetCourseDetailsResponse.class);

        System.out.println("\n Using POJO class to deserialize the JSON response: \n Web Automation Courses:");
        detailsObj.getCourses().getWebAutomation().stream().filter(c -> c.getPrice() == 40)
                .forEach(c -> {
                    System.out.println(" - Title: "  + c.getCourseTitle() + " - Price: "  + c.getPrice());
                });
    }
}
