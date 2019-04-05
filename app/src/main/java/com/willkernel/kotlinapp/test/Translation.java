package com.willkernel.kotlinapp.test;

import java.util.List;


class Translation {


    /**
     * status : 0
     * content : {"ph_en":"hə'ləʊ","ph_am":"həˈloʊ","ph_en_mp3":"","ph_am_mp3":"http://res.iciba.com/resource/amp3/1/0/5d/41/5d41402abc4b2a76b9719d911017c592.mp3","ph_tts_mp3":"http://res-tts.iciba.com/5/d/4/5d41402abc4b2a76b9719d911017c592.mp3","word_mean":["int. 哈喽，喂;你好，您好;表示问候;打招呼;","n. \u201c喂\u201d的招呼声或问候声;","vi. 喊\u201c喂\u201d;"]}
     */

    private int status;
    private ContentBean content;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * ph_en : hə'ləʊ
         * ph_am : həˈloʊ
         * ph_en_mp3 :
         * ph_am_mp3 : http://res.iciba.com/resource/amp3/1/0/5d/41/5d41402abc4b2a76b9719d911017c592.mp3
         * ph_tts_mp3 : http://res-tts.iciba.com/5/d/4/5d41402abc4b2a76b9719d911017c592.mp3
         * word_mean : ["int. 哈喽，喂;你好，您好;表示问候;打招呼;","n. \u201c喂\u201d的招呼声或问候声;","vi. 喊\u201c喂\u201d;"]
         */

        private String ph_en;
        private String ph_am;
        private String ph_en_mp3;
        private String ph_am_mp3;
        private String ph_tts_mp3;
        private List<String> word_mean;

        public String getPh_en() {
            return ph_en;
        }

        public void setPh_en(String ph_en) {
            this.ph_en = ph_en;
        }

        public String getPh_am() {
            return ph_am;
        }

        public void setPh_am(String ph_am) {
            this.ph_am = ph_am;
        }

        public String getPh_en_mp3() {
            return ph_en_mp3;
        }

        public void setPh_en_mp3(String ph_en_mp3) {
            this.ph_en_mp3 = ph_en_mp3;
        }

        public String getPh_am_mp3() {
            return ph_am_mp3;
        }

        public void setPh_am_mp3(String ph_am_mp3) {
            this.ph_am_mp3 = ph_am_mp3;
        }

        public String getPh_tts_mp3() {
            return ph_tts_mp3;
        }

        public void setPh_tts_mp3(String ph_tts_mp3) {
            this.ph_tts_mp3 = ph_tts_mp3;
        }

        public List<String> getWord_mean() {
            return word_mean;
        }

        public void setWord_mean(List<String> word_mean) {
            this.word_mean = word_mean;
        }

        @Override
        public String toString() {
            return "ContentBean{" +
                    "ph_en='" + ph_en + '\'' +
                    ", ph_am='" + ph_am + '\'' +
                    ", ph_en_mp3='" + ph_en_mp3 + '\'' +
                    ", ph_am_mp3='" + ph_am_mp3 + '\'' +
                    ", ph_tts_mp3='" + ph_tts_mp3 + '\'' +
                    ", word_mean=" + word_mean +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Translation{" +
                "status=" + status +
                ", content=" + content +
                '}';
    }
}
