package com.example.wasteawayapplication.Admin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.wasteawayapplication.R;

import java.util.ArrayList;

public class AdminInventoryActivity extends AppCompatActivity {

    private ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayList<String> arrayList1 = new ArrayList<String>();
    private ArrayList<String> arrayList2 = new ArrayList<String>();
    private TableLayout tableLayout;

    private EditText inputName, inputCategory, inputQuantity;
    private Button addProduct;
    private ImageView backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inventory);

        inputName = (EditText) findViewById(R.id.et_inventory_name);
        inputCategory = (EditText) findViewById(R.id.et_inventory_category);
        inputQuantity = (EditText) findViewById(R.id.et_inventory_quantity);
        addProduct = (Button) findViewById(R.id.btn_inventory_add);
        backBtn = (ImageView) findViewById(R.id.iv_inventory_back);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

    }

    public void addProduct() {
        String productName = inputName.getText().toString();
        String productCategory = inputCategory.getText().toString();
        int productQuantity = Integer.parseInt(inputQuantity.getText().toString());

        arrayList.add(productName);
        arrayList1.add(productCategory);
        arrayList2.add(String.valueOf(productQuantity));

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tl_inventory);

        TableRow tableRow = new TableRow(this);
        TextView displayName = new TextView(this);
        TextView displayCategory = new TextView(this);
        TextView displayQuantity = new TextView(this);

        for (int i = 0; i< arrayList.size(); i++) {
            String pName = arrayList.get(i);
            String pCategory = arrayList1.get(i);
            String pQuantity = arrayList2.get(i);

            displayName.setText(pName);
            displayCategory.setText(pCategory);
            displayQuantity.setText(pQuantity);
        }

        tableRow.addView(displayName);
        tableRow.addView(displayCategory);
        tableRow.addView(displayQuantity);
        tableLayout.addView(tableRow);
    }

}