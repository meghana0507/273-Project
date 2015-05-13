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

    //================================================================
    //                      API for Photos
    //================================================================

    @RequestMapping(value = "/{user-id}/photos", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    UserPhotos getPhotos(@PathVariable("user-id") String id) {

        User me;
        me = fbClient.fetchObject(id, com.restfb.types.User.class);
        //PhotoAttributes object = new PhotoAttributes();
        UserPhotos object = new UserPhotos();
        //set name and id
        object.setName(me.getName());
        object.setId(me.getId());


        JsonObject photosConnection = fbClient.fetchObject("me/photos", JsonObject.class);
        ArrayList<PhotoAttributes> myPhotos = new ArrayList<PhotoAttributes>();
        int size = photosConnection.getJsonArray("data").length();

        //-------LIKES--------//
        for (int i = 0; i < size; i++) {

            PhotoAttributes obj = new PhotoAttributes();
            String firstPhotoUrl = photosConnection.getJsonArray("data").getJsonObject(i).getString("source");
            obj.setPhotoUrl(firstPhotoUrl);
            List<PersonInfo> nameLikes = new ArrayList<PersonInfo>();
            List<PersonInfo> nameComments = new ArrayList<PersonInfo>();
            List<PersonInfo> nameTags = new ArrayList<PersonInfo>();
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("likes")) {

                JsonArray nameLikesJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("likes").getJsonArray("data");
                int m = 0;

                for (int n = 0; n < nameLikesJson.length(); n++) {
                    PersonInfo person = new PersonInfo();
                    String temp1 = nameLikesJson.getJsonObject(n).getString("name");
                    String temp2 = nameLikesJson.getJsonObject(n).getString("id");
                    person.setId(temp2);
                    person.setName(temp1);
                    nameLikes.add(person);
                    m++;
                }
                obj.setLikes(nameLikes);
                obj.setNumberOfLikes(m);
            }

            //----------------COMMENTS-------------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("comments")) {

                JsonArray nameCommentsJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("comments").getJsonArray("data");
                int k = 0;
                for (int c = 0; c < nameCommentsJson.length(); c++) {
                    JsonObject fromCommentsJson = nameCommentsJson.getJsonObject(c).getJsonObject("from");
                    String temp3 = fromCommentsJson.getString("name");

                    String temp4 = fromCommentsJson.getString("id");
                    PersonInfo person = new PersonInfo();
                    person.setId(temp4);
                    person.setName(temp3);
                    nameComments.add(person);
                    k++;
                }

                obj.setComments(nameComments);
                obj.setNumberOfComments(k);

            }
            //---------TAGS-----------//
            if (photosConnection.getJsonArray("data").getJsonObject(i).has("tags")) {

                JsonArray nameTagsJson = photosConnection.getJsonArray("data").getJsonObject(i).getJsonObject("tags").getJsonArray("data");
                int k = 0;
                for (int c = 0; c < nameTagsJson.length(); c++) {
                    String temp6 = " ";
                    if (nameTagsJson.getJsonObject(c).has("id")) {
                        temp6 = nameTagsJson.getJsonObject(c).getString("id");
                    }
                    String temp5 = nameTagsJson.getJsonObject(c).getString("name");


                    PersonInfo person = new PersonInfo();
                    person.setId(temp6);
                    person.setName(temp5);
                    nameTags.add(person);
                    k++;
                }

                obj.setTags(nameTags);
                obj.setNumberOfTags(k);

            }

            myPhotos.add(obj);
        }

        object.setPhotos(myPhotos);

        photoRepo.save(object);
        return object;
    }

    //================================================================
    //                      API for Posts
    //================================================================
    @RequestMapping(value = "{user-id}/photos", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    UserPhotos getPhotos(@PathVariable("user-id") String id) throws JsonProcessingException {

    }
}