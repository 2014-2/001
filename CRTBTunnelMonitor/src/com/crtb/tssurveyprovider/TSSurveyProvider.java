package com.crtb.tssurveyprovider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.crtb.log.CRTBLog;

/**
 * total station survey manager, include bluetooth.
 */
public final class TSSurveyProvider implements ISurveyProvider {

	private static ISurveyProvider gTSProvider;

	public static synchronized ISurveyProvider getDefaultAdapter() {
		if (gTSProvider == null) gTSProvider = new TSSurveyProvider();
		return gTSProvider;
	}

	private BluetoothAdapter mBTLocalAdapt;
	private static BluetoothSocket mBTToServerSocket;
	private ITSCoordinate mTSCoordinate;

	private final String TAG = "TSSur";
	private final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	UUID uuid_spp;
	public static boolean mTestTS = false; // TS测试用
	private TSCommandType mTSCommandType = TSCommandType.NoneTS; // 指令类型
	private boolean mTSRunning = false;

	public TSSurveyProvider() {
		uuid_spp = UUID.fromString(SPP_UUID);
		mBTLocalAdapt = BluetoothAdapter.getDefaultAdapter();
	}

	public boolean ensureConnect() {
		if (mBTToServerSocket == null) {
			if (mTestTS) return true;
			return false;
		}

		return true;
	}

	private int measure(final Coordinate3D testObject) {
		if (mTSCoordinate == null) return -2;

		if (ensureConnect()) {
			if (mTestTS) {
				measure_test(testObject);
				return 1;
			}

			// new Thread(new Runnable() {
			// @Override
			// public void run() {
			int nret = -1;
			Log.d(TAG, "BT measure thread to begin.");
			mTSRunning = true;
			try {
				nret = mTSCoordinate.measure(testObject);
			}
			catch (IOException e) {
				CRTBLog.d(TAG, e);
			}
			catch (InterruptedException e) {
				// TODO: 线程sleep
				CRTBLog.d(TAG, e);
			}
			finally {
				Log.d(TAG, "ok");
				mTSRunning = false;
				// synchronized (buffer) {
				// buffer.notify();
				// }
			}
			// }
			// }).start();
			// synchronized (buffer) {
			// try {
			// buffer.wait(mWaitTime * 2);
			// }
			// catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			return nret;
		}
		return -1;
	}

	private void measure_test(final Coordinate3D testObject) {
		String testCoordRet = new String(mTSCoordinate.GetCoordRETString());
		String coordRet = mTSCoordinate.ParseRETString(testCoordRet);
		testObject.setCoordinate(coordRet, true);
	}

	private synchronized int test_ts_connect() {
		if (mTSCoordinate == null) return -1;

		int nret = -1;
		if (ensureConnect()) {
			if (mTestTS) return 1;

			Log.d(TAG, "BT test total station thread to begin.");
			try {
				nret = mTSCoordinate.TestTSConnect();
				if (nret <= 0) return nret;
			}
			catch (IOException e) {
				CRTBLog.d(TAG, e);
			}
			catch (InterruptedException e) {
				CRTBLog.d(TAG, e);
			}
			finally {
				Log.d(TAG, "ok");
			}
		}
		return nret;
	}

	private final static String TAGCOM = "TSCOM";

	/**
	 * echo measure command via bluetooth.
	 * 
	 * @param mBTToServerSocket [in], bluetooth socket object.
	 * @param buffer [out], measure result.
	 * @param command [in], command.
	 * @return status code, -1: failed, 1: success.
	 */
	public static int DoMeasure(final char[] buffer, final byte[] command) throws IOException, InterruptedException {
		int nret = -1;
		final int TS_TIMEOUT = 10000; // 超时10s
		final int[] nrets = new int[1];

		try {
			// 书写指令至全站仪
			final OutputStream out = mBTToServerSocket.getOutputStream();
			Thread writeThread = new Thread(new Runnable() {
				public void run() {
					try {
						out.write(command);
						out.flush();
						CRTBLog.d(TAGCOM, "=>" + new String(command).replace("\r", "").replace("\n", ""));
						nrets[0] = 1;
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					finally {
						synchronized (buffer) {
							buffer.notify();
						}
					}
				}
			});

			writeThread.start();
			synchronized (buffer) {
				try {
					buffer.wait(TS_TIMEOUT);
					nret = nrets[0];
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// Thread.sleep(30); // 0.03s
			// 接收仪器返回的数据
			// if (nret == 1) nret = ReadM(buffer);
		}
		catch (Exception e) {
			CRTBLog.d(TAGCOM, e);
			nret = -1;
		}
		return nret;
	}

	/**
	 * only read inputstream.
	 * 
	 * @param mBTToServerSocket [in], bluetooth socket object.
	 * @param buffer [out], measure result.
	 * @return status code, -1: failed, 1: success.
	 */
	public static int ReadMeasureString(final char[] buffer) throws IOException, InterruptedException {
		return ReadM(buffer);
	}

	public static int ReadMeasureString2(final char[] buffer) throws IOException, InterruptedException {
		Thread.sleep(100);
		return ReadM(buffer);
	}

	// 读取字符串
	private static int ReadM(final char[] buffer) throws IOException {
		int nret = -1;
		final int TS_TIMEOUT = 10000;
		final int[] nrets = new int[2]; // [0]状态，[1]接收到字符数
		nrets[1] = 1;

		for (int j = 0; j < buffer.length; j++)
			buffer[j] = '\0';

		final InputStream input = mBTToServerSocket.getInputStream();

		// 接收仪器返回的数据
		Thread readThread = new Thread(new Runnable() {
			public void run() {
				try {
					int buffer_index = -1;
					char ch;
					while (true) {
						ch = (char) input.read();
						if (ch == '\r') {
							nrets[0] = 1;
							nrets[1] = ++buffer_index;
							break;
						}
						else if (ch == '\n')
						;
						else buffer[++buffer_index] = ch;
					}
				}
				catch (IOException e) {
					e.printStackTrace();
					nrets[0] = -1;
				}
				finally {
					synchronized (buffer) {
						buffer.notify();
					}
				}
			}
		});

		readThread.start();
		synchronized (buffer) {
			try {
				buffer.wait(TS_TIMEOUT);
				nret = nrets[0];
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		CRTBLog.d(TAGCOM, "<-" + String.valueOf(buffer, 0, nrets[1]).replace("\r", "").replace("\n", ""));
		if (nrets[1] == 1 && buffer[0] == '\0') nret = -1;
		return nret;
	}

	// 清空缓冲区
	public static void ClearMeasBuffer() throws IOException {
		// final int TS_TIMEOUT = 5000;
		// final Object obj = new Object();
		// final InputStream input = mBTToServerSocket.getInputStream();
		//
		// // 接收仪器返回的数据
		// Thread readThread = new Thread(new Runnable() {
		// public void run() {
		// try {
		// char[] temp = new char[1024];
		// int buffer_index = -1;
		// char ch;
		// while (true) {
		// ch = (char) input.read();
		//
		// if (buffer_index >= 1024) {
		// CRTBLog.d(TAGCOM, "clear<-" + String.valueOf(temp).replace("\r",
		// "").replace("\n", ""));
		// buffer_index = -1;
		// temp = new char[1024];
		// }
		//
		// if (ch == '\r') {
		// CRTBLog.d(TAGCOM, "clear<-" + String.valueOf(temp).replace("\r",
		// "").replace("\n", ""));
		// break;
		// }
		// else if (ch == '\n')
		// ;
		// else temp[++buffer_index] = ch;
		// }
		// }
		// catch (IOException e) {
		// e.printStackTrace();
		// }
		// finally {
		// synchronized (obj) {
		// obj.notify();
		// }
		// }
		// }
		// });
		//
		// readThread.start();
		// synchronized (obj) {
		// try {
		// obj.wait(TS_TIMEOUT);
		// }
		// catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}

	public synchronized int BeginConnection(TSConnectType tsType, TSCommandType tsCmdType, String[] params) {
		Log.d(TAG, "-> (1)begin connect to ts." + tsType.toString() + "," +tsCmdType.toString() + "," + params.length);
		if (params.length == 0) return 0;
		if (mBTToServerSocket != null) return 111111;
		int nret = -1;

		mTSCommandType = tsCmdType;
		switch (mTSCommandType) {
		case LeicaGEOCOM:
			mTSCoordinate = new TSLeicaGeoCOM();
			break;
		case LeicaGSI16:
			mTSCoordinate = new TSLeicaGSI16();
			break;
		case LeicaGSI8:
			mTSCoordinate = new TSLeicaGSI8();
			break;
		case SouthNTS01:
			mTSCoordinate = new TSSouth1();
			break;
		case SokkiaTS01:
			mTSCoordinate = new TSSokkia1();
			break;
		case TopconTS01:
			mTSCoordinate = new TSTopcon102N();
			break;
		default:
			mTSCoordinate = null;
			break;
		}

		if (mTestTS) return 1;

		switch (tsType) {
		case Bluetooth: {
			if (params.length <= 1) return 0;

			// [0]name, [1]address
			String address = params[1];
			BluetoothDevice btDev = mBTLocalAdapt.getRemoteDevice(address);
			try {
				if (mBTToServerSocket != null) {
					mBTToServerSocket.close();
				}
				// mBTToServerSocket =
				// btDev.createRfcommSocketToServiceRecord(uuid_spp);
				Method m = btDev.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
				mBTToServerSocket = (BluetoothSocket) m.invoke(btDev, 1);
			}
			catch (IOException e) {
				Log.d(TAG, e.getMessage());
				e.printStackTrace();
				mBTToServerSocket = null;
				nret = -1;
			}
			catch (SecurityException e) {
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
			try
			{
				Log.d(TAG, "begin to connect via bluetooth...");
				mBTLocalAdapt.cancelDiscovery();
				mBTToServerSocket.connect();
				Log.v(TAG, "client get accepted");
				nret = 1;
			}
			catch (IOException connectException){
				Log.d(TAG, "connectException " + connectException.getMessage());
				try
				{
					mBTToServerSocket.close();
					mBTToServerSocket = null;
				}
				catch (IOException cloaeException){
					Log.d(TAG, "cloaeException " + cloaeException.getMessage());
					return -1;
				}
			}
		}
			break;
		default:
			break;
		}

		if (nret != -1) nret = TestConnection();
		return nret;
	}

	public synchronized int GetCoord(double prismAddConst, double prismHeight, Coordinate3D xyh) {
		if (mTSRunning) return 2;

		return measure(xyh);
	}

	public synchronized int EndConnection() {
		if (mBTToServerSocket == null) return 1;

		try {
			mBTToServerSocket.close();
			mBTToServerSocket = null;
		}
		catch (IOException e) {
			CRTBLog.d(TAG, e);
			e.printStackTrace();
		}

		return 1;
	}

	public synchronized int TestConnection() {
		if (mTSRunning) return 2;

		return test_ts_connect();
	}
}
