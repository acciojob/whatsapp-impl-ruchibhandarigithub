package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<String, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String ,User> userAcc;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();

        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();

        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String  createUser(String name, String mobile) throws Exception {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }

       User user  = new User(name,mobile);
        return "SUCCESS";
    }
    public Group createGroup(List<User> users) {
        if(users.size()==2){
            String group = users.get(1).getName();
            Group g = new Group(group,2);
            groupUserMap.put(g,users);
            return g;
        }
        this.customGroupCount++;
        String group = "Group "+this.customGroupCount;
        Group g = new Group(group,users.size());
        groupUserMap.put(g,users);
        adminMap.put(g,users.get(0));
        return g;


    }

    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
       messageId++;
       Message message = new Message(messageId,content);
        return message.getId();
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
       if(!groupUserMap.containsKey(group)){
           throw new Exception("Group does not exist");
       }
       if(!this.userExistInGroup(group,sender)){
           throw new Exception("You are not allowed to send message");
       }

       List<Message> messages = new ArrayList<>();
       if(groupMessageMap.containsKey(group))
           messages=groupMessageMap.get(group);

       messages.add(message);
       groupMessageMap.put(group,messages);
       return messages.size();

    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw C if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!this.userExistInGroup(group,user)){
            throw new Exception("User is not a participant");
        }
        if(!adminMap.get(group).equals(approver)){
            throw new Exception("Approver does not have rights");
        }
        adminMap.put(group,user);

        return "SUCCESS";
    }
    private boolean userExistInGroup(Group group,User sender){
        List<User> users = groupUserMap.get(group);
        for(User user : users){
            if(user.equals(sender)){
                return true;
            }
        }
        return false;
    }



}
