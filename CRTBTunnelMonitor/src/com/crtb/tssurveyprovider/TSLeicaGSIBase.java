package com.crtb.tssurveyprovider;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class TSLeicaGSIBase {
	
	//earch word has 24 characters ,GSI-16 data format.
	//		 GSI-16 data word structure:
	//Pos. 1-2: Word Index (WI) e.g. “11”; WI code
	//Pos. 3-6: Information related to data e.g. “002”
	//Pos. 7: Sign e.g. "+" or "-"
	//Pos. 8-23: GSI-16 data (16 digits) e.g. “000000000PNC0058”; PtID
	//Pos. 16/24: Blank (= separating character)

	protected BigDecimal mUnit = new BigDecimal("1");
	protected int mSign = 1;
	protected String mPat = "";

  protected boolean gsiLine ( String sLine, final Coordinate3D testObject ) {
  	boolean bRet = false;
  	mUnit = new BigDecimal(1);

    Pattern p = Pattern.compile(mPat);
		Matcher m = p.matcher(sLine);
    while ( m.find() ) {
    	// 找到一块后
    	if (m.groupCount() == 3)
    	{
    		String wi = gsiwiblock (m.group(1)); // 从1开始,0是原串
    		gsidataSign ( m.group(2) );
    		String data = gsidatablock ( m.group(3) );
    		
    		if (wi.equalsIgnoreCase("81")) {
    			testObject.E = mSign * Double.parseDouble(data) * 0.001 * mUnit.doubleValue();
    		}
    		else if (wi.equalsIgnoreCase("82")) {
    			testObject.N = mSign * Double.parseDouble(data) * 0.001 * mUnit.doubleValue();
    		}
    		else if (wi.equalsIgnoreCase("83")) {
    			testObject.H = mSign * Double.parseDouble(data) * 0.001 * mUnit.doubleValue();
    		}
    	}
      bRet = true;
    }
    return bRet;
  }
 
	protected String gsidatablock ( String text ) {
		String pat = "(?!0).+";
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(text);

    if (m.find()) {
    		return m.group(0);
    }
    return null;
  }
  
	protected String gsiwiblock ( String text ) {
		String wi = "";
		String pat = "(\\d{2})...(\\S{1})";
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(text);
   
    if ( m.find() ) {
    	if (m.groupCount() == 2)
    	{
    		wi = m.group(1);
    		
    		String tmp = m.group(2);
    		if (tmp.equalsIgnoreCase("6")) mUnit = new BigDecimal(0.1); // 0.1mm
    		else if (tmp.equalsIgnoreCase("8")) mUnit = new BigDecimal(0.01); // 0.01mm
    		else mUnit = new BigDecimal(1); // 1mm
    	}
    }
    return wi;
  }
	
  protected void gsidataSign ( String text ) {
    if (text.equalsIgnoreCase("+")) {
    	mSign = 1;
    }
    else if (text.equalsIgnoreCase("-") ) {
    	mSign = -1;
    }
  }
	
}
