package in.co.krishnaconsultancy.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class AccountGeneral {
    public static final String ACCOUNT_TYPE = "in.co.krishnaconsultancy.cookmymeal.recipe";
    public static final String ACCOUNT_NAME = "KrishnaConsultancy";
    public static final String USERDATA_USER_OBJ_ID = "userObjectId";
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Udinic account";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an KrishnaConsultancy account";
    public static final ServerAuthenticate AUTHENTICATOR = new ServerAuthenticate() {
		@Override
		public User userSignUp(String name, String email, String pass) throws Exception {
	        String url = "http://krishnaconsultancy.co.in/api/user.php";
	        
	        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
	        nameValuePair.add(new BasicNameValuePair("request", "signUp"));
	        nameValuePair.add(new BasicNameValuePair("service", "cookmymeal"));
	        nameValuePair.add(new BasicNameValuePair("name", name));
	        nameValuePair.add(new BasicNameValuePair("username", email));
	        nameValuePair.add(new BasicNameValuePair("password", pass));
	        
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	        
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        try {
	            HttpResponse response = httpClient.execute(httpPost);
	            String responseString = EntityUtils.toString(response.getEntity());
	            System.out.println("responce :- "+responseString);
	            if (response.getStatusLine().getStatusCode() != 201) {
	                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
	                throw new Exception("Error creating user[" + error.code + "] - " + error.error);
	            }
	            return new Gson().fromJson(responseString, User.class);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
		
		@Override
		public User userSignIn(String user, String pass) throws Exception {
	        String url = "http://krishnaconsultancy.co.in/api/user.php";
	        
	        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
	        nameValuePair.add(new BasicNameValuePair("request", "signIn"));
	        nameValuePair.add(new BasicNameValuePair("service", "cookmymeal"));
	        nameValuePair.add(new BasicNameValuePair("username", user));
	        nameValuePair.add(new BasicNameValuePair("password", pass));
	        
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
	        
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        try {
	            HttpResponse response = httpClient.execute(httpPost);
	            String responseString = EntityUtils.toString(response.getEntity());
	            System.out.println(responseString);
	            if (response.getStatusLine().getStatusCode() != 201) {
	                ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
	                throw new Exception("Error creating user[" + error.code + "] - " + error.error);
	            }
	            return new Gson().fromJson(responseString, User.class);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
		}
	};
}
