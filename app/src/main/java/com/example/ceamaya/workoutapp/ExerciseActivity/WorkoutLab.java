package com.example.ceamaya.workoutapp.ExerciseActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ceamaya.workoutapp.Database.ExerciseBaseHelper;
import com.example.ceamaya.workoutapp.Database.ExerciseDbSchema.ExerciseSetTable;
import com.example.ceamaya.workoutapp.Database.ExerciseSetCursorWrapper;
import com.example.ceamaya.workoutapp.ExerciseSet;
import com.example.ceamaya.workoutapp.Workout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

class WorkoutLab {

    private static WorkoutLab workoutLab;
    private SQLiteDatabase database;

    private WorkoutLab(Context context) {
        database = new ExerciseBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    static WorkoutLab get(Context context) {
        if (workoutLab == null) {
            workoutLab = new WorkoutLab(context);
        }
        return workoutLab;
    }

    ArrayList<Workout> getWorkouts(int exerciseId) {
        String whereClause = ExerciseSetTable.Cols.EXERCISE_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(exerciseId)};
        ExerciseSetCursorWrapper cursor = queryExerciseSet(whereClause, whereArgs);

        HashMap<Long, ArrayList<ExerciseSet>> exerciseSetsMap = new HashMap<>();
        while (cursor.moveToNext()) {
            ExerciseSet exerciseSet = cursor.getExerciseSet();
            long timestamp = exerciseSet.getTimeStamp();

            ArrayList<ExerciseSet> exerciseSets;
            if (exerciseSetsMap.containsKey(timestamp)) {
                exerciseSets = exerciseSetsMap.get(timestamp);
            } else {
                exerciseSets = new ArrayList<>();
            }
            exerciseSets.add(exerciseSet);
            exerciseSetsMap.put(timestamp, exerciseSets);
        }
        cursor.close();

        ArrayList<Workout> workouts = new ArrayList<>();

        for (long timestamp : exerciseSetsMap.keySet()) {
            workouts.add(new Workout(timestamp, exerciseSetsMap.get(timestamp)));
        }

        Collections.sort(workouts);

        return workouts;
    }

    private ExerciseSetCursorWrapper queryExerciseSet(String whereClause, String[] whereArgs) {
        Cursor cursor = database.query(
                ExerciseSetTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new ExerciseSetCursorWrapper(cursor);
    }

    Workout getWorkout(int exerciseId, long timeStamp) {
        String whereClause = ExerciseSetTable.Cols.EXERCISE_ID + "=? AND " +
                ExerciseSetTable.Cols.TIME_STAMP + "=?";
        String[] whereArgs = new String[]{String.valueOf(exerciseId), String.valueOf(timeStamp)};
        ExerciseSetCursorWrapper cursor = queryExerciseSet(whereClause, whereArgs);

        ArrayList<ExerciseSet> exerciseSets = new ArrayList<>();
        while (cursor.moveToNext()) {
            exerciseSets.add(cursor.getExerciseSet());
        }
        cursor.close();

        return new Workout(timeStamp, exerciseSets);
    }

    void deleteWorkout(long time) {
        String[] whereArgs = new String[]{String.valueOf(time)};
        database.delete(ExerciseSetTable.NAME, ExerciseSetTable.Cols.TIME_STAMP + "=?",
                whereArgs);
    }

    void insertWorkout(Workout workout) {
        for (ExerciseSet exerciseSet : workout.getExerciseSets()) {
            workoutLab.insertExerciseSet(exerciseSet, workout.getTimeStamp());
        }
    }

    private void insertExerciseSet(ExerciseSet exerciseSet, long timeStamp) {
        ContentValues values = getContentValues(exerciseSet, timeStamp);
        database.insert(ExerciseSetTable.NAME, null, values);
    }

    private static ContentValues getContentValues(ExerciseSet exerciseSet, long timeStamp) {
        ContentValues values = new ContentValues();
        values.put(ExerciseSetTable.Cols.REPS, exerciseSet.getReps());
        values.put(ExerciseSetTable.Cols.WEIGHT, exerciseSet.getWeight());
        values.put(ExerciseSetTable.Cols.SET_NUMBER, exerciseSet.getSetNumber());
        values.put(ExerciseSetTable.Cols.EXERCISE_ID, exerciseSet.getExerciseId());
        values.put(ExerciseSetTable.Cols.TIME_STAMP, timeStamp);
        return values;
    }
}
