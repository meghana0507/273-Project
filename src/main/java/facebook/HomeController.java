package facebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.exceptions.OAuthSignatureException;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.io.IOException;
import java.util.*;


@Controller
public class HomeController {

    //Declaring the Variables
    private static final Token EMPTY_TOKEN = null;
    private static final String code = null;
    public static final String clientId = "*************";
    public static final String clientSecret = "*******************";
    public static final String STATE = "state";
    private String applicationHost = "http://localhost:8080";

    //Scribe Object
    private OAuthService oAuthService;

    //Creating Facebook Client Object
    FacebookClient fbClient = new DefaultFacebookClient(Version.VERSION_2_2);

    //Constructor
    public HomeController() {
        this.FacebookScribeAuthenticator(clientId, clientSecret, applicationHost);
    }

    //Initial Login Page
    @RequestMapping("/login/facebook")
    public String LoginPage(){
        return "login";
    }

    //Scribe Method For AccessTokn
    public void FacebookScribeAuthenticator (String clientId, String clientSecret, String applicationHost){
        this.applicationHost = applicationHost;
        this.oAuthService = buildoAuthService(clientId, clientSecret);
    }

    //OAuth Builder Service
    private OAuthService buildoAuthService(String clientId, String clientSecret) {
        return new ServiceBuilder()
                .apiKey(clientId)
                .apiSecret(clientSecret)
                .scope("user_photos")
                .scope("user_posts")
                .callback(applicationHost + "/auth/facebook/callback")
                .provider(FacebookApi.class)
                .build();
    }

    //================================================================
    //                  API to redirect to Facebook
    //================================================================
    @RequestMapping("/auth/facebook")
    public RedirectView startAuthentication(HttpSession session){
        String state = UUID.randomUUID().toString();
        session.setAttribute(STATE,state);
        String authorizationUrl =oAuthService.getAuthorizationUrl(EMPTY_TOKEN)+"&"+STATE+ "="+state;
        return new RedirectView(authorizationUrl);
    }

    //================================================================
    //               API to handle Callback from Facebook
    //================================================================
    @RequestMapping("/auth/facebook/callback")
    public String callback(@RequestParam("code") String code,@RequestParam(STATE) String state, HttpSession session) throws IOException {
        Token accessToken = getAccessToken(code);
        fbClient = new DefaultFacebookClient(accessToken.getToken(), Version.VERSION_2_2);
        return "logged-in";
    }

    //Method to take the accessToken provided by Facebook
    private Token getAccessToken(String code) {
        Verifier verifier = new Verifier(code);
        return oAuthService.getAccessToken(Token.empty(), verifier);
    }

    
	
	private HashMap<String, Long> scoreList = new HashMap<String, Long>();
    private TopFriendsService service;

    @Autowired
    Controller(TopFriendsService service){
        this.service = service;
    }

    // ------------------------------------------------------------------
    //                      API for TOP FRIENDS
    // ------------------------------------------------------------------
    @RequestMapping(value = "/{user-id}/topfriends", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List getTopFriends(@PathVariable("user-id") String id) {
        scoreList.clear();
        long scoreCounter;
        FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_2);
        User me;
        me = fbClient.fetchObject(id, com.restfb.types.User.class);
        PersonInfo object = new PersonInfo();
        object.setName(me.getName());
        object.setId(me.getId());


        JsonObject postConnection = fbClient.fetchObject("me/posts", JsonObject.class);
       JsonObject photosConnection = fbClient.fetchObject("me/photos", JsonObject.class);

        int postSize = postConnection.getJsonArray("data").length();
        int photoSize = photosConnection.getJsonArray("data").length();

        // ------------------------------------------------------------------
        //                              POSTS
        // ------------------------------------------------------------------

        for (int i = 0; i < postSize; i++) {
            PostAttributes obj = new PostAttributes();
            List<PersonInfo> nameLikes = new ArrayList<PersonInfo>();
            List<PersonInfo> nameComments = new ArrayList<PersonInfo>();
            List<PersonInfo> nameTags = new ArrayList<PersonInfo>();
             Set<PersonInfo> hs = new HashSet<>();

            //-------LIKES--------//
            if (postConnection.getJsonArray("data").getJsonObject(i).has("likes")) {

                JsonArray nameLikesJson = postConnection.getJsonArray("data").getJsonObject(i).getJsonObject("likes").getJsonArray("data");
                for (int n = 0; n < nameLikesJson.length(); n++) {
                    PersonInfo person = new PersonInfo();
                    String temp1 = nameLikesJson.getJsonObject(n).getString("name");
                    String temp2 = nameLikesJson.getJsonObject(n).getString("id");
                    person.setId(temp2);
                    person.setName(temp1);
                    nameLikes.add(person);
                }
            }


            for(PersonInfo personInfo: nameLikes){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x++;
                    scoreList.put(personInfo.getName(), x);
                }
                else {
                    scoreCounter = 0;
                    scoreCounter++;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }



            //----------------COMMENTS-------------//
            if (postConnection.getJsonArray("data").getJsonObject(i).has("comments")) {

                JsonArray nameCommentsJson = postConnection.getJsonArray("data").getJsonObject(i).getJsonObject("comments").getJsonArray("data");
                for (int c = 0; c < nameCommentsJson.length(); c++) {
                    JsonObject fromCommentsJson = nameCommentsJson.getJsonObject(c).getJsonObject("from");
                    String temp3 = fromCommentsJson.getString("name");
                    String temp4 = fromCommentsJson.getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp4);
                    person.setName(temp3);
                    nameComments.add(person);
                }
                hs.addAll(nameComments);
                nameComments.clear();
                nameComments.addAll(hs);
            }

            for(PersonInfo personInfo: nameComments){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x+=5;
                    scoreList.put(personInfo.getName(), x);
                }
                else
                {
                    scoreCounter = 0;
                    scoreCounter += 5;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }


            //---------TAGS-----------//
            if (postConnection.getJsonArray("data").getJsonObject(i).has("tags")) {

                JsonArray nameTagsJson = postConnection.getJsonArray("data").getJsonObject(i).getJsonObject("tags").getJsonArray("data");
                int k = 0;
                for (int c = 0; c < nameTagsJson.length(); c++) {
                    String temp5 = nameTagsJson.getJsonObject(c).getString("name");
                    String temp6 = nameTagsJson.getJsonObject(c).getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp6);
                    person.setName(temp5);
                    nameTags.add(person);
                    k++;
                }
                obj.setNumberOfTags(k);
            }

            if(obj.getNumberOfTags() < 5) {
                for (PersonInfo personInfo : nameTags) {
                    if(scoreList.containsKey(personInfo.getName())){
                        long x = scoreList.get(personInfo.getName());
                        x+=10;
                        scoreList.put(personInfo.getName(), x);
                    }
                    else
                    {
                        scoreCounter = 0;
                        scoreCounter += 10;
                        scoreList.put(personInfo.getName(), scoreCounter);
                    }
                }
            }
        }


        // ------------------------------------------------------------------
        //                              PHOTOS
        // ------------------------------------------------------------------

        for (int i = 0; i < photoSize; i++) {

            PhotoAttributes obj = new PhotoAttributes();
            List<PersonInfo> nameLikes = new ArrayList<PersonInfo>();
            List<PersonInfo> nameComments = new ArrayList<PersonInfo>();
            Set<PersonInfo> hs = new HashSet<>();
            List<PersonInfo> nameTags = new ArrayList<PersonInfo>();
            List<PersonInfo> photoOwner = new ArrayList<PersonInfo>();

            //-------LIKES--------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("likes")) {
                JsonArray nameLikesJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("likes").getJsonArray("data");
                for (int n = 0; n < nameLikesJson.length(); n++) {
                    PersonInfo person = new PersonInfo();
                    String temp1 = nameLikesJson.getJsonObject(n).getString("name");
                    String temp2 = nameLikesJson.getJsonObject(n).getString("id");
                    person.setId(temp2);
                    person.setName(temp1);
                    nameLikes.add(person);
                }
            }

            for(PersonInfo personInfo: nameLikes){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x++;
                    scoreList.put(personInfo.getName(), x);
                }
                else {
                    scoreCounter = 0;
                    scoreCounter++;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }

            //----------------COMMENTS-------------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("comments")) {

                JsonArray nameCommentsJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("comments").getJsonArray("data");
                for (int c = 0; c < nameCommentsJson.length(); c++) {
                    JsonObject fromCommentsJson = nameCommentsJson.getJsonObject(c).getJsonObject("from");
                    String temp3 = fromCommentsJson.getString("name");
                    String temp4 = fromCommentsJson.getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp4);
                    person.setName(temp3);
                    nameComments.add(person);
                 }

                hs.addAll(nameComments);
                nameComments.clear();
                nameComments.addAll(hs);
            }

            for(PersonInfo personInfo: nameComments){
                if(scoreList.containsKey(personInfo.getName())){
                    long x = scoreList.get(personInfo.getName());
                    x++;
                    scoreList.put(personInfo.getName(), x);
                }
                else
                {
                    scoreCounter = 0;
                    scoreCounter++;
                    scoreList.put(personInfo.getName(), scoreCounter);
                }
            }

            //---------TAGS-----------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("tags")) {
                JsonArray nameTagsJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("tags").getJsonArray("data");
                int k = 0;

                for (int c = 0; c < nameTagsJson.length(); c++) {
                    String temp6 =" ";
                    if(nameTagsJson.getJsonObject(c).has("id")) {
                        temp6 = nameTagsJson.getJsonObject(c).getString("id");
                    }

                    String temp5 = nameTagsJson.getJsonObject(c).getString("name");

                    PersonInfo person = new PersonInfo();
                    person.setId(temp6);
                    person.setName(temp5);
                    nameTags.add(person);
                    k++;
                }
                obj.setNumberOfTags(k);
            }

            if(obj.getNumberOfTags() < 4) {
                for (PersonInfo personInfo : nameTags) {
                    if(scoreList.containsKey(personInfo.getName())){
                        long x = scoreList.get(personInfo.getName());
                        x++;
                        scoreList.put(personInfo.getName(), x);
                    }
                    else
                    {
                        scoreCounter = 0;
                        scoreCounter++;
                        scoreList.put(personInfo.getName(), scoreCounter);
                    }
                }
            }

            //-------------PhotoOwner------------//
            JsonObject photoOwnerJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("from");
            String temp7 = photoOwnerJson.getString("name");
            String temp8 = photoOwnerJson.getString("id")  ;
            PersonInfo person = new PersonInfo();
            person.setId(temp8);
            person.setName(temp7);
            photoOwner.add(person);

            if(scoreList.containsKey(person.getName())){
                long x = scoreList.get(person.getName());
                x++;
                scoreList.put(person.getName(), x);
            }
            else
            {
                scoreCounter = 0;
                scoreCounter++;
                scoreList.put(person.getName(), scoreCounter);
            }
        }
   
		scoreList.remove(object.getName());
        sortedScores ob = new sortedScores(scoreList);
        List sortedList = new ArrayList<>();
        Map<String, Long> sortedMap = ob.sortByValues(scoreList);



        int cnt = 1;
        sb.append("Your Top 5 Friends are - ");
        sb.append("\n");
        sb.append("\n");
        for (Map.Entry<String, Long> entry : sortedMap.entrySet()) {

            sb.append(cnt+".  "+entry.getKey());
            sb.append("\n");
            sortedList.add(entry.getKey());
            cnt++;

            if(cnt == 6){
                break;
            }
        }

		sendEmail("deeptrivedi6803@gmail.com",sb);

        return sortedList;
    }
	
	public void sendEmail(String recipient, StringBuilder sb) {
        // Construct an object to contain the recipient address.
        //Destination destination = new Destination().withToAddresses(new String[]{recipient});
        Destination destination = new Destination().withToAddresses(recipient);
        // Create the subject and body of the message.
        Content subject = new Content().withData(SUBJECT);



        Content textBody = new Content().withData(sb.toString());
        Body body = new Body().withText(textBody);

        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);

        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(FROM).withDestination(destination).withMessage(message);

        try {
            System.out.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");

            // Instantiate an Amazon SES client, which will make the service call. The service call requires your AWS credentials.
            // Because we're not providing an argument when instantiating the client, the SDK will attempt to find your AWS credentials
            // using the default credential provider chain. The first place the chain looks for the credentials is in environment variables
            // AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
            // For more information, see http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

            // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your sandbox
            // status, sending limits, and Amazon SES identity-related settings are specific to a given AWS
            // region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
            // the US West (Oregon) region. Examples of other regions that Amazon SES supports are US_EAST_1
            // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
            Region REGION = Region.getRegion(Regions.US_EAST_1);
            client.setRegion(REGION);

            // Send the email.
            client.sendEmail(request);
            System.out.println("Email sent!");
        } catch(Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }

    }
    }	
}