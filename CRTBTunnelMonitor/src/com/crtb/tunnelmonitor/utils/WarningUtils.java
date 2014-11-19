package com.crtb.tunnelmonitor.utils;

import com.crtb.tunnelmonitor.common.Constant;
import com.crtb.tunnelmonitor.infors.Exceeding;
import com.crtb.tunnelmonitor.infors.OffsetLevel;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

/**
 * 预警处理Util
 * @author xu
 *
 */
public class WarningUtils {
	
	/**
	 * 判断是否超过极限
	 * @param context 运行上下文
	 * @param caller 回调
	 */
	public static void judgeTransfinite(Context context, final UploadCallBack caller) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setCancelable(false);
		builder.setMessage("累计超限已经大于3000毫米不能上传至工管中心，是否保存");
		builder.setTitle("测量提示");
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				caller.done(true);
			}
		});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				caller.done(false);
			}
		});
		builder.create().show();
	}

	public interface UploadCallBack {
		public void done(boolean state);
	}

	  /**
     * 检查位移等级
     * @param value 测量值
     * @param rockGrade 围岩等级：I、II……
     * @param isLeiji 是否为累积
     * @return 位移等级
     */
    public static OffsetLevel[] checkOffsetLevel(Exceeding exceeding,String rockGrade){
		OffsetLevel[] offsetList = new OffsetLevel[2];
		OffsetLevel leiji = new OffsetLevel();
		OffsetLevel sulv = new OffsetLevel();
		StringBuilder sbLeiJi = new StringBuilder();
		StringBuilder sbSulv = new StringBuilder();
		double value;

		if (exceeding == null) {
			return null;
		}

		//累积
		
		leiji.Value = exceeding.leijiValue;
		leiji.PreType = exceeding.leijiType;
		value = Math.abs(exceeding.leijiValue);
		int levelBase = Constant.LEI_JI_OFFSET_LEVEL_BASE[CrtbUtils.getRockgrade(rockGrade)];
		if (value < levelBase) {
			leiji.IsTransfinite = false;
			leiji.TransfiniteLevel = 0;
		} else if (value <= 2 * levelBase) {
			leiji.IsTransfinite = true;
			leiji.TransfiniteLevel = 2;
		} else {
			leiji.IsTransfinite = true;
			leiji.TransfiniteLevel = 1;
		}

		if (value >= Constant.ALARM_MAX_VALUE) {
			leiji.IsLargerThanMaxValue = true;
		} else {
			leiji.IsLargerThanMaxValue = false;
		}

		if (leiji.PreType > -1) {
			leiji.TextColor = Constant.leijiOffsetLevelColor[leiji.TransfiniteLevel];
			if (leiji.IsTransfinite) {
				sbLeiJi.append(Constant.U_TYPE_MSGS[leiji.PreType]).append(" ").append(leiji.Value).append("毫米");
			} else {
				sbLeiJi.append(Constant.U_TYPE_MSGS_SAFE[leiji.PreType]).append(" ").append(leiji.Value).append("毫米");
			}
			leiji.Content = sbLeiJi.toString();
		}
		offsetList[0] = leiji;
		
		//速率
		sulv.Value = exceeding.sulvValue;
		sulv.PreType = exceeding.sulvType;
		if (Math.abs(exceeding.sulvValue) < Constant.SPEED_THRESHOLD) {
			sulv.IsTransfinite = false;
			sulv.TransfiniteLevel = 0;
		} else {
			sulv.IsTransfinite = true;
			sulv.TransfiniteLevel = 1;
		}
		if (sulv != null && sulv.PreType > 0) {
			sulv.TextColor = Constant.sulvOffsetLevelColor[sulv.TransfiniteLevel];
			if (sulv.IsTransfinite) {
				sbSulv.append(Constant.U_TYPE_MSGS[sulv.PreType]).append(" ").append(sulv.Value).append("毫米");
			} else {
				sbSulv.append(Constant.U_TYPE_MSGS_SAFE[sulv.PreType]).append(" ").append(sulv.Value).append("毫米");
			}
			sulv.Content = sbSulv.toString();
			//速率永不超限
			sulv.IsTransfinite = false;
		}
		offsetList[1] = sulv;
		
        return offsetList;
    }
}
