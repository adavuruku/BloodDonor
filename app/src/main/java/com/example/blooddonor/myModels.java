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
}
