package algofocus.algofocusdemo.Activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.Serializable;

import algofocus.algofocusdemo.Model.UserInfo;
import algofocus.algofocusdemo.R;

public class LogInActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    LoginButton loginButton;
    FacebookCallback<LoginResult> callback;
    public static UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        //printHashKey(getApplicationContext());
        initialize();
    }

    //Generating Hash Key
    /*public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("KEY", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }*/

    public void initialize(){
        loginButton=(LoginButton)findViewById(R.id.login_button);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();
        userInfo=new UserInfo();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                UserData(currentProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult!=null) {
                    Log.d("TEST", "On Success");
                    AccessToken accessToken = loginResult.getAccessToken();
                    Profile profile = Profile.getCurrentProfile();

                    Log.d("TEST", "After OnSuccess");
                }
            }

            @Override
            public void onCancel() {
                Log.d("TEST", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TEST", "onError"+error.toString());
            }
        };
        //loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager,callback);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TEST","onActivityResult");
        callbackManager.onActivityResult(requestCode,resultCode,data);
        Log.d("TEST","After onActivityResult :"+data);

        Log.d("TEST","..........> :"+userInfo.getName()+" "+userInfo.getProfilePicURL());

        Intent i=new Intent(getApplicationContext(),UserDetailsActivity.class);
        i.putExtra("user_id",userInfo.getId());
        i.putExtra("user_name",userInfo.getName());
        i.putExtra("first_name",userInfo.getFirstName());
        i.putExtra("mid_name",userInfo.getMiddleName());
        i.putExtra("lst_name",userInfo.getLastName());
        i.putExtra("lnk_uri",userInfo.getLinkURI());
        i.putExtra("pro_url",userInfo.getProfilePicURL());


        startActivity(i);
        finish();


    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Profile profile= Profile.getCurrentProfile();
        UserData(profile);
    }

    public void UserData(Profile profile){
        if (profile!=null){

            //UserInfo userInfo=new UserInfo();

            //Setting All User Data
            userInfo.setId(profile.getId());
            userInfo.setName(profile.getName());
            userInfo.setFirstName(profile.getFirstName());
            userInfo.setMiddleName(profile.getMiddleName());
            userInfo.setLastName(profile.getLastName());
            userInfo.setLinkURI(profile.getLinkUri().toString());
            userInfo.setProfilePicURL(profile.getProfilePictureUri(150,150).toString());

        }
    }



}
