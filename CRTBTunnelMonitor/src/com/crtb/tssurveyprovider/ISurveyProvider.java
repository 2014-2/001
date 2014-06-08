package com.crtb.tssurveyprovider;

public interface ISurveyProvider {

  /**
   * begin to connect total station via the parameters.
   *
   * @param tsType [in], total station connection method.
   * @param tsCmdType [in], vendor's total station command type.
   * @param params [in], [0]:device name 
   *                     [1]:bluetooth address
   *                     e.g. bluetooth {"TS09p_1363947", "00:13:43:08:D4:80"}
   * @return status code, 0: no params, -1: failed, 1: success.
   */
	int BeginConnection(TSConnectType tsType, TSCommandType tsCmdType, String[] params);
	
	int TestConnection();

	/**
	 * get coordinates from ts
	 * @param prismAddConst [in] default 0.
	 * @param prismHeight [in] default 0.
	 * @param xyh [out] coordinates after measure.
	 * @return status code, 1=success, 2=measuring, 0=failed.
	 * @throws InterruptedException
	 */
	int GetCoord(double prismAddConst, double prismHeight, Coordinate3D xyh);
	
	int EndConnection();
}
