package com.crtb.tssurveyprovider;

public enum TSCommandType {
  LeicaGEOCOM(1),
  LeicaGSI8(2),
  LeicaGSI16(3),
  SouthNTS01(4),
  SokkiaTS01(5),
  TopconTS01(6),
  NoneTS(0);

  private final int value;  
  public int getValue() {  
      return value;  
  }  
  
  //构造器默认也只能是private, 从而保证构造函数只能在内部使用  
  TSCommandType(int value) {  
      this.value = value;  
  }  
}
