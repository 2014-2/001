//package com.sxlc.db;
//
//import android.content.ContentValues;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//public class Data {
//	MyData MydataBases;
//	SQLiteDatabase db;
//	public void add_data(Context ct,int version,String s,String time){//
//		ContentValues values= new ContentValues();		
//		values.put("id", "1");
//		values.put("name", s);
//		values.put("time", time);
//	//	values.put("age",42 );
//		MydataBases= new MyData(ct, "my_db",version);  
//		//获取数据库对象
//		db= MydataBases.getReadableDatabase();
//		db.insert("user", null, values);
//		db.close();
//	}
//	public String  search(Context ct,int version ,String[] columns,int id){
//		String name="没有查找到该数据";
//		MydataBases= new MyData(ct, "my_db",version);  
//		db= MydataBases.getReadableDatabase();
//		Cursor cursor= db.query("user", columns, "id=?", new String[]{String.valueOf(id)}, null, null, null);
//		while (cursor.moveToNext()) { //逐条读取获取的每条name信息
//			name= cursor.getString(cursor.getColumnIndex("name"));
//		}
//		db.close();
//		return name;
//	}
//	
//	public String[] display(Context ct,int version,String[] columns,String leixing){
//			 MydataBases= new MyData(ct, "my_db",version);  
//			 db= MydataBases.getReadableDatabase();
//			 Cursor cur=db.query("user",columns, "name=?", new String[]{leixing}, null,null,null);
//			 int count=cur.getCount();
//			 String[]datastring=new String[count];
//			 if(count!=0){
//			 count=0;
//			 while(cur.moveToNext())
//			 {
//				datastring[count++]=cur.getString(cur.getColumnIndex("time"));
//			 }
//			 }
//			 cur.close();
//			 db.close();
//			 return datastring;
//		 }
//	public void Detele_data(Context ct,int version,int id ){
//		MydataBases= new MyData(ct, "my_db",version);  
//		db= MydataBases.getReadableDatabase();
//		db.delete("user", "id=?",new String[]{String.valueOf(id)});
//		db.close();
//	}
//	
//	public void revamped_data(Context ct,int version,int id,String data){//修改数据    如果该数据不存在也不会把该条数据添加进去
//		ContentValues values= new ContentValues();
//		values.put("name",data);
//		MydataBases= new MyData(ct, "my_db",version);  
//		db= MydataBases.getReadableDatabase();
//		db.update("user",values, "id=?", new String[]{String.valueOf(id)});
//		db.close();
//	}
//	
//	public void update_SQL(Context ct,int version){//更新数据库
//		MydataBases= new MyData(ct, "my_db",version);  
//		db= MydataBases.getReadableDatabase();
//		System.out.println("更新到版本"+db.getVersion());
//		db.close();	
//	}
//}
