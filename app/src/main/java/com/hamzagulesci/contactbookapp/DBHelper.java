package com.hamzagulesci.contactbookapp;

import android.content.ContentValues;            // Used to add data to the database, Veritabanına veri eklemek için kullanılır
import android.content.Context;                  // Represents the application environment, Uygulama ortamını temsil eder
import android.database.Cursor;                  // Represents query results, Sorgu sonuçlarını temsil eder
import android.database.sqlite.SQLiteDatabase;   // Base class for database operations, Veritabanı işlemleri için temel sınıf
import android.database.sqlite.SQLiteOpenHelper; // Class for creating and managing databases, Veritabanı oluşturmak ve yönetmek için kullanılan sınıf

// DBHelper class inherits from SQLiteOpenHelper
// DBHelper sınıfı SQLiteOpenHelper'dan kalıtım alır
public class DBHelper extends SQLiteOpenHelper{

    // Database name and version are defined as constants
    // Veritabanı adı ve versiyonu sabit olarak tanımlanır
    public static final String DB_NAME = "Rehber.db";
    public static final int DB_VERSION = 1;

    // Constructor method: Called when creating the database object
    // Yapıcı metod: Veritabanı nesnesi oluşturulurken çağrılır
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // onCreate method runs when the database is created for the first time
    // onCreate metodu, veritabanı ilk kez oluşturulduğunda çalışır
    @Override
    public void onCreate(SQLiteDatabase db){
        // Creating a table named 'kisiler' with SQL command
        // SQL komutu ile 'kisiler' adında tablo oluşturuluyor
        String sql= "CREATE TABLE kisiler (id INTEGER PRIMARY KEY AUTOINCREMENT, ad TEXT, telefon TEXT)";

        // Executing Command
        // Komut Çalıştırılıyor
        db.execSQL(sql);
    }


    // onUpgrade method works when the database version changes (in case of an update)
    // onUpgrade metodu, veritabanı versiyonu değiştiğinde çalışır (güncelleme durumunda)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
        // If there is a table, delete and recreate it
        // Eğer tablo varsa sil ve yeniden oluştur
        db.execSQL("DROP TABLE İF EXİSTS kisiler");

        onCreate(db);
    }

    // // Method to add a new contact to the database
    // Veritabanına yeni kişi eklemek için metod
    public boolean kisiEkle(String ad, String telefon) {
        SQLiteDatabase db = this.getWritableDatabase(); // Get writable database object, Yazılabilir veritabanı nesnesi al
        ContentValues cv = new ContentValues(); // Class that stores data as key-values, Verileri key-value şeklinde saklayan sınıf

        cv.put("ad", ad); // put data in the 'name' column, 'ad' sütununa veri yerleştir
        cv.put("telefon", telefon); // put data in the 'phone' column, 'telefon' sütununa veri yerleştir

        long result = db.insert("kisiler", null, cv); // Insert data into table 'kisiler', Verileri 'kisiler' tablosuna ekle
        db.close(); // Close database connection, Veritabanı bağlantısını kapat

        // Returns true if the insertion succeeds, false if it fails
        // Ekleme başarılıysa true döner, başarısızsa false
        return result != -1;
    }

    // Method to list all contacts
    // Tüm kişileri listelemek için metod
    public Cursor tumKisileriGetir() {
        SQLiteDatabase db = this.getReadableDatabase(); // Get read-only database object, Sadece okunabilir veritabanı nesnesi al

        // fetch all data in the 'people' table
        // 'kisiler' tablosundaki tüm verileri getir
        return db.rawQuery("SELECT * FROM kisiler", null);
    }

    // Method that deletes the person with a specific ID
    // Belirli ID'ye sahip kişiyi silen metod
    public boolean kisiSil(int id) {
        SQLiteDatabase db = this.getWritableDatabase(); // Make the database writable, Veritabanını yazılabilir aç
        int result = db.delete("kisiler", "id=?", new String[]{String.valueOf(id)}); // Delete by ID, ID'ye göre sil
        db.close(); // Close database connection, Veritabanı bağlantısını kapat
        return result > 0; // Returns true if at least 1 record was deleted, En az 1 kayıt silindiyse true döner
    }

    // Method that updates the person with a specific ID
    // Belirli ID'ye sahip kişiyi güncelleyen metod
    public boolean kisiGuncelle(int id, String yeniAd, String yeniTelefon) {
        SQLiteDatabase db = this.getWritableDatabase(); // Open writable database, Yazılabilir veritabanı aç
        ContentValues cv = new ContentValues(); // Hold the values to update, Güncellenecek değerleri tut
        cv.put("ad", yeniAd); // Enter new name, Yeni ad gir
        cv.put("telefon", yeniTelefon); // Enter new phone, Yeni telefon gir
        int result = db.update("kisiler", cv, "id=?", new String[]{String.valueOf(id)}); // Update, Güncelle
        db.close();
        return result > 0; // Returns true if at least 1 record has been updated, En az 1 kayıt güncellendiyse true döner
    }

    // Checks if a specific phone number already exists
    // Belirli bir telefon numarası zaten var mı kontrol eder
    public boolean kisiVarMi(String telefon) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM kisiler WHERE telefon = ?", new String[]{telefon});
        boolean varMi = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return varMi;
    }
}
// github.com/hamzagulesci