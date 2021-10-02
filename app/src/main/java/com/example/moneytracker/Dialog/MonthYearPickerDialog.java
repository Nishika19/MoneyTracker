package com.example.moneytracker.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.fragment.app.DialogFragment;

import com.example.moneytracker.R;

import java.util.Calendar;
import java.util.Objects;

public class MonthYearPickerDialog extends DialogFragment {

private static final int MAX_YEAR = 2099;
private DatePickerDialog.OnDateSetListener listener;

    public MonthYearPickerDialog(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public MonthYearPickerDialog(int contentLayoutId, DatePickerDialog.OnDateSetListener listener) {
        super(contentLayoutId);
        this.listener = listener;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
    this.listener = listener;
}

@SuppressLint("ResourceAsColor")
@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
    LayoutInflater inflater = requireActivity().getLayoutInflater();

    Calendar cal = Calendar.getInstance();

    View dialog = inflater.inflate(R.layout.month_year_picker_dialog, null);
    final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
    final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);

    monthPicker.setMinValue(1);
    monthPicker.setMaxValue(12);
    monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

    int year = cal.get(Calendar.YEAR);
    yearPicker.setTextSize(60);
    yearPicker.setMinValue(1900);
    yearPicker.setMaxValue(3500);
    yearPicker.setValue(year);

    builder.setView(dialog).setPositiveButton(Html.fromHtml("<font color='#FF0187861'>Ok</font>"), new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
        }
    }).setNegativeButton(Html.fromHtml("<font color='#FF018786'>Cancel</font>"), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            MonthYearPickerDialog.this.getDialog().cancel();
        }
    });
    return builder.create();
   }
 }