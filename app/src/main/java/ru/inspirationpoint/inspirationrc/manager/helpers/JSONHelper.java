package ru.inspirationpoint.inspirationrc.manager.helpers;


import android.content.Context;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import server.schemas.requests.FightInput;
import server.schemas.requests.GroupResult;
import server.schemas.responses.DialogOutput;
import server.schemas.responses.FightOutput;
import server.schemas.responses.FighterStat;
import server.schemas.responses.Message;

public class JSONHelper {

    private static final String FILE_NAME_FIGHTS = "fights_list.json";
//    private static final String FILE_NAME_TRAININGS = "trainings.json";
    private static final String FILE_NAME_FIGHTERS = "fighters_list.json";
    private static final String FILE_NAME_GROUP_RESULT = "group_list.json";
    private static final String FILE_NAME_DIALOGS = "dialogs_list.json";
    private static final String FILE_NAME_MESSAGES_CACHE = ".json";

    public static void exportToJSON(Context context, ArrayList<?> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setItems(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
//            if (dataList.get(0) instanceof Training) {
//                fileOutputStream = context.openFileOutput(FILE_NAME_TRAININGS, Context.MODE_PRIVATE);
//            } else
                if (dataList.get(0) instanceof FightInput || dataList.get(0) instanceof FightOutput) {
                fileOutputStream = context.openFileOutput(FILE_NAME_FIGHTS, Context.MODE_PRIVATE);
            } else if (dataList.get(0) instanceof GroupResult) {
                fileOutputStream = context.openFileOutput(FILE_NAME_GROUP_RESULT, Context.MODE_PRIVATE);
            }else if (dataList.get(0) instanceof FighterStat) {
                fileOutputStream = context.openFileOutput(FILE_NAME_FIGHTERS, Context.MODE_PRIVATE);
            } else if (dataList.get(0) instanceof DialogOutput) {
                    fileOutputStream = context.openFileOutput(FILE_NAME_DIALOGS, Context.MODE_PRIVATE);
                }
            if (fileOutputStream != null) {
                fileOutputStream.write(jsonString.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void exportToJSON(Context context, ArrayList<?> dataList, String dialogId) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setItems(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            if (dataList.get(0) instanceof Message) {
                fileOutputStream = context.openFileOutput(dialogId + FILE_NAME_MESSAGES_CACHE, Context.MODE_PRIVATE);
            }
            if (fileOutputStream != null) {
                fileOutputStream.write(jsonString.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<?> importFromJSON(Context context, ItemClass type, String dialogId) {

        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try{
            switch (type) {
                case Messages:
                    fileInputStream = context.openFileInput(dialogId + FILE_NAME_MESSAGES_CACHE);
                    break;
            }
            if (fileInputStream != null) {
                streamReader = new InputStreamReader(fileInputStream);
            }
            Gson gson = new Gson();
            if (streamReader != null) {
                switch (type) {
                    case Messages:
                        MessagesItems items = gson.fromJson(streamReader, MessagesItems.class);
                        if (items != null) {
                            return items.getItems();
                        }
                        break;
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static ArrayList<?> importFromJSON(Context context, ItemClass type) {

        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try{
            switch (type) {
//                case Training :
//                    fileInputStream = context.openFileInput(FILE_NAME_TRAININGS);
//                    break;
                case FightInput:
                    fileInputStream = context.openFileInput(FILE_NAME_FIGHTS);
                    break;
                case FightOutput:
                    fileInputStream = context.openFileInput(FILE_NAME_FIGHTS);
                    break;
                case FighterStat:
                    fileInputStream = context.openFileInput(FILE_NAME_FIGHTERS);
                    break;
                case GroupResult:
                    fileInputStream = context.openFileInput(FILE_NAME_GROUP_RESULT);
                    break;
                case Dialogs:
                    fileInputStream = context.openFileInput(FILE_NAME_DIALOGS);
                    break;
            }
            if (fileInputStream != null) {
                streamReader = new InputStreamReader(fileInputStream);
            }
            Gson gson = new Gson();
            if (streamReader != null) {
                switch (type) {
//                    case Training :
//                        TrainingItems items = gson.fromJson(streamReader, TrainingItems.class);
//                        if (items != null) {
//                            return items.getItems();
//                        }
//                        break;
                    case FighterStat:
                        FighterStatItems items3 = gson.fromJson(streamReader, FighterStatItems.class);
                        if (items3 != null) {
                            return items3.getItems();
                        }
                        break;
                    case FightInput:
                        FightInputItems items1 = gson.fromJson(streamReader, FightInputItems.class);
                        if (items1 != null) {
                            return items1.getItems();
                        }
                        break;
                    case FightOutput:
                        FightOutputItems items2 = gson.fromJson(streamReader, FightOutputItems.class);
                        if (items2 != null) {
                            return items2.getItems();
                        }
                        break;
                    case GroupResult:
                        GroupResultItems items4 = gson.fromJson(streamReader, GroupResultItems.class);
                        if (items4 != null) {
                            return items4.getItems();
                        }
                        break;
                    case Dialogs:
                        DialogsItems items5 = gson.fromJson(streamReader, DialogsItems.class);
                        if (items5 != null) {
                            return items5.getItems();
                        }
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private static class DataItems {
        private ArrayList<?> items;

        ArrayList<?> getItems() {
            return items;
        }
        void setItems(ArrayList<?> items) {
            this.items = items;
        }
    }

    private static class FightInputItems {
        private ArrayList<FightInput> items;

        ArrayList<FightInput> getItems() {
            return items;
        }
        void setItems(ArrayList<FightInput> items) {
            this.items = items;
        }
    }

    private static class DialogsItems {
        private ArrayList<DialogOutput> items;

        ArrayList<DialogOutput> getItems() {
            return items;
        }
        void setItems(ArrayList<DialogOutput> items) {
            this.items = items;
        }
    }

    private static class GroupResultItems {
        private ArrayList<GroupResult> items;

        ArrayList<GroupResult> getItems() {
            return items;
        }
        void setItems(ArrayList<GroupResult> items) {
            this.items = items;
        }
    }

    private static class FightOutputItems {
        private ArrayList<FightOutput> items;

        ArrayList<FightOutput> getItems() {
            return items;
        }
        void setItems(ArrayList<FightOutput> items) {
            this.items = items;
        }
    }

//    private static class TrainingItems {
//        private ArrayList<Training> items;
//
//        ArrayList<Training> getItems() {
//            return items;
//        }
//        void setItems(ArrayList<Training> items) {
//            this.items = items;
//        }
//    }

    private static class FighterStatItems {
        private ArrayList<FighterStat> items;

        ArrayList<FighterStat> getItems() {
            return items;
        }
        void setItems(ArrayList<FighterStat> items) {
            this.items = items;
        }
    }

    private static class MessagesItems {
        private ArrayList<Message> items;

        ArrayList<Message> getItems() {
            return items;
        }
        void setItems(ArrayList<Message> items) {
            this.items = items;
        }
    }

    public enum ItemClass {/*Training,*/ FightInput, FightOutput, FighterStat, GroupResult, Dialogs, Messages}
}