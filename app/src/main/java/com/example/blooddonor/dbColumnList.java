package com.example.blooddonor;

import android.provider.BaseColumns;

/**
 * Created by sherif146 on 03/01/2018.
 */

public class dbColumnList {
    public static String serveraddress = "https://antenantal.000webhostapp.com/blooddonorrest.php";
    public static String fromlogin;

    public static class usersRecord implements BaseColumns{
        public static final String TABLE_NAME = "usersRecord";
        public static final String COLUMN_FULLNAME = "fullname";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_BLOODTYPE= "bloodtype";
        public static final String COLUMN_STATE = "userstate";
        public static final String COLUMN_LGOV= "userlocalgov";
        public static final String COLUMN_USERTYPE = "usertype";
        public static final String COLUMN_ACTIVE = "active";
        public static final String COLUMN_DATEREG = "dateReg";
        public static final String COLUMN_CONTACTADD = "contactAddress";
    }

    public static class hospitalBankBlood implements BaseColumns{
        public static final String TABLE_NAME = "hospitalBankBlood";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_RECCORDID = "recordId";
        public static final String COLUMN_BLOODTYPE= "bloodtype";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static class usersRequest implements BaseColumns{
        public static final String TABLE_NAME = "usersRequest";
        public static final String COLUMN_REQUESTID = "requestId";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_STATE = "userstate";
        public static final String COLUMN_LOCALGOV = "userlocalgov";
        public static final String COLUMN_REQUESTTYPE= "requesttype";
        public static final String COLUMN_BLOODTYPE= "bloodtype";
        public static final String COLUMN_REQUESTSTATUS = "requeststatus";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_CONTACTADD = "contactAddress";
        public static final String COLUMN_DATE = "recordDate";
    }
}
