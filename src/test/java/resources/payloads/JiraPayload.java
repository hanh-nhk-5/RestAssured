package resources.payloads;

public class JiraPayload {
    public static String createIssue(String summary){
        return "{\n" +
                "    \"fields\": {\n" +
                "       \"project\":\n" +
                "       {\n" +
                "          \"key\": \"HS\"\n" +
                "       },\n" +
                "       \"summary\": \""+summary+"\",       \n" +
                "       \"issuetype\": {\n" +
                "          \"name\": \"Bug\"\n" +
                "       }\n" +
                "   }\n" +
                "}";
    }
}
