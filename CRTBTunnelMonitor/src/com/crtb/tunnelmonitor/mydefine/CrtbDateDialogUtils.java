package com.crtb.tunnelmonitor.mydefine;

import java.util.Date;

import org.zw.android.framework.util.DateUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public final class CrtbDateDialogUtils {

	static boolean mSet = false;

	public static String datePickerStringUtil(String date) {

		StringBuffer strBuf = new StringBuffer(date);

		if (date.length() == 1)
			strBuf.insert(0, '0');

		return strBuf.toString();
	}

	public static DatePickerDialog setAnyDateDialog(final Activity activity,
			final TextView text, Date date) {

		final int mYear = DateUtils.getDateTime(date)[0];
		final int mMonth = DateUtils.getDateTime(date)[1];
		final int mDay = DateUtils.getDateTime(date)[2];
		final int mHour = DateUtils.getDateTime(date)[3];
		final int mMinute = DateUtils.getDateTime(date)[4];

		DatePickerDialog dialog = new DatePickerDialog(activity,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						new TimePickerDialog(activity, new OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker arg0, int hour,
									int minute) {
								text.setText(mYear + "-"
										+ DateUtils.pad(mMonth + 1) + "-"
										+ DateUtils.pad(mDay) + " "
										+ DateUtils.pad(hour) + ":"
										+ DateUtils.pad(minute));
							}
						}, mHour, mMinute, true).show();

					}
				}, mYear, mMonth - 1, mDay);

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (CrtbDateDialogUtils.mSet == false) {
					text.setText("");
				} else {
					mSet = false;
				}
			}

		});

		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

		return dialog;
	}

	public static TimePickerDialog setTimeDialog(Context activity,
			final TextView text, Date date) {
		int mHour = DateUtils.getDateTime(date)[3];
		int mMinute = DateUtils.getDateTime(date)[4];
		TimePickerDialog dialog = new TimePickerDialog(activity,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker arg0, int hourOfDay,
							int minute) {
						text.setText(DateUtils.pad(hourOfDay) + ":"
								+ DateUtils.pad(minute));
					}

				}, mHour, mMinute, true);

		dialog.setButton3("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		dialog.setButton2("重置", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				text.setText("");
			}
		});
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

		return dialog;
	}

	public static void setDateTimeDialog(final Activity activity,
			final TextView text, Date date) {

		int mYear = DateUtils.getDateTime(date)[0];
		int mMonth = DateUtils.getDateTime(date)[1];
		int mDay = DateUtils.getDateTime(date)[2];
		final int mHour = DateUtils.getDateTime(date)[3];
		final int mMinute = DateUtils.getDateTime(date)[4];

		new DatePickerDialog(activity,
				new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, final int year,
							final int monthOfYear, final int dayOfMonth) {

						new TimePickerDialog(activity, new OnTimeSetListener() {

							@Override
							public void onTimeSet(TimePicker arg0, int hour,
									int minute) {
								text.setText(year + "-"
										+ DateUtils.pad(monthOfYear + 1) + "-"
										+ DateUtils.pad(dayOfMonth) + " "
										+ DateUtils.pad(hour) + ":"
										+ DateUtils.pad(minute));
							}
						}, mHour, mMinute, true).show();
					}
				}, mYear, mMonth - 1, mDay).show();

	}
}
