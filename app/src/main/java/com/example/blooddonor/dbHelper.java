package com.example.blooddonor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sherif146 on 03/01/2018.
 */

public class dbHelper extends SQLiteOpenHelper {
    // Database Info
    // Database Info
    public static final String DATABASE_NAME = "blooddonor.db";
    public static final String DBLOCATION = "/data/data/com.example.abuadit/databases/";
    private static final int DATABASE_VERSION = 4;
    private Context mcontext;
    private SQLiteDatabase mdatabase;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mcontext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_USERSRECORD = "CREATE TABLE IF NOT EXISTS " + dbColumnList.usersRecord.TABLE_NAME +
                "(" +
                dbColumnList.usersRecord._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Define a primary key
                dbColumnList.usersRecord.COLUMN_BLOODTYPE + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_CONTACTADD + " TEXT, " +
                dbColumnList.usersRecord.COLUMN_EMAIL + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_FULLNAME + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_GENDER + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_PHONE + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_STATE + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_USERTYPE + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_ACTIVE + " INTEGER, " +
                dbColumnList.usersRecord.COLUMN_DATEREG + " VARCHAR, " +
                dbColumnList.usersRecord.COLUMN_LGOV + " VARCHAR " +
                ")";

        String CREATE_TABLE_USERSREQUEST= "CREATE TABLE IF NOT EXISTS " + dbColumnList.usersRequest.TABLE_NAME +
                "(" +
                dbColumnList.usersRequest._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Define a primary key
                dbColumnList.usersRequest.COLUMN_REQUESTID + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_BLOODTYPE + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_CONTACTADD + " TEXT, " +
                dbColumnList.usersRequest.COLUMN_DATE + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_LOCALGOV + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_PHONE + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_REQUESTSTATUS + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_STATE + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_REQUESTTYPE + " VARCHAR, " +
                dbColumnList.usersRequest.COLUMN_UNIT + " INTEGER " +
                ")";

        String CREATE_TABLE_HOSPITALBANKBLOOD = "CREATE TABLE IF NOT EXISTS " + dbColumnList.hospitalBankBlood.TABLE_NAME +
                "(" +
                dbColumnList.hospitalBankBlood._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Define a primary key
                dbColumnList.hospitalBankBlood.COLUMN_PHONE + " VARCHAR, " +
                dbColumnList.hospitalBankBlood.COLUMN_RECCORDID + " VARCHAR, " +
                dbColumnList.hospitalBankBlood.COLUMN_BLOODTYPE + " TEXT, " +
                dbColumnList.hospitalBankBlood.COLUMN_QUANTITY + " INTEGER " +
                ")";


        sqLiteDatabase.execSQL(CREATE_TABLE_USERSRECORD);
        sqLiteDatabase.execSQL(CREATE_TABLE_USERSREQUEST);
        sqLiteDatabase.execSQL(CREATE_TABLE_HOSPITALBANKBLOOD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + dbColumnList.usersRequest.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + dbColumnList.usersRecord.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + dbColumnList.hospitalBankBlood.TABLE_NAME);
            //recreate the tables
            onCreate(sqLiteDatabase);
        }
    }

/*******************************************************************/
    /****** USER DETAILS *********************************************/

    //***********************************************************************
    public Cursor verifyUserExist(String phone){
        SQLiteDatabase database = getReadableDatabase();
        String sql = "SELECT * FROM "+dbColumnList.usersRecord.TABLE_NAME + " WHERE "+dbColumnList.usersRecord.COLUMN_PHONE +"= '" + phone +"' Limit 1";
        return database.rawQuery(sql, null);
    }
    public void SaveUserInformation(String fullname, String phone,String email, String gender,
                           String bloodtype, String userstate,String localgov, String address,
                                    String usertype, String active, String dateReg){

        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbColumnList.usersRecord.COLUMN_FULLNAME, fullname);
        cv.put(dbColumnList.usersRecord.COLUMN_PHONE, phone);
        cv.put(dbColumnList.usersRecord.COLUMN_EMAIL, email);
        cv.put(dbColumnList.usersRecord.COLUMN_GENDER, gender);
        cv.put(dbColumnList.usersRecord.COLUMN_BLOODTYPE, bloodtype);
        cv.put(dbColumnList.usersRecord.COLUMN_STATE, userstate);
        cv.put(dbColumnList.usersRecord.COLUMN_LGOV, localgov);
        cv.put(dbColumnList.usersRecord.COLUMN_CONTACTADD, address);
        cv.put(dbColumnList.usersRecord.COLUMN_USERTYPE, usertype);
        cv.put(dbColumnList.usersRecord.COLUMN_ACTIVE, active);
        cv.put(dbColumnList.usersRecord.COLUMN_DATEREG, dateReg);

        Cursor cursor = verifyUserExist(phone);
        if(cursor.getCount() >= 1) {
            database.update(dbColumnList.usersRecord.TABLE_NAME, cv, dbColumnList.usersRecord.COLUMN_PHONE + "= ?", new String[]{phone});
        }else{
            database.insert(dbColumnList.usersRecord.TABLE_NAME,null,cv);
        }
    }
    public Cursor getAllUsers(){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.usersRecord.TABLE_NAME,
                null,
                dbColumnList.usersRecord.COLUMN_ACTIVE +" = ?",
                new String[]{"1"},
                null,
                null,
                null);
    }

    public Cursor getAllGroup(String group){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.usersRecord.TABLE_NAME,
                null,
                dbColumnList.usersRecord.COLUMN_USERTYPE +" = ? AND "+ dbColumnList.usersRecord.COLUMN_ACTIVE +" = ?",
                new String[]{group,"1"},
                null,
                null,
                null);
    }

    public Cursor getAUser(String phone){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.usersRecord.TABLE_NAME,
                null,
                dbColumnList.usersRecord.COLUMN_PHONE +" = ? Limit 1",
                new String[]{phone},
                null,
                null,null );
    }

    public void UpdateUser(String prevphone, String newphone){
        SQLiteDatabase database = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbColumnList.usersRecord.COLUMN_PHONE, newphone);
        database.update(dbColumnList.usersRecord.TABLE_NAME, cv, dbColumnList.usersRecord.COLUMN_PHONE + "= ?", new String[]{prevphone});
    }


//    //delete all company
//    public void deleteCompany(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.abuadCompany.TABLE_NAME,
//                null,null);
//    }
//
    //*******************Student Start****************************************************

    public Cursor verifyRequestExist(String regno){
        SQLiteDatabase database = getReadableDatabase();
        String sql = "SELECT * FROM "+dbColumnList.usersRequest.TABLE_NAME + " WHERE "+dbColumnList.usersRequest.COLUMN_REQUESTID +"= '" + regno +"' Limit 1";
        return database.rawQuery(sql, null);
    }
    public void saveRequestInformation(String bloodtype, String requestId, String state,
                                         String localgovt, String phone, String unit,
                                         String requesttype, String address,String recorddate, String requeststatus){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbColumnList.usersRequest.COLUMN_BLOODTYPE, bloodtype);
        cv.put(dbColumnList.usersRequest.COLUMN_STATE, state);
        cv.put(dbColumnList.usersRequest.COLUMN_LOCALGOV, localgovt);
        cv.put(dbColumnList.usersRequest.COLUMN_UNIT, unit);
        cv.put(dbColumnList.usersRequest.COLUMN_REQUESTTYPE, requesttype);
        cv.put(dbColumnList.usersRequest.COLUMN_CONTACTADD, address);
        cv.put(dbColumnList.usersRequest.COLUMN_DATE, recorddate);
        cv.put(dbColumnList.usersRequest.COLUMN_REQUESTSTATUS, requeststatus);
        cv.put(dbColumnList.usersRequest.COLUMN_PHONE, phone);
        cv.put(dbColumnList.usersRequest.COLUMN_REQUESTID, requestId);

        Cursor cursor = verifyRequestExist(requestId);
        if(cursor.getCount() >= 1) {
            database.update(dbColumnList.usersRequest.TABLE_NAME, cv, dbColumnList.usersRequest.COLUMN_REQUESTID + "= ?", new String[]{requestId});
        }else{
            database.insert(dbColumnList.usersRequest.TABLE_NAME,null,cv);
        }
    }
    public Cursor getARequest(String requestId){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.usersRequest.TABLE_NAME,
                null,
                dbColumnList.usersRequest.COLUMN_REQUESTID +" = ? Limit 1",
                new String[]{requestId},
                null,
                null,null);
    }

    public Cursor getAllRequest(){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.usersRequest.TABLE_NAME,
                null,
                dbColumnList.usersRequest.COLUMN_REQUESTSTATUS +" = ? OR "+
                        dbColumnList.usersRequest.COLUMN_REQUESTSTATUS +" = ?",
                new String[]{"0","1"},
                null,
                null,null);
    }

    //active 0 cleared 1 deleted 2
    public Cursor getAUserRequest(String phone){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.usersRequest.TABLE_NAME,
                null,
                dbColumnList.usersRequest.COLUMN_PHONE +" = ? AND "
                        + dbColumnList.usersRequest.COLUMN_REQUESTSTATUS +" = ?"+
                " OR " + dbColumnList.usersRequest.COLUMN_REQUESTSTATUS +" = ?",
                new String[]{phone,"0","1"},
                null,
                null,null);
    }

    public void UpdateUserRequest(String prevphone, String newphone){
        SQLiteDatabase database = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbColumnList.usersRequest.COLUMN_PHONE, newphone);
        database.update(dbColumnList.usersRequest.TABLE_NAME, cv, dbColumnList.usersRequest.COLUMN_PHONE + "= ?", new String[]{prevphone});
    }

    public Cursor verifyBloodBankExist(String recordid){
        SQLiteDatabase database = getReadableDatabase();
        String sql = "SELECT * FROM "+dbColumnList.hospitalBankBlood.TABLE_NAME + " WHERE "+dbColumnList.hospitalBankBlood.COLUMN_RECCORDID +"= '" + recordid +"' Limit 1";
        return database.rawQuery(sql, null);
    }
    public void saveBloodBank(String recordid,String bloodtype, String phone,String quantity){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(dbColumnList.hospitalBankBlood.COLUMN_RECCORDID, recordid);
        cv.put(dbColumnList.hospitalBankBlood.COLUMN_BLOODTYPE, bloodtype);
        cv.put(dbColumnList.hospitalBankBlood.COLUMN_PHONE, phone);
        cv.put(dbColumnList.hospitalBankBlood.COLUMN_QUANTITY, quantity);
        Cursor cursor = verifyBloodBankExist(recordid);
        if(cursor.getCount() >= 1) {
            database.update(dbColumnList.hospitalBankBlood.TABLE_NAME, cv, dbColumnList.hospitalBankBlood.COLUMN_RECCORDID + " = ?", new String[]{recordid});
        }else{
            database.insert(dbColumnList.hospitalBankBlood.TABLE_NAME,null,cv);
        }
    }
    public Cursor getACompanyBank(String phone){
        SQLiteDatabase database = getReadableDatabase();
        return database.query(dbColumnList.hospitalBankBlood.TABLE_NAME,
                null,
                dbColumnList.hospitalBankBlood.COLUMN_PHONE +" = ? Limit 1",
                new String[]{phone},
                null,
                null,null);
    }


//
//    //delete all student
//    public void deleteProfilePics(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.userProfilePics.TABLE_NAME,
//                null,null);
//    }
//

}
