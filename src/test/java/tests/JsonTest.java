package tests;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;
import resources.payloads.CoursePayload;

import java.util.List;
import java.util.Map;

public class JsonTest {
    @Test
    public void testAccessJson(){
        JsonPath jsonPath = new JsonPath(CoursePayload.CoursePrice());

        //get size of an array
        int numberOfCourses = jsonPath.getInt("courses.size()");
        Assert.assertEquals(numberOfCourses,4);

        //access child
        int purchaseAmount = jsonPath.getInt("dashboard.purchaseAmount");
        Assert.assertEquals(purchaseAmount,1162);

        //access an element of an array using index
        String title = jsonPath.getString("courses[0].title");
        Assert.assertEquals(title,"Selenium Python");

        //loop through an array to access value
        int price;
        for(int i=0;i<numberOfCourses;i++){
            title = jsonPath.getString("courses["+ i +"].title");
            price = jsonPath.getInt("courses["+ i +"].price");
            System.out.println(title + " course - price: $" + price);
        }

        //find an element in an array and access its attribute
        int copies = jsonPath.getInt("courses.find {it.title == 'RPA'}.copies");
        System.out.println("The RPA course has " + copies + " copies"); ;

        //find an element in an array and return a map
        Map<String, Object> course = jsonPath.getMap("courses.find {it.title == 'Cypress'}");
        System.out.println(course);

        //find an element in an array by multiple conditions
        List<Map<String, Object>> courses = jsonPath.getList("courses.findAll {it.title == 'Cypress' || it.price == 36}");
        for(Map<String, Object> c: courses){
            System.out.println("The course " + c.get("title") + " costs $" + c.get("price") + " and there is "+ c.get("copies")+" copies");
        }

        courses = jsonPath.getList("courses");
        int sum = 0;
        for(Map<String, Object> c: courses){
            sum += Integer.parseInt(c.get("price").toString());
        }
        Assert.assertEquals(sum,purchaseAmount);
    }

}
