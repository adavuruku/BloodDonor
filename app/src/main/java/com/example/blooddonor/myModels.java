package com.example.blooddonor;

public class myModels {
    public class Donors {

        private String fullname;
        private String phone;
        private String email;
        private String gender;
        private String bloodtype;
        private String state;
        private String localGovt;
        private String address;

        public Donors(String fullname, String phone, String email, String gender,
                      String bloodtype, String state, String localGovt, String address) {
            this.fullname = fullname;
            this.phone = phone;
            this.email = email;
            this.gender = gender;
            this.bloodtype = bloodtype;
            this.state = state;
            this.localGovt = localGovt;
            this.address = address;
        }

        public String getFullname() {
            return fullname;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getGender() {
            return gender;
        }

        public String getBloodtype() {
            return bloodtype;
        }

        public String getState() {
            return state;
        }

        public String getLocalGovt() {
            return localGovt;
        }

        public String getAddress() {
            return address;
        }
    }

    public class Hospitals {
        private String fullname;
        private String phone;
        private String email;
        private String type;
        int ap,an,bp,bn,op,on,abp,abn;
        private String state;
        private String localGovt;
        private String address;
        private String messageBlood;

        public Hospitals(String fullname, String phone, String email,
                         String type,String state,String localGovt, String address, int ap, int an, int bp, int bn,
                         int op, int on, int abp, int abn,String messageBlood
                         ) {
            this.fullname = fullname;
            this.phone = phone;
            this.email = email;
            this.type = type;
            this.ap = ap;
            this.an = an;
            this.bp = bp;
            this.bn = bn;
            this.op = op;
            this.on = on;
            this.abp = abp;
            this.abn = abn;
            this.state = state;
            this.localGovt = localGovt;
            this.address = address;
            this.messageBlood = messageBlood;
        }

        public String getFullname() {
            return fullname;
        }

        public String getMessageBlood() {
            return messageBlood;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getType() {
            return type;
        }

        public int getAp() {
            return ap;
        }

        public int getAn() {
            return an;
        }

        public int getBp() {
            return bp;
        }

        public int getBn() {
            return bn;
        }

        public int getOp() {
            return op;
        }

        public int getOn() {
            return on;
        }

        public int getAbp() {
            return abp;
        }

        public int getAbn() {
            return abn;
        }

        public String getState() {
            return state;
        }

        public String getLocalGovt() {
            return localGovt;
        }

        public String getAddress() {
            return address;
        }
    }

    public class Request {
        private String requestID;
        private String requestAuthor;
        private String dateReg;
        private String state;
        private String email;
        private String localGovt;
        private String address;
        private String type;
        private Boolean mytype;
        private String phone;
        private String bloodtype;
        private String unit;

        public Request(String requestID, String requestAuthor, String dateReg, String state,
                       String email, String localGovt, String address,
                       String type,Boolean mytype,String phone, String bloodtype,String unit) {
            this.requestID = requestID;
            this.requestAuthor = requestAuthor;
            this.dateReg = dateReg;
            this.phone = phone;
            this.state = state;
            this.unit = unit;
            this.email = email;
            this.localGovt = localGovt;
            this.address = address;
            this.type = type;
            this.mytype = mytype;
            this.bloodtype = bloodtype;
        }

        public Boolean getMytype() {
            return mytype;
        }

        public String getUnit() {
            return unit;
        }
        public String getPhone() {
            return phone;
        }

        public String getRequestAuthor() {
            return requestAuthor;
        }
        public String getRequestID() {
            return requestID;
        }

        public String getDateReg() {
            return dateReg;
        }

        public String getState() {
            return state;
        }

        public String getEmail() {
            return email;
        }

        public String getLocalGovt() {
            return localGovt;
        }

        public String getAddress() {
            return address;
        }

        public String getType() {
            return type;
        }

        public String getBloodtype() {
            return bloodtype;
        }
    }

    public class Facts{
        String facts;

        public Facts(String facts) {
            this.facts = facts;
        }

        public String getFacts() {
            return facts;
        }
    }
}
