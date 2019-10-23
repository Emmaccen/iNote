package com.echo.iNote;

class ChatModel {
    String sender;
    String receiver;
    String message;
    String isNote;
    String phoneTime;
    String senderNumber;
    String isDeleted;
    String isNoteSaved;
    String time;
    String isSeen;

    public String getIsNoteSaved() {
        return isNoteSaved;
    }

    public void setIsNoteSaved(String isNoteSaved) {
        this.isNoteSaved = isNoteSaved;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public String getTime() {
        return time;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public String getPhoneTime() {
        return phoneTime;
    }

    public String getIsNote() {
        return isNote;
    }


    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }
}
