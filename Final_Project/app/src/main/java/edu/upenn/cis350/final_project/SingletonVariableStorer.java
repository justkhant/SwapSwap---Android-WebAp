package edu.upenn.cis350.final_project;

public class SingletonVariableStorer {
    private static SingletonVariableStorer instance = null;
    private String curr_user;

    private SingletonVariableStorer() {
        curr_user = "";
    }

    private void setCurrUser(String curr_user) {
        this.curr_user = curr_user;
    }

    private String getCurrUser() {
        return curr_user;
    }

    public static String getCurrUserInstance() {
        if (instance == null) {
            instance = new SingletonVariableStorer();
        }
        return instance.getCurrUser();
    }

    public static void setCurrUserInstance(String curr_user) {
        if (instance == null) {
            instance = new SingletonVariableStorer();
        }
        instance.setCurrUser(curr_user);
    }
}
