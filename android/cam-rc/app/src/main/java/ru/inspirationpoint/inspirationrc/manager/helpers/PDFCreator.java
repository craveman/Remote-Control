package ru.inspirationpoint.inspirationrc.manager.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.inspirationpoint.inspirationrc.InspirationDayApplication;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightActionData;
import ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightData;
import ru.inspirationpoint.inspirationrc.rc.ui.adapter.FightActionsAdapter;

import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.phrasesEN;
import static ru.inspirationpoint.inspirationrc.manager.constants.CommonConstants.phrasesRU;
import static ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightActionData.ActionType.SetScoreLeft;
import static ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightActionData.ActionType.SetScoreRight;
import static ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightActionData.ActionType.Start;
import static ru.inspirationpoint.inspirationrc.rc.manager.dataEntities.FightActionData.ActionType.Stop;

public class PDFCreator {

    private File outputFile;
    private Context mContext;
    private ArrayList<String> labels = new ArrayList<>();
    private Date mCurrentTime = new Date();
    private SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private ArrayList<FightActionData> actionsList = new ArrayList<>();
    private Boolean withPhrases = false;
    private int yOffset;

    public PDFCreator(String name, Context context, String phrases) {
        try {
            withPhrases = Boolean.parseBoolean(phrases);
        } catch (Exception e){
            Log.d("ERR", "PARSE");
        }
        outputFile = getDocumentsDir(name);
        mContext = context;
    }

    private File getDocumentsDir(String name) {

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, name +".pdf");
        try {
            path.mkdirs();

            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public String createClubsPDF() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595,
                842, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(13);

        StringBuilder sb = new StringBuilder();

//        if (content instanceof ArrayList ) {
//            if (((ArrayList) content).get(0) instanceof ClubOutput) {
//                int xOffset = 10;
//                int yOffset = 20;
//                for (ClubOutput output : (ArrayList<ClubOutput>) content) {
//                    canvas.drawText(output.name, xOffset, yOffset, paint);
//                    yOffset += 15;
//                    canvas.drawText(output.address, xOffset, yOffset, paint);
//                    yOffset += 25;
//                }
//            }
//        }

        int xOffset = 10;
        int yOffset = 20;
        canvas.drawText("erreuviraeuhpieairnvierianviuernv", xOffset, yOffset, paint);

        document.finishPage(page);

        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return outputFile.getAbsolutePath();
    }

    public Uri createLogPDF(FightData data) {
        PdfDocument document = new PdfDocument();

        long pureTime = data.getPureTime();

        FightActionsAdapter adapter = new FightActionsAdapter(InspirationDayApplication.getApplication().getApplicationContext(), true);
        adapter.setData(data.getActionsList());
        data.getActionsList().clear();
        data.getActionsList().addAll(adapter.getData());

        writeOnePage(document, data, pureTime);

        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return FileProvider.getUriForFile(
                mContext,
                mContext.getApplicationContext()
                        .getPackageName() + ".provider", outputFile);
    }

    private void writeOnePage(PdfDocument document, FightData data, long pureTime) {
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595,
                842, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);
        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        int xOffset = 30;
        yOffset = 40;
        Canvas canvas = page.getCanvas();
        canvas.drawText(mContext.getResources().getString(R.string.start_time) + " " + Helper.timeToString(data.getTime()), xOffset+30, yOffset, paint);
        yOffset+=30;
        Date fullTimeCount = new Date();
        fullTimeCount.setTime(data.getEndTime().getTime() - data.getTime().getTime());
        canvas.drawText(mContext.getResources().getString(R.string.full_time) + " " + (new SimpleDateFormat("mm:ss", Locale.getDefault()).format(fullTimeCount)), xOffset+30, yOffset, paint);
        yOffset+=30;
        canvas.drawText(mContext.getResources().getString(R.string.pure_time) + " " + Helper.timeToString(new Date(pureTime)), xOffset+30, yOffset, paint);
        yOffset+=30;
        String address = data.getPlace();
        if (TextUtils.isEmpty(address)) {
            address = InspirationDayApplication.getApplication().getApplicationContext().getResources().getString(R.string.not_detected);
        }
        String arrWords[] = address.split(" ");
        Log.d("ARRAY", arrWords.length + "");
        ArrayList<String> arrPhrases = new ArrayList<>();

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(mContext.getResources().getString(R.string.place)).append(" ");
        Log.d("BUFF START", stringBuffer.toString());
        int cnt = 0;
        int index = 0;
        int length = arrWords.length;

        while (index != length) {
            if (cnt + arrWords[index].length() <= 60) {
                cnt += arrWords[index].length() + 1;
                stringBuffer.append(arrWords[index]).append(" ");
                Log.d("BUFF IN YES", stringBuffer.toString());
                index++;
            } else {
                arrPhrases.add(stringBuffer.toString());
                stringBuffer = new StringBuilder();
                cnt = 0;
                Log.d("BUFF IN NO", stringBuffer.toString());
            }

        }

        Log.d("BUFF END", stringBuffer.toString());
        if (stringBuffer.length() > 0) {
            arrPhrases.add(stringBuffer.toString());
        }

        for (String s : arrPhrases) {
            canvas.drawText(s, xOffset + 30, yOffset, paint);
            yOffset += 20;
        }
        Paint dividerPaint = new Paint();
        dividerPaint.setColor(mContext.getResources().getColor(R.color.divider));
        canvas.drawLine(xOffset, yOffset, 595-xOffset, yOffset, dividerPaint);
        Drawable d = mContext.getResources().getDrawable(R.drawable.logo);
        d.setBounds(455, 0, 455+140, 140);
        d.draw(canvas);
        yOffset+=30;
        drawNamesAndSores(canvas, yOffset, data.getLeftFighter().getName(), data.getRightFighter().getName(),
                data.getLeftFighter().getScore(), data.getRightFighter().getScore());
        yOffset+=55;

        for (FightActionData data0 : data.getActionsList()) {
            if (data0.equals(data.getActionsList().get(data.getActionsList().size()-1)) ) {
                actionsList.add(data0);
            } else if (data0.getActionType() != Start &&
                    data0.getActionType() != Stop &&
                    data0.getActionType() != FightActionData.ActionType.RedCardLeft &&
                    data0.getActionType() != FightActionData.ActionType.RedCardRight) {
                actionsList.add(data0);
            }
        }

        if (actionsList.size() > 25) {
            if (actionsList.size() <= 33) {
                drawActions(actionsList, canvas, actionsList.size(), true, withPhrases);
                document.finishPage(page);
                PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(595,
                        842, 2).create();

                PdfDocument.Page page2 = document.startPage(pageInfo2);
                canvas = page2.getCanvas();
                drawGraph(canvas, data, 40);
                document.finishPage(page2);
            } else {
                drawActions(actionsList, canvas, 32, true, withPhrases);
                document.finishPage(page);
                PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(595,
                        842, 2).create();
                PdfDocument.Page page2 = document.startPage(pageInfo2);
                canvas = page2.getCanvas();
                yOffset = 40;
                drawActions(actionsList, canvas, actionsList.size() - 32, false, withPhrases);
                yOffset += 15;
                drawGraph(canvas, data, yOffset);
                document.finishPage(page2);
            }
        } else {
            drawActions(actionsList, canvas, actionsList.size(), true, withPhrases);
            drawGraph(canvas, data, 670);
            document.finishPage(page);
        }
    }

    private void drawNamesAndSores(Canvas canvas, int yOffset, String leftName, String rightName, int leftScore, int rightScore) {
        float center = 595/2;
        String[] leftNameStrings = leftName.split("\\s+");
        String[] rightNameStrings = rightName.split("\\s+");
        TextPaint namePaint = new TextPaint();
        namePaint.setColor(Color.BLACK);
        namePaint.setTextSize(20);
        namePaint.setTextAlign(Paint.Align.LEFT);
        Rect leftNameFirstBounds = new Rect();
        Rect rightNameFirstBounds = new Rect();
        Rect leftNameSecondBounds = new Rect();
        Rect rightNameSecondBounds = new Rect();
        Rect scoreBounds = new Rect();
        namePaint.getTextBounds(leftNameStrings[0], 0, leftNameStrings[0].length(), leftNameFirstBounds);
        namePaint.getTextBounds(rightNameStrings[0], 0, rightNameStrings[0].length(), rightNameFirstBounds);
        TextPaint scorePaint = new TextPaint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(28);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        String score = String.valueOf(leftScore) + " : " + String.valueOf(rightScore);
        scorePaint.getTextBounds(score, 0, score.length(), scoreBounds);
        float scoreOffset = center - scoreBounds.width()/2;
        float leftNameFirstOffset = scoreOffset/2 - leftNameFirstBounds.width()/2;
        float rightNameFirstOffset = center/2 + scoreBounds.width() + ((595 - center/2 + scoreBounds.width()/2)/2) - rightNameFirstBounds.width()/2;
        canvas.drawText(leftNameStrings[0], leftNameFirstOffset, yOffset, namePaint);
        canvas.drawText(rightNameStrings[0], rightNameFirstOffset, yOffset, namePaint);
        if (leftNameStrings.length > 1) {
            namePaint.getTextBounds(leftNameStrings[1], 0, leftNameStrings[1].length(), leftNameSecondBounds);
            float leftNameSecondOffset = scoreOffset/2 - leftNameSecondBounds.width()/2;
            canvas.drawText(leftNameStrings[1], leftNameSecondOffset, yOffset + 20, namePaint);
            canvas.drawText(score, scoreOffset, yOffset + 20, scorePaint);
        } else {
            canvas.drawText(score, scoreOffset, yOffset, scorePaint);
        }
        if (rightNameStrings.length > 1) {
            namePaint.getTextBounds(rightNameStrings[1], 0, rightNameStrings[1].length(), rightNameSecondBounds);
            float rightNameSecondOffset = center/2 + scoreBounds.width() + ((595 - center/2 + scoreBounds.width()/2)/2) - rightNameSecondBounds.width()/2;
            canvas.drawText(rightNameStrings[1], rightNameSecondOffset, yOffset + 20, namePaint);
        }
    }

    private void drawActions(ArrayList<FightActionData> actionsList, Canvas canvas, int amount, boolean fromStart, boolean withPhrases) {
        TextPaint paint = new TextPaint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(12);
        int left = 0;
        int right = 0;
        Drawable redCardIcon = mContext.getResources().getDrawable(R.drawable.red);
        Drawable yellowCardIcon = mContext.getResources().getDrawable(R.drawable.yellow);
        Drawable greenArrowIcon = mContext.getResources().getDrawable(R.drawable.green_arrow);
        Drawable redArrowIcon = mContext.getResources().getDrawable(R.drawable.red_arrow);
        for (int i = 0; i < amount; i++) {
            FightActionData actionData = actionsList.get(fromStart ? i : i + 32);
            for (int j = 0; j <= (fromStart ? i : i + 32); j++) {
                if (actionsList.get(j).getActionType() == SetScoreLeft) {
                    left = actionsList.get(j).getScore();
                } else if (actionsList.get(j).getActionType() == SetScoreRight) {
                    right = actionsList.get(j).getScore();
                }
            }
            canvas.drawText(Helper.timeToString(new Date(actionData.getTime())), 120, yOffset, paint);
            canvas.drawText(String.format("%s : %s", String.valueOf(left), String.valueOf(right)), 180, yOffset, paint);
                switch (actionData.getActionType()) {
                    case Start:
                        canvas.drawText(mContext.getResources().getString(R.string.end_fight), 300, yOffset, paint);
                        break;
                    case Stop:
                        canvas.drawText(mContext.getResources().getString(R.string.end_fight), 300, yOffset, paint);
                        break;
                    case Reset:
                        canvas.drawText(mContext.getResources().getString(R.string.reset), 300, yOffset, paint);
                        break;
                    case YellowCardRight:
                        canvas.drawText(mContext.getResources().getString(R.string.yc_right), 300, yOffset, paint);
                        yellowCardIcon.setBounds(250, yOffset-10, 259, yOffset+3);
                        yellowCardIcon.draw(canvas);
                        break;
                    case SetScoreRight:
                        if (actionData.getPhrase() == 10) {
                            canvas.drawText(mContext.getResources().getString(R.string.rc_left), 300, yOffset, paint);
                            redCardIcon.setBounds(250, yOffset-10, 259, yOffset+3);
                            redCardIcon.draw(canvas);
                        } else if(actionData.getPhrase() == 15) {
                            canvas.drawText(mContext.getResources().getString(R.string.changing_score), 300, yOffset, paint);
                        } else {
                            canvas.drawText(withPhrases ? (LocaleHelper.getLanguage(mContext).equals("ru") ?
                                    phrasesRU[actionData.getPhrase()] : phrasesEN[actionData.getPhrase()]) :
                                    mContext.getResources().getString(R.string.point_left), 300, yOffset, paint);
                            greenArrowIcon.setBounds(240, yOffset-9, 270, yOffset+4);
                            greenArrowIcon.draw(canvas);
                        }
                        break;
                    case SetTime:
                        canvas.drawText(mContext.getResources().getString(R.string.set_time,
                                Helper.timeToString(new Date(actionData.getEstablishedTime()))), 300, yOffset, paint);
//                        mDate.setText(Helper.timeToString(new Date(actionData.getEstablishedTime())));
                        break;
                    case SetPause:
                        canvas.drawText(mContext.getResources().getString(R.string.pause_time), 300, yOffset, paint);
                        break;
                    case SetPeriod:
                        canvas.drawText(mContext.getResources().getString(R.string.period, actionData.getFightPeriod()), 300, yOffset, paint);
                        break;
                    case SetScoreLeft:
                        if (actionData.getPhrase() == 10) {
                            canvas.drawText(mContext.getResources().getString(R.string.rc_right), 300, yOffset, paint);
                            redCardIcon.setBounds(250, yOffset-10, 259, yOffset+3);
                            redCardIcon.draw(canvas);
                        } else if(actionData.getPhrase() == 15) {
                            canvas.drawText(mContext.getResources().getString(R.string.changing_score), 300, yOffset, paint);
                        } else {
                            canvas.drawText(withPhrases ? (LocaleHelper.getLanguage(mContext).equals("ru") ?
                                    phrasesRU[actionData.getPhrase()] : phrasesEN[actionData.getPhrase()]) :
                                    mContext.getResources().getString(R.string.point_right), 300, yOffset, paint);
                            redArrowIcon.setBounds(240, yOffset-9, 270, yOffset+4);
                            redArrowIcon.draw(canvas);
                        }
                        break;
                    case SetPriorityLeft:
                        canvas.drawText(mContext.getResources().getString(R.string.priority_left), 300, yOffset, paint);
                        break;
                    case YellowCardLeft:
                        canvas.drawText(mContext.getResources().getString(R.string.yc_right), 300, yOffset, paint);
                        yellowCardIcon.setBounds(250, yOffset-10, 259, yOffset+3);
                        yellowCardIcon.draw(canvas);
                        break;
                    case SetPriorityRight:
                        canvas.drawText(mContext.getResources().getString(R.string.priority_right), 300, yOffset, paint);
                        break;
                }
            yOffset+=5;
            Paint dividerPaint = new Paint();
            dividerPaint.setColor(mContext.getResources().getColor(R.color.divider));
            canvas.drawLine(90, yOffset, 505, yOffset, dividerPaint);
            yOffset+=13;
        }

    }

    private void drawGraph(Canvas canvas, FightData data, float yOffset) {
        canvas.save();
        canvas.translate(10, yOffset);
        GraphView graphView = new GraphView(mContext);
        setupGraph(graphView, data);
        graphView.draw(canvas);
        canvas.restore();

        TextPaint paint = new TextPaint();
        paint.setTextSize(11);
        int dx = 80;
        for (int i = 0; i < labels.size() - 1; i++) {
            String s = labels.get(i);
            canvas.save();
            canvas.rotate(-45f, dx+30, yOffset + 158);
            canvas.drawText(s, dx+30, yOffset + 158, paint);
            canvas.restore();
            Paint gridPaint = new Paint();
            gridPaint.setColor(mContext.getResources().getColor(R.color.divider));
            canvas.drawLine(dx+45, yOffset+130, dx+45, yOffset+20, gridPaint);
            dx += 62;
        }

    }
    private void setupGraph(GraphView graphView, FightData data) {
        DataPoint[] pointArray = data.getPointArray(ContextCompat.getColor(InspirationDayApplication.getApplication().getApplicationContext(), R.color.colorPrimaryDark),
                ContextCompat.getColor(InspirationDayApplication.getApplication().getApplicationContext(), R.color.leftFighter),
                ContextCompat.getColor(InspirationDayApplication.getApplication().getApplicationContext(), R.color.rightFighter));

        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double minY = 0.0;
        for (DataPoint aPointArray : pointArray) {
            double x = aPointArray.getX();
            if (x > maxX) {
                maxX = x;
            }

            double y = aPointArray.getY();
            if (y > maxY) {
                maxY = y;
            }
            if (y < minY) {
                minY = y;
            }
        }
        for (int i = 1; i < 9; i++) {
            mCurrentTime.setTime((data.getmEndTime() - data.getmStartTime()) /8*i*11/10);
            labels.add(mTimerFormat.format(mCurrentTime));
        }
        maxX = maxX*1.1;
        maxY++;
        minY--;

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(pointArray);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(3f);
        series.setThickness(2);
        graphView.addSeries(series);

        Viewport viewport = graphView.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(maxX);

        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(minY);
        viewport.setMaxY(maxY);
        viewport.setDrawBorder(true);

        viewport.setScrollable(false);
        viewport.setScalable(false);
        graphView.getGridLabelRenderer().setTextSize(12f);
        graphView.getGridLabelRenderer().setNumVerticalLabels(maxY - minY > 8 ? (int) (maxY - minY + 2) / 2 : (int)((maxY - minY + 1)));
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graphView.getGridLabelRenderer().setLabelsSpace(20);

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {

            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    mCurrentTime.setTime((long) value);
                    return mTimerFormat.format(mCurrentTime);
                }
                return super.formatLabel(Math.abs(value), isValueX);
            }
        });

        graphView.layout(0, 0, 550, 180);
    }
}
