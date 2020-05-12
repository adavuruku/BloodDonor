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
    private static final int DATABASE_VERSION = 3;
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
                dbColumnList.usersRecord.COLUMN_LGOV + " VARCHAR " +
                ")";

        String CREATE_TABLE_USERSREQUEST= "CREATE TABLE IF NOT EXISTS " + dbColumnList.usersRequest.TABLE_NAME +
                "(" +
                dbColumnList.usersRequest._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Define a primary key
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

        String CREATE_TABLE_HOSPITALBANKBLOOD= "CREATE TABLE IF NOT EXISTS " + dbColumnList.hospitalBankBlood.TABLE_NAME +
                "(" +
                dbColumnList.hospitalBankBlood._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Define a primary key
                dbColumnList.hospitalBankBlood.COLUMN_PHONE + " VARCHAR, " +
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
                                    String usertype){
//        openDatabase();
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
                null,
                null,
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

//    //delete all company
//    public void deleteCompany(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.abuadCompany.TABLE_NAME,
//                null,null);
//    }
//
//    //*******************Student Start****************************************************
//
//    public Cursor verifyStudentExist(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.abuadstudent.TABLE_NAME + " WHERE "+dbColumnList.abuadstudent.COLUMN_REGNO +"= '" + regno +"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveStudentInformation(String regno, String fullname,
//                                         String faculty, String department,String phone, String email,
//                                         String contactAddress, String gender,String degree, String mode,
//                                         String itState, String itLgov,String itLevel){
////        openDatabase();
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.abuadstudent.COLUMN_REGNO, regno);
//        cv.put(dbColumnList.abuadstudent.COLUMN_FULLNAME, fullname);
//        cv.put(dbColumnList.abuadstudent.COLUMN_FACULTY, faculty);
//        cv.put(dbColumnList.abuadstudent.COLUMN_DEPARTMENT, department);
//        cv.put(dbColumnList.abuadstudent.COLUMN_MODE, mode);
//        cv.put(dbColumnList.abuadstudent.COLUMN_DEGREE, degree);
//        cv.put(dbColumnList.abuadstudent.COLUMN_STATE, itState);
//        cv.put(dbColumnList.abuadstudent.COLUMN_LGOV, itLgov);
//        cv.put(dbColumnList.abuadstudent.COLUMN_LEVEL, itLevel);
//        cv.put(dbColumnList.abuadstudent.COLUMN_GENDER, gender);
//        cv.put(dbColumnList.abuadstudent.COLUMN_PHONE, phone);
//        cv.put(dbColumnList.abuadstudent.COLUMN_EMAIL, email);
//        cv.put(dbColumnList.abuadstudent.COLUMN_CONTACTADD, contactAddress);
//
//        Cursor cursor = verifyStudentExist(regno);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.abuadstudent.TABLE_NAME, cv, dbColumnList.abuadstudent.COLUMN_REGNO + "= ?", new String[]{regno});
//        }else{
//            database.insert(dbColumnList.abuadstudent.TABLE_NAME,null,cv);
//        }
////        closeDatabase();
//    }
//    public Cursor getAStudent(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.abuadstudent.TABLE_NAME,
//                null,
//                dbColumnList.abuadstudent.COLUMN_REGNO +" = ? Limit 1",
//                new String[]{regno},
//                null,
//                null,null);
//    }
//
//    //delete all student
//    public void deleteStudent(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.abuadstudent.TABLE_NAME,
//                null,null);
//    }
//    //*********************************LECTURERS*************************************************
//
//
//    public Cursor verifyLecturerExist(String staffid){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.abuadLecturer.TABLE_NAME + " WHERE "+dbColumnList.abuadLecturer.COLUMN_STAFFID +"= '" + staffid +"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveLecturerInformation(String staffid, String fullname,
//                                         String faculty, String department,String phone, String email,String staffaddress){
////        openDatabase();
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.abuadLecturer.COLUMN_STAFFID, staffid);
//        cv.put(dbColumnList.abuadLecturer.COLUMN_FULLNAME, fullname);
//        cv.put(dbColumnList.abuadLecturer.COLUMN_FACULTY, faculty);
//        cv.put(dbColumnList.abuadLecturer.COLUMN_DEPARTMENT, department);
//        cv.put(dbColumnList.abuadLecturer.COLUMN_STAFFADDRESS, staffaddress);
//        cv.put(dbColumnList.abuadLecturer.COLUMN_EMAIL, email);
//        cv.put(dbColumnList.abuadLecturer.COLUMN_PHONE, phone);
//
//        Cursor cursor = verifyLecturerExist(staffid);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.abuadLecturer.TABLE_NAME, cv, dbColumnList.abuadLecturer.COLUMN_STAFFID + "= ?", new String[]{staffid});
//        }else{
//            database.insert(dbColumnList.abuadLecturer.TABLE_NAME,null,cv);
//        }
////        closeDatabase();
//    }
//    public Cursor getALecturerInfo(String staffid){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.abuadLecturer.TABLE_NAME,
//                null,
//                dbColumnList.abuadLecturer.COLUMN_STAFFID +" = ? Limit 1",
//                new String[]{staffid},
//                null,
//                null,null);
//    }
//
//    //delete all student
//    public void deleteLeturer(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.abuadLecturer.TABLE_NAME,
//                null,null);
//    }
//    //*********************** IT INFORMATION ***********************************************************
//
//    public Cursor verifyInformationExist(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.abuadItInformation.TABLE_NAME + " WHERE "+dbColumnList.abuadItInformation.COLUMN_REGNO +"= '" + regno +"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveITInformation(String regno,String startDate, String endDate,String duration, String staffid,
//                                          String companyId){
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.abuadItInformation.COLUMN_REGNO, regno);
//        cv.put(dbColumnList.abuadItInformation.COLUMN_DATESTART, startDate);
//        cv.put(dbColumnList.abuadItInformation.COLUMN_DATEEND, endDate);
//        cv.put(dbColumnList.abuadItInformation.COLUMN_COMPANYID, companyId);
//        cv.put(dbColumnList.abuadItInformation.COLUMN_STAFFID, staffid);
//        cv.put(dbColumnList.abuadItInformation.COLUMN_DURATION, duration);
//
//        Cursor cursor = verifyInformationExist(regno);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.abuadItInformation.TABLE_NAME, cv, dbColumnList.abuadItInformation.COLUMN_REGNO + "= ?", new String[]{regno});
//        }else{
//            database.insert(dbColumnList.abuadItInformation.TABLE_NAME,null,cv);
//        }
//    }
//    public Cursor getAStudentItInfo(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.abuadItInformation.TABLE_NAME,
//                null,
//                dbColumnList.abuadItInformation.COLUMN_REGNO +" = ? Limit 1",
//                new String[]{regno},
//                null,
//                null,null);
//    }
//
//    public void UpdateStudentItInfo(String regno, String companyId){
//        SQLiteDatabase database = getReadableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.abuadItInformation.COLUMN_COMPANYID, companyId);
//        database.update(dbColumnList.abuadItInformation.TABLE_NAME, cv, dbColumnList.abuadItInformation.COLUMN_REGNO + "= ?", new String[]{regno});
//    }
//
//    public Cursor getAllStaffITStudent(String staffID){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.abuadItInformation.TABLE_NAME,
//                null,
//                dbColumnList.abuadItInformation.COLUMN_STAFFID + " = ? ",
//                new String[]{staffID},
//                null,
//                null,null);
//    }
//
//    //delete all student
//    public void deleteItInformation(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.abuadItInformation.TABLE_NAME,
//                null,null);
//    }
////    *************************************************************************
////*********************** NOTICE ***********************************************************
//
//    public Cursor verifyNoticeExist(String noticeid){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.abuadNotice.TABLE_NAME + " WHERE "+dbColumnList.abuadNotice.COLUMN_ID +"= '" + noticeid +"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveNotice(String noticeid,String noticedesc, String author,String title, String date, String status){
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.abuadNotice.COLUMN_ID, noticeid);
//        cv.put(dbColumnList.abuadNotice.COLUMN_TITLE, title);
//        cv.put(dbColumnList.abuadNotice.COLUMN_DESCRIPTION, noticedesc);
//        cv.put(dbColumnList.abuadNotice.COLUMN_NOTICEDATE, date);
//        cv.put(dbColumnList.abuadNotice.COLUMN_AUTHOR, author);
//        cv.put(dbColumnList.abuadNotice.COLUMN_STATUS, status);
//
//        Cursor cursor = verifyNoticeExist(noticeid);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.abuadNotice.TABLE_NAME, cv, dbColumnList.abuadNotice.COLUMN_ID + " = ?", new String[]{noticeid});
//        }else{
//            database.insert(dbColumnList.abuadNotice.TABLE_NAME,null,cv);
//        }
//    }
//    public Cursor getANotice(String noticeId){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.abuadNotice.TABLE_NAME,
//                null,
//                dbColumnList.abuadNotice.COLUMN_ID +" = ?  AND "+ dbColumnList.abuadNotice.COLUMN_STATUS + " = ? Limit 1",
//                new String[]{noticeId,"0"},
//                null,
//                null,null);
//    }
//
//    public Cursor getAllNotice(){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.abuadNotice.TABLE_NAME,
//                null,
//                 dbColumnList.abuadNotice.COLUMN_STATUS + " = ?",
//                new String[]{"0"},
//                null,
//                null,
//                null);
//    }
//
//
//
////    **************************PROFILE PICS********************************
//
//
//    public Cursor verifyImageExist(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.userProfilePics.TABLE_NAME + " WHERE "+dbColumnList.userProfilePics.COLUMN_REGNO +"= '" + regno +"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveProfilePics(String regno,byte[] imageData){
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.userProfilePics.COLUMN_REGNO, regno);
//        cv.put(dbColumnList.userProfilePics.COLUMN_PROFILEPICS, imageData);
//
//        Cursor cursor = verifyImageExist(regno);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.userProfilePics.TABLE_NAME, cv, dbColumnList.userProfilePics.COLUMN_REGNO + " = ?", new String[]{regno});
//        }else{
//            database.insert(dbColumnList.userProfilePics.TABLE_NAME,null,cv);
//        }
//    }
//    public Cursor getAProfilePics(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.userProfilePics.TABLE_NAME,
//                null,
//                dbColumnList.userProfilePics.COLUMN_REGNO + " = ? Limit 1",
//                new String[]{regno},
//                null,
//                null,null);
//    }
//
//    //delete all student
//    public void deleteProfilePics(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.userProfilePics.TABLE_NAME,
//                null,null);
//    }
//
//    //    **************************PROFILE PICS********************************
//
//
//    public Cursor verifyApplicationExist(String regno, String companyId){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.applicationList.TABLE_NAME + " WHERE "+dbColumnList.applicationList.COLUMN_REGNO +"= '" + regno +"' AND " +
//                dbColumnList.applicationList.COLUMN_COMPANYID +"= '" + companyId +"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveApplication(String regno,String companyId, String status){
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.applicationList.COLUMN_REGNO, regno);
//        cv.put(dbColumnList.applicationList.COLUMN_COMPANYID, companyId);
//        cv.put(dbColumnList.applicationList.COLUMN_ACCEPTSTATUS, status);
//
//        Cursor cursor = verifyApplicationExist(regno,companyId);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.applicationList.TABLE_NAME, cv, dbColumnList.applicationList.COLUMN_REGNO + " = ? AND "
//                    + dbColumnList.applicationList.COLUMN_COMPANYID + " = ?" , new String[]{regno,companyId});
//        }else{
//            database.insert(dbColumnList.applicationList.TABLE_NAME,null,cv);
//        }
//    }
//
//    public Cursor getAllStudentApplication(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.applicationList.TABLE_NAME,
//                null,
//                dbColumnList.applicationList.COLUMN_REGNO + " = ?",
//                new String[]{regno},
//                null,
//                null,dbColumnList.applicationList.COLUMN_ACCEPTSTATUS + " desc");
//    }
//    public Cursor getAllCompanyApplication(String companyId, String status){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.applicationList.TABLE_NAME,
//                null,
//                dbColumnList.applicationList.COLUMN_COMPANYID + " = ? AND " +dbColumnList.applicationList.COLUMN_ACCEPTSTATUS + "= ?",
//                new String[]{companyId, status},
//                null,
//                null,null);
//    }
//
//    //delete all student Application
//    public void deleteApplication(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.applicationList.TABLE_NAME,
//                null,null);
//    }
//
//
//
//
//    public Cursor verifyRegisterExist(String regno, String companyId,String dateAttendance){
//        SQLiteDatabase database = getReadableDatabase();
//        String sql = "SELECT * FROM "+dbColumnList.registerList.TABLE_NAME + " WHERE "+dbColumnList.registerList.COLUMN_REGNO +"= '" + regno +"' AND " +
//                dbColumnList.registerList.COLUMN_COMPANYID +"= '" + companyId +"' AND "+dbColumnList.registerList.COLUMN_DATE+" = '"+dateAttendance+"' Limit 1";
//        return database.rawQuery(sql, null);
//    }
//    public void saveRegister(String regno, String companyId, String status, String dateAttendance){
//        SQLiteDatabase database = getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(dbColumnList.registerList.COLUMN_REGNO, regno);
//        cv.put(dbColumnList.registerList.COLUMN_COMPANYID, companyId);
//        cv.put(dbColumnList.registerList.COLUMN_DATE, dateAttendance);
//        cv.put(dbColumnList.registerList.COLUMN_RECORSTATUS, status);
//
//        Cursor cursor = verifyRegisterExist(regno, companyId, dateAttendance);
//        if(cursor.getCount() >= 1) {
//            database.update(dbColumnList.registerList.TABLE_NAME, cv, dbColumnList.registerList.COLUMN_REGNO + " = ? AND "
//                    + dbColumnList.registerList.COLUMN_COMPANYID + " = ? AND " + dbColumnList.registerList.COLUMN_DATE + " = ?", new String[]{regno,companyId,dateAttendance});
//        }else{
//            database.insert(dbColumnList.registerList.TABLE_NAME,null,cv);
//        }
//    }
//    public Cursor getAllStudentAttendance(String regno){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.registerList.TABLE_NAME,
//                null,
//                dbColumnList.registerList.COLUMN_REGNO + " = ?",
//                new String[]{regno},
//                null,
//                null,dbColumnList.registerList.COLUMN_DATE + " desc");
//    }
//    public Cursor getAllAttendance(){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.registerList.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,dbColumnList.registerList.COLUMN_DATE + " desc");
//    }
//    public Cursor getAllCompanyAttendance(String companyId){
//        SQLiteDatabase database = getReadableDatabase();
//        return database.query(dbColumnList.registerList.TABLE_NAME,
//                null,
//                dbColumnList.registerList.COLUMN_COMPANYID + " = ?",
//                new String[]{companyId},
//                null,
//                null,null);
//    }
//
//    //delete all attendance
//    public void deleteAttendace(){
//        SQLiteDatabase database = getWritableDatabase();
//        database.delete(dbColumnList.registerList.TABLE_NAME,
//                null,null);
//    }

}
