package com.example.blooddonor;

import android.provider.BaseColumns;

/**
 * Created by sherif146 on 03/01/2018.
 */

public class dbColumnList {
    public static String serveraddress = "https://antenantal.000webhostapp.com/blooddonorrest.php";
    public static String fromlogin;

    public static final String allfacts[] ={
            "About 1 in 7 people entering a hospital need blood.",
            "One pint of blood can save up to three lives.",
            "Healthy adults who are at least 17 years old, and at least 110 pounds may donate about a pint of blood—the most common form of donation—every 56 days, or every two months. Females receive 53 percent of blood transfusions; males receive 47 percent.",
            "Four main red blood cell types: A, B, AB and O. Each can be positive or negative for the Rh factor. AB is the universal recipient; O negative is the universal donor of red blood cells.",
            "Dr. Karl Landsteiner first identified the major human blood groups – A, B, AB and O – in 1901.",
            "One unit of blood can be separated into several components: red blood cells, plasma, platelets and cryoprecipitate.",
            "Red blood cells carry oxygen to the body’s organs and tissues.",
            "Red blood cells live about 120 days in the circulatory system.",
            "Platelets promote blood clotting and give those with leukemia and other cancers a chance to live.",
            "Plasma is a pale yellow mixture of water, proteins and salts.",
            "Plasma, which is 90 percent water, makes up 55 percent of blood volume.",
            "Healthy bone marrow makes a constant supply of red cells, plasma and platelets.",
            "Blood or plasma that comes from people who have been paid for it cannot be used to human transfusion.",
            "Granulocytes, a type of white blood cell, roll along blood vessel walls in search of bacteria to engulf and destroy.",
            "White cells are the body’s primary defense against infection.",
            "Apheresis is a special kind of blood donation that allows a donor to give specific blood components, such as platelets.",
            "42 days: how long most donated red blood cells can be stored.",
            "Five days: how long most donated platelets can be stored.",
            "One year: how long frozen plasma can be stored.",
            "Much of today’s medical care depends on a steady supply of blood from healthy donors.",
            "2.7 pints: the average whole blood and red blood cell transfusion.",
            "Children being treated for cancer, premature infants and children having heart surgery need blood and platelets from donors of all types, especially type O.",
            "Anemic patients need blood transfusions to increase their red blood cell levels.",
            "Cancer, transplant and trauma patients, and patients undergoing open-heart surgery may require platelet transfusions to survive.",
            "Sickle cell disease is an inherited disease that affects more than 80,000 people in the United States, 98 percent of whom are of African descent.",
            "Many patients with severe sickle cell disease receive blood transfusions every month.",
            "A patient could be forced to pass up a lifesaving organ, if compatible blood is not available to support the transplant.",
            "Thirteen tests (11 for infectious diseases) are performed on each unit of donated blood.",
            "17 percent of non-donors cite “never thought about it” as the main reason for not giving, while 15 percent say they’re too busy.",
            "The #1 reason blood donors say they give is because they “want to help others.” ",
            "Shortages of all blood types happen during the summer and winter holidays.",
            "Blood centers often run short of types O and B red blood cells.",
            "The rarest blood type is the one not on the shelf when it’s needed by a patient.",
            "There is no substitute for human blood.",
            "If all blood donors gave three times a year, blood shortages would be a rare event (The current average is about two.).",
            "If only one more percent of all Nigerians would give blood, blood shortages would disappear for the foreseeable future.",
            "46.5 gallons: amount of blood you could donate if you begin at age 17 and donate every 56 days until you reach 79 years old.",
            "Four easy steps to donate blood: medical history, quick physical, donation and snacks.",
            "The actual blood donation usually takes about 10 minutes. The entire process – from the time you sign in to the time you leave – takes about an hour.",
            "After donating blood, you replace the fluid in hours and the red blood cells within four weeks. It takes eight weeks to restore the iron lost after donating.",
            " You cannot get AIDS or any other infectious disease by donating blood.",
            "10 pints: amount of blood in the body of an average adult.",
            "One unit of whole blood is roughly the equivalent of one pint.",
            "Blood makes up about 7 percent of your body’s weight.",
            "A newborn baby has about one cup of blood in his body.",
            "Giving blood will not decrease your strength.",
            "Any company, community organization, place of worship or individual may contact their local community blood center to host a blood drive.",
            "People who donate blood are volunteers and are not paid for their donation.",
            "Blood donation. It’s about an hour of your time. It’s About Life."
    };
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
