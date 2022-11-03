package com.example.snackoverflow;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class MealPlannerAddMeal extends DialogFragment implements AdapterView.OnItemSelectedListener {
    // Check if we are editing data
    private boolean edit;
    // Data from other activities
    ArrayList<Recipe> recipeDataList;
    // Data storage
    private Spinner spinner;
    private TextView TextViewDate;
    private Mealday mealDay;
    private Recipe recipe;
    // date picker
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    // listener for Meal Planner
    public OnFragmentInteractionListener listener;

    // constructor
    public MealPlannerAddMeal() {
        edit = false;
    }
    public MealPlannerAddMeal(Mealday mealDay, Recipe recipe){
        this.edit = true;
        this.mealDay = mealDay;
        this.recipe = recipe;
    }

    public interface OnFragmentInteractionListener {
        void addMeal(Recipe recipe, LocalDate date);

        void deleteMealDay(Mealday mealDay);
    }

    @Override
    public void onAttach(Context context) {
        // from lab
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Inflate the layout
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.mealplanner_add_meal_fragment, null);
        spinner = view.findViewById(R.id.spinner);
        TextViewDate = view.findViewById(R.id.text_view_date);

        TextViewDate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                if (day <= 9){
                    String date_s = year + "-" + month + "-0" + day;
                    TextViewDate.setText(date_s);
                }
                else {
                    String date_s = year + "-" + month + "-" + day;
                    TextViewDate.setText(date_s);
                }
            }
        };

        // DUMMY DATA
        String []recipestitle = {"Curry","NOODLES","nidal"};
        int [] servings = {1,2};
        String []categories = {"Lunch","Dinner","nice"};
        recipeDataList = new ArrayList<Recipe>();

        for (int i =0;i<recipestitle.length;i++){
            recipeDataList.add(new Recipe(recipestitle[i], 120,2.0f,"Lunch","HAHA","boil" ));
        }
        //

        String[] recipeNames = new String[recipeDataList.size()+1];
        recipeNames[0] = "Recipe";
        for (int i =1;i<=recipeDataList.size();i++){
            recipeNames[i] = recipeDataList.get(i-1).getTitle();
        }
        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, recipeNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (edit == false) {
            return builder
                    .setView(view)
                    .setTitle("Add Meal")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(Objects.equals(spinner.getSelectedItemPosition(), 0)){
                                new ErrorFragment("Invalid Recipe Chosen").show(getParentFragmentManager(), "error");
                            }
                            else{
                                String date_text = TextViewDate.getText().toString();
                                if(Objects.equals(date_text,"Date")){
                                    new ErrorFragment("Invalid Date Chosen").show(getParentFragmentManager(), "error");
                                }
                                else {
                                    recipe = recipeDataList.get(spinner.getSelectedItemPosition() - 1);
                                    LocalDate date = null;
                                    try {
                                        date = stringToDate(date_text);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    listener.addMeal(recipe, date);
                                }
                                }
                            }
                    }).create();
        }
        else{
            spinner.setSelection(Arrays.asList(recipeNames).indexOf(recipe.getTitle()));
            TextViewDate.setText(mealDay.getDate().toString());
            return builder
                    .setView(view)
                    .setTitle("Edit Meal")
                    .setNeutralButton("Cancel", null)
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mealDay.getMeals().remove(recipe);
                            if (mealDay.getMeals().size() == 0){
                                listener.deleteMealDay(mealDay);
                            }
                        }
                    })
                    .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(Objects.equals(spinner.getSelectedItemPosition(), 0)){
                                new ErrorFragment("Invalid Recipe Chosen").show(getParentFragmentManager(), "error");
                            }
                            else {
                                mealDay.getMeals().remove(recipe);
                                if (mealDay.getMeals().size() == 0) {
                                    listener.deleteMealDay(mealDay);
                                }
                                String date_text = TextViewDate.getText().toString();
                                Recipe recipe = recipeDataList.get(spinner.getSelectedItemPosition() - 1);
                                try {
                                    LocalDate date = stringToDate(date_text);
                                    listener.addMeal(recipe, date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).create();
        }
    }
    private void isValidDate(EditText edt) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            String after = dateFormat.format(dateFormat.parse(edt.getText().toString()));
        } catch (ParseException e) {
            edt.setError("Format: yyyy-mm-dd");
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        spinner.setSelection(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private LocalDate stringToDate(String adate) throws ParseException {
        LocalDate Date = LocalDate.parse(adate);
        return Date;
    }

}