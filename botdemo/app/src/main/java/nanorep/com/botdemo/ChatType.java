package nanorep.com.botdemo;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static nanorep.com.botdemo.ChatType.LoggedIn;
import static nanorep.com.botdemo.ChatType.NotLoggedIn;

@Retention(RetentionPolicy.SOURCE)
@StringDef({LoggedIn, NotLoggedIn})
public @interface ChatType {
    String LoggedIn = "loggedIn";
    String NotLoggedIn = "notLoggedIn";
}
