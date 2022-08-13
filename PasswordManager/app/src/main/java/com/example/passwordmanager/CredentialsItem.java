package com.example.passwordmanager;

import java.io.Serializable;

public class CredentialsItem  implements Serializable {
        private String user;
        private String password;
        private String site;

        public CredentialsItem(String site, String user, String password) {
            this.user = user;
            this.password = password;
            this.site = site;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }
    }

