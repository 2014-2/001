package com.crtb.tssurveyprovider;

/**
 * Total Station connection type.
 */
public enum TSConnectType {
  RS232(1),
  Bluetooth(2);

  private final int value;  
  public int getValue() {  
      return value;  
  }  
  
  // 构造器默认也只能是private, 从而保证构造函数只能在内部使用  
  TSConnectType(int value) {  
      this.value = value;  
  }  
}
