package com.example.ceamaya.workoutapp.routineActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ceamaya.workoutapp.Exercise;
import com.example.ceamaya.workoutapp.R;
import com.example.ceamaya.workoutapp.exerciseActivity.ExerciseActivity;
import com.example.ceamaya.workoutapp.labs.RoutineExerciseLab;

import java.util.ArrayList;
import java.util.List;

public class RoutineFragment extends Fragment {
    private static final String ARG_ROUTINE_ID = "ARG_ROUTINE_ID";
    private static final String TAG = "RoutineFragment";

    private int routineId;
    private Activity activity;
    private RoutineExerciseLab routineExerciseLab;
    private ArrayList<Exercise> exercises;

    public RoutineFragment() {
        // Required empty public constructor
    }

    public static RoutineFragment newInstance(int routineId) {
        Log.d(TAG, "newInstance: routineId" + routineId);
        RoutineFragment fragment = new RoutineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROUTINE_ID, routineId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            routineId = getArguments().getInt(ARG_ROUTINE_ID);
        }
        activity = getActivity();
        routineExerciseLab = RoutineExerciseLab.get(activity);
        exercises = routineExerciseLab.getExercises(routineId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_select, container, false);


        RecyclerView exerciseRecyclerView = fragmentView.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        exerciseRecyclerView.setLayoutManager(linearLayoutManager);
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(exercises);
        exerciseRecyclerView.setAdapter(exerciseAdapter);

        FloatingActionButton addExerciseFab = fragmentView.findViewById(R.id.new_fab);
        addExerciseFab.setOnClickListener(addExerciseFabClickListener());
        return fragmentView;
    }

    private View.OnClickListener addExerciseFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                routineExerciseLab.insertRoutineExercise(routineId, 1);
            }
        };
    }

    private class ExerciseHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private Exercise exercise;

        ExerciseHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.simple_list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(Exercise exercise) {
            this.exercise = exercise;
            ((TextView) itemView).setText(exercise.getExerciseName());
        }

        @Override
        public void onClick(View view) {
            Intent intent = ExerciseActivity.newIntent(activity, exercise.getExerciseName(),
                    exercise.getExerciseId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            //
            return true;
        }

    }

    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseHolder> {

        private final List<Exercise> exercises;

        ExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @NonNull
        @Override
        public ExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ExerciseHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.bind(exercise);
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }
    }

}