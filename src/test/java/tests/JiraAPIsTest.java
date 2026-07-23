package tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import resources.payloads.JiraPayload;

import java.io.File;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.*;
import static resources.utils.AssertHelpers.*;

public class JiraAPIsTest {
    private static final String BASE_URI = "https://dungchungonline.atlassian.net";
    private static final String TOKEN = "Basic ZHVuZ2NodW5nb25saW5lQGdtYWlsLmNvbTpBVEFUVDN4RmZHRjB0Wl9uN2Vfajh4ck5qREF2UnNjVmpoaUlHLVp1VWpUdnlJQTR6bTNaYjFMWXctbHVMR3dtRUxObk5JdGMxa1plVlh5ZFl1YmdZRzB3NGpMZkphYVVUVGJGSmdVb2I4WVNaOXd0Rk40a2g2Q1JibjBIS1dNWlZmdWpkaUhtaGVidnpudEdFalRsZGVITjBjcVBpb2hGSV9veW9sOS1uME9iTTF3dkJEdE1BMEE9RUQyODYyOUE";
    private static final String CREATE_ISSUE_ENDPOINT = "/rest/api/3/issue/";
    private static final String ADD_ATTACHMENT_ENDPOINT = "/rest/api/3/issue/{issueId}/attachments";

    private static final RequestSpecification requestSpecification=
            new RequestSpecBuilder().setBaseUri(BASE_URI).addHeader("Authorization", TOKEN).build();
    private static final ResponseSpecification STATUS_CREATED = new ResponseSpecBuilder().expectStatusCode(201).build();
    private static final ResponseSpecification STATUS_OK = new ResponseSpecBuilder().expectStatusCode(200).build();

    private static final String ISSUE_SUMMARY = "RestAssured Test issue created at "+ LocalDateTime.now();
    private static final String FILE_PATH = System.getProperty("user.home") + "\\Desktop\\Untitled.png";

    @Test
    public void testAddingAttachmentAPI() {
        String issueId = createIssue();
        assertNonBlank(issueId, "Issue ID must be returned from create-issue API");

        addAttachment(issueId);
    }

    private String createIssue() {
        String response = given()
                .spec(requestSpecification)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .body(JiraPayload.createIssue(JiraAPIsTest.ISSUE_SUMMARY))
                .when()
                .post(CREATE_ISSUE_ENDPOINT)
                .then()
                .spec(STATUS_CREATED)
                .extract().response().asString();

        return JsonPath.from(response).getString("id");
    }

    private void addAttachment(String issueId){
        File file = new File(FILE_PATH);
        assertTrue(file.exists(), "File does not exist");

        given().spec(requestSpecification)
                .pathParam("issueId", issueId)
                .header("X-Atlassian-Token", "nocheck")
                .multiPart(file)
                .when()
                .post(ADD_ATTACHMENT_ENDPOINT)
                .then()
                .spec(STATUS_OK);
    }


}
