package tests;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.ResponseSpecification;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;
import resources.pojo.ecommerce.CreateOrderRequest;
import resources.pojo.ecommerce.CreateOrderResponse;
import resources.pojo.ecommerce.LoginRequest;
import resources.pojo.ecommerce.LoginResponse;
import resources.pojo.ecommerce.Order;

import java.io.File;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class EcommerceAPITest {
    private static final String BASE_URI = "https://rahulshettyacademy.com";
    private static final String LOGIN_ENDPOINT = "/api/ecom/auth/login";
    private static final String ADD_PRODUCT_ENDPOINT = "/api/ecom/product/add-product";
    private static final String CREATE_ORDER_ENDPOINT = "/api/ecom/order/create-order";
    private static final String DELETE_ORDER_ENDPOINT = "/api/ecom/order/delete-order/{orderId}";
    private static final String DELETE_PRODUCT_ENDPOINT = "/api/ecom/product/delete-product/{productID}";

    private static final String USER_EMAIL = System.getProperty("ecom.user.email", "dungchungonline@gmail.com");
    private static final String USER_PASSWORD = System.getProperty("ecom.user.password", "123qwe");
    private static final String PRODUCT_IMAGE_PATH = System.getProperty(
            "ecom.product.image.path",
            System.getProperty("user.home") + "\\Desktop\\Untitled.png"
    );

    private static final ResponseSpecification STATUS_OK = new ResponseSpecBuilder().expectStatusCode(200).build();
    private static final ResponseSpecification STATUS_CREATED = new ResponseSpecBuilder().expectStatusCode(201).build();

    @Test
    public void endToEndTest() {
        LoginResponse loginResponse = login(USER_EMAIL, USER_PASSWORD);
        assertNotNull(loginResponse.getToken(), "Login token must be returned");
        assertNotNull(loginResponse.getUserId(), "User ID must be returned");

        String productId = addProduct(loginResponse.getToken(), loginResponse.getUserId());
        assertNotNull(productId, "Product ID must be returned");
        assertFalse(productId.isBlank(), "Product ID must not be blank");

        String orderId = createOrder(loginResponse.getToken(), productId);
        assertNotNull(orderId, "Order ID must be returned");
        assertFalse(orderId.isBlank(), "Order ID must not be blank");

        deleteOrder(loginResponse.getToken(), orderId);
        deleteProduct(loginResponse.getToken(), productId);
    }

    private LoginResponse login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail(email);
        loginRequest.setUserPassword(password);

        return given()
                .spec(baseRequestSpec())
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .extract()
                .as(LoginResponse.class);
    }

    private String addProduct(String token, String userId) {
        File productImage = new File(PRODUCT_IMAGE_PATH);
        assertTrue(productImage.exists(), "Product image file does not exist: " + PRODUCT_IMAGE_PATH);

        String responseStr = given()
                .spec(authorizedRequestSpec(token))
                .formParam("productName", "Hanh Shirt")
                .formParam("productAddedBy", userId)
                .formParam("productCategory", "fashion")
                .formParam("productSubCategory", "shirts")
                .formParam("productPrice", 11500)
                .formParam("productDescription", "Adias Originals")
                .formParam("productFor", "women")
                .multiPart("productImage", productImage)
                .when()
                .post(ADD_PRODUCT_ENDPOINT)
                .then()
                .spec(STATUS_CREATED)
                .body("message", equalTo("Product Added Successfully"))
                .extract()
                .asString();

        return JsonPath.from(responseStr).getString("productId");
    }

    private String createOrder(String token, String productId) {
        Order order = new Order();
        order.setCountry("United States");
        order.setProductOrderedId(productId);

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setOrders(orders);

        CreateOrderResponse createOrderResponse = given()
                .spec(authorizedRequestSpec(token))
                .contentType(ContentType.JSON)
                .body(createOrderRequest)
                .when()
                .post(CREATE_ORDER_ENDPOINT)
                .then()
                .spec(STATUS_CREATED)
                .body("message", equalTo("Order Placed Successfully"))
                .extract()
                .as(CreateOrderResponse.class);

        return createOrderResponse.getOrders().get(0);
    }

    private void deleteOrder(String token, String orderId) {
        given()
                .spec(authorizedRequestSpec(token))
                .pathParam("orderId", orderId)
                .when()
                .delete(DELETE_ORDER_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .body("message", equalTo("Orders Deleted Successfully"));
    }

    private void deleteProduct(String token, String productId) {
        given()
                .spec(authorizedRequestSpec(token))
                .pathParam("productID", productId)
                .when()
                .delete(DELETE_PRODUCT_ENDPOINT)
                .then()
                .spec(STATUS_OK)
                .body("message", equalTo("Product Deleted Successfully"));
    }

    private RequestSpecification baseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .build();
    }

    private RequestSpecification authorizedRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addHeader("Authorization", token)
                .build();
    }
}
