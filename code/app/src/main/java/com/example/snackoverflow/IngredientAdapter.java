package com.example.snackoverflow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.sql.Array;
import java.util.ArrayList;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {
    private ArrayList<Ingredient> ingredients;
    private Context context;

    public IngredientAdapter(Context context, ArrayList<Ingredient> ingredients){
        super(context, 0, ingredients);
        this.ingredients = ingredients;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.ingredient_content, parent,false);
        }
        Ingredient ingredient = ingredients.get(position);
        TextView ingredientDescription = view.findViewById(R.id.ingredient_description);
        TextView ingredientBestBefore = view.findViewById(R.id.ingredient_bestBefore);
        TextView ingredientUnit = view.findViewById(R.id.ingredient_unit);
        ingredientDescription.setText(ingredient.getDescription());
        ingredientBestBefore.setText(String.valueOf(ingredient.getBestBefore()));
        ingredientUnit.setText(String.valueOf(ingredient.getUnit()));
        return view;
    }
}