package net.suntrans.engineering.bean;

/**
 * Created by Looney on 2018/7/31.
 * Des:
 */
public class LoginEntity {


    /**
     * code : 200
     * msg : 登录成功
     * token : {"access_token":"7c2d9ff4226ddd6dc76e4ddd3d1b276a0925a4d2","expires_in":2592000,"token_type":"Bearer","scope":null,"refresh_token":"11de2feb97fa9a861f6c137c3da9161ab1d22046"}
     * user : {"id":"1","group_id":"1","username":"admin","nickname":"管理员","email":null,"email_verified":null,"scope":null,"status":"1"}
     */

    public int code;
    public String msg;
    public TokenBean token;
    public UserBean user;

    public static class TokenBean {
        /**
         * access_token : 7c2d9ff4226ddd6dc76e4ddd3d1b276a0925a4d2
         * expires_in : 2592000
         * token_type : Bearer
         * scope : null
         * refresh_token : 11de2feb97fa9a861f6c137c3da9161ab1d22046
         */

        public String access_token;
        public String expires_in;
        public String token_type;
        public String scope;
        public String refresh_token;
    }

    public static class UserBean {
        /**
         * id : 1
         * group_id : 1
         * username : admin
         * nickname : 管理员
         * email : null
         * email_verified : null
         * scope : null
         * status : 1
         */

        public String id;
        public String group_id;
        public String username;
        public String nickname;
        public String email;
        public String email_verified;
        public String scope;
        public String status;
    }
}
