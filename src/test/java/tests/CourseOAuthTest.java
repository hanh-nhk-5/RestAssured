package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import resources.pojo.course.GetCourseDetailsResponse;

import static io.restassured.RestAssured.given;

public class CourseOAuthTest {
    @Test
    public void testGetCourseDetailsWithOAuth(){
        //S1: get access token from oAuth API
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        String responseStr = given().formParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                            .formParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
                            .formParam("grant_type", "client_credentials")
                            .formParam("scope", "scope")
                            .when().post("/oauthapi/oauth2/resourceOwner/token")
                            .then().assertThat().statusCode(200).extract().response().asString();
        JsonPath js= new  JsonPath(responseStr);

        //S2: execute the getCourseDetails API using the access token and POJO to deserialize the JSON response
        Response response = given().queryParam("access_token", js.getString("access_token"))
                .when().get("/oauthapi/getCourseDetails");

        GetCourseDetailsResponse detailsObj = response.as(GetCourseDetailsResponse.class);
        System.out.println("\n Using POJO class to deserialize the JSON response: \n Web Automation Courses:");
        detailsObj.getCourses().getWebAutomation().stream().filter(c -> c.getPrice() == 40)
                .forEach(c -> {
                    System.out.println(" - Title: "  + c.getCourseTitle() + " - Price: "  + c.getPrice());
                });

        responseStr = response.asString();
        System.out.println("\n getCourseDetails response string: " + responseStr);


    }
}
