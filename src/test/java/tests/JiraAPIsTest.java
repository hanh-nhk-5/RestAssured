package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;
import resources.payloads.JiraPayload;

import java.io.File;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.*;

public class JiraAPIsTest {
    @Test
    public void testAddingAttachmentAPI() {
        RestAssured.baseURI = "https://dungchungonline.atlassian.net";
        String response = given().header("Authorization", "Basic ZHVuZ2NodW5nb25saW5lQGdtYWlsLmNvbTpBVEFUVDN4RmZHRjB0Wl9uN2Vfajh4ck5qREF2UnNjVmpoaUlHLVp1VWpUdnlJQTR6bTNaYjFMWXctbHVMR3dtRUxObk5JdGMxa1plVlh5ZFl1YmdZRzB3NGpMZkphYVVUVGJGSmdVb2I4WVNaOXd0Rk40a2g2Q1JibjBIS1dNWlZmdWpkaUhtaGVidnpudEdFalRsZGVITjBjcVBpb2hGSV9veW9sOS1uME9iTTF3dkJEdE1BMEE9RUQyODYyOUE=")
                                .header("Content-Type", "application/json")
                                .header("accept", "application/json")
                                .body(JiraPayload.createIssue("Bug is created from RestAssured " + LocalDateTime.now()))
                            .when().post("/rest/api/3/issue/")
                            .then().assertThat().statusCode(201).extract().response().asString();

        JsonPath js = new JsonPath(response);
        String issueId= js.getString("id");

        given().pathParams("issueId", issueId)
                .header("Authorization", "Basic ZHVuZ2NodW5nb25saW5lQGdtYWlsLmNvbTpBVEFUVDN4RmZHRjB0Wl9uN2Vfajh4ck5qREF2UnNjVmpoaUlHLVp1VWpUdnlJQTR6bTNaYjFMWXctbHVMR3dtRUxObk5JdGMxa1plVlh5ZFl1YmdZRzB3NGpMZkphYVVUVGJGSmdVb2I4WVNaOXd0Rk40a2g2Q1JibjBIS1dNWlZmdWpkaUhtaGVidnpudEdFalRsZGVITjBjcVBpb2hGSV9veW9sOS1uME9iTTF3dkJEdE1BMEE9RUQyODYyOUE=")
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(new File("\\Users\\HanhNHK\\Desktop\\Untitled.png"))
                .when().post("/rest/api/3/issue/{issueId}/attachments")
                .then().assertThat().statusCode(200);//.extract().response().asString();

    }
}
