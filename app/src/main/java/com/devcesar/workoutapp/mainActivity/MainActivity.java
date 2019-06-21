package com.devcesar.workoutapp.mainActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import com.devcesar.workoutapp.R;
import com.devcesar.workoutapp.database.InitDatabase;
import com.devcesar.workoutapp.databinding.ActivityMainBinding;
import com.devcesar.workoutapp.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
  // todo
  // settings
  // Clear All Workouts
  // Delete All Exercises, Categories, Routines
  // Import Default Exercises, Categories, Routines
  // Start Timer After Adding Set - Switch

  private final Fragment routineFragment = SelectFragment.newInstance(Constants.TYPE_ROUTINE);
  private final Fragment exerciseFragment = SelectFragment.newInstance(Constants.TYPE_EXERCISE);
  private final Fragment categoryFragment = SelectFragment.newInstance(Constants.TYPE_CATEGORY);
  private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
      item -> {
        switch (item.getItemId()) {
          case R.id.nav_exercise:
            setTitle(R.string.exercise);
            setTabStateFragment(item.getItemId());
            return true;
          case R.id.nav_category:
            setTitle(R.string.category);
            setTabStateFragment(item.getItemId());
            return true;
          case R.id.nav_routine:
            setTitle(R.string.routine);
            setTabStateFragment(item.getItemId());
            return true;
        }
        return false;
      };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    String IS_FIRST_RUN = "IS_FIRST_RUN";
    boolean isFirstRun = prefs.getBoolean(IS_FIRST_RUN, true);
    if (isFirstRun) {
      InitDatabase.run(this);
      PreferenceManager.getDefaultSharedPreferences(this)
          .edit()
          .putBoolean(IS_FIRST_RUN, false)
          .apply();
      PreferenceManager.getDefaultSharedPreferences(this)
          .edit()
          .putInt(Constants.START_TIME, Constants.DEFAULT_START_TIME)
          .apply();
    }

    ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.bottomNavigation.setOnNavigationItemSelectedListener(navListener);

    setTitle(R.string.exercise);
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fragment_container, exerciseFragment)
        .add(R.id.fragment_container, routineFragment)
        .add(R.id.fragment_container, categoryFragment)
        .commit();
    setTabStateFragment(R.id.nav_exercise);
  }

  private void setTabStateFragment(int type) {
    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    switch (type) {
      case R.id.nav_category:
        transaction.show(categoryFragment);
        transaction.hide(exerciseFragment);
        transaction.hide(routineFragment);
        break;
      case R.id.nav_exercise:
        transaction.hide(categoryFragment);
        transaction.show(exerciseFragment);
        transaction.hide(routineFragment);
        break;
      case R.id.nav_routine:
        transaction.hide(categoryFragment);
        transaction.hide(exerciseFragment);
        transaction.show(routineFragment);
        break;
    }

    transaction.commit();
  }

}
