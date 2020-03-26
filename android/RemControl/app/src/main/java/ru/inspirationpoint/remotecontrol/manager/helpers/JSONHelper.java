package ru.inspirationpoint.remotecontrol.manager.helpers;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ru.inspirationpoint.remotecontrol.internalServer.schemas.requests.FightInput;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.FightOutput;
import ru.inspirationpoint.remotecontrol.internalServer.schemas.responses.FighterStat;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FightData;
import ru.inspirationpoint.remotecontrol.manager.dataEntities.FullFightInfo;

public class JSONHelper {

    private static final String FILE_NAME_FIGHTS = "fights_list.json";
    private static final String FILE_NAME_FIGHTERS = "fighters_list.json";
    private static final String FILE_NAME_CACHED_FIGHT = "cached_fight.json";
    private static final String FILE_NAME_LAST_FIGHT = "last_fight";

    public static void exportToJSON(Context context, ArrayList<?> dataList) {

        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        dataItems.setItems(dataList);
        String jsonString = gson.toJson(dataItems);

        FileOutputStream fileOutputStream = null;

        try {
            if (dataList.get(0) instanceof FightInput || dataList.get(0) instanceof FightOutput) {
                fileOutputStream = context.openFileOutput(FILE_NAME_FIGHTS, Context.MODE_PRIVATE);
            }else if (dataList.get(0) instanceof FighterStat) {
                fileOutputStream = context.openFileOutput(FILE_NAME_FIGHTERS, Context.MODE_PRIVATE);
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

    public static ArrayList<?> importFromJSON(Context context, ItemClass type) {

        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try{
            switch (type) {
                case FightInput:
                    fileInputStream = context.openFileInput(FILE_NAME_FIGHTS);
                    break;
                case FightOutput:
                    fileInputStream = context.openFileInput(FILE_NAME_FIGHTS);
                    break;
                case FighterStat:
                    fileInputStream = context.openFileInput(FILE_NAME_FIGHTERS);
                    break;
            }
            if (fileInputStream != null) {
                streamReader = new InputStreamReader(fileInputStream);
            }
            Gson gson = new Gson();
            if (streamReader != null) {
                switch (type) {
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

    public static void exportToJSON(Context context, FightData data) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(data);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME_CACHED_FIGHT, Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                fileOutputStream.write(jsonString.getBytes());
            }
        } catch (IOException e) {
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

    public static void exportToJSON(Context context, FullFightInfo data, String appendix) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(data);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME_LAST_FIGHT + "_" + appendix + ".json", Context.MODE_PRIVATE);
            if (fileOutputStream != null) {
                Log.wtf("FOS EXPORT", "+");
                fileOutputStream.write(jsonString.getBytes());
            }
        } catch (IOException e) {
            Log.wtf("EXPORT EXCEPTION", e.toString());
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

    public static FightData importFightFromJSON (Context context) {
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(FILE_NAME_CACHED_FIGHT);
            if (fileInputStream != null) {
                streamReader = new InputStreamReader(fileInputStream);
            }
            Gson gson = new Gson();
            if (streamReader != null) {
                FightData cached = gson.fromJson(streamReader, FightData.class);
                if (cached != null) {
                    return cached;
                }
            }
        }catch (IOException ex){
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

    public static void copyFightToStorage(String filename, Context context) {
        String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()) + "/";
        File dir = new File(newPath);
        if (!dir.exists())
            dir.mkdirs();
        File newFight = new File(newPath + filename + ".json");
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.openFileInput(FILE_NAME_CACHED_FIGHT);
            try {
                out = new FileOutputStream(newFight);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (IOException e) {
            Log.wtf("copy error", e.getLocalizedMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static FullFightInfo importLastFightFromJSON (Context context, String appendix) {
        Log.wtf("IMPORT CALLED", appendix);
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(FILE_NAME_LAST_FIGHT + "_" + appendix + ".json");
            if (fileInputStream != null) {
                Log.wtf("FIS != NULL", "++");
                streamReader = new InputStreamReader(fileInputStream);
            }
            Gson gson = new Gson();
            if (streamReader != null) {
                Log.wtf("STREAM READER != NULL", "++");
                FullFightInfo cached = gson.fromJson(streamReader, FullFightInfo.class);
                if (cached != null) {
                    Log.wtf("CACHED != NULL", "++");
                    return cached;
                }
            }
        }catch (IOException ex){
            Log.wtf("IMPORT EXCEPTION", ex.toString());
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

        void setItems(ArrayList<?> items) {
            this.items = items;
        }
    }

    private static class FightInputItems {
        private ArrayList<FightInput> items;

        ArrayList<FightInput> getItems() {
            return items;
        }
    }

    private static class FightOutputItems {
        private ArrayList<FightOutput> items;

        ArrayList<FightOutput> getItems() {
            return items;
        }
    }

    private static class FighterStatItems {
        private ArrayList<FighterStat> items;

        ArrayList<FighterStat> getItems() {
            return items;
        }
    }

    public enum ItemClass {FightInput, FightOutput, FighterStat}
}